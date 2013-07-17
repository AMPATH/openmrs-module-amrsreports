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
 * Converter for formatting a list of dates using a simple date format
 */

public class DateListCustomConverter implements DataConverter {

	private String desiredFormat;


	public DateListCustomConverter() {
		//do nothing
	}

	public DateListCustomConverter(String format) {
		this.desiredFormat = format;
	}

	/**
	 * @param original
	 * @return formatted string of date list
	 * @should return a formatted String from a list of dates
	 */
	@Override
	public Object convert(Object original) {

		if (original == null)
			return null;

		Set<Date> listOfDates = (Set<Date>) original;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(this.desiredFormat);

		List<String> formattedDates = new ArrayList<String>();

		for (Date date : listOfDates) {
			if (date != null)
				formattedDates.add(simpleDateFormat.format(date));
		}

		return StringUtils.join(formattedDates, AmrsReportsConstants.INTER_CELL_SEPARATOR);
	}

	@Override
	public Class<?> getInputDataType() {
		return Set.class;
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

}
