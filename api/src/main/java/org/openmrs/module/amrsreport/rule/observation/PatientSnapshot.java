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

    public void setProperties(Properties properties) {

        this.properties = properties;
    }

    public Properties getProperties() {
        if(this.properties==null){
            this.properties= new Properties();
        }
        return this.properties;
    }

    private Properties properties;
    private Map<String, Concept> cachedConcepts = null;

    /**
     * @should get value of property with a given key
     * @param key
     * @return
     */
    public Object getProperty(String key) {
        return properties.get(key);
    }

    /**
     * @should create a property and sets its value
     * @param key
     * @param value
     * @return
     */
    public Object setProperty(String key, Object value) {
        return this.getProperties().put(key,value);
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

    /**
     * set flags based on observation values. if a flag is set, return true.  otherwise, false.
     * @should recognize and set WHO stage from an obs or specify peds WHO
     * @param o observation to be consumed
     * @return whether a flag was set
     */
    public abstract Boolean consume(Obs o);

    /**
     * determine eligibility based on age group and flags
     * @should determine eligibility based on age group and flags
     * @return eligibility
     */
    public abstract boolean eligible();


}
