package org.openmrs.module.amrsreport.rule.observation;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.module.amrsreport.rule.MohEvaluableNameConstants;

/**
 * Created with IntelliJ IDEA.
 * User: oliver
 * Date: 11/5/12
 * Time: 12:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class ARVPatientSnapshot extends PatientSnapshot {

    public static final String REASON_CLINICAL = "Clinical Only";
    public static final String REASON_CLINICAL_CD4 = "Clinical + CD4";
    public static final String REASON_CLINICAL_CD4_HIV_DNA_PCR = "Clinical + CD4 + HIV DNA PCR";
    public static final String REASON_CLINICAL_HIV_DNA_PCR = "Clinical + HIV DNA PCR";


    public ARVPatientSnapshot(){
        this.setProperty("pedsWHOStage",0);
        this.setProperty("adultWHOStage",0);
        this.setProperty("cd4ByFacs",Double.MAX_VALUE);
        this.setProperty("cd4PercentByFacs",Double.MAX_VALUE);
        this.setProperty("HIVDNAPCRPositive",false);
        this.setProperty("reason","");

    }

    /* @see PatientSnapshot#consume(Obs) */
    public Boolean consume(Obs o) {
        Concept q = o.getConcept();
        Concept answer = o.getValueCoded();
        Double value = o.getValueNumeric();

        if (q.equals(getCachedConcept(MohEvaluableNameConstants.WHO_STAGE_ADULT))) {
            if (answer.equals(getCachedConcept(MohEvaluableNameConstants.WHO_STAGE_1_ADULT))) {
                this.setProperty("adultWHOStage", 1);

            } else if (answer.equals(getCachedConcept(MohEvaluableNameConstants.WHO_STAGE_2_ADULT))) {
                this.setProperty("adultWHOStage", 2);
            } else if (answer.equals(getCachedConcept(MohEvaluableNameConstants.WHO_STAGE_3_ADULT))) {
                this.setProperty("adultWHOStage", 3);
            } else if (answer.equals(getCachedConcept(MohEvaluableNameConstants.WHO_STAGE_4_ADULT))) {
                this.setProperty("adultWHOStage", 4);
            }
            return true;
        }
        if (q.equals(getCachedConcept(MohEvaluableNameConstants.WHO_STAGE_PEDS))) {
            if (answer.equals(getCachedConcept(MohEvaluableNameConstants.WHO_STAGE_1_PEDS))) {
                this.setProperty("pedsWHOStage", 1);
            } else if (answer.equals(getCachedConcept(MohEvaluableNameConstants.WHO_STAGE_2_PEDS))) {
                this.setProperty("pedsWHOStage", 2);
            } else if (answer.equals(getCachedConcept(MohEvaluableNameConstants.WHO_STAGE_3_PEDS))) {
                this.setProperty("pedsWHOStage", 3);
            } else if (answer.equals(getCachedConcept(MohEvaluableNameConstants.WHO_STAGE_4_PEDS))) {
                this.setProperty("pedsWHOStage", 4);
            }
            return true;
        }
        if (q.equals(getCachedConcept(MohEvaluableNameConstants.HIV_DNA_PCR))) {
            if (answer.equals(getCachedConcept(MohEvaluableNameConstants.POSITIVE))) {
                this.setProperty("HIVDNAPCRPositive", true);
                return true;
            }
        }
        if (q.equals(getCachedConcept(MohEvaluableNameConstants.CD4_BY_FACS))) {
            this.setProperty("cd4ByFacs", value);
            return true;
        }
        if (q.equals(getCachedConcept(MohEvaluableNameConstants.CD4_PERCENT))) {
            this.setProperty("cd4PercentByFacs", value);
            return true;
        }
        return false;
    }

    /* @see PatientSnapshot#eligible() */
    public boolean eligible() {
        MohEvaluableNameConstants.AgeGroup ageGroup= this.getAgeGroup();
        // eligible if under 12 and WHO Stage is 4 or 3 with other factors
        if (!ageGroup.equals(MohEvaluableNameConstants.AgeGroup.ABOVE_TWELVE_YEARS)) {
            if (this.getProperty("pedsWHOStage").equals(4)) {
                this.setProperty("reason", REASON_CLINICAL);
                return true;
            } else if (this.getProperty("pedsWHOStage").equals(3) && (Integer)this.getProperty("cd4ByFacs") < Integer.valueOf(500) && (Integer)this.getProperty("cd4PercentByFacs") < Integer.valueOf(25)) {
                this.setProperty("reason", REASON_CLINICAL_CD4);
                return true;
            }
        }

        // otherwise, check by age group
        if (ageGroup.equals(MohEvaluableNameConstants.AgeGroup.UNDER_EIGHTEEN_MONTHS)) {
            if (this.getProperty("pedsWHOStage").equals(2) && (Integer)this.getProperty("cd4ByFacs") < Integer.valueOf(500) && (Boolean)this.getProperty("HIVDNAPCRPositive")) {
                this.setProperty("reason", REASON_CLINICAL_CD4_HIV_DNA_PCR);
                return true;
            } else if (this.getProperty("pedsWHOStage").equals(1) && (Boolean)this.getProperty("HIVDNAPCRPositive")) {
                this.setProperty("reason", REASON_CLINICAL_HIV_DNA_PCR);
                return true;
            }
        } else if (ageGroup.equals(MohEvaluableNameConstants.AgeGroup.EIGHTEEN_MONTHS_TO_FIVE_YEARS)
                && (this.getProperty("pedsWHOStage").equals(1) || this.getProperty("pedsWHOStage").equals(2)) && (Integer)this.getProperty("cd4PercentByFacs") < Integer.valueOf(20)) {
            this.setProperty("reason", REASON_CLINICAL_CD4);
            return true;
        } else if (ageGroup.equals(MohEvaluableNameConstants.AgeGroup.FIVE_YEARS_TO_TWELVE_YEARS)
                && (this.getProperty("pedsWHOStage").equals(1) || this.getProperty("pedsWHOStage").equals(2)) && (Integer)this.getProperty("cd4PercentByFacs")<Integer.valueOf(25)) {
            this.setProperty("reason", REASON_CLINICAL_CD4);
            return true;
        } else if (ageGroup.equals(MohEvaluableNameConstants.AgeGroup.ABOVE_TWELVE_YEARS)) {
            if ((this.getProperty("pedsWHOStage").equals(1) || this.getProperty("pedsWHOStage").equals(2)) && (Integer)this.getProperty("cd4ByFacs")<Integer.valueOf(350)) {
                this.setProperty("reason", REASON_CLINICAL_CD4);
                return true;
            } else if (this.getProperty("adultWHOStage").equals(4) || this.getProperty("adultWHOStage").equals(3)) {
                this.setProperty("reason", REASON_CLINICAL);
                return true;
            } else if ((this.getProperty("adultWHOStage").equals(1) || this.getProperty("adultWHOStage").equals(2)) && (Integer)this.getProperty("cd4ByFacs")<Integer.valueOf(350)) {
                this.setProperty("reason", REASON_CLINICAL_CD4);
                return true;
            }
        }
        return false;
    }


}
