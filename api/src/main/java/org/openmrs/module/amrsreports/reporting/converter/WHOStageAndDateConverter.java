package org.openmrs.module.amrsreports.reporting.converter;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.module.amrsreports.cache.MohCacheUtils;
import org.openmrs.module.amrsreports.reporting.common.ObsRepresentation;
import org.openmrs.module.amrsreports.rule.MohEvaluableNameConstants;
import org.openmrs.module.amrsreports.util.MOHReportUtil;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.util.OpenmrsUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Converter for formatting WHO Stage and Date column data
 */

public class WHOStageAndDateConverter implements DataConverter {

	@Override
	public Object convert(Object original) {
		ObsRepresentation o = (ObsRepresentation) original;

		if (o == null)
			return "";

		String whoStage = (String) new WHOStageConverter().convert(o);

		if (StringUtils.isBlank(whoStage))
			return "";

		return String.format(
				MOHReportUtil.joinAsSingleCell(whoStage, "%s"),
				MOHReportUtil.formatdates(o.getObsDatetime()));
	}

	@Override
	public Class<?> getInputDataType() {
		return ObsRepresentation.class;
	}

	@Override
	public Class<?> getDataType() {
		return String.class;
	}
}
