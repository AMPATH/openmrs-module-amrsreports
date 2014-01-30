package org.openmrs.module.amrsreports.reporting.converter;

import junit.framework.TestCase;
import org.junit.Test;
import org.openmrs.module.amrsreports.AmrsReportsConstants;
import org.openmrs.module.amrsreports.model.PatientTBTreatmentData;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * test class for TbTreatmentStartDateConverter
 */
public class TbTreatmentStartDateConverterTest extends TestCase {
    @Test
    public void test_list_of_dates_with_tbRegNo() throws Exception {

        Set<Date> dateList = new LinkedHashSet<Date>(Arrays.asList(
                new Date("05 Dec 2012"),
                new Date("02 Apr 2013"),
                new Date("25 May 2013")));

        String expected = (
                "12/2012" + AmrsReportsConstants.INTER_CELL_SEPARATOR +
                        "04/2013" + AmrsReportsConstants.INTER_CELL_SEPARATOR +
                        "05/2013" + AmrsReportsConstants.INTER_CELL_SEPARATOR +
                        "TB/560/2013");


        PatientTBTreatmentData patientTBTreatmentData = new PatientTBTreatmentData();
        patientTBTreatmentData.setEvaluationDates(dateList);
        patientTBTreatmentData.setTbRegNO("TB/560/2013");

        TbTreatmentStartDateConverter tbTreatmentStartDateConverter = new TbTreatmentStartDateConverter();

        assertEquals(expected,tbTreatmentStartDateConverter.convert(patientTBTreatmentData));

    }

    @Test
    public void test_list_of_dates_with_null_tbRegNo() throws Exception {

        Set<Date> dateList = new LinkedHashSet<Date>(Arrays.asList(
                new Date("05 Dec 2012"),
                new Date("02 Apr 2013"),
                new Date("25 May 2013")));

        String expected = (
                "12/2012" + AmrsReportsConstants.INTER_CELL_SEPARATOR +
                        "04/2013" + AmrsReportsConstants.INTER_CELL_SEPARATOR +
                        "05/2013");


        PatientTBTreatmentData patientTBTreatmentData = new PatientTBTreatmentData();
        patientTBTreatmentData.setEvaluationDates(dateList);

        TbTreatmentStartDateConverter tbTreatmentStartDateConverter = new TbTreatmentStartDateConverter();

        assertEquals(expected,tbTreatmentStartDateConverter.convert(patientTBTreatmentData));

    }
}
