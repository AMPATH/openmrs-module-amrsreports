package org.openmrs.module.amrsreports.reporting.data.evaluator;

import junit.framework.Assert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.MOHFacility;
import org.openmrs.module.amrsreports.reporting.data.ARTTransferStatusDataDefinition;
import org.openmrs.module.amrsreports.reporting.data.DateARTStartedDataDefinition;
import org.openmrs.module.amrsreports.reporting.data.EligibilityForARTDataDefinition;
import org.openmrs.module.amrsreports.reporting.data.EligibilityForARTIncludingTransferDataDefinition;
import org.openmrs.module.amrsreports.rule.MohEvaluableNameConstants;
import org.openmrs.module.amrsreports.snapshot.ARVPatientSnapshot;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.service.PersonDataService;
import org.openmrs.module.reporting.dataset.query.service.DataSetQueryService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.context.PersonEvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.TestUtil;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Please give a brief explanation of what the class does
 */

public class EligibilityForARTIncludingTransferEvaluatorTest extends BaseModuleContextSensitiveTest {

    private Date evaluationDate;
    private MOHFacility facility;
    private EvaluationContext evaluationContext;
    private EligibilityForARTIncludingTransferEvaluator evaluator;
    private EligibilityForARTIncludingTransferDataDefinition definition;

    private Log log = LogFactory.getLog(this.getClass());

    @Before
    public void setUp() throws Exception {

        executeDataSet("datasets/art-transfer-status.xml");
        executeDataSet("datasets/concepts-eligibility-for-art.xml");


        Cohort c = new Cohort("6,7,8,9");

        evaluationDate = new Date();
        facility = new MOHFacility();
        facility.addLocation(new Location(1));

        evaluationContext = new EvaluationContext();
        evaluationContext.setEvaluationDate(evaluationDate);
        evaluationContext.setBaseCohort(c);

        Map<String,Object> params = new HashMap<String, Object>();
        params.put("facility",facility);

        evaluationContext.setParameterValues(params);

        definition = new EligibilityForARTIncludingTransferDataDefinition();
        evaluator = new EligibilityForARTIncludingTransferEvaluator();
    }

    @Ignore
    @Test
    public void shouldReturnArtTransferStatus() throws Exception {
        TestUtil.printOutTableContents(getConnection(), "amrsreports_hiv_care_enrollment");
        TestUtil.printOutTableContents(getConnection(), "concept");
    }

    @Test
    public void shouldConfirmAPatientIsTransferIn() throws EvaluationException {

        EvaluatedPersonData actual = evaluator.evaluate(definition, evaluationContext);
        Map<Integer, Object> data = actual.getData();

        // retrieve result for patient #1
        ARVPatientSnapshot transferStatus = (ARVPatientSnapshot)data.get(7);

        Boolean actualStatus = (Boolean)transferStatus.get("transfer");
        Boolean expected = true;
        Assert.assertEquals("Test for patient #7 failed", actualStatus, expected);


    }

}
