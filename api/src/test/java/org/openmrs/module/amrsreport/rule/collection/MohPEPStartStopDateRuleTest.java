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
 * test file for MohPEPStartStopDateRule
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class MohPEPStartStopDateRuleTest {

	private static final List<String> initConcepts = Arrays.asList(
			MohPEPStartStopDateRule.ANTIRETROVIRAL_THERAPY_STATUS,
			MohPEPStartStopDateRule.ARVs_RECOMMENDED_FOR_PEP,
			MohPEPStartStopDateRule.COMPLETED,
			MohPEPStartStopDateRule.DAYS_ON_PEP_MEDS1,
			MohPEPStartStopDateRule.DAYS_ON_PEP_MEDS2,
			MohPEPStartStopDateRule.LOPINAVIR_AND_RITONAVIR,
			MohPEPStartStopDateRule.ON_ANTIRETROVIRAL_THERAPY,
			MohPEPStartStopDateRule.PATIENT_REFUSAL,
			MohPEPStartStopDateRule.REASON_ANTIRETROVIRALS_STOPPED,
			MohPEPStartStopDateRule.ZIDOVUDINE_AND_LAMIVUDINE
	);

	private static final int PATIENT_ID = 5;

	private ConceptService conceptService;
	private EncounterService encounterService;
	private MohCoreService mohCoreService;

	private MohPEPStartStopDateRule rule;

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
		Mockito.when(encounterService.getEncounterType(MohPEPStartStopDateRule.POST_EXPOSURE_INITIAL_FORM)).thenReturn(new EncounterType(0));
		Mockito.when(encounterService.getEncounterType(MohPEPStartStopDateRule.POST_EXPOSURE_RETURN_FORM)).thenReturn(new EncounterType(1));

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
		rule = new MohPEPStartStopDateRule();
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
	 * @verifies start on ANTIRETROVIRAL THERAPY STATUS of ON ANTIRETROVIRAL THERAPY
	 * @see MohPEPStartStopDateRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Test
	public void evaluate_shouldStartOnANTIRETROVIRALTHERAPYSTATUSOfONANTIRETROVIRALTHERAPY() throws Exception {
		addObs(MohPEPStartStopDateRule.ANTIRETROVIRAL_THERAPY_STATUS, MohPEPStartStopDateRule.ON_ANTIRETROVIRAL_THERAPY, "16 Oct 1975");
		Assert.assertEquals(1, currentObs.size());
		Assert.assertEquals(new Result("16-Oct-75 - Unknown"), rule.evaluate(null, PATIENT_ID, null));
	}

	/**
	 * @verifies start on ARVs RECOMMENDED FOR PEP is ZIDOVUDINE AND LAMIVUDINE
	 * @see MohPEPStartStopDateRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Test
	public void evaluate_shouldStartOnARVsRECOMMENDEDFORPEPIsZIDOVUDINEANDLAMIVUDINE() throws Exception {
		addObs(MohPEPStartStopDateRule.ARVs_RECOMMENDED_FOR_PEP, MohPEPStartStopDateRule.ZIDOVUDINE_AND_LAMIVUDINE, "17 Oct 1975");
		Assert.assertEquals(1, currentObs.size());
		Assert.assertEquals(new Result("17-Oct-75 - Unknown"), rule.evaluate(null, PATIENT_ID, null));
	}

	/**
	 * @verifies start on ARVs RECOMMENDED FOR PEP is LOPINAVIR AND RITONAVIR
	 * @see MohPEPStartStopDateRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Test
	public void evaluate_shouldStartOnARVsRECOMMENDEDFORPEPIsLOPINAVIRANDRITONAVIR() throws Exception {
		addObs(MohPEPStartStopDateRule.ARVs_RECOMMENDED_FOR_PEP, MohPEPStartStopDateRule.LOPINAVIR_AND_RITONAVIR, "18 Oct 1975");
		Assert.assertEquals(1, currentObs.size());
		Assert.assertEquals(new Result("18-Oct-75 - Unknown"), rule.evaluate(null, PATIENT_ID, null));
	}

	/**
	 * @verifies stop on REASON ANTIRETROVIRALS STOPPED is PATIENT REFUSAL
	 * @see MohPEPStartStopDateRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Test
	public void evaluate_shouldStopOnREASONANTIRETROVIRALSSTOPPEDIsPATIENTREFUSAL() throws Exception {
		addObs(MohPEPStartStopDateRule.REASON_ANTIRETROVIRALS_STOPPED, MohPEPStartStopDateRule.PATIENT_REFUSAL, "19 Oct 1975");
		Assert.assertEquals(1, currentObs.size());
		Assert.assertEquals(new Result("Unknown - 19-Oct-75"), rule.evaluate(null, PATIENT_ID, null));
	}

	/**
	 * @verifies stop on REASON ANTIRETROVIRALS STOPPED is COMPLETED
	 * @see MohPEPStartStopDateRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Test
	public void evaluate_shouldStopOnREASONANTIRETROVIRALSSTOPPEDIsCOMPLETED() throws Exception {
		addObs(MohPEPStartStopDateRule.REASON_ANTIRETROVIRALS_STOPPED, MohPEPStartStopDateRule.COMPLETED, "20 Oct 1975");
		Assert.assertEquals(1, currentObs.size());
		Assert.assertEquals(new Result("Unknown - 20-Oct-75"), rule.evaluate(null, PATIENT_ID, null));
	}

	/**
	 * @verifies stop on any value for DAYS ON POST EXPOSURE PROPHYLAXIS BEFORE STOPPING DUE TO NON ADHERENCE ANTIRETROVIRALS
	 * @see MohPEPStartStopDateRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Test
	public void evaluate_shouldStopOnAnyValueForDAYSONPOSTEXPOSUREPROPHYLAXISBEFORESTOPPINGDUETONONADHERENCEANTIRETROVIRALS() throws Exception {
		addObs(MohPEPStartStopDateRule.DAYS_ON_PEP_MEDS1, null, "21 Oct 1975");
		Assert.assertEquals(1, currentObs.size());
		Assert.assertEquals(new Result("Unknown - 21-Oct-75"), rule.evaluate(null, PATIENT_ID, null));
	}

	/**
	 * @verifies stop on any value for DAYS ON POST EXPOSURE PROPHYLAXIS BEFORE STOPPING DUE TO SIDE EFFECTS ANTIRETROVIRALS
	 * @see MohPEPStartStopDateRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Test
	public void evaluate_shouldStopOnAnyValueForDAYSONPOSTEXPOSUREPROPHYLAXISBEFORESTOPPINGDUETOSIDEEFFECTSANTIRETROVIRALS() throws Exception {
		addObs(MohPEPStartStopDateRule.DAYS_ON_PEP_MEDS2, null, "22 Oct 1975");
		Assert.assertEquals(1, currentObs.size());
		Assert.assertEquals(new Result("Unknown - 22-Oct-75"), rule.evaluate(null, PATIENT_ID, null));
	}

}
