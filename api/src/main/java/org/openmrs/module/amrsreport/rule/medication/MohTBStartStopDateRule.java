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

package org.openmrs.module.amrsreport.rule.medication;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.amrsreport.cache.MohCacheUtils;
import org.openmrs.module.amrsreport.rule.MohEvaluableNameConstants;
import org.openmrs.module.amrsreport.rule.MohEvaluableRule;
import org.openmrs.module.amrsreport.rule.util.MohRuleUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Author ningosi
 */

/**
 * Parameters: <ul> <li>[Optional] encounterType: list of all applicable encounter types </ul>
 */
public class MohTBStartStopDateRule extends MohEvaluableRule {

	public static final String TOKEN = "MOH TB Start-Stop Date";

	private static class SortByDateComparator implements Comparator<Object> {

		@Override
		public int compare(Object a, Object b) {
			Obs ao = (Obs) a;
			Obs bo = (Obs) b;
			return ao.getValueDatetime().compareTo(bo.getValueDatetime());
		}
	}

	/**
	 * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	//@Override
	public Result evaluate(final LogicContext context, final Integer patientId, final Map<String, Object> parameters) throws LogicException {
		ObsService obsService = Context.getObsService();

		//get concepts to use here plan, start and stop
		Concept TBStartConcept = MohCacheUtils.getConcept(MohEvaluableNameConstants.TUBERCULOSIS_TREATMENT_STARTED);

		//find the patient
		Patient patient = Context.getPatientService().getPatient(patientId);

		//find obs based on the start-Stop dates 
		List<Obs> obsCol = obsService.getObservations(Arrays.<Person>asList(patient), null, getQuestionConcepts(),
				null, null, null, null, null, null, null, null, false);

		Collections.sort(obsCol, new SortByDateComparator());

		List<Obs> uniqueObs = popObs(obsCol);

		//loop through the obsCol list of obs
		String ret = "";
		boolean wasStart = true;
		for (Obs obs : uniqueObs) {

			String obsValueDatetime = MohRuleUtils.formatdates(obs.getValueDatetime());

			if (obs.getConcept().equals(TBStartConcept)) {
				if (wasStart) {
					if (ret.equals(""))
						ret += (obsValueDatetime + " - ");
					else
						ret += (";" + obsValueDatetime + " - ");
				} else {
					ret += (obsValueDatetime  + " - ");
				}
				wasStart = true;
			} else {
				if (ret.equals("")) {
					ret += (" - " + obsValueDatetime + ";");
				} else {
					if (wasStart) {
						ret += (obsValueDatetime + ";");
					} else {
						ret += (" - " + obsValueDatetime + ";");
					}
				}
				wasStart = false;
			}
		}

		return new Result(ret);
	}

	private List<Concept> getQuestionConcepts() {
		return Arrays.asList(
				new Concept[]{
						MohCacheUtils.getConcept(MohEvaluableNameConstants.TUBERCULOSIS_TREATMENT_STARTED),
						MohCacheUtils.getConcept(MohEvaluableNameConstants.TUBERCULOSIS_TREATMENT_COMPLETED_DATE)
				});
	}

	//pass to a function setObsPop(obsCol)
	//loop thru the obsCol and get unique obsValueDateTime > Then add to newSet
	//return newSet
	private List<Obs> popObs(List<Obs> listObs) {
		Set<Date> setObs = new HashSet<Date>();
		List<Obs> retObs = new ArrayList<Obs>();

		for (Obs obs2 : listObs) {
			if (!setObs.contains(obs2.getValueDatetime())) {
				setObs.add(obs2.getValueDatetime());
				retObs.add(obs2);
			}
		}

		return retObs;
	}


	//return the tokens
	protected String getEvaluableToken() {
		return TOKEN;
	}

	/**
	 * @see org.openmrs.logic.Rule#getDependencies()
	 */
	//@Override
	public String[] getDependencies() {
		return new String[]{};
	}

	@Override
	public Datatype getDefaultDatatype() {
		// TODO Auto-generated method stub
		return Datatype.TEXT;
	}

	public Set<RuleParameterInfo> getParameterList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getTTL() {
		// TODO Auto-generated method stub
		return 0;
	}

}
