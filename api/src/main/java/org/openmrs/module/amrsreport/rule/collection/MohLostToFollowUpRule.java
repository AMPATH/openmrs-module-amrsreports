package org.openmrs.module.amrsreport.rule.collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
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

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Author jmwogi
 */
public class MohLostToFollowUpRule extends MohEvaluableRule {

	private static final Log log = LogFactory.getLog(MohLostToFollowUpRule.class);

	public static final String TOKEN = "MOH LTFU-TO-DEAD";

	private List<OpenmrsObject> questionConcepts = Arrays.<OpenmrsObject>asList(new Concept[]{
			MohCacheUtils.getConcept(LostToFollowUpPatientSnapshot.CONCEPT_DATE_OF_DEATH),
			MohCacheUtils.getConcept(LostToFollowUpPatientSnapshot.CONCEPT_DEATH_REPORTED_BY),
			MohCacheUtils.getConcept(LostToFollowUpPatientSnapshot.CONCEPT_CAUSE_FOR_DEATH),
			MohCacheUtils.getConcept(LostToFollowUpPatientSnapshot.CONCEPT_DECEASED),
			MohCacheUtils.getConcept(LostToFollowUpPatientSnapshot.CONCEPT_PATIENT_DIED),
			MohCacheUtils.getConcept(LostToFollowUpPatientSnapshot.CONCEPT_TRANSFER_CARE_TO_OTHER_CENTER),
			MohCacheUtils.getConcept(MohEvaluableNameConstants.RETURN_VISIT_DATE),
			MohCacheUtils.getConcept(LostToFollowUpPatientSnapshot.CONCEPT_RETURN_VISIT_DATE_EXP_CARE_NURSE)
	});

	private static final MohCoreService mohCoreService = Context.getService(MohCoreService.class);

	/**
	 * @should get date and reason why a patient was lost
	 * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, org.openmrs.Patient,
	 *      java.util.Map)
	 */
	public Result evaluate(LogicContext context, Integer patientId, Map<String, Object> parameters) throws LogicException {

		Patient patient = Context.getPatientService().getPatient(patientId);

		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");

		if (patient.getDeathDate() != null) {
			return new Result("DEAD | " + sdf.format(patient.getDeathDate()));
		}

		//pull relevant observations then loop while checking concepts
		Map<String, Collection<OpenmrsObject>> obsRestrictions = new HashMap<String, Collection<OpenmrsObject>>();
		obsRestrictions.put("concept", questionConcepts);
		MohFetchRestriction mohFetchRestriction = new MohFetchRestriction();
		List<Obs> observations = mohCoreService.getPatientObservations(patientId, obsRestrictions, mohFetchRestriction);

		LostToFollowUpPatientSnapshot lostToFollowUpPatientSnapshot = new LostToFollowUpPatientSnapshot();

		/*Loop through Observations*/
		for (Obs ob : observations) {
			if (lostToFollowUpPatientSnapshot.consume(ob)) {
				if (lostToFollowUpPatientSnapshot.eligible())
					return new Result(lostToFollowUpPatientSnapshot.getProperty("reason").toString());
			}
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