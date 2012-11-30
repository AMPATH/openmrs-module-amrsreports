package org.openmrs.module.amrsreport.rule.who;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.OpenmrsObject;
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
import org.openmrs.module.amrsreport.service.MohCoreService;
import org.openmrs.module.amrsreport.util.MohFetchRestriction;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MohWHOStageRule extends MohEvaluableRule {

	private static final Log log = LogFactory.getLog(MohWHOStageRule.class);

	public static final String TOKEN = "MOH WHO Stage";

	private static final Concept HivStagingMinorMucocutaneousManifestations = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_MINOR_MUCOCUTANEOUS_MANIFESTATIONS);
	private static final Concept HivStagingAdultHerpesZoster = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_ADULT_HERPES_ZOSTER);
	private static final Concept HivStagingWeightLossLessThanTenPercent = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_WEIGHT_LOSS_LESS_THAN_TEN_PERCENT);
	private static final Concept AngularCheilitis = MohCacheUtils.getConcept(MohEvaluableNameConstants.ANGULAR_CHEILITIS);
	private static final Concept SeborrheicDermatitis = MohCacheUtils.getConcept(MohEvaluableNameConstants.SEBORRHEIC_DERMATITIS);
	private static final Concept HivStagingSeriousBacterialInfections = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_SERIOUS_BACTERIAL_INFECTIONS);
	private static final Concept HivStagingTuberculosisWithinYear = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_TUBERCULOSIS_WITHIN_YEAR);
	private static final Concept HivStagingWeightLossGreaterThanTenPercent = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_WEIGHT_LOSS_GREATER_THAN_TEN_PERCENT);
	private static final Concept ConfirmedHivStagingWeightLossGreaterThanTenPercent = MohCacheUtils.getConcept(MohEvaluableNameConstants.CONFIRMED_HIV_STAGING_WEIGHT_LOSS_GREATER_THAN_TEN_PERCENT);
	private static final Concept ConfirmedHivStagingDiarrheaChronic = MohCacheUtils.getConcept(MohEvaluableNameConstants.CONFIRMED_HIV_STAGING_DIARRHEA_CHRONIC);
	private static final Concept ConfirmedHivStagingPersistentCandidiasisOral = MohCacheUtils.getConcept(MohEvaluableNameConstants.CONFIRMED_HIV_STAGING_PERSISTENT_CANDIDIASIS_ORAL);
	private static final Concept ConfirmedHivStagingPersistentFever = MohCacheUtils.getConcept(MohEvaluableNameConstants.CONFIRMED_HIV_STAGING_PERSISTENT_FEVER);
	private static final Concept ConfirmedWhoStagingOralHairyLeukoplakia = MohCacheUtils.getConcept(MohEvaluableNameConstants.CONFIRMED_WHO_STAGING_ORAL_HAIRY_LEUKOPLAKIA);
	private static final Concept ConfirmedHivStagingTuberculosisPulmonary = MohCacheUtils.getConcept(MohEvaluableNameConstants.CONFIRMED_HIV_STAGING_TUBERCULOSIS_PULMONARY);
	private static final Concept ConfirmedHivStagingSevereBacterialInfections = MohCacheUtils.getConcept(MohEvaluableNameConstants.CONFIRMED_HIV_STAGING_SEVERE_BACTERIAL_INFECTIONS);
	private static final Concept ConfirmedHivStagingUnexplainedAnemiaNeutripaenia = MohCacheUtils.getConcept(MohEvaluableNameConstants.CONFIRMED_HIV_STAGING_UNEXPLAINED_ANAEMIA_NEUTROPAENIA);
	private static final Concept ConfirmedHivStagingAcuteNecrotizingStomatitisGingitivitis = MohCacheUtils.getConcept(MohEvaluableNameConstants.CONFIRMED_HIV_STAGING_ACUTE_NECROTIZING_STOMATITIS_GINGIVITIS);
	private static final Concept HivStagingDisseminatedEndemicMycosis = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_DISSEMINATED_ENDEMIC_MYCOSIS);
	private static final Concept HivStagingLymphoma = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_LYMPHOMA);
	private static final Concept HivStagingMucocutaneousHerpesSimplex = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_MUCOCUTANEOUS_HERPES_SIMPLEX);
	private static final Concept HivStagingSalmonellaSepticemia = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_SALMONELLA_SEPTICEMIA);
	private static final Concept KaposiSarcoma = MohCacheUtils.getConcept(MohEvaluableNameConstants.KAPOSIS_SARCOMA);
	private static final Concept ToxoplasmosisCentralNervousSystem = MohCacheUtils.getConcept(MohEvaluableNameConstants.TOXOPLASMOSIS_CENTRAL_NERVOUS_SYSTEM);
	private static final Concept ConfirmedHivStagingHivWastingSyndrome = MohCacheUtils.getConcept(MohEvaluableNameConstants.CONFIRMED_HIV_STAGING_HIV_WASTING_SYNDROME);
	private static final Concept ConfirmedHivStagingPneumocysticPneumonia = MohCacheUtils.getConcept(MohEvaluableNameConstants.CONFIRMED_HIV_STAGING_PNEUMOCYSTIC_PNEUMONIA);
	private static final Concept ConfirmedHivStagingRecurrentSevereBacterialPneumoia = MohCacheUtils.getConcept(MohEvaluableNameConstants.CONFIRMED_HIV_STAGING_RECURRENT_SEVERE_BACTERIAL_PNEUMONIA);
	private static final Concept ConfirmedHivStagingChronicHerpesSimplex = MohCacheUtils.getConcept(MohEvaluableNameConstants.CONFIRMED_HIV_STAGING_CHRONIC_HERPES_SIMPLEX);
	private static final Concept ConfirmedHivStagingCandidiasis = MohCacheUtils.getConcept(MohEvaluableNameConstants.CONFIRMED_HIV_STAGING_CANDIDIASIS);
	private static final Concept ConfirmedHivStagingExtrapulmonaryTuberculosis = MohCacheUtils.getConcept(MohEvaluableNameConstants.CONFIRMED_HIV_STAGING_EXTRAPULMONARY_TUBERCULOSIS);
	private static final Concept ConfirmedHivStagingKaposiSarcomaKs = MohCacheUtils.getConcept(MohEvaluableNameConstants.CONFIRMED_HIV_STAGING_KAPOSIS_SARCOMA_KS);
	private static final Concept ConfirmedHivStagingCytomegalovirusDisease = MohCacheUtils.getConcept(MohEvaluableNameConstants.CONFIRMED_HIV_STAGING_CYTOMEGALOVIRUS_DISEASE);
	private static final Concept ConfirmedHivStagingToxoplasmosisCns = MohCacheUtils.getConcept(MohEvaluableNameConstants.CONFIRMED_HIV_STAGING_TOXOPLASMOSIS_CNS);
	private static final Concept ConfirmedHivStagingHivEncephalopathy = MohCacheUtils.getConcept(MohEvaluableNameConstants.CONFIRMED_HIV_STAGING_HIV_ENCEPHALOPATHY);
	private static final Concept ConfirmedHivStagingCryptococcossosExtraPulmonary = MohCacheUtils.getConcept(MohEvaluableNameConstants.CONFIRMED_HIV_STAGING_CRYPTOCOCCOSIS_EXTRAPULMONARY);
	private static final Concept ConfirmedHivStagingDisseminatedNonTuberculosisMyobacterialInfection = MohCacheUtils.getConcept(MohEvaluableNameConstants.CONFIRMED_HIV_STAGING_DISSEMINATED_NON_TUBERCULOSIS_MYCOBACTERIAL_INFECTION);
	private static final Concept ConfirmedHivStagingProgressiveMultifocalLeukoencephalopathy = MohCacheUtils.getConcept(MohEvaluableNameConstants.CONFIRMED_HIV_STAGING_PROGRESSIVE_MULTIFOCAL_LEUKOENCEPHALOPATHY);
	private static final Concept ConfirmedHivStagingChronicCryptosporidiosis = MohCacheUtils.getConcept(MohEvaluableNameConstants.CONFIRMED_HIV_STAGING_CHRONIC_CRYPTOSPORIDIOSIS);
	private static final Concept ConfirmedHivStagingChronicIsosporiasis = MohCacheUtils.getConcept(MohEvaluableNameConstants.CONFIRMED_HIV_STAGING_CHRONIC_ISOSPORIASIS);
	//		private static final Concept ConfirmedHivStagingDisseminatedMycosis = MohCacheUtils.getConcept(MohEvaluableNameConstants.CONFIRMED_HIV_STAGING_DISSEMINATED_MYCOSIS);
	private static final Concept ConfirmedHivStagingRecurrentSepticemia = MohCacheUtils.getConcept(MohEvaluableNameConstants.CONFIRMED_HIV_STAGING_RECURRENT_SEPTICEMIA);
	private static final Concept ConfirmedHivStagingLymphoma = MohCacheUtils.getConcept(MohEvaluableNameConstants.CONFIRMED_HIV_STAGING_LYMPHOMA);
	private static final Concept ConfirmedHivStagingInvasiveCervicalCarcinoma = MohCacheUtils.getConcept(MohEvaluableNameConstants.CONFIRMED_HIV_STAGING_INVASIVE_CERVICAL_CARCINOMA);
	private static final Concept ConfirmedHivStagingAtypicalDisseminatedLeishmaniasis = MohCacheUtils.getConcept(MohEvaluableNameConstants.CONFIRMED_HIV_STAGING_ATYPICAL_DISSEMINATED_LEISHMANIASIS);
	private static final Concept ConfirmedHivStagingSymptomaticHivAssociatedNephoropathy = MohCacheUtils.getConcept(MohEvaluableNameConstants.CONFIRMED_HIV_STAGING_SYMPTOMATIC_HIV_ASSOCIATED_NEPHROPATHY);
	private static final Concept AsymptomaticHivInfection = MohCacheUtils.getConcept(MohEvaluableNameConstants.ASYMPTOMATIC_HIV_INFECTION);
	private static final Concept HivStagingPersistentGenerelizedLymphadenopathy = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_PERSISTENT_GENERALIZED_LYMPHADENOPATHY);
	private static final Concept Dermatitis = MohCacheUtils.getConcept(MohEvaluableNameConstants.DERMATITIS);
	private static final Concept HerpesZoster = MohCacheUtils.getConcept(MohEvaluableNameConstants.HERPES_ZOSTER);
	private static final Concept HivStagingHsvStomatitis = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_HSV_STOMATITIS);
	private static final Concept HivStagingRecurrentUpperRespiratoryInfection = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_RECURRENT_UPPER_RESPIRATORY_INFECTION);
	private static final Concept HivStagingSteroidResistantThrombocytopenia = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_STEROID_RESISTANT_THROMBOCYTOPENIA);
	private static final Concept HumanPapillomavirus = MohCacheUtils.getConcept(MohEvaluableNameConstants.HUMAN_PAPILLOMAVIRUS);
	private static final Concept MolluscumContagiosum = MohCacheUtils.getConcept(MohEvaluableNameConstants.MOLLUSCUM_CONTAGIOSUM);
	private static final Concept ParotidEnlargement = MohCacheUtils.getConcept(MohEvaluableNameConstants.PAROTID_ENLARGEMENT);
	private static final Concept VerrucaPlanus = MohCacheUtils.getConcept(MohEvaluableNameConstants.VERRUCA_PLANUS);
	private static final Concept RecurrentOralUlceration = MohCacheUtils.getConcept(MohEvaluableNameConstants.RECURRENT_ORAL_ULCERATION);
	private static final Concept PapularPruriticEruption = MohCacheUtils.getConcept(MohEvaluableNameConstants.PAPULAR_PRURITIC_ERUPTION);
	private static final Concept FungalNailInfection = MohCacheUtils.getConcept(MohEvaluableNameConstants.FUNGAL_NAIL_INFECTIONS);
	private static final Concept LinearGingivalErythema = MohCacheUtils.getConcept(MohEvaluableNameConstants.LINEAR_GINGIVAL_ERYTHEMA);
	private static final Concept WartsGenital = MohCacheUtils.getConcept(MohEvaluableNameConstants.WARTS_GENITAL);
	private static final Concept ChronicUpperRespiratoryTractInfections = MohCacheUtils.getConcept(MohEvaluableNameConstants.CHRONIC_UPPER_RESPIRATORY_TRACT_INFECTIONS);
	private static final Concept Hepatpsplenomegaly = MohCacheUtils.getConcept(MohEvaluableNameConstants.HEPATOSPLENOMEGALY);
	private static final Concept DiarrheaChronic = MohCacheUtils.getConcept(MohEvaluableNameConstants.DIARRHEA_CHRONIC);
	private static final Concept FailureToThrive = MohCacheUtils.getConcept(MohEvaluableNameConstants.FAILURE_TO_THRIVE);
	private static final Concept HivStagingCandidiasisOroresperatoryTract = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_CANDIDIASIS_ORORESPIRATORY_TRACT);
	private static final Concept HivStagingLymphoidInterstitialPneumonia = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_LYMPHOID_INTERSTITIAL_PNEUMONIA);
	private static final Concept HivStagingPedsHerpesZoster = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_PEDS_HERPES_ZOSTER);
	private static final Concept HivStagingRecurrentBActerialPneumonia = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_RECURRENT_BACTERIAL_PNEUMONIA);
	private static final Concept HivStagingRefractoryAnemia = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_REFRACTORY_ANEMIA);
	private static final Concept HivStagingVaricellaDisseminated = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_VARICELLA_DISSEMINATED);
	private static final Concept PneumoniaTuberculosis = MohCacheUtils.getConcept(MohEvaluableNameConstants.PNEUMONIA_TUBERCULOUS);
	private static final Concept RectovaginalFistula = MohCacheUtils.getConcept(MohEvaluableNameConstants.RECTOVAGINAL_FISTULA);
	private static final Concept HivStagingModerateMalnutrition = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_MODERATE_MALNUTRITION);
	private static final Concept HivStagingPersistentFever = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_PERSISTENT_FEVER);
	private static final Concept CandidiasisOral = MohCacheUtils.getConcept(MohEvaluableNameConstants.CANDIDIASIS_ORAL);
	private static final Concept OralHairyLeukoplakia = MohCacheUtils.getConcept(MohEvaluableNameConstants.ORAL_HAIRY_LEUKOPLAKIA);
	private static final Concept LymphNodeTuberculosis = MohCacheUtils.getConcept(MohEvaluableNameConstants.LYMPH_NODE_TUBERCULOSIS);
	private static final Concept HivStagingChronicHivAssociatedLungDisease = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_CHRONIC_HIV_ASSOCIATED_LUNG_DISEASE);
	private static final Concept HivStagingUnexplainedAnemiaNeutropenia = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_UNEXAPLINED_ANEMIA_NEUTROPENIA);
	private static final Concept HivStagingAcuteNecrotizingUlcerativeGingitivitisPeriodontis = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_ACUTE_NECROTIZING_ULCERATIVE_GINGIVITIS_PERIODONTIS);
	private static final Concept Candidiasis = MohCacheUtils.getConcept(MohEvaluableNameConstants.CANDIDIASIS);
	private static final Concept CARDIOMYOPATHY = MohCacheUtils.getConcept(MohEvaluableNameConstants.CARDIOMYOPATHY);
	private static final Concept Cryptococcosis = MohCacheUtils.getConcept(MohEvaluableNameConstants.CRYPTOCOCCOSIS);
	private static final Concept Encephalopathy = MohCacheUtils.getConcept(MohEvaluableNameConstants.ENCEPHALOPATHY);
	private static final Concept HivStagingCoccidiodomycosisDisseminated = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_COCCIDIODOMYCOSIS_DISSEMINATED);
	private static final Concept HivStagingCryptococcosisExtraPulmonary = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_CRYPTOCOCCOSIS_EXTRAPULMONARY);
	private static final Concept HivStagingCryptospoidiosis = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_CRYPTOSPORIDIOSIS);
	private static final Concept HivStagingCytomegalovirusDisease = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_CYTOMEGALOVIRUS_DISEASE);
	private static final Concept HivStagingHistoplasmosisDisseminated = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_HISTOPLASMOSIS_DISSEMINATED);
	private static final Concept HivStagingMycobacteriumOther = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_MYCOBACTERIUM_OTHER);
	private static final Concept HivStagingNonResponsiveHerpesSimplexVirus = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_NONRESPONSIVE_HERPES_SIMPLEX_VIRUS);
	private static final Concept HivStagingSevereBacterialInfection = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_SEVERE_BACTERIAL_INFECTION);
	private static final Concept Nephropathy = MohCacheUtils.getConcept(MohEvaluableNameConstants.NEPHROPATHY);
	private static final Concept PneumocysticCariniiPneumonia = MohCacheUtils.getConcept(MohEvaluableNameConstants.PNEUMOCYSTIC_CARINII_PNEUMONIA);
	private static final Concept ProgressiveMultifocalLeukoencephalopathy = MohCacheUtils.getConcept(MohEvaluableNameConstants.PROGRESSIVE_MULTIFOCAL_LEUKOENCEPHALOPATHY);
	//		private static final Concept Toxoplasmosis = MohCacheUtils.getConcept(MohEvaluableNameConstants.TOXOPLASMOSIS);
	private static final Concept WastingSyndrome = MohCacheUtils.getConcept(MohEvaluableNameConstants.WASTING_SYNDROME);
	//		private static final Concept CandidiasisOesophageal = MohCacheUtils.getConcept(MohEvaluableNameConstants.CANDIDIASIS_OESOPHAGEAL);
	private static final Concept MycobacteriumTuberculosisExtrapulmonary = MohCacheUtils.getConcept(MohEvaluableNameConstants.MYCOBACTERIUM_TUBERCULOSIS_EXTRAPULMONARY);
	//		private static final Concept HivStagingChronicIsisporiasis = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_CHRONIC_ISOSPORIASIS);
	private static final Concept HivStagingCerebralBCellNonHodgkinLymphoma = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_CEREBRAL_B_CELL_NON_HODGKIN_LYMPHOMA);
	private static final Concept PresumptiveHivStagingTuberculosisPulmorary = MohCacheUtils.getConcept(MohEvaluableNameConstants.PRESUMPTIVE_HIV_STAGING_TUBERCULOSIS_PULMONARY);
	private static final Concept OtitisMedia = MohCacheUtils.getConcept(MohEvaluableNameConstants.OTITIS_MEDIA);
//		private static final Concept ADULTWHOCONDITIONQUERY = MohCacheUtils.getConcept(MohEvaluableNameConstants.ADULT_WHO_CONDITION_QUERY);
//		private static final Concept PaedsWhoSpecificQuery = MohCacheUtils.getConcept(MohEvaluableNameConstants.PEDS_WHO_SPECIFIC_CONDITION_QUERY);

	private static final List<OpenmrsObject> questionConcepts = Arrays.<OpenmrsObject>asList(new Concept[]{
			MohCacheUtils.getConcept(MohEvaluableNameConstants.ADULT_WHO_CONDITION_QUERY),
			MohCacheUtils.getConcept(MohEvaluableNameConstants.PEDS_WHO_SPECIFIC_CONDITION_QUERY)
	});

	private static final MohCoreService mohCoreService = Context.getService(MohCoreService.class);

	/**
	 * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, org.openmrs.Patient,
	 *      java.util.Map)
	 */
	@Override
	protected Result evaluate(LogicContext context, Integer patientId, Map<String, Object> parameters) throws LogicException {
		Result result = new Result();

		//pull relevant observations then loop while checking concepts
		Map<String, Collection<OpenmrsObject>> obsRestrictions = new HashMap<String, Collection<OpenmrsObject>>();
		obsRestrictions.put("concept", questionConcepts);
		MohFetchRestriction mohFetchRestriction = new MohFetchRestriction();
		List<Obs> observations = mohCoreService.getPatientObservations(patientId, obsRestrictions, mohFetchRestriction);

		for (Obs WHOObs : observations) {
			Concept value = WHOObs.getValueCoded();

			if (value.equals(AsymptomaticHivInfection)
					|| value.equals(HivStagingPersistentGenerelizedLymphadenopathy)) {

				log.info("Entering stage 1 for determination adult");
				String stageDate1 = MohRuleUtils.formatdates((WHOObs.getObsDatetime()));
				result.add(new Result("WHO STAGE 1 ADULT - " + stageDate1));

			} else if (value.equals(HivStagingAdultHerpesZoster)
					|| value.equals(HivStagingMinorMucocutaneousManifestations)
					|| value.equals(HivStagingRecurrentUpperRespiratoryInfection)
					|| value.equals(HivStagingWeightLossLessThanTenPercent)
					|| value.equals(AngularCheilitis)
					|| value.equals(RecurrentOralUlceration)
					|| value.equals(PapularPruriticEruption)
					|| value.equals(SeborrheicDermatitis)
					|| value.equals(FungalNailInfection)
					) {

				log.info("Entering stage 2 for determination adult");
				String stageDate2 = MohRuleUtils.formatdates(WHOObs.getObsDatetime());
				result.add(new Result("WHO STAGE 2 ADULT - " + stageDate2));

			} else if (value.equals(HivStagingWeightLossGreaterThanTenPercent)
					|| value.equals(CandidiasisOral)
					|| value.equals(DiarrheaChronic)
					|| value.equals(HivStagingPersistentFever)
					|| value.equals(HivStagingSeriousBacterialInfections)
					|| value.equals(HivStagingTuberculosisWithinYear)
					|| value.equals(OralHairyLeukoplakia)
					|| value.equals(ConfirmedHivStagingWeightLossGreaterThanTenPercent)
					|| value.equals(ConfirmedHivStagingDiarrheaChronic)
					|| value.equals(ConfirmedHivStagingPersistentCandidiasisOral)
					|| value.equals(ConfirmedHivStagingPersistentFever)
					|| value.equals(ConfirmedWhoStagingOralHairyLeukoplakia)
					|| value.equals(PresumptiveHivStagingTuberculosisPulmorary)
					|| value.equals(ConfirmedHivStagingTuberculosisPulmonary)
					|| value.equals(ConfirmedHivStagingSevereBacterialInfections)
					|| value.equals(ConfirmedHivStagingUnexplainedAnemiaNeutripaenia)
					|| value.equals(ConfirmedHivStagingAcuteNecrotizingStomatitisGingitivitis)
					) {

				log.info("Entering stage 3 for determination adult");
				String stageDate3 = MohRuleUtils.formatdates(WHOObs.getObsDatetime());
				result.add(new Result("WHO STAGE 3 ADULT - " + stageDate3));

			} else if (value.equals(Encephalopathy)
					|| value.equals(HivStagingCandidiasisOroresperatoryTract)
					|| value.equals(HivStagingCryptococcosisExtraPulmonary)
					|| value.equals(HivStagingCryptospoidiosis)
					|| value.equals(HivStagingCytomegalovirusDisease)
					|| value.equals(HivStagingDisseminatedEndemicMycosis)
					|| value.equals(HivStagingLymphoma)
					|| value.equals(HivStagingMucocutaneousHerpesSimplex)
					|| value.equals(HivStagingMycobacteriumOther)
					|| value.equals(HivStagingSalmonellaSepticemia)
					|| value.equals(KaposiSarcoma)
					|| value.equals(MycobacteriumTuberculosisExtrapulmonary)
					|| value.equals(PneumocysticCariniiPneumonia)
					|| value.equals(ProgressiveMultifocalLeukoencephalopathy)
					|| value.equals(WastingSyndrome)
					|| value.equals(ToxoplasmosisCentralNervousSystem)
					|| value.equals(ConfirmedHivStagingHivWastingSyndrome)
					|| value.equals(ConfirmedHivStagingPneumocysticPneumonia)
					|| value.equals(ConfirmedHivStagingRecurrentSevereBacterialPneumoia)
					|| value.equals(ConfirmedHivStagingChronicHerpesSimplex)
					|| value.equals(ConfirmedHivStagingCandidiasis)
					|| value.equals(ConfirmedHivStagingExtrapulmonaryTuberculosis)
					|| value.equals(ConfirmedHivStagingKaposiSarcomaKs)
					|| value.equals(ConfirmedHivStagingCytomegalovirusDisease)
					|| value.equals(ConfirmedHivStagingToxoplasmosisCns)
					|| value.equals(ConfirmedHivStagingHivEncephalopathy)
					|| value.equals(ConfirmedHivStagingCryptococcossosExtraPulmonary)
					|| value.equals(ConfirmedHivStagingDisseminatedNonTuberculosisMyobacterialInfection)
					|| value.equals(ConfirmedHivStagingProgressiveMultifocalLeukoencephalopathy)
					|| value.equals(ConfirmedHivStagingChronicCryptosporidiosis)
					|| value.equals(ConfirmedHivStagingChronicIsosporiasis)
					|| value.equals(ConfirmedHivStagingRecurrentSepticemia)
					|| value.equals(ConfirmedHivStagingLymphoma)
					|| value.equals(ConfirmedHivStagingInvasiveCervicalCarcinoma)
					|| value.equals(ConfirmedHivStagingAtypicalDisseminatedLeishmaniasis)
					|| value.equals(ConfirmedHivStagingSymptomaticHivAssociatedNephoropathy)
					) {

				log.info("Entering stage 4 for determination adult");
				String stageDate4 = MohRuleUtils.formatdates(WHOObs.getObsDatetime());
				result.add(new Result("WHO STAGE 4 ADULT - " + stageDate4));

			} else if (value.equals(AsymptomaticHivInfection)
					|| value.equals(Hepatpsplenomegaly)
					|| value.equals(HivStagingPersistentGenerelizedLymphadenopathy)
					) {

				log.info("Entering stage 1 for determination paeds");
				String stageDatePaeds1 = MohRuleUtils.formatdates(WHOObs.getObsDatetime());
				result.add(new Result("WHO STAGE 1 PEDS - " + stageDatePaeds1));

			} else if (value.equals(Dermatitis)
					|| value.equals(HerpesZoster)
					|| value.equals(HivStagingHsvStomatitis)
					|| value.equals(HivStagingRecurrentUpperRespiratoryInfection)
					|| value.equals(HivStagingSteroidResistantThrombocytopenia)
					|| value.equals(HumanPapillomavirus)
					|| value.equals(MolluscumContagiosum)
					|| value.equals(OtitisMedia)
					|| value.equals(ParotidEnlargement)
					|| value.equals(VerrucaPlanus)
					|| value.equals(Hepatpsplenomegaly)
					|| value.equals(RecurrentOralUlceration)
					|| value.equals(PapularPruriticEruption)
					|| value.equals(FungalNailInfection)
					|| value.equals(LinearGingivalErythema)
					|| value.equals(WartsGenital)
					|| value.equals(ChronicUpperRespiratoryTractInfections)
					) {

				log.info("Entering stage 2 for determination paeds");
				String stagePaeds2 = "WHO STAGE 2 PEDS";
				String stageDatePaeds2 = MohRuleUtils.formatdates(WHOObs.getObsDatetime());
				result.add(new Result("WHO STAGE 2 PEDS - " + stageDatePaeds2));

			} else if (value.equals(DiarrheaChronic)
					|| value.equals(HivStagingCandidiasisOroresperatoryTract)
					|| value.equals(FailureToThrive)
					|| value.equals(HivStagingLymphoidInterstitialPneumonia)
					|| value.equals(HivStagingNonResponsiveHerpesSimplexVirus)
					|| value.equals(HivStagingPedsHerpesZoster)
					|| value.equals(HivStagingRecurrentBActerialPneumonia)
					|| value.equals(HivStagingRefractoryAnemia)
					|| value.equals(HivStagingVaricellaDisseminated)
					|| value.equals(PneumoniaTuberculosis)
					|| value.equals(RectovaginalFistula)
					|| value.equals(HivStagingModerateMalnutrition)
					|| value.equals(HivStagingPersistentFever)
					|| value.equals(CandidiasisOral)
					|| value.equals(OralHairyLeukoplakia)
					|| value.equals(LymphNodeTuberculosis)
					|| value.equals(HivStagingChronicHivAssociatedLungDisease)
					|| value.equals(HivStagingUnexplainedAnemiaNeutropenia)
					|| value.equals(HivStagingAcuteNecrotizingUlcerativeGingitivitisPeriodontis)
					) {

				log.info("Entering stage 3 for determination paeds");
				String stageDatePaeds3 = MohRuleUtils.formatdates(WHOObs.getObsDatetime());
				result.add(new Result("WHO STAGE 3 PEDS - " + stageDatePaeds3));

			} else if (value.equals(CARDIOMYOPATHY)
					|| value.equals(Candidiasis)
					|| value.equals(Cryptococcosis)
					|| value.equals(Encephalopathy)
					|| value.equals(HivStagingCoccidiodomycosisDisseminated)
					|| value.equals(HivStagingCryptococcosisExtraPulmonary)
					|| value.equals(HivStagingCryptospoidiosis)
					|| value.equals(HivStagingCytomegalovirusDisease)
					|| value.equals(HivStagingHistoplasmosisDisseminated)
					|| value.equals(HivStagingMycobacteriumOther)
					|| value.equals(HivStagingNonResponsiveHerpesSimplexVirus)
					|| value.equals(HivStagingSevereBacterialInfection)
					|| value.equals(KaposiSarcoma)
					|| value.equals(Nephropathy)
					|| value.equals(PneumocysticCariniiPneumonia)
					|| value.equals(ProgressiveMultifocalLeukoencephalopathy)
					|| value.equals(MycobacteriumTuberculosisExtrapulmonary)
					|| value.equals(HivStagingCerebralBCellNonHodgkinLymphoma)

					) {

				log.info("Entering stage 4 for determination paeds");
				String stageDatePaeds4 = MohRuleUtils.formatdates(WHOObs.getObsDatetime());
				result.add(new Result("WHO STAGE 4 PEDS - " + stageDatePaeds4));
			}
		}

		return result;
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

	@Override
	public Datatype getDefaultDatatype() {
		return Datatype.DATETIME;
	}

	public Set<RuleParameterInfo> getParameterList() {
		return null;
	}

	@Override
	public int getTTL() {
		return 0;
	}
}