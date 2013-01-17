package org.openmrs.module.amrsreport.rule.collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.result.Result;
import org.openmrs.module.amrsreport.rule.MohEvaluableConstants;
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

/**
 * test file for {@link MohReasonsForPepRule}
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class MohReasonsForPepRuleTest {

	private static final List<String> initConcepts = Arrays.asList(
			MohReasonsForPepRule.METHOD_OF_HIV_EXPOSURE,
			MohReasonsForPepRule.SEXUAL_ASSAULT,
			MohReasonsForPepRule.SPOUSES_PARTNER_SUSPECTED_HIV,
			MohReasonsForPepRule.PHYSICAL_ASSAULT,
			MohReasonsForPepRule.OCCUPATIONAL_EXPOSURE,
			MohReasonsForPepRule.OTHER_NON_CODED,
			MohEvaluableNameConstants.NONE
	);

	private static final int PATIENT_ID = 5;

	private static final String WRONG_ENCOUNTER_TYPE = "bleh";

	private ConceptService conceptService;
	private EncounterService encounterService;
	private MohCoreService mohCoreService;

	private MohReasonsForPepRule rule;

	private List<Obs> currentObs;

	@Before
	public void setup() {

		// initialize the current obs
		currentObs = new ArrayList<Obs>();

		// build the concept service
		int i = 0;
		conceptService = Mockito.mock(ConceptService.class);
		for (String conceptName : initConcepts) {
			Mockito.when(conceptService.getConcept(conceptName)).thenReturn(new Concept(i++));
		}
		Mockito.when(conceptService.getConcept((String) null)).thenReturn(null);

		// build the encounter service
		encounterService = Mockito.mock(EncounterService.class);
		Mockito.when(encounterService.getEncounterType(MohReasonsForPepRule.POST_EXPOSURE_INITIAL_FORM)).thenReturn(new EncounterType(0));
		Mockito.when(encounterService.getEncounterType(MohReasonsForPepRule.POST_EXPOSURE_RETURN_FORM)).thenReturn(new EncounterType(1));
		Mockito.when(encounterService.getEncounterType(WRONG_ENCOUNTER_TYPE)).thenReturn(new EncounterType(3));

		// build the MOH Core service
		mohCoreService = Mockito.mock(MohCoreService.class);
		Mockito.when(mohCoreService.getPatientObservationsWithEncounterRestrictions(Mockito.eq(PATIENT_ID),
				Mockito.anyMap(), Mockito.anyMap(), Mockito.any(MohFetchRestriction.class))).thenReturn(currentObs);

		// set up Context
		PowerMockito.mockStatic(Context.class);
		Mockito.when(Context.getConceptService()).thenReturn(conceptService);
		Mockito.when(Context.getEncounterService()).thenReturn(encounterService);
		Mockito.when(Context.getService(MohCoreService.class)).thenReturn(mohCoreService);

		// create a rule instance
		rule = new MohReasonsForPepRule();
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
	 * @param conceptName
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
	 * @verifies recognize SEXUAL_ASSAULT
	 * @see MohReasonsForPepRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Test
	public void evaluate_shouldRecognizeSEXUAL_ASSAULT() throws Exception {
		addObs(MohReasonsForPepRule.METHOD_OF_HIV_EXPOSURE, MohReasonsForPepRule.SEXUAL_ASSAULT, "16 Oct 1975");
		Assert.assertEquals(new Result("SEXUAL ASSAULT"), rule.evaluate(null, PATIENT_ID, null));
	}

	/**
	 * @verifies recognize SPOUSES_PARTNER_SUSPECTED_HIV
	 * @see MohReasonsForPepRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Test
	public void evaluate_shouldRecognizeSPOUSES_PARTNER_SUSPECTED_HIV() throws Exception {
		addObs(MohReasonsForPepRule.METHOD_OF_HIV_EXPOSURE, MohReasonsForPepRule.SPOUSES_PARTNER_SUSPECTED_HIV, "16 Oct 1975");
		Assert.assertEquals(new Result("SPOUSES PARTNER SUSPECTED HIV+"), rule.evaluate(null, PATIENT_ID, null));
	}

	/**
	 * @verifies recognize PHYSICAL_ASSAULT
	 * @see MohReasonsForPepRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Test
	public void evaluate_shouldRecognizePHYSICAL_ASSAULT() throws Exception {
		addObs(MohReasonsForPepRule.METHOD_OF_HIV_EXPOSURE, MohReasonsForPepRule.PHYSICAL_ASSAULT, "16 Oct 1975");
		Assert.assertEquals(new Result("PHYSICAL ASSAULT"), rule.evaluate(null, PATIENT_ID, null));
	}

	/**
	 * @verifies recognize OCCUPATIONAL_EXPOSURE
	 * @see MohReasonsForPepRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Test
	public void evaluate_shouldRecognizeOCCUPATIONAL_EXPOSURE() throws Exception {
		addObs(MohReasonsForPepRule.METHOD_OF_HIV_EXPOSURE, MohReasonsForPepRule.OCCUPATIONAL_EXPOSURE, "16 Oct 1975");
		Assert.assertEquals(new Result("OCCUPATIONAL EXPOSURE"), rule.evaluate(null, PATIENT_ID, null));
	}

	/**
	 * @verifies recognize OTHER_NON_CODED
	 * @see MohReasonsForPepRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Test
	public void evaluate_shouldRecognizeOTHER_NON_CODED() throws Exception {
		addObs(MohReasonsForPepRule.METHOD_OF_HIV_EXPOSURE, MohReasonsForPepRule.OTHER_NON_CODED, "16 Oct 1975");
		Assert.assertEquals(new Result("OTHER NON-CODED"), rule.evaluate(null, PATIENT_ID, null));
	}

	/**
	 * @verifies recognize multiple reasons
	 * @see MohReasonsForPepRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Test
	public void evaluate_shouldRecognizeMultipleReasons() throws Exception {
		addObs(MohReasonsForPepRule.METHOD_OF_HIV_EXPOSURE, MohReasonsForPepRule.OTHER_NON_CODED, "17 Oct 1975");
		addObs(MohReasonsForPepRule.METHOD_OF_HIV_EXPOSURE, MohReasonsForPepRule.OCCUPATIONAL_EXPOSURE, "18 Oct 1975");
		addObs(MohReasonsForPepRule.METHOD_OF_HIV_EXPOSURE, MohReasonsForPepRule.PHYSICAL_ASSAULT, "19 Oct 1975");
		addObs(MohReasonsForPepRule.METHOD_OF_HIV_EXPOSURE, MohReasonsForPepRule.SPOUSES_PARTNER_SUSPECTED_HIV, "20 Oct 1975");
		addObs(MohReasonsForPepRule.METHOD_OF_HIV_EXPOSURE, MohReasonsForPepRule.SEXUAL_ASSAULT, "21 Oct 1975");
		Assert.assertEquals(new Result("OTHER NON-CODED;OCCUPATIONAL EXPOSURE;PHYSICAL ASSAULT;SPOUSES PARTNER SUSPECTED HIV+;SEXUAL ASSAULT"), rule.evaluate(null, PATIENT_ID, null));
	}

	/**
	 * @verifies not recognize other reasons
	 * @see MohReasonsForPepRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Test
	public void evaluate_shouldNotRecognizeOtherReasons() throws Exception {
		addObs(MohReasonsForPepRule.METHOD_OF_HIV_EXPOSURE, MohEvaluableNameConstants.NONE, "17 Oct 1975");
		addObs(MohReasonsForPepRule.METHOD_OF_HIV_EXPOSURE, MohReasonsForPepRule.OCCUPATIONAL_EXPOSURE, "18 Oct 1975");
		addObs(MohReasonsForPepRule.METHOD_OF_HIV_EXPOSURE, MohReasonsForPepRule.PHYSICAL_ASSAULT, "19 Oct 1975");
		addObs(MohReasonsForPepRule.METHOD_OF_HIV_EXPOSURE, MohEvaluableNameConstants.NONE, "20 Oct 1975");
		addObs(MohReasonsForPepRule.METHOD_OF_HIV_EXPOSURE, MohReasonsForPepRule.SEXUAL_ASSAULT, "21 Oct 1975");
		Assert.assertEquals(new Result("OCCUPATIONAL EXPOSURE;PHYSICAL ASSAULT;SEXUAL ASSAULT"), rule.evaluate(null, PATIENT_ID, null));
	}
}
