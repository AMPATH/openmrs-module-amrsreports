package org.openmrs.module.amrsreports.rule.medication;

import org.junit.runner.RunWith;
import org.openmrs.api.context.Context;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * test file for {@link MohDateArtStartedRule}
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class MohDateArtStartedRuleTest {

//	private static final List<String> initConcepts = Arrays.asList(
//			MohEvaluableNameConstants.ANTIRETROVIRAL_DRUG_TREATMENT_START_DATE,
//			MohEvaluableNameConstants.ANTIRETROVIRAL_PLAN,
//			MohEvaluableNameConstants.REASON_ANTIRETROVIRALS_STARTED,
//			MohEvaluableNameConstants.PATIENT_REPORTED_REASON_FOR_CURRENT_ANTIRETROVIRALS_STARTED,
//			MohEvaluableNameConstants.NEWBORN_ANTIRETROVIRAL_USE,
//			MohEvaluableNameConstants.NEWBORN_PROPHYLACTIC_ANTIRETROVIRAL_USE,
//			MohEvaluableNameConstants.TOTAL_MATERNAL_TO_CHILD_TRANSMISSION_PROPHYLAXIS,
//			MohEvaluableNameConstants.PREVENTION_OF_MOTHER_TO_CHILD_TRANSMISSION_OF_HIV,
//			MohEvaluableNameConstants.STAVUDINE,
//			MohEvaluableNameConstants.LAMIVUDINE,
//			MohEvaluableNameConstants.NEVIRAPINE,
//			MohEvaluableNameConstants.NELFINAVIR,
//			"LOPINAVIR AND RITONAVIR",
//			MohEvaluableNameConstants.ZIDOVUDINE,
//			MohEvaluableNameConstants.OTHER_NON_CODED,
//			MohEvaluableNameConstants.START_DRUGS,
//			MohEvaluableNameConstants.CONTINUE_REGIMEN,
//			MohEvaluableNameConstants.CHANGE_FORMULATION,
//			MohEvaluableNameConstants.CHANGE_REGIMEN,
//			MohEvaluableNameConstants.REFILLED,
//			MohEvaluableNameConstants.NOT_REFILLED,
//			MohEvaluableNameConstants.DRUG_SUBSTITUTION,
//			MohEvaluableNameConstants.DRUG_RESTART,
//			MohEvaluableNameConstants.DOSING_CHANGE,
//			"TRUE"
//	);
//
//	private List<Concept> questionConcepts;
//
//	private static final int PATIENT_ID = 5;
//
//	private ConceptService conceptService;
//	private MohCoreService mohCoreService;
//
//	private MohDateArtStartedRule rule;
//	private LogicContext logicContext;
//
//	private List<Obs> exclusionObs;
//	private List<Obs> inclusionObs;
//
//	@Before
//	public void setup() {
//
//		// initialize the current obs
//		exclusionObs = new ArrayList<Obs>();
//		inclusionObs = new ArrayList<Obs>();
//
//		// build the concept service
//		int i = 0;
//		conceptService = Mockito.mock(ConceptService.class);
//		for (String conceptName : initConcepts) {
//			Mockito.when(conceptService.getConcept(conceptName)).thenReturn(new Concept(i++));
//		}
//		Mockito.when(conceptService.getConcept((String) null)).thenReturn(null);
//
//		// build the MOH Core service
//		mohCoreService = Mockito.mock(MohCoreService.class);
//
//		// set up Context
//		PowerMockito.mockStatic(Context.class);
//		Mockito.when(Context.getConceptService()).thenReturn(conceptService);
//		Mockito.when(Context.getService(MohCoreService.class)).thenReturn(mohCoreService);
//
//		Map<String, Collection<OpenmrsObject>> exclusions = new HashMap<String, Collection<OpenmrsObject>>();
//		exclusions.put("concept", MohDateArtStartedRule.excludeQuestions);
//		Mockito.when(mohCoreService.getPatientObservations(Mockito.eq(PATIENT_ID), Mockito.eq(exclusions),
//				Mockito.any(MohFetchRestriction.class), Mockito.any(Date.class))).thenReturn(exclusionObs);
//
//		Map<String, Collection<OpenmrsObject>> questions = new HashMap<String, Collection<OpenmrsObject>>();
//		questions.put("concept", MohDateArtStartedRule.questionConcepts);
//		Mockito.when(mohCoreService.getPatientObservations(Mockito.eq(PATIENT_ID), Mockito.eq(questions),
//				Mockito.any(MohFetchRestriction.class), Mockito.any(Date.class))).thenReturn(inclusionObs);
//
//		// set up question concepts
//		questionConcepts = Arrays.<Concept>asList(
//				conceptService.getConcept(MohEvaluableNameConstants.ANTIRETROVIRAL_DRUG_TREATMENT_START_DATE),
//				conceptService.getConcept(MohEvaluableNameConstants.ANTIRETROVIRAL_PLAN)
//		);
//
//		// create a rule instance
//		rule = new MohDateArtStartedRule();
//
//		// initialize logic context
//		logicContext = Mockito.mock(LogicContext.class);
//		Mockito.when(logicContext.getIndexDate()).thenReturn(new Date());
//	}
//
//	/**
//	 * generate a date from a string
//	 *
//	 * @param date
//	 * @return
//	 */
//	private Date makeDate(String date) {
//		try {
//			return new SimpleDateFormat("d MMM yyyy", Locale.ENGLISH).parse(date);
//		} catch (Exception e) {
//			// pass
//		}
//		return new Date();
//	}
//
//	/**
//	 * adds an observation with the given date as the obs datetime
//	 *
//	 * @param conceptName
//	 * @param date
//	 */
//	private void addObs(String concept, String answer, String date) {
//		Obs obs = new Obs();
//		obs.setConcept(conceptService.getConcept(concept));
//		obs.setValueCoded(conceptService.getConcept(answer));
//		obs.setObsDatetime(makeDate(date));
//
//		if (OpenmrsUtil.isConceptInList(obs.getConcept(), questionConcepts))
//			inclusionObs.add(obs);
//		else
//			exclusionObs.add(obs);
//	}
//
//	/**
//	 * adds an observation with a value date and the given obs datetime
//	 *
//	 * @param concept
//	 * @param valueDate
//	 * @param obsDate
//	 */
//	private void addDateObs(String concept, String valueDate, String obsDate) {
//		Obs obs = new Obs();
//		obs.setConcept(conceptService.getConcept(concept));
//		obs.setValueDatetime(makeDate(valueDate));
//		obs.setObsDatetime(makeDate(obsDate));
//
//		if (OpenmrsUtil.isConceptInList(obs.getConcept(), questionConcepts))
//			inclusionObs.add(obs);
//		else
//			exclusionObs.add(obs);
//	}
//
//	/**
//	 * @verifies return first obs date for ANTIRETROVIRAL_PLAN in allowed answers
//	 * @see MohDateArtStartedRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
//	 */
//	@Test
//	public void evaluate_shouldReturnFirstObsDateForANTIRETROVIRAL_PLANInAllowedAnswers() throws Exception {
//		addObs(MohEvaluableNameConstants.ANTIRETROVIRAL_PLAN, MohEvaluableNameConstants.START_DRUGS, "16 Oct 1975");
//		assertThat(rule.evaluate(logicContext, PATIENT_ID, null), is(new Result("16/10/1975")));
//
//		inclusionObs.clear();
//
//		addObs(MohEvaluableNameConstants.ANTIRETROVIRAL_PLAN, MohEvaluableNameConstants.CONTINUE_REGIMEN, "17 Oct 1975");
//		assertThat(rule.evaluate(logicContext, PATIENT_ID, null), is(new Result("17/10/1975")));
//
//		inclusionObs.clear();
//
//		addObs(MohEvaluableNameConstants.ANTIRETROVIRAL_PLAN, MohEvaluableNameConstants.CHANGE_FORMULATION, "18 Oct 1975");
//		assertThat(rule.evaluate(logicContext, PATIENT_ID, null), is(new Result("18/10/1975")));
//
//		inclusionObs.clear();
//
//		addObs(MohEvaluableNameConstants.ANTIRETROVIRAL_PLAN, MohEvaluableNameConstants.CHANGE_REGIMEN, "19 Oct 1975");
//		assertThat(rule.evaluate(logicContext, PATIENT_ID, null), is(new Result("19/10/1975")));
//
//		inclusionObs.clear();
//
//		addObs(MohEvaluableNameConstants.ANTIRETROVIRAL_PLAN, MohEvaluableNameConstants.REFILLED, "20 Oct 1975");
//		assertThat(rule.evaluate(logicContext, PATIENT_ID, null), is(new Result("20/10/1975")));
//
//		inclusionObs.clear();
//
//		addObs(MohEvaluableNameConstants.ANTIRETROVIRAL_PLAN, MohEvaluableNameConstants.NOT_REFILLED, "21 Oct 1975");
//		assertThat(rule.evaluate(logicContext, PATIENT_ID, null), is(new Result("21/10/1975")));
//
//		inclusionObs.clear();
//
//		addObs(MohEvaluableNameConstants.ANTIRETROVIRAL_PLAN, MohEvaluableNameConstants.DRUG_SUBSTITUTION, "22 Oct 1975");
//		assertThat(rule.evaluate(logicContext, PATIENT_ID, null), is(new Result("22/10/1975")));
//
//		inclusionObs.clear();
//
//		addObs(MohEvaluableNameConstants.ANTIRETROVIRAL_PLAN, MohEvaluableNameConstants.DRUG_RESTART, "23 Oct 1975");
//		assertThat(rule.evaluate(logicContext, PATIENT_ID, null), is(new Result("23/10/1975")));
//
//		inclusionObs.clear();
//
//		addObs(MohEvaluableNameConstants.ANTIRETROVIRAL_PLAN, MohEvaluableNameConstants.DOSING_CHANGE, "24 Oct 1975");
//		assertThat(rule.evaluate(logicContext, PATIENT_ID, null), is(new Result("24/10/1975")));
//	}
//
//	/**
//	 * @verifies return first value date for ANTIRETROVIRAL_DRUG_TREATMENT_START_DATE
//	 * @see MohDateArtStartedRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
//	 */
//	@Test
//	public void evaluate_shouldReturnFirstValueDateForANTIRETROVIRAL_DRUG_TREATMENT_START_DATE() throws Exception {
//		addDateObs(MohEvaluableNameConstants.ANTIRETROVIRAL_DRUG_TREATMENT_START_DATE, "17 Oct 1975", "16 Oct 1975");
//		assertThat(rule.evaluate(logicContext, PATIENT_ID, null), is(new Result("17/10/1975")));
//	}
//
//	/**
//	 * @verifies return first date for mixed valid observations
//	 * @see MohDateArtStartedRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
//	 */
//	@Test
//	public void evaluate_shouldReturnFirstDateForMixedValidObservations() throws Exception {
//		addDateObs(MohEvaluableNameConstants.ANTIRETROVIRAL_DRUG_TREATMENT_START_DATE, "17 Oct 1975", "16 Oct 1975");
//		addObs(MohEvaluableNameConstants.ANTIRETROVIRAL_PLAN, MohEvaluableNameConstants.START_DRUGS, "18 Oct 1975");
//		assertThat(rule.evaluate(logicContext, PATIENT_ID, null), is(new Result("17/10/1975")));
//	}
//
//	/**
//	 * @verifies exclude if REASON_ANTIRETROVIRALS_STARTED is in excluded reasons
//	 * @see MohDateArtStartedRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
//	 */
//	@Test
//	public void evaluate_shouldExcludeIfREASON_ANTIRETROVIRALS_STARTEDIsInExcludedReasons() throws Exception {
//		addObs(MohEvaluableNameConstants.REASON_ANTIRETROVIRALS_STARTED,
//				MohEvaluableNameConstants.TOTAL_MATERNAL_TO_CHILD_TRANSMISSION_PROPHYLAXIS, "16 Oct 1975");
//		assertThat(rule.evaluate(logicContext, PATIENT_ID, null), is(new Result("Excluded")));
//
//		exclusionObs.clear();
//
//		addObs(MohEvaluableNameConstants.REASON_ANTIRETROVIRALS_STARTED,
//				MohEvaluableNameConstants.PREVENTION_OF_MOTHER_TO_CHILD_TRANSMISSION_OF_HIV, "16 Oct 1975");
//		assertThat(rule.evaluate(logicContext, PATIENT_ID, null), is(new Result("Excluded")));
//
//	}
//
//	/**
//	 * @verifies exclude if PATIENT_REPORTED_REASON_FOR_CURRENT_ANTIRETROVIRALS_STARTED is PREVENTION_OF_MOTHER_TO_CHILD_TRANSMISSION_OF_HIV
//	 * @see MohDateArtStartedRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
//	 */
//	@Test
//	public void evaluate_shouldExcludeIfPATIENT_REPORTED_REASON_FOR_CURRENT_ANTIRETROVIRALS_STARTEDIsPREVENTION_OF_MOTHER_TO_CHILD_TRANSMISSION_OF_HIV() throws Exception {
//		addObs(MohEvaluableNameConstants.PATIENT_REPORTED_REASON_FOR_CURRENT_ANTIRETROVIRALS_STARTED,
//				MohEvaluableNameConstants.PREVENTION_OF_MOTHER_TO_CHILD_TRANSMISSION_OF_HIV, "16 Oct 1975");
//		assertThat(rule.evaluate(logicContext, PATIENT_ID, null), is(new Result("Excluded")));
//	}
//
//	/**
//	 * @verifies exclude if NEWBORN_PROPHYLACTIC_ANTIRETROVIRAL_USE is TRUE
//	 * @see MohDateArtStartedRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
//	 */
//	@Test
//	public void evaluate_shouldExcludeIfNEWBORN_PROPHYLACTIC_ANTIRETROVIRAL_USEIsTRUE() throws Exception {
//		addObs(MohEvaluableNameConstants.NEWBORN_PROPHYLACTIC_ANTIRETROVIRAL_USE, "TRUE", "16 Oct 1975");
//		assertThat(rule.evaluate(logicContext, PATIENT_ID, null), is(new Result("Excluded")));
//	}
//
//	/**
//	 * @verifies exclude if NEWBORN_ANTIRETROVIRAL_USE is in excluded newborn ARVs
//	 * @see MohDateArtStartedRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
//	 */
//	@Test
//	public void evaluate_shouldExcludeIfNEWBORN_ANTIRETROVIRAL_USEIsInExcludedNewbornARVs() throws Exception {
//		addObs(MohEvaluableNameConstants.NEWBORN_ANTIRETROVIRAL_USE, MohEvaluableNameConstants.STAVUDINE, "16 Oct 1975");
//		assertThat(rule.evaluate(logicContext, PATIENT_ID, null), is(new Result("Excluded")));
//
//		exclusionObs.clear();
//
//		addObs(MohEvaluableNameConstants.NEWBORN_ANTIRETROVIRAL_USE, MohEvaluableNameConstants.LAMIVUDINE, "17 Oct 1975");
//		assertThat(rule.evaluate(logicContext, PATIENT_ID, null), is(new Result("Excluded")));
//
//		exclusionObs.clear();
//
//		addObs(MohEvaluableNameConstants.NEWBORN_ANTIRETROVIRAL_USE, MohEvaluableNameConstants.NEVIRAPINE, "18 Oct 1975");
//		assertThat(rule.evaluate(logicContext, PATIENT_ID, null), is(new Result("Excluded")));
//
//		exclusionObs.clear();
//
//		addObs(MohEvaluableNameConstants.NEWBORN_ANTIRETROVIRAL_USE, MohEvaluableNameConstants.NELFINAVIR, "19 Oct 1975");
//		assertThat(rule.evaluate(logicContext, PATIENT_ID, null), is(new Result("Excluded")));
//
//		exclusionObs.clear();
//
//		addObs(MohEvaluableNameConstants.NEWBORN_ANTIRETROVIRAL_USE, "LOPINAVIR AND RITONAVIR", "20 Oct 1975");
//		assertThat(rule.evaluate(logicContext, PATIENT_ID, null), is(new Result("Excluded")));
//
//		exclusionObs.clear();
//
//		addObs(MohEvaluableNameConstants.NEWBORN_ANTIRETROVIRAL_USE, MohEvaluableNameConstants.ZIDOVUDINE, "21 Oct 1975");
//		assertThat(rule.evaluate(logicContext, PATIENT_ID, null), is(new Result("Excluded")));
//
//		exclusionObs.clear();
//
//		addObs(MohEvaluableNameConstants.NEWBORN_ANTIRETROVIRAL_USE, MohEvaluableNameConstants.OTHER_NON_CODED, "22 Oct 1975");
//		assertThat(rule.evaluate(logicContext, PATIENT_ID, null), is(new Result("Excluded")));
//	}
}
