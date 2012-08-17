package org.openmrs.module.amrsreport.rule.collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.amrsreport.rule.MohEvaluableRule;
import org.openmrs.util.OpenmrsUtil;

import java.util.Map;
import java.util.Set;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: oliver
 * Date: 5/24/12
 * Time: 12:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class MohIdentifierRule extends MohEvaluableRule {

    private static final Log log = LogFactory.getLog(MohIdentifierRule.class);

    public static final String TOKEN ="MOH Ampath Identifier";


    public Result evaluate(LogicContext context, Integer patientId, Map<String, Object> parameters) throws LogicException {
            String identifier="";

        Patient patient = Context.getPatientService().getPatient(patientId);
        AdministrationService ams=Context.getAdministrationService();
        PatientIdentifierType patientIdentifierType=Context.getPatientService().getPatientIdentifierTypeByName(ams.getGlobalProperty("mflgenerator.mfl"));


        List<PatientIdentifier>  listPi= patient.getActiveIdentifiers() ;

        for(PatientIdentifier pid:listPi) {

            if(!OpenmrsUtil.nullSafeEquals(pid.getIdentifierType(), patientIdentifierType)){
                return new Result(pid.getIdentifier());
            }
        }

        return new Result();
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
