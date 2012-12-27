package org.openmrs.module.amrsreport.rule.collection;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.amrsreport.rule.MohEvaluableRule;

import java.util.Map;
import java.util.Set;

/**
 * MohNamesRule class returns a patient's full names
 */
public class MohNamesRule extends MohEvaluableRule {

	private static final Log log = LogFactory.getLog(MohNamesRule.class);
	public static final String TOKEN = "MOH Patient Names";

	/**
	 * TODO figure out why we can't just use person.getName().getFullName()
	 *
	 * @should return the full name of a patient
	 * @param context
	 * @param patientId
	 * @param parameters
	 * @return
	 * @throws LogicException
	 */
	public Result evaluate(LogicContext context, Integer patientId, Map<String, Object> parameters) throws LogicException {
		String givenname = "";
		String middlename = "";
		String lastname = "";

		Patient patient = Context.getPatientService().getPatient(patientId);

		if (!StringUtils.isEmpty(patient.getGivenName()))
			givenname = patient.getGivenName();

		if (!StringUtils.isEmpty(patient.getMiddleName()))
			middlename = patient.getMiddleName();

		if (!StringUtils.isEmpty(patient.getFamilyName()))
			lastname = patient.getFamilyName();

		return new Result(givenname + " " + middlename + " " + lastname);
	}

	@Override
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
	public Result.Datatype getDefaultDatatype() {
		return Result.Datatype.TEXT;
	}

	public Set<RuleParameterInfo> getParameterList() {
		return null;
	}

	@Override
	public int getTTL() {
		return 0;
	}

}
