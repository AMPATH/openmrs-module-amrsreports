package org.openmrs.module.amrsreports.snapshot;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.module.amrsreports.AmrsReportsConstants;
import org.openmrs.module.amrsreports.cache.MohCacheUtils;
import org.openmrs.module.amrsreports.rule.MohEvaluableNameConstants;
import org.openmrs.module.amrsreports.util.MOHReportUtil;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Patient snapshot for determining eligibility for ART
 */
public class DateARTStartedPatientSnapshot extends PatientSnapshot {

	public DateARTStartedPatientSnapshot() {
		this.set("result", null);
	}

	private static final List<Concept> excludedReasons = Arrays.<Concept>asList(
			MohCacheUtils.getConcept(MohEvaluableNameConstants.TOTAL_MATERNAL_TO_CHILD_TRANSMISSION_PROPHYLAXIS),
			MohCacheUtils.getConcept(MohEvaluableNameConstants.PREVENTION_OF_MOTHER_TO_CHILD_TRANSMISSION_OF_HIV)
	);

	private static final List<Concept> excludedNewbornARVs = Arrays.<Concept>asList(
			MohCacheUtils.getConcept(MohEvaluableNameConstants.STAVUDINE),
			MohCacheUtils.getConcept(MohEvaluableNameConstants.LAMIVUDINE),
			MohCacheUtils.getConcept(MohEvaluableNameConstants.NEVIRAPINE),
			MohCacheUtils.getConcept(MohEvaluableNameConstants.NELFINAVIR),
			MohCacheUtils.getConcept("LOPINAVIR AND RITONAVIR"),
			MohCacheUtils.getConcept(MohEvaluableNameConstants.ZIDOVUDINE),
			MohCacheUtils.getConcept(MohEvaluableNameConstants.OTHER_NON_CODED)
	);

	private static final List<Concept> allowedAnswers = Arrays.<Concept>asList(
			MohCacheUtils.getConcept(MohEvaluableNameConstants.START_DRUGS),
			MohCacheUtils.getConcept(MohEvaluableNameConstants.CONTINUE_REGIMEN),
			MohCacheUtils.getConcept(MohEvaluableNameConstants.CHANGE_FORMULATION),
			MohCacheUtils.getConcept(MohEvaluableNameConstants.CHANGE_REGIMEN),
			MohCacheUtils.getConcept(MohEvaluableNameConstants.REFILLED),
			MohCacheUtils.getConcept(MohEvaluableNameConstants.NOT_REFILLED),
			MohCacheUtils.getConcept(MohEvaluableNameConstants.DRUG_SUBSTITUTION),
			MohCacheUtils.getConcept(MohEvaluableNameConstants.DRUG_RESTART),
			MohCacheUtils.getConcept(MohEvaluableNameConstants.DOSING_CHANGE)
	);

	/**
	 * @see org.openmrs.module.amrsreports.snapshot.PatientSnapshot#consume(org.openmrs.Obs)
	 */
	public Boolean consume(Obs o) {
		Concept q = o.getConcept();
		Concept answer = o.getValueCoded();
		Double value = o.getValueNumeric();

		if (MOHReportUtil.compareConceptToName(q, MohEvaluableNameConstants.REASON_ANTIRETROVIRALS_STARTED)
				&& OpenmrsUtil.isConceptInList(answer, excludedReasons)) {
			this.set("result", AmrsReportsConstants.EXCLUDED);
			return true;
		}

		if (MOHReportUtil.compareConceptToName(q, MohEvaluableNameConstants.PATIENT_REPORTED_REASON_FOR_CURRENT_ANTIRETROVIRALS_STARTED)
				&& MOHReportUtil.compareConceptToName(answer, MohEvaluableNameConstants.PREVENTION_OF_MOTHER_TO_CHILD_TRANSMISSION_OF_HIV)) {
			this.set("result", AmrsReportsConstants.EXCLUDED);
			return true;
		}

		if (MOHReportUtil.compareConceptToName(q, MohEvaluableNameConstants.NEWBORN_PROPHYLACTIC_ANTIRETROVIRAL_USE)
				&& MOHReportUtil.compareConceptToName(answer, "TRUE")) {
			this.set("result", AmrsReportsConstants.EXCLUDED);
			return true;
		}

		if (MOHReportUtil.compareConceptToName(q, MohEvaluableNameConstants.NEWBORN_ANTIRETROVIRAL_USE)
				&& OpenmrsUtil.isConceptInList(answer, excludedNewbornARVs)) {
			this.set("result", AmrsReportsConstants.EXCLUDED);
			return true;
		}

		if (MOHReportUtil.compareConceptToName(q, MohEvaluableNameConstants.ANTIRETROVIRAL_DRUG_TREATMENT_START_DATE)) {
			this.set("result", MOHReportUtil.formatdates(o.getValueDatetime()));
			return true;
		}

		if (MOHReportUtil.compareConceptToName(q, MohEvaluableNameConstants.ANTIRETROVIRAL_PLAN)
				&& OpenmrsUtil.isConceptInList(answer, allowedAnswers)) {
			this.set("result", MOHReportUtil.formatdates(o.getObsDatetime()));
			return true;
		}

		return false;
	}

	public boolean eligible() {
		return StringUtils.hasText(this.get("result").toString());
	}

}
