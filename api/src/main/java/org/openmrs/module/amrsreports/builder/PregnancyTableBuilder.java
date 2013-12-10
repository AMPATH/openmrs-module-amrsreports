/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.amrsreports.builder;

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.util.TableBuilderUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PregnancyTableBuilder {

	private static PregnancyTableBuilder instance;

	public static PregnancyTableBuilder getInstance() {
		if (instance == null)
			instance = new PregnancyTableBuilder();
		return instance;
	}

	private PregnancyTableBuilder() {
		// pass
	}

	private static final String DROP_TABLE =
			"DROP TABLE IF EXISTS `amrsreports_pregnancy`";

	private static final String CREATE_TABLE =
			"CREATE TABLE `amrsreports_pregnancy` (" +
					"  `pregnancy_id` int(11) NOT NULL AUTO_INCREMENT," +
					"  `person_id` int(11) NOT NULL," +
					"  `pregnancy_date` datetime NOT NULL," +
					"  `due_date` datetime DEFAULT NULL," +
					"  `due_date_source` varchar(255) DEFAULT NULL," +
					"  `due_date_invalid` boolean DEFAULT FALSE," +
					"  `edd_fh` datetime DEFAULT NULL," +
					"  `edd_edc` datetime DEFAULT NULL," +
					"  `edd_wkmn` datetime DEFAULT NULL," +
					"  `edd_lmp` datetime DEFAULT NULL," +
					"  `episode` int DEFAULT 0," +
					"  `pregstatus` int(1) DEFAULT 0," +
					"  `probpreg` int(1) DEFAULT 0," +
					"  `testpreg` int(1) DEFAULT 0," +
					"  `dangerpreg` int(1) DEFAULT 0," +
					"  `reasnvispreg` int(1) DEFAULT 0," +
					"  `durpreg` int(1) DEFAULT 0," +
					"  `fundpreg` int(1) DEFAULT 0," +
					"  `ancpreg` int(1) DEFAULT 0," +
					"  `eddpreg` int(1) DEFAULT 0," +
					"  `arvpreg` int(1) DEFAULT 0," +
					"  PRIMARY KEY (`pregnancy_id`)," +
					"  KEY `amrsreport_pregnancy_pregdate` (`pregnancy_date`)," +
					"  CONSTRAINT `amrsreport_unique_person_pregdate` UNIQUE (`person_id`, `pregnancy_date`)" +
//					"  CONSTRAINT `amrsreport_pregnancy_person_ref` FOREIGN KEY (`person_id`) REFERENCES `person` (`person_id`)" +
					") ENGINE=InnoDB DEFAULT CHARSET=utf8";

	private static String MACRO_UPDATE_COLUMN =
			"insert into amrsreports_pregnancy (" +
					"  person_id, " +
					"  pregnancy_date, " +
					"  :column" +
					" )" +
					"  select " +
					"    e.patient_id as person_id," +
					"    DATE_FORMAT(e.encounter_datetime, '%Y-%m-%d') as p_date," +
					"    1" +
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
					"  order by" +
					"    person_id asc, p_date asc" +
					" ON DUPLICATE KEY UPDATE" +
					"  :column = 1";

	private static final String UPDATE_EDD_LMP =
			"insert into amrsreports_pregnancy (" +
					"  person_id, " +
					"  pregnancy_date, " +
					"  edd_lmp" +
					" )" +
					"  select " +
					"    e.patient_id as person_id," +
					"    DATE_FORMAT(e.encounter_datetime, '%Y-%m-%d') as p_date," +
					"    DATE_ADD(value_datetime, INTERVAL (287) DAY) as d_date" +
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
					"    and concept_id in (1836)" +
					"    and value_datetime IS NOT NULL" +
					"  order by" +
					"    person_id asc, p_date asc" +
					" ON DUPLICATE KEY UPDATE" +
					"  edd_lmp = VALUES(edd_lmp)";

	private static final String UPDATE_EDD_FROM_EDC =
			"insert into amrsreports_pregnancy (" +
					"  person_id, " +
					"  pregnancy_date, " +
					"  edd_edc," +
					"  eddpreg" +
					" )" +
					"  select " +
					"    e.patient_id as person_id," +
					"    DATE_FORMAT(e.encounter_datetime, '%Y-%m-%d') as p_date," +
					"    value_datetime as d_date," +
					"    1" +
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
					"  order by" +
					"    person_id asc, p_date asc" +
					" ON DUPLICATE KEY UPDATE" +
					"  edd_edc = VALUES(edd_edc)," +
					"  eddpreg = 1";

	private static String MACRO_UPDATE_EDD =
			"INSERT INTO amrsreports_pregnancy (" +
					"	person_id," +
					"	pregnancy_date," +
					"	:source," +
					"   :column" +
					" ) SELECT" +
					"	e.patient_id as person_id, " +
					"	DATE_FORMAT(e.encounter_datetime, '%Y-%m-%d') AS p_date," +
					"	DATE_ADD(o.obs_datetime, INTERVAL (280 - (o.value_numeric * :days)) DAY) AS d_date," +
					"   1" +
					" FROM" +
					"	obs o INNER JOIN encounter e ON e.encounter_id = o.encounter_id AND e.voided = 0" +
					" WHERE" +
					"	o.voided = 0" +
					"	and ( :criteria )" +
					"  order by" +
					"    person_id asc, p_date asc" +
					" ON DUPLICATE KEY UPDATE" +
					"	:source = VALUES(:source)," +
					"   :column = 1";

	private static final String UPDATE_FALSE_EDD = "" +
			"UPDATE amrsreports_pregnancy" +
			" SET due_date_invalid=true" +
			" WHERE" +
			"   pregnancy_date >= DATE_ADD(due_date, INTERVAL 37 DAY)";

	private static final String UPDATE_EDD_PREFERENCE =
			"UPDATE amrsreports_pregnancy" +
					" SET due_date =" +
					"   IF(edd_lmp IS NOT NULL, edd_lmp," +
					"     IF(edd_edc IS NOT NULL, edd_edc," +
					"       IF(edd_wkmn IS NOT NULL, edd_wkmn," +
					"         IF(edd_fh IS NOT NULL, edd_fh, NULL))))," +
					" due_date_source =" +
					"   IF(edd_lmp IS NOT NULL, 'LMP'," +
					"     IF(edd_edc IS NOT NULL, 'EDC'," +
					"       IF(edd_wkmn IS NOT NULL, 'GEST'," +
					"         IF(edd_fh IS NOT NULL, 'FH', NULL))))";

	private static final String DISTINCT_PEOPLE_WITH_DUE_DATES =
			"select distinct person_id" +
					" from amrsreports_pregnancy" +
					" where due_date is not null and due_date_invalid is false";

	private static String EDDS_FOR_PERSON =
			"select pregnancy_id, pregnancy_date, due_date" +
					" from amrsreports_pregnancy" +
					" where person_id = :personId" +
					"   and due_date is not null" +
					" order by pregnancy_date asc";

	private static String MACRO_UPDATE_EPISODE =
			"update amrsreports_pregnancy" +
					" SET episode = :episode" +
					" WHERE pregnancy_id = :rowId";

	private static final Integer ACCEPTABLE_EDD_GAP = 40;

	/**
	 * drops, creates and fills out the pregnancy table
	 */
	public void execute() {
		AdministrationService admin = Context.getAdministrationService();

		// drop the table
		TableBuilderUtil.runUpdateSQL(DROP_TABLE);

		// recreate the table
		TableBuilderUtil.runUpdateSQL(CREATE_TABLE);

		// update several columns
		updateColumn("pregstatus", "concept_id = 5272 and value_coded = 1065 and e.form_id <> 245");
		updateColumn("probpreg", "concept_id in (6042, 1790) and value_coded in (44, 47, 46)");
		updateColumn("testpreg", "(concept_id = 45 and value_coded = 703) or (concept_id = 1856 and value_coded <> 1175)");
		updateColumn("reasnvispreg", "concept_id in (1834, 1835) and value_coded = 1831");
		updateColumn("ancpreg", "concept_id = 2055 and value_coded = 1065");
		updateColumn("arvpreg", "(concept_id = 1181 and value_coded = 1148)" +
				" or (concept_id = 1251 and value_coded = 1776)" +
				" or (concept_id = 1992 and value_coded not in (1066, 67))");

		// update due dates based on obsDatetime and valueNumeric

		// obs date + (280 days - # days in weeks gestation from fundal height)
		updateEDD("edd_fh", 7, "concept_id = 1855", "fundpreg");

		// obs date + (280 days - # days in months gestation)
		updateEDD("edd_wkmn", 30, "concept_id = 5992 AND value_numeric <= 9", "durpreg");

		// obs date + (280 days - # days in weeks gestation) ... value over 9 is considered to mean weeks
		updateEDD("edd_wkmn", 7, "concept_id = 5992 AND value_numeric > 9", "durpreg");

		// obs date + (280 days - # days in weeks pregnant)
		updateEDD("edd_wkmn", 7, "concept_id = 1279", "durpreg");

		// update due dates based on valueDatetime

		// EDD = valueDatetime of LMP + 287 days
		TableBuilderUtil.runUpdateSQL(UPDATE_EDD_LMP);

		// EDD = valueDatetime of EDC observation
		TableBuilderUtil.runUpdateSQL(UPDATE_EDD_FROM_EDC);

		// select EDD based on priorities
		TableBuilderUtil.runUpdateSQL(UPDATE_EDD_PREFERENCE);

		// indicate whether a due date is probably not valid
		TableBuilderUtil.runUpdateSQL(UPDATE_FALSE_EDD);

		// get unique people
		List<List<Object>> people = admin.executeSQL(DISTINCT_PEOPLE_WITH_DUE_DATES, true);

		// loop through them
		for (List<Object> person : people) {
			Integer personId = (Integer) person.get(0);

			// get EDDs
			List<List<Object>> edds = admin.executeSQL(
					EDDS_FOR_PERSON.replaceAll(":personId", personId.toString()), true);

			// set a few variables
			Integer episode = 0;
			Date benchmark = null;

			// loop through EDDs
			for (List<Object> row : edds) {

				Integer rowId = (Integer) row.get(0);
				Date edd = (Date) row.get(2);

				// if the benchmark has not been set yet (first time)
				// or the edd is after the specified benchmark,
				// start a new episode.
				if (benchmark == null || edd.after(benchmark)) {
					Date encDate = (Date) row.get(1);
					benchmark = addWeeksToDate(encDate, ACCEPTABLE_EDD_GAP);
					episode++;
				}

				// update this row with the episode number
				updateEpisode(rowId, episode);
			}
		}
	}

	private void updateEpisode(Integer rowId, Integer episode) {
		if (rowId == null || episode == null)
			return;

		String query = MACRO_UPDATE_EPISODE
				.replaceAll(":rowId", rowId.toString())
				.replaceAll(":episode", episode.toString());

		TableBuilderUtil.runUpdateSQL(query);
	}

	/**
	 * updates a given column based on provided criteria
	 */
	private void updateColumn(String column, String criteria) {
		if (StringUtils.isBlank(column) || StringUtils.isBlank(criteria))
			return;

		String query = MACRO_UPDATE_COLUMN
				.replaceAll(":criteria", criteria)
				.replaceAll(":column", column);

		TableBuilderUtil.runUpdateSQL(query);
	}

	/**
	 * updates due_date based on provided criteria
	 */
	private void updateEDD(String source, Integer days, String criteria, String column) {
		if (days == null || StringUtils.isBlank(criteria))
			return;

		String query = MACRO_UPDATE_EDD
				.replaceAll(":source", source)
				.replaceAll(":criteria", criteria)
				.replaceAll(":days", days.toString())
				.replaceAll(":column", column);

		TableBuilderUtil.runUpdateSQL(query);
	}

	/**
	 * adds a number of days to a given date
	 */
	private Date addWeeksToDate(Date date, Integer weeks) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.WEEK_OF_MONTH, weeks);
		return c.getTime();
	}

}
