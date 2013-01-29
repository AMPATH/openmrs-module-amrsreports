package org.openmrs.module.amrsreport.rule.collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.api.ConceptService;
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
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * test file for MohConfirmedHivPositiveDateRule class
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class MohConfirmedHivPositiveDateRuleTest {

	private static final List<String> initConcepts = Arrays.asList(
			MohEvaluableNameConstants.HIV_ENZYME_IMMUNOASSAY_QUALITATIVE,
			MohEvaluableNameConstants.HIV_RAPID_TEST_QUALITATIVE,
			MohEvaluableNameConstants.POSITIVE
	);

	private static final int PATIENT_ID = 5;
	private ConceptService conceptService;
	private MohCoreService mohCoreService;
	private List<Obs> currentObs;
	private List<Encounter> currentEncounters;
	private MohConfirmedHivPositiveDateRule rule;

	@Before
	public void setup() {

		// initialize the current obs and Encounters
		currentObs = new ArrayList<Obs>();
		currentEncounters = new ArrayList<Encounter>();

		// build the concept service
		int i = 0;
		conceptService = Mockito.mock(ConceptService.class);

		for (String conceptName : initConcepts) {
			Mockito.when(conceptService.getConcept(conceptName)).thenReturn(new Concept(i++));
		}
		Mockito.when(conceptService.getConcept((String) null)).thenReturn(null);

		//set up MohCoreService
		mohCoreService = Mockito.mock(MohCoreService.class);

		//return current Observations
		Mockito.when(mohCoreService.getPatientObservations(Mockito.eq(PATIENT_ID),
				Mockito.anyMap(), Mockito.any(MohFetchRestriction.class))).thenReturn(currentObs);

		//return current encounters
		Mockito.when(mohCoreService.getPatientEncounters(Mockito.eq(PATIENT_ID),
				Mockito.anyMap(), Mockito.any(MohFetchRestriction.class))).thenReturn(currentEncounters);

		// set up Context
		PowerMockito.mockStatic(Context.class);
		Mockito.when(Context.getConceptService()).thenReturn(conceptService);
		Mockito.when(Context.getService(MohCoreService.class)).thenReturn(mohCoreService);

		rule = new MohConfirmedHivPositiveDateRule();
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
	 * adds an observation with the given date as the obs datetime
	 *
	 * @param concept
	 * @param date
	 */
	private void addObs(String concept, String answer, String date) {
		Obs obs = new Obs();
		obs.setConcept(conceptService.getConcept(concept));
		obs.setValueCoded(conceptService.getConcept(answer));
		obs.setObsDatetime(makeDate(date));
		currentObs.add(obs);
	}

	/**
	 * adds an encounter
	 *
	 * @param date
	 */
	private void addEncounter(String date) {
		Encounter encounter = new Encounter();
		encounter.setDateCreated(makeDate(date));
		encounter.setEncounterDatetime(makeDate(date));
		currentEncounters.add(encounter);
	}


	/**
	 * @verifies return the the first date a patient was confirmed HIV positive using HIV_ENZYME_IMMUNOASSAY_QUALITATIVE test
	 * @see MohConfirmedHivPositiveDateRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Test
	public void evaluate_shouldReturnTheTheFirstDateAPatientWasConfirmedHIVPositiveUsingHIV_ENZYME_IMMUNOASSAY_QUALITATIVETest() throws Exception {
		addObs(MohEvaluableNameConstants.HIV_ENZYME_IMMUNOASSAY_QUALITATIVE, MohEvaluableNameConstants.POSITIVE, "16 Oct 2012");
		assertThat(rule.evaluate(null, PATIENT_ID, null), is(new Result("16-Oct-12")));
	}

	/**
	 * @verifies return the first date a patient was confirmed HIV Positive using HIV_RAPID_TEST_QUALITATIVE test
	 * @see MohConfirmedHivPositiveDateRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Test
	public void evaluate_shouldReturnTheFirstDateAPatientWasConfirmedHIVPositiveUsingHIV_RAPID_TEST_QUALITATIVETest() throws Exception {
		addObs(MohEvaluableNameConstants.HIV_RAPID_TEST_QUALITATIVE, MohEvaluableNameConstants.POSITIVE, "17 Oct 2012");
		assertThat(rule.evaluate(null, PATIENT_ID, null), is(new Result("17-Oct-12")));
	}

	/**
	 * @verifies return result for a patient who is HIV negative
	 * @see MohConfirmedHivPositiveDateRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Test
	public void evaluate_shouldReturnResultForAPatientWhoIsHIVNegative() throws Exception {
		assertThat(rule.evaluate(null, PATIENT_ID, null), is(new Result(MohEvaluableNameConstants.UNKNOWN)));
	}

	/**
	 * @verifies return the date for the first encounter
	 * @see MohConfirmedHivPositiveDateRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Test
	public void evaluate_shouldReturnTheDateForTheFirstEncounter() throws Exception {
		addEncounter("18 Oct 2012");
		assertThat(rule.evaluate(null, PATIENT_ID, null), is(new Result("18-Oct-12")));
	}
}
