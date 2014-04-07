/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.amrsreports.reporting.data.evaluator;

import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.model.RegimenChange;
import org.openmrs.module.amrsreports.reporting.common.ObsDatetimeComparator;
import org.openmrs.module.amrsreports.reporting.common.SortedSetMap;
import org.openmrs.module.amrsreports.reporting.data.RegimenHistoryDataDefinition;
import org.openmrs.module.amrsreports.util.MOHReportUtil;
import org.openmrs.module.drughistory.DrugSnapshot;
import org.openmrs.module.drughistory.Regimen;
import org.openmrs.module.drughistory.api.RegimenService;
import org.openmrs.module.reporting.common.Age;
import org.openmrs.module.reporting.common.Birthdate;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.BirthdateDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.evaluator.PersonDataEvaluator;
import org.openmrs.module.reporting.data.person.service.PersonDataService;
import org.openmrs.module.reporting.dataset.query.service.DataSetQueryService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.util.OpenmrsUtil;

import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Handler(supports = RegimenHistoryDataDefinition.class, order = 50)
public class RegimenHistoryDataEvaluator implements PersonDataEvaluator {

	private static final Integer AGE_AT_WHICH_ONE_BECOMES_AN_ADULT = 15;

	private Log log = LogFactory.getLog(getClass());
	private List<Regimen> allRegimens = null;

	/**
	 * Finds a history of regimens and reasons for change for each person in a cohort
	 *
	 * @param definition the RegimenHistoryDataDefinition to be evaluated
	 * @param context    the evaluation context for a given report
	 * @return an ordered list of RegimenChange objects for each person
	 * @throws EvaluationException
	 * @should find a regimen if it exists
	 * @should return nothing if DrugSnapshots do not indicate a Regimen
	 * @should only return a reason from the same encounter as the DrugSnapshot used to indicate a Regimen
	 */
	@Override
	public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {

		EvaluatedPersonData c = new EvaluatedPersonData(definition, context);

		if (context.getBaseCohort().isEmpty())
			return c;

		// get drug snapshots
		SortedSetMap<Integer, DrugSnapshot> snapshots = MOHReportUtil
				.getARVSnapshotsMapFromTables(context.getBaseCohort(), context.getEvaluationDate());

		// get relevant reason observations
		SortedSetMap<Integer, Obs> reasons = getReasons(context);

		// get birthdates
		EvaluatedPersonData birthDateData = Context.getService(PersonDataService.class).evaluate(new BirthdateDataDefinition(), context);
		Map<Integer, Object> birthDates = birthDateData.getData();

		// build the regimen history
		for (Integer pId : snapshots.keySet()) {
			if (snapshots.get(pId) != null) {

				// find the birthdate
				Birthdate birthDate = null;
				if (birthDates.containsKey(pId)) {
					birthDate = (Birthdate) birthDates.get(pId);
				}

				// get iterators for snapshots and reasons
				Iterator<DrugSnapshot> dsI = snapshots.get(pId).iterator();
				Iterator<Obs> oI = reasons.containsKey(pId) ? reasons.get(pId).iterator() : IteratorUtils.emptyIterator();

				Regimen lastRegimen = null;

				// crawl through snapshots
				List<RegimenChange> res = new ArrayList<RegimenChange>();
				while (dsI.hasNext()) {

					DrugSnapshot ds = dsI.next();
					if (ds != null) {

						// get the current ageRange for this snapshot
						String ageRange = getAgeRange(birthDate, ds.getDateTaken());

						// figure out what regimens belong in this snapshot
						List<Regimen> regimens = findPotentialRegimens(ds, ageRange);

						// use the first regimen in the list
						if (regimens != null && !regimens.isEmpty()) {
							if (regimens.size() > 1) {
								log.warn("Multiple regimens match " + ds + ", using " + regimens.get(0));
							}

							Regimen thisRegimen = regimens.get(0);

							// only add a RegimenChange if thisRegimen is different from the last one
							if (lastRegimen == null || !OpenmrsUtil.nullSafeEquals(thisRegimen.getName(), lastRegimen.getName())) {

								// update lastRegimen
								lastRegimen = thisRegimen;

								// fill out a change if this snapshot has regimens
								RegimenChange rc = new RegimenChange();
								rc.setRegimen(thisRegimen);
								rc.setDateOccurred(ds.getDateTaken());

								// find the reason
								boolean found = false;
								while (oI.hasNext() && !found) {
									Obs o = oI.next();

									// if the snapshot and reason come from the same encounter, we have a winner
									if (OpenmrsUtil.nullSafeEquals(o.getEncounter().getId(), ds.getEncounter().getId())) {
										rc.setReason(o.getValueCoded().getDisplayString());
										found = true;
									}
								}

								// whether the reason was found or not, add the change
								res.add(rc);
							}
						}
					}
				}

				c.addData(pId, res);
			}
		}

		return c;
	}

	private List<Regimen> findPotentialRegimens(DrugSnapshot ds, String ageRange) {
		List<Regimen> results = new ArrayList<Regimen>();
		for (Regimen r : getAllRegimens()) {
			if (OpenmrsUtil.nullSafeEquals(r.getAge(), ageRange)) {
				// if the drugs in the regimen are all contained in the incoming parameter ...
				Set<Concept> s = new HashSet<Concept>(r.getDrugs());
				if (!s.retainAll(ds.getConcepts())) {
					// we have a match!
					results.add(r);
				}
			}
		}
		return results;
	}

	private String getAgeRange(Birthdate birthDate, Date dateTaken) {
		if (birthDate == null) {
			return Regimen.AGE_ADULT;
		}

		Age age = new Age(birthDate.getBirthdate(), dateTaken);
		if (age.getFullYears() >= AGE_AT_WHICH_ONE_BECOMES_AN_ADULT) {
			return Regimen.AGE_ADULT;
		}

		return Regimen.AGE_PEDS;
	}

	private SortedSetMap<Integer, Obs> getReasons(EvaluationContext context) {

		SortedSetMap<Integer, Obs> res = new SortedSetMap<Integer, Obs>();
		DataSetQueryService qs = Context.getService(DataSetQueryService.class);

		String encounterHql = "select encounter.id" +
				"   from Obs" +
				"	where voided = false " +
				"		and personId in (:personIds) " +
				"		and concept.id = 1255" +
				"		and valueCoded.id = 1849" +
				"		and obsDatetime <= :reportDate";

		String hql = "from Obs" +
				"	where voided = false " +
				"		and personId in (:personIds)" +
				"       and encounter.id in (:encounterIds)" +
				"		and concept.id = 1252" +
				"		and obsDatetime <= :reportDate";

		Map<String, Object> m = new HashMap<String, Object>();
		m.put("personIds", context.getBaseCohort());
		m.put("reportDate", context.getEvaluationDate());

		// get the encounter ids based on 1255:1849 question
		List<Object> encounterIds = qs.executeHqlQuery(encounterHql, m);

		if (encounterIds == null || encounterIds.isEmpty()) {
			return res;
		}

		m.put("encounterIds", encounterIds);

		// find all 1252 questions for reasons
		List<Object> queryResult = qs.executeHqlQuery(hql, m);

		// build a result set
		res.setSetComparator(new ObsDatetimeComparator());

		for (Object o : queryResult) {
			Obs obs = (Obs) o;
			res.putInList(obs.getPersonId(), obs);
		}

		return res;
	}

	public List<Regimen> getAllRegimens() {
		if (allRegimens == null) {
			allRegimens = Context.getService(RegimenService.class).getAllRegimens(false);
		}
		return allRegimens;
	}
}
