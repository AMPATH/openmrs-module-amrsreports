package org.openmrs.module.amrsreports.reporting.converter;

import org.openmrs.module.amrsreports.AmrsReportsConstants;
import org.openmrs.module.reporting.data.converter.DataConverter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Converter for formatting WHO Stage and Date column data
 */

public class DateListCustomConverter implements DataConverter {

    private String desiredFormat;


    public DateListCustomConverter(){
       //do nothing
    }

    public  DateListCustomConverter(String format){
        this.desiredFormat = format;

    }

    /**
     * @should return a formatted String from a list of dates
     * @param original
     * @return formatted string of date list
     */
	@Override
	public Object convert(Object original) {
        List<Date> listOfDates = (List<Date>) original;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(this.desiredFormat);

        String formattedDate = "";

        for(Date date:listOfDates){
            String thisDate = simpleDateFormat.format(date);
            formattedDate += thisDate + AmrsReportsConstants.INTER_CELL_SEPARATOR;
        }
        return formattedDate;
	}

	@Override
	public Class<?> getInputDataType() {
		return List.class;
	}

	@Override
	public Class<?> getDataType() {
		return List.class;
	}

    public String getDesiredFormat() {
        return desiredFormat;
    }

    public void setDesiredFormat(String desiredFormat) {
        this.desiredFormat = desiredFormat;
    }

}
