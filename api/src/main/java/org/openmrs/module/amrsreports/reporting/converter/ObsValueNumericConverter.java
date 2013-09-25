package org.openmrs.module.amrsreports.reporting.converter;

import org.openmrs.ConceptNumeric;
import org.openmrs.Obs;
import org.openmrs.module.reporting.data.converter.DataConverter;

import java.text.DecimalFormat;

/**
 * Converter to get valueCoded from an observation
 */
public class ObsValueNumericConverter implements DataConverter {

	private Integer precision = null;

	private static final DecimalFormat df = new DecimalFormat();

	public ObsValueNumericConverter() {
		// pass
	}

	public ObsValueNumericConverter(Integer precision) {
		this.setPrecision(precision);
	}

	@Override
	public Object convert(Object original) {

		Obs o = (Obs) original;

		if (o == null || !(o.getConcept() instanceof ConceptNumeric))
			return "";

		ConceptNumeric cn = (ConceptNumeric) o.getConcept();
		String units = cn.getUnits();

		String value;
		if (precision != null) {
			value = df.format(o.getValueNumeric());
		} else {
			value = o.getValueNumeric().toString();
		}

		return String.format("%s %s", value, units);
	}

	@Override
	public Class<?> getInputDataType() {
		return Obs.class;
	}

	@Override
	public Class<?> getDataType() {
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
