package org.openmrs.module.amrsreports.snapshot;

import org.openmrs.Obs;
import org.openmrs.module.amrsreports.reporting.common.ObsRepresentation;
import org.openmrs.module.amrsreports.rule.MohEvaluableNameConstants;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * An abstract class with flags to identify factors for eligibility on a patient
 */
public abstract class PatientSnapshot {

	private MohEvaluableNameConstants.AgeGroup ageGroup;
	private Map<String, Object> properties;
	private Date evaluationDate;

	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}

	public Map<String, Object> getProperties() {
		if (this.properties == null) {
			this.properties = new HashMap<String, Object>();
		}
		return this.properties;
	}

	/**
	 * @param key
	 * @return
	 * @should get value of property with a given key
	 */
	public Object get(String key) {
		return getProperties().get(key);
	}

	public Boolean hasProperty(String key) {
		return getProperties().containsKey(key);
	}

	/**
	 * @param key
	 * @param value
	 * @return
	 * @should create a property and sets its value
	 */
	public Object set(String key, Object value) {
		return this.getProperties().put(key, value);
	}

	public MohEvaluableNameConstants.AgeGroup getAgeGroup() {
		return ageGroup;
	}

	public void setAgeGroup(MohEvaluableNameConstants.AgeGroup ageGroup) {
		this.ageGroup = ageGroup;
	}

	public Date getEvaluationDate() {
		return evaluationDate;
	}

	public void setEvaluationDate(Date evaluationDate) {
		this.evaluationDate = evaluationDate;
	}

	/**
	 * set flags based on observation values. if a flag is set, return true.  otherwise, false.
	 *
	 * @param o observation to be consumed
	 * @return whether a flag was set
	 * @should recognize and set WHO stage from an obs or specify peds WHO
	 */
	public abstract Boolean consume(Obs o);

	/**
	 * set flags based on observation values. if a flag is set, return true.  otherwise, false.
	 *
	 * @param o observation to be consumed
	 * @return whether a flag was set
	 * @should recognize and set WHO stage from an obs or specify peds WHO
	 */
	public abstract Boolean consume(ObsRepresentation o);

	/**
	 * determine eligibility based on age group and flags
	 *
	 * @return eligibility
	 * @should determine eligibility based on age group and flags
	 */
	public abstract boolean eligible();


}
