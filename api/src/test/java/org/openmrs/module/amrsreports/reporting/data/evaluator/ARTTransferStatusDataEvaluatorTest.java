package org.openmrs.module.amrsreports.reporting.data.evaluator;

import junit.framework.Assert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Location;
import org.openmrs.module.amrsreports.MOHFacility;
import org.openmrs.module.amrsreports.reporting.data.ARTTransferStatusDataDefinition;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.context.PersonEvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.TestUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Please give a brief explanation of what the class does
 */
public class ARTTransferStatusDataEvaluatorTest extends BaseModuleContextSensitiveTest {

    private Date evaluationDate;
    private MOHFacility facility;
    private PersonEvaluationContext evaluationContext;
    private ARTTransferStatusDataEvaluator evaluator;
    private ARTTransferStatusDataDefinition definition;

    private Log log = LogFactory.getLog(this.getClass());

    @Before
    public void setUp() throws Exception {

        executeDataSet("datasets/art-transfer-status.xml");

        Cohort c = new Cohort("6,7,8,9");

        evaluationDate = new Date();
        facility = new MOHFacility();
        facility.addLocation(new Location(1));

        evaluationContext = new PersonEvaluationContext(evaluationDate);
        evaluationContext.setBaseCohort(c);

        Map<String,Object> params = new HashMap<String, Object>();
        params.put("facility",facility);

        evaluationContext.setParameterValues(params);

        definition = new ARTTransferStatusDataDefinition();
        evaluator = new ARTTransferStatusDataEvaluator();
    }

    @Test
    public void shouldReturnArtTransferStatus() throws Exception {
        TestUtil.printOutTableContents(getConnection(), "amrsreports_hiv_care_enrollment");
    }

    @Test
    public void shouldConfirmAPatientIsTransferIn() throws EvaluationException {

        EvaluatedPersonData actual = evaluator.evaluate(definition, evaluationContext);
        Map<Integer, Object> data = actual.getData();

        // retrieve result for patient #1
        Boolean transferStatus = (Boolean)data.get(6);
        Boolean expectedStatus = false;
        Assert.assertEquals("Test for patient #6 failed",transferStatus,expectedStatus);

    }

    @Test
    public void shouldConfirmAPatientIsNoneTransfer() throws EvaluationException {

        EvaluatedPersonData actual = evaluator.evaluate(definition, evaluationContext);
        Map<Integer, Object> data = actual.getData();

        // retrieve result for patient #2
        Boolean transferStatus = (Boolean)data.get(7);
        Boolean expectedStatus = true;
        Assert.assertEquals("Test for patient #7 failed",transferStatus,expectedStatus);
    }
}
