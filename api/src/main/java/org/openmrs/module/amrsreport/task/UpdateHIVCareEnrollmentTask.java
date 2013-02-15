package org.openmrs.module.amrsreport.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.scheduler.tasks.AbstractTask;

import java.util.concurrent.TimeUnit;

/**
 * Updates HIV Care HIVCareEnrollment table with latest enrollment information
 */
public class UpdateHIVCareEnrollmentTask extends AbstractTask {

	private static final Log log = LogFactory.getLog(UpdateHIVCareEnrollmentTask.class);

	private static final String QUERY_TRUNCATE_TABLE =
			"delete from amrsreport_hiv_care_enrollment";

	private static String QUERY_DELETE_FAKE_PATIENTS =
			"delete" +
					" from amrsreport_hiv_care_enrollment" +
					" where person_id in (" +
					"   select person_id" +
					"   from person_attribute pa" +
					"   where" +
					"     pa.person_attribute_type_id = 28" +
					"     and pa.voided = 0" +
					"     and pa.value =\"true\")";

	private static final String QUERY_INSERT_FROM_ENCOUNTERS =
			"insert into amrsreport_hiv_care_enrollment (" +
					"   person_id, " +
					"   enrollment_location_id," +
					"   enrollment_date," +
					"   enrollment_age," +
					"   first_hiv_encounter_id," +
					"   first_hiv_encounter_location_id, " +
					"   first_hiv_encounter_date, " +
					"   first_hiv_encounter_age, " +
					"   enrollment_reason, " +
					"   uuid" +
					" )" +
					" select " +
					"   p.person_id, " +
					"   fir.location_id," +
					"   fir.encounter_datetime," +
					"   datediff(fir.encounter_datetime, p.birthdate) / 365.25," +
					"   fir.encounter_id," +
					"   fir.location_id," +
					"   fir.encounter_datetime," +
					"   datediff(fir.encounter_datetime, p.birthdate) / 365.25," +
					"   \"ENCOUNTER\"," +
					"   UUID()" +
					" from" +
					"   (select " +
					"       patient_id, " +
					"       encounter_id," +
					"       encounter_datetime, " +
					"       location_id," +
					"       encounter_type " +
					"   from encounter" +
					"   where " +
					"       voided=0 " +
					"       and encounter_type in(1,2,3,4,13)" +
					"       group by patient_id" +
					"       order by patient_id, encounter_datetime) fir" +
					" join person p" +
					"   on p.person_id=fir.patient_id" +
					" where p.voided=0";

	private static final String QUERY_UPDATE_LAST_POSITIVE =
			"update " +
					"  amrsreport_hiv_care_enrollment ae" +
					"  join" +
					"  (" +
					"    select " +
					"      o.person_id, o.obs_datetime" +
					"    from " +
					"      obs o join amrsreport_hiv_care_enrollment ae on o.person_id = ae.person_id and ae.enrollment_age < 2" +
					"    where" +
					"      o.voided = 0" +
					"      and (" +
					"        (o.concept_id in (1040, 1030, 1042) and o.value_coded = 703)" +
					"        or" +
					"        (o.concept_id = 6042 and o.value_coded = 1169)" +
					"      )" +
					"    group by o.person_id" +
					"    order by obs_datetime desc" +
					"  ) last" +
					"  on ae.person_id = last.person_id" +
					" set" +
					"  ae.last_positive_obs_date = last.obs_datetime";

	private static final String QUERY_UPDATE_LAST_WHO_STAGE_AND_DATE =
			"update " +
					"  amrsreport_hiv_care_enrollment ae" +
					"  join" +
					"  (" +
					"    select " +
					"      o.person_id, " +
					"      o.obs_datetime," +
					"      if(o.value_coded in (1220,1204), 1, " +
					"        if(o.value_coded in(1221,1205), 2, " +
					"          if(o.value_coded in(1222,1206), 3, " +
					"            if(o.value_coded in(1223,1207),4,0)))) as stage" +
					"    from " +
					"      obs o join amrsreport_hiv_care_enrollment ae on o.person_id = ae.person_id" +
					"    where " +
					"      o.voided = 0" +
					"      and o.concept_id in (1224, 5356)" +
					"    group by o.person_id" +
					"    order by o.obs_datetime desc" +
					"  ) who" +
					"  on who.person_id = ae.person_id" +
					" set" +
					"  ae.last_who_stage = who.stage," +
					"  ae.last_who_stage_date = who.obs_datetime";

	private static final String QUERY_UPDATE_FIRST_ARV_DATE =
			"update " +
					"  amrsreport_hiv_care_enrollment ae" +
					"  join" +
					"  (" +
					"    select " +
					"      o.person_id, " +
					"      o.obs_datetime" +
					"    from " +
					"      obs o join amrsreport_hiv_care_enrollment ae on o.person_id = ae.person_id" +
					"    where " +
					"      o.voided = 0" +
					"      and (" +
					"        o.concept_id in (996, 1085, 1086, 1088, 1147, 1176, 1187, 1250)" +
					"        or (" +
					"          o.concept_id = 1193 " +
					"          and o.value_coded in (630, 792, 6180, 628, 797, 625, 633, 814, 794, 796, 802, 749, 6156)" +
					"        ) or (" +
					"          o.concept_id = 1895" +
					"          and o.value_coded in (select concept_id from concept_set where concept_set=1085)" +
					"        ) or (" +
					"          o.concept_id in (2157, 2154) " +
					"          and o.value_coded not in (1065, 1066)" +
					"        )" +
					"      )" +
					"    group by o.person_id" +
					"    order by o.obs_datetime asc" +
					"  ) art" +
					"  on art.person_id = ae.person_id" +
					" set" +
					"  ae.first_arv_date = art.obs_datetime";

	private static final String QUERY_INVALIDATE_WITH_NO_POSITIVE_OBS =
			"update amrsreport_hiv_care_enrollment" +
					" set" +
					"   enrollment_date = NULL," +
					"   enrollment_location_id = NULL," +
					"   enrollment_age = NULL," +
					"   enrollment_reason = \"INVALID\"" +
					" where" +
					"   enrollment_age < 2" +
					"   and last_positive_obs_date is null";

	private static final String QUERY_UPDATE_LAST_NEGATIVE =
			"update amrsreport_hiv_care_enrollment ae" +
					"  join" +
					"  (" +
					"    select " +
					"      o.person_id, o.obs_datetime" +
					"    from " +
					"      obs o join amrsreport_hiv_care_enrollment ae" +
					"        on o.person_id = ae.person_id" +
					"        and ae.enrollment_age < 2 and ae.enrollment_reason <> \"INVALID\"" +
					"    where" +
					"      o.voided = 0" +
					"      and (o.concept_id in (1040, 1030, 1042) and o.value_coded = 664)" +
					"    group by o.person_id" +
					"    order by obs_datetime desc" +
					"  ) last" +
					"  on ae.person_id = last.person_id" +
					" set" +
					"  ae.last_negative_obs_date = last.obs_datetime";

	private static final String QUERY_INVALIDATE_CONFLICTING_PEDS =
			"update amrsreport_hiv_care_enrollment" +
					" set" +
					"  enrollment_date = NULL," +
					"  enrollment_location_id = NULL," +
					"  enrollment_age = NULL," +
					"  enrollment_reason = \"INVALID\"" +
					" where" +
					"  enrollment_age < 2" +
					"  and last_who_stage is null" +
					"  and first_arv_date is null" +
					"  and last_negative_obs_date is not null" +
					"  and last_positive_obs_date is not null" +
					"  and last_negative_obs_date >= last_positive_obs_date";


	private static final String QUERY_UPDATE_FIRST_POSITIVE =
			"update amrsreport_hiv_care_enrollment ae" +
					"  join person p on p.person_id = ae.person_id" +
					"  join" +
					"  (" +
					"    select " +
					"      o.person_id, o.obs_datetime, o.location_id" +
					"    from " +
					"      obs o join amrsreport_hiv_care_enrollment ae" +
					"        on o.person_id = ae.person_id" +
					"        and ae.enrollment_age < 2" +
					"        and ae.enrollment_reason <> \"INVALID\"" +
					"    where" +
					"      o.voided = 0" +
					"      and (" +
					"        (o.concept_id in (1040, 1030, 1042) and o.value_coded = 703)" +
					"        or" +
					"        (o.concept_id = 6042 and o.value_coded = 1169)" +
					"      )" +
					"    group by o.person_id" +
					"    order by obs_datetime asc" +
					"  ) first" +
					"  on ae.person_id = first.person_id" +
					" set" +
					"  ae.enrollment_location_id = first.location_id," +
					"  ae.enrollment_date = first.obs_datetime," +
					"  ae.enrollment_age = datediff(first.obs_datetime, p.birthdate) / 365.25," +
					"  ae.first_positive_obs_location_id = first.location_id," +
					"  ae.first_positive_obs_date = first.obs_datetime," +
					"  ae.enrollment_reason = \"OBSERVATION\"";

	private static final String QUERY_UPDATE_TRANSFER_INS =
			"update" +
					"  amrsreport_hiv_care_enrollment ae" +
					"  join" +
					"  (" +
					"    select " +
					"      o.person_id, 1 as transfer, o.obs_datetime" +
					"    from " +
					"      obs o join amrsreport_hiv_care_enrollment ae on o.encounter_id = ae.first_hiv_encounter_id" +
					"    where " +
					"      o.concept_id in (7015, 7016) and o.value_coded = 1287" +
					"    group by person_id" +
					"  ) t" +
					"  on ae.person_id = t.person_id" +
					" set" +
					"   transferred_in = t.transfer," +
					"   transferred_in_date = t.obs_datetime";

	@Override
	public void execute() {
		AdministrationService administrationService = Context.getAdministrationService();

		log.info("Rebuilding HIV Care Enrollment table now");

		Long startTime = System.currentTimeMillis();

		// clear the table
		administrationService.executeSQL(QUERY_TRUNCATE_TABLE, false);

		// insert from enrollment query
		administrationService.executeSQL(QUERY_INSERT_FROM_ENCOUNTERS, false);

		// remove fake patients
		administrationService.executeSQL(QUERY_DELETE_FAKE_PATIENTS, false);

		// update peds with latest positive obs
		administrationService.executeSQL(QUERY_UPDATE_LAST_POSITIVE, false);

		// update everyone with latest WHO stage
		administrationService.executeSQL(QUERY_UPDATE_LAST_WHO_STAGE_AND_DATE, false);

		// update everyone with first ARV date
		administrationService.executeSQL(QUERY_UPDATE_FIRST_ARV_DATE, false);

		// mark peds with no positive observations as invalid
		administrationService.executeSQL(QUERY_INVALIDATE_WITH_NO_POSITIVE_OBS, false);

		// update peds with latest negative obs
		administrationService.executeSQL(QUERY_UPDATE_LAST_NEGATIVE, false);

		// mark peds with negative > positive obs as invalid
		administrationService.executeSQL(QUERY_INVALIDATE_CONFLICTING_PEDS, false);

		// update remaining peds with first positive obs and location
		administrationService.executeSQL(QUERY_UPDATE_FIRST_POSITIVE, false);

		// update everyone with transfer in status
		administrationService.executeSQL(QUERY_UPDATE_TRANSFER_INS, false);

		Long millis = System.currentTimeMillis() - startTime;
		String elapsed = String.format("%d min, %d sec",
				TimeUnit.MILLISECONDS.toMinutes(millis),
				TimeUnit.MILLISECONDS.toSeconds(millis) -
						TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
		);

		log.info("Finished rebuilding HIV Care Enrollment table, time elapsed = " + elapsed);
	}
}
