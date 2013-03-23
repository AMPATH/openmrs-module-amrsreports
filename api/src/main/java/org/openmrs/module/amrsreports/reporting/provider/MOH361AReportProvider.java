package org.openmrs.module.amrsreports.reporting.provider;

import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.reporting.cohort.definition.Moh361ACohortDefinition;
import org.openmrs.module.amrsreports.reporting.converter.DecimalAgeConverter;
import org.openmrs.module.amrsreports.reporting.converter.EntryPointConverter;
import org.openmrs.module.amrsreports.reporting.converter.WHOStageAndDateConverter;
import org.openmrs.module.amrsreports.reporting.data.EnrollmentDateDataDefinition;
import org.openmrs.module.amrsreports.reporting.data.FirstWHOStageDataDefinition;
import org.openmrs.module.amrsreports.reporting.data.LTFUTODeadDataDefinition;
import org.openmrs.module.amrsreports.reporting.data.SerialNumberDataDefinition;
import org.openmrs.module.amrsreports.rule.MohEvaluableNameConstants;
import org.openmrs.module.amrsreports.rule.util.MohRuleUtils;
import org.openmrs.module.amrsreports.service.MohCoreService;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.data.MappedData;
import org.openmrs.module.reporting.data.converter.BirthdateConverter;
import org.openmrs.module.reporting.data.converter.DateConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
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
 * Provides mechanisms for rendering the MOH 361A Pre-ART Register
 */
public class MOH361AReportProvider implements ReportProvider {

	@Override
	public String getName() {
		return "MOH 361A";
	}

	@Override
	public ReportDefinition getReportDefinition() {

		String nullString = null;
        ObjectFormatter nullStringConverter = new ObjectFormatter();
		DateConverter commonDateConverter = new DateConverter(MohRuleUtils.DATE_FORMAT);
		MohCoreService service = Context.getService(MohCoreService.class);

		ReportDefinition report = new PeriodIndicatorReportDefinition();
		report.setName("MOH 361A Report");

		// set up the columns
		PatientDataSetDefinition dsd = new PatientDataSetDefinition();
		dsd.setName("MOH 361A Data Set Definition");

		// patient id ... until we get this thing working proper
		dsd.addColumn("Person ID", new PersonIdDataDefinition(), nullString);

		// a. serial number
		dsd.addColumn("Serial Number", new SerialNumberDataDefinition(), nullString, nullStringConverter);

		// b. date chronic HIV+ care started
		EnrollmentDateDataDefinition enrollmentDate = new EnrollmentDateDataDefinition();
		dsd.addColumn("Date Chronic HIV Care Started", enrollmentDate, nullString, commonDateConverter);

		// c. Unique Patient Number
		PatientIdentifierType pit = service.getCCCNumberIdentifierType();
        PatientIdentifierDataDefinition cccColumn = new PatientIdentifierDataDefinition("CCC", pit);
		dsd.addColumn("Unique Patient Number", cccColumn, nullString);

		// d. Patient's Name
		dsd.addColumn("Name", new PreferredNameDataDefinition(), nullString);

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
		PersonAttributeType pat = Context.getPersonService().getPersonAttributeTypeByName(MohEvaluableNameConstants.POINT_OF_HIV_TESTING);
		dsd.addColumn("Entry Point", new PersonAttributeDataDefinition("entryPoint", pat), nullString, new EntryPointConverter());

		// h. Confirmed HIV+ Date
		dsd.addColumn("Confirmed HIV+ Date", enrollmentDate, nullString, commonDateConverter);

//		// i. PEP Start / Stop Date
//		LogicDataDefinition columnI = new LogicDataDefinition();
//		columnI.setLogicQuery("\"MOH PEP Start Stop Date\"");
//		dsd.addColumn("PEP Start / Stop Date", columnI, nullString);
//
//		// j. Reasons for PEP use:
//		LogicDataDefinition columnJ = new LogicDataDefinition();
//		columnJ.setLogicQuery("\"MOH Reasons For PEP\"");
//		dsd.addColumn("Reasons for PEP Use", columnJ, nullString);

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
		dsd.addColumn("LTFU / TO / DEAD", new LTFUTODeadDataDefinition(), nullString, nullStringConverter);

		// p. WHO clinical Stage and date
		dsd.addColumn("WHO Clinical Stage", new FirstWHOStageDataDefinition(), nullString, new WHOStageAndDateConverter());

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

	@Override
	public CohortDefinition getCohortDefinition() {
		return new Moh361ACohortDefinition();
	}

}
