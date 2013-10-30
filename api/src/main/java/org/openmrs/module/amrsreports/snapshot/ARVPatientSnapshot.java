package org.openmrs.module.amrsreports.snapshot;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.module.amrsreports.cache.MohCacheUtils;
import org.openmrs.module.amrsreports.reporting.common.ObsRepresentation;
import org.openmrs.module.amrsreports.rule.MohEvaluableNameConstants;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

/**
 * Patient snapshot for determining eligibility for ART
 */
public class ARVPatientSnapshot extends PatientSnapshot {

	public static final String REASON_CLINICAL = "Clinical Only";
	public static final String REASON_CLINICAL_CD4 = "Clinical + CD4";
	public static final String REASON_CLINICAL_CD4_HIV_DNA_PCR = "Clinical + CD4 + HIV DNA PCR";
	public static final String REASON_CLINICAL_HIV_DNA_PCR = "Clinical + HIV DNA PCR";

	public ARVPatientSnapshot() {
		this.set("pedsWHOStage", 0);
		this.set("adultWHOStage", 0);
		this.set("cd4ByFacs", Double.MAX_VALUE);
		this.set("cd4PercentByFacs", Double.MAX_VALUE);
		this.set("HIVDNAPCRPositive", false);
	}

	@Override
	public Boolean consume(Obs o) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	/**
	 * @see PatientSnapshot#consume(java.util.Map)
	 */
	@Override
	public Boolean consume(ObsRepresentation o) {
		Integer q = o.getConceptId();
		Integer answer = o.getValueCodedId();
		Double value = o.getValueNumeric();
		Date date = o.getObsDatetime();

		if (q == null || (getEvaluationDate() != null && date.after(getEvaluationDate())))
			return false;

		if (q.equals(MohCacheUtils.getConceptId(MohEvaluableNameConstants.WHO_STAGE_ADULT))) {
			if (answer == null)
				return false;
			if (answer.equals(MohCacheUtils.getConceptId(MohEvaluableNameConstants.WHO_STAGE_1_ADULT))) {
				this.set("adultWHOStage", 1);
			} else if (answer.equals(MohCacheUtils.getConceptId(MohEvaluableNameConstants.WHO_STAGE_2_ADULT))) {
				this.set("adultWHOStage", 2);
			} else if (answer.equals(MohCacheUtils.getConceptId(MohEvaluableNameConstants.WHO_STAGE_3_ADULT))) {
				this.set("adultWHOStage", 3);
			} else if (answer.equals(MohCacheUtils.getConceptId(MohEvaluableNameConstants.WHO_STAGE_4_ADULT))) {
				this.set("adultWHOStage", 4);
			}
			this.set("lastDate", date);
			return true;
		}
		if (q.equals(MohCacheUtils.getConceptId(MohEvaluableNameConstants.WHO_STAGE_PEDS))) {
			if (answer == null)
				return false;
			if (answer.equals(MohCacheUtils.getConceptId(MohEvaluableNameConstants.WHO_STAGE_1_PEDS))) {
				this.set("pedsWHOStage", 1);
			} else if (answer.equals(MohCacheUtils.getConceptId(MohEvaluableNameConstants.WHO_STAGE_2_PEDS))) {
				this.set("pedsWHOStage", 2);
			} else if (answer.equals(MohCacheUtils.getConceptId(MohEvaluableNameConstants.WHO_STAGE_3_PEDS))) {
				this.set("pedsWHOStage", 3);
			} else if (answer.equals(MohCacheUtils.getConceptId(MohEvaluableNameConstants.WHO_STAGE_4_PEDS))) {
				this.set("pedsWHOStage", 4);
			}
			this.set("lastDate", date);
			return true;
		}
		if (q.equals(MohCacheUtils.getConceptId(MohEvaluableNameConstants.HIV_DNA_PCR))) {
			if (answer == null)
				return false;
			if (answer.equals(MohCacheUtils.getConceptId(MohEvaluableNameConstants.POSITIVE))) {
				this.set("HIVDNAPCRPositive", true);
				this.set("lastDate", date);
				return true;
			}
		}
		if (q.equals(MohCacheUtils.getConceptId(MohEvaluableNameConstants.CD4_BY_FACS))) {
			if (value == null)
				return false;
			this.set("cd4ByFacs", value);
			this.set("lastDate", date);
			return true;
		}
		if (q.equals(MohCacheUtils.getConceptId(MohEvaluableNameConstants.CD4_PERCENT))) {
			if (value == null)
				return false;
			this.set("cd4PercentByFacs", value);
			this.set("lastDate", date);
			return true;
		}
		return false;
	}

	/**
	 * @should set reason to Clinical if under 12 and PEDS WHO Stage is 4
	 * @should set reason to Clinical if over 12 and ADULT WHO Stage is 3 or 4
	 * @should set reason to CD4 if under 12 and PEDS WHO Stage is 3 and CD4 is under 500 and CD4 percentage is under 25
	 * @should set reason to CD4 if between 5 years and 12 years and PEDS WHO Stage is 1 or 2 and CD4 percentage is under
	 * 25
	 * @should set reason to CD4 if over 12 and ADULT or PEDS WHO Stage is 1 or 2 and CD4 is under 350
	 * @should set reason to HIV DNA PCR if under 18 months and PEDS WHO Stage is 1 and HIV DNA PCR is positive
	 * @should set reason to CD4 and HIV DNA PCR if under 18 months and PEDS WHO Stage is 2 and CD4 is under 500 and HIV
	 * DNA PCR is positive
	 * @should set reason to CD4 and HIV DNA PCR if between 18 months and 5 years and PEDS WHO Stage is 1 or 2 and CD4 is
	 * under 500 and HIV DNA PCR is positive
	 * @see PatientSnapshot#eligible()
	 */
	public boolean eligible() {
		MohEvaluableNameConstants.AgeGroup ageGroup = this.getAgeGroup();
		// eligible if under 12 and WHO Stage is 4 or 3 with other factors
		if (!ageGroup.equals(MohEvaluableNameConstants.AgeGroup.ABOVE_TWELVE_YEARS)) {
			if (this.get("pedsWHOStage").equals(4)) {
				this.set("reason", REASON_CLINICAL);
				this.set("extras", Collections.singletonList("WHO Stage 4"));
				return true;

			} else if (this.get("pedsWHOStage").equals(3)
					&& (Double) this.get("cd4ByFacs") < 500d
					&& (Double) this.get("cd4PercentByFacs") < 25d) {
				this.set("reason", REASON_CLINICAL_CD4);
				this.set("extras", Arrays.asList(
						"WHO Stage 3",
						String.format("CD4 Count: %.0f", this.get("cd4ByFacs")),
						String.format("CD4 %%: %.0f", this.get("cd4PercentByFacs"))
				));
				return true;
			}
		}

		// otherwise, check by age group
		if (ageGroup.equals(MohEvaluableNameConstants.AgeGroup.UNDER_EIGHTEEN_MONTHS)) {
			if (this.get("pedsWHOStage").equals(2)
					&& (Double) this.get("cd4ByFacs") < 500d
					&& (Boolean) this.get("HIVDNAPCRPositive")) {
				this.set("reason", REASON_CLINICAL_CD4_HIV_DNA_PCR);
				this.set("extras", Arrays.asList(
						"WHO Stage 2",
						String.format("CD4 Count: %.0f", this.get("cd4ByFacs")),
						"HIV DNA PCR: Positive"
				));
				return true;

			} else if (this.get("pedsWHOStage").equals(1) && (Boolean) this.get("HIVDNAPCRPositive")) {
				this.set("reason", REASON_CLINICAL_HIV_DNA_PCR);
				this.set("extras", Arrays.asList(
						"WHO Stage 1",
						"HIV DNA PCR: Positive"
				));
				return true;
			}

		} else if (ageGroup.equals(MohEvaluableNameConstants.AgeGroup.EIGHTEEN_MONTHS_TO_FIVE_YEARS)
				&& (this.get("pedsWHOStage").equals(1) || this.get("pedsWHOStage").equals(2))
				&& (Double) this.get("cd4PercentByFacs") < 20d) {
			this.set("reason", REASON_CLINICAL_CD4);
			this.set("extras", Arrays.asList(
					String.format("WHO Stage %d", this.get("pedsWHOStage")),
					String.format("CD4 %%: %.0f", this.get("cd4PercentByFacs"))
			));
			return true;

		} else if (ageGroup.equals(MohEvaluableNameConstants.AgeGroup.FIVE_YEARS_TO_TWELVE_YEARS)
				&& (this.get("pedsWHOStage").equals(1) || this.get("pedsWHOStage").equals(2))
				&& (Double) this.get("cd4PercentByFacs") < 25d) {
			this.set("reason", REASON_CLINICAL_CD4);
			this.set("extras", Arrays.asList(
					String.format("WHO Stage %d", this.get("pedsWHOStage")),
					String.format("CD4 %%: %.0f", this.get("cd4PercentByFacs"))
			));
			return true;

		} else if (ageGroup.equals(MohEvaluableNameConstants.AgeGroup.ABOVE_TWELVE_YEARS)) {
			if ((this.get("pedsWHOStage").equals(1) || this.get("pedsWHOStage").equals(2))
					&& (Double) this.get("cd4ByFacs") < 350d) {
				this.set("reason", REASON_CLINICAL_CD4);
				this.set("extras", Arrays.asList(
						String.format("WHO Stage %d", this.get("pedsWHOStage")),
						String.format("CD4 Count: %.0f", this.get("cd4ByFacs"))
				));
				return true;

			} else if ((this.get("adultWHOStage").equals(1) || this.get("adultWHOStage").equals(2))
					&& (Double) this.get("cd4ByFacs") < 350d) {
				this.set("reason", REASON_CLINICAL_CD4);
				this.set("extras", Arrays.asList(
						String.format("WHO Stage %d", this.get("adultWHOStage")),
						String.format("CD4 Count: %.0f", this.get("cd4ByFacs"))
				));
				return true;

			} else if (this.get("adultWHOStage").equals(4) || this.get("adultWHOStage").equals(3)) {
				this.set("reason", REASON_CLINICAL);
				this.set("extras", Collections.singletonList(
						String.format("WHO Stage %d", this.get("adultWHOStage"))));
				return true;
			}
		}
		return false;
	}

}
