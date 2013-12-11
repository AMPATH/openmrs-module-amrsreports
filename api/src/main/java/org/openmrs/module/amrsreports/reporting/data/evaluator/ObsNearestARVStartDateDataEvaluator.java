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


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.reporting.common.ObsRepresentation;
import org.openmrs.module.amrsreports.reporting.common.ObsRepresentationOrderComparator;
import org.openmrs.module.amrsreports.reporting.data.DateARTStartedDataDefinition;
import org.openmrs.module.amrsreports.reporting.data.ObsNearestARVStartDateDataDefinition;
import org.openmrs.module.reporting.common.Age;
import org.openmrs.module.reporting.data.MappedData;
import org.openmrs.module.reporting.data.converter.DateConverter;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.AgeAtDateOfOtherDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.service.PersonDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

/**
 * Handler for last HIV encounter data
 */
@Handler(supports = ObsNearestARVStartDateDataDefinition.class, order = 50)
public class ObsNearestARVStartDateDataEvaluator extends BatchedExecutionDataEvaluator<ObsRepresentation> {

	private ObsNearestARVStartDateDataDefinition definition;

	private final Log log = LogFactory.getLog(getClass());

	@Override
	protected ObsRepresentation renderSingleResult(Map<String, Object> m) {
		return new ObsRepresentation(m);
	}

	@Override
	protected Comparator<ObsRepresentation> getResultsComparator() {
		return new ObsRepresentationOrderComparator();
	}

	@Override
	protected PersonDataDefinition setDefinition(PersonDataDefinition def) {
		definition = (ObsNearestARVStartDateDataDefinition) def;
		return definition;
	}

	@Override
	protected Object doExecute(Integer pId, SortedSet<ObsRepresentation> o, EvaluationContext context) {
		ObsRepresentation or = o.first();
		if (!or.getObsDatetime().after(context.getEvaluationDate()))
			return or;
		return null;
	}

	@Override
	protected boolean doBefore(EvaluationContext context, EvaluatedPersonData c, Cohort cohort) {

		// if the age limit is set, remove anyone whose age is above the limit from the cohort
		if (definition.getAgeLimit() != null) {

			// create a mapped Date ART Started definition
			DateARTStartedDataDefinition artStarted = new DateARTStartedDataDefinition();
			MappedData<DateARTStartedDataDefinition> mappedDef = new MappedData<DateARTStartedDataDefinition>();
			mappedDef.setParameterizable(artStarted);
			mappedDef.addConverter(new DateConverter());

			// create the Age definition
			AgeAtDateOfOtherDataDefinition startAge = new AgeAtDateOfOtherDataDefinition();
			startAge.setEffectiveDateDefinition(mappedDef);

			// evaluate it
			EvaluatedPersonData startAges = null;
			try {
				startAges = Context.getService(PersonDataService.class).evaluate(startAge, context);
			} catch (EvaluationException e) {
				log.warn("could not evaluate AgeAtDateOfOtherDataDefinition, ignoring age limit.", e);
			}

			if (startAges == null)
				return true;

			// get the evaluated data
			Map<Integer, Object> artStartAges = startAges.getData();

			// loop and remove from base cohort if over the age limit
			for (Integer personId : artStartAges.keySet()) {
				Age age = (Age) artStartAges.get(personId);
				if (age.getFullYears() > definition.getAgeLimit())
					cohort.removeMember(personId);
			}
		}

		return true;
	}

	@Override
	protected void doAfter(EvaluationContext context, EvaluatedPersonData c) {
		// pass
	}

	@Override
	protected String getHQL() {
		return "select new map(" +
				"		o.personId as personId, " +
				"		o.concept.id as conceptId," +
				"		o.valueCoded.id as valueCodedId," +
				"		o.valueNumeric as valueNumeric," +
				"		o.obsDatetime as obsDatetime," +
				"		ABS(DATEDIFF(o.obsDatetime, hce.firstARVDate)) as order)" +
				" FROM Obs AS o, HIVCareEnrollment AS hce" +
				" WHERE" +
				"	o.person.personId = hce.patient.personId" +
				"   AND o.voided = false" +
				"	AND o.personId in (:personIds) " +
				"	AND o.concept.conceptId in (:conceptIds)" +
				"	AND o.obsDatetime BETWEEN SUBDATE(hce.firstARVDate, 21) AND ADDDATE(hce.firstARVDate, 7)";
	}

	@Override
	protected Map<String, Object> getSubstitutions(EvaluationContext context) {
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("conceptIds", getListOfIds(definition.getQuestions()));
		return m;
	}

	private List<Integer> getListOfIds(List<Concept> questions) {
		List<Integer> l = new ArrayList<Integer>();
		for (Concept c : questions) {
			l.add(c.getId());
		}
		return l;
	}
}
