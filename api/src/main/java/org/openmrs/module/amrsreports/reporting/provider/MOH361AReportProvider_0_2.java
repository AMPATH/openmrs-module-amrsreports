package org.openmrs.module.amrsreports.reporting.provider;

import org.apache.commons.io.IOUtils;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.MOHFacility;
import org.openmrs.module.amrsreports.reporting.cohort.definition.Moh361ACohortDefinition;
import org.openmrs.module.amrsreports.reporting.common.EncounterRepresentation;
import org.openmrs.module.amrsreports.reporting.common.ObsRepresentation;
import org.openmrs.module.amrsreports.reporting.converter.ARVPatientSnapshotDateConverter;
import org.openmrs.module.amrsreports.reporting.converter.ARVPatientSnapshotReasonConverter;
import org.openmrs.module.amrsreports.reporting.converter.DecimalAgeConverter;
import org.openmrs.module.amrsreports.reporting.converter.EncounterDatetimeConverter;
import org.openmrs.module.amrsreports.reporting.converter.EncounterLocationConverter;
import org.openmrs.module.amrsreports.reporting.converter.EntryPointConverter;
import org.openmrs.module.amrsreports.reporting.converter.FormattedDateSetConverter;
import org.openmrs.module.amrsreports.reporting.converter.ObsValueDatetimeConverter;
import org.openmrs.module.amrsreports.reporting.converter.WHOStageAndDateConverter;
import org.openmrs.module.amrsreports.reporting.data.CtxStartStopDataDefinition;
import org.openmrs.module.amrsreports.reporting.data.DateARTStartedDataDefinition;
import org.openmrs.module.amrsreports.reporting.data.EligibilityForARTDataDefinition;
import org.openmrs.module.amrsreports.reporting.data.EnrollmentDateDataDefinition;
import org.openmrs.module.amrsreports.reporting.data.FirstEncounterAtFacilityDataDefinition;
import org.openmrs.module.amrsreports.reporting.data.FirstWHOStageDataDefinition;
import org.openmrs.module.amrsreports.reporting.data.FluconazoleStartStopDataDefinition;
import org.openmrs.module.amrsreports.reporting.data.LTFUTODeadDataDefinition;
import org.openmrs.module.amrsreports.reporting.data.LastHIVEncounterDataDefinition;
import org.openmrs.module.amrsreports.reporting.data.LastRTCDateDataDefinition;
import org.openmrs.module.amrsreports.reporting.data.PmtctPregnancyDataDefinition;
import org.openmrs.module.amrsreports.reporting.data.SerialNumberDataDefinition;
import org.openmrs.module.amrsreports.reporting.data.TbStartStopDataDefinition;
import org.openmrs.module.amrsreports.reporting.data.TransferStatusDataDefinition;
import org.openmrs.module.amrsreports.rule.MohEvaluableNameConstants;
import org.openmrs.module.amrsreports.service.MohCoreService;
import org.openmrs.module.amrsreports.util.MOHReportUtil;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.SortCriteria;
import org.openmrs.module.reporting.data.MappedData;
import org.openmrs.module.reporting.data.converter.BirthdateConverter;
import org.openmrs.module.reporting.data.converter.DateConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.converter.PropertyConverter;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.data.person.definition.AgeAtDateOfOtherDataDefinition;
import org.openmrs.module.reporting.data.person.definition.BirthdateDataDefinition;
import org.openmrs.module.reporting.data.person.definition.GenderDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonAttributeDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonIdDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredNameDataDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportDesignResource;
import org.openmrs.module.reporting.report.definition.PeriodIndicatorReportDefinition;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.renderer.ExcelTemplateRenderer;
import org.openmrs.util.OpenmrsClassLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Provides mechanisms for rendering the MOH 361A Pre-ART Register
 */
public class MOH361AReportProvider_0_2 extends ReportProvider {

	public MOH361AReportProvider_0_2() {
		this.name = "MOH 361A 0.2-SNAPSHOT";
		this.visible = true;
	}

	@Override
	public ReportDefinition getReportDefinition() {

		String nullString = null;
		ObjectFormatter nullStringConverter = new ObjectFormatter();
		MohCoreService service = Context.getService(MohCoreService.class);

		ReportDefinition report = new PeriodIndicatorReportDefinition();
		report.setName("MOH 361A Report");

		// set up the DSD
		PatientDataSetDefinition dsd = new PatientDataSetDefinition();
		dsd.setName("allPatients");

		// set up parameters
		Parameter facility = new Parameter();
		facility.setName("facility");
		facility.setType(MOHFacility.class);

		// add to report and data set definition
		report.addParameter(facility);
		dsd.addParameter(facility);

		// sort by serial number, then by date
		dsd.addSortCriteria("Transfer Status", SortCriteria.SortDirection.ASC);
		dsd.addSortCriteria("Date Chronic HIV Care Started", SortCriteria.SortDirection.ASC);
		dsd.addSortCriteria("Serial Number", SortCriteria.SortDirection.ASC);

		// set up the columns ...

		// patient id ... until we get this thing working proper
		dsd.addColumn("Person ID", new PersonIdDataDefinition(), nullString);

		// a. serial number
		dsd.addColumn("Serial Number", new SerialNumberDataDefinition(), "facility=${facility}");

		// b. date chronic HIV+ care started
		EnrollmentDateDataDefinition enrollmentDate = new EnrollmentDateDataDefinition();
		dsd.addColumn("Date Chronic HIV Care Started", enrollmentDate, nullString);

		// extra column to help understand reason for including in this cohort
		dsd.addColumn("First Encounter Date At Facility", new FirstEncounterAtFacilityDataDefinition(),
				"facility=${facility}", new EncounterDatetimeConverter());

		// c. Unique Patient Number
		PatientIdentifierType pit = service.getCCCNumberIdentifierType();
		PatientIdentifierDataDefinition cccColumn = new PatientIdentifierDataDefinition("CCC", pit);
		cccColumn.setIncludeFirstNonNullOnly(true);
		dsd.addColumn("Unique Patient Number", cccColumn, nullString);

		// AMRS Universal ID
		PatientIdentifierDataDefinition uidColumn = new PatientIdentifierDataDefinition(
				"AMRS Universal ID", Context.getPatientService().getPatientIdentifierType(8));
		uidColumn.setIncludeFirstNonNullOnly(true);
		dsd.addColumn("AMRS Universal ID", uidColumn, nullString);

		// AMRS Medical Record Number
		PatientIdentifierDataDefinition mrnColumn = new PatientIdentifierDataDefinition(
				"AMRS Medical Record Number", Context.getPatientService().getPatientIdentifierType(3));
		mrnColumn.setIncludeFirstNonNullOnly(true);
		dsd.addColumn("AMRS Medical Record Number", mrnColumn, nullString);

		// d. Patient's Name
		dsd.addColumn("Name", new PreferredNameDataDefinition(), nullString);

		// e1. Date of Birth
		dsd.addColumn("Date of Birth", new BirthdateDataDefinition(), nullString,
				new BirthdateConverter(MOHReportUtil.DATE_FORMAT));

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
		dsd.addColumn("Confirmed HIV Date", enrollmentDate, nullString);

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
		dsd.addColumn("CTX Start Stop Date", new CtxStartStopDataDefinition(), nullString);

		// l. Fluconazole startdate and stopdate
		dsd.addColumn("Fluconazole Start Stop Date", new FluconazoleStartStopDataDefinition(), nullString);

		// m. TB treatment startdate and stopdate
		dsd.addColumn("TB Treatment Start Stop Date", new TbStartStopDataDefinition(), nullString);

		// n. Pregnancy Yes?, Due date, PMTCT refer
		dsd.addColumn("Pregnancy EDD and Referral", new PmtctPregnancyDataDefinition(), nullString, new FormattedDateSetConverter());

		// o. LTFU / TO / Dead and date when the event occurred
		dsd.addColumn("LTFU TO DEAD", new LTFUTODeadDataDefinition(), nullString, nullStringConverter);

		// p. WHO clinical Stage and date
		dsd.addColumn("WHO Clinical Stage", new FirstWHOStageDataDefinition(), nullString, new WHOStageAndDateConverter());

		// q. Date medically eligible for ART
		EligibilityForARTDataDefinition eligibility = new EligibilityForARTDataDefinition();
		dsd.addColumn("Date Medically Eligible for ART", eligibility, nullString, new ARVPatientSnapshotDateConverter());

		// r. Reason Medically Eligible for ART
		dsd.addColumn("Reason Medically Eligible for ART", eligibility, nullString, new ARVPatientSnapshotReasonConverter());

		// s. Date ART started (Transfer to ART register)
		dsd.addColumn("Date ART Started", new DateARTStartedDataDefinition(), nullString);

		// additional columns for troubleshooting
		LastHIVEncounterDataDefinition lastHIVEncounter = new LastHIVEncounterDataDefinition();

		dsd.addColumn("Last HIV Encounter Date", lastHIVEncounter, nullString,
				new PropertyConverter(EncounterRepresentation.class, "encounterDatetime"));

		dsd.addColumn("Last HIV Encounter Location", lastHIVEncounter, nullString,
				new PropertyConverter(EncounterRepresentation.class, "locationName"));

		// informative column for the destination clinics
		dsd.addColumn("Last Return to Clinic Date", new LastRTCDateDataDefinition(), nullString,
				new PropertyConverter(ObsRepresentation.class, "valueDatetime"));

		// transfer status column (used for sorting, not needed in output)
		dsd.addColumn("Transfer Status", new TransferStatusDataDefinition(), "facility=${facility}");

		Map<String, Object> mappings = new HashMap<String, Object>();
		mappings.put("facility", "${facility}");
		report.addDataSetDefinition(dsd, mappings);

		return report;
	}

	@Override
	public CohortDefinition getCohortDefinition() {
		return new Moh361ACohortDefinition();
	}

	@Override
	public ReportDesign getReportDesign() {
		ReportDesign design = new ReportDesign();
		design.setName("MOH 361A Register Design");
		design.setReportDefinition(this.getReportDefinition());
		design.setRendererType(ExcelTemplateRenderer.class);

		Properties props = new Properties();
		props.put("repeatingSections", "sheet:2,row:4,dataset:allPatients");

		design.setProperties(props);

		ReportDesignResource resource = new ReportDesignResource();
		resource.setName("template.xls");
		InputStream is = OpenmrsClassLoader.getInstance().getResourceAsStream("templates/MOH361AReportTemplate_0_2.xls");

		if (is == null)
			throw new APIException("Could not find report template.");

		try {
			resource.setContents(IOUtils.toByteArray(is));
		} catch (IOException ex) {
			throw new APIException("Could not create report design for MOH 361A Register.", ex);
		}

		IOUtils.closeQuietly(is);
		design.addResource(resource);

		return design;
	}
}
