package org.openmrs.module.amrsreports.reporting.converter;

import org.openmrs.Encounter;
import org.openmrs.module.amrsreports.rule.util.MohRuleUtils;
import org.openmrs.module.reporting.data.converter.DataConverter;

/**
 * Converter to get the encounter date from an encounter and format it for the MOH
 */
public class MOHEncounterDateConverter implements DataConverter {
	@Override
	public Object convert(Object original) {
		Encounter e = (Encounter) original;

		if (e == null)
			return "";

		return MohRuleUtils.formatdates(e.getEncounterDatetime());
	}

	@Override
	public Class<?> getInputDataType() {
		return Encounter.class;
	}

	@Override
	public Class<?> getDataType() {
		return String.class;
	}
}
