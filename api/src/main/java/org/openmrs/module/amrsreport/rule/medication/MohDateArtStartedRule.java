package org.openmrs.module.amrsreport.rule.medication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import org.openmrs.util.OpenmrsUtil;

public class MohDateArtStartedRule extends MohEvaluableRule {

	private static final Log log = LogFactory.getLog(MohDateArtStartedRule.class);

	public static final String TOKEN = "MOH Date ART Started";

	private static class SortByDateComparator implements Comparator<Object> {

		@Override
		public int compare(Object a, Object b) {
			Obs ao = (Obs) a;
			Obs bo = (Obs) b;
			return ao.getValueDatetime().compareTo(bo.getValueDatetime());
		}
	}

	@Override
	public Result evaluate(LogicContext context, Integer patientId, Map<String, Object> parameters) throws LogicException {
		ObsService obsService = Context.getObsService();
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
		for (Obs observations : uniqueObs) {
			ret += MohRuleUtils.formatdates(observations.getValueDatetime()) + ";";
		}

		return new Result(ret);
	}

	private List<Concept> getQuestionConcepts() {
		return Collections.singletonList(MohCacheUtils.getConcept(MohEvaluableNameConstants.ANTIRETROVIRAL_DRUG_TREATMENT_START_DATE));
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
		return Datatype.DATETIME;
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