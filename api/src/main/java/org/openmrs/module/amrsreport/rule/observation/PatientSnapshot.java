package org.openmrs.module.amrsreport.rule.observation;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreport.rule.MohEvaluableNameConstants;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * An abstract class with flags to identify factors for eligibility on a patient
 */
public abstract class PatientSnapshot {

    private MohEvaluableNameConstants.AgeGroup ageGroup;

    private Properties properties;
    private Map<String, Concept> cachedConcepts = null;

    public Object getProperty(String key) {
        return properties.get(key);
    }

    public Object addProperty(String key,Object value) {
        return this.properties.put(key,value);
    }

    public MohEvaluableNameConstants.AgeGroup getAgeGroup() {
        return ageGroup;
    }

    public void setAgeGroup(MohEvaluableNameConstants.AgeGroup ageGroup) {
        this.ageGroup = ageGroup;
    }

    /**
     * maintains a cache of concepts and stores them by name
     *
     * @param name the name of the cached concept to retrieve
     * @return the concept matching the name
     */
    public Concept getCachedConcept(String name) {
        if (cachedConcepts == null) {
            cachedConcepts = new HashMap<String, Concept>();
        }
        if (!cachedConcepts.containsKey(name)) {
            cachedConcepts.put(name, Context.getConceptService().getConcept(name));
        }
        return cachedConcepts.get(name);
    }

    public abstract Boolean consume(Obs o);

    public abstract boolean eligible(MohEvaluableNameConstants.AgeGroup ageGroup);


}
