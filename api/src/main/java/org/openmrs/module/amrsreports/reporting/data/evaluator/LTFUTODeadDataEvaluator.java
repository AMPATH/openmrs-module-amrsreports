package org.openmrs.module.amrsreports.reporting.data.evaluator;

import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.AmrsReportsConstants;
import org.openmrs.module.amrsreports.reporting.data.LTFUTODeadDataDefinition;
import org.openmrs.module.amrsreports.service.MohCoreService;
import org.openmrs.module.amrsreports.util.MOHReportUtil;
import org.openmrs.module.reporting.common.ListMap;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.evaluator.PersonDataEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Handler for LTFU / TO / Dead column
 */
@Handler(supports = LTFUTODeadDataDefinition.class, order = 50)
public class LTFUTODeadDataEvaluator implements PersonDataEvaluator {

	@Override
	public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {
		EvaluatedPersonData ret = new EvaluatedPersonData(definition, context);

		if (context.getBaseCohort().isEmpty())
			return ret;

		Map<String, Object> m = new HashMap<String, Object>();
		m.put("patientIds", context.getBaseCohort());

		// determine death status and date using multiple queries

		String obsDeathSQL = "select person_id, min(obs_datetime)" +
				"	from obs" +
				"	where" +
				"		voided=0" +
				"		and person_id in (:patientIds)" +
				"		and (" +
				"			concept_id in (1570, 1734, 1573)" +
				"			or (concept_id=6206 and value_coded=159)" +
				"			or (concept_id=1733 and value_coded=159)" +
				"			or (concept_id=1596 and value_coded=1593)" +
				"		)" +
				" 	group by person_id";

		Map<Integer, Date> deathObs = makeDateMapFromSQL(obsDeathSQL, m);

		// get most recent RTC dates
		String rtcSQL = "select person_id, max(value_datetime)" +
				" 	from obs" +
				" 	where" +
				"		voided=0" +
				"		and person_id in (:patientIds)" +
				"		and concept_id in (1502, 5096)" +
				// TODO reintroduce the datetime limitation somehow
				// "   	and obs_datetime <= :reportDate" +
				" 	group by person_id";

		Map<Integer, Date> rtcDates = makeDateMapFromSQL(rtcSQL, m);

		// add reportDate for the rest of the queries
		m.put("reportDate", context.getEvaluationDate());

		String encDeathSQL = "select patient_id, min(encounter_datetime)" +
				"	from encounter" +
				"	where" +
				"		voided=0" +
				"		and patient_id in (:patientIds)" +
				"		and encounter_type=31" +
				"		and encounter_datetime <= :reportDate" +
				" 	group by patient_id";

		Map<Integer, Date> deathEncs = makeDateMapFromSQL(encDeathSQL, m);

		String propsDeathSQL = "select person_id, death_date" +
				" 	from person" +
				" 	where" +
				"		death_date <= :reportDate" +
				"		and person_id in (:patientIds)" +
				" 		and dead = 1";

		Map<Integer, Date> deathProps = makeDateMapFromSQL(propsDeathSQL, m);

		// load up a combined set of ids for dead patients
		Set<Integer> deadPeople = new HashSet<Integer>();
		deadPeople.addAll(deathObs.keySet());
		deadPeople.addAll(deathEncs.keySet());
		deadPeople.addAll(deathProps.keySet());

		// find the first death date from all possible sources, for each id
		Map<Integer, Date> deathFinal = new HashMap<Integer, Date>();
		for (Integer pid : deadPeople) {
			Date deathDate = null;

			if (deathObs.containsKey(pid) && deathObs.get(pid) != null)
				deathDate = deathObs.get(pid);

			if (deathEncs.containsKey(pid) && deathEncs.get(pid) != null)
				deathDate = (deathDate != null && deathDate.before(deathEncs.get(pid))) ? deathDate : deathEncs.get(pid);

			if (deathProps.containsKey(pid) && deathProps.get(pid) != null)
				deathDate = (deathDate != null && deathDate.before(deathProps.get(pid))) ? deathDate : deathProps.get(pid);

			deathFinal.put(pid, deathDate);
		}

		// find transfer out dates
		String transferSQL = "select person_id, max(obs_datetime)" +
				"	from obs" +
				"	where" +
				"		voided=0" +
				"		and person_id in (:patientIds)" +
				"		and (concept_id=1285 and value_coded=1287)" +
				"		and obs_datetime <= :reportDate" +
				" 	group by person_id";

		Map<Integer, Date> transfers = makeDateMapFromSQL(transferSQL, m);

		// get most recent encounter date
		String lastEncounterSQL = "select encounter.patient_id, max(encounter_datetime)" +
				" from encounter" +
				" where" +
				"	voided=0" +
				"	and patient_id in (:patientIds)" +
				"   and encounter_datetime <= :reportDate" +
				"	and (" +
				"		encounter_type in (1, 2, 3, 4, 13, 14, 15, 17, 18, 19, 20, 21, 22, 23, 26)" +
				"		or form_id in (248, 249)" +
				"	)" +
				" group by patient_id";

		Map<Integer, Date> lastEncounters = makeDateMapFromSQL(lastEncounterSQL, m);

		// set a few repeatedly used variables
		Calendar rtcOverdueDate = Calendar.getInstance();
		rtcOverdueDate.setTime(context.getEvaluationDate());
		rtcOverdueDate.add(Calendar.DAY_OF_MONTH, -93);

		// populate them, leaving the rest null
		for (Integer personId : context.getBaseCohort().getMemberIds()) {

			// get the last encounter date -- this should never be empty
			Date lastEncounterDate = lastEncounters.containsKey(personId) ? lastEncounters.get(personId) : null;

			// set RTC to what is found or last encounter date + 21 days
			Date rtcExpectedDate = null;
			if (rtcDates.containsKey(personId))
				rtcExpectedDate = rtcDates.get(personId);
			else if (lastEncounterDate != null) {
				Calendar c = Calendar.getInstance();
				c.setTime(lastEncounterDate);
				c.add(Calendar.DAY_OF_MONTH, 21);
				rtcExpectedDate = c.getTime();
			}

			// report dead if dead
			if (deathFinal.containsKey(personId))
				ret.addData(personId, MOHReportUtil.joinAsSingleCell("Dead", MOHReportUtil.formatdates(deathFinal.get(personId))));

				// report TO if after last encounter
			else if (transfers.containsKey(personId) && transfers.get(personId).after(lastEncounterDate))
				ret.addData(personId, MOHReportUtil.joinAsSingleCell("TO", MOHReportUtil.formatdates(transfers.get(personId))));

				// report LTFU if RTC is overdue
			else if (rtcExpectedDate != null && rtcExpectedDate.before(rtcOverdueDate.getTime())) {
				Calendar expectedDate = Calendar.getInstance();
				expectedDate.setTime(rtcExpectedDate);
				expectedDate.add(Calendar.DAY_OF_MONTH, 93);
				ret.addData(personId, MOHReportUtil.joinAsSingleCell("LTFU", MOHReportUtil.formatdates(expectedDate.getTime())));
			}
		}

		return ret;
	}

	private Map<Integer, Date> makeDateMapFromSQL(String sql, Map<String, Object> substitutions) {
		List<Object> data = Context.getService(MohCoreService.class).executeSqlQuery(sql, substitutions);
		return makeDateMap(data);
	}

	/**
	 * generates a map of integers to dates, assuming this is the kind of response expected from the SQL
	 */
	private Map<Integer, Date> makeDateMap(List<Object> data) {
		Map<Integer, Date> m = new HashMap<Integer, Date>();
		for (Object o : data) {
			Object[] parts = (Object[]) o;
			if (parts.length == 2) {
				Integer pId = (Integer) parts[0];
				Date date = (Date) parts[1];
				m.put(pId, date);
			}
		}

		return m;
	}

}
