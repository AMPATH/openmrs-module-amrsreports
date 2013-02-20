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
					"     and pa.value ='true')";

	private static final String QUERY_INSERT_FROM_ENCOUNTERS =
			"insert into amrsreport_hiv_care_enrollment (" +
					"   person_id, " +
					"   first_hiv_encounter_id," +
					"   first_hiv_encounter_location_id, " +
					"   first_hiv_encounter_date, " +
					"   first_hiv_encounter_age, " +
					"   uuid" +
					" )" +
					" select " +
					"   p.person_id, " +
					"   fir.encounter_id," +
					"   fir.location_id," +
					"   fir.encounter_datetime," +
					"   datediff(fir.encounter_datetime, p.birthdate) / 365.25," +
					"   UUID()" +
					" from" +
					"   (select " +
					"       patient_id, " +
					"       encounter_id," +
					"       encounter_datetime, " +
					"       location_id" +
					"   from (" +
					"   	   select " +
					"   	      patient_id, encounter_id, encounter_datetime, location_id " +
					"   	      from encounter " +
					"          join person p" +
					"          on p.person_id = encounter.patient_id and p.voided = 0" +
					"   	      where " +
					"   	          encounter_type in (1, 2, 3, 4, 13) " +
					"   	          and encounter.voided=0 " +
					"   	      order by encounter_datetime asc" +
					"   ) e" +
					"   group by patient_id) fir" +
					" join person p" +
					"   on p.person_id=fir.patient_id";

	private static final String QUERY_UPDATE_LAST_POSITIVE =
			"update amrsreport_hiv_care_enrollment ae" +
					"  join" +
					"  (" +
					"  	select person_id, obs_datetime" +
					"   from (" +
					"	    select " +
					"	      o.person_id, o.obs_datetime" +
					"	    from " +
					"	      obs o" +
					"           join amrsreport_hiv_care_enrollment ae" +
					"           on o.person_id = ae.person_id and ae.enrollment_reason is NULL" +
					"	    where" +
					"	      o.voided = 0" +
					"	      and (" +
					"	        (o.concept_id in (1040, 1030, 1042) and o.value_coded = 703)" +
					"	        or" +
					"	        (o.concept_id = 6042 and o.value_coded = 1169)" +
					"	      )" +
					"	    order by obs_datetime desc" +
					"	) ordered" +
					"    group by person_id" +
					"  ) last" +
					"  on ae.person_id = last.person_id" +
					" set" +
					"  ae.last_positive_obs_date = last.obs_datetime";

	private static final String QUERY_UPDATE_LAST_WHO_STAGE_AND_DATE =
			"update amrsreport_hiv_care_enrollment ae" +
					"  join" +
					"  (" +
					"  	select person_id, obs_datetime, stage from (" +
					"	    select " +
					"	      o.person_id, " +
					"	      o.obs_datetime," +
					"	      if(o.value_coded in (1220,1204), 1, " +
					"	        if(o.value_coded in(1221,1205), 2, " +
					"	          if(o.value_coded in(1222,1206), 3, " +
					"	            if(o.value_coded in(1223,1207),4,0)))) as stage" +
					"	    from " +
					"	      obs o join amrsreport_hiv_care_enrollment ae on o.person_id = ae.person_id" +
					"	    where " +
					"	      o.voided = 0" +
					"	      and o.concept_id in (1224, 5356)" +
					"	    order by o.obs_datetime desc" +
					"	) ordered" +
					"    group by person_id" +
					"  ) who" +
					"  on who.person_id = ae.person_id" +
					" set" +
					"  ae.last_who_stage = who.stage," +
					"  ae.last_who_stage_date = who.obs_datetime";

	private static final String QUERY_UPDATE_FIRST_ARV_DATE =
			"update amrsreport_hiv_care_enrollment ae" +
					"  join" +
					"  (" +
					"  	select person_id, obs_datetime, location_id from (" +
					"	    select " +
					"	      o.person_id, " +
					"	      o.obs_datetime," +
					"	      o.location_id" +
					"	    from " +
					"	      obs o join amrsreport_hiv_care_enrollment ae on o.person_id = ae.person_id" +
					"	    where " +
					"	      o.voided = 0" +
					"	      and (" +
					"	        o.concept_id in (966, 1085, 1086, 1088, 1187, 1250)" +
					"	        or (" +
					"	          o.concept_id = 1193 " +
					"	          and o.value_coded in (630, 792, 6180, 628, 797, 625, 633, 814, 794, 796, 802, 749, 6156)" +
					"	        ) or (" +
					"	          o.concept_id = 1895" +
					"	          and o.value_coded in (select concept_id from concept_set where concept_set = 1085)" +
					"	        ) or (" +
					"	          o.concept_id in (2157, 2154) " +
					"	          and o.value_coded not in (1065, 1066)" +
					"	        )" +
					"	      )" +
					"	    order by o.obs_datetime asc" +
					"	) ordered" +
					"    group by person_id" +
					"  ) arv" +
					"  on arv.person_id = ae.person_id" +
					" set" +
					"  ae.first_arv_date = arv.obs_datetime," +
					"  ae.first_arv_location_id = arv.location_id";

	private static final String QUERY_UPDATE_LAST_NEGATIVE =
			"update amrsreport_hiv_care_enrollment ae" +
					"  join" +
					"  (" +
					"  	select person_id, obs_datetime from (" +
					"	    select " +
					"	      o.person_id, o.obs_datetime" +
					"	    from " +
					"	      obs o join amrsreport_hiv_care_enrollment ae" +
					"	        on o.person_id = ae.person_id and ae.enrollment_reason is NULL" +
					"	    where" +
					"	      o.voided = 0" +
					"	      and (o.concept_id in (1040, 1030, 1042) and o.value_coded = 664)" +
					"	    order by obs_datetime desc" +
					"    ) ordered" +
					"    group by person_id" +
					"  ) last" +
					"  on ae.person_id = last.person_id" +
					" set" +
					"  ae.last_negative_obs_date = last.obs_datetime";

	private static final String QUERY_UPDATE_FIRST_POSITIVE =
			"update amrsreport_hiv_care_enrollment ae" +
					"  join person p on p.person_id = ae.person_id" +
					"  join" +
					"  (" +
					"  	select person_id, obs_datetime, location_id from (" +
					"	    select " +
					"	      o.person_id, o.obs_datetime, o.location_id" +
					"	    from " +
					"	      obs o join amrsreport_hiv_care_enrollment ae" +
					"	        on o.person_id = ae.person_id" +
					"           and ae.last_positive_obs_date is not NULL" +
					"	        and ae.enrollment_reason is NULL" +
					"	    where" +
					"	      o.voided = 0" +
					"	      and (" +
					"	        (o.concept_id in (1040, 1030, 1042) and o.value_coded = 703)" +
					"	        or" +
					"	        (o.concept_id = 6042 and o.value_coded = 1169)" +
					"	      )" +
					"	    order by obs_datetime asc" +
					"    ) ordered" +
					"    group by person_id" +
					"  ) first" +
					"  on ae.person_id = first.person_id" +
					" set" +
					"  ae.enrollment_location_id = first.location_id," +
					"  ae.enrollment_date = first.obs_datetime," +
					"  ae.enrollment_age = datediff(first.obs_datetime, p.birthdate) / 365.25," +
					"  ae.first_positive_obs_location_id = first.location_id," +
					"  ae.first_positive_obs_date = first.obs_datetime";

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

	private static final String QUERY_UPDATE_DISCONTINUES =
			"update" +
					"	amrsreport_hiv_care_enrollment ae" +
					"	join (" +
					"		select" +
					"			patient_id, encounter_datetime as last_encounter_date" +
					"		from (" +
					"			select" +
					"				patient_id, encounter_datetime" +
					"			from " +
					"				encounter" +
					"				join amrsreport_hiv_care_enrollment ae" +
					"				on encounter.patient_id = ae.person_id" +
					"			where" +
					"				voided = 0" +
					"				and encounter_type in (1,2,3,4,13,10,11,12,17,18,19,20,21,22,23,25,26,44,46,47,48,67)" +
					"			order by  encounter_datetime desc" +
					"		) ordered" +
					"		group by patient_id" +
					"	) encounters" +
					"	on ae.person_id = encounters.patient_id" +
					"	join (" +
					"		select" +
					"			person_id, obs_datetime as last_obs_date, location_id as last_obs_location_id" +
					"		from (" +
					"			select" +
					"				o.person_id, o.obs_datetime, o.location_id" +
					"			from " +
					"				obs o" +
					"				join amrsreport_hiv_care_enrollment ae" +
					"				on o.person_id = ae.person_id" +
					"			where" +
					"				o.voided = 0" +
					"				and (" +
					"					(o.concept_id = 1946 and o.value_coded = 1065)" +
					"					or (o.concept_id = 1596 and o.value_coded = 1946)" +
					"				)" +
					"			order by obs_datetime desc" +
					"		) ordered" +
					"		group by person_id" +
					"	) discontinued" +
					"	on ae.person_id = discontinued.person_id" +
					" set" +
					" 	ae.last_discontinue_date = last_obs_date" +
					" where " +
					"	last_encounter_date <= last_obs_date";

	private static final String QUERY_FILL_ENROLLMENT_FROM_FIRST_ENCOUNTER =
			"update amrsreport_hiv_care_enrollment" +
					" set" +
					"  enrollment_date = first_hiv_encounter_date," +
					"  enrollment_age = first_hiv_encounter_age," +
					"  enrollment_location_id = first_hiv_encounter_location_id," +
					"  enrollment_reason = 'FIRST ENCOUNTER OVER TWO'" +
					" where" +
					"  first_hiv_encounter_age >= 2";

	private static final String QUERY_FILL_ENROLLMENT_FOR_PEDS_WITH_ONLY_ADULT_ENCOUNTERS =
			"update amrsreport_hiv_care_enrollment ae" +
					" set" +
					"  enrollment_date = first_hiv_encounter_date," +
					"  enrollment_age = first_hiv_encounter_age," +
					"  enrollment_location_id = first_hiv_encounter_location_id," +
					"  enrollment_reason = 'ONLY ADULT ENCOUNTERS'" +
					" where" +
					"  enrollment_reason is NULL" +
					"  and not exists (" +
					"    select 1" +
					"    from encounter" +
					"    where " +
					"      encounter_type in (3,4)" +
					"      and patient_id = ae.person_id" +
					"      and voided =  0)";

	private static final String QUERY_FILL_ENROLLMENT_FROM_NON_CONFLICTING_OBS =
			"update amrsreport_hiv_care_enrollment" +
					" set" +
					"  enrollment_reason = 'POSITIVE OBSERVATION'" +
					" where" +
					"  enrollment_reason is NULL" +
					"  and last_positive_obs_date is not NULL" +
					"  and (" +
					"    last_negative_obs_date is NULL" +
					"    or (" +
					"      last_negative_obs_date is not NULL" +
					"      and last_negative_obs_date < last_positive_obs_date" +
					"    )" +
					"  )";

	private static final String QUERY_FILL_ENROLLMENT_FROM_CONFLICTING_OBS_WITH_WHO_AND_ARVS =
			"update amrsreport_hiv_care_enrollment" +
					" set" +
					"  enrollment_reason = 'VERIFIED CONFLICTING OBSERVATIONS'" +
					" where" +
					"  enrollment_reason is NULL" +
					"  and last_positive_obs_date is not NULL" +
					"  and last_negative_obs_date is not NULL" +
					"  and last_negative_obs_date >= last_positive_obs_date" +
					"  and last_who_stage_date is not NULL" +
					"  and first_arv_date is not NULL";

	private static final String QUERY_FILL_ENROLLMENT_FOR_ARV_ONLY =
			"update amrsreport_hiv_care_enrollment ae" +
					"  join person p on p.person_id = ae.person_id" +
					" set" +
					"  ae.enrollment_date = ae.first_arv_date," +
					"  ae.enrollment_age = datediff(ae.first_arv_date, p.birthdate) / 365.25," +
					"  ae.enrollment_location_id = ae.first_arv_location_id," +
					"  ae.enrollment_reason = 'ARVS'" +
					" where" +
					"  ae.enrollment_reason is NULL" +
					"  and ae.last_positive_obs_date is NULL" +
					"  and ae.first_arv_date is not NULL";

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

		// update everyone with latest WHO stage
		administrationService.executeSQL(QUERY_UPDATE_LAST_WHO_STAGE_AND_DATE, false);

		// update everyone with first ARV date
		administrationService.executeSQL(QUERY_UPDATE_FIRST_ARV_DATE, false);

		// fill out enrollment info for patients >= 2 years old at first encounter (Group A)
		administrationService.executeSQL(QUERY_FILL_ENROLLMENT_FROM_FIRST_ENCOUNTER, false);

		// fill out enrollment info for remainin patients with only adult encounters (Group A)
		administrationService.executeSQL(QUERY_FILL_ENROLLMENT_FOR_PEDS_WITH_ONLY_ADULT_ENCOUNTERS, false);

		// update remaining with latest positive obs
		administrationService.executeSQL(QUERY_UPDATE_LAST_POSITIVE, false);

		// update remaining with latest negative obs
		administrationService.executeSQL(QUERY_UPDATE_LAST_NEGATIVE, false);

		// update remaining with first positive obs and location
		// NOTE: this fills out enrollment data but leaves reason blank
		administrationService.executeSQL(QUERY_UPDATE_FIRST_POSITIVE, false);

		// fill out enrollment info for remaining with non-conflicting observations (Groups B and C)
		administrationService.executeSQL(QUERY_FILL_ENROLLMENT_FROM_NON_CONFLICTING_OBS, false);

		// fill out enrollment info for conflicting observations with WHO Stage and on ARVs (Group D)
		administrationService.executeSQL(QUERY_FILL_ENROLLMENT_FROM_CONFLICTING_OBS_WITH_WHO_AND_ARVS, false);

		// fill out enrollment info for no positive observations but with ARVs (Group E)
		administrationService.executeSQL(QUERY_FILL_ENROLLMENT_FOR_ARV_ONLY, false);

		// update everyone with transfer in status
		administrationService.executeSQL(QUERY_UPDATE_TRANSFER_INS, false);

		// update everyone with discontinue status
		administrationService.executeSQL(QUERY_UPDATE_DISCONTINUES, false);

		Long millis = System.currentTimeMillis() - startTime;
		String elapsed = String.format("%d min, %d sec",
				TimeUnit.MILLISECONDS.toMinutes(millis),
				TimeUnit.MILLISECONDS.toSeconds(millis) -
						TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
		);

		log.info("Finished rebuilding HIV Care Enrollment table, time elapsed = " + elapsed);
	}
}
