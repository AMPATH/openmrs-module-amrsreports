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

public class MohFluconazoleStartStopDateRule extends MohEvaluableRule {

	public static final String TOKEN = "MOH Fluconazole Start-Stop Date";

	//START ANSWERS
	Concept StartDrugs = MohCacheUtils.getConcept(MohEvaluableNameConstants.START_DRUGS);
	Concept FluconazoleDrug = MohCacheUtils.getConcept(MohEvaluableNameConstants.FLUCONAZOLE);

	//STOP ANSWERS :Not necessary
	Concept StopDrugs = MohCacheUtils.getConcept(MohEvaluableNameConstants.STOP_ALL);


	private static class SortByDateComparator implements Comparator<Object> {

		@Override
		public int compare(Object a, Object b) {
			Obs ao = (Obs) a;
			Obs bo = (Obs) b;
			return ao.getObsDatetime().compareTo(bo.getObsDatetime());
		}
	}


	/**
	 * MOH Fluconazole Start-Stop Date
	 * <p/>
	 * For Fluconazole startdate: Use the observation date when CRYPTOCOCCAL TREATMENT PLAN (1277)=START DRUGS (1256) or
	 * CRYPTOCOSSUS TREATMENT STARTED (1278)= FLUCONAZOLE (747) were captured.
	 * <p/>
	 * For Fluconazole stopdate: Use the observation date when CRYPTOCOCCAL TREATMENT PLAN (1277)=STOP ALL (1260) was captured
	 */
	@Override
	protected Result evaluate(LogicContext context, Integer patientId, Map<String, Object> parameters) throws LogicException {
		ObsService obsService = Context.getObsService();
		Patient patient = Context.getPatientService().getPatient(patientId);

		//find obs based on the start-Stop dates 
		List<Obs> obsCol = obsService.getObservations(Arrays.<Person>asList(patient), null, getQuestionConcepts(),
				null, null, null, null, null, null, null, null, false);

		Collections.sort(obsCol, new SortByDateComparator());

		List<Obs> uniqueObs = popObs(obsCol);

		String ret = "";
		boolean wasStart = true;
		for (Obs observations : uniqueObs) {
			if ((observations.getValueCoded().equals(StartDrugs)) || (observations.getValueCoded().equals(FluconazoleDrug))) {
				if (wasStart) {
					if (ret.equals(""))
						ret += (MohRuleUtils.formatdates(observations.getObsDatetime()) + " - ");
					else
						ret += (";" + (MohRuleUtils.formatdates(observations.getObsDatetime())) + " - ");
				} else {
					ret += ((MohRuleUtils.formatdates(observations.getObsDatetime())) + " - ");
				}
				wasStart = true;
			} else if (observations.getValueCoded().equals(StopDrugs)) {
				if (ret.equals("")) {
					ret += (" - " + (MohRuleUtils.formatdates(observations.getObsDatetime())) + ";");
				} else {
					if (wasStart) {
						ret += ((MohRuleUtils.formatdates(observations.getObsDatetime())) + ";");
					} else {
						ret += (" - " + (MohRuleUtils.formatdates(observations.getObsDatetime())) + ";");
					}
				}
				wasStart = false;
			} else {
			}
		}
		return new Result(ret);
	}

	private List<Obs> popObs(List<Obs> listObs) {
		Set<Date> setObs = new HashSet<Date>();
		List<Obs> retObs = new ArrayList<Obs>();

		for (Obs obs2 : listObs) {
			if (!setObs.contains(obs2.getObsDatetime())) {
				if (obs2.getValueCoded().equals(StartDrugs) || obs2.getValueCoded().equals(FluconazoleDrug) || obs2.getValueCoded().equals(StopDrugs)) {
					setObs.add(obs2.getObsDatetime());
					retObs.add(obs2);
				}
			}
		}

		return retObs;
	}

	private List<Concept> getQuestionConcepts() {
		return Arrays.asList(
				new Concept[]{
						MohCacheUtils.getConcept(MohEvaluableNameConstants.CRYPTOCOCCAL_TREATMENT_PLAN),
						MohCacheUtils.getConcept(MohEvaluableNameConstants.CRYPTOCOSSUS_TREATMENT_STARTED)
				});
	}

	protected String getEvaluableToken() {
		return TOKEN;
	}


	/**
	 * @see org.openmrs.logic.Rule#getDependencies()
	 */
	@Override
	public String[] getDependencies() {
		return new String[]{};
	}

	/**
	 * Get the definition of each parameter that should be passed to this rule execution
	 *
	 * @return all parameter that applicable for each rule execution
	 */

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