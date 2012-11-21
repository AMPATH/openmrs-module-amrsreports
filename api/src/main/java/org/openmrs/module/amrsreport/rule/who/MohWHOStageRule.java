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

		Concept HivStagingAdultHerpesZoster = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_ADULT_HERPES_ZOSTER);
		Concept HivStagingMinorMucocutaneousManifestations = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_MINOR_MUCOCUTANEOUS_MANIFESTATIONS);
		Concept HivStagingWeightLossLessThanTenPercent = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_WEIGHT_LOSS_LESS_THAN_TEN_PERCENT);
		Concept AngularCheilitis = MohCacheUtils.getConcept(MohEvaluableNameConstants.ANGULAR_CHEILITIS);
		Concept SeborrheicDermatitis = MohCacheUtils.getConcept(MohEvaluableNameConstants.SEBORRHEIC_DERMATITIS);
		Concept HivStagingSeriousBacterialInfections = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_SERIOUS_BACTERIAL_INFECTIONS);
		Concept HivStagingTuberculosisWithinYear = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_TUBERCULOSIS_WITHIN_YEAR);
		Concept HivStagingWeightLossGreaterThanTenPercent = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_WEIGHT_LOSS_GREATER_THAN_TEN_PERCENT);
		Concept ConfirmedHivStagingWeightLossGreaterThanTenPercent = MohCacheUtils.getConcept(MohEvaluableNameConstants.CONFIRMED_HIV_STAGING_WEIGHT_LOSS_GREATER_THAN_TEN_PERCENT);
		Concept ConfirmedHivStagingDiarrheaChronic = MohCacheUtils.getConcept(MohEvaluableNameConstants.CONFIRMED_HIV_STAGING_DIARRHEA_CHRONIC);
		Concept ConfirmedHivStagingPersistentCandidiasisOral = MohCacheUtils.getConcept(MohEvaluableNameConstants.CONFIRMED_HIV_STAGING_PERSISTENT_CANDIDIASIS_ORAL);
		Concept ConfirmedHivStagingPersistentFever = MohCacheUtils.getConcept(MohEvaluableNameConstants.CONFIRMED_HIV_STAGING_PERSISTENT_FEVER);
		Concept ConfirmedWhoStagingOralHairyLeukoplakia = MohCacheUtils.getConcept(MohEvaluableNameConstants.CONFIRMED_WHO_STAGING_ORAL_HAIRY_LEUKOPLAKIA);
		Concept ConfirmedHivStagingTuberculosisPulmonary = MohCacheUtils.getConcept(MohEvaluableNameConstants.CONFIRMED_HIV_STAGING_TUBERCULOSIS_PULMONARY);
		Concept ConfirmedHivStagingSevereBacterialInfections = MohCacheUtils.getConcept(MohEvaluableNameConstants.CONFIRMED_HIV_STAGING_SEVERE_BACTERIAL_INFECTIONS);
		Concept ConfirmedHivStagingUnexplainedAnemiaNeutripaenia = MohCacheUtils.getConcept(MohEvaluableNameConstants.CONFIRMED_HIV_STAGING_UNEXPLAINED_ANAEMIA_NEUTROPAENIA);
		Concept ConfirmedHivStagingAcuteNecrotizingStomatitisGingitivitis = MohCacheUtils.getConcept(MohEvaluableNameConstants.CONFIRMED_HIV_STAGING_ACUTE_NECROTIZING_STOMATITIS_GINGIVITIS);
		Concept HivStagingDisseminatedEndemicMycosis = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_DISSEMINATED_ENDEMIC_MYCOSIS);
		Concept HivStagingLymphoma = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_LYMPHOMA);
		Concept HivStagingMucocutaneousHerpesSimplex = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_MUCOCUTANEOUS_HERPES_SIMPLEX);
		Concept HivStagingSalmonellaSepticemia = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_SALMONELLA_SEPTICEMIA);
		Concept KaposiSarcoma = MohCacheUtils.getConcept(MohEvaluableNameConstants.KAPOSIS_SARCOMA);
		Concept ToxoplasmosisCentralNervousSystem = MohCacheUtils.getConcept(MohEvaluableNameConstants.TOXOPLASMOSIS_CENTRAL_NERVOUS_SYSTEM);
		Concept ConfirmedHivStagingHivWastingSyndrome = MohCacheUtils.getConcept(MohEvaluableNameConstants.CONFIRMED_HIV_STAGING_HIV_WASTING_SYNDROME);
		Concept ConfirmedHivStagingPneumocysticPneumonia = MohCacheUtils.getConcept(MohEvaluableNameConstants.CONFIRMED_HIV_STAGING_PNEUMOCYSTIC_PNEUMONIA);
		Concept ConfirmedHivStagingRecurrentSevereBacterialPneumoia = MohCacheUtils.getConcept(MohEvaluableNameConstants.CONFIRMED_HIV_STAGING_RECURRENT_SEVERE_BACTERIAL_PNEUMONIA);
		Concept ConfirmedHivStagingChronicHerpesSimplex = MohCacheUtils.getConcept(MohEvaluableNameConstants.CONFIRMED_HIV_STAGING_CHRONIC_HERPES_SIMPLEX);
		Concept ConfirmedHivStagingCandidiasis = MohCacheUtils.getConcept(MohEvaluableNameConstants.CONFIRMED_HIV_STAGING_CANDIDIASIS);
		Concept ConfirmedHivStagingExtrapulmonaryTuberculosis = MohCacheUtils.getConcept(MohEvaluableNameConstants.CONFIRMED_HIV_STAGING_EXTRAPULMONARY_TUBERCULOSIS);
		Concept ConfirmedHivStagingKaposiSarcomaKs = MohCacheUtils.getConcept(MohEvaluableNameConstants.CONFIRMED_HIV_STAGING_KAPOSIS_SARCOMA_KS);
		Concept ConfirmedHivStagingCytomegalovirusDisease = MohCacheUtils.getConcept(MohEvaluableNameConstants.CONFIRMED_HIV_STAGING_CYTOMEGALOVIRUS_DISEASE);
		Concept ConfirmedHivStagingToxoplasmosisCns = MohCacheUtils.getConcept(MohEvaluableNameConstants.CONFIRMED_HIV_STAGING_TOXOPLASMOSIS_CNS);
		Concept ConfirmedHivStagingHivEncephalopathy = MohCacheUtils.getConcept(MohEvaluableNameConstants.CONFIRMED_HIV_STAGING_HIV_ENCEPHALOPATHY);
		Concept ConfirmedHivStagingCryptococcossosExtraPulmonary = MohCacheUtils.getConcept(MohEvaluableNameConstants.CONFIRMED_HIV_STAGING_CRYPTOCOCCOSIS_EXTRAPULMONARY);
		Concept ConfirmedHivStagingDisseminatedNonTuberculosisMyobacterialInfection = MohCacheUtils.getConcept(MohEvaluableNameConstants.CONFIRMED_HIV_STAGING_DISSEMINATED_NON_TUBERCULOSIS_MYCOBACTERIAL_INFECTION);
		Concept ConfirmedHivStagingProgressiveMultifocalLeukoencephalopathy = MohCacheUtils.getConcept(MohEvaluableNameConstants.CONFIRMED_HIV_STAGING_PROGRESSIVE_MULTIFOCAL_LEUKOENCEPHALOPATHY);
		Concept ConfirmedHivStagingChronicCryptosporidiosis = MohCacheUtils.getConcept(MohEvaluableNameConstants.CONFIRMED_HIV_STAGING_CHRONIC_CRYPTOSPORIDIOSIS);
		Concept ConfirmedHivStagingChronicIsosporiasis = MohCacheUtils.getConcept(MohEvaluableNameConstants.CONFIRMED_HIV_STAGING_CHRONIC_ISOSPORIASIS);
//		Concept ConfirmedHivStagingDisseminatedMycosis = MohCacheUtils.getConcept(MohEvaluableNameConstants.CONFIRMED_HIV_STAGING_DISSEMINATED_MYCOSIS);
		Concept ConfirmedHivStagingRecurrentSepticemia = MohCacheUtils.getConcept(MohEvaluableNameConstants.CONFIRMED_HIV_STAGING_RECURRENT_SEPTICEMIA);
		Concept ConfirmedHivStagingLymphoma = MohCacheUtils.getConcept(MohEvaluableNameConstants.CONFIRMED_HIV_STAGING_LYMPHOMA);
		Concept ConfirmedHivStagingInvasiveCervicalCarcinoma = MohCacheUtils.getConcept(MohEvaluableNameConstants.CONFIRMED_HIV_STAGING_INVASIVE_CERVICAL_CARCINOMA);
		Concept ConfirmedHivStagingAtypicalDisseminatedLeishmaniasis = MohCacheUtils.getConcept(MohEvaluableNameConstants.CONFIRMED_HIV_STAGING_ATYPICAL_DISSEMINATED_LEISHMANIASIS);
		Concept ConfirmedHivStagingSymptomaticHivAssociatedNephoropathy = MohCacheUtils.getConcept(MohEvaluableNameConstants.CONFIRMED_HIV_STAGING_SYMPTOMATIC_HIV_ASSOCIATED_NEPHROPATHY);
		Concept AsymptomaticHivInfection = MohCacheUtils.getConcept(MohEvaluableNameConstants.ASYMPTOMATIC_HIV_INFECTION);
		Concept HivStagingPersistentGenerelizedLymphadenopathy = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_PERSISTENT_GENERALIZED_LYMPHADENOPATHY);
		Concept Dermatitis = MohCacheUtils.getConcept(MohEvaluableNameConstants.DERMATITIS);
		Concept HerpesZoster = MohCacheUtils.getConcept(MohEvaluableNameConstants.HERPES_ZOSTER);
		Concept HivStagingHsvStomatitis = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_HSV_STOMATITIS);
		Concept HivStagingRecurrentUpperRespiratoryInfection = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_RECURRENT_UPPER_RESPIRATORY_INFECTION);
		Concept HivStagingSteroidResistantThrombocytopenia = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_STEROID_RESISTANT_THROMBOCYTOPENIA);
		Concept HumanPapillomavirus = MohCacheUtils.getConcept(MohEvaluableNameConstants.HUMAN_PAPILLOMAVIRUS);
		Concept MolluscumContagiosum = MohCacheUtils.getConcept(MohEvaluableNameConstants.MOLLUSCUM_CONTAGIOSUM);
		Concept ParotidEnlargement = MohCacheUtils.getConcept(MohEvaluableNameConstants.PAROTID_ENLARGEMENT);
		Concept VerrucaPlanus = MohCacheUtils.getConcept(MohEvaluableNameConstants.VERRUCA_PLANUS);
		Concept RecurrentOralUlceration = MohCacheUtils.getConcept(MohEvaluableNameConstants.RECURRENT_ORAL_ULCERATION);
		Concept PapularPruriticEruption = MohCacheUtils.getConcept(MohEvaluableNameConstants.PAPULAR_PRURITIC_ERUPTION);
		Concept FungalNailInfection = MohCacheUtils.getConcept(MohEvaluableNameConstants.FUNGAL_NAIL_INFECTIONS);
		Concept LinearGingivalErythema = MohCacheUtils.getConcept(MohEvaluableNameConstants.LINEAR_GINGIVAL_ERYTHEMA);
		Concept WartsGenital = MohCacheUtils.getConcept(MohEvaluableNameConstants.WARTS_GENITAL);
		Concept ChronicUpperRespiratoryTractInfections = MohCacheUtils.getConcept(MohEvaluableNameConstants.CHRONIC_UPPER_RESPIRATORY_TRACT_INFECTIONS);
		Concept Hepatpsplenomegaly = MohCacheUtils.getConcept(MohEvaluableNameConstants.HEPATOSPLENOMEGALY);
		Concept DiarrheaChronic = MohCacheUtils.getConcept(MohEvaluableNameConstants.DIARRHEA_CHRONIC);
		Concept FailureToThrive = MohCacheUtils.getConcept(MohEvaluableNameConstants.FAILURE_TO_THRIVE);
		Concept HivStagingCandidiasisOroresperatoryTract = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_CANDIDIASIS_ORORESPIRATORY_TRACT);
		Concept HivStagingLymphoidInterstitialPneumonia = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_LYMPHOID_INTERSTITIAL_PNEUMONIA);
		Concept HivStagingPedsHerpesZoster = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_PEDS_HERPES_ZOSTER);
		Concept HivStagingRecurrentBActerialPneumonia = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_RECURRENT_BACTERIAL_PNEUMONIA);
		Concept HivStagingRefractoryAnemia = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_REFRACTORY_ANEMIA);
		Concept HivStagingVaricellaDisseminated = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_VARICELLA_DISSEMINATED);
		Concept PneumoniaTuberculosis = MohCacheUtils.getConcept(MohEvaluableNameConstants.PNEUMONIA_TUBERCULOUS);
		Concept RectovaginalFistula = MohCacheUtils.getConcept(MohEvaluableNameConstants.RECTOVAGINAL_FISTULA);
		Concept HivStagingModerateMalnutrition = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_MODERATE_MALNUTRITION);
		Concept HivStagingPersistentFever = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_PERSISTENT_FEVER);
		Concept CandidiasisOral = MohCacheUtils.getConcept(MohEvaluableNameConstants.CANDIDIASIS_ORAL);
		Concept OralHairyLeukoplakia = MohCacheUtils.getConcept(MohEvaluableNameConstants.ORAL_HAIRY_LEUKOPLAKIA);
		Concept LymphNodeTuberculosis = MohCacheUtils.getConcept(MohEvaluableNameConstants.LYMPH_NODE_TUBERCULOSIS);
		Concept HivStagingChronicHivAssociatedLungDisease = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_CHRONIC_HIV_ASSOCIATED_LUNG_DISEASE);
		Concept HivStagingUnexplainedAnemiaNeutropenia = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_UNEXAPLINED_ANEMIA_NEUTROPENIA);
		Concept HivStagingAcuteNecrotizingUlcerativeGingitivitisPeriodontis = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_ACUTE_NECROTIZING_ULCERATIVE_GINGIVITIS_PERIODONTIS);
		Concept Candidiasis = MohCacheUtils.getConcept(MohEvaluableNameConstants.CANDIDIASIS);
		Concept CARDIOMYOPATHY = MohCacheUtils.getConcept(MohEvaluableNameConstants.CARDIOMYOPATHY);
		Concept Cryptococcosis = MohCacheUtils.getConcept(MohEvaluableNameConstants.CRYPTOCOCCOSIS);
		Concept Encephalopathy = MohCacheUtils.getConcept(MohEvaluableNameConstants.ENCEPHALOPATHY);
		Concept HivStagingCoccidiodomycosisDisseminated = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_COCCIDIODOMYCOSIS_DISSEMINATED);
		Concept HivStagingCryptococcosisExtraPulmonary = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_CRYPTOCOCCOSIS_EXTRAPULMONARY);
		Concept HivStagingCryptospoidiosis = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_CRYPTOSPORIDIOSIS);
		Concept HivStagingCytomegalovirusDisease = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_CYTOMEGALOVIRUS_DISEASE);
		Concept HivStagingHistoplasmosisDisseminated = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_HISTOPLASMOSIS_DISSEMINATED);
		Concept HivStagingMycobacteriumOther = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_MYCOBACTERIUM_OTHER);
		Concept HivStagingNonResponsiveHerpesSimplexVirus = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_NONRESPONSIVE_HERPES_SIMPLEX_VIRUS);
		Concept HivStagingSevereBacterialInfection = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_SEVERE_BACTERIAL_INFECTION);
		Concept Nephropathy = MohCacheUtils.getConcept(MohEvaluableNameConstants.NEPHROPATHY);
		Concept PneumocysticCariniiPneumonia = MohCacheUtils.getConcept(MohEvaluableNameConstants.PNEUMOCYSTIC_CARINII_PNEUMONIA);
		Concept ProgressiveMultifocalLeukoencephalopathy = MohCacheUtils.getConcept(MohEvaluableNameConstants.PROGRESSIVE_MULTIFOCAL_LEUKOENCEPHALOPATHY);
//		Concept Toxoplasmosis = MohCacheUtils.getConcept(MohEvaluableNameConstants.TOXOPLASMOSIS);
		Concept WastingSyndrome = MohCacheUtils.getConcept(MohEvaluableNameConstants.WASTING_SYNDROME);
//		Concept CandidiasisOesophageal = MohCacheUtils.getConcept(MohEvaluableNameConstants.CANDIDIASIS_OESOPHAGEAL);
		Concept MycobacteriumTuberculosisExtrapulmonary = MohCacheUtils.getConcept(MohEvaluableNameConstants.MYCOBACTERIUM_TUBERCULOSIS_EXTRAPULMONARY);
//		Concept HivStagingChronicIsisporiasis = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_CHRONIC_ISOSPORIASIS);
		Concept HivStagingCerebralBCellNonHodgkinLymphoma = MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_STAGING_CEREBRAL_B_CELL_NON_HODGKIN_LYMPHOMA);
		Concept PresumptiveHivStagingTuberculosisPulmorary = MohCacheUtils.getConcept(MohEvaluableNameConstants.PRESUMPTIVE_HIV_STAGING_TUBERCULOSIS_PULMONARY);
		Concept OtitisMedia = MohCacheUtils.getConcept(MohEvaluableNameConstants.OTITIS_MEDIA);
//		Concept ADULTWHOCONDITIONQUERY = MohCacheUtils.getConcept(MohEvaluableNameConstants.ADULT_WHO_CONDITION_QUERY);
//		Concept PaedsWhoSpecificQuery = MohCacheUtils.getConcept(MohEvaluableNameConstants.PEDS_WHO_SPECIFIC_CONDITION_QUERY);


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