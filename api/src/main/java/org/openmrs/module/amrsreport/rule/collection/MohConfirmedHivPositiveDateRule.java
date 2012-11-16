package org.openmrs.module.amrsreport.rule.collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
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

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Author jmwogi
 */
public class MohConfirmedHivPositiveDateRule extends MohEvaluableRule {

	private static final Log log = LogFactory.getLog(MohConfirmedHivPositiveDateRule.class);

	public static final String TOKEN = "MOH Confirmed HIV Positive Date";

	/**
	 * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, org.openmrs.Patient,
	 *      java.util.Map)
	 */
	public Result evaluate(LogicContext context, Integer patientId, Map<String, Object> parameters) throws LogicException {
		Date firstEncounterDate = null;

		try {
			Patient patient = Context.getPatientService().getPatient(patientId);

			List<Encounter> e = Context.getEncounterService().getEncountersByPatient(patient);

			//sort the encounters
			Collections.sort(e, new SortByDateComparator());

			//Iterate though encounters for the patient
			Iterator<Encounter> it = e.iterator();
			boolean first = true;

			while (it.hasNext()) {
				// get next encounter
				Encounter encounter = it.next();

				// set firstEncounterDate if it is first
				if (first) {
					firstEncounterDate = encounter.getEncounterDatetime();
					first = false;
				}

				//pull encounter obs then loop while checking concepts
				Iterator<Obs> it1 = encounter.getAllObs().iterator();
				while (it1.hasNext()) {
					Obs obs = it1.next();
					if (
							(obs.getConcept() == MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_ENZYME_IMMUNOASSAY_QUALITATIVE)
									&& (obs.getValueCoded() == MohCacheUtils.getConcept(MohEvaluableNameConstants.POSITIVE)))
									||
									(obs.getConcept() == MohCacheUtils.getConcept(MohEvaluableNameConstants.HIV_RAPID_TEST_QUALITATIVE)
											&& (obs.getValueCoded() == MohCacheUtils.getConcept(MohEvaluableNameConstants.POSITIVE)))) {
						return new Result(obs.getObsDatetime());
					}
				}
			}
		} catch (Exception e) {
			// do nothing? TODO log something here ...
		}

		// return the first encounter date if nothing else is found
		return new Result(firstEncounterDate);
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

	private static class SortByDateComparator implements Comparator<Encounter> {

		@Override
		public int compare(Encounter a, Encounter b) {
			Encounter ao = (Encounter) a;
			Encounter bo = (Encounter) b;
			return ao.getDateCreated().compareTo(bo.getDateCreated());
		}
	}

}