package org.openmrs.module.amrsreport.rule.collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.OpenmrsObject;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.amrsreport.cache.MohCacheUtils;
import org.openmrs.module.amrsreport.rule.medication.DrugStartStopDateRule;
import org.openmrs.module.amrsreport.service.MohCoreService;
import org.openmrs.module.amrsreport.util.MohFetchRestriction;
import org.springframework.beans.factory.annotation.Autowired;

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

	private static final List<OpenmrsObject> questionConcepts = Arrays.<OpenmrsObject>asList(new Concept[]{
			MohCacheUtils.getConcept(ANTIRETROVIRAL_THERAPY_STATUS),
			MohCacheUtils.getConcept(ARVs_RECOMMENDED_FOR_PEP),
			MohCacheUtils.getConcept(REASON_ANTIRETROVIRALS_STOPPED)
	});

	private static final List<OpenmrsObject> encounterTypes = Arrays.<OpenmrsObject>asList(new EncounterType[]{
			MohCacheUtils.getEncounterType(POST_EXPOSURE_INITIAL_FORM),
			MohCacheUtils.getEncounterType(POST_EXPOSURE_RETURN_FORM)
	});

	private static final MohCoreService mohCoreService = Context.getService(MohCoreService.class);

	public Result evaluate(LogicContext context, Integer patientId, Map<String, Object> parameters) throws LogicException {

		//pull relevant observations then loop while checking concepts
		Map<String, Collection<OpenmrsObject>> obsRestrictions = new HashMap<String, Collection<OpenmrsObject>>();
		obsRestrictions.put("concept", questionConcepts);
		obsRestrictions.put("encounter.encounterType", encounterTypes);
		MohFetchRestriction mohFetchRestriction = new MohFetchRestriction();
		List<Obs> observations = mohCoreService.getPatientObservations(patientId, obsRestrictions, mohFetchRestriction);

		List<Obs> startObs = new ArrayList<Obs>();
		List<Obs> stopObs = new ArrayList<Obs>();

		for (Obs observation : observations) {
			Concept value = observation.getValueCoded();

			if (value.equals(MohCacheUtils.getConcept(ON_ANTIRETROVIRAL_THERAPY))
					|| value.equals(MohCacheUtils.getConcept(NOT_ON_ANTIRETROVIRAL_THERAPY))
					|| value.equals(MohCacheUtils.getConcept(INTERRUPTED))
					|| value.equals(MohCacheUtils.getConcept(UNKNOWN))
					|| value.equals(MohCacheUtils.getConcept(ZIDOVUDINE_AND_LAMIVUDINE))
					|| value.equals(MohCacheUtils.getConcept(LOPINAVIR_AND_RITONAVIR))
					|| value.equals(MohCacheUtils.getConcept(OTHER_ANTIRETROVIRAL_DRUG))
					) {
				startObs.add(observation);
			} else if (value.equals(MohCacheUtils.getConcept(REGIMEN_FAILURE))
					|| value.equals(MohCacheUtils.getConcept(WEIGHT_CHANGE))
					|| value.equals(MohCacheUtils.getConcept(TOXICITY_DRUG))
					|| value.equals(MohCacheUtils.getConcept(OTHER_NON_CODED))
					|| value.equals(MohCacheUtils.getConcept(COMPLETED_TOTAL_PMTCT))
					|| value.equals(MohCacheUtils.getConcept(POOR_ADHERENCE_NOS))
					|| value.equals(MohCacheUtils.getConcept(ADD_DRUGS))
					|| value.equals(MohCacheUtils.getConcept(FACILITY_STOCKED_OUT_OF_MEDICATION))
					|| value.equals(MohCacheUtils.getConcept(TUBERCULOSIS))
					|| value.equals(MohCacheUtils.getConcept(PREGNANCY_RISK))
					|| value.equals(MohCacheUtils.getConcept(FIRST_TRIMESTER_OF_PREGNANCY))
					|| value.equals(MohCacheUtils.getConcept(PATIENT_REFUSAL))
					|| value.equals(MohCacheUtils.getConcept(COMPLETED))
					) {

				stopObs.add(observation);
			}
		}

		return buildResultFromObservations(startObs, stopObs);
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
