package org.openmrs.module.amrsreport.reporting;

import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreport.cache.MohCacheUtils;
import org.openmrs.module.amrsreport.reporting.converter.DecimalAgeConverter;
import org.openmrs.module.amrsreport.reporting.converter.MOHPersonNameConverter;
import org.openmrs.module.amrsreport.reporting.converter.MOHSerialNumberConverter;
import org.openmrs.module.amrsreport.reporting.data.EnrollmentDateDataDefinition;
import org.openmrs.module.amrsreport.rule.MohEvaluableNameConstants;
import org.openmrs.module.amrsreport.rule.util.MohRuleUtils;
import org.openmrs.module.reporting.data.MappedData;
import org.openmrs.module.reporting.data.converter.BirthdateConverter;
import org.openmrs.module.reporting.data.converter.DateConverter;
import org.openmrs.module.reporting.data.patient.definition.LogicDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.data.person.definition.AgeAtDateOfOtherDataDefinition;
import org.openmrs.module.reporting.data.person.definition.BirthdateDataDefinition;
import org.openmrs.module.reporting.data.person.definition.GenderDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonAttributeDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonIdDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredNameDataDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.report.definition.PeriodIndicatorReportDefinition;
import org.openmrs.module.reporting.report.definition.ReportDefinition;

/**
 * Renders the MOH 361A Pre-ART Register
 */
public class MOH361AReport {

	public static ReportDefinition getReportDefinition() {

		String nullString = null;

		DateConverter commonDateConverter = new DateConverter(MohRuleUtils.DATE_FORMAT);

		ReportDefinition report = new PeriodIndicatorReportDefinition();
		report.setName("MOH 361A Report");

		// set up the columns
		PatientDataSetDefinition dsd = new PatientDataSetDefinition();
		dsd.setName("MOH 361A Data Set Definition");

		// patient id ... until we get this thing working proper
		dsd.addColumn("Person ID", new PersonIdDataDefinition(), nullString);

		// a. serial number
		PatientIdentifierType pit = MohCacheUtils.getPatientIdentifierType(
				Context.getAdministrationService().getGlobalProperty("cccgenerator.CCC"));
		PatientIdentifierDataDefinition cccColumn = new PatientIdentifierDataDefinition("CCC", pit);

		dsd.addColumn("Serial Number", cccColumn, nullString, new MOHSerialNumberConverter());

		// b. date chronic HIV+ care started
		EnrollmentDateDataDefinition enrollmentDate = new EnrollmentDateDataDefinition();
		dsd.addColumn("Date Chronic HIV Care Started", enrollmentDate, nullString, commonDateConverter);

		// c. Unique Patient Number
		dsd.addColumn("Unique Patient Number", cccColumn, nullString);

		// d. Patient's Name
		dsd.addColumn("Name", new PreferredNameDataDefinition(), nullString, new MOHPersonNameConverter());

		// e1. Date of Birth
		dsd.addColumn("Date of Birth", new BirthdateDataDefinition(), nullString, new BirthdateConverter(MohRuleUtils.DATE_FORMAT));

		// e2. Age at Enrollment

		MappedData<EnrollmentDateDataDefinition> mappedDef = new MappedData<EnrollmentDateDataDefinition>();
		mappedDef.setParameterizable(enrollmentDate);
		mappedDef.addConverter(new DateConverter());
		AgeAtDateOfOtherDataDefinition ageAtEnrollment = new AgeAtDateOfOtherDataDefinition();
		ageAtEnrollment.setEffectiveDateDefinition(mappedDef);

		dsd.addColumn("Age at Enrollment", ageAtEnrollment, nullString, new DecimalAgeConverter(2));

		// f. Sex
		dsd.addColumn("Sex", new GenderDataDefinition(), nullString);

		// g. Entry point: From where?
		// TODO add a StringConverter here
		PersonAttributeType pat = Context.getPersonService().getPersonAttributeTypeByName(MohEvaluableNameConstants.POINT_OF_HIV_TESTING);
		dsd.addColumn("Entry Point", new PersonAttributeDataDefinition("entryPoint", pat), nullString);

		// h. Confirmed HIV+ Date
		dsd.addColumn("Confirmed HIV+ Date", enrollmentDate, nullString, commonDateConverter);

		// i. PEP Start / Stop Date
		LogicDataDefinition columnI = new LogicDataDefinition();
		columnI.setLogicQuery("\"MOH PEP Start Stop Date\"");
		dsd.addColumn("PEP Start / Stop Date", columnI, nullString);

		// j. Reasons for PEP use:
		LogicDataDefinition columnJ = new LogicDataDefinition();
		columnJ.setLogicQuery("\"MOH Reasons For PEP\"");
		dsd.addColumn("Reasons for PEP Use", columnJ, nullString);

		// k. CTX startdate and stopdate:
		LogicDataDefinition columnK = new LogicDataDefinition();
		columnK.setLogicQuery("\"MOH CTX Start-Stop Date\"");
		dsd.addColumn("CTX Start / Stop Date", columnK, nullString);

		// l. Fluconazole startdate and stopdate
		LogicDataDefinition columnL = new LogicDataDefinition();
		columnL.setLogicQuery("\"MOH Fluconazole Start-Stop Date\"");
		dsd.addColumn("Fluconazole Start / Stop Date", columnL, nullString);

		// m. TB treatment startdate and stopdate
		LogicDataDefinition columnM = new LogicDataDefinition();
		columnM.setLogicQuery("\"MOH TB Start-Stop Date\"");
		dsd.addColumn("TB Treatment Start / Stop Date", columnM, nullString);

		// n. Pregnancy Yes?, Due date, PMTCT refer
		LogicDataDefinition columnN = new LogicDataDefinition();
		columnN.setLogicQuery("\"MOH Pregnancy PMTC Referral\"");
		dsd.addColumn("Pregnancy EDD and Referral", columnN, nullString);

		// o. LTFU / TO / Dead and date when the event occurred
		LogicDataDefinition columnO = new LogicDataDefinition();
		columnO.setLogicQuery("\"MOH LTFU-TO-DEAD\"");
		dsd.addColumn("LTFU / TO / DEAD", columnO, nullString);

		// p. WHO clinical Stage and date
		LogicDataDefinition columnP = new LogicDataDefinition();
		columnP.setLogicQuery("\"MOH WHO Stage\"");
		dsd.addColumn("WHO Clinical Stage", columnP, nullString);

		// q. Date medically eligible for ART
		LogicDataDefinition columnQ = new LogicDataDefinition();
		columnQ.setLogicQuery("\"MOH Date and Reason Medically Eligible For ART\"");
		dsd.addColumn("Date and Reason Medically Eligible for ART", columnQ, nullString);

		// r. Reason Medically Eligible for ART
// TODO make this into a separate column by using a converter
//		LogicDataDefinition columnR = new LogicDataDefinition();
//		columnR.setLogicQuery("\"MOH Date and Reason Medically Eligible For ART\"");
//		dsd.addColumn("Reason Medically Eligible for ART", columnQ, nullString);

		// s. Date ART started (Transfer to ART register)
		LogicDataDefinition columnS = new LogicDataDefinition();
		columnS.setLogicQuery("\"MOH Date ART Started\"");
		dsd.addColumn("Date ART Started", columnS, nullString);

		report.addDataSetDefinition(dsd, null);

		return report;
	}

}
