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
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.amrsreport.cache.MohCacheUtils;
import org.openmrs.module.amrsreport.rule.MohEvaluableNameConstants;
import org.openmrs.module.amrsreport.rule.MohEvaluableRule;
 
 /**
  * Author jmwogi
  */
public class MohLostToFollowUpRule extends MohEvaluableRule {
 
 	private static final Log log = LogFactory.getLog(MohLostToFollowUpRule.class);
 
 	public static final String TOKEN = "MOH LTFU-TO-DEAD";
 	
 	/**
	 * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, org.openmrs.Patient,
	 *      java.util.Map)
 	 */
	public Result evaluate(LogicContext context, Integer patientId, Map<String, Object> parameters) throws LogicException {
	 try {
		Patient patient = Context.getPatientService().getPatient(patientId);
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
		if(patient.getDead())
			return new Result("DEAD | " + sdf.format(patient.getDeathDate()));
		else if(patient.getDeathDate() != null)
			return new Result("DEAD | " + sdf.format(patient.getDeathDate()));
		else if(patient.getCauseOfDeath() != null)
			return new Result("DEAD | " + sdf.format(patient.getDeathDate()));
		List<Encounter> e = Context.getEncounterService().getEncountersByPatient(patient);
		EncounterType encTpInit = Context.getEncounterService().getEncounterType(MohEvaluableNameConstants.ENCOUNTER_TYPE_ADULT_INITIAL);
		EncounterType encTpRet = Context.getEncounterService().getEncounterType(MohEvaluableNameConstants.ENCOUNTER_TYPE_ADULT_RETURN);
		// DEAD
		for (Iterator<Encounter> it = e.iterator(); it.hasNext();) {
		    Encounter encounter = it.next();
		    if (Context.getEncounterService().getEncounterType(31) == encounter.getEncounterType())
		    	return new Result("DEAD | " + sdf.format(encounter.getEncounterDatetime()));
		    @SuppressWarnings({ "deprecation" })
			Set<Obs> o = Context.getObsService().getObservations(encounter);
		    for (Iterator<Obs> obs = o.iterator();obs.hasNext();) {
		    	Obs ob = obs.next();
		    	if(ob.getConcept() == MohCacheUtils.getConcept("DATE OF DEATH")){
					return new Result("DEAD | " + sdf.format(sdf.format(ob.getObsDatetime())));
				}else if(ob.getConcept() == MohCacheUtils.getConcept("DEATH REPORTED BY")){
					return new Result("DEAD | " + sdf.format(sdf.format(ob.getObsDatetime())));
				}else if(ob.getConcept() == MohCacheUtils.getConcept("CAUSE FOR DEATH")){
					return new Result("DEAD | " + sdf.format(sdf.format(ob.getObsDatetime())));
				}else if(/*(ob.getConcept() == MohCacheUtils.getConcept("REASON FOR MISSED VISIT")) &&
						(*/ob.getValueCoded() == MohCacheUtils.getConcept("DECEASED")){
					return new Result("DEAD | " + sdf.format(sdf.format(ob.getObsDatetime())));
				}else if(/*(ob.getConcept() == MohCacheUtils.getConcept("REASON EXITED CARE")) &&
						(*/ob.getValueCoded() == MohCacheUtils.getConcept("PATIENT DIED")){
					return new Result("DEAD | " + sdf.format(ob.getObsDatetime()));
				}/*else if((ob.getConcept() == MohCacheUtils.getConcept("OUTCOME AT END OF TUBERCULOSIS TREATMENT")) &&
						(ob.getValueCoded() == MohCacheUtils.getConcept("DECEASED"))){
					return new Result("DEAD - " + sdf.format(ob.getObsDatetime()));
				}*/
				
				if(ob.getConcept() == MohCacheUtils.getConcept("TRANSFER CARE TO OTHER CENTER")){
					if(ob.getValueCoded() == MohCacheUtils.getConcept("AMPATH"))
						return new Result("TO | (Ampath) " + sdf.format(ob.getObsDatetime()));
					else
						return new Result("TO | (Non-Ampath) " + sdf.format(ob.getObsDatetime()));
				}
				if((encTpInit == encounter.getEncounterType()) || (encounter.getEncounterType() == encTpRet)){
					if(ob.getConcept().getConceptId() == MohCacheUtils.getConcept(MohEvaluableNameConstants.RETURN_VISIT_DATE).getConceptId()){
						if(sdf.format(ob.getObsDatetime()) != null){
							long requiredTimeToShowup = ((ob.getValueDatetime().getTime()) - (ob.getObsDatetime().getTime())) + (long)(1000 * 60 * 60 * 24 * 30.4375 * 3);
							long todayTimeFromSchedule = (new Date()).getTime() - (ob.getObsDatetime().getTime());
							if( requiredTimeToShowup < todayTimeFromSchedule ){
								return new Result("LTFU | " + sdf.format(ob.getValueDatetime()));
							}
						}
					}
					if(ob.getConcept() == MohCacheUtils.getConcept("RETURN VISIT DATE, EXPRESS CARE NURSE")){
						if(sdf.format(ob.getObsDatetime()) != null){
							long requiredTimeToShowup = ((ob.getValueDatetime().getTime()) - (ob.getObsDatetime().getTime())) + (long)(1000 * 60 * 60 * 24 * 30.4375 * 3);
							long todayTimeFromSchedule = (new Date()).getTime() - (ob.getObsDatetime().getTime());
							if( requiredTimeToShowup < todayTimeFromSchedule ){
								return new Result("LTFU | " + sdf.format(ob.getValueDatetime()));
							}
						}
					}
				}
	        }
		}
		
		for (Iterator<Encounter> it = e.iterator(); it.hasNext();) {
		    Encounter encounter = it.next();
		    if((encTpInit == encounter.getEncounterType()) || (encounter.getEncounterType() == encTpRet)){
		    	int requiredTimeToShowup = (int) (1000 * 60 * 60 * 24 * 30.4375 * 6);
				int todayTimeFromEncounter = (int) ((new Date()).getTime() - (encounter.getEncounterDatetime().getTime()));
				if(!(requiredTimeToShowup >= todayTimeFromEncounter)){
					return new Result("LTFU | " + sdf.format(encounter.getEncounterDatetime()));
				}
		    	break;
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