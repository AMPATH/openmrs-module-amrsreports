package org.openmrs.module.amrsreport.rule.collection;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.module.amrsreport.rule.collection.MohUniquePatientNumberRule;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.AdministrationService;
import org.openmrs.module.amrsreport.cache.MohCacheUtils;

import java.lang.String;

/**
 * Created with IntelliJ IDEA.
 * User: oliver
 * Date: 11/27/12
 * Time: 11:03 AM
 * To change this template use File | Settings | File Templates.
 */
public class MohUniquePatientNumberRuleTest extends BaseModuleContextSensitiveTest {
    /**
     * @verifies return a Unique Patient Number
     * @see MohUniquePatientNumberRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
     */
    @Test
    public void evaluate_shouldReturnAUniquePatientNumber() throws Exception {
        /*Test with a patient with no CCC Number*/
        Patient patient = Context.getPatientService().getPatient(8);

        MohUniquePatientNumberRule mohUniquePatientNumberRule = new MohUniquePatientNumberRule();
        String result = mohUniquePatientNumberRule.evaluate(null, patient.getId(), null).toString();
        String expectedResult = "not found";

        Assert.assertEquals("The patient has no CCC Number",expectedResult,result );

        /*Create a patient and assign a CCC Number*/

        Patient patient1 = Context.getPatientService().getPatient(6);
        AdministrationService ams = Context.getAdministrationService();
        PatientIdentifierType pit = MohCacheUtils.getPatientIdentifierType(ams.getGlobalProperty("mflgenerator.mfl"));

        PatientIdentifier pi = new PatientIdentifier("11740-00001", pit, null);
        pi.setPatient(patient1);

        PatientIdentifier pidtest=patient1.getPatientIdentifier(pit);

        String result1 = mohUniquePatientNumberRule.evaluate(null, patient1.getId(), null).toString();
        String expectedResult1 = "11740-00001";
        Assert.assertEquals("The two identifiers are not equal",expectedResult1,result1);


    }
}
