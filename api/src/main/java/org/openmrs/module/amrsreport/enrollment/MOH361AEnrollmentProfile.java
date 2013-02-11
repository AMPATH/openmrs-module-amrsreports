package org.openmrs.module.amrsreport.enrollment;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;

/**
 * EnrollmentProvider for the MOH 361A Register
 */
public class MOH361AEnrollmentProfile implements EnrollmentProfile {

	private static final Log log = LogFactory.getLog(MOH361AEnrollmentProfile.class);

	private static final String QUERY_TRUNCATE_TABLE = "delete from amrsreport_enrollment";

	private static String QUERY_DELETE_FAKE_PATIENTS =
			"delete" +
					" from amrsreport_enrollment" +
					" where person_id in (" +
					"   select person_id" +
					"   from person_attribute pa" +
					"   where" +
					"     pa.person_attribute_type_id = 28" +
					"     and pa.voided = 0" +
					"     and pa.value =\"true\")";

	private static final String QUERY_INSERT_FROM_ENCOUNTERS =
			"insert into amrsreport_enrollment" +
					" (person_id, location_id, enrollment_date, enrollment_age, enrollment_reason, uuid)" +
					" select " +
					"   p.person_id, " +
					"   fir.location_id," +
					"   fir.encounter_datetime," +
					"   datediff(fir.encounter_datetime, p.birthdate) / 365.25 as age," +
					"   \"ENCOUNTER\"," +
					"   UUID()" +
					" from" +
					"   (select " +
					"       patient_id, " +
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
			"update amrsreport_enrollment ae" +
					" set" +
					"	last_positive_date = (" +
					"		select " +
					"			max(o.obs_datetime) as last_date" +
					"		from " +
					"			obs o" +
					"		where" +
					"			o.person_id = ae.person_id" +
					"			and o.voided = 0" +
					"			and (" +
					"				(o.concept_id in (1040, 1030, 1042) and o.value_coded = 703)" +
					"				or" +
					"				(o.concept_id = 6042 and o.value_coded = 1169)" +
					"			)" +
					"	)" +
					" where" +
					"	ae.enrollment_age < 2";

	private static final String QUERY_UPDATE_LAST_NEGATIVE =
			"update amrsreport_enrollment ae" +
					" set" +
					"	last_negative_date = (" +
					"		select " +
					"			max(o.obs_datetime) as last_date" +
					"		from " +
					"			obs o" +
					"		where" +
					"			o.person_id = ae.person_id" +
					"			and o.voided = 0" +
					"			and (o.concept_id in (1040, 1030, 1042) and o.value_coded = 664)" +
					"	)" +
					" where" +
					"	ae.enrollment_age < 2";
	;

	private static final String QUERY_DELETE_CONFLICTING_PEDS =
			"delete from amrsreport_enrollment" +
					" where" +
					"   enrollment_age < 2" +
					"   and (" +
					"      last_negative_date is not null" +
					"      and last_positive_date is not null" +
					"      and last_negative_date >= last_positive_date)";

	private static final String QUERY_UPDATE_FIRST_POSITIVE =
			"update " +
					"	amrsreport_enrollment ae" +
					"	join" +
					"	(" +
					"		select " +
					"			o.person_id, o.obs_datetime, o.location_id" +
					"		from " +
					"			obs o join amrsreport_enrollment ae on o.person_id = ae.person_id and ae.enrollment_age < 2" +
					"		where" +
					"			o.voided = 0" +
					"			and (" +
					"				(o.concept_id in (1040, 1030, 1042) and o.value_coded = 703)" +
					"				or" +
					"				(o.concept_id = 6042 and o.value_coded = 1169)" +
					"			)" +
					"		group by o.person_id" +
					"		order by obs_datetime asc" +
					"	) ack" +
					"	on ae.person_id = ack.person_id" +
					" set" +
					"	ae.enrollment_date = ack.obs_datetime," +
					"	ae.location_id = ack.location_id," +
					"	ae.enrollment_reason = \"OBSERVATION\"";

	@Override
	public void enroll() {

		AdministrationService administrationService = Context.getAdministrationService();

		// clear the table
		administrationService.executeSQL(QUERY_TRUNCATE_TABLE, false);

		// insert from enrollment query
		administrationService.executeSQL(QUERY_INSERT_FROM_ENCOUNTERS, false);

		// remove fake patients
		administrationService.executeSQL(QUERY_DELETE_FAKE_PATIENTS, false);

		// update peds with latest positive obs
		administrationService.executeSQL(QUERY_UPDATE_LAST_POSITIVE, false);

		// update peds with latest negative obs
		administrationService.executeSQL(QUERY_UPDATE_LAST_NEGATIVE, false);

		// delete peds with negative > positive obs
		administrationService.executeSQL(QUERY_DELETE_CONFLICTING_PEDS, false);

		// update remaining peds with first positive obs and location
		administrationService.executeSQL(QUERY_UPDATE_FIRST_POSITIVE, false);
	}
}
