package org.openmrs.module.amrsreport.rule.collection;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreport.cache.MohCacheUtils;
import org.openmrs.module.amrsreport.rule.MohEvaluableNameConstants;
import org.openmrs.module.amrsreport.rule.observation.PatientSnapshot;
import org.openmrs.util.OpenmrsUtil;
import org.openmrs.web.OpenmrsBindingInitializer;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Patient snapshot for use with LTFU Rule
 */
public class LostToFollowUpPatientSnapshot extends PatientSnapshot {

	public static final String CONCEPT_DATE_OF_DEATH = "DATE OF DEATH";
	public static final String CONCEPT_DEATH_REPORTED_BY = "DEATH REPORTED BY";
	public static final String CONCEPT_CAUSE_FOR_DEATH = "CAUSE FOR DEATH";
	public static final String CONCEPT_DECEASED = "DECEASED";
	public static final String CONCEPT_PATIENT_DIED = "PATIENT DIED";
	public static final String CONCEPT_TRANSFER_CARE_TO_OTHER_CENTER = "TRANSFER CARE TO OTHER CENTER";
	public static final String CONCEPT_AMPATH = "AMPATH";
	public static final String CONCEPT_RETURN_VISIT_DATE_EXP_CARE_NURSE = "RETURN VISIT DATE, EXPRESS CARE NURSE";

	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");

	private static final EncounterType encTpInit = MohCacheUtils.getEncounterType(MohEvaluableNameConstants.ENCOUNTER_TYPE_ADULT_INITIAL);
	private static final EncounterType encTpRet = MohCacheUtils.getEncounterType(MohEvaluableNameConstants.ENCOUNTER_TYPE_ADULT_RETURN);
	private static final EncounterType encTpDeath = MohCacheUtils.getEncounterType(MohEvaluableNameConstants.ENCOUNTER_TYPE_DEATH_REPORT);

	private static final long MONTHS_3 = 7889400000L; // 1000 * 60 * 60 * 24 * 30.4375 * 3
	private static final long MONTHS_6 = 15778800000L; // 1000 * 60 * 60 * 24 * 30.4375 * 6

	private Encounter lastEncounter = null;

	/**
	 * @param o observation to be consumed
	 * @return
	 * @should find out if a particular Obs is consumed
	 */
	@Override
	public Boolean consume(Obs o) {
		Boolean encounterConsumed = false;
		Encounter encounter = o.getEncounter();
		if (!OpenmrsUtil.nullSafeEquals(encounter, lastEncounter)) {
			lastEncounter = encounter;
			encounterConsumed = this.consume(encounter);
		}

		Concept concept = o.getConcept();
		Concept answer = o.getValueCoded();

		if (concept.equals(MohCacheUtils.getConcept(CONCEPT_DATE_OF_DEATH))) {
			this.setProperty("reason", "DEAD | " + sdf.format(sdf.format(o.getObsDatetime())));
			return true;
		} else if (concept.equals(MohCacheUtils.getConcept(CONCEPT_DEATH_REPORTED_BY))) {
			this.setProperty("reason", "DEAD | " + sdf.format(sdf.format(o.getObsDatetime())));
			return true;
		} else if (concept.equals(MohCacheUtils.getConcept(CONCEPT_CAUSE_FOR_DEATH))) {
			this.setProperty("reason", "DEAD | " + sdf.format(sdf.format(o.getObsDatetime())));
			return true;
		} else if (concept.equals(MohCacheUtils.getConcept(CONCEPT_DECEASED))) {
			this.setProperty("reason", "DEAD | " + sdf.format(sdf.format(o.getObsDatetime())));
			return true;
		} else if (concept.equals(MohCacheUtils.getConcept(CONCEPT_PATIENT_DIED))) {
			this.setProperty("reason", "DEAD | " + sdf.format(o.getObsDatetime()));
			return true;
		}

		if (concept.equals(MohCacheUtils.getConcept(CONCEPT_TRANSFER_CARE_TO_OTHER_CENTER))) {
			if (answer == MohCacheUtils.getConcept(CONCEPT_AMPATH))
				this.setProperty("reason", "TO | (Ampath) " + sdf.format(o.getObsDatetime()));
			else
				this.setProperty("reason", "TO | (Non-Ampath) " + sdf.format(o.getObsDatetime()));

			return true;
		}

		if (concept.equals(MohCacheUtils.getConcept(MohEvaluableNameConstants.RETURN_VISIT_DATE).getConceptId())) {
			if (sdf.format(o.getObsDatetime()) != null) {
				long requiredTimeToShowup = ((o.getValueDatetime().getTime()) - (o.getObsDatetime().getTime())) + MONTHS_3;
				long todayTimeFromSchedule = (new Date()).getTime() - (o.getObsDatetime().getTime());
				if (requiredTimeToShowup < todayTimeFromSchedule) {
					this.setProperty("reason", "LTFU | " + sdf.format(o.getValueDatetime()));
					return true;
				}
			}
		}

		if (concept.equals(MohCacheUtils.getConcept(CONCEPT_RETURN_VISIT_DATE_EXP_CARE_NURSE))) {
			if (sdf.format(o.getObsDatetime()) != null) {
				long requiredTimeToShowup = ((o.getValueDatetime().getTime()) - (o.getObsDatetime().getTime())) + MONTHS_3;
				long todayTimeFromSchedule = (new Date()).getTime() - (o.getObsDatetime().getTime());
				if (requiredTimeToShowup < todayTimeFromSchedule) {
					this.setProperty("reason", "LTFU | " + sdf.format(o.getValueDatetime()));
					return true;
				}
			}
		}

		return encounterConsumed;
	}

	@Override
	public boolean eligible() {
		// todo put the eligibility requirements in here
		return true;
	}

	/**
	 * @param e
	 * @return
	 * @should test if a given encounter is consumed
	 */
	public Boolean consume(Encounter e) {
		if (e == null)
			return false;

		if (OpenmrsUtil.nullSafeEquals(encTpDeath, e.getEncounterType())) {
			this.setProperty("reason", "DEAD | " + sdf.format(e.getEncounterDatetime()));
			return true;
		} else if (OpenmrsUtil.nullSafeEquals(encTpInit, e.getEncounterType()) || OpenmrsUtil.nullSafeEquals(encTpRet, e.getEncounterType())) {
			if (Math.abs(new Date().getTime() - e.getEncounterDatetime().getTime()) < MONTHS_6) {
				this.setProperty("reason", "LTFU | " + sdf.format(e.getEncounterDatetime()));
				return true;
			}
		}
		return false;
	}

}