package org.openmrs.module.amrsreport.rule.collection;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.amrsreport.rule.collection.LostToFollowUpPatientSnapshot;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptName;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientSetService;
import java.util.Set;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.openmrs.module.amrsreport.rule.util.MohTestUtils;
import org.openmrs.api.context.Context;
import org.openmrs.Patient;

/**
 * Created with IntelliJ IDEA.
 * User: oliver
 * Date: 11/20/12
 * Time: 11:25 AM
 * To change this template use File | Settings | File Templates.
 */
public class LostToFollowUpPatientSnapshotTest extends BaseModuleContextSensitiveTest {

    public static final String CONCEPT_TRANSFER_CARE_TO_OTHER_CENTER = "TRANSFER CARE TO OTHER CENTER";
    public static final String CONCEPT_AMPATH = "AMPATH";
    /**
     * @verifies find out if a particular Obs is consumed
     * @see LostToFollowUpPatientSnapshot#consume(org.openmrs.Obs)
     */
    @Test
    public void consume_shouldFindOutIfAParticularObsIsConsumed() throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");

        Patient patient2 = Context.getPatientService().getPatient(8);


        Assert.assertNotNull(patient2) ;
        Assert.assertTrue("The patient should be alive",!(patient2.isDead()));


        MohTestUtils.createQuestion(CONCEPT_TRANSFER_CARE_TO_OTHER_CENTER, new String[]{
                CONCEPT_AMPATH,
                "NON-AMPATH"
        });

        ConceptService conceptService = Context.getConceptService();

        EncounterService service = Context.getEncounterService();

        Encounter sampleEncounter = new Encounter() ;
        Date encounterDate = new Date() ;
        sampleEncounter.setEncounterDatetime(encounterDate);
        sampleEncounter.setPatient(patient2);
        sampleEncounter.setEncounterType(service.getEncounterType("ADULTINITIAL"));

        ObsService obsService = Context.getObsService();

        Obs obs = new Obs();
        obs.setConcept(conceptService.getConceptByName(CONCEPT_TRANSFER_CARE_TO_OTHER_CENTER));
        obs.setValueCoded(conceptService.getConceptByName(CONCEPT_AMPATH));
        obs.setObsDatetime(new Date());
        obs.setEncounter(sampleEncounter);

        //sampleEncounter.setObs(allObs);

        Encounter resEncounter=service.saveEncounter(sampleEncounter);

        Assert.assertNotNull("The encounter was not saved",resEncounter.getUuid()!=null);

        Assert.assertNotNull("No Obs was saved",resEncounter.getObs().size()>0);

        Assert.assertTrue("The patient is dead",patient2.getDeathDate()==null);

        LostToFollowUpPatientSnapshot samplePatientSnapshot = new LostToFollowUpPatientSnapshot();
        samplePatientSnapshot.consume(obs);


        String transferResult= samplePatientSnapshot.getProperty("reason").toString();

        String expectedTransferRes ="TO | (Ampath) " + sdf.format(obs.getObsDatetime());

        Assert.assertTrue(transferResult, transferResult.equals(expectedTransferRes));
    }

    /**
     * @verifies test if a given encounter is consumed
     * @see LostToFollowUpPatientSnapshot#consume(org.openmrs.Encounter)
     */
    @Test
    public void consume_shouldTestIfAGivenEncounterIsConsumed() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");

        Patient patient2 = Context.getPatientService().getPatient(8);


        Assert.assertNotNull(patient2) ;
        Assert.assertTrue("The patient should be alive",!(patient2.isDead()));


        MohTestUtils.createQuestion(CONCEPT_TRANSFER_CARE_TO_OTHER_CENTER, new String[]{
                CONCEPT_AMPATH,
                "NON-AMPATH"
        });

        ConceptService conceptService = Context.getConceptService();

        EncounterService service = Context.getEncounterService();

        Encounter sampleEncounter = new Encounter() ;
        Date encounterDate = new Date() ;
        sampleEncounter.setEncounterDatetime(encounterDate);
        sampleEncounter.setPatient(patient2);
        sampleEncounter.setEncounterType(service.getEncounterType(31));

        ObsService obsService = Context.getObsService();

        Obs obs = new Obs();
        obs.setConcept(conceptService.getConceptByName(CONCEPT_TRANSFER_CARE_TO_OTHER_CENTER));
        obs.setValueCoded(conceptService.getConceptByName(CONCEPT_AMPATH));
        obs.setObsDatetime(new Date());
        obs.setEncounter(sampleEncounter);

        //sampleEncounter.setObs(allObs);

        Encounter resEncounter=service.saveEncounter(sampleEncounter);

        Assert.assertNotNull("The encounter was not saved",resEncounter.getUuid()!=null);

        Assert.assertNotNull("No Obs was saved",resEncounter.getObs().size()>0);

        Assert.assertTrue("The patient is dead",patient2.getDeathDate()==null);

        LostToFollowUpPatientSnapshot samplePatientSnapshot = new LostToFollowUpPatientSnapshot();
        samplePatientSnapshot.consume(sampleEncounter);


        String transferResult= samplePatientSnapshot.getProperty("reason").toString();

        String expectedTransferRes ="DEAD | " + sdf.format(sampleEncounter.getEncounterDatetime());

        Assert.assertTrue(transferResult, transferResult.equals(expectedTransferRes));
    }
}
