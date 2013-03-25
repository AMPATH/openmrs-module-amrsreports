package org.openmrs.module.amrsreports.reporting.converter;

import org.openmrs.module.amrsreports.snapshot.ARVPatientSnapshot;
import org.openmrs.module.amrsreports.rule.util.MohRuleUtils;
import org.openmrs.module.reporting.data.converter.DataConverter;

import java.util.Date;

/**
 * Converter for Patient Snapshots
 */
public class ARVPatientSnapshotDateConverter implements DataConverter {

	@Override
	public Object convert(Object original) {
		ARVPatientSnapshot s = (ARVPatientSnapshot) original;

		if (s == null)
			return "";

		Date d = (Date) s.get("lastDate");
		if (d == null)
			return "";

		return MohRuleUtils.formatdates(d);
	}

	@Override
	public Class<?> getInputDataType() {
		return ARVPatientSnapshot.class;
	}

	@Override
	public Class<?> getDataType() {
		return String.class;
	}
}
