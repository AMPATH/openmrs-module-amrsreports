package org.openmrs.module.amrsreport.rule.observation;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.module.amrsreport.rule.MohEvaluableNameConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: oliver
 * Date: 11/5/12
 * Time: 11:46 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class PatientSnapshot {

    private Map<String,Object> property=new HashMap<String, Object>();

    public Boolean consume(Obs o);

    private boolean eligible(MohEvaluableNameConstants.AgeGroup ageGroup);

    public Map<String, Object> getProperty() {
        return property;
    }

    public void setProperty(Map<String, Object> property) {
        this.property = property;
    }
}
