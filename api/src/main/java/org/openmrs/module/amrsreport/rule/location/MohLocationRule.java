package org.openmrs.module.amrsreport.rule.location;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.OpenmrsObject;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.result.Result;
import org.openmrs.module.amrsreport.cache.MohCacheUtils;
import org.openmrs.module.amrsreport.rule.MohEvaluableNameConstants;
import org.openmrs.module.amrsreport.rule.MohEvaluableRule;
import org.openmrs.module.amrsreport.service.MohCoreService;
import org.openmrs.module.amrsreport.util.MohFetchRestriction;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MohLocationRule extends MohEvaluableRule {

	private static final Log log = LogFactory.getLog(MohLocationRule.class);

	public static final String TOKEN = "MOH Location";

	private static final List<OpenmrsObject> questionConcepts = Arrays.<OpenmrsObject>asList(new Concept[]{
			MohCacheUtils.getConcept(MohEvaluableNameConstants.TRANSFER_CARE_TO_OTHER_CENTER),
			MohCacheUtils.getConcept(MohEvaluableNameConstants.REASON_FOR_MISSED_VISIT),
			MohCacheUtils.getConcept(MohEvaluableNameConstants.TRANSFER_CARE_TO_OTHER_CENTER_DETAILED),
			MohCacheUtils.getConcept(MohEvaluableNameConstants.REASON_EXITED_CARE),
			MohCacheUtils.getConcept(MohEvaluableNameConstants.OUTCOME_AT_END_OF_TUBERCULOSIS_TREATMENT)
	});

	private static final List<OpenmrsObject> valueConcepts = Arrays.<OpenmrsObject>asList(new Concept[]{
			MohCacheUtils.getConcept(MohEvaluableNameConstants.WITHIN_AMPATH_CLINICS),
			MohCacheUtils.getConcept(MohEvaluableNameConstants.AMPATH_CLINIC_TRANSFER),
			MohCacheUtils.getConcept(MohEvaluableNameConstants.FREETEXT_GENERAL),
			MohCacheUtils.getConcept(MohEvaluableNameConstants.NON_AMPATH),
			MohCacheUtils.getConcept(MohEvaluableNameConstants.PATIENT_TRANSFERRED_OUT)
	});

	private static final MohCoreService mohCoreService = Context.getService(MohCoreService.class);

	@Override
	protected Result evaluate(LogicContext context, Integer patientId, Map<String, Object> parameters) {
		Result result = new Result();

		//pull relevant observations then loop while checking concepts
		Map<String, Collection<OpenmrsObject>> obsRestrictions = new HashMap<String, Collection<OpenmrsObject>>();
		obsRestrictions.put("concept", questionConcepts);
		obsRestrictions.put("valueCoded", valueConcepts);
		MohFetchRestriction mohFetchRestriction = new MohFetchRestriction();
		List<Obs> observations = mohCoreService.getPatientObservations(patientId, obsRestrictions, mohFetchRestriction);

		boolean foundEntry = false;

		for (Obs obs: observations) {
			if (!foundEntry && obs.getEncounter() != null) {
				result.add(new Result("Entry Location: " + obs.getEncounter().getLocation().getName()));
				foundEntry = true;
			}

			result.add(new Result("Transferred Within Ampath to: " + obs.getLocation().getName()));
		}

		return result;
	}

	@Override
	protected String getEvaluableToken() {
		return TOKEN;
	}

}
