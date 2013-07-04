package org.openmrs.module.amrsreports.reporting.converter;

import org.openmrs.Obs;
import org.openmrs.module.amrsreports.AmrsReportsConstants;
import org.openmrs.module.reporting.data.converter.DataConverter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Converter for formatting WHO Stage and Date column data
 */

public class DateListCustomConverter implements DataConverter {

    private String desiredFormat;
    private List<Date> listOfDates = new ArrayList<Date>();

    public DateListCustomConverter(){
       //do nothing
    }

    public  DateListCustomConverter(String format,List<Date> dateList){
        this.desiredFormat = format;
        this.listOfDates = dateList;

    }

    /**
     * @should return a formatted String from a list of dates
     * @param original
     * @return formatted string of date list
     */
	@Override
	public Object convert(Object original) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(this.desiredFormat);

        String formattedDate = "";

        for(Date date:this.listOfDates){
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

    public String getDesiredFormat() {
        return desiredFormat;
    }

    public void setDesiredFormat(String desiredFormat) {
        this.desiredFormat = desiredFormat;
    }

    public List<Date> getListOfDates() {
        return listOfDates;
    }

    public void setListOfDates(List<Date> listOfDates) {
        this.listOfDates = listOfDates;
    }
}
