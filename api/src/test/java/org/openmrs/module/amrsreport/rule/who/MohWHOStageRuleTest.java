package org.openmrs.module.amrsreport.rule.who;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.openmrs.Concept;
import org.openmrs.ConceptSet;
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
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * test class for {@link MohWHOStageRule}
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class MohWHOStageRuleTest {

	private static final List<String> initConcepts = Arrays.asList(
			MohEvaluableNameConstants.ADULT_WHO_CONDITION_QUERY,
			MohEvaluableNameConstants.PEDS_WHO_SPECIFIC_CONDITION_QUERY,
			MohEvaluableNameConstants.NONE
	);

	private static final List<String> initConceptSets = Arrays.asList(
			MohEvaluableNameConstants.WHO_STAGE_1_ADULT,
			MohEvaluableNameConstants.WHO_STAGE_2_ADULT,
			MohEvaluableNameConstants.WHO_STAGE_3_ADULT,
			MohEvaluableNameConstants.WHO_STAGE_4_ADULT,
			MohEvaluableNameConstants.WHO_STAGE_1_PEDS,
			MohEvaluableNameConstants.WHO_STAGE_2_PEDS,
			MohEvaluableNameConstants.WHO_STAGE_3_PEDS,
			MohEvaluableNameConstants.WHO_STAGE_4_PEDS
	);

	private static final int PATIENT_ID = 5;

	private ConceptService conceptService;
	private MohCoreService mohCoreService;

	private MohWHOStageRule rule;

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

		for (String conceptName : initConceptSets) {
			Mockito.when(conceptService.getConcept(conceptName)).thenReturn(generateConceptSet(i++));
		}

		// build the MOH Core service
		mohCoreService = Mockito.mock(MohCoreService.class);
		Mockito.when(mohCoreService.getPatientObservations(Mockito.eq(PATIENT_ID),
				Mockito.anyMap(), Mockito.any(MohFetchRestriction.class))).thenReturn(currentObs);

		// set up Context
		PowerMockito.mockStatic(Context.class);
		Mockito.when(Context.getConceptService()).thenReturn(conceptService);
		Mockito.when(Context.getService(MohCoreService.class)).thenReturn(mohCoreService);

		// create a rule instance
		rule = new MohWHOStageRule();
	}

	/**
	 * convenience method to make a concept set with random members
	 *
	 * @param id
	 * @return
	 */
	private Concept generateConceptSet(int id) {
		Concept concept = new Concept(id);
		concept.setSet(Boolean.TRUE);

		// initialize the sets ... guess this is normally done by hibernate?
		concept.setConceptSets(new HashSet<ConceptSet>());

		for (int i = 1; i < 5; i++) {
			Concept member = new Concept(id * 1000 + i);
			// member.addName(new ConceptName(member.getId()));
			concept.addSetMember(member);
		}

		return concept;
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
	private void addObs(Concept concept, Concept answer, String date) {
		Obs obs = new Obs();
		obs.setConcept(concept);
		obs.setValueCoded(answer);
		obs.setObsDatetime(makeDate(date));
		currentObs.add(obs);
	}

	/**
	 * convenience method for clearing the current observation list
	 */
	private void resetObs() {
		currentObs.clear();
	}

	/**
	 * @verifies recognize WHO_STAGE_1_ADULT
	 * @see MohWHOStageRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Test
	public void evaluate_shouldRecognizeWHO_STAGE_1_ADULT() throws Exception {
		Concept question = conceptService.getConcept(MohEvaluableNameConstants.ADULT_WHO_CONDITION_QUERY);
		for (Concept value : conceptService.getConcept(MohEvaluableNameConstants.WHO_STAGE_1_ADULT).getSetMembers()) {
			resetObs();
			addObs(question, value, "16 Oct 1975");
			assertThat(rule.evaluate(null, PATIENT_ID, null), is(new Result("WHO STAGE 1 ADULT - 16-Oct-75")));
		}
	}

	/**
	 * @verifies recognize WHO_STAGE_2_ADULT
	 * @see MohWHOStageRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Test
	public void evaluate_shouldRecognizeWHO_STAGE_2_ADULT() throws Exception {
		Concept question = conceptService.getConcept(MohEvaluableNameConstants.ADULT_WHO_CONDITION_QUERY);
		for (Concept value : conceptService.getConcept(MohEvaluableNameConstants.WHO_STAGE_2_ADULT).getSetMembers()) {
			resetObs();
			addObs(question, value, "16 Oct 1975");
			assertThat(rule.evaluate(null, PATIENT_ID, null), is(new Result("WHO STAGE 2 ADULT - 16-Oct-75")));
		}
	}

	/**
	 * @verifies recognize WHO_STAGE_3_ADULT
	 * @see MohWHOStageRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Test
	public void evaluate_shouldRecognizeWHO_STAGE_3_ADULT() throws Exception {
		Concept question = conceptService.getConcept(MohEvaluableNameConstants.ADULT_WHO_CONDITION_QUERY);
		for (Concept value : conceptService.getConcept(MohEvaluableNameConstants.WHO_STAGE_3_ADULT).getSetMembers()) {
			resetObs();
			addObs(question, value, "16 Oct 1975");
			assertThat(rule.evaluate(null, PATIENT_ID, null), is(new Result("WHO STAGE 3 ADULT - 16-Oct-75")));
		}
	}

	/**
	 * @verifies recognize WHO_STAGE_4_ADULT
	 * @see MohWHOStageRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Test
	public void evaluate_shouldRecognizeWHO_STAGE_4_ADULT() throws Exception {
		Concept question = conceptService.getConcept(MohEvaluableNameConstants.ADULT_WHO_CONDITION_QUERY);
		for (Concept value : conceptService.getConcept(MohEvaluableNameConstants.WHO_STAGE_4_ADULT).getSetMembers()) {
			resetObs();
			addObs(question, value, "16 Oct 1975");
			assertThat(rule.evaluate(null, PATIENT_ID, null), is(new Result("WHO STAGE 4 ADULT - 16-Oct-75")));
		}
	}

	/**
	 * @verifies recognize WHO_STAGE_1_PEDS
	 * @see MohWHOStageRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Test
	public void evaluate_shouldRecognizeWHO_STAGE_1_PEDS() throws Exception {
		Concept question = conceptService.getConcept(MohEvaluableNameConstants.PEDS_WHO_SPECIFIC_CONDITION_QUERY);
		for (Concept value : conceptService.getConcept(MohEvaluableNameConstants.WHO_STAGE_1_PEDS).getSetMembers()) {
			resetObs();
			addObs(question, value, "16 Oct 1975");
			assertThat(rule.evaluate(null, PATIENT_ID, null), is(new Result("WHO STAGE 1 PEDS - 16-Oct-75")));
		}
	}

	/**
	 * @verifies recognize WHO_STAGE_2_PEDS
	 * @see MohWHOStageRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Test
	public void evaluate_shouldRecognizeWHO_STAGE_2_PEDS() throws Exception {
		Concept question = conceptService.getConcept(MohEvaluableNameConstants.PEDS_WHO_SPECIFIC_CONDITION_QUERY);
		for (Concept value : conceptService.getConcept(MohEvaluableNameConstants.WHO_STAGE_2_PEDS).getSetMembers()) {
			resetObs();
			addObs(question, value, "16 Oct 1975");
			assertThat(rule.evaluate(null, PATIENT_ID, null), is(new Result("WHO STAGE 2 PEDS - 16-Oct-75")));
		}
	}

	/**
	 * @verifies recognize WHO_STAGE_3_PEDS
	 * @see MohWHOStageRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Test
	public void evaluate_shouldRecognizeWHO_STAGE_3_PEDS() throws Exception {
		Concept question = conceptService.getConcept(MohEvaluableNameConstants.PEDS_WHO_SPECIFIC_CONDITION_QUERY);
		for (Concept value : conceptService.getConcept(MohEvaluableNameConstants.WHO_STAGE_3_PEDS).getSetMembers()) {
			resetObs();
			addObs(question, value, "16 Oct 1975");
			assertThat(rule.evaluate(null, PATIENT_ID, null), is(new Result("WHO STAGE 3 PEDS - 16-Oct-75")));
		}
	}

	/**
	 * @verifies recognize WHO_STAGE_4_PEDS
	 * @see MohWHOStageRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Test
	public void evaluate_shouldRecognizeWHO_STAGE_4_PEDS() throws Exception {
		Concept question = conceptService.getConcept(MohEvaluableNameConstants.PEDS_WHO_SPECIFIC_CONDITION_QUERY);
		for (Concept value : conceptService.getConcept(MohEvaluableNameConstants.WHO_STAGE_4_PEDS).getSetMembers()) {
			resetObs();
			addObs(question, value, "16 Oct 1975");
			assertThat(rule.evaluate(null, PATIENT_ID, null), is(new Result("WHO STAGE 4 PEDS - 16-Oct-75")));
		}
	}

	/**
	 * @verifies return UNKNOWN if not found
	 * @see MohWHOStageRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
	 */
	@Test
	public void evaluate_shouldReturnUNKNOWNIfNotFound() throws Exception {
		Concept question = conceptService.getConcept(MohEvaluableNameConstants.PEDS_WHO_SPECIFIC_CONDITION_QUERY);
		resetObs();
		addObs(question, conceptService.getConcept(MohEvaluableNameConstants.NONE), "16 Oct 1975");
		assertThat(rule.evaluate(null, PATIENT_ID, null), is(new Result("Unknown")));
	}
}
