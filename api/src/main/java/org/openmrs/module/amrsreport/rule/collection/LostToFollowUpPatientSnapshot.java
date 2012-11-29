package org.openmrs.module.amrsreport.rule.collection;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreport.rule.MohEvaluableNameConstants;
import org.openmrs.module.amrsreport.rule.observation.PatientSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: oliver
 * Date: 11/15/12
 * Time: 12:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class LostToFollowUpPatientSnapshot extends PatientSnapshot {

    public static final String CONCEPT_DATE_OF_DEATH = "DATE OF DEATH";
    public static final String CONCEPT_DEATH_REPORTED_BY = "DEATH REPORTED BY";
    public static final String CONCEPT_CAUSE_FOR_DEATH = "CAUSE FOR DEATH";
    public static final String CONCEPT_DECEASED = "DECEASED";
    public static final String CONCEPT_PATIENT_DIED = "PATIENT DIED";
    public static final String CONCEPT_TRANSFER_CARE_TO_OTHER_CENTER = "TRANSFER CARE TO OTHER CENTER";
    public static final String CONCEPT_AMPATH = "AMPATH";
    public static final String CONCEPT_RETURN_VISIT_DATE_EXP_CARE_NURSE = "RETURN VISIT DATE, EXPRESS CARE NURSE";



    SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");

    /**
     * @should find out if a particular Obs is consumed
     * @param o observation to be consumed
     * @return
     */
    @Override
    public Boolean consume(Obs o){
        Concept ob = o.getConcept();
        Concept answer = o.getValueCoded();


        if(ob.equals(getCachedConcept(CONCEPT_DATE_OF_DEATH))){
            this.setProperty("reason", "DEAD | " + sdf.format(sdf.format(o.getObsDatetime())));
            return true;
        }
        else if(ob.equals(getCachedConcept(CONCEPT_DEATH_REPORTED_BY))){
            this.setProperty("reason", "DEAD | " + sdf.format(sdf.format(o.getObsDatetime())));
            return true;
        }else if(ob.equals(getCachedConcept(CONCEPT_CAUSE_FOR_DEATH))){
            this.setProperty("reason", "DEAD | " + sdf.format(sdf.format(o.getObsDatetime())));
            return true;
        }else if(ob.equals(getCachedConcept(CONCEPT_DECEASED))){
            this.setProperty("reason","DEAD | " + sdf.format(sdf.format(o.getObsDatetime())));
            return true;
        }else if(ob.equals(getCachedConcept(CONCEPT_PATIENT_DIED))){
            this.setProperty("reason", "DEAD | " + sdf.format(o.getObsDatetime()));
            return true;
        }



        if(ob.equals(getCachedConcept(CONCEPT_TRANSFER_CARE_TO_OTHER_CENTER))){
            if(answer == getCachedConcept(CONCEPT_AMPATH))
                this.setProperty("reason", "TO | (Ampath) " + sdf.format(o.getObsDatetime()));
            else
                this.setProperty("reason", "TO | (Non-Ampath) " + sdf.format(o.getObsDatetime()));

            return true;
        }

        if(ob.equals(getCachedConcept(MohEvaluableNameConstants.RETURN_VISIT_DATE).getConceptId())){
            if(sdf.format(o.getObsDatetime()) != null){
                long requiredTimeToShowup = ((o.getValueDatetime().getTime()) - (o.getObsDatetime().getTime())) + (long)(1000 * 60 * 60 * 24 * 30.4375 * 3);
                long todayTimeFromSchedule = (new Date()).getTime() - (o.getObsDatetime().getTime());
                if( requiredTimeToShowup < todayTimeFromSchedule ){
                    this.setProperty("reason", "LTFU | " + sdf.format(o.getValueDatetime()));
                    return true;
                }
            }
        }

        if(ob.equals(getCachedConcept(CONCEPT_RETURN_VISIT_DATE_EXP_CARE_NURSE))){
            if(sdf.format(o.getObsDatetime()) != null){
                long requiredTimeToShowup = ((o.getValueDatetime().getTime()) - (o.getObsDatetime().getTime())) + (long)(1000 * 60 * 60 * 24 * 30.4375 * 3);
                long todayTimeFromSchedule = (new Date()).getTime() - (o.getObsDatetime().getTime());
                if( requiredTimeToShowup < todayTimeFromSchedule ){
                    this.setProperty("reason", "LTFU | " + sdf.format(o.getValueDatetime()));
                    return true;
                }
            }
        }

        return false;
    }


    @Override
    public boolean eligible() {
        return false;
    }

    /**
     * @should test if a given encounter is consumed
     * @param e
     * @return
     */
    public Boolean consume(Encounter e){

        EncounterType encTpInit = Context.getEncounterService().getEncounterType(MohEvaluableNameConstants.ENCOUNTER_TYPE_ADULT_INITIAL);
        EncounterType encTpRet = Context.getEncounterService().getEncounterType(MohEvaluableNameConstants.ENCOUNTER_TYPE_ADULT_RETURN);
        // DEAD
        EncounterService et = Context.getEncounterService();

        if (et.getEncounterType(31) == e.getEncounterType()){
            this.setProperty("reason","DEAD | " + sdf.format(e.getEncounterDatetime()));
            return true;
        }
        else if((encTpInit == e.getEncounterType()) || (e.getEncounterType() == encTpRet)){
            int requiredTimeToShowup = (int) (1000 * 60 * 60 * 24 * 30.4375 * 6);
            int todayTimeFromEncounter = (int) ((new Date()).getTime() - (e.getEncounterDatetime().getTime()));
            if(!(requiredTimeToShowup >= todayTimeFromEncounter)){
                this.setProperty("reason","LTFU | " + sdf.format(e.getEncounterDatetime()));
                return true;
            }

        }

        return false;
    }




}