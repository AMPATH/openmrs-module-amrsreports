package org.openmrs.module.amrsreport.rule.collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.amrsreport.cache.MohCacheUtils;
import org.openmrs.module.amrsreport.rule.MohEvaluableRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MohReasonsForPepRule extends MohEvaluableRule {

	private static final Log log = LogFactory.getLog(MohReasonsForPepRule.class);

	public static final String TOKEN = "MOH Reasons For PEP";

	public static final String METHOD_OF_HIV_EXPOSURE = "METHOD OF HIV EXPOSURE";
	public static final String SEXUAL_ASSAULT = "SEXUAL ASSAULT";
	public static final String SPOUSES_PARTNER_SUSPECTED_HIV = "SPOUSES PARTNER SUSPECTED HIV+";
	public static final String OCCUPATIONAL_EXPOSURE = "OCCUPATIONAL EXPOSURE";
	public static final String PHYSICAL_ASSAULT = "PHYSICAL ASSAULT";
	public static final String SUSPECTED_PAST_HIV_EXPOSURE = "SUSPECTED PAST HIV EXPOSURE";
	public static final String CONTAMINATED_NEEDLE_STICK = "CONTAMINATED NEEDLE STICK";
	public static final String BLOOD_TRANSFUSION = "BLOOD TRANSFUSION";
	public static final String INTRAVENOUS_DRUG_USE = "INTRAVENOUS DRUG USE";
	public static final String OTHER_NON_CODED = "OTHER NON-CODED";
	public static final String UNKNOWN = "UNKNOWN";
	public static final String POST_EXPOSURE_INITIAL_FORM = "PEPINITIAL";
	public static final String POST_EXPOSURE_RETURN_FORM = "PEPRETURN";
	private Map<String, EncounterType> cachedEncounterType = null;

	private static class SortByDateComparator implements Comparator<Object> {

		@Override
		public int compare(Object a, Object b) {
			Obs ao = (Obs) a;
			Obs bo = (Obs) b;
			return ao.getObsDatetime().compareTo(bo.getObsDatetime());
		}
	}

	public Result evaluate(LogicContext context, Integer patientId, Map<String, Object> parameters) throws LogicException {
		ObsService obsService = Context.getObsService();
		String pepReason = "";

		Patient patient = Context.getPatientService().getPatient(patientId);

		List<Obs> observations = obsService.getObservations(Arrays.<Person>asList(patient), getCachedEncounters(patient), getQuestionConcepts(),
				null, null, null, null, null, null, null, null, false);
		//sort in order to get the latest dates
		Collections.sort(observations, new SortByDateComparator());

		if (log.isDebugEnabled()) {
			log.debug("Total observations count " + observations.size());
		}

		for (Obs obs : observations) {


			if (obs.getValueCoded().equals(MohCacheUtils.getConcept(SEXUAL_ASSAULT))) {
				if (pepReason == "") {
					pepReason = SEXUAL_ASSAULT;
				} else {
					pepReason += ";" + SEXUAL_ASSAULT;
				}
			}


			if (obs.getValueCoded().equals(MohCacheUtils.getConcept(SPOUSES_PARTNER_SUSPECTED_HIV))) {
				if (pepReason == "") {
					pepReason = SPOUSES_PARTNER_SUSPECTED_HIV;
				} else {
					pepReason += ";" + SPOUSES_PARTNER_SUSPECTED_HIV;
				}
			}  //return new Result(SPOUSES_PARTNER_SUSPECTED_HIV);

			if (obs.getValueCoded().equals(MohCacheUtils.getConcept(OCCUPATIONAL_EXPOSURE))) {
				if (pepReason == "") {
					pepReason = OCCUPATIONAL_EXPOSURE;
				} else {
					pepReason += ";" + OCCUPATIONAL_EXPOSURE;
				}
				// return new Result(OCCUPATIONAL_EXPOSURE);
			}

			if (obs.getValueCoded().equals(MohCacheUtils.getConcept(PHYSICAL_ASSAULT))) {
				if (pepReason == "") {
					pepReason = PHYSICAL_ASSAULT;
				} else {
					pepReason += ";" + PHYSICAL_ASSAULT;
				}
				//return new Result(PHYSICAL_ASSAULT);
			}

			if (obs.getValueCoded().equals(MohCacheUtils.getConcept(SUSPECTED_PAST_HIV_EXPOSURE))) {
				if (pepReason == "") {
					pepReason = SUSPECTED_PAST_HIV_EXPOSURE;
				} else {
					pepReason += ";" + SUSPECTED_PAST_HIV_EXPOSURE;
				}
				//return new Result(SUSPECTED_PAST_HIV_EXPOSURE);
			}

			if (obs.getValueCoded().equals(MohCacheUtils.getConcept(CONTAMINATED_NEEDLE_STICK))) {
				if (pepReason == "") {
					pepReason = CONTAMINATED_NEEDLE_STICK;
				} else {
					pepReason += ";" + CONTAMINATED_NEEDLE_STICK;
				}
				//return new Result(CONTAMINATED_NEEDLE_STICK);
			}

			if (obs.getValueCoded().equals(MohCacheUtils.getConcept(BLOOD_TRANSFUSION))) {

				if (pepReason == "") {
					pepReason = BLOOD_TRANSFUSION;
				} else {
					pepReason += ";" + BLOOD_TRANSFUSION;
				}
				//return new Result(BLOOD_TRANSFUSION);
			}

			if (obs.getValueCoded().equals(MohCacheUtils.getConcept(INTRAVENOUS_DRUG_USE))) {

				if (pepReason == "") {
					pepReason = INTRAVENOUS_DRUG_USE;
				} else {
					pepReason += ";" + INTRAVENOUS_DRUG_USE;
				}
				//return new Result(INTRAVENOUS_DRUG_USE);
			}

			if (obs.getValueCoded().equals(MohCacheUtils.getConcept(OTHER_NON_CODED))) {
				if (pepReason == "") {
					pepReason = OTHER_NON_CODED;
				} else {
					pepReason += ";" + OTHER_NON_CODED;
				}
				//return new Result(OTHER_NON_CODED);
			}
			if (obs.getValueCoded().equals(MohCacheUtils.getConcept(UNKNOWN))) {
				if (pepReason == "") {
					pepReason = UNKNOWN;
				} else {
					pepReason += ";" + UNKNOWN;
				}
			}


		}
		if (pepReason != null || pepReason == "") {
			return new Result(pepReason);
		}

		return new Result();

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

	private List<Concept> getQuestionConcepts() {
		return Collections.singletonList(MohCacheUtils.getConcept(METHOD_OF_HIV_EXPOSURE));
	}

	private EncounterType getCachedEncounterType(String name) {
		if (cachedEncounterType == null)
			cachedEncounterType = new HashMap<String, EncounterType>();
		if (!cachedEncounterType.containsKey(name))
			cachedEncounterType.put(name, Context.getEncounterService().getEncounterType(name));
		return cachedEncounterType.get(name);
	}

	private List<Encounter> getCachedEncounters(Patient patient) {
		List<Encounter> pepinitialreturn = new ArrayList<Encounter>();
		List<Encounter> cachedEncounters = new ArrayList<Encounter>();

		cachedEncounters.addAll(Context.getEncounterService().getEncountersByPatient(patient));
		//log.info("All encounters for patient "+patient.getPatientId()+"  is"+cachedEncounters.size());

		for (Encounter encounters : cachedEncounters) {

			if (encounters.getEncounterType().equals(getCachedEncounterType(POST_EXPOSURE_INITIAL_FORM))
					|| encounters.getEncounterType().equals(getCachedEncounterType(POST_EXPOSURE_RETURN_FORM)))

				pepinitialreturn.add(encounters);
		}


		return pepinitialreturn;
	}

}
