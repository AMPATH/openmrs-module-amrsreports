package org.openmrs.module.amrsreports.reporting.converter;

import org.openmrs.Obs;
import org.openmrs.module.amrsreports.AmrsReportsConstants;
import org.openmrs.module.reporting.data.converter.DataConverter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Converter for formatting WHO Stage and Date column data
 */

public class DateListCustomConverter implements DataConverter {


	@Override
	public Object convert(Object original) {
		return null;
	}

    /**
     * @should return a formatted String from a list of dates
     * @param format
     * @param dateList
     * @return a list of dates separated by line breaks
     */
    public Object convert(String format,List<Date> dateList) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);

        String formattedDate = "";

        for(Date date:dateList){
            String thisDate = simpleDateFormat.format(date);
            formattedDate += thisDate + AmrsReportsConstants.INTER_CELL_SEPARATOR;
        }
        return formattedDate;
    }

	@Override
	public Class<?> getInputDataType() {
		return Obs.class;
	}

	@Override
	public Class<?> getDataType() {
		return String.class;
	}
}
