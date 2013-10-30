package org.openmrs.module.amrsreports.builder;

import org.openmrs.module.amrsreports.util.TableBuilderUtil;

/**
 * Builds the HIV Care Enrollment table via SQL statements
 */
public class HIVCareEnrollmentBuilder {

	private static HIVCareEnrollmentBuilder instance;

	public static HIVCareEnrollmentBuilder getInstance() {
		if (instance == null)
			instance = new HIVCareEnrollmentBuilder();
		return instance;
	}

	private HIVCareEnrollmentBuilder() {
		// pass
	}

	private static final String QUERY_CLEAR_RECORDS =
			"delete from amrsreports_hiv_care_enrollment";

	private static final String QUERY_DELETE_FAKE_PATIENTS =
			"delete" +
					" from amrsreports_hiv_care_enrollment" +
					" where patient_id in (" +
					"   select person_id" +
					"   from person_attribute pa" +
					"   where" +
					"     pa.person_attribute_type_id = 28" +
					"     and pa.voided = 0" +
					"     and pa.value ='true')";

	private static final String QUERY_INSERT_FROM_ENCOUNTERS =
			"insert into amrsreports_hiv_care_enrollment (" +
					"   patient_id," +
					"   first_hiv_encounter_id," +
					"   first_hiv_encounter_location_id," +
					"   first_hiv_encounter_date," +
					"   first_hiv_encounter_age," +
					"   uuid" +
					" )" +
					" select" +
					"   fir.patient_id," +
					"   fir.encounter_id," +
					"   fir.location_id," +
					"   fir.encounter_datetime," +
					"   datediff(fir.encounter_datetime, p.birthdate) / 365.25," +
					"   UUID()" +
					" from" +
					"   (select" +
					"       patient_id," +
					"       encounter_id," +
					"       encounter_datetime," +
					"       location_id" +
					"   from (" +
					"   	   select" +
					"   	      encounter.patient_id, encounter_id, encounter_datetime, location_id" +
					"   	      from encounter " +
					"          join person p" +
					"          on p.person_id = encounter.patient_id and p.voided = 0" +
					"          join patient pt" +
					"          on pt.patient_id = encounter.patient_id and pt.voided = 0" +
					"   	      where" +
					"   	          encounter_type in (1,2,3,4,13)" +
					"   	          and encounter.voided=0" +
					"   	      order by encounter_datetime asc" +
					"   ) e" +
					"   group by patient_id) fir" +
					" join person p" +
					"   on p.person_id=fir.patient_id";

	private static final String QUERY_UPDATE_LAST_POSITIVE =
			"update amrsreports_hiv_care_enrollment ae" +
					"  join" +
					"  (" +
					"  	select person_id, obs_datetime" +
					"   from (" +
					"	    select " +
					"	      o.person_id, o.obs_datetime" +
					"	    from " +
					"	      obs o" +
					"           join amrsreports_hiv_care_enrollment ae" +
					"           on o.person_id = ae.patient_id" +
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
					"  on ae.patient_id = last.person_id" +
					" set" +
					"  ae.last_positive_obs_date = last.obs_datetime";

	private static final String QUERY_UPDATE_LAST_WHO_STAGE_AND_DATE =
			"update amrsreports_hiv_care_enrollment ae" +
					"  join" +
					"  (" +
					"  	select person_id, obs_datetime, stage from (" +
					"	    select " +
					"	      o.person_id, " +
					"	      o.obs_datetime," +
					"         if(o.value_coded=1204, 'A1'," +
					"           if(o.value_coded=1220, 'P1'," +
					"             if(o.value_coded=1205, 'A2'," +
					"               if(o.value_coded=1221, 'P2'," +
					"                 if(o.value_coded=1206, 'A3'," +
					"                   if(o.value_coded=1222, 'P3'," +
					"                     if(o.value_coded=1207, 'A4'," +
					"                       if(o.value_coded=1223, 'P4', NULL)))))))) as stage" +
					"	    from " +
					"	      obs o join amrsreports_hiv_care_enrollment ae on o.person_id = ae.patient_id" +
					"	    where " +
					"	      o.voided = 0" +
					"	      and o.concept_id in (1224, 5356)" +
					"	    order by o.obs_datetime desc" +
					"	) ordered" +
					"    group by person_id" +
					"  ) who" +
					"  on who.person_id = ae.patient_id" +
					" set" +
					"  ae.last_who_stage = who.stage," +
					"  ae.last_who_stage_date = who.obs_datetime";

	private static final String QUERY_UPDATE_FIRST_ARV_DATE =
			"update amrsreports_hiv_care_enrollment ae" +
					"  join" +
					"  (" +
					"    select patient_id, encounter_date, location_id from (" +
					"      select" +
					"        ac.patient_id," +
					"        ac.encounter_date," +
					"        ac.location_id" +
					"      from" +
					"        amrsreports_arv_current ac join amrsreports_hiv_care_enrollment ae" +
					"          on ac.patient_id = ae.patient_id" +
					"      where" +
					"        ac.on_ART=1" +
					"      order by ac.encounter_date asc" +
					"    ) ordered" +
					"    group by patient_id" +
					"  ) arv" +
					"  on arv.patient_id = ae.patient_id" +
					" set" +
					"  ae.first_arv_date = arv.encounter_date," +
					"  ae.first_arv_location_id = arv.location_id";

	private static final String QUERY_UPDATE_LAST_NEGATIVE =
			"update amrsreports_hiv_care_enrollment ae" +
					"  join" +
					"  (" +
					"  	select person_id, obs_datetime from (" +
					"	    select " +
					"	      o.person_id, o.obs_datetime" +
					"	    from " +
					"	      obs o join amrsreports_hiv_care_enrollment ae" +
					"	        on o.person_id = ae.patient_id" +
					"	    where" +
					"	      o.voided = 0" +
					"	      and (o.concept_id in (1040, 1030, 1042) and o.value_coded = 664)" +
					"	    order by obs_datetime desc" +
					"    ) ordered" +
					"    group by person_id" +
					"  ) last" +
					"  on ae.patient_id = last.person_id" +
					" set" +
					"  ae.last_negative_obs_date = last.obs_datetime";

	private static final String QUERY_UPDATE_FIRST_POSITIVE =
			"update amrsreports_hiv_care_enrollment ae" +
					"  join person p on p.person_id = ae.patient_id" +
					"  join" +
					"  (" +
					"  	select person_id, obs_datetime, location_id from (" +
					"	    select " +
					"	      o.person_id, o.obs_datetime, o.location_id" +
					"	    from " +
					"	      obs o join amrsreports_hiv_care_enrollment ae" +
					"	        on o.person_id = ae.patient_id" +
					"           and ae.last_positive_obs_date is not NULL" +
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
					"  on ae.patient_id = first.person_id" +
					" set" +
					"  ae.first_positive_obs_location_id = first.location_id," +
					"  ae.first_positive_obs_date = first.obs_datetime";

	private static final String QUERY_UPDATE_TRANSFER_INS =
			"update" +
					"  amrsreports_hiv_care_enrollment ae" +
					"  join" +
					"  (" +
					"    select " +
					"      o.person_id, o.obs_datetime" +
					"    from " +
					"      obs o join amrsreports_hiv_care_enrollment ae" +
					"        on o.encounter_id = ae.first_hiv_encounter_id" +
					"    where" +
					"      o.voided = 0" +
					"      and o.concept_id = 7015" +
					"      and o.value_coded = 1287" +
					"    group by person_id" +
					"  ) t" +
					"  on ae.patient_id = t.person_id" +
					" set" +
					"   transferred_in_date = t.obs_datetime";

	private static final String QUERY_UPDATE_DISCONTINUES =
			"update" +
					"	amrsreports_hiv_care_enrollment ae" +
					"	join (" +
					"		select" +
					"			patient_id, encounter_datetime as last_encounter_date, location_id as last_encounter_location" +
					"		from (" +
					"			select" +
					"				ae.patient_id, encounter_datetime, location_id" +
					"			from " +
					"				encounter" +
					"				join amrsreports_hiv_care_enrollment ae" +
					"				  on encounter.patient_id = ae.patient_id" +
					"			where" +
					"				voided = 0" +
					"				and encounter_type in (1,2,3,4,13,10,11,12,17,18,19,20,21,22,23,25,26,44,46,47,48,67)" +
					"			order by encounter_datetime desc" +
					"		) ordered" +
					"		group by patient_id" +
					"	) encounters" +
					"	on ae.patient_id = encounters.patient_id" +
					"	left join (" +
					"		select" +
					"			person_id, obs_datetime as last_obs_date, value_coded as last_reason" +
					"		from (" +
					"			select" +
					"				o.person_id, o.obs_datetime, o.value_coded" +
					"			from " +
					"				obs o" +
					"				join amrsreports_hiv_care_enrollment ae" +
					"			      on o.person_id = ae.patient_id" +
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
					"	on ae.patient_id = discontinued.person_id" +
					" set" +
					"   ae.last_hiv_encounter_date = last_encounter_date," +
					"   ae.last_hiv_encounter_location_id = last_encounter_location," +
					" 	ae.last_discontinue_date = last_obs_date," +
					"   ae.last_discontinue_reason = last_reason";

	private static final String QUERY_FILL_ENROLLMENT_FROM_FIRST_ENCOUNTER =
			"update amrsreports_hiv_care_enrollment" +
					" set" +
					"  enrollment_date = first_hiv_encounter_date," +
					"  enrollment_age = first_hiv_encounter_age," +
					"  enrollment_location_id = first_hiv_encounter_location_id," +
					"  enrollment_reason = 'FIRST ENCOUNTER OVER TWO'" +
					" where" +
					"  first_hiv_encounter_age >= 2";

	private static final String QUERY_FILL_ENROLLMENT_FOR_PEDS_WITH_ONLY_ADULT_ENCOUNTERS =
			"update amrsreports_hiv_care_enrollment ae" +
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
					"      and patient_id = ae.patient_id" +
					"      and voided =  0)";

	private static final String QUERY_FILL_ENROLLMENT_FROM_FIRST_POSITIVE_OBS =
			"update amrsreports_hiv_care_enrollment ae" +
					"  join person p on p.person_id = ae.patient_id" +
					" set" +
					"  ae.enrollment_location_id = ae.first_positive_obs_location_id," +
					"  ae.enrollment_date = ae.first_positive_obs_date," +
					"  ae.enrollment_age = datediff(ae.first_positive_obs_date, p.birthdate) / 365.25," +
					"  ae.enrollment_reason = 'POSITIVE OBSERVATION'" +
					" where" +
					"  ae.enrollment_reason is NULL" +
					"  and ae.last_positive_obs_date is not NULL";

	private static final String QUERY_FILL_ENROLLMENT_FROM_ARVS =
			"update amrsreports_hiv_care_enrollment ae" +
					"  join person p on p.person_id = ae.patient_id" +
					" set" +
					"  ae.enrollment_date = ae.first_arv_date," +
					"  ae.enrollment_age = datediff(ae.first_arv_date, p.birthdate) / 365.25," +
					"  ae.enrollment_location_id = ae.first_arv_location_id," +
					"  ae.enrollment_reason = 'ARVS'" +
					" where" +
					"  (" +
					"    ae.enrollment_reason is NULL" +
					"    or (" +
					"      ae.enrollment_reason = 'POSITIVE OBSERVATION'" +
					"      and ae.enrollment_date > ae.first_arv_date" +
					"    )" +
					"  )" +
					"  and ae.last_who_stage_date is not NULL" +
					"  and ae.first_arv_date is not NULL";

	public void execute() {
		// clear the table
		TableBuilderUtil.runUpdateSQL(QUERY_CLEAR_RECORDS);

		// insert from enrollment query
		TableBuilderUtil.runUpdateSQL(QUERY_INSERT_FROM_ENCOUNTERS);

		// remove fake patients
		TableBuilderUtil.runUpdateSQL(QUERY_DELETE_FAKE_PATIENTS);

		// update everyone with latest WHO stage
		TableBuilderUtil.runUpdateSQL(QUERY_UPDATE_LAST_WHO_STAGE_AND_DATE);

		// update everyone with first ARV date
		TableBuilderUtil.runUpdateSQL(QUERY_UPDATE_FIRST_ARV_DATE);

		// update everyone with latest positive obs
		TableBuilderUtil.runUpdateSQL(QUERY_UPDATE_LAST_POSITIVE);

		// update everyone with latest negative obs
		TableBuilderUtil.runUpdateSQL(QUERY_UPDATE_LAST_NEGATIVE);

		// update everyone with first positive obs and location
		TableBuilderUtil.runUpdateSQL(QUERY_UPDATE_FIRST_POSITIVE);

		// fill out enrollment info for patients >= 2 years old at first encounter (Group A)
		TableBuilderUtil.runUpdateSQL(QUERY_FILL_ENROLLMENT_FROM_FIRST_ENCOUNTER);

		// fill out enrollment info for remainin patients with only adult encounters (Group A)
		TableBuilderUtil.runUpdateSQL(QUERY_FILL_ENROLLMENT_FOR_PEDS_WITH_ONLY_ADULT_ENCOUNTERS);

		// fill out enrollment info for remaining with any positive observation (Groups B and C)
		TableBuilderUtil.runUpdateSQL(QUERY_FILL_ENROLLMENT_FROM_FIRST_POSITIVE_OBS);

		// fill out enrollment info for patients taking ARVs if not found yet or if earlier than first positive obs (Group E)
		TableBuilderUtil.runUpdateSQL(QUERY_FILL_ENROLLMENT_FROM_ARVS);

		// update everyone with transfer in status
		TableBuilderUtil.runUpdateSQL(QUERY_UPDATE_TRANSFER_INS);

		// update everyone with discontinue status
		TableBuilderUtil.runUpdateSQL(QUERY_UPDATE_DISCONTINUES);
	}

}
