package org.openmrs.module.amrsreports.reporting.converter;

import org.apache.commons.lang.StringUtils;
import org.openmrs.module.amrsreports.AmrsReportsConstants;
import org.openmrs.module.reporting.data.converter.DataConverter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * converter class for TbTreatmentStartDateDataDefinition
 */
public class TbTreatmentStartDateConverter implements DataConverter {
    @Override
    public Object convert(Object original) {
        if (original == null)
            return null;

        List details = (List) original;
        Set<Date> listOfDates = (Set<Date>) details.get(0);
        List<Date> thisList = new ArrayList<Date>(listOfDates);
        Date firstTbTreatment = thisList.get(0);

        String regNo = (String)details.get(1);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        simpleDateFormat.format(firstTbTreatment);

        List formattedOutput = new ArrayList();
        formattedOutput.add(firstTbTreatment);
        formattedOutput.add(regNo);

        return StringUtils.join(formattedOutput, AmrsReportsConstants.INTER_CELL_SEPARATOR);
    }

    @Override
    public Class<?> getInputDataType() {
        return List.class;
    }

    @Override
    public Class<?> getDataType() {
        return String.class;
    }
}
