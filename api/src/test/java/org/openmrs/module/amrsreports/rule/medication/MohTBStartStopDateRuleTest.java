package org.openmrs.module.amrsreports.rule.medication;

import org.junit.runner.RunWith;
import org.openmrs.api.context.Context;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * test class for {@link MohTBStartStopDateRule}
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class MohTBStartStopDateRuleTest {

//	private static final List<String> initConcepts = Arrays.asList(
//			MohEvaluableNameConstants.TUBERCULOSIS_TREATMENT_STARTED,
//			MohEvaluableNameConstants.TUBERCULOSIS_TREATMENT_COMPLETED_DATE
//	);
//
//	private static final int PATIENT_ID = 5;
//
//	private ConceptService conceptService;
//	private MohCoreService mohCoreService;
//
//	private MohTBStartStopDateRule rule;
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
//				conceptService.getConcept(MohEvaluableNameConstants.TUBERCULOSIS_TREATMENT_STARTED)));
//
//		Map<String, Collection<OpenmrsObject>> stopRestrictions = new HashMap<String, Collection<OpenmrsObject>>();
//		stopRestrictions.put("concept", Arrays.<OpenmrsObject>asList(
//				conceptService.getConcept(MohEvaluableNameConstants.TUBERCULOSIS_TREATMENT_COMPLETED_DATE)));
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
//		rule = new MohTBStartStopDateRule();
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
//		obs.setValueDatetime(makeDate(answer));
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
//		obs.setValueDatetime(makeDate(answer));
//		obs.setObsDatetime(makeDate(date));
//		currentStopObs.add(obs);
//	}
//
//	/**
//	 * @verifies look at valueDatetime and not obsDatetime
//	 * @see MohTBStartStopDateRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
//	 */
//	@Test
//	public void evaluate_shouldLookAtValueDatetimeAndNotObsDatetime() throws Exception {
//		addStartObs(MohEvaluableNameConstants.TUBERCULOSIS_TREATMENT_STARTED, "17 Oct 1975", "16 Oct 1975");
//		Assert.assertEquals(new Result("17/10/1975 - Unknown"), rule.evaluate(logicContext, PATIENT_ID, null));
//
//		addStopObs(MohEvaluableNameConstants.TUBERCULOSIS_TREATMENT_COMPLETED_DATE, "20 Oct 1975", "16 Oct 1975");
//		Assert.assertEquals(new Result("17/10/1975 - 20/10/1975"), rule.evaluate(logicContext, PATIENT_ID, null));
//	}
}
