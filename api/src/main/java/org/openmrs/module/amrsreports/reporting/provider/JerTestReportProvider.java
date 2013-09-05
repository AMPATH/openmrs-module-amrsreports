package org.openmrs.module.amrsreports.reporting.provider;

import org.apache.commons.io.IOUtils;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.MOHFacility;
import org.openmrs.module.amrsreports.reporting.cohort.definition.Moh361ACohortDefinition;
import org.openmrs.module.amrsreports.reporting.common.EncounterRepresentation;
import org.openmrs.module.amrsreports.reporting.converter.EncounterDatetimeConverter;
import org.openmrs.module.amrsreports.reporting.data.FirstEncounterAtFacilityDataDefinition;
import org.openmrs.module.amrsreports.reporting.data.LastHIVEncounterDataDefinition;
import org.openmrs.module.amrsreports.service.MohCoreService;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.SortCriteria;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.converter.PropertyConverter;
import org.openmrs.module.reporting.data.person.definition.PersonIdDataDefinition;
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
public class JerTestReportProvider extends ReportProvider {

	public JerTestReportProvider() {
		this.name = "Jer's Test";
		this.visible = true;
	}

	@Override
	public ReportDefinition getReportDefinition() {

		String nullString = null;
		ObjectFormatter nullStringConverter = new ObjectFormatter();
		MohCoreService service = Context.getService(MohCoreService.class);

		ReportDefinition report = new PeriodIndicatorReportDefinition();
		report.setName("Jer's Test Report");

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
		dsd.addSortCriteria("Serial Number", SortCriteria.SortDirection.ASC);
		dsd.addSortCriteria("First Encounter Date At Facility", SortCriteria.SortDirection.ASC);

		// set up the columns ...

		// patient id ... until we get this thing working proper
		dsd.addColumn("Person ID", new PersonIdDataDefinition(), nullString);

//		// a. serial number
//		dsd.addColumn("Serial Number", new SerialNumberDataDefinition(), "facility=${facility}");
//
//		// b. date chronic HIV+ care started
//		EnrollmentDateDataDefinition enrollmentDate = new EnrollmentDateDataDefinition();
//		dsd.addColumn("Date Chronic HIV Care Started", enrollmentDate, nullString);
//
//		// extra column to help understand reason for including in this cohort
//		dsd.addColumn("First Encounter Date At Facility", new FirstEncounterAtFacilityDataDefinition(),
//				"facility=${facility}", new EncounterDatetimeConverter());
//
//		// c. Unique Patient Number
//		PatientIdentifierType pit = service.getCCCNumberIdentifierType();
//		CohortRestrictedPatientIdentifierDataDefinition cccColumn = new CohortRestrictedPatientIdentifierDataDefinition("CCC", pit);
//		cccColumn.setIncludeFirstNonNullOnly(true);
//		dsd.addColumn("Unique Patient Number", cccColumn, nullString);
//
//		List<PatientIdentifierType> idTypes = Context.getPatientService().getAllPatientIdentifierTypes();
//		idTypes.remove(pit);
//		PatientIdentifierDataDefinition idColumn = new PatientIdentifierDataDefinition("Identifier");
//		idColumn.setTypes(idTypes);
//		idColumn.setIncludeFirstNonNullOnly(true);
//		dsd.addColumn("AMPATH Identifier", idColumn, nullString);
//
//		// d. Patient's Name
//		dsd.addColumn("Name", new PreferredNameDataDefinition(), nullString);
//
//		// e1. Date of Birth
//		dsd.addColumn("Date of Birth", new BirthdateDataDefinition(), nullString,
//				new BirthdateConverter(MOHReportUtil.DATE_FORMAT));
//
//		// e2. Age at Enrollment
//
//		MappedData<EnrollmentDateDataDefinition> mappedDef = new MappedData<EnrollmentDateDataDefinition>();
//		mappedDef.setParameterizable(enrollmentDate);
//		mappedDef.addConverter(new DateConverter());
//		AgeAtDateOfOtherDataDefinition ageAtEnrollment = new AgeAtDateOfOtherDataDefinition();
//		ageAtEnrollment.setEffectiveDateDefinition(mappedDef);
//		dsd.addColumn("Age at Enrollment", ageAtEnrollment, nullString, new DecimalAgeConverter(2));
//
//		// f. Sex
//		dsd.addColumn("Sex", new GenderDataDefinition(), nullString);
//
//		// g. Entry point: From where?
//		PersonAttributeType pat = Context.getPersonService().getPersonAttributeTypeByName(MohEvaluableNameConstants.POINT_OF_HIV_TESTING);
//		dsd.addColumn("Entry Point", new PersonAttributeDataDefinition("entryPoint", pat), nullString, new EntryPointConverter());
//
//		// h. Confirmed HIV+ Date
//		dsd.addColumn("Confirmed HIV Date", enrollmentDate, nullString);
//
//		// k. CTX startdate and stopdate:
//		dsd.addColumn("CTX Start Stop Date", new CtxStartStopDataDefinition(), nullString);
//
//		// l. Fluconazole startdate and stopdate
//		dsd.addColumn("Fluconazole Start Stop Date", new FluconazoleStartStopDataDefinition(), nullString);
//
//		// m. TB treatment startdate and stopdate
//		dsd.addColumn("TB Treatment Start Stop Date", new TbStartStopDataDefinition(), nullString);
//
//		// n. Pregnancy Yes?, Due date, PMTCT refer
//		dsd.addColumn("Pregnancy EDD and Referral", new PmtctPregnancyDataDefinition(), nullString, new PMTCTDatesConverter());
//
//		// o. LTFU / TO / Dead and date when the event occurred
//		dsd.addColumn("LTFU TO DEAD", new LTFUTODeadDataDefinition(), nullString, nullStringConverter);
//
//		// p. WHO clinical Stage and date
//		dsd.addColumn("WHO Clinical Stage", new FirstWHOStageDataDefinition(), nullString, new WHOStageAndDateConverter());
//
//		// q. Date medically eligible for ART
//		EligibilityForARTDataDefinition eligibility = new EligibilityForARTDataDefinition();
//		dsd.addColumn("Date Medically Eligible for ART", eligibility, nullString, new ARVPatientSnapshotDateConverter());
//
//		// r. Reason Medically Eligible for ART
//		dsd.addColumn("Reason Medically Eligible for ART", eligibility, nullString, new ARVPatientSnapshotReasonConverter());
//
//		// s. Date ART started (Transfer to ART register)
//		dsd.addColumn("Date ART Started", new DateARTStartedDataDefinition(), nullString);
//
		// additional columns for troubleshooting
		LastHIVEncounterDataDefinition lastHIVEncounter = new LastHIVEncounterDataDefinition();
		dsd.addColumn("Last HIV Encounter Date", lastHIVEncounter, nullString,
				new PropertyConverter(EncounterRepresentation.class, "encounterDatetime"));
		dsd.addColumn("Last HIV Encounter Location", lastHIVEncounter, nullString,
				new PropertyConverter(EncounterRepresentation.class, "location"));
//
//		// informative column for the destination clinics
//		dsd.addColumn("Last Return to Clinic Date", new LastRTCDateDataDefinition(), nullString, new ObsValueDatetimeConverter());

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
		props.put("repeatingSections", "sheet:1,row:4,dataset:allPatients");

		design.setProperties(props);

		ReportDesignResource resource = new ReportDesignResource();
		resource.setName("template.xls");
		InputStream is = OpenmrsClassLoader.getInstance().getResourceAsStream("templates/MOH361AReportTemplate_0_1.xls");

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
