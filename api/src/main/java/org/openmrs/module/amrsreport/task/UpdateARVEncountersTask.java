package org.openmrs.module.amrsreport.task;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.scheduler.tasks.AbstractTask;

/**
 * Updates the ARV encounter table with flags for each ARV drug for every related encounter
 */
public class UpdateARVEncountersTask extends AbstractTask {

	private static final Log log = LogFactory.getLog(UpdateARVEncountersTask.class);

	private static final String QUERY_DROP_TABLE =
			"DROP TABLE IF EXISTS `amrsreport_arv_encounter`";

	private static final String QUERY_CREATE_TABLE =
			"CREATE TABLE `amrsreport_arv_encounter` (" +
					"  `arv_encounter_id` int(11) NOT NULL AUTO_INCREMENT," +
					"  `patient_id` int(11) DEFAULT NULL," +
					"  `encounter_id` int(11) DEFAULT NULL," +
					"  `encounter_date` datetime DEFAULT NULL," +
					"  `STAVUDINE` int(1) DEFAULT 0," +
					"  `LAMIVUDINE` int(1) DEFAULT 0," +
					"  `AZT` int(1) DEFAULT 0," +
					"  `NEVIRAPINE` int(1) DEFAULT 0," +
					"  `EFAVIRENZ` int(1) DEFAULT 0," +
					"  `NELFINAVIR` int(1) DEFAULT 0," +
					"  `INDINAVIR` int(1) DEFAULT 0," +
					"  `EMTRICITABINE` int(1) DEFAULT 0," +
					"  `LOPINAVIR` int(1) DEFAULT 0," +
					"  `RITONAVIR` int(1) DEFAULT 0," +
					"  `DIDANOSINE` int(1) DEFAULT 0," +
					"  `TENOFOVIR` int(1) DEFAULT 0," +
					"  `ABACAVIR` int(1) DEFAULT 0," +
					"  `RALTEGRAVIR` int(1) DEFAULT 0," +
					"  `DARUNAVIR` int(1) DEFAULT 0," +
					"  `ETRAVIRINE` int(1) DEFAULT 0," +
					"  `ATAZANAVIR` int(1) DEFAULT 0," +
					"  `UNK` int(1) DEFAULT 0," +
					"  `OTHER` int(1) DEFAULT 0," +
					"  PRIMARY KEY (`arv_encounter_id`)," +
					"  KEY `amrsreport_arv_encounter_encounter_ref` (`encounter_id`)," +
					"  CONSTRAINT `amrsreport_unique_encounter_id` UNIQUE (encounter_id)," +
					"  CONSTRAINT `amrsreport_arv_encounter_patient_ref` FOREIGN KEY (`patient_id`) REFERENCES `patient` (`patient_id`)," +
					"  CONSTRAINT `amrsreport_arv_encounter_encounter_ref` FOREIGN KEY (`encounter_id`) REFERENCES `encounter` (`encounter_id`)" +
					") ENGINE=InnoDB DEFAULT CHARSET=utf8";

	private static String MACRO_UPDATE_DRUG =
			"insert into amrsreport_arv_encounter (" +
					"  encounter_id, " +
					"  patient_id, " +
					"  encounter_date, " +
					"  :column" +
					" )" +
					"  select " +
					"    e.encounter_id, e.patient_id, e.encounter_datetime, 1" +
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
					"    and o.concept_id in (1255,1250,1895)" +
					"    and o.value_coded :condition" +
					"  group by" +
					"    e.encounter_id" +
					"  order by" +
					"    e.encounter_datetime asc" +
					" ON DUPLICATE KEY UPDATE" +
					"  :column = 1";

	private AdministrationService administrationService;

	/**
	 * drops, creates and fills out the ARV encounter table
	 */
	@Override
	public void execute() {
		// drop the table
		getAdministrationService().executeSQL(QUERY_DROP_TABLE, false);

		// recreate the table
		getAdministrationService().executeSQL(QUERY_CREATE_TABLE, false);

		// update all of the ARVs
		updateARVs("STAVUDINE", 625, 792, 6965);
		updateARVs("LAMIVUDINE", 628, 792, 630, 1400, 6467, 6679, 6964, 6965);
		updateARVs("AZT", 797, 6467, 630);
		updateARVs("NEVIRAPINE", 631, 6467, 792);
		updateARVs("EFAVIRENZ", 633, 6964);
		updateARVs("NELFINAVIR", 635);
		updateARVs("INDINAVIR", 749);
		updateARVs("EMTRICITABINE", 791, 6180);
		updateARVs("LOPINAVIR", 794);
		updateARVs("RITONAVIR", 794, 6160, 795);
		updateARVs("DIDANOSINE", 796);
		updateARVs("TENOFOVIR", 802, 6180, 1400, 6964);
		updateARVs("ABACAVIR", 814, 6679);
		updateARVs("RALTEGRAVIR", 6156);
		updateARVs("DARUNAVIR", 6157);
		updateARVs("ETRAVIRINE", 6158);
		updateARVs("ATAZANAVIR", 6159);
		updateARVs("UNK", 5811);
		updateARVs("OTHER", 5424);
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

		// modify query with condition and column
		String query = MACRO_UPDATE_DRUG.replaceAll(":condition", condition).replaceAll(":column", drug);

		// execute the query
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
