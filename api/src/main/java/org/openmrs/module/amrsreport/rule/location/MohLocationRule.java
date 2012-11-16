package org.openmrs.module.amrsreport.rule.location;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.result.Result;
import org.openmrs.module.amrsreport.cache.MohCacheUtils;
import org.openmrs.module.amrsreport.rule.MohEvaluableNameConstants;
import org.openmrs.module.amrsreport.rule.MohEvaluableRule;

import java.util.List;
import java.util.Map;

public class MohLocationRule extends MohEvaluableRule {

	private static final Log log = LogFactory.getLog(MohLocationRule.class);

	public static final String TOKEN = "MOH Location";

	@Override
	protected Result evaluate(LogicContext context, Integer patientId, Map<String, Object> parameters) {
		Result result = new Result();

		Patient patient = context.getPatient(patientId);

		//find all the concepts needed to use here
		Concept TRANSFER_CARE_TO_OTHER_CENTER = MohCacheUtils.getConcept(MohEvaluableNameConstants.TRANSFER_CARE_TO_OTHER_CENTER);
		Concept WITHIN_AMPATH_CLINICS = MohCacheUtils.getConcept(MohEvaluableNameConstants.WITHIN_AMPATH_CLINICS);
		Concept REASON_FOR_MISSED_VISIT = MohCacheUtils.getConcept(MohEvaluableNameConstants.REASON_FOR_MISSED_VISIT);
		Concept TRANSFER_IN_WITHIN_AMPATH_CLINICS = MohCacheUtils.getConcept(MohEvaluableNameConstants.AMPATH_CLINIC_TRANSFER);
		Concept TRANSFER_CARE_TO_OTHER_CENTER_DETAILED = MohCacheUtils.getConcept(MohEvaluableNameConstants.TRANSFER_CARE_TO_OTHER_CENTER_DETAILED);
		Concept FREETEXT_GENERAL = MohCacheUtils.getConcept(MohEvaluableNameConstants.FREETEXT_GENERAL);
		Concept NON_AMPATH = MohCacheUtils.getConcept(MohEvaluableNameConstants.NON_AMPATH);
		Concept REASON_EXITED_CARE = MohCacheUtils.getConcept(MohEvaluableNameConstants.REASON_EXITED_CARE);
		Concept TRANSFER_OUT_FROM_THE_PROGRAM = MohCacheUtils.getConcept(MohEvaluableNameConstants.PATIENT_TRANSFERRED_OUT);
		Concept OUTCOME_AT_END_OF_TUBERCULOSIS_TREATMENT = MohCacheUtils.getConcept(MohEvaluableNameConstants.OUTCOME_AT_END_OF_TUBERCULOSIS_TREATMENT);

		//get the entry location from encounter
		List<Encounter> encounters = Context.getEncounterService().getEncountersByPatient(patient);

		for (Encounter loc : encounters) {
			String location = "Entry Location:  " + loc.getLocation().getName();
			Result lociNameResult = new Result(location);
			result.add(lociNameResult);
			// TODO figure out why Jonah put a break here
			// break;
		}

		//get obs list based on patient and concept
		List<Obs> obs1 = Context.getObsService().getObservationsByPersonAndConcept(patient, TRANSFER_CARE_TO_OTHER_CENTER);

		//loop through obs1 and find a match base on an answer
		for (Obs ob1 : obs1) {
			String loc1 = null;
			if (ob1.getValueCoded().equals(WITHIN_AMPATH_CLINICS))
				loc1 = ob1.getLocation().getName();
			if (loc1 != "" || loc1 != null) {
				String loc11 = "Transferred Within Ampath to: " + loc1;
				Result loc11Result = new Result(loc11);
				result.add(loc11Result);
			}
		}

		//get obs list based on patient and concept
		List<Obs> obs2 = Context.getObsService().getObservationsByPersonAndConcept(patient, REASON_FOR_MISSED_VISIT);

		//loop through obs1 and find a match base on an answer
		for (Obs ob2 : obs2) {
			String loc2 = null;
			if (ob2.getValueCoded().equals(TRANSFER_IN_WITHIN_AMPATH_CLINICS))
				loc2 = ob2.getLocation().getName();
			if (loc2 != "" || loc2 != null) {
				String loc21 = "Transferred Within Ampath to: " + loc2;
				Result loc21Result = new Result(loc21);
				result.add(loc21Result);
			}
		}

		//get obs list based on patient and concept
		List<Obs> obs3 = Context.getObsService().getObservationsByPersonAndConcept(patient, TRANSFER_CARE_TO_OTHER_CENTER_DETAILED);

		//loop through obs1 and find a match base on an answer
		for (Obs ob3 : obs3) {
			String loc3 = null;
			if (ob3.getValueCoded().equals(FREETEXT_GENERAL))
				loc3 = ob3.getLocation().getName();
			if (loc3 != "" || loc3 != null) {
				String loc31 = "Transferred Within Ampath to: " + loc3;
				Result loc31Result = new Result(loc31);
				result.add(loc31Result);
			}
		}

		//transfer outside the ampath site

		//loop through obs1 and find a match base on an answer
		for (Obs ob4 : obs1) {
			String loc4 = null;
			if (ob4.getValueCoded().equals(NON_AMPATH))
				loc4 = ob4.getLocation().getName();
			if (loc4 != "" || loc4 != null) {
				String loc41 = "Transferred From Ampath to: " + loc4;
				Result loc41Result = new Result(loc41);
				result.add(loc41Result);
			}
		}

		//get obs list based on patient and concept
		List<Obs> obs4 = Context.getObsService().getObservationsByPersonAndConcept(patient, REASON_EXITED_CARE);

		//loop through obs1 and find a match base on an answer
		for (Obs ob5 : obs4) {
			String loc5 = null;
			if (ob5.getValueCoded().equals(TRANSFER_OUT_FROM_THE_PROGRAM))
				loc5 = ob5.getLocation().getName();
			if (loc5 != "" || loc5 != null) {
				String loc51 = "Transferred From Ampath to: " + loc5;
				Result loc51Result = new Result(loc51);
				result.add(loc51Result);
			}
		}

		//get obs list based on patient and concept
		List<Obs> obs5 = Context.getObsService().getObservationsByPersonAndConcept(patient, OUTCOME_AT_END_OF_TUBERCULOSIS_TREATMENT);

		//loop through obs1 and find a match base on an answer
		for (Obs ob6 : obs5) {
			String loc6 = null;
			if (ob6.getValueCoded().equals(TRANSFER_OUT_FROM_THE_PROGRAM))
				loc6 = ob6.getLocation().getName();
			if (loc6 != "" || loc6 != null) {
				String loc61 = "Transferred From Ampath to: " + loc6;
				Result loc61Result = new Result(loc61);
				result.add(loc61Result);
			}
		}

		return result;
	}

	@Override
	protected String getEvaluableToken() {
		// TODO Auto-generated method stub
		return TOKEN;
	}

}
