package org.openmrs.module.amrsreport.task;

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.scheduler.tasks.AbstractTask;

/**
 * Updates the ARV encounter table with flags for each ARV drug for every related encounter
 */
public class UpdateARVEncountersTask extends AbstractTask {

	private static final String MACRO_DROP_TABLE =
			"DROP TABLE IF EXISTS `:table`";

	private static final String MACRO_CREATE_TABLE =
			"CREATE TABLE `:table` (" +
					"  `:table_id` int(11) NOT NULL AUTO_INCREMENT," +
					"  `patient_id` int(11) DEFAULT NULL," +
					"  `encounter_id` int(11) DEFAULT NULL," +
					"  `location_id` int(11) DEFAULT NULL," +
					"  `encounter_date` datetime DEFAULT NULL," +
					"  `on_ART` int(1) DEFAULT 0," +
					"  `ABACAVIR` int(1) DEFAULT 0," +
					"  `ATAZANAVIR` int(1) DEFAULT 0," +
					"  `DARUNAVIR` int(1) DEFAULT 0," +
					"  `DIDANOSINE` int(1) DEFAULT 0," +
					"  `EFAVIRENZ` int(1) DEFAULT 0," +
					"  `EMTRICITABINE` int(1) DEFAULT 0," +
					"  `ETRAVIRINE` int(1) DEFAULT 0," +
					"  `INDINAVIR` int(1) DEFAULT 0," +
					"  `LAMIVUDINE` int(1) DEFAULT 0," +
					"  `LOPINAVIR` int(1) DEFAULT 0," +
					"  `NELFINAVIR` int(1) DEFAULT 0," +
					"  `NEVIRAPINE` int(1) DEFAULT 0," +
					"  `RALTEGRAVIR` int(1) DEFAULT 0," +
					"  `RITONAVIR` int(1) DEFAULT 0," +
					"  `STAVUDINE` int(1) DEFAULT 0," +
					"  `TENOFOVIR` int(1) DEFAULT 0," +
					"  `ZIDOVUDINE` int(1) DEFAULT 0," +
					"  `OTHER` int(1) DEFAULT 0," +
					"  `UNKNOWN` int(1) DEFAULT 0," +
					"  PRIMARY KEY (`:table_id`)," +
					"  KEY `:table_encounter_ref` (`encounter_id`)," +
					"  INDEX `:table_on_ART_idx` (`on_ART`)," +
					"  CONSTRAINT `:table_unique_encounter_id` UNIQUE (encounter_id)," +
					"  CONSTRAINT `:table_patient_ref` FOREIGN KEY (`patient_id`) REFERENCES `patient` (`patient_id`)," +
					"  CONSTRAINT `:table_encounter_ref` FOREIGN KEY (`encounter_id`) REFERENCES `encounter` (`encounter_id`)," +
					"  CONSTRAINT `:table_location_ref` FOREIGN KEY (`location_id`) REFERENCES `location` (`location_id`)" +
					") ENGINE=InnoDB DEFAULT CHARSET=utf8";

	private static String MACRO_UPDATE_DRUG =
			"insert into :table (" +
					"  encounter_id," +
					"  location_id," +
					"  patient_id," +
					"  encounter_date," +
					"  :column" +
					" )" +
					"  select " +
					"    e.encounter_id, e.location_id, e.patient_id, e.encounter_datetime, 1" +
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
					"    and o.concept_id in (:questions)" +
					"    and o.value_coded :condition" +
					"  group by" +
					"    e.encounter_id" +
					"  order by" +
					"    e.encounter_datetime asc" +
					" ON DUPLICATE KEY UPDATE" +
					"  :column = 1";

	private static String MACRO_UPDATE_ON_ART =
			"update :table" +
					" set on_ART=1" +
					" where" +
					"   ABACAVIR +" +
					"   ATAZANAVIR +" +
					"   DARUNAVIR +" +
					"   DIDANOSINE +" +
					"   EFAVIRENZ +" +
					"   EMTRICITABINE +" +
					"   ETRAVIRINE +" +
					"   INDINAVIR +" +
					"   LAMIVUDINE +" +
					"   LOPINAVIR +" +
					"   NELFINAVIR +" +
					"   NEVIRAPINE +" +
					"   RALTEGRAVIR +" +
					"   RITONAVIR +" +
					"   STAVUDINE +" +
					"   TENOFOVIR +" +
					"   ZIDOVUDINE +" +
					"   OTHER +" +
					"   UNKNOWN >= 3";

	private static String TABLE_CURRENT = "amrsreport_arv_current";
	private static String QUESTIONS_CURRENT = "966, 1088, 1250, 1895, 2154";

	private static String TABLE_PREVIOUS = "amrsreport_arv_previous";
	private static String QUESTIONS_PREVIOUS = "1086, 1087, 2157";

	private AdministrationService administrationService;

	/**
	 * drops, creates and fills out the ARV encounter table
	 */
	@Override
	public void execute() {

		for (String table : new String[]{TABLE_CURRENT, TABLE_PREVIOUS}) {
			// drop the table
			getAdministrationService().executeSQL(MACRO_DROP_TABLE.replaceAll(":table", table), false);
			// recreate the table
			getAdministrationService().executeSQL(MACRO_CREATE_TABLE.replaceAll(":table", table), false);
		}

		// update all of the ARVs
		updateARVs("ABACAVIR", 814, 817, 6679);
		updateARVs("ATAZANAVIR", 6159, 6160);
		updateARVs("DARUNAVIR", 6157);
		updateARVs("DIDANOSINE", 796);
		updateARVs("EFAVIRENZ", 633, 6964);
		updateARVs("EMTRICITABINE", 791, 6180);
		updateARVs("ETRAVIRINE", 6158);
		updateARVs("INDINAVIR", 749);
		updateARVs("LAMIVUDINE", 628, 630, 792, 817, 1400, 6467, 6679, 6964, 6965);
		updateARVs("LOPINAVIR", 794);
		updateARVs("NELFINAVIR", 635);
		updateARVs("NEVIRAPINE", 631, 792, 6467);
		updateARVs("RALTEGRAVIR", 6156);
		updateARVs("RITONAVIR", 794, 795, 6160);
		updateARVs("STAVUDINE", 625, 792, 6965);
		updateARVs("TENOFOVIR", 802, 1400, 6180, 6964);
		updateARVs("ZIDOVUDINE", 630, 797, 817, 6467);
		updateARVs("UNKNOWN", 5811);
		updateARVs("OTHER", 5424);

		for (String table : new String[]{TABLE_CURRENT, TABLE_PREVIOUS}) {
			// update on_ART column
			getAdministrationService().executeSQL(MACRO_UPDATE_ON_ART.replaceAll(":table", table), false);
		}
	}

	/**
	 * updates ARV columns based on the drug and related concept IDs
	 */
	private void updateARVs(String drug, Integer... concepts) {
		// do nothing if no concepts were passed in
		if (concepts.length == 0)
			return;

		// determine the condition based on number of concepts
		String condition;
		if (concepts.length == 1)
			condition = "= " + concepts[0];
		else
			condition = String.format("in (%s)", StringUtils.join(concepts, ","));

		String baseQuery = MACRO_UPDATE_DRUG.replaceAll(":condition", condition).replaceAll(":column", drug);

		// run query with for current questions
		String query = baseQuery.replaceAll(":table", TABLE_CURRENT).replaceAll(":questions", QUESTIONS_CURRENT);
		getAdministrationService().executeSQL(query, false);

		// run query with for historical questions
		query = baseQuery.replaceAll(":table", TABLE_PREVIOUS).replaceAll(":questions", QUESTIONS_PREVIOUS);
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
