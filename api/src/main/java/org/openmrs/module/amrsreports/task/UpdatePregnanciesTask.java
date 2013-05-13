package org.openmrs.module.amrsreports.task;

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;

/**
 * Updates the pregnancy table with flags for each date associated with a pregnancy observation
 */
public class UpdatePregnanciesTask extends AMRSReportsTask {

	private static final String DROP_TABLE =
			"DROP TABLE IF EXISTS `amrsreports_pregnancy`";

	private static final String CREATE_TABLE =
			"CREATE TABLE `amrsreports_pregnancy` (" +
					"  `pregnancy_id` int(11) NOT NULL AUTO_INCREMENT," +
					"  `person_id` int(11) NOT NULL," +
					"  `pregnancy_date` datetime NOT NULL," +
					"  `due_date` datetime DEFAULT NULL," +
					"  `pregstatus` int(1) DEFAULT 0," +
					"  `probpreg` int(1) DEFAULT 0," +
					"  `testpreg` int(1) DEFAULT 0," +
					"  `dangerpreg` int(1) DEFAULT 0," +
					"  `reasnvispreg` int(1) DEFAULT 0," +
					"  `durpreg` int(1) DEFAULT 0," +
					"  `fundpreg` int(1) DEFAULT 0," +
					"  `ancpreg` int(1) DEFAULT 0," +
					"  PRIMARY KEY (`pregnancy_id`)," +
					"  KEY `amrsreport_pregnancy_pregdate` (`pregnancy_date`)," +
					"  CONSTRAINT `amrsreport_unique_person_pregdate` UNIQUE (`person_id`, `pregnancy_date`)," +
					"  CONSTRAINT `amrsreport_pregnancy_person_ref` FOREIGN KEY (`person_id`) REFERENCES `person` (`person_id`)" +
					") ENGINE=InnoDB DEFAULT CHARSET=utf8";

	private static String MACRO_UPDATE_COLUMN =
			"insert into amrsreports_pregnancy (" +
					"  person_id, " +
					"  pregnancy_date, " +
					"  :column" +
					" )" +
					"  select " +
					"    e.patient_id, DATE_FORMAT(e.encounter_datetime, '%Y-%m-%d') as p_date, 1" +
					"  from " +
					"    obs o" +
					"    join encounter e" +
					"      on e.encounter_id = o.encounter_id and e.voided = 0" +
					"    join person p" +
					"      on e.patient_id = p.person_id and p.voided = 0" +
					"    join patient pt" +
					"      on e.patient_id = pt.patient_id and pt.voided = 0" +
					"  where " +
					"    o.voided = 0" +
					"    and ( :criteria )" +
					"  group by" +
					"    p_date" +
					"  order by" +
					"    p_date asc" +
					" ON DUPLICATE KEY UPDATE" +
					"  :column = 1";

	private static final String UPDATE_DUE_DATES =
			"insert into amrsreports_pregnancy (" +
					"  person_id, " +
					"  pregnancy_date, " +
					"  due_date" +
					" )" +
					"  select " +
					"    e.patient_id, DATE_FORMAT(e.encounter_datetime, '%Y-%m-%d') as p_date, value_datetime as d_date" +
					"  from " +
					"    obs o" +
					"    join encounter e" +
					"      on e.encounter_id = o.encounter_id and e.voided = 0" +
					"    join person p" +
					"      on e.patient_id = p.person_id and p.voided = 0" +
					"    join patient pt" +
					"      on e.patient_id = pt.patient_id and pt.voided = 0" +
					"  where " +
					"    o.voided = 0" +
					"    and concept_id in (1854, 5596)" +
					"    and value_datetime IS NOT NULL" +
					"  group by" +
					"    p_date" +
					"  order by" +
					"    p_date asc" +
					" ON DUPLICATE KEY UPDATE" +
					"  due_date = (" +
					"    select value_datetime" +
					"    from obs" +
					"    join encounter e on e.encounter_id = obs.encounter_id and e.voided = 0" +
					"    join person p on p.person_id = obs.person_id and p.voided = 0" +
					"    where" +
					"      obs.voided = 0" +
					"      and obs.person_id = VALUES(person_id)" +
					"      and DATE_FORMAT(VALUES(pregnancy_date), '%Y-%m-%d') = DATE_FORMAT(e.encounter_datetime, '%Y-%m-%d')" +
					"      and concept_id in (1854, 5596)" +
					"      and value_datetime IS NOT NULL" +
					"  )";

	private static String MACRO_UPDATE_EDD =
			"INSERT INTO amrsreports_pregnancy (" +
					"	person_id," +
					"	pregnancy_date," +
					"	due_date" +
					" ) SELECT" +
					"	e.patient_id, " +
					"	DATE_FORMAT(e.encounter_datetime, '%Y-%m-%d') AS p_date," +
					"	DATE_ADD(o.obs_datetime, INTERVAL (280 - (o.value_numeric * :days)) DAY) AS d_date" +
					" FROM" +
					"	obs o INNER JOIN encounter e ON e.encounter_id = o.encounter_id AND e.voided = 0" +
					" WHERE" +
					"	o.voided = 0" +
					"	and ( :criteria )" +
					" GROUP BY" +
					"	p_date" +
					" ORDER BY" +
					"	p_date asc" +
					" ON DUPLICATE KEY UPDATE" +
					"	due_date = (" +
					"       SELECT DATE_ADD(o.obs_datetime, INTERVAL (280 - (o.value_numeric * :days)) DAY)" +
					"       FROM obs o INNER JOIN encounter e ON e.encounter_id = o.encounter_id AND e.voided = 0" +
					"       WHERE" +
					"           o.voided = 0" +
					"           AND o.person_id = VALUES(person_id)" +
					"           AND DATE_FORMAT(VALUES(pregnancy_date), '%Y-%m-%d') = DATE_FORMAT(e.encounter_datetime, '%Y-%m-%d')" +
					"           AND ( :criteria )" +
					"   )";

	private AdministrationService administrationService;

	/**
	 * drops, creates and fills out the pregnancy table
	 */
	@Override
	public void doExecute() {

		// drop the table
		getAdministrationService().executeSQL(DROP_TABLE, false);

		// recreate the table
		getAdministrationService().executeSQL(CREATE_TABLE, false);

		// update all of the columns
		updateColumn("pregstatus", "concept_id = 5272 and value_coded = 1065 and e.form_id <> 245");
		updateColumn("probpreg", "concept_id in (6042, 1790) and value_coded in (44, 47, 46)");
		updateColumn("testpreg", "(concept_id = 45 and value_coded = 703) or (concept_id = 1856 and value_coded <> 1175)");
		updateColumn("reasnvispreg", "concept_id in (1834, 1835) and value_coded = 1831");
		// TODO add setting fundpreg and durpreg columns to 1 in updateEDD
		updateColumn("durpreg", "concept_id in (1279, 5992) and value_numeric > 0");
		updateColumn("fundpreg", "concept_id = 1855 and value_numeric > 0");
		updateColumn("ancpreg", "concept_id = 2055 and value_coded = 1065");
		// menstrual period date: 1836

		// update due dates
		updateEDD(7, "concept_id = 1855");
		updateEDD(30, "concept_id = 5992 AND value_numeric <= 9");
		updateEDD(7, "concept_id = 1279 OR (concept_id = 5992 AND value_numeric > 9)");

		getAdministrationService().executeSQL(UPDATE_DUE_DATES, false);
	}

	/**
	 * updates a given column based on provided criteria
	 */
	private void updateColumn(String column, String criteria) {
		if (StringUtils.isBlank(column) || StringUtils.isBlank(criteria))
			return;

		String query = MACRO_UPDATE_COLUMN.replaceAll(":criteria", criteria).replaceAll(":column", column);
		getAdministrationService().executeSQL(query, false);
	}

	/**
	 * updates due_date based on provided criteria
	 */
	private void updateEDD(Integer days, String criteria) {
		if (days == null || StringUtils.isBlank(criteria))
			return;

		String query = MACRO_UPDATE_EDD.replaceAll(":criteria", criteria).replaceAll(":days", days.toString());
		getAdministrationService().executeSQL(query, false);
	}

	/**
	 * getter for administrationService
	 */
	public AdministrationService getAdministrationService() {
		if (administrationService == null)
			administrationService = Context.getAdministrationService();
		return administrationService;
	}
}
