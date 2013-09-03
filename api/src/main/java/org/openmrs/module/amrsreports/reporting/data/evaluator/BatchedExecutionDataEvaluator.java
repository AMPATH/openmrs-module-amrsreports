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

import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.AmrsReportsConstants;
import org.openmrs.module.amrsreports.reporting.common.ObsRepresentation;
import org.openmrs.module.amrsreports.reporting.common.ObsRepresentationDatetimeComparator;
import org.openmrs.module.amrsreports.reporting.common.SortedSetMap;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.evaluator.PersonDataEvaluator;
import org.openmrs.module.reporting.dataset.query.service.DataSetQueryService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

public abstract class BatchedExecutionDataEvaluator implements PersonDataEvaluator {

	private Log log = LogFactory.getLog(getClass());

	@Override
	public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {

		PersonDataDefinition def = setDefinition(definition);

		EvaluatedPersonData c = new EvaluatedPersonData(def, context);

		if (context.getBaseCohort() != null && context.getBaseCohort().isEmpty()) {
			return c;
		}

		// perform pre-actions, return the data if it failed
		if (!doBefore(context, c))
			return c;

		// create cohort partitions
		int partitionSize = AmrsReportsConstants.DEFAULT_BATCH_SIZE;
		List<Cohort> partitions = new LinkedList<Cohort>();
		List<Integer> ids = new ArrayList<Integer>();
		ids.addAll(context.getBaseCohort().getMemberIds());
		for (int i = 0; i < ids.size(); i += partitionSize) {
			partitions.add(new Cohort(ids.subList(i,
					i + Math.min(partitionSize, ids.size() - i))));
		}

		log.info("number of partitions: " + partitions.size());

		Map<String, Object> m = getSubstitutions();

		String hql = getHQL();

		DataSetQueryService qs = Context.getService(DataSetQueryService.class);

		// calculate for [partition] people at a time
		for (Cohort partition : partitions) {

			m.put("personIds", partition);

			StopWatch timer = new StopWatch();
			timer.start();

			List<Object> queryResult = qs.executeHqlQuery(hql, m);

			timer.stop();
			String timeGetting = timer.toString();
			timer.reset();
			timer.start();

			SortedSetMap<Integer, ObsRepresentation> obsForPatients = new SortedSetMap<Integer, ObsRepresentation>();
			obsForPatients.setSetComparator(new ObsRepresentationDatetimeComparator());

			for (Object o : queryResult) {
				Map<String, Object> obs = (Map<String, Object>) o;
				ObsRepresentation or = new ObsRepresentation(obs);
				obsForPatients.putInList(or.getPersonId(), or);
			}

			timer.stop();
			String timeLoading = timer.toString();
			timer.reset();
			timer.start();

			for (Integer pId : obsForPatients.keySet()) {
				c.addData(pId, doExecute(pId, obsForPatients.get(pId), context));
			}

			Context.flushSession();
			Context.clearSession();

			timer.stop();
			String timeConsuming = timer.toString();

			log.warn(String.format("Get: %s | Load: %s | Consume: %s | Patients: %d", timeGetting, timeLoading, timeConsuming, partition.size()));
		}

		// perform post-actions, return the data if it failed
		doAfter(context, c);

		return c;
	}

	protected abstract PersonDataDefinition setDefinition(PersonDataDefinition definition);

	protected abstract Object doExecute(Integer pId, SortedSet<ObsRepresentation> o, EvaluationContext context);

	protected abstract boolean doBefore(EvaluationContext context, EvaluatedPersonData c);

	protected abstract void doAfter(EvaluationContext context, EvaluatedPersonData c);

	protected abstract String getHQL();

	protected abstract Map<String, Object> getSubstitutions();

}
