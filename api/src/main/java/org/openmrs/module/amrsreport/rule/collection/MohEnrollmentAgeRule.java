package org.openmrs.module.amrsreport.rule.collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.OpenmrsObject;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.amrsreport.cache.MohCacheUtils;
import org.openmrs.module.amrsreport.rule.MohEvaluableNameConstants;
import org.openmrs.module.amrsreport.rule.MohEvaluableRule;
import org.openmrs.module.amrsreport.service.MohCoreService;
import org.openmrs.module.amrsreport.util.MohFetchOrdering;
import org.openmrs.module.amrsreport.util.MohFetchRestriction;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Author jmwogi
 */
public class MohEnrollmentAgeRule extends MohEvaluableRule {

	private static final Log log = LogFactory.getLog(MohEnrollmentAgeRule.class);
	private static final long ageDivisor = 31557600000L; // 1000 * 60 * 60 * 24 * 30.4375 * 12
	public static final String TOKEN = "MOH Age At Enrollment";

	private static final EncounterType adultInitialType = MohCacheUtils.getEncounterType(MohEvaluableNameConstants.ENCOUNTER_TYPE_ADULT_INITIAL);
	private static final EncounterType adultReturnType = MohCacheUtils.getEncounterType(MohEvaluableNameConstants.ENCOUNTER_TYPE_ADULT_RETURN);

	private static final MohCoreService mohCoreService = Context.getService(MohCoreService.class);

	/**
	 * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, org.openmrs.Patient,
	 *      java.util.Map)
	 */
	public Result evaluate(LogicContext context, Integer patientId, Map<String, Object> parameters) throws LogicException {

		try {

			Patient patient = Context.getPatientService().getPatient(patientId);

			if (patient == null)
				return null;

			if (patient.getBirthdate() == null)
				return new Result(MohEvaluableNameConstants.UNKNOWN);

			Map<String, Collection<OpenmrsObject>> restrictions = new HashMap<String, Collection<OpenmrsObject>>();
			MohFetchRestriction mohFetchRestriction = new MohFetchRestriction();
			mohFetchRestriction.setFetchOrdering(MohFetchOrdering.ORDER_ASCENDING);
			List<Encounter> e = mohCoreService.getPatientEncounters(patientId, restrictions, mohFetchRestriction);

			//Iterate though encounters for the patient
			Date encounterDate = null;
			Boolean isChild = true;
			Iterator<Encounter> it = e.iterator();
			while (it.hasNext() && isChild) {
				Encounter encounter = it.next();
				encounterDate = encounter.getEncounterDatetime();
				if (encounter.getEncounterType().equals(adultInitialType) || encounter.getEncounterType().equals(adultReturnType))
					isChild = false;
			}

			//Get age in years
			if (encounterDate != null) {
				Double ageInYears = (double) (encounterDate.getTime() - patient.getBirthdate().getTime()) / ageDivisor;
				if (isChild && ageInYears < 1) {
					return new Result(((int) Math.floor(ageInYears * 12)) + "m");
				}
				return new Result(((int) Math.floor(ageInYears)) + "y");
			}
		} catch (Exception e) {
			// TODO log something here?
		}

		return new Result("");
	}

	protected String getEvaluableToken() {
		return TOKEN;
	}

	/**
	 * @see org.openmrs.logic.Rule#getDependencies()
	 */
	@Override
	public String[] getDependencies() {
		return new String[]{};
	}

	/**
	 * Get the definition of each parameter that should be passed to this rule execution
	 *
	 * @return all parameter that applicable for each rule execution
	 */
	@Override
	public Datatype getDefaultDatatype() {
		return Datatype.TEXT;
	}

	public Set<RuleParameterInfo> getParameterList() {
		return null;
	}

	@Override
	public int getTTL() {
		return 0;
	}

}