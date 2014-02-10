package org.openmrs.module.amrsreports.reporting.provider;

import org.apache.commons.io.IOUtils;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.AmrsReportsConceptNames;
import org.openmrs.module.amrsreports.cache.MohCacheUtils;
import org.openmrs.module.amrsreports.reporting.cohort.definition.Moh361BCohortDefinition;
import org.openmrs.module.amrsreports.reporting.converter.ARTMonthZeroConverter;
import org.openmrs.module.amrsreports.reporting.converter.DateListCustomConverter;
import org.openmrs.module.amrsreports.reporting.converter.DecimalAgeConverter;
import org.openmrs.module.amrsreports.reporting.converter.FormattedDateSetConverter;
import org.openmrs.module.amrsreports.reporting.converter.IntervalObsValueNumericConverter;
import org.openmrs.module.amrsreports.reporting.converter.ObsRepresentationValueNumericConverter;
import org.openmrs.module.amrsreports.reporting.converter.PersonAddressConverter;
import org.openmrs.module.amrsreports.reporting.converter.TbTreatmentStartDateConverter;
import org.openmrs.module.amrsreports.reporting.converter.WHOStageConverter;
import org.openmrs.module.amrsreports.reporting.data.AgeAtEvaluationDateDataDefinition;
import org.openmrs.module.amrsreports.reporting.data.CtxStartDataDefinition;
import org.openmrs.module.amrsreports.reporting.data.DateARTStartedDataDefinition;
import org.openmrs.module.amrsreports.reporting.data.INHStartDateDataDefinition;
import org.openmrs.module.amrsreports.reporting.data.ObsNearestARVStartDateDataDefinition;
import org.openmrs.module.amrsreports.reporting.data.PmtctPregnancyDataDefinition;
import org.openmrs.module.amrsreports.reporting.data.SortedObsSinceOtherDefinitionDataDefinition;
import org.openmrs.module.amrsreports.reporting.data.TbTreatmentStartDateDataDefinition;
import org.openmrs.module.amrsreports.service.MohCoreService;
import org.openmrs.module.amrsreports.util.MOHReportUtil;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.data.MappedData;
import org.openmrs.module.reporting.data.converter.BirthdateConverter;
import org.openmrs.module.reporting.data.converter.DateConverter;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.data.person.definition.BirthdateDataDefinition;
import org.openmrs.module.reporting.data.person.definition.GenderDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonAttributeDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonIdDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredAddressDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredNameDataDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportDesignResource;
import org.openmrs.module.reporting.report.definition.PeriodIndicatorReportDefinition;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.renderer.ExcelTemplateRenderer;
import org.openmrs.util.OpenmrsClassLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Provides mechanisms for rendering the MOH 361A Pre-ART Register
 */
public class MOH361BReportProvider_0_1 extends ReportProvider {

	public static final String CONTACT_PHONE_ATTRIBUTE_TYPE = "Contact Phone Number";
	private static final String MONTH_AND_YEAR_FORMAT = "MM/yyyy";

	public MOH361BReportProvider_0_1() {
		this.name = "MOH 361B 0.1-SNAPSHOT";
		this.visible = true;
	}

	@Override
	public ReportDefinition getReportDefinition() {

		String nullString = null;
		MohCoreService service = Context.getService(MohCoreService.class);

		ReportDefinition report = new PeriodIndicatorReportDefinition();
		report.setName("MOH 361B Report");

		// set up the DSD
		PatientDataSetDefinition dsd = new PatientDataSetDefinition();
		dsd.setName("allPatients");

		// set up the columns ...

		// patient id ... until we get this thing working proper
		dsd.addColumn("Person ID", new PersonIdDataDefinition(), nullString);

		//save definition in a variable
		DateARTStartedDataDefinition dateARTStartedDataDefinition = new DateARTStartedDataDefinition();

		// b. Date ART started (Transfer to ART register)
		dsd.addColumn("Date ART Started", dateARTStartedDataDefinition, nullString);

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

		// e. Sex
		dsd.addColumn("Sex", new GenderDataDefinition(), nullString);

		// f1. Date of Birth
		dsd.addColumn("Date of Birth", new BirthdateDataDefinition(), nullString, new BirthdateConverter(MOHReportUtil.DATE_FORMAT));

		// f1. Age
		AgeAtEvaluationDateDataDefinition add = new AgeAtEvaluationDateDataDefinition();
		dsd.addColumn("Age", add, nullString, new DecimalAgeConverter(2));

		// g1. Address
		PreferredAddressDataDefinition padd = new PreferredAddressDataDefinition();
		dsd.addColumn("Address", padd, nullString, new PersonAddressConverter());

		// g2. Phone Number
		PersonAttributeType pat = Context.getPersonService().getPersonAttributeTypeByName(CONTACT_PHONE_ATTRIBUTE_TYPE);
		PersonAttributeDataDefinition patientPhoneContact = new PersonAttributeDataDefinition(pat);
		dsd.addColumn("Phone Number", patientPhoneContact, nullString);

		// h. Reason for Eligibility

		// i. WHO Stage at start of ARVs
		ObsNearestARVStartDateDataDefinition whoDef = new ObsNearestARVStartDateDataDefinition(
				"WHO closest to ARV start",
				Context.getConceptService().getConcept(5356),
				Context.getConceptService().getConcept(1224)
		);
		dsd.addColumn("WHO Stage at ART Start", whoDef, nullString, new WHOStageConverter());

		// j. CD4 at start of ARVs
		ObsNearestARVStartDateDataDefinition cd4Def = new ObsNearestARVStartDateDataDefinition(
				"CD4 closest to ARV start",
				Context.getConceptService().getConcept(5497),
				Context.getConceptService().getConcept(730)
		);
		dsd.addColumn("CD4 at ART Start", cd4Def, nullString, new ObsRepresentationValueNumericConverter(1));

		// k. Height at start of ARVs
		ObsNearestARVStartDateDataDefinition heightDef = new ObsNearestARVStartDateDataDefinition(
				"height closest to ARV start",
				Context.getConceptService().getConcept(5090)
		);
		heightDef.setAgeLimit(12);
		dsd.addColumn("Height at ART Start", heightDef, nullString, new ObsRepresentationValueNumericConverter(1));

		// l. Weight at start of ARVs
		ObsNearestARVStartDateDataDefinition weightDef = new ObsNearestARVStartDateDataDefinition(
				"weight closest to ARV start",
				Context.getConceptService().getConcept(5089)
		);
		weightDef.setAgeLimit(12);
		dsd.addColumn("Weight at ART Start", weightDef, nullString, new ObsRepresentationValueNumericConverter(1));

		// m. CTX start date
		dsd.addColumn("CTX Start Dates", new CtxStartDataDefinition(), nullString, new DateListCustomConverter(MONTH_AND_YEAR_FORMAT));

		// n. INH Start
		dsd.addColumn("INH Start Dates", new INHStartDateDataDefinition(), nullString, new DateListCustomConverter(MONTH_AND_YEAR_FORMAT));

		// o. TB Treatment Start Month / Year
		dsd.addColumn("TB Treatment Start Dates", new TbTreatmentStartDateDataDefinition(), nullString, new TbTreatmentStartDateConverter());

		// p, q, r. Pregnancies
		dsd.addColumn("Pregnancies", new PmtctPregnancyDataDefinition(), nullString, new FormattedDateSetConverter());

		// create mapped definition of dateARTStarted
		MappedData<DateARTStartedDataDefinition> artDateMap = new MappedData<DateARTStartedDataDefinition>();
		artDateMap.setParameterizable(dateARTStartedDataDefinition);
		artDateMap.addConverter(new DateConverter());

		//aa. month zero
		dsd.addColumn("Month 0", dateARTStartedDataDefinition, nullString, new ARTMonthZeroConverter());

		// ah. 6 month CD4 count (and aq, az, bi)
		SortedObsSinceOtherDefinitionDataDefinition sixMonthCD4 = new SortedObsSinceOtherDefinitionDataDefinition("6 Month CD4");
		sixMonthCD4.addQuestion(MohCacheUtils.getConcept(AmrsReportsConceptNames.CD4_COUNT));
		sixMonthCD4.addQuestion(MohCacheUtils.getConcept(AmrsReportsConceptNames.CD4_PERCENT));
		sixMonthCD4.setEffectiveDateDefinition(artDateMap);

		// ai. 6 Month Weight (and ar, ba, bj)
		SortedObsSinceOtherDefinitionDataDefinition sixMonthWeight = new SortedObsSinceOtherDefinitionDataDefinition("6 Month Weight");
		sixMonthWeight.addQuestion(MohCacheUtils.getConcept(AmrsReportsConceptNames.WEIGHT));
		sixMonthWeight.setEffectiveDateDefinition(artDateMap);

		dsd.addColumn("6 Month CD4", sixMonthCD4, nullString, new IntervalObsValueNumericConverter(1, 6));
		dsd.addColumn("6 Month Weight", sixMonthWeight, nullString, new IntervalObsValueNumericConverter(1, 6));

		dsd.addColumn("12 Month CD4", sixMonthCD4, nullString, new IntervalObsValueNumericConverter(1, 12));
		dsd.addColumn("12 Month Weight", sixMonthWeight, nullString, new IntervalObsValueNumericConverter(1, 12));

		dsd.addColumn("18 Month CD4", sixMonthCD4, nullString, new IntervalObsValueNumericConverter(1, 18));
		dsd.addColumn("18 Month Weight", sixMonthWeight, nullString, new IntervalObsValueNumericConverter(1, 18));

		dsd.addColumn("24 Month CD4", sixMonthCD4, nullString, new IntervalObsValueNumericConverter(1, 24));
		dsd.addColumn("24 Month Weight", sixMonthWeight, nullString, new IntervalObsValueNumericConverter(1, 24));

		report.addDataSetDefinition(dsd, null);

		return report;
	}

	@Override
	public CohortDefinition getCohortDefinition() {
		return new Moh361BCohortDefinition();
	}

	@Override
	public ReportDesign getReportDesign() {
		ReportDesign design = new ReportDesign();
		design.setName("MOH 361B Register Design");
		design.setReportDefinition(this.getReportDefinition());
		design.setRendererType(ExcelTemplateRenderer.class);

		Properties props = new Properties();
		props.put("repeatingSections", "sheet:1,row:7,dataset:allPatients");

		design.setProperties(props);

		ReportDesignResource resource = new ReportDesignResource();
		resource.setName("template.xls");
		InputStream is = OpenmrsClassLoader.getInstance().getResourceAsStream("templates/MOH361BReportTemplate_0_1.xls");

		if (is == null)
			throw new APIException("Could not find report template.");

		try {
			resource.setContents(IOUtils.toByteArray(is));
		} catch (IOException ex) {
			throw new APIException("Could not create report design for MOH 361B Register.", ex);
		}

		IOUtils.closeQuietly(is);
		design.addResource(resource);

		return design;
	}
}