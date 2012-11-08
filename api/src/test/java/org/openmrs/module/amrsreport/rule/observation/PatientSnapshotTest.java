package org.openmrs.module.amrsreport.rule.observation;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.UserContext;
import org.openmrs.module.amrsreport.rule.MohEvaluableNameConstants;

/**
 * Test class for PatientSnapshot
 */
public class PatientSnapshotTest {
    /**
     * @verifies recognize and set WHO stage from an obs or specify peds WHO
     * @see PatientSnapshot#consume(org.openmrs.Obs)
     */
    @Test
    public void consume_shouldRecognizeAndSetWHOStageFromAnObsOrSpecifyPedsWHO() throws Exception {
        Context.openSession();
        UserContext userContext = new UserContext();
        User systemUser = new User();
        systemUser.setUserId(501);
        userContext.getAllRoles(systemUser);

        Context.setUserContext(userContext);

        /*Concepts for WHO_STAGE_ADULT*/
        Concept cp=Context.getConceptService().getConcept(MohEvaluableNameConstants.WHO_STAGE_ADULT);
        Concept cpAns=Context.getConceptService().getConcept(MohEvaluableNameConstants.WHO_STAGE_2_ADULT);

        /*Concepts for WHO_STAGE_PEDS*/
        Concept cpPeds=Context.getConceptService().getConcept(MohEvaluableNameConstants.WHO_STAGE_PEDS);
        Concept cpAnsPeds=Context.getConceptService().getConcept(MohEvaluableNameConstants.WHO_STAGE_2_PEDS);

        /*Concepts for HIV_DNA_PCR*/
        Concept cpHiv=Context.getConceptService().getConcept(MohEvaluableNameConstants.HIV_DNA_PCR);
        Concept cpAnsHiv=Context.getConceptService().getConcept(MohEvaluableNameConstants.POSITIVE);



        /*Set concepts for adults*/
        Obs obs= new Obs();
        obs.setConcept(cp);
        obs.setValueCoded(cpAns);

        /*Set concepts for peds*/
        Obs obsPeds= new Obs();
        obs.setConcept(cpPeds);
        obs.setValueCoded(cpAnsPeds);

        /*Set concepts for hiv dna pcr*/
        Obs obsHiv= new Obs();
        obs.setConcept(cpHiv);
        obs.setValueCoded(cpAnsHiv);


        ARVPatientSnapshot arvPatientSnapshot = new ARVPatientSnapshot();

        /*Test result for adults*/
        arvPatientSnapshot.consume(obs);
        Integer expectedResult=(Integer)arvPatientSnapshot.getProperty("adultWHOStage");

        Assert.assertTrue(expectedResult ==2);

        /*Test results for peds*/
        arvPatientSnapshot.consume(obsPeds);
        Integer expectedPedsResult=(Integer)arvPatientSnapshot.getProperty("pedsWHOStage");
        Assert.assertTrue(expectedPedsResult ==2);

        /*Test results for HIV_DNA_PCR*/
        arvPatientSnapshot.consume(obsHiv);
        Boolean expectedHivDnaPcrRes= (Boolean) arvPatientSnapshot.getProperty("HIVDNAPCRPositive");
        Assert.assertTrue(expectedHivDnaPcrRes);

    }

    /**
     * @verifies determine eligibility based on age group and flags
     * @see PatientSnapshot#eligible()
     */
    @Test
    public void eligible_shouldDetermineEligibilityBasedOnAgeGroupAndFlags() throws Exception {

        ARVPatientSnapshot arvPatientSnapshot = new ARVPatientSnapshot();
        arvPatientSnapshot.setAgeGroup(MohEvaluableNameConstants.AgeGroup.EIGHTEEN_MONTHS_TO_FIVE_YEARS);
        arvPatientSnapshot.setProperty("pedsWHOStage",4);
        arvPatientSnapshot.eligible();

        String expectedReason = (String) arvPatientSnapshot.getProperty("reason");
        //Assert.assertTrue(expectedReason.equals("Clinical Only"));
        assert expectedReason.equals("Clinical Only"):"They are not equal";


        arvPatientSnapshot.setProperty("reason","Clinical Only");
        Integer expectedStage = (Integer) arvPatientSnapshot.getProperty("pedsWHOStage");
        assert expectedStage ==4:"That is pedsWHOStage";

    }
}
