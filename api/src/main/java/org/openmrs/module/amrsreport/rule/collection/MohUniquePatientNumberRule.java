package org.openmrs.module.amrsreport.rule.collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.amrsreport.cache.MohCacheUtils;
import org.openmrs.module.amrsreport.rule.MohEvaluableRule;
import org.openmrs.util.OpenmrsUtil;

import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * MohUniquePatientNumberRule class checks and returns CCC Number for a patient
 */
public class MohUniquePatientNumberRule extends MohEvaluableRule {

	private static final Log log = LogFactory.getLog(MohUniquePatientNumberRule.class);

	public static final String TOKEN = "MOH Unique Patient Number";
	/**
     * @should return a Unique Patient Number
	 * @should return not found if no Unique Patient Number exists
	 * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, org.openmrs.Patient,
	 *      java.util.Map)
	 */
	public Result evaluate(LogicContext context, Integer patientId, Map<String, Object> parameters) throws LogicException {
		Patient patient = Context.getPatientService().getPatient(patientId);

        PatientIdentifier mflIdentifier = patient.getPatientIdentifier(MohCacheUtils.getPatientIdentifierType(Context.getAdministrationService().getGlobalProperty("cccgenerator.CCC")));



        try{
            if (mflIdentifier == null) {

            log.error("CCC Number not found for patient " + patientId);
            return new Result("not found");
        }

        }
        catch(NullPointerException nullPointerException){
            log.info("CCC Number not found  "+nullPointerException.toString());
        }


        return new Result(mflIdentifier.getIdentifier());
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