package org.openmrs.module.amrsreports;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Person;

import java.util.Date;

/**
 * Snapshot of patient data useful to reporting queries
 */
public class HIVCareEnrollment extends BaseOpenmrsData {

	Integer enrollmentId;
	Patient patient;

	Date enrollmentDate;
	Location enrollmentLocation;
	Double enrollmentAge;
	String enrollmentReason;

	Encounter firstHIVEncounter;
	Date firstHIVEncounterDate;
	Location firstHIVEncounterLocation;
	Double firstHIVEncounterAge;

	Date lastHIVEncounterDate;
	Location lastHIVEncounterLocation;

	Date firstPositiveObsDate;
	Location firstPositiveObsLocation;

	Date lastPositiveObsDate;
	Date lastNegativeObsDate;

	String lastWHOStage;
	Date lastWHOStageDate;

	Date firstARVDate;
	Location firstARVLocation;

	Date lastDiscontinueDate;
	String lastDiscontinueReason;

	Date transferredInDate;

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

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
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

	public Date getLastHIVEncounterDate() {
		return lastHIVEncounterDate;
	}

	public void setLastHIVEncounterDate(Date lastHIVEncounterDate) {
		this.lastHIVEncounterDate = lastHIVEncounterDate;
	}

	public Location getLastHIVEncounterLocation() {
		return lastHIVEncounterLocation;
	}

	public void setLastHIVEncounterLocation(Location lastHIVEncounterLocation) {
		this.lastHIVEncounterLocation = lastHIVEncounterLocation;
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

	public String getLastWHOStage() {
		return lastWHOStage;
	}

	public void setLastWHOStage(String lastWHOStage) {
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

	public Location getFirstARVLocation() {
		return firstARVLocation;
	}

	public void setFirstARVLocation(Location firstARVLocation) {
		this.firstARVLocation = firstARVLocation;
	}

	public Date getLastDiscontinueDate() {
		return lastDiscontinueDate;
	}

	public void setLastDiscontinueDate(Date lastDiscontinueDate) {
		this.lastDiscontinueDate = lastDiscontinueDate;
	}

	public String getLastDiscontinueReason() {
		return lastDiscontinueReason;
	}

	public void setLastDiscontinueReason(String lastDiscontinueReason) {
		this.lastDiscontinueReason = lastDiscontinueReason;
	}

	public Date getTransferredInDate() {
		return transferredInDate;
	}

	public void setTransferredInDate(Date transferredInDate) {
		this.transferredInDate = transferredInDate;
	}
}
