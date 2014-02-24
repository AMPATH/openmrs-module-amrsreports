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

import com.sun.xml.internal.xsom.impl.scd.Iterators;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.evaluator.PersonDataEvaluator;
import org.openmrs.module.reporting.dataset.query.service.DataSetQueryService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.util.OpenmrsUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Handler(supports = RegimenHistoryDataDefinition.class, order = 50)
public class RegimenHistoryDataEvaluator implements PersonDataEvaluator {

	private Log log = LogFactory.getLog(getClass());

	@Override
	public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {

		EvaluatedPersonData c = new EvaluatedPersonData(definition, context);

		if (context.getBaseCohort().isEmpty())
			return c;

		// get drug snapshots
		SortedSetMap<Integer, DrugSnapshot> snapshots = MOHReportUtil.getARVSnapshotsMap(context.getBaseCohort());

		// get relevant reason observations
		SortedSetMap<Integer, Obs> reasons = getReasons(context);

		// build the regimen history
		for (Integer pId : snapshots.keySet()) {
			if (snapshots.get(pId) != null) {
				Iterator<DrugSnapshot> dsI = snapshots.get(pId).iterator();
				Iterator<Obs> oI = reasons.containsKey(pId) ? reasons.get(pId).iterator() : Iterators.<Obs>empty();

				List<RegimenChange> res = new ArrayList<RegimenChange>();
				while (dsI.hasNext()) {
					DrugSnapshot ds = dsI.next();
					if (ds != null) {
						// figure out what regimens belong in this snapshot
						List<Regimen> regimens = Context.getService(RegimenService.class).getRegimensFromSnapshot(ds);
						if (regimens != null && !regimens.isEmpty()) {
							RegimenChange rc = new RegimenChange();

							// use the first regimen in the list
							if (regimens.size() > 1) {
								log.warn("Found more than one regimen for snapshot " + ds + ", using " + regimens.get(0));
							}
							rc.setRegimen(regimens.get(0));

							// set the date
							rc.setDateOccurred(ds.getDateTaken());

							// find the reason
							boolean found = false;
							while (oI.hasNext() && !found) {
								Obs o = oI.next();

								// if the snapshot and reason come from the same encounter, we have a winner
								if (OpenmrsUtil.nullSafeEquals(o.getEncounter(), ds.getEncounter())) {
									rc.setReason(o.getValueCoded());
									found = true;
								}
							}

							// whether the reason was found or not, add the change
							res.add(rc);
						}
					}
				}
				c.addData(pId, res);
			}
		}

		return c;
	}

	private SortedSetMap<Integer, Obs> getReasons(EvaluationContext context) {

		DataSetQueryService qs = Context.getService(DataSetQueryService.class);

		String encounterHql = "select encounterId" +
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
		m.put("encounterIds", encounterIds);

		// find all 1252 questions for reasons
		List<Object> queryResult = qs.executeHqlQuery(hql, m);

		// build a result set
		SortedSetMap<Integer, Obs> res = new SortedSetMap<Integer, Obs>();
		res.setSetComparator(new ObsDatetimeComparator());

		for (Object o : queryResult) {
			Obs obs = (Obs) o;
			res.putInList(obs.getPersonId(), obs);
		}

		return res;
	}
}
