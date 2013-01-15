package org.openmrs.module.amrsreport.rule.collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.OpenmrsObject;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.amrsreport.cache.MohCacheUtils;
import org.openmrs.module.amrsreport.rule.medication.DrugStartStopDateRule;
import org.openmrs.module.amrsreport.rule.util.MohRuleUtils;
import org.openmrs.module.amrsreport.service.MohCoreService;
import org.openmrs.module.amrsreport.util.MohFetchOrdering;
import org.openmrs.module.amrsreport.util.MohFetchRestriction;
import org.openmrs.util.OpenmrsUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MohPEPStartStopDateRule extends DrugStartStopDateRule {

	private static final Log log = LogFactory.getLog(MohPEPStartStopDateRule.class);

	public static final String TOKEN = "MOH PEP Start Stop Date";

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
	public static final String DAYS_ON_PEP_MEDS1 = "DAYS ON POST EXPOSURE PROPHYLAXIS BEFORE STOPPING DUE TO NON-ADHERENCE, ANTIRETROVIRALS";
	public static final String DAYS_ON_PEP_MEDS2 = "DAYS ON POST EXPOSURE PROPHYLAXIS BEFORE STOPPING DUE TO SIDE EFFECTS, ANTIRETROVIRALS";

	private static final List<OpenmrsObject> questionConcepts = Arrays.<OpenmrsObject>asList(
			MohCacheUtils.getConcept(ANTIRETROVIRAL_THERAPY_STATUS),
			MohCacheUtils.getConcept(ARVs_RECOMMENDED_FOR_PEP),
			MohCacheUtils.getConcept(REASON_ANTIRETROVIRALS_STOPPED),
			MohCacheUtils.getConcept(DAYS_ON_PEP_MEDS1),
			MohCacheUtils.getConcept(DAYS_ON_PEP_MEDS2)
	);

	private static final List<Concept> numericQuestions = Arrays.<Concept>asList(
			MohCacheUtils.getConcept(DAYS_ON_PEP_MEDS1),
			MohCacheUtils.getConcept(DAYS_ON_PEP_MEDS2)
	);

	private static final List<OpenmrsObject> encounterTypes = Arrays.<OpenmrsObject>asList(
			MohCacheUtils.getEncounterType(POST_EXPOSURE_INITIAL_FORM),
			MohCacheUtils.getEncounterType(POST_EXPOSURE_RETURN_FORM)
	);

	private MohCoreService mohCoreService = Context.getService(MohCoreService.class);

	private static final List<Concept> START_CONCEPTS = Arrays.<Concept>asList(
			MohCacheUtils.getConcept(ON_ANTIRETROVIRAL_THERAPY),
			//MohCacheUtils.getConcept(NOT_ON_ANTIRETROVIRAL_THERAPY),
			//MohCacheUtils.getConcept(INTERRUPTED),
			//MohCacheUtils.getConcept(UNKNOWN),
			MohCacheUtils.getConcept(ZIDOVUDINE_AND_LAMIVUDINE),
			MohCacheUtils.getConcept(LOPINAVIR_AND_RITONAVIR)
			//MohCacheUtils.getConcept(OTHER_ANTIRETROVIRAL_DRUG)
	);

	private static final List<Concept> STOP_CONCEPTS = Arrays.<Concept>asList(
			//MohCacheUtils.getConcept(REGIMEN_FAILURE),
			//MohCacheUtils.getConcept(WEIGHT_CHANGE),
			//MohCacheUtils.getConcept(TOXICITY_DRUG),
			//MohCacheUtils.getConcept(OTHER_NON_CODED),
			//MohCacheUtils.getConcept(COMPLETED_TOTAL_PMTCT),
			//MohCacheUtils.getConcept(POOR_ADHERENCE_NOS),
			//MohCacheUtils.getConcept(ADD_DRUGS),
			//MohCacheUtils.getConcept(FACILITY_STOCKED_OUT_OF_MEDICATION),
			//MohCacheUtils.getConcept(TUBERCULOSIS),
			//MohCacheUtils.getConcept(PREGNANCY_RISK),
			//MohCacheUtils.getConcept(FIRST_TRIMESTER_OF_PREGNANCY),
			MohCacheUtils.getConcept(PATIENT_REFUSAL),
			MohCacheUtils.getConcept(COMPLETED)
	);

	/**
	 * @should start on ANTIRETROVIRAL THERAPY STATUS of ON ANTIRETROVIRAL THERAPY
	 * @should start on ARVs RECOMMENDED FOR PEP is ZIDOVUDINE AND LAMIVUDINE
	 * @should start on ARVs RECOMMENDED FOR PEP is LOPINAVIR AND RITONAVIR
	 * @should stop on REASON ANTIRETROVIRALS STOPPED is PATIENT REFUSAL
	 * @should stop on REASON ANTIRETROVIRALS STOPPED is COMPLETED
	 * @should stop on any value for DAYS ON POST EXPOSURE PROPHYLAXIS BEFORE STOPPING DUE TO NON ADHERENCE ANTIRETROVIRALS
	 * @should stop on any value for DAYS ON POST EXPOSURE PROPHYLAXIS BEFORE STOPPING DUE TO SIDE EFFECTS ANTIRETROVIRALS
	 * @see org.openmrs.module.amrsreport.rule.MohEvaluableRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	public Result evaluate(LogicContext context, Integer patientId, Map<String, Object> parameters) throws LogicException {

		// pull relevant observations then loop while checking concepts
		Map<String, Collection<OpenmrsObject>> obsRestrictions = new HashMap<String, Collection<OpenmrsObject>>();
		obsRestrictions.put("concept", questionConcepts);
		Map<String, Collection<OpenmrsObject>> encounterRestrictions = new HashMap<String, Collection<OpenmrsObject>>();
		obsRestrictions.put("encounterType", encounterTypes);

		MohFetchRestriction mohFetchRestriction = new MohFetchRestriction();
		mohFetchRestriction.setFetchOrdering(MohFetchOrdering.ORDER_ASCENDING);

		List<Obs> observations = mohCoreService.getPatientObservationsWithEncounterRestrictions(
				patientId, obsRestrictions, encounterRestrictions, mohFetchRestriction);

		List<Obs> startObs = new ArrayList<Obs>();
		List<Obs> stopObs = new ArrayList<Obs>();

		for (Obs observation : observations) {
			Concept value = observation.getValueCoded();

			if (OpenmrsUtil.isConceptInList(value, START_CONCEPTS)) {
				startObs.add(observation);
			} else if (OpenmrsUtil.isConceptInList(value, STOP_CONCEPTS) ||
					OpenmrsUtil.isConceptInList(observation.getConcept(), numericQuestions)) {
				stopObs.add(observation);
			}
		}

		return buildResultFromObservations(startObs, stopObs);
	}

	@Override
	protected boolean validateStartObs(Obs obs) {
		return (MohRuleUtils.compareConceptToName(obs.getConcept(), ANTIRETROVIRAL_THERAPY_STATUS) &&
				MohRuleUtils.compareConceptToName(obs.getValueCoded(), ON_ANTIRETROVIRAL_THERAPY))
				||
				(MohRuleUtils.compareConceptToName(obs.getConcept(), ARVs_RECOMMENDED_FOR_PEP) &&
						(MohRuleUtils.compareConceptToName(obs.getValueCoded(), LOPINAVIR_AND_RITONAVIR) ||
								MohRuleUtils.compareConceptToName(obs.getValueCoded(), ZIDOVUDINE_AND_LAMIVUDINE))
				);
	}

	@Override
	protected boolean validateStopObs(Obs obs) {
		return (MohRuleUtils.compareConceptToName(obs.getConcept(), DAYS_ON_PEP_MEDS1) ||
				MohRuleUtils.compareConceptToName(obs.getConcept(), DAYS_ON_PEP_MEDS2) ||
				(MohRuleUtils.compareConceptToName(obs.getConcept(), REASON_ANTIRETROVIRALS_STOPPED) &&
						(MohRuleUtils.compareConceptToName(obs.getValueCoded(), PATIENT_REFUSAL) ||
								MohRuleUtils.compareConceptToName(obs.getValueCoded(), COMPLETED))
				));
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
