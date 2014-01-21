package org.openmrs.module.amrsreports.model;

import java.util.Date;
import java.util.Set;

/**
 * Holds evaluation data for tb treatment start date data evaluator
 */
public class PatientTBTreatmentData {

    private Set<Date> evaluationDates;
    private String tbRegNO;

    public PatientTBTreatmentData() {
    }

    public PatientTBTreatmentData(Set<Date> evaluationDates, String tbRegNO) {

        this.evaluationDates = evaluationDates;
        this.tbRegNO = tbRegNO;
    }

    public Set<Date> getEvaluationDates() {
        return evaluationDates;
    }

    public void setEvaluationDates(Set<Date> evaluationDates) {
        this.evaluationDates = evaluationDates;
    }

    public String getTbRegNO() {
        return tbRegNO;
    }

    public void setTbRegNO(String tbRegNO) {
        this.tbRegNO = tbRegNO;
    }
}
