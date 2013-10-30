package org.openmrs.module.amrsreports.rule.medication;

import org.junit.runner.RunWith;
import org.openmrs.api.context.Context;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * tests for {@link MohFluconazoleStartStopDateRule}
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class MohFluconazoleStartStopDateRuleTest {

//	private static final List<String> initConcepts = Arrays.asList(
//			MohEvaluableNameConstants.CRYPTOCOCCAL_TREATMENT_PLAN,
//			MohEvaluableNameConstants.CRYPTOCOSSUS_TREATMENT_STARTED,
//			MohEvaluableNameConstants.START_DRUGS,
//			MohEvaluableNameConstants.FLUCONAZOLE,
//			MohEvaluableNameConstants.STOP_ALL
//	);
//
//	private static final int PATIENT_ID = 5;
//
//	private ConceptService conceptService;
//	private MohCoreService mohCoreService;
//
//	private MohFluconazoleStartStopDateRule rule;
//	private LogicContext logicContext;
//
//	private List<Obs> currentStartObs;
//	private List<Obs> currentStopObs;
//
//	@Before
//	public void setup() {
//
//		// initialize the current obs
//		currentStartObs = new ArrayList<Obs>();
//		currentStopObs = new ArrayList<Obs>();
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
//		Map<String, Collection<OpenmrsObject>> startRestrictions = new HashMap<String, Collection<OpenmrsObject>>();
//		startRestrictions.put("concept", Arrays.<OpenmrsObject>asList(
//				conceptService.getConcept(MohEvaluableNameConstants.CRYPTOCOCCAL_TREATMENT_PLAN),
//				conceptService.getConcept(MohEvaluableNameConstants.CRYPTOCOSSUS_TREATMENT_STARTED)
//		));
//
//		Map<String, Collection<OpenmrsObject>> stopRestrictions = new HashMap<String, Collection<OpenmrsObject>>();
//		stopRestrictions.put("concept", Arrays.<OpenmrsObject>asList(
//				conceptService.getConcept(MohEvaluableNameConstants.CRYPTOCOCCAL_TREATMENT_PLAN)));
//
//		Mockito.when(mohCoreService.getPatientObservations(Mockito.eq(PATIENT_ID), Mockito.eq(startRestrictions),
//				Mockito.any(MohFetchRestriction.class), Mockito.any(Date.class))).thenReturn(currentStartObs);
//		Mockito.when(mohCoreService.getPatientObservations(Mockito.eq(PATIENT_ID), Mockito.eq(stopRestrictions),
//				Mockito.any(MohFetchRestriction.class), Mockito.any(Date.class))).thenReturn(currentStopObs);
//
//		// set up Context
//		PowerMockito.mockStatic(Context.class);
//		Mockito.when(Context.getConceptService()).thenReturn(conceptService);
//		Mockito.when(Context.getService(MohCoreService.class)).thenReturn(mohCoreService);
//
//		// create a rule instance
//		rule = new MohFluconazoleStartStopDateRule();
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
//	 * adds a start observation with the given date as the obs datetime
//	 *
//	 * @param conceptName
//	 * @param date
//	 */
//	private void addStartObs(String concept, String answer, String date) {
//		Obs obs = new Obs();
//		obs.setConcept(conceptService.getConcept(concept));
//		obs.setValueCoded(conceptService.getConcept(answer));
//		obs.setObsDatetime(makeDate(date));
//		currentStartObs.add(obs);
//	}
//
//	/**
//	 * adds a stop observation with the given date as the obs datetime
//	 *
//	 * @param conceptName
//	 * @param date
//	 */
//	private void addStopObs(String concept, String answer, String date) {
//		Obs obs = new Obs();
//		obs.setConcept(conceptService.getConcept(concept));
//		obs.setValueCoded(conceptService.getConcept(answer));
//		obs.setObsDatetime(makeDate(date));
//		currentStopObs.add(obs);
//	}
//
//	/**
//	 * @verifies start on CRYPTOCOCCAL_TREATMENT_PLAN is START_DRUGS
//	 * @see MohFluconazoleStartStopDateRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
//	 */
//	@Test
//	public void evaluate_shouldStartOnCRYPTOCOCCAL_TREATMENT_PLANIsSTART_DRUGS() throws Exception {
//		addStartObs(MohEvaluableNameConstants.CRYPTOCOCCAL_TREATMENT_PLAN, MohEvaluableNameConstants.START_DRUGS, "16 Oct 1975");
//		Assert.assertEquals(new Result("16/10/1975 - Unknown"), rule.evaluate(logicContext, PATIENT_ID, null));
//	}
//
//	/**
//	 * @verifies start on CRYPTOCOSSUS_TREATMENT_STARTED is FLUCONAZOLE
//	 * @see MohFluconazoleStartStopDateRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
//	 */
//	@Test
//	public void evaluate_shouldStartOnCRYPTOCOSSUS_TREATMENT_STARTEDIsFLUCONAZOLE() throws Exception {
//		addStartObs(MohEvaluableNameConstants.CRYPTOCOSSUS_TREATMENT_STARTED, MohEvaluableNameConstants.FLUCONAZOLE, "17 Oct 1975");
//		Assert.assertEquals(new Result("17/10/1975 - Unknown"), rule.evaluate(logicContext, PATIENT_ID, null));
//	}
//
//	/**
//	 * @verifies stop on CRYPTOCOCCAL_TREATMENT_PLAN is STOP_ALL
//	 * @see MohFluconazoleStartStopDateRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
//	 */
//	@Test
//	public void evaluate_shouldStopOnCRYPTOCOCCAL_TREATMENT_PLANIsSTOP_ALL() throws Exception {
//		addStopObs(MohEvaluableNameConstants.CRYPTOCOCCAL_TREATMENT_PLAN, MohEvaluableNameConstants.STOP_ALL, "18 Oct 1975");
//		Assert.assertEquals(new Result("Unknown - 18/10/1975"), rule.evaluate(logicContext, PATIENT_ID, null));
//	}
//
//	/**
//	 * @verifies start and stop on CRYPTOCOCCAL_TREATMENT_PLAN with correct values
//	 * @see MohFluconazoleStartStopDateRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
//	 */
//	@Test
//	public void evaluate_shouldStartAndStopOnCRYPTOCOCCAL_TREATMENT_PLANWithCorrectValues() throws Exception {
//		// valid values: 16th and 18th - invalid will still get picked up due to same obs.concept
//		addStartObs(MohEvaluableNameConstants.CRYPTOCOCCAL_TREATMENT_PLAN, MohEvaluableNameConstants.START_DRUGS, "16 Oct 1975");
//		addStartObs(MohEvaluableNameConstants.CRYPTOCOCCAL_TREATMENT_PLAN, MohEvaluableNameConstants.STOP_ALL, "17 Oct 1975");
//		addStartObs(MohEvaluableNameConstants.CRYPTOCOCCAL_TREATMENT_PLAN, MohEvaluableNameConstants.START_DRUGS, "18 Oct 1975");
//		addStartObs(MohEvaluableNameConstants.CRYPTOCOCCAL_TREATMENT_PLAN, MohEvaluableNameConstants.STOP_ALL, "19 Oct 1975");
//
//		// valid values: 17th and 19th - invalid will still get picked up due to same obs.concept
//		addStopObs(MohEvaluableNameConstants.CRYPTOCOCCAL_TREATMENT_PLAN, MohEvaluableNameConstants.START_DRUGS, "16 Oct 1975");
//		addStopObs(MohEvaluableNameConstants.CRYPTOCOCCAL_TREATMENT_PLAN, MohEvaluableNameConstants.STOP_ALL, "17 Oct 1975");
//		addStopObs(MohEvaluableNameConstants.CRYPTOCOCCAL_TREATMENT_PLAN, MohEvaluableNameConstants.START_DRUGS, "18 Oct 1975");
//		addStopObs(MohEvaluableNameConstants.CRYPTOCOCCAL_TREATMENT_PLAN, MohEvaluableNameConstants.STOP_ALL, "19 Oct 1975");
//
//		String expected = MOHReportUtil.joinAsSingleCell(
//				"16/10/1975 - 17/10/1975",
//				"18/10/1975 - 19/10/1975"
//		);
//
//		Assert.assertEquals(new Result(expected), rule.evaluate(logicContext, PATIENT_ID, null));
//	}
}
