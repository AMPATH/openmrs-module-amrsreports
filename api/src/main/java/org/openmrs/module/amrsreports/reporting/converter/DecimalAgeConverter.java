package org.openmrs.module.amrsreports.reporting.converter;

import org.openmrs.module.reporting.common.Age;
import org.openmrs.module.reporting.data.converter.DataConverter;

import java.text.DecimalFormat;

/**
 * Converts an Age into a Double unless precision is specified; in that case, it returns a formatted string
 */
public class DecimalAgeConverter implements DataConverter {

	private Integer precision = null;

	private static final DecimalFormat df = new DecimalFormat();

	public DecimalAgeConverter() {
		// do nothing
	}

	public DecimalAgeConverter(Integer precision) {
		this.setPrecision(precision);
	}

	@Override
	public Object convert(Object original) {

		if (original == null)
			return null;

		Age age = (Age) original;
		Double decimalAge = age.getFullYears() + (new Double(age.getFullMonthsSinceLastBirthday()) / 12d);

		if (precision == null)
			return decimalAge;

		return df.format(decimalAge);
	}

	@Override
	public Class<?> getInputDataType() {
		return Age.class;
	}

	@Override
	public Class<?> getDataType() {
		if (precision == null)
			return Double.class;

		return String.class;
	}

	public Integer getPrecision() {
		return precision;
	}

	public void setPrecision(Integer precision) {
		this.precision = precision;
		df.setMinimumFractionDigits(precision);
		df.setMaximumFractionDigits(precision);
	}
}
