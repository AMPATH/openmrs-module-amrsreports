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
 * Returns month name from art start date
 */

public class ARTMonthZeroConverter implements DataConverter {
    private  String DATE_FORMAT = "MMMM";
	/**
	 * @param original
	 * @return string for month name
	 * @should return a string for month name
	 */
	@Override
	public Object convert(Object original) {

		if (original == null)
			return null;

		Date date = (Date) original;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(this.DATE_FORMAT);

		return simpleDateFormat.format(date);
	}

	@Override
	public Class<?> getInputDataType() {
		return Date.class;
	}

	@Override
	public Class<?> getDataType() {
		return String.class;
	}


}
