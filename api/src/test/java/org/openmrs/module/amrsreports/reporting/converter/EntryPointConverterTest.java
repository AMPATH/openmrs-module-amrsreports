package org.openmrs.module.amrsreports.reporting.converter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.openmrs.Concept;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.rule.MohEvaluableNameConstants;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Unit tests for {@link EntryPointConverter}
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class EntryPointConverterTest {

	private ConceptService conceptService;
	private PersonAttributeType personAttributeType;
	private EntryPointConverter converter;

	@Before
	public void setup() {

		// set up the person attribute type
		personAttributeType = new PersonAttributeType();
		personAttributeType.setPersonAttributeTypeId(1);
		personAttributeType.setFormat("Concept");
		personAttributeType.setName(MohEvaluableNameConstants.POINT_OF_HIV_TESTING);

		// build the concept service
		int i = 0;
		conceptService = Mockito.mock(ConceptService.class);
		Mockito.when(conceptService.getConcept(MohEvaluableNameConstants.MOBILE_VOLUNTARY_COUNSELING_AND_TESTING)).thenReturn(new Concept(i++));
		Mockito.when(conceptService.getConcept(MohEvaluableNameConstants.MATERNAL_CHILD_HEALTH_PROGRAM)).thenReturn(new Concept(i++));
		Mockito.when(conceptService.getConcept(MohEvaluableNameConstants.PREVENTION_OF_MOTHER_TO_CHILD_TRANSMISSION_OF_HIV)).thenReturn(new Concept(i++));
		Mockito.when(conceptService.getConcept(MohEvaluableNameConstants.VOLUNTARY_COUNSELING_AND_TESTING_CENTER)).thenReturn(new Concept(i++));
		Mockito.when(conceptService.getConcept(MohEvaluableNameConstants.TUBERCULOSIS)).thenReturn(new Concept(i++));
		Mockito.when(conceptService.getConcept(MohEvaluableNameConstants.HOME_BASED_TESTING_PROGRAM)).thenReturn(new Concept(i++));
		Mockito.when(conceptService.getConcept(MohEvaluableNameConstants.INPATIENT_CARE_OR_HOSPITALIZATION)).thenReturn(new Concept(i++));
		Mockito.when(conceptService.getConcept(MohEvaluableNameConstants.PROVIDER_INITIATED_TESTING_AND_COUNSELING)).thenReturn(new Concept(i++));
		Mockito.when(conceptService.getConcept(MohEvaluableNameConstants.PEDIATRIC_OUTPATIENT_CLINIC)).thenReturn(new Concept(i++));
		Mockito.when(conceptService.getConcept(MohEvaluableNameConstants.OTHER_NON_CODED)).thenReturn(new Concept(i++));
		Mockito.when(conceptService.getConcept("fake")).thenReturn(new Concept(i++));

		// set up Context
		PowerMockito.mockStatic(Context.class);
		Mockito.when(Context.getConceptService()).thenReturn(conceptService);

		// set up converter
		converter = new EntryPointConverter();
	}

	private PersonAttribute createPersonAttribute(String value) {
		Concept c = conceptService.getConcept(value);
		PersonAttribute pa = new PersonAttribute();
		pa.setAttributeType(personAttributeType);
		pa.setValue(c.getId().toString());
		return pa;
	}

	/**
	 * @verifies return MVCT for Mobile Voluntary Counseling and Testing
	 * @see EntryPointConverter#convert(Object)
	 */
	@Test
	public void convert_shouldReturnMVCTForMobileVoluntaryCounselingAndTesting() throws Exception {
		PersonAttribute pa = createPersonAttribute(MohEvaluableNameConstants.MOBILE_VOLUNTARY_COUNSELING_AND_TESTING);
		assertThat((String) converter.convert(pa), is("MVCT"));
	}

	/**
	 * @verifies return MCH for Maternal Child Health Program
	 * @see EntryPointConverter#convert(Object)
	 */
	@Test
	public void convert_shouldReturnMCHForMaternalChildHealthProgram() throws Exception {
		PersonAttribute pa = createPersonAttribute(MohEvaluableNameConstants.MATERNAL_CHILD_HEALTH_PROGRAM);
		assertThat((String) converter.convert(pa), is("MCH"));
	}

	/**
	 * @verifies return PMTCT for Prevention of Mother to Child Transmission of HIV
	 * @see EntryPointConverter#convert(Object)
	 */
	@Test
	public void convert_shouldReturnPMTCTForPreventionOfMotherToChildTransmissionOfHIV() throws Exception {
		PersonAttribute pa = createPersonAttribute(MohEvaluableNameConstants.PREVENTION_OF_MOTHER_TO_CHILD_TRANSMISSION_OF_HIV);
		assertThat((String) converter.convert(pa), is("PMTCT"));
	}

	/**
	 * @verifies return VCT for Voluntary Counseling and Testing Center
	 * @see EntryPointConverter#convert(Object)
	 */
	@Test
	public void convert_shouldReturnVCTForVoluntaryCounselingAndTestingCenter() throws Exception {
		PersonAttribute pa = createPersonAttribute(MohEvaluableNameConstants.VOLUNTARY_COUNSELING_AND_TESTING_CENTER);
		assertThat((String) converter.convert(pa), is("VCT"));
	}

	/**
	 * @verifies return TB for Tuberculosis
	 * @see EntryPointConverter#convert(Object)
	 */
	@Test
	public void convert_shouldReturnTBForTuberculosis() throws Exception {
		PersonAttribute pa = createPersonAttribute(MohEvaluableNameConstants.TUBERCULOSIS);
		assertThat((String) converter.convert(pa), is("TB"));
	}

	/**
	 * @verifies return HCT for Home Based Testing Program
	 * @see EntryPointConverter#convert(Object)
	 */
	@Test
	public void convert_shouldReturnHCTForHomeBasedTestingProgram() throws Exception {
		PersonAttribute pa = createPersonAttribute(MohEvaluableNameConstants.HOME_BASED_TESTING_PROGRAM);
		assertThat((String) converter.convert(pa), is("HCT"));
	}

	/**
	 * @verifies return IPD for Inpatient Care or Hospitalization
	 * @see EntryPointConverter#convert(Object)
	 */
	@Test
	public void convert_shouldReturnIPDForInpatientCareOrHospitalization() throws Exception {
		PersonAttribute pa = createPersonAttribute(MohEvaluableNameConstants.INPATIENT_CARE_OR_HOSPITALIZATION);
		assertThat((String) converter.convert(pa), is("IPD"));
	}

	/**
	 * @verifies return PITC for Provider Initiated Testing and Counseling
	 * @see EntryPointConverter#convert(Object)
	 */
	@Test
	public void convert_shouldReturnPITCForProviderInitiatedTestingAndCounseling() throws Exception {
		PersonAttribute pa = createPersonAttribute(MohEvaluableNameConstants.PROVIDER_INITIATED_TESTING_AND_COUNSELING);
		assertThat((String) converter.convert(pa), is("PITC"));
	}

	/**
	 * @verifies return POC for Pediatric Outpatient Clinic
	 * @see EntryPointConverter#convert(Object)
	 */
	@Test
	public void convert_shouldReturnPOCForPediatricOutpatientClinic() throws Exception {
		PersonAttribute pa = createPersonAttribute(MohEvaluableNameConstants.PEDIATRIC_OUTPATIENT_CLINIC);
		assertThat((String) converter.convert(pa), is("POC"));
	}

	/**
	 * @verifies return Other for Other Non Coded
	 * @see EntryPointConverter#convert(Object)
	 */
	@Test
	public void convert_shouldReturnOtherForOtherNonCoded() throws Exception {
		PersonAttribute pa = createPersonAttribute(MohEvaluableNameConstants.OTHER_NON_CODED);
		assertThat((String) converter.convert(pa), is(EntryPointConverter.OTHER));
	}

	/**
	 * @verifies return Other if no point of HIV testing exists
	 * @see EntryPointConverter#convert(Object)
	 */
	@Test
	public void convert_shouldReturnOtherIfNoPointOfHIVTestingExists() throws Exception {
		PersonAttribute pa = new PersonAttribute();
		assertThat((String) converter.convert(pa), is(EntryPointConverter.OTHER));
	}

	/**
	 * @verifies return Other if point of HIV testing is not recognized
	 * @see EntryPointConverter#convert(Object)
	 */
	@Test
	public void convert_shouldReturnOtherIfPointOfHIVTestingIsNotRecognized() throws Exception {
		PersonAttribute pa = createPersonAttribute("fake");
		assertThat((String) converter.convert(pa), is(EntryPointConverter.OTHER));
	}
}
