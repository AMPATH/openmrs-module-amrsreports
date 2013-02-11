package org.openmrs.module.amrsreport;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.Location;
import org.openmrs.Person;

import java.util.Date;

/**
 * Snapshot of patient data useful to reporting queries
 */
public class Enrollment extends BaseOpenmrsData {

	Integer enrollmentId;
	Person person;
	Location location;
	Date enrollmentDate;
	Double enrollmentAge;
	String enrollmentReason;
	Date lastNegativeDate;
	Date lastPositiveDate;

	public static final String REASON_ENCOUNTER = "ENCOUNTER";
	public static final String REASON_OBSERVATION = "OBSERVATION";

	@Override
	public Integer getId() {
		return enrollmentId;
	}

	@Override
	public void setId(Integer id) {
		enrollmentId = id;
	}

	public Integer getEnrollmentId() {
		return enrollmentId;
	}

	public void setEnrollmentId(Integer enrollmentId) {
		this.enrollmentId = enrollmentId;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public Date getEnrollmentDate() {
		return enrollmentDate;
	}

	public void setEnrollmentDate(Date enrollmentDate) {
		this.enrollmentDate = enrollmentDate;
	}

	public Double getEnrollmentAge() {
		return enrollmentAge;
	}

	public void setEnrollmentAge(Double enrollmentAge) {
		this.enrollmentAge = enrollmentAge;
	}

	public String getEnrollmentReason() {
		return enrollmentReason;
	}

	public void setEnrollmentReason(String enrollmentReason) {
		this.enrollmentReason = enrollmentReason;
	}

	public Date getLastNegativeDate() {
		return lastNegativeDate;
	}

	public void setLastNegativeDate(Date lastNegativeDate) {
		this.lastNegativeDate = lastNegativeDate;
	}

	public Date getLastPositiveDate() {
		return lastPositiveDate;
	}

	public void setLastPositiveDate(Date lastPositiveDate) {
		this.lastPositiveDate = lastPositiveDate;
	}
}
