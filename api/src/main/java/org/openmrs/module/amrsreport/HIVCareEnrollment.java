package org.openmrs.module.amrsreport;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Person;

import java.util.Date;

/**
 * Snapshot of patient data useful to reporting queries
 */
public class HIVCareEnrollment extends BaseOpenmrsData {

	Integer enrollmentId;
	Person person;

	Date enrollmentDate;
	Location enrollmentLocation;
	Double enrollmentAge;
	String enrollmentReason;

	Encounter firstHIVEncounter;
	Date firstHIVEncounterDate;
	Location firstHIVEncounterLocation;
	Double firstHIVEncounterAge;

	Date firstPositiveObsDate;
	Location firstPositiveObsLocation;

	Date lastPositiveObsDate;
	Date lastNegativeObsDate;

	Integer lastWHOStage;
	Date lastWHOStageDate;

	Date firstARVDate;

	Date lastDiscontinueDate;

	Boolean transferredIn;
	Date transferredInDate;

	public static final String REASON_ENCOUNTER = "ENCOUNTER";
	public static final String REASON_OBSERVATION = "OBSERVATION";

	@Override
	public Integer getId() {
		return getEnrollmentId();
	}

	@Override
	public void setId(Integer id) {
		setEnrollmentId(id);
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

	public Date getEnrollmentDate() {
		return enrollmentDate;
	}

	public void setEnrollmentDate(Date enrollmentDate) {
		this.enrollmentDate = enrollmentDate;
	}

	public Location getEnrollmentLocation() {
		return enrollmentLocation;
	}

	public void setEnrollmentLocation(Location enrollmentLocation) {
		this.enrollmentLocation = enrollmentLocation;
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

	public Encounter getFirstHIVEncounter() {
		return firstHIVEncounter;
	}

	public void setFirstHIVEncounter(Encounter firstHIVEncounter) {
		this.firstHIVEncounter = firstHIVEncounter;
	}

	public Date getFirstHIVEncounterDate() {
		return firstHIVEncounterDate;
	}

	public void setFirstHIVEncounterDate(Date firstHIVEncounterDate) {
		this.firstHIVEncounterDate = firstHIVEncounterDate;
	}

	public Location getFirstHIVEncounterLocation() {
		return firstHIVEncounterLocation;
	}

	public void setFirstHIVEncounterLocation(Location firstHIVEncounterLocation) {
		this.firstHIVEncounterLocation = firstHIVEncounterLocation;
	}

	public Double getFirstHIVEncounterAge() {
		return firstHIVEncounterAge;
	}

	public void setFirstHIVEncounterAge(Double firstHIVEncounterAge) {
		this.firstHIVEncounterAge = firstHIVEncounterAge;
	}

	public Date getFirstPositiveObsDate() {
		return firstPositiveObsDate;
	}

	public void setFirstPositiveObsDate(Date firstPositiveObsDate) {
		this.firstPositiveObsDate = firstPositiveObsDate;
	}

	public Location getFirstPositiveObsLocation() {
		return firstPositiveObsLocation;
	}

	public void setFirstPositiveObsLocation(Location firstPositiveObsLocation) {
		this.firstPositiveObsLocation = firstPositiveObsLocation;
	}

	public Date getLastPositiveObsDate() {
		return lastPositiveObsDate;
	}

	public void setLastPositiveObsDate(Date lastPositiveObsDate) {
		this.lastPositiveObsDate = lastPositiveObsDate;
	}

	public Date getLastNegativeObsDate() {
		return lastNegativeObsDate;
	}

	public void setLastNegativeObsDate(Date lastNegativeObsDate) {
		this.lastNegativeObsDate = lastNegativeObsDate;
	}

	public Integer getLastWHOStage() {
		return lastWHOStage;
	}

	public void setLastWHOStage(Integer lastWHOStage) {
		this.lastWHOStage = lastWHOStage;
	}

	public Date getLastWHOStageDate() {
		return lastWHOStageDate;
	}

	public void setLastWHOStageDate(Date lastWHOStageDate) {
		this.lastWHOStageDate = lastWHOStageDate;
	}

	public Date getFirstARVDate() {
		return firstARVDate;
	}

	public void setFirstARVDate(Date firstARVDate) {
		this.firstARVDate = firstARVDate;
	}

	public Date getLastDiscontinueDate() {
		return lastDiscontinueDate;
	}

	public void setLastDiscontinueDate(Date lastDiscontinueDate) {
		this.lastDiscontinueDate = lastDiscontinueDate;
	}

	public Boolean getTransferredIn() {
		return transferredIn;
	}

	public void setTransferredIn(Boolean transferredIn) {
		this.transferredIn = transferredIn;
	}

	public Date getTransferredInDate() {
		return transferredInDate;
	}

	public void setTransferredInDate(Date transferredInDate) {
		this.transferredInDate = transferredInDate;
	}
}
