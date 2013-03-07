package org.openmrs.module.amrsreport.reporting.converter;

import org.openmrs.module.amrsreport.model.WHOStageAndDate;
import org.openmrs.module.amrsreport.rule.util.MohRuleUtils;
import org.openmrs.module.reporting.data.converter.DataConverter;

/**
 * Converter for formatting WHO Stage and Date column data
 */

public class WHOStageAndDateConverter implements DataConverter {
	@Override
	public Object convert(Object original) {
		WHOStageAndDate whoStageAndDate = (WHOStageAndDate) original;
		return String.format("%s - %s", whoStageAndDate.getStage(), MohRuleUtils.formatdates(whoStageAndDate.getDate()));
	}

	@Override
	public Class<?> getInputDataType() {
		return WHOStageAndDate.class;
	}

	@Override
	public Class<?> getDataType() {
		return String.class;
	}
}
