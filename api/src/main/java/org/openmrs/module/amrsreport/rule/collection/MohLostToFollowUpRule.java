package org.openmrs.module.amrsreport.rule.collection;
 
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.amrsreport.rule.MohEvaluableNameConstants;
import org.openmrs.module.amrsreport.rule.MohEvaluableRule;
 
 /**
  * Author jmwogi
  */
public class MohLostToFollowUpRule extends MohEvaluableRule {
 
 	private static final Log log = LogFactory.getLog(MohLostToFollowUpRule.class);
 
 	public static final String TOKEN = "MOH LTFU-TO-DEAD";

 	
 	/**
      * @should get date and reason why a patient was lost
	 * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, org.openmrs.Patient,
	 *      java.util.Map)
 	 */
	public Result evaluate(LogicContext context, Integer patientId, Map<String, Object> parameters) throws LogicException {
	 try {

		Patient patient = Context.getPatientService().getPatient(patientId);
        Boolean patientDead = patient.getDead();

		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");

        if(patient.getDeathDate()!=null){
                return new Result("DEAD | " + sdf.format(patient.getDeathDate()));

        }

            List<Encounter> e = Context.getEncounterService().getEncountersByPatient(patient);

            LostToFollowUpPatientSnapshot lostToFollowUpPatientSnapshot = new LostToFollowUpPatientSnapshot();
            /*Loop through Encounters*/
            for(Encounter encounter:e){
                if(lostToFollowUpPatientSnapshot.consume(encounter)){

                    return new Result(lostToFollowUpPatientSnapshot.getProperty("reason").toString());

                }

                /*Loop through Observations*/
                @SuppressWarnings({ "deprecation" })
                Set<Obs> o = Context.getObsService().getObservations(encounter);
                for (Obs ob:o) {
                    if(lostToFollowUpPatientSnapshot.consume(ob)){
                        return new Result(lostToFollowUpPatientSnapshot.getProperty("reason").toString());
                    }

                }

            }
		} catch (Exception e) {}
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