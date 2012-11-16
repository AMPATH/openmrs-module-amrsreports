package org.openmrs.module.amrsreport.rule.collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.amrsreport.cache.MohCacheUtils;
import org.openmrs.module.amrsreport.rule.MohEvaluableRule;
import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.module.amrsreport.rule.util.MohRuleUtils;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Nicholas Ingosi Magaja
 * Date: 5/29/12
 * Time: 9:03 AM
 * To change this template use File | Settings | File Templates.
 */
public class MohPEPStartStopDateRule extends MohEvaluableRule {

	private static final Log log = LogFactory.getLog(MohPEPStartStopDateRule.class);

	public static final String TOKEN = "MOH PEP Start Stop Date";

	private Map<String, Concept> cachedConcepts = null;
	private List<Concept> cachedQuestions = null;

	private Map<String, EncounterType> cachedEncounterType = null;
	//private List<Encounter> cachedEncounters = null;


	public static final String POST_EXPOSURE_INITIAL_FORM = "PEPINITIAL";
	public static final String POST_EXPOSURE_RETURN_FORM = "PEPRETURN";
	public static final String ANTIRETROVIRAL_THERAPY_STATUS = "ANTIRETROVIRAL THERAPY STATUS";
	public static final String ON_ANTIRETROVIRAL_THERAPY = "ON ANTIRETROVIRAL THERAPY";
	public static final String ARVs_RECOMMENDED_FOR_PEP = "ARVs RECOMMENDED FOR PEP";
	public static final String ZIDOVUDINE_AND_LAMIVUDINE = "ZIDOVUDINE AND LAMIVUDINE";
	public static final String LOPINAVIR_AND_RITONAVIR = "LOPINAVIR AND RITONAVIR";
	public static final String OTHER_ANTIRETROVIRAL_DRUG = "OTHER ANTIRETROVIRAL DRUG";
	public static final String REASON_ANTIRETROVIRALS_STOPPED = "REASON ANTIRETROVIRALS STOPPED";
	public static final String REGIMEN_FAILURE = "REGIMEN FAILURE";
	public static final String WEIGHT_CHANGE = "WEIGHT CHANGE";
	public static final String TOXICITY_DRUG = "TOXICITY, DRUG";
	public static final String OTHER_NON_CODED = "OTHER NON-CODED";
	public static final String COMPLETED_TOTAL_PMTCT = "COMPLETED TOTAL PMTCT";
	public static final String POOR_ADHERENCE_NOS = "POOR ADHERENCE, NOS";
	public static final String ADD_DRUGS = "ADD DRUG(S)";
	public static final String FACILITY_STOCKED_OUT_OF_MEDICATION = "FACILITY STOCKED OUT OF MEDICATION";
	public static final String TUBERCULOSIS = "TUBERCULOSIS";
	public static final String PREGNANCY_RISK = "PREGNANCY RISK";
	public static final String PATIENT_REFUSAL = "PATIENT REFUSAL";
	public static final String COMPLETED = "COMPLETED";
	public static final String FIRST_TRIMESTER_OF_PREGNANCY = "FIRST TRIMESTER OF PREGNANCY";
	public static final String NOT_ON_ANTIRETROVIRAL_THERAPY = "NOT ON ANTIRETROVIRAL THERAPY";
	public static final String INTERRUPTED = "INTERRUPTED";
	public static final String UNKNOWN = "UNKNOWN";

	private static class SortByDateComparator implements Comparator<Object> {

		@Override
		public int compare(Object a, Object b) {
			Obs ao = (Obs) a;
			Obs bo = (Obs) b;
			return ao.getObsDatetime().compareTo(bo.getObsDatetime());
		}
	}

	public Result evaluate(LogicContext context, Integer patientId, Map<String, Object> parameters) throws LogicException {
		ObsService obsService = Context.getObsService();

		Patient patient = Context.getPatientService().getPatient(patientId);
		String pepDates = "";
		boolean startPep = true;

		List<Obs> observations = obsService.getObservations(Arrays.<Person>asList(patient), getCachedEncounters(patient), getQuestionConcepts(),
				null, null, null, null, null, null, null, null, false);

		Collections.sort(observations, new SortByDateComparator());

		for (Obs observation : observations) {

			if (
					(observation.getValueCoded().equals(MohCacheUtils.getConcept(ON_ANTIRETROVIRAL_THERAPY)))
							|| (observation.getValueCoded().equals(MohCacheUtils.getConcept(NOT_ON_ANTIRETROVIRAL_THERAPY)))
							|| (observation.getValueCoded().equals(MohCacheUtils.getConcept(INTERRUPTED)))
							|| (observation.getValueCoded().equals(MohCacheUtils.getConcept(UNKNOWN)))
							|| (observation.getValueCoded().equals(MohCacheUtils.getConcept(ZIDOVUDINE_AND_LAMIVUDINE)))
							|| (observation.getValueCoded().equals(MohCacheUtils.getConcept(LOPINAVIR_AND_RITONAVIR)))
							|| (observation.getValueCoded().equals(MohCacheUtils.getConcept(OTHER_ANTIRETROVIRAL_DRUG)))
					) {


				if (startPep) {
					if (pepDates.equals(""))
						pepDates += MohRuleUtils.formatdates(observation.getObsDatetime()) + " - ";
					else
						pepDates += ";" + (MohRuleUtils.formatdates(observation.getObsDatetime())) + " - ";
				} else {
					pepDates += MohRuleUtils.formatdates(observation.getObsDatetime()) + " - ";
				}
			}
			if (
					(observation.getValueCoded().equals(MohCacheUtils.getConcept(REGIMEN_FAILURE)))
							|| (observation.getValueCoded().equals(MohCacheUtils.getConcept(WEIGHT_CHANGE)))
							|| (observation.getValueCoded().equals(MohCacheUtils.getConcept(TOXICITY_DRUG)))
							|| (observation.getValueCoded().equals(MohCacheUtils.getConcept(OTHER_NON_CODED)))
							|| (observation.getValueCoded().equals(MohCacheUtils.getConcept(COMPLETED_TOTAL_PMTCT)))
							|| (observation.getValueCoded().equals(MohCacheUtils.getConcept(POOR_ADHERENCE_NOS)))
							|| (observation.getValueCoded().equals(MohCacheUtils.getConcept(ADD_DRUGS)))
							|| (observation.getValueCoded().equals(MohCacheUtils.getConcept(FACILITY_STOCKED_OUT_OF_MEDICATION)))
							|| (observation.getValueCoded().equals(MohCacheUtils.getConcept(TUBERCULOSIS)))
							|| (observation.getValueCoded().equals(MohCacheUtils.getConcept(PREGNANCY_RISK)))
							|| (observation.getValueCoded().equals(MohCacheUtils.getConcept(FIRST_TRIMESTER_OF_PREGNANCY)))
							|| (observation.getValueCoded().equals(MohCacheUtils.getConcept(PATIENT_REFUSAL)))
							|| (observation.getValueCoded().equals(MohCacheUtils.getConcept(COMPLETED)))
					) {

				startPep = false;
				if (pepDates.equals("")) {
					pepDates += (" - " + (MohRuleUtils.formatdates(observation.getObsDatetime())) + ";");
				} else {
					if (startPep) {
						pepDates += ((MohRuleUtils.formatdates(observation.getObsDatetime())) + ";");
					} else {
						pepDates += (" - " + (MohRuleUtils.formatdates(observation.getObsDatetime())) + ";");
					}

				}

			}


		}

		return new Result(pepDates);


	}

	private List<Concept> getQuestionConcepts() {
		return Arrays.asList(
				new Concept[]{
						MohCacheUtils.getConcept(ANTIRETROVIRAL_THERAPY_STATUS),
						MohCacheUtils.getConcept(ARVs_RECOMMENDED_FOR_PEP),
						MohCacheUtils.getConcept(REASON_ANTIRETROVIRALS_STOPPED)
				});
	}

	private EncounterType getCachedEncounterType(String name) {

		if (cachedEncounterType == null)
			cachedEncounterType = new HashMap<String, EncounterType>();
		if (!cachedEncounterType.containsKey(name))
			cachedEncounterType.put(name, Context.getEncounterService().getEncounterType(name));
		return cachedEncounterType.get(name);

	}

	private List<Encounter> getCachedEncounters(Patient patient) {
		List<Encounter> pepinitialreturn = new ArrayList<Encounter>();
		List<Encounter> cachedEncounters = new ArrayList<Encounter>();


		cachedEncounters.addAll(Context.getEncounterService().getEncountersByPatient(patient));

		//log.info("All encounters for patient "+patient.getPatientId()+"  is"+cachedEncounters.size());

		for (Encounter encounters : cachedEncounters) {

			if (encounters.getEncounterType().equals(getCachedEncounterType(POST_EXPOSURE_INITIAL_FORM))
					|| encounters.getEncounterType().equals(getCachedEncounterType(POST_EXPOSURE_RETURN_FORM)))

				pepinitialreturn.add(encounters);
		}


		return pepinitialreturn;
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
	public Result.Datatype getDefaultDatatype() {
		return Result.Datatype.TEXT;
	}

	public Set<RuleParameterInfo> getParameterList() {
		return null;
	}

	@Override
	public int getTTL() {
		return 0;
	}

}
