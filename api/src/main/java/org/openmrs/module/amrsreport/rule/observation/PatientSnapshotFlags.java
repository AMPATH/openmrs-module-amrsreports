package org.openmrs.module.amrsreport.rule.observation;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreport.rule.MohEvaluableNameConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: oliver
 * Date: 11/5/12
 * Time: 12:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class PatientSnapshotFlags extends PatientSnapshot {

   /* private Integer pedsWHOStage = 0;
    private Integer adultWHOStage = 0;
    private Double cd4ByFacs = Double.MAX_VALUE;
    private Double cd4PercentByFacs = Double.MAX_VALUE;
    private Boolean HIVDNAPCRPositive = false;
    private String reason = "";*/
   private Map<String, Concept> cachedConcepts = null;

    /**
     * set flags based on observation values. if a flag is set, return true.  otherwise, false.
     *
     * @param o observation to be consumed
     * @return whether a flag was set
     */
    public Boolean consume(Obs o) {
        Concept q = o.getConcept();
        Concept answer = o.getValueCoded();
        Double value = o.getValueNumeric();

        if (q.equals(getCachedConcept(MohEvaluableNameConstants.WHO_STAGE_ADULT))) {
            if (answer.equals(getCachedConcept(MohEvaluableNameConstants.WHO_STAGE_1_ADULT))) {
                this.setProperty("adultWHOStage",);
                //adultWHOStage = 1;
            } else if (answer.equals(getCachedConcept(MohEvaluableNameConstants.WHO_STAGE_2_ADULT))) {
                adultWHOStage = 2;
            } else if (answer.equals(getCachedConcept(MohEvaluableNameConstants.WHO_STAGE_3_ADULT))) {
                adultWHOStage = 3;
            } else if (answer.equals(getCachedConcept(MohEvaluableNameConstants.WHO_STAGE_4_ADULT))) {
                adultWHOStage = 4;
            }
            return true;
        }
        if (q.equals(getCachedConcept(MohEvaluableNameConstants.WHO_STAGE_PEDS))) {
            if (answer.equals(getCachedConcept(MohEvaluableNameConstants.WHO_STAGE_1_PEDS))) {
                pedsWHOStage = 1;
            } else if (answer.equals(getCachedConcept(MohEvaluableNameConstants.WHO_STAGE_2_PEDS))) {
                pedsWHOStage = 2;
            } else if (answer.equals(getCachedConcept(MohEvaluableNameConstants.WHO_STAGE_3_PEDS))) {
                pedsWHOStage = 3;
            } else if (answer.equals(getCachedConcept(MohEvaluableNameConstants.WHO_STAGE_4_PEDS))) {
                pedsWHOStage = 4;
            }
            return true;
        }
        if (q.equals(getCachedConcept(MohEvaluableNameConstants.HIV_DNA_PCR))) {
            if (answer.equals(getCachedConcept(MohEvaluableNameConstants.POSITIVE))) {
                HIVDNAPCRPositive = true;
                return true;
            }
        }
        if (q.equals(getCachedConcept(MohEvaluableNameConstants.CD4_BY_FACS))) {
            cd4ByFacs = value;
            return true;
        }
        if (q.equals(getCachedConcept(MohEvaluableNameConstants.CD4_PERCENT))) {
            cd4PercentByFacs = value;
            return true;
        }
        return false;
    }

    /**
     * determine eligibility based on age group and flags
     *
     * @param ageGroup the age group to check against
     * @return eligibility
     */
    private boolean eligible(MohEvaluableNameConstants.AgeGroup ageGroup) {
        // eligible if under 12 and WHO Stage is 4 or 3 with other factors
        if (!ageGroup.equals(MohEvaluableNameConstants.AgeGroup.ABOVE_TWELVE_YEARS)) {
            if (pedsWHOStage.equals(4)) {
                reason = REASON_CLINICAL;
                return true;
            } else if (pedsWHOStage.equals(3) && cd4ByFacs < 500 && cd4PercentByFacs < 25) {
                reason = REASON_CLINICAL_CD4;
                return true;
            }
        }

        // otherwise, check by age group
        if (ageGroup.equals(MohEvaluableNameConstants.AgeGroup.UNDER_EIGHTEEN_MONTHS)) {
            if (pedsWHOStage.equals(2) && cd4ByFacs < 500 && HIVDNAPCRPositive) {
                reason = REASON_CLINICAL_CD4_HIV_DNA_PCR;
                return true;
            } else if (pedsWHOStage.equals(1) && HIVDNAPCRPositive) {
                reason = REASON_CLINICAL_HIV_DNA_PCR;
                return true;
            }
        } else if (ageGroup.equals(MohEvaluableNameConstants.AgeGroup.EIGHTEEN_MONTHS_TO_FIVE_YEARS)
                && (pedsWHOStage.equals(1) || pedsWHOStage.equals(2)) && cd4PercentByFacs < 20) {
            reason = REASON_CLINICAL_CD4;
            return true;
        } else if (ageGroup.equals(MohEvaluableNameConstants.AgeGroup.FIVE_YEARS_TO_TWELVE_YEARS)
                && (pedsWHOStage.equals(1) || pedsWHOStage.equals(2)) && cd4PercentByFacs < 25) {
            reason = REASON_CLINICAL_CD4;
            return true;
        } else if (ageGroup.equals(MohEvaluableNameConstants.AgeGroup.ABOVE_TWELVE_YEARS)) {
            if ((pedsWHOStage.equals(1) || pedsWHOStage.equals(2)) && cd4ByFacs < 350) {
                reason = REASON_CLINICAL_CD4;
                return true;
            } else if (adultWHOStage.equals(4) || adultWHOStage.equals(3)) {
                reason = REASON_CLINICAL;
                return true;
            } else if ((adultWHOStage.equals(1) || adultWHOStage.equals(2)) && cd4ByFacs < 350) {
                reason = REASON_CLINICAL_CD4;
                return true;
            }
        }
        return false;
    }

    /**
     * maintains a cache of concepts and stores them by name
     *
     * @param name the name of the cached concept to retrieve
     * @return the concept matching the name
     */
    private Concept getCachedConcept(String name) {
        if (cachedConcepts == null) {
            cachedConcepts = new HashMap<String, Concept>();
        }
        if (!cachedConcepts.containsKey(name)) {
            cachedConcepts.put(name, Context.getConceptService().getConcept(name));
        }
        return cachedConcepts.get(name);
    }
}
