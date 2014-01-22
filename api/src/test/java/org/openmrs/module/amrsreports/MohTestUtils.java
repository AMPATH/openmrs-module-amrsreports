package org.openmrs.module.amrsreports;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptName;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.cache.MohCacheUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

/**
 *Contains utility methods for test classes
 */
public class MohTestUtils {
    private static final Log log = LogFactory.getLog(MohTestUtils.class);

    /**
     * create a question concept with associated set of answers from strings
     *
     * @param question
     * @param answers
     */
    public static void createQuestion(String question, String[] answers) {
        // create a new question concept
        ConceptService conceptService=Context.getConceptService();
        Concept q = new Concept();
        q.addName(new ConceptName(question, Context.getLocale()));
        q.setConceptClass(conceptService.getConceptClassByName("Question"));

        // loop over answers and add them one by one to the question
        for (String answer: answers) {
            // create a new concept for the answer
            Concept a = new Concept();
            a.addName(new ConceptName(answer, Context.getLocale()));
            conceptService.saveConcept(a);
            // create a ConceptAnswer and add it to the question
            ConceptAnswer ca = new ConceptAnswer();
            ca.setAnswerConcept(a);
            q.addAnswer(ca);
        }

        // save the question
        conceptService.saveConcept(q);
    }

	public static Patient createTestPatient() {
		Patient patient = new Patient();

		PersonName pName = new PersonName();
		pName.setGivenName("Tom");
		pName.setMiddleName("E.");
		pName.setFamilyName("Patient");
		patient.addName(pName);

		PersonAddress pAddress = new PersonAddress();
		pAddress.setAddress1("123 My street");
		pAddress.setAddress2("Apt 402");
		pAddress.setCityVillage("Anywhere city");
		pAddress.setCountry("Some Country");
		Set<PersonAddress> pAddressList = patient.getAddresses();
		pAddressList.add(pAddress);
		patient.setAddresses(pAddressList);
		patient.addAddress(pAddress);

		PatientIdentifier pi = new PatientIdentifier();
		pi.setLocation(Context.getLocationService().getLocation(1));
		pi.setIdentifierType(Context.getPatientService().getPatientIdentifierType(2));
		pi.setIdentifier("10101");
		patient.addIdentifier(pi);

		patient.setBirthdate(new Date());
		patient.setBirthdateEstimated(true);
		patient.setGender("male");

		patient = Context.getPatientService().savePatient(patient);

		return patient;
	}
    /*
    * Add Person Attribute to an existing patient
    * */

    public static Patient addAttribute(Patient p,Integer attrbId,String attribVal){

        PersonAttribute personAttribute = new PersonAttribute();
        personAttribute.setAttributeType(new PersonAttributeType(attrbId));
        personAttribute.setValue(attribVal);
        personAttribute.setDateCreated(new Date());
        personAttribute.setVoided(false);
        p.addAttribute(personAttribute);
        p = Context.getPatientService().savePatient(p);

        return p;

    }

	/**
	 * generate a date from a string
	 *
	 * @param date
	 * @return
	 */
	public static Date makeDate(String date) {
		try {
			return new SimpleDateFormat("d MMM yyyy", Locale.ENGLISH).parse(date);
		} catch (Exception e) {
			// pass
		}
		return new Date();
	}


	public static void addCodedObs(Patient p, String conceptName, String conceptAnswer, String date) {
		Obs o = new Obs();
		o.setPerson(p);
		o.setConcept(MohCacheUtils.getConcept(conceptName));
		o.setValueCoded(MohCacheUtils.getConcept(conceptAnswer));
		o.setObsDatetime(MohTestUtils.makeDate(date));
		Context.getObsService().saveObs(o, null);

	}

    public static void addDateTimeObs(Patient p, String conceptName, String conceptAnswer, String date) {
        Obs o = new Obs();
        o.setPerson(p);
        o.setConcept(MohCacheUtils.getConcept(conceptName));
        o.setValueDatetime(MohTestUtils.makeDate(conceptAnswer));
        o.setObsDatetime(MohTestUtils.makeDate(date));
        Context.getObsService().saveObs(o, null);
    }

}
