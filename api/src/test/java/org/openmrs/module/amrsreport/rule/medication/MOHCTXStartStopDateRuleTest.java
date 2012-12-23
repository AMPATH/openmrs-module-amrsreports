package org.openmrs.module.amrsreport.rule.medication;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.OpenmrsObject;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.result.Result;
import org.openmrs.module.amrsreport.rule.MohEvaluableNameConstants;
import org.openmrs.module.amrsreport.service.MohCoreService;
import org.openmrs.module.amrsreport.util.MohFetchRestriction;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * test class for {@link MOHCTXStartStopDateRule}
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class MOHCTXStartStopDateRuleTest {

	private static final List<String> initConcepts = Arrays.asList(
			MohEvaluableNameConstants.PCP_PROPHYLAXIS_STARTED,
			MohEvaluableNameConstants.REASON_PCP_PROPHYLAXIS_STOPPED,
			MohEvaluableNameConstants.REASON_PCP_PROPHYLAXIS_STOPPED_DETAILED,
			MohEvaluableNameConstants.NONE
	);

	private static final int PATIENT_ID = 5;

	private ConceptService conceptService;
	private MohCoreService mohCoreService;

	private MOHCTXStartStopDateRule rule;

	private List<Obs> currentStartObs;
	private List<Obs> currentStopObs;

	@Before
	public void setup() {

		// initialize the current obs
		currentStartObs = new ArrayList<Obs>();
		currentStopObs = new ArrayList<Obs>();

		// build the concept service
		int i = 0;
		conceptService = Mockito.mock(ConceptService.class);
		for (String conceptName : initConcepts) {
			Mockito.when(conceptService.getConcept(conceptName)).thenReturn(new Concept(i++));
		}
		Mockito.when(conceptService.getConcept((String) null)).thenReturn(null);

		// build the MOH Core service
		mohCoreService = Mockito.mock(MohCoreService.class);

		Map<String, Collection<OpenmrsObject>> startRestrictions = new HashMap<String, Collection<OpenmrsObject>>();
		startRestrictions.put("concept", Arrays.<OpenmrsObject>asList(
				conceptService.getConcept(MohEvaluableNameConstants.PCP_PROPHYLAXIS_STARTED)));

		Map<String, Collection<OpenmrsObject>> stopRestrictions = new HashMap<String, Collection<OpenmrsObject>>();
		stopRestrictions.put("concept", Arrays.<OpenmrsObject>asList(
				conceptService.getConcept(MohEvaluableNameConstants.REASON_PCP_PROPHYLAXIS_STOPPED),
				conceptService.getConcept(MohEvaluableNameConstants.REASON_PCP_PROPHYLAXIS_STOPPED_DETAILED)
		));

		Mockito.when(mohCoreService.getPatientObservations(Mockito.eq(PATIENT_ID), Mockito.eq(startRestrictions),
				Mockito.any(MohFetchRestriction.class))).thenReturn(currentStartObs);
		Mockito.when(mohCoreService.getPatientObservations(Mockito.eq(PATIENT_ID), Mockito.eq(stopRestrictions),
				Mockito.any(MohFetchRestriction.class))).thenReturn(currentStopObs);

		// set up Context
		PowerMockito.mockStatic(Context.class);
		Mockito.when(Context.getConceptService()).thenReturn(conceptService);
		Mockito.when(Context.getService(MohCoreService.class)).thenReturn(mohCoreService);

		// create a rule instance
		rule = new MOHCTXStartStopDateRule();
	}

	/**
	 * generate a date from a string
	 *
	 * @param date
	 * @return
	 */
	private Date makeDate(String date) {
		try {
			return new SimpleDateFormat("d MMM yyyy", Locale.ENGLISH).parse(date);
		} catch (Exception e) {
			// pass
		}
		return new Date();
	}

	/**
	 * adds a start observation with the given date as the obs datetime
	 *
	 * @param conceptName
	 * @param date
	 */
	private void addStartObs(String concept, String answer, String date) {
		Obs obs = new Obs();
		obs.setConcept(conceptService.getConcept(concept));
		obs.setValueCoded(conceptService.getConcept(answer));
		obs.setObsDatetime(makeDate(date));
		currentStartObs.add(obs);
	}

	/**
	 * adds a stop observation with the given date as the obs datetime
	 *
	 * @param conceptName
	 * @param date
	 */
	private void addStopObs(String concept, String answer, String date) {
		Obs obs = new Obs();
		obs.setConcept(conceptService.getConcept(concept));
		obs.setValueCoded(conceptService.getConcept(answer));
		obs.setObsDatetime(makeDate(date));
		currentStopObs.add(obs);
	}

	/**
	 * @verifies start on PCP_PROPHYLAXIS_STARTED with not null answer
	 * @see MOHCTXStartStopDateRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Test
	public void evaluate_shouldStartOnPCP_PROPHYLAXIS_STARTEDWithNotNullAnswer() throws Exception {
		addStartObs(MohEvaluableNameConstants.PCP_PROPHYLAXIS_STARTED, MohEvaluableNameConstants.NONE, "16 Oct 1975");
		Assert.assertEquals(new Result("16-Oct-75 - Unknown"), rule.evaluate(null, PATIENT_ID, null));
	}

	/**
	 * @verifies not start on PCP_PROPHYLAXIS_STARTED with null answer
	 * @see MOHCTXStartStopDateRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Test
	public void evaluate_shouldNotStartOnPCP_PROPHYLAXIS_STARTEDWithNullAnswer() throws Exception {
		addStartObs(MohEvaluableNameConstants.PCP_PROPHYLAXIS_STARTED, null, "17 Oct 1975");
		Assert.assertEquals(new Result(""), rule.evaluate(null, PATIENT_ID, null));
	}

	/**
	 * @verifies stop on REASON_PCP_PROPHYLAXIS_STOPPED with not null answer
	 * @see MOHCTXStartStopDateRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Test
	public void evaluate_shouldStopOnREASON_PCP_PROPHYLAXIS_STOPPEDWithNotNullAnswer() throws Exception {
		addStopObs(MohEvaluableNameConstants.REASON_PCP_PROPHYLAXIS_STOPPED, MohEvaluableNameConstants.NONE, "18 Oct 1975");
		Assert.assertEquals(new Result("Unknown - 18-Oct-75"), rule.evaluate(null, PATIENT_ID, null));
	}

	/**
	 * @verifies not stop on REASON_PCP_PROPHYLAXIS_STOPPED with null answer
	 * @see MOHCTXStartStopDateRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Test
	public void evaluate_shouldNotStopOnREASON_PCP_PROPHYLAXIS_STOPPEDWithNullAnswer() throws Exception {
		addStopObs(MohEvaluableNameConstants.PCP_PROPHYLAXIS_STARTED, null, "19 Oct 1975");
		Assert.assertEquals(new Result(""), rule.evaluate(null, PATIENT_ID, null));
	}

	/**
	 * @verifies stop on REASON_PCP_PROPHYLAXIS_STOPPED_DETAILED with not null answer
	 * @see MOHCTXStartStopDateRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Test
	public void evaluate_shouldStopOnREASON_PCP_PROPHYLAXIS_STOPPED_DETAILEDWithNotNullAnswer() throws Exception {
		addStopObs(MohEvaluableNameConstants.REASON_PCP_PROPHYLAXIS_STOPPED_DETAILED, MohEvaluableNameConstants.NONE, "20 Oct 1975");
		Assert.assertEquals(new Result("Unknown - 20-Oct-75"), rule.evaluate(null, PATIENT_ID, null));
	}

	/**
	 * @verifies not stop on REASON_PCP_PROPHYLAXIS_STOPPED_DETAILED with null answer
	 * @see MOHCTXStartStopDateRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Test
	public void evaluate_shouldNotStopOnREASON_PCP_PROPHYLAXIS_STOPPED_DETAILEDWithNullAnswer() throws Exception {
		addStopObs(MohEvaluableNameConstants.REASON_PCP_PROPHYLAXIS_STOPPED_DETAILED, null, "21 Oct 1975");
		Assert.assertEquals(new Result(""), rule.evaluate(null, PATIENT_ID, null));
	}
}
