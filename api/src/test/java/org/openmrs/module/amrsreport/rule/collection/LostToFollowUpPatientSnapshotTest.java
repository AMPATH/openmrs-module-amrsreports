package org.openmrs.module.amrsreport.rule.collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreport.rule.MohEvaluableNameConstants;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;


/**
 * A test file for LostToFollowUpPatientSnapshot class. It contains methods that check if a given
 * Obs is consumed and finally evaluates eligibility for processing
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class LostToFollowUpPatientSnapshotTest {

	private static final List<String> initConcepts = Arrays.asList(
			LostToFollowUpPatientSnapshot.CONCEPT_DATE_OF_DEATH,
			LostToFollowUpPatientSnapshot.CONCEPT_DEATH_REPORTED_BY,
			LostToFollowUpPatientSnapshot.CONCEPT_CAUSE_FOR_DEATH,
			LostToFollowUpPatientSnapshot.CONCEPT_DECEASED,
			LostToFollowUpPatientSnapshot.CONCEPT_PATIENT_DIED,
			LostToFollowUpPatientSnapshot.CONCEPT_TRANSFER_CARE_TO_OTHER_CENTER,
			LostToFollowUpPatientSnapshot.CONCEPT_AMPATH,
			LostToFollowUpPatientSnapshot.CONCEPT_RETURN_VISIT_DATE_EXP_CARE_NURSE,
			MohEvaluableNameConstants.RETURN_VISIT_DATE,
			"TRUE"
	);

	private String DECEASED_ANSWER = "TRUE";
	private String ENCOUNTER_TYPE_ADULT_INITIAL = "ADULTINITIAL";
	private String ENCOUNTER_TYPE_ADULT_RETURN = "ADULTRETURN";
	private String ENCOUNTER_TYPE_DEATH_REPORT = "DEATHREPORT";

	private ConceptService conceptService;
	private EncounterService encounterService;
	private LostToFollowUpPatientSnapshot rule;

	private Obs currentObs;
	private Encounter encounter;

	@Before
	public void setup() {
		// build the mock services
		encounterService = Mockito.mock(EncounterService.class);

		Mockito.when(encounterService.getEncounterType(this.ENCOUNTER_TYPE_ADULT_INITIAL)).thenReturn(new EncounterType(0));
		Mockito.when(encounterService.getEncounterType(this.ENCOUNTER_TYPE_ADULT_RETURN)).thenReturn(new EncounterType(1));
		Mockito.when(encounterService.getEncounterType(this.ENCOUNTER_TYPE_DEATH_REPORT)).thenReturn(new EncounterType(2));

		// build the concept service
		int i = 0;
		conceptService = Mockito.mock(ConceptService.class);
		for (String conceptName : initConcepts) {
			Mockito.when(conceptService.getConcept(conceptName)).thenReturn(new Concept(i++));
		}
		Mockito.when(conceptService.getConcept((String) null)).thenReturn(null);

		// set up Context
		PowerMockito.mockStatic(Context.class);
		Mockito.when(Context.getConceptService()).thenReturn(conceptService);
		Mockito.when(Context.getEncounterService()).thenReturn(encounterService);

		rule = new LostToFollowUpPatientSnapshot();
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
	private void setObs(String concept, String answer, String date) {
		currentObs = new Obs();
		currentObs.setConcept(conceptService.getConcept(concept));
		currentObs.setValueCoded(conceptService.getConcept(answer));
		currentObs.setObsDatetime(makeDate(date));
		currentObs.setEncounter(encounter);
	}

	/**
	 * adds an encounter with the given type and date as the encounter datetime
	 *
	 * @param encounterType
	 * @param date
	 */
	private void setEncounter(String encounterType, String date) {
		encounter = new Encounter();
		encounter.setEncounterType(encounterService.getEncounterType(encounterType));
		encounter.setEncounterDatetime(makeDate(date));
	}

	/**
	 * @verifies find out if a particular Obs is consumed
	 * @see LostToFollowUpPatientSnapshot#consume(org.openmrs.Obs)
	 */

	@Test
	public void consume_shouldProperlyDetermineTOfromAnObservation() throws Exception {
		setEncounter(this.ENCOUNTER_TYPE_ADULT_INITIAL, "16 Oct 1975");
		setObs(LostToFollowUpPatientSnapshot.CONCEPT_TRANSFER_CARE_TO_OTHER_CENTER, LostToFollowUpPatientSnapshot.CONCEPT_AMPATH, "16 Oct 1975");
		rule.consume(currentObs);
		assertThat(rule.getProperty("why").toString(), is("TO | (Ampath) "));
		assertThat(rule.getProperty("obsDate").toString(), is("16-Oct-75"));
	}

	@Test
	public void consume_shouldProperlyDetermineDEADfromAnEncounter() throws Exception {
		setEncounter(this.ENCOUNTER_TYPE_DEATH_REPORT, "16 Oct 1975");
		rule.consume(encounter);
		assertThat(rule.getProperty("reason").toString(), is("DEAD | 16-Oct-75"));
	}

	@Test
	public void consume_shouldProperlyDetermineLTFUfromAnEncounter() throws Exception {
		setEncounter(this.ENCOUNTER_TYPE_ADULT_INITIAL, "16 Oct 2012");
		rule.consume(encounter);
		assertThat(rule.getProperty("reason").toString(), is("LTFU | 16-Oct-12"));
	}


}
