package org.openmrs.module.amrsreport.rule.who;

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

public class MohWHOStageRule extends MohEvaluableRule {

	@SuppressWarnings("unused")
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

	private static class SortByDateComparator implements Comparator<Object> {

		@Override
		public int compare(Object a, Object b) {
			Obs ao = (Obs) a;
			Obs bo = (Obs) b;
			return bo.getObsDatetime().compareTo(ao.getObsDatetime());
		}
	}

	/**
	 * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, org.openmrs.Patient,
	 *      java.util.Map)
	 */
	@Override
	protected Result evaluate(LogicContext context, Integer patientId, Map<String, Object> parameters) throws LogicException {
		Result result = new Result();

		//find the patient
		Patient patient = Context.getPatientService().getPatient(patientId);

		////////////////////////////////////////////////////////////////////////////////////////////////////////////
		//find all obs based on the who stages
		//find the observation based on the patient and a set of  concept question required
		List<Obs> whoObs = Context.getObsService().getObservations(Arrays.<Person>asList(patient), null, getQuestionConcepts(),
				null, null, null, null, null, null, null, null, false);

		// sort the obs per the obsdate time
		Collections.sort(whoObs, new SortByDateComparator());
		List<Obs> uniqueWHOObs = popObs(whoObs);

		for (Obs WHOObs : uniqueWHOObs) {
			//start processing WHO for adults


			if (WHOObs.getValueCoded().equals(AsymptomaticHivInfection)
					|| WHOObs.getValueCoded().equals(HivStagingPersistentGenerelizedLymphadenopathy)) {

				log.info("Entering stage 1 for determination adult");
				String stage1 = "WHO STAGE 1 ADULT";
				String stageDate1 = MohRuleUtils.formatdates((WHOObs.getObsDatetime()));
				String stageDateCombined1 = stage1 + "-" + stageDate1;
				Result WHOStage1AdultResult = new Result(stageDateCombined1);
				result.add(WHOStage1AdultResult);
			} else if (WHOObs.getValueCoded().equals(HivStagingAdultHerpesZoster)
					|| WHOObs.getValueCoded().equals(HivStagingMinorMucocutaneousManifestations)
					|| WHOObs.getValueCoded().equals(HivStagingRecurrentUpperRespiratoryInfection)
					|| WHOObs.getValueCoded().equals(HivStagingWeightLossLessThanTenPercent)
					|| WHOObs.getValueCoded().equals(AngularCheilitis)
					|| WHOObs.getValueCoded().equals(RecurrentOralUlceration)
					|| WHOObs.getValueCoded().equals(PapularPruriticEruption)
					|| WHOObs.getValueCoded().equals(SeborrheicDermatitis)
					|| WHOObs.getValueCoded().equals(FungalNailInfection)
					) {
				log.info("Entering stage 2 for determination adult");
				String stage2 = "WHO STAGE 2 ADULT";
				String stageDate2 = MohRuleUtils.formatdates(WHOObs.getObsDatetime());
				String stageDateCombined2 = stage2 + "-" + stageDate2;
				Result WHOStage2AdultResult = new Result(stageDateCombined2);
				result.add(WHOStage2AdultResult);

			} else if (WHOObs.getValueCoded().equals(HivStagingWeightLossGreaterThanTenPercent)
					|| WHOObs.getValueCoded().equals(CandidiasisOral)
					|| WHOObs.getValueCoded().equals(DiarrheaChronic)
					|| WHOObs.getValueCoded().equals(HivStagingPersistentFever)
					|| WHOObs.getValueCoded().equals(HivStagingSeriousBacterialInfections)
					|| WHOObs.getValueCoded().equals(HivStagingTuberculosisWithinYear)
					|| WHOObs.getValueCoded().equals(OralHairyLeukoplakia)
					|| WHOObs.getValueCoded().equals(ConfirmedHivStagingWeightLossGreaterThanTenPercent)
					|| WHOObs.getValueCoded().equals(ConfirmedHivStagingDiarrheaChronic)
					|| WHOObs.getValueCoded().equals(ConfirmedHivStagingPersistentCandidiasisOral)
					|| WHOObs.getValueCoded().equals(ConfirmedHivStagingPersistentFever)
					|| WHOObs.getValueCoded().equals(ConfirmedWhoStagingOralHairyLeukoplakia)
					|| WHOObs.getValueCoded().equals(PresumptiveHivStagingTuberculosisPulmorary)
					|| WHOObs.getValueCoded().equals(ConfirmedHivStagingTuberculosisPulmonary)
					|| WHOObs.getValueCoded().equals(ConfirmedHivStagingSevereBacterialInfections)
					|| WHOObs.getValueCoded().equals(ConfirmedHivStagingUnexplainedAnemiaNeutripaenia)
					|| WHOObs.getValueCoded().equals(ConfirmedHivStagingAcuteNecrotizingStomatitisGingitivitis)
					) {
				log.info("Entering stage 3 for determination adult");
				String stage3 = "WHO STAGE 3 ADULT";
				String stageDate3 = MohRuleUtils.formatdates(WHOObs.getObsDatetime());
				String stageDateCombined3 = stage3 + "-" + stageDate3;
				Result WHOStage3AdultResult = new Result(stageDateCombined3);
				result.add(WHOStage3AdultResult);

			} else if (WHOObs.getValueCoded().equals(Encephalopathy)
					|| WHOObs.getValueCoded().equals(HivStagingCandidiasisOroresperatoryTract)
					|| WHOObs.getValueCoded().equals(HivStagingCryptococcosisExtraPulmonary)
					|| WHOObs.getValueCoded().equals(HivStagingCryptospoidiosis)
					|| WHOObs.getValueCoded().equals(HivStagingCytomegalovirusDisease)
					|| WHOObs.getValueCoded().equals(HivStagingDisseminatedEndemicMycosis)
					|| WHOObs.getValueCoded().equals(HivStagingLymphoma)
					|| WHOObs.getValueCoded().equals(HivStagingMucocutaneousHerpesSimplex)
					|| WHOObs.getValueCoded().equals(HivStagingMycobacteriumOther)
					|| WHOObs.getValueCoded().equals(HivStagingSalmonellaSepticemia)
					|| WHOObs.getValueCoded().equals(KaposiSarcoma)
					|| WHOObs.getValueCoded().equals(MycobacteriumTuberculosisExtrapulmonary)
					|| WHOObs.getValueCoded().equals(PneumocysticCariniiPneumonia)
					|| WHOObs.getValueCoded().equals(ProgressiveMultifocalLeukoencephalopathy)
					|| WHOObs.getValueCoded().equals(WastingSyndrome)
					|| WHOObs.getValueCoded().equals(ToxoplasmosisCentralNervousSystem)
					|| WHOObs.getValueCoded().equals(ConfirmedHivStagingHivWastingSyndrome)
					|| WHOObs.getValueCoded().equals(ConfirmedHivStagingPneumocysticPneumonia)
					|| WHOObs.getValueCoded().equals(ConfirmedHivStagingRecurrentSevereBacterialPneumoia)
					|| WHOObs.getValueCoded().equals(ConfirmedHivStagingChronicHerpesSimplex)
					|| WHOObs.getValueCoded().equals(ConfirmedHivStagingCandidiasis)
					|| WHOObs.getValueCoded().equals(ConfirmedHivStagingExtrapulmonaryTuberculosis)
					|| WHOObs.getValueCoded().equals(ConfirmedHivStagingKaposiSarcomaKs)
					|| WHOObs.getValueCoded().equals(ConfirmedHivStagingCytomegalovirusDisease)
					|| WHOObs.getValueCoded().equals(ConfirmedHivStagingToxoplasmosisCns)
					|| WHOObs.getValueCoded().equals(ConfirmedHivStagingHivEncephalopathy)
					|| WHOObs.getValueCoded().equals(ConfirmedHivStagingCryptococcossosExtraPulmonary)
					|| WHOObs.getValueCoded().equals(ConfirmedHivStagingDisseminatedNonTuberculosisMyobacterialInfection)
					|| WHOObs.getValueCoded().equals(ConfirmedHivStagingProgressiveMultifocalLeukoencephalopathy)
					|| WHOObs.getValueCoded().equals(ConfirmedHivStagingChronicCryptosporidiosis)
					|| WHOObs.getValueCoded().equals(ConfirmedHivStagingChronicIsosporiasis)
					|| WHOObs.getValueCoded().equals(ConfirmedHivStagingRecurrentSepticemia)
					|| WHOObs.getValueCoded().equals(ConfirmedHivStagingLymphoma)
					|| WHOObs.getValueCoded().equals(ConfirmedHivStagingInvasiveCervicalCarcinoma)
					|| WHOObs.getValueCoded().equals(ConfirmedHivStagingAtypicalDisseminatedLeishmaniasis)
					|| WHOObs.getValueCoded().equals(ConfirmedHivStagingSymptomaticHivAssociatedNephoropathy)

					) {

				log.info("Entering stage 4 for determination adult");
				String stage4 = "WHO STAGE 4 ADULT";
				String stageDate4 = MohRuleUtils.formatdates(WHOObs.getObsDatetime());
				String stageDateCombined4 = stage4 + "-" + stageDate4;
				Result WHOStage4AdultResult = new Result(stageDateCombined4);
				result.add(WHOStage4AdultResult);
			} else if (WHOObs.getValueCoded().equals(AsymptomaticHivInfection)
					|| WHOObs.getValueCoded().equals(Hepatpsplenomegaly)
					|| WHOObs.getValueCoded().equals(HivStagingPersistentGenerelizedLymphadenopathy)
					) {
				log.info("Entering stage 1 for determination paeds");
				String stagePaeds1 = "WHO STAGE 1 PEDS";
				String stageDatePaeds1 = MohRuleUtils.formatdates(WHOObs.getObsDatetime());
				String stageDateCombinedPaeds1 = stagePaeds1 + "-" + stageDatePaeds1;
				Result WHOStage1PaedsResult = new Result(stageDateCombinedPaeds1);
				result.add(WHOStage1PaedsResult);

			} else if (WHOObs.getValueCoded().equals(Dermatitis)
					|| WHOObs.getValueCoded().equals(HerpesZoster)
					|| WHOObs.getValueCoded().equals(HivStagingHsvStomatitis)
					|| WHOObs.getValueCoded().equals(HivStagingRecurrentUpperRespiratoryInfection)
					|| WHOObs.getValueCoded().equals(HivStagingSteroidResistantThrombocytopenia)
					|| WHOObs.getValueCoded().equals(HumanPapillomavirus)
					|| WHOObs.getValueCoded().equals(MolluscumContagiosum)
					|| WHOObs.getValueCoded().equals(OtitisMedia)
					|| WHOObs.getValueCoded().equals(ParotidEnlargement)
					|| WHOObs.getValueCoded().equals(VerrucaPlanus)
					|| WHOObs.getValueCoded().equals(Hepatpsplenomegaly)
					|| WHOObs.getValueCoded().equals(RecurrentOralUlceration)
					|| WHOObs.getValueCoded().equals(PapularPruriticEruption)
					|| WHOObs.getValueCoded().equals(FungalNailInfection)
					|| WHOObs.getValueCoded().equals(LinearGingivalErythema)
					|| WHOObs.getValueCoded().equals(WartsGenital)
					|| WHOObs.getValueCoded().equals(ChronicUpperRespiratoryTractInfections)
					) {
				log.info("Entering stage 2 for determination paeds");
				String stagePaeds2 = "WHO STAGE 2 PEDS";
				String stageDatePaeds2 = MohRuleUtils.formatdates(WHOObs.getObsDatetime());
				String stageDateCombinedPaeds2 = stagePaeds2 + "-" + stageDatePaeds2;
				Result WHOStage2PaedsResult = new Result(stageDateCombinedPaeds2);
				result.add(WHOStage2PaedsResult);

			} else if (WHOObs.getValueCoded().equals(DiarrheaChronic)
					|| WHOObs.getValueCoded().equals(HivStagingCandidiasisOroresperatoryTract)
					|| WHOObs.getValueCoded().equals(FailureToThrive)
					|| WHOObs.getValueCoded().equals(HivStagingLymphoidInterstitialPneumonia)
					|| WHOObs.getValueCoded().equals(HivStagingNonResponsiveHerpesSimplexVirus)
					|| WHOObs.getValueCoded().equals(HivStagingPedsHerpesZoster)
					|| WHOObs.getValueCoded().equals(HivStagingRecurrentBActerialPneumonia)
					|| WHOObs.getValueCoded().equals(HivStagingRefractoryAnemia)
					|| WHOObs.getValueCoded().equals(HivStagingVaricellaDisseminated)
					|| WHOObs.getValueCoded().equals(PneumoniaTuberculosis)
					|| WHOObs.getValueCoded().equals(RectovaginalFistula)
					|| WHOObs.getValueCoded().equals(HivStagingModerateMalnutrition)
					|| WHOObs.getValueCoded().equals(HivStagingPersistentFever)
					|| WHOObs.getValueCoded().equals(CandidiasisOral)
					|| WHOObs.getValueCoded().equals(OralHairyLeukoplakia)
					|| WHOObs.getValueCoded().equals(LymphNodeTuberculosis)
					|| WHOObs.getValueCoded().equals(HivStagingChronicHivAssociatedLungDisease)
					|| WHOObs.getValueCoded().equals(HivStagingUnexplainedAnemiaNeutropenia)
					|| WHOObs.getValueCoded().equals(HivStagingAcuteNecrotizingUlcerativeGingitivitisPeriodontis)
					) {
				log.info("Entering stage 3 for determination paeds");
				String stagePaeds3 = "WHO STAGE 3 PEDS";
				String stageDatePaeds3 = MohRuleUtils.formatdates(WHOObs.getObsDatetime());
				String stageDateCombinedPaeds3 = stagePaeds3 + "-" + stageDatePaeds3;
				Result WHOStage3PaedsResult = new Result(stageDateCombinedPaeds3);
				result.add(WHOStage3PaedsResult);
			} else if (WHOObs.getValueCoded().equals(CARDIOMYOPATHY)
					|| WHOObs.getValueCoded().equals(Candidiasis)
					|| WHOObs.getValueCoded().equals(Cryptococcosis)
					|| WHOObs.getValueCoded().equals(Encephalopathy)
					|| WHOObs.getValueCoded().equals(HivStagingCoccidiodomycosisDisseminated)
					|| WHOObs.getValueCoded().equals(HivStagingCryptococcosisExtraPulmonary)
					|| WHOObs.getValueCoded().equals(HivStagingCryptospoidiosis)
					|| WHOObs.getValueCoded().equals(HivStagingCytomegalovirusDisease)
					|| WHOObs.getValueCoded().equals(HivStagingHistoplasmosisDisseminated)
					|| WHOObs.getValueCoded().equals(HivStagingMycobacteriumOther)
					|| WHOObs.getValueCoded().equals(HivStagingNonResponsiveHerpesSimplexVirus)
					|| WHOObs.getValueCoded().equals(HivStagingSevereBacterialInfection)
					|| WHOObs.getValueCoded().equals(KaposiSarcoma)
					|| WHOObs.getValueCoded().equals(Nephropathy)
					|| WHOObs.getValueCoded().equals(PneumocysticCariniiPneumonia)
					|| WHOObs.getValueCoded().equals(ProgressiveMultifocalLeukoencephalopathy)
					|| WHOObs.getValueCoded().equals(MycobacteriumTuberculosisExtrapulmonary)
					|| WHOObs.getValueCoded().equals(HivStagingCerebralBCellNonHodgkinLymphoma)

					) {
				log.info("Entering stage 4 for determination paeds");
				String stagePaeds4 = "WHO STAGE 4 PEDS";
				String stageDatePaeds4 = MohRuleUtils.formatdates(WHOObs.getObsDatetime());
				String stageDateCombinedPaeds4 = stagePaeds4 + "-" + stageDatePaeds4;
				Result WHOStage4PaedsResult = new Result(stageDateCombinedPaeds4);
				result.add(WHOStage4PaedsResult);
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

	private List<Concept> getQuestionConcepts() {
		return Arrays.asList(
				new Concept[]{
						MohCacheUtils.getConcept(MohEvaluableNameConstants.ADULT_WHO_CONDITION_QUERY),
						MohCacheUtils.getConcept(MohEvaluableNameConstants.PEDS_WHO_SPECIFIC_CONDITION_QUERY)
				});
	}

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


}