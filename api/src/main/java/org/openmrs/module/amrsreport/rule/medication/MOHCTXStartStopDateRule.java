package org.openmrs.module.amrsreport.rule.medication;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
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
public class MOHCTXStartStopDateRule extends MohEvaluableRule {

	private static final Log log = LogFactory.getLog(MOHCTXStartStopDateRule.class);

	public static final String TOKEN = "MOH CTX Start-Stop Date";

	private static class SortByDateComparator implements Comparator<Object> {

		@Override
		public int compare(Object a, Object b) {
			Obs ao = (Obs) a;
			Obs bo = (Obs) b;
			return ao.getObsDatetime().compareTo(bo.getObsDatetime());
		}
	}

	//List of concepts to be used in comparison as for start dates
	Concept PCP_PROPHYLAXIS_STARTED = MohCacheUtils.getConcept(MohEvaluableNameConstants.PCP_PROPHYLAXIS_STARTED);
	Concept REASON_PCP_PROPHYLAXIS_STOPPED = MohCacheUtils.getConcept(MohEvaluableNameConstants.REASON_PCP_PROPHYLAXIS_STOPPED);

	//concepts to be used for stop dates
	//Concept DAPSONE=MohCacheUtils.getConcept(MohEvaluableNameConstants.DAPSONE);

	//The real method that does the magic comes here
	public Result evaluate(final LogicContext context, final Integer patientId, final Map<String, Object> parameters) throws LogicException {

		//find the patient based on the patient id
		Patient patient = Context.getPatientService().getPatient(patientId);

		//find the observation based on the patient and a set of  concept question required
		List<Obs> ctxObs = Context.getObsService().getObservations(Arrays.<Person>asList(patient), null, getQuestionConcepts(),
				null, null, null, null, null, null, null, null, false);

		Collections.sort(ctxObs, new SortByDateComparator());

		List<Obs> uniqueCTXObs = popObs(ctxObs);
		String ret = "";
		boolean wasStart = true;

		for (Obs observations : uniqueCTXObs) {
			if (observations.getConcept().equals(PCP_PROPHYLAXIS_STARTED)) {
				if (wasStart) {
					if (ret.equals(""))
						ret += MohRuleUtils.formatdates(observations.getObsDatetime()) + " - ";
					else
						ret += (";" + (MohRuleUtils.formatdates(observations.getObsDatetime())) + " - ");
				} else {
					ret += ((MohRuleUtils.formatdates(observations.getObsDatetime())) + " - ");
				}
				wasStart = true;
			} else {
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
			}
		}
		return new Result(ret);

	}

	private List<Concept> getQuestionConcepts() {
		return Arrays.asList(
				new Concept[]{
						MohCacheUtils.getConcept(MohEvaluableNameConstants.REASON_PCP_PROPHYLAXIS_STOPPED),
						MohCacheUtils.getConcept(MohEvaluableNameConstants.PCP_PROPHYLAXIS_STARTED)
				});
	}

	//pass to a function setObsPop(obsCol)
	//loop thru the obsCol and get unique obsValueDateTime > Then add to newSet
	//return newSet
	private List<Obs> popObs(List<Obs> listObs) {
		Set<Date> setObs = new HashSet<Date>();
		List<Obs> retObs = new ArrayList<Obs>();

		for (Obs obs2 : listObs) {
			if (!setObs.contains(obs2.getObsDatetime())) {
				setObs.add(obs2.getObsDatetime());
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