package org.openmrs.module.amrsreports.reporting.converter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptNumeric;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.reporting.common.ObsRepresentation;
import org.openmrs.module.reporting.data.converter.DataConverter;

import java.text.DecimalFormat;

/**
 * Converter to get valueCoded from an observation
 */
public class ObsRepresentationValueNumericConverter implements DataConverter {

	private Integer precision = null;

	private Log log = LogFactory.getLog(getClass());

	private static final DecimalFormat df = new DecimalFormat();

	public ObsRepresentationValueNumericConverter() {
		// pass
	}

	public ObsRepresentationValueNumericConverter(Integer precision) {
		this.setPrecision(precision);
	}

	@Override
	public Object convert(Object original) {

		ObsRepresentation o = (ObsRepresentation) original;

		if (o == null)
			return "";

		if (o.getConceptId() == null) {
			log.warn("no concept on an ObsRepresentation " + o);
			return "";
		}

		Concept c = Context.getConceptService().getConcept(o.getConceptId());
		if (c == null || !(c instanceof ConceptNumeric))
			return "";

		ConceptNumeric cn = (ConceptNumeric) c;
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
		return ObsRepresentation.class;
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
