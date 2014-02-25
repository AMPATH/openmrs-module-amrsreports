package org.openmrs.module.amrsreports.reporting.data.evaluator;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.module.amrsreports.MohTestUtils;
import org.openmrs.module.amrsreports.model.SortedObsFromDate;
import org.openmrs.module.amrsreports.reporting.data.DateARTStartedDataDefinition;
import org.openmrs.module.amrsreports.reporting.data.TBStatusDataDefinition;
import org.openmrs.module.amrsreports.rule.MohEvaluableNameConstants;
import org.openmrs.module.reporting.data.MappedData;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.context.PersonEvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Test class for TBStatusDataEvaluator class
 */
public class TBStatusDataEvaluatorTest extends BaseModuleContextSensitiveTest {

    protected Patient patient;
    private Date evaluationDate;
    private PersonEvaluationContext evaluationContext;
    private TBStatusDataEvaluator evaluator;
    private TBStatusDataDefinition definition;

    @Before
    public void setUp() throws Exception {

        executeDataSet("datasets/concepts-tb-status.xml");

        patient = MohTestUtils.createTestPatient();

        Cohort c = new Cohort(Collections.singleton(patient.getId()));

        evaluationDate = new Date();
        evaluationContext = new PersonEvaluationContext(evaluationDate);
        evaluationContext.setBaseCohort(c);

        definition = new TBStatusDataDefinition();

        MappedData<DateARTStartedDataDefinition> artDateMap = new MappedData<DateARTStartedDataDefinition>();
        artDateMap.setParameterizable(new DateARTStartedDataDefinition());
        definition.setEffectiveDateDefinition(artDateMap);

        evaluator = new TBStatusDataEvaluator();

    }

    /**
     * @should return  NORMAL for PATIENT_REPORTED_X_RAY_CHEST
     * @throws Exception
     */
    @Test
    public void shouldReturnNORMALForPATIENT_REPORTED_X_RAY_CHEST() throws Exception {
        MohTestUtils.addCodedObs(patient, MohEvaluableNameConstants.PATIENT_REPORTED_X_RAY_CHEST,
                MohEvaluableNameConstants.NORMAL, "16 Oct 1990");

        SortedObsFromDate actual  = getActualResult();
        ArrayList<Obs> observations= new ArrayList<Obs>(actual.getData());

        assertThat(observations.size(), is(1));

        String valueCoded = observations.get(0).getValueCoded().getName().toString();
        String thisConcept = observations.get(0).getConcept().getName().toString();
        assertEquals(valueCoded, MohEvaluableNameConstants.NORMAL);
        assertEquals(thisConcept, MohEvaluableNameConstants.PATIENT_REPORTED_X_RAY_CHEST);




    }

 /**
 * @should return NEGATIVE for SPUTUM FOR AFB
 * @throws Exception
 */
        @Test
        public void shouldReturnNEGATIVEForSPUTUM_FOR_AFB() throws Exception {
            MohTestUtils.addCodedObs(patient, MohEvaluableNameConstants.SPUTUM_FOR_AFB,
                    MohEvaluableNameConstants.NEGATIVE, "16 Oct 1990");

            SortedObsFromDate actual  = getActualResult();
            ArrayList<Obs> observations= new ArrayList<Obs>(actual.getData());

            assertThat(observations.size(), is(1));

            String valueCoded = observations.get(0).getValueCoded().getName().toString();
            String thisConcept = observations.get(0).getConcept().getName().toString();
            assertEquals(valueCoded, MohEvaluableNameConstants.NEGATIVE);
            assertEquals(thisConcept, MohEvaluableNameConstants.SPUTUM_FOR_AFB);

        }

    /**
     * @should return WEEKS COUGH DURATION, CODED with WEEKS result
     * @throws Exception
     */
    @Test
    public void shouldReturnWEEKSForCOUGH_DURATION_CODED() throws Exception {
        MohTestUtils.addCodedObs(patient, MohEvaluableNameConstants.COUGH_DURATION_CODED,
                MohEvaluableNameConstants.WEEKS, "16 Oct 1990");

        SortedObsFromDate actual  = getActualResult();
        ArrayList<Obs> observations= new ArrayList<Obs>(actual.getData());

        assertThat(observations.size(), is(1));

        String valueCoded = observations.get(0).getValueCoded().getName().toString();
        String thisConcept = observations.get(0).getConcept().getName().toString();
        assertEquals(valueCoded, MohEvaluableNameConstants.WEEKS);
        assertEquals(thisConcept, MohEvaluableNameConstants.COUGH_DURATION_CODED);

    }


    /**
     * @should return value_datetime for COUGH DURATION, CODED with MONTHS result
     * @throws Exception
     */
    @Test
    public void shouldReturnMONTHSForCOUGH_DURATION_CODED() throws Exception {
        MohTestUtils.addCodedObs(patient, MohEvaluableNameConstants.COUGH_DURATION_CODED,
                MohEvaluableNameConstants.MONTHS, "16 Oct 1990");

        SortedObsFromDate actual  = getActualResult();
        ArrayList<Obs> observations= new ArrayList<Obs>(actual.getData());

        assertThat(observations.size(), is(1));

        String valueCoded = observations.get(0).getValueCoded().getName().toString();
        String thisConcept = observations.get(0).getConcept().getName().toString();
        assertEquals(valueCoded, MohEvaluableNameConstants.MONTHS);
        assertEquals(thisConcept, MohEvaluableNameConstants.COUGH_DURATION_CODED);

    }

    /**
     * @should return CONTINUOUS for COUGH DURATION, CODED
     * @throws Exception
     */
    @Test
    public void shouldReturnCONTINUOUSForCOUGH_DURATION_CODED() throws Exception {
        MohTestUtils.addCodedObs(patient, MohEvaluableNameConstants.COUGH_DURATION_CODED,
                MohEvaluableNameConstants.CONTINUOUS, "16 Oct 1990");

        SortedObsFromDate actual  = getActualResult();
        ArrayList<Obs> observations= new ArrayList<Obs>(actual.getData());

        assertThat(observations.size(), is(1));

        String valueCoded = observations.get(0).getValueCoded().getName().toString();
        String thisConcept = observations.get(0).getConcept().getName().toString();
        assertEquals(valueCoded, MohEvaluableNameConstants.CONTINUOUS);
        assertEquals(thisConcept, MohEvaluableNameConstants.COUGH_DURATION_CODED);

    }


    /**
     * @should return NEGATIVE  for Spatum for AFB result
     * @throws Exception
     */
    @Test
    public void shouldTestForNegativeSpatumForAFB() throws Exception {
        MohTestUtils.addCodedObs(patient, MohEvaluableNameConstants.SPUTUM_FOR_AFB,
                MohEvaluableNameConstants.NEGATIVE, "16 Oct 1990");

        SortedObsFromDate actual  = getActualResult();
        ArrayList<Obs> observations= new ArrayList<Obs>(actual.getData());

        assertThat(observations.size(), is(1));

        String valueCoded = observations.get(0).getValueCoded().getName().toString();
        String thisConcept = observations.get(0).getConcept().getName().toString();
        assertEquals(valueCoded, MohEvaluableNameConstants.NEGATIVE);
        assertEquals(thisConcept, MohEvaluableNameConstants.SPUTUM_FOR_AFB);

    }


    /**
     *
     * @should test if REVIEW OF SYSTEMS, GENERAL (1069) = COUGH FOR MORE THAN TWO WEEKS (6171)
     * @throws Exception
     */
    @Test
    public void shouldTestForREVIEW_OF_SYSTEMS_GENERAL() throws Exception {
        MohTestUtils.addCodedObs(patient, MohEvaluableNameConstants.REVIEW_OF_SYSTEMS_GENERAL,
                MohEvaluableNameConstants.COUGH_FOR_MORE_THAN_TWO_WEEKS, "16 Oct 1990");

        SortedObsFromDate actual  = getActualResult();
        ArrayList<Obs> observations= new ArrayList<Obs>(actual.getData());

        assertThat(observations.size(), is(1));

        String valueCoded = observations.get(0).getValueCoded().getName().toString();
        String thisConcept = observations.get(0).getConcept().getName().toString();
        assertEquals(valueCoded, MohEvaluableNameConstants.COUGH_FOR_MORE_THAN_TWO_WEEKS);
        assertEquals(thisConcept, MohEvaluableNameConstants.REVIEW_OF_SYSTEMS_GENERAL);

    }

    /**
     *
     * @should test if PATIENT REPORTED X-RAY, CHEST (7178) <> NORMAL (1115)
     * @throws Exception
     */
    @Test
    public void shouldTestifPATIENT_REPORTED_X_RAY_CHEST_isNormal() throws Exception {
        MohTestUtils.addCodedObs(patient, MohEvaluableNameConstants.PATIENT_REPORTED_X_RAY_CHEST,
                MohEvaluableNameConstants.NORMAL, "16 Oct 1990");

        SortedObsFromDate actual  = getActualResult();
        ArrayList<Obs> observations= new ArrayList<Obs>(actual.getData());

        assertThat(observations.size(), is(1));

        String valueCoded = observations.get(0).getValueCoded().getName().toString();
        String thisConcept = observations.get(0).getConcept().getName().toString();
        assertEquals(valueCoded, MohEvaluableNameConstants.NORMAL);
        assertEquals(thisConcept, MohEvaluableNameConstants.PATIENT_REPORTED_X_RAY_CHEST);

    }

    /**
     *
     * @should test if TUBERCULOSIS TREATMENT PLAN (1268) <> NONE (1107)
     * @throws Exception
     */
    @Test
    public void shouldTestifTUBERCULOSIS_TREATMENT_PLAN_isNotNONE() throws Exception {
        MohTestUtils.addCodedObs(patient, MohEvaluableNameConstants.TUBERCULOSIS_TREATMENT_PLAN,
                MohEvaluableNameConstants.START_DRUGS, "16 Oct 1990");

        SortedObsFromDate actual  = getActualResult();
        ArrayList<Obs> observations= new ArrayList<Obs>(actual.getData());

        assertThat(observations.size(), is(1));

    }

    /**
     * @should return NOT DONE  for Spatum for AFB result
     * @throws Exception
     */
    @Test
    public void shouldReturnNotDoneForSpatumForAFB() throws Exception {
        MohTestUtils.addCodedObs(patient, MohEvaluableNameConstants.SPUTUM_FOR_AFB,
                MohEvaluableNameConstants.NOT_DONE, "16 Oct 1990");

        SortedObsFromDate actual  = getActualResult();
        ArrayList<Obs> observations= new ArrayList<Obs>(actual.getData());

        assertThat(observations.size(), is(1));

        String valueCoded = observations.get(0).getValueCoded().getName().toString();
        String thisConcept = observations.get(0).getConcept().getName().toString();
        assertEquals(valueCoded, MohEvaluableNameConstants.NOT_DONE);
        assertEquals(thisConcept, MohEvaluableNameConstants.SPUTUM_FOR_AFB);

    }


    private SortedObsFromDate getActualResult() throws EvaluationException {

        EvaluatedPersonData actual = evaluator.evaluate(definition, evaluationContext);
        Map<Integer, Object> data =  actual.getData();

        SortedObsFromDate patientData = (SortedObsFromDate) data.get(patient.getId());
        return patientData;
    }

}
