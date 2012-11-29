package org.openmrs.module.amrsreport.rule.collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.module.amrsreport.rule.observation.PatientSnapshot;

import java.util.Calendar;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: oliver
 * Date: 11/21/12
 * Time: 12:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class MohPregnancyPMTCReferralRuleSnapshot extends PatientSnapshot {

    public static final String TOKEN = "MOH Pregnancy PMTC Referral";

    private static final Log log = LogFactory.getLog(MohPregnancyPMTCReferralRule.class);

    public static final String ESTIMATED_DATE_OF_CONFINEMENT = "ESTIMATED DATE OF CONFINEMENT";

    public static final String ESTIMATED_DATE_OF_CONFINEMENT_ULTRASOUND = "ESTIMATED DATE OF CONFINEMENT, ULTRASOUND";

    public static final String CURRENT_PREGNANT = "CURRENT PREGNANT";

    public static final String NO_OF_WEEK_OF_PREGNANCY = "NO OF WEEK OF PREGNANCY";

    public static final String FUNDAL_LENGTH = "FUNDAL LENGTH";

    public static final String PREGNANCY_URINE_TEST = "PREGNANCY URINE TEST";

    public static final String URGENT_MEDICAL_ISSUES = "URGENT MEDICAL ISSUES";

    public static final String PROBLEM_ADDED = "PROBLEM ADDED";

    public static final String FOETAL_MOVEMENT = "FOETAL MOVEMENT";

    public static final String REASON_FOR_CURRENT_VISIT = "REASON FOR CURRENT VISIT";

    public static final String REASON_FOR_NEXT_VISIT = "REASON FOR NEXT VISIT";

    public static final String YES = "YES";

    public static final String MONTH_OF_CURRENT_GESTATION = "MONTH OF CURRENT GESTATION";

    public static final String POSITIVE = "POSITIVE";

    public static final String PREGNANCY = "PREGNANCY";

    public static final String PREGNANCY_ECTOPIC = "PREGNANCY, ECTOPIC";

    public static final String ANTENATAL_CARE = "ANTENATAL CARE";




    @Override
    public Boolean consume(Obs o) {
        Concept concept = o.getConcept();
        Date valueDatetime = o.getValueDatetime();
        Double valueNumeric = o.getValueNumeric();
        Concept valueCoded = o.getValueCoded();

        // ESTIMATED DATE OF CONFINEMENT (5596)-OBS. DATE)<=42 OR
        if (concept.equals(this.getCachedConcept(ESTIMATED_DATE_OF_CONFINEMENT))) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(o.getObsDatetime());
            calendar.add(Calendar.DATE, 42 * 7);
            if (calendar.getTime().after(valueDatetime))
                return true;
        }

        // ESTIMATED DATE OF CONFINEMENT, ULTRASOUND (6743)-OBS. DATE)<=294 OR
        if (concept.equals(this.getCachedConcept(ESTIMATED_DATE_OF_CONFINEMENT_ULTRASOUND))) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(o.getObsDatetime());
            calendar.add(Calendar.DATE, 294);
            if (calendar.getTime().after(valueDatetime))
                return true;
        }

        // CURRENT PREGNANT (5272)=YES (1065) OR
        if (concept.equals(this.getCachedConcept(CURRENT_PREGNANT))) {
            if (valueCoded.equals(this.getCachedConcept(YES)))
                return true;
        }

        // NO OF WEEK OF PREGNANCY (1279)>0 OR
        if (concept.equals(this.getCachedConcept(NO_OF_WEEK_OF_PREGNANCY))) {
            if (valueNumeric > 0)
                return true;
        }

        // MONTH OF CURRENT GESTATION (5992)>0 OR
        if (concept.equals(this.getCachedConcept(MONTH_OF_CURRENT_GESTATION))) {
            if (valueNumeric > 0)
                return true;
        }

        // FUNDAL LENGTH (1855) >0 OR
        if (concept.equals(this.getCachedConcept(FUNDAL_LENGTH))) {
            if (valueNumeric > 0)
                return true;
        }

        // PREGNANCY URINE TEST (45)=POSITIVE (703) OR
        if (concept.equals(this.getCachedConcept(PREGNANCY_URINE_TEST))) {
            if (valueCoded.equals(this.getCachedConcept(POSITIVE)))
                return true;
        }

        // URGENT MEDICAL ISSUES (1790) = PREGNANCY (44) OR
        if (concept.equals(this.getCachedConcept(URGENT_MEDICAL_ISSUES))) {
            if (valueCoded.equals(this.getCachedConcept(PREGNANCY)))
                return true;
        }

        /*// PROBLEM ADDED (6042) = PREGNANCY, ECTOPIC (46) OR
        if (concept.equals(getCachedConcept(PROBLEM_ADDED))) {
                if (valueCoded.equals(getCachedConcept(PREGNANCY_ECTOPIC)))
                        return true;
        }*/

        // FOETAL MOVEMENT (1856)=YES (1065) OR
        if (concept.equals(this.getCachedConcept(FOETAL_MOVEMENT))) {
            if (valueCoded.equals(this.getCachedConcept(YES)))
                return true;
        }

        // REASON FOR CURRENT VISIT (1834)=ANTENATAL CARE (1831) OR
        if (concept.equals(this.getCachedConcept(REASON_FOR_CURRENT_VISIT))) {
            if (valueCoded.equals(this.getCachedConcept(ANTENATAL_CARE)))
                return true;
        }

        // REASON FOR NEXT VISIT (1835)=ANTENATAL CARE (1831) OR
        if (concept.equals(this.getCachedConcept(REASON_FOR_NEXT_VISIT))) {
            if (valueCoded.equals(this.getCachedConcept(ANTENATAL_CARE)))
                return true;
        }

        return false;
    }

    @Override
    public boolean eligible() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
