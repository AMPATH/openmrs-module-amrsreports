package org.openmrs.module.amrsreports.reporting.data.evaluator;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.AmrsReportsConstants;
import org.openmrs.module.amrsreports.MohTestUtils;
import org.openmrs.module.amrsreports.model.PatientTBTreatmentData;
import org.openmrs.module.amrsreports.reporting.data.CtxStartStopDataDefinition;
import org.openmrs.module.amrsreports.reporting.data.TbTreatmentStartDateDataDefinition;
import org.openmrs.module.amrsreports.rule.MohEvaluableNameConstants;
import org.openmrs.module.reporting.common.ListMap;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.context.PersonEvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * test class for TbTreatmentStartDateDataEvaluator
 */
public class TbTreatmentStartDateDataEvaluatorTest extends BaseModuleContextSensitiveTest {

    protected Patient patient;
    private Date evaluationDate;
    private PersonEvaluationContext evaluationContext;
    private TbTreatmentStartDateDataEvaluator evaluator;
    private TbTreatmentStartDateDataDefinition definition;
    private Integer tbRegAtrrType;

    @Before
    public void setUp() throws Exception {

        executeDataSet("datasets/concepts-tb.xml");

        patient = MohTestUtils.createTestPatient();

        Cohort c = new Cohort(Collections.singleton(patient.getId()));

        evaluationDate = new Date();
        evaluationContext = new PersonEvaluationContext(evaluationDate);
        evaluationContext.setBaseCohort(c);

        definition = new TbTreatmentStartDateDataDefinition();
        evaluator = new TbTreatmentStartDateDataEvaluator();

        String typeId = Context.getAdministrationService().getGlobalProperty(AmrsReportsConstants.TB_REGISTRATION_NO_ATTRIBUTE_TYPE);
        tbRegAtrrType = Integer.valueOf(typeId);
    }

    /**
     * @should return value_datetime for TUBERCULOSIS DRUG TREATMENT START DATE
     * @throws Exception
     */
    @Test
    public void shouldReturnValue_datetimeForTUBERCULOSIS_DRUG_TREATMENT_START_DATE() throws Exception {
        MohTestUtils.addDateTimeObs(patient, MohEvaluableNameConstants.TUBERCULOSIS_DRUG_TREATMENT_START_DATE,
                "10 Oct 1975 ", "16 Oct 1990");

        PatientTBTreatmentData actual  = getActualResult();
        List<Date> tbStartDates = new ArrayList<Date>(actual.getEvaluationDates());

        assertThat(tbStartDates.size(), is(1));
        assertThat(String.valueOf(tbStartDates.get(0)), is("1975-10-10 00:00:00.0"));



    }

    /**
     * @should return obs_datetime when drug TUBERCULOSIS TREATMENT PLAN is START DRUGS
     * @throws Exception
     */
    @Test
    public void shouldReturnObs_datetimeIfTUBERCULOSIS_TREATMENT_PLAN_Equals_START_DRUGS() throws Exception {
        MohTestUtils.addCodedObs(patient, MohEvaluableNameConstants.TUBERCULOSIS_TREATMENT_PLAN,
                MohEvaluableNameConstants.START_DRUGS, "16 Oct 1975");

        PatientTBTreatmentData actual  = getActualResult();
        List<Date> tbStartDates = new ArrayList<Date>(actual.getEvaluationDates());

        assertThat(tbStartDates.size(), is(1));
        assertThat(String.valueOf(tbStartDates.get(0)), is("1975-10-16 00:00:00.0"));


    }

    /**
     * @should return TB reg.no and obs_datetime when drug TUBERCULOSIS TREATMENT PLAN is START DRUGS
     * @throws Exception
     */
    @Test
    public void shouldReturnRegNOObs_datetimeIfTUBERCULOSIS_TREATMENT_PLAN_Equals_START_DRUGS() throws Exception {

        MohTestUtils.addAttribute(patient, tbRegAtrrType, "TB/200/2010");
        MohTestUtils.addCodedObs(patient, MohEvaluableNameConstants.TUBERCULOSIS_TREATMENT_PLAN,
                MohEvaluableNameConstants.START_DRUGS, "16 Oct 1975");

        PatientTBTreatmentData actual  = getActualResult();
        List<Date> tbStartDates = new ArrayList<Date>(actual.getEvaluationDates());
        assertThat(tbStartDates.size(), is(1));
        assertThat(String.valueOf(tbStartDates.get(0)), is("1975-10-16 00:00:00.0"));

        assertThat(actual.getTbRegNO(),is("TB/200/2010"));

    }

    /**
     * @should return TB reg.no and obs_datetime when drug TUBERCULOSIS TREATMENT PLAN is START DRUGS
     * @throws Exception
     */
    @Test
    public void shouldReturnRegNOandTUBERCULOSIS_DRUG_TREATMENT_START_DATE() throws Exception {

        MohTestUtils.addAttribute(patient,tbRegAtrrType,"TB/800/2010");
        MohTestUtils.addDateTimeObs(patient, MohEvaluableNameConstants.TUBERCULOSIS_DRUG_TREATMENT_START_DATE,
                "10 Oct 1975 ", "16 Oct 1990");

        PatientTBTreatmentData actual  = getActualResult();
        List<Date> tbStartDates = new ArrayList<Date>(actual.getEvaluationDates());

        assertThat(tbStartDates.size(), is(1));
        assertThat(String.valueOf(tbStartDates.get(0)), is("1975-10-10 00:00:00.0"));

        assertThat(actual.getTbRegNO(),is("TB/800/2010"));

    }


    private PatientTBTreatmentData getActualResult() throws EvaluationException {

        EvaluatedPersonData actual = evaluator.evaluate(definition, evaluationContext);
        Map<Integer, Object> data =  actual.getData();

        PatientTBTreatmentData patientData = (PatientTBTreatmentData) data.get(patient.getId());
        return patientData;
    }


}
