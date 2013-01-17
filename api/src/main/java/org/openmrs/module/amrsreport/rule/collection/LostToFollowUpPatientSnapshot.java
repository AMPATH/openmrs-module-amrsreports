package org.openmrs.module.amrsreport.rule.collection;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.module.amrsreport.cache.MohCacheUtils;
import org.openmrs.module.amrsreport.rule.MohEvaluableNameConstants;
import org.openmrs.module.amrsreport.rule.observation.PatientSnapshot;
import org.openmrs.module.amrsreport.rule.util.MohRuleUtils;
import org.openmrs.util.OpenmrsUtil;

import java.util.Arrays;
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
		String formattedObsDate = MohRuleUtils.formatdates(o.getObsDatetime());
		String formattedObsValueDate = MohRuleUtils.formatdates(o.getValueDatetime());

		if (concept.equals(MohCacheUtils.getConcept(CONCEPT_DATE_OF_DEATH))) {
			this.setProperty("why", "DEAD");
			this.setProperty("obsDate", formattedObsDate);
			return true;
		} else if (concept.equals(MohCacheUtils.getConcept(CONCEPT_DEATH_REPORTED_BY))) {
			this.setProperty("why", "DEAD");
			this.setProperty("obsDate", formattedObsDate);
			return true;
		} else if (concept.equals(MohCacheUtils.getConcept(CONCEPT_CAUSE_FOR_DEATH))) {
			this.setProperty("why", "DEAD");
			this.setProperty("obsDate", formattedObsDate);
			return true;
		} else if (concept.equals(MohCacheUtils.getConcept(CONCEPT_DECEASED))) {
			this.setProperty("why", "DEAD");
			this.setProperty("obsDate", formattedObsDate);
			return true;
		} else if (concept.equals(MohCacheUtils.getConcept(CONCEPT_PATIENT_DIED))) {
			return true;
		}

		if (concept.equals(MohCacheUtils.getConcept(CONCEPT_TRANSFER_CARE_TO_OTHER_CENTER))) {
			if (answer == MohCacheUtils.getConcept(CONCEPT_AMPATH)) {
				this.setProperty("why", "TO | (Ampath) ");
				this.setProperty("obsDate", formattedObsDate);
			} else {
				this.setProperty("why", "TO | (Non-Ampath) ");
				this.setProperty("obsDate", formattedObsDate);
			}
			return true;
		}

		if (StringUtils.isNotBlank(formattedObsValueDate) &&
				OpenmrsUtil.isConceptInList(concept, Arrays.asList(
						MohCacheUtils.getConcept(MohEvaluableNameConstants.RETURN_VISIT_DATE),
						MohCacheUtils.getConcept(CONCEPT_RETURN_VISIT_DATE_EXP_CARE_NURSE)))) {

			long requiredTimeToShowup = ((o.getValueDatetime().getTime()) - (o.getObsDatetime().getTime())) + MONTHS_3;
			long todayTimeFromSchedule = (new Date()).getTime() - (o.getObsDatetime().getTime());

			if (requiredTimeToShowup < todayTimeFromSchedule) {
				this.setProperty("why", "LTFU");
				this.setProperty("obsDate", formattedObsValueDate);
				return true;
			}
		}

		return encounterConsumed;
	}

	@Override
	public boolean eligible() {

		if (this.getProperty("why").equals("DEAD")) {
			this.setProperty("reason", "DEAD | " + this.getProperty("obsDate"));
			return true;
		} else if (this.getProperty("why").equals("TO | (Ampath) ")) {
			this.setProperty("reason", "TO | (Ampath) " + this.getProperty("obsDate"));
			return true;
		} else if (this.getProperty("why").equals("TO | (Non-Ampath) ")) {
			this.setProperty("reason", "TO | (Non-Ampath) " + this.getProperty("obsDate"));
			return true;
		} else if (this.getProperty("why").equals("LTFU")) {
			this.setProperty("reason", "LTFU | " + this.getProperty("obsDate"));
			return true;
		}

		return false;

	}

	/**
	 * @param e
	 * @return
	 * @should test if a given encounter is consumed
	 */
	public Boolean consume(Encounter e) {
		if (e == null)
			return false;

		String formattedEncounterDatetime = MohRuleUtils.formatdates(e.getEncounterDatetime());

		if (OpenmrsUtil.nullSafeEquals(encTpDeath, e.getEncounterType())) {
			this.setProperty("reason", "DEAD | " + formattedEncounterDatetime);
			return true;
		} else if (OpenmrsUtil.nullSafeEquals(encTpInit, e.getEncounterType()) || OpenmrsUtil.nullSafeEquals(encTpRet, e.getEncounterType())) {
			if (Math.abs(new Date().getTime() - e.getEncounterDatetime().getTime()) < MONTHS_6) {
				this.setProperty("reason", "LTFU | " + formattedEncounterDatetime);
				return true;
			}
		}
		return false;
	}


}