package org.openmrs.module.amrsreports.reporting.converter;

import org.openmrs.module.amrsreports.snapshot.ARVPatientSnapshot;
import org.openmrs.module.amrsreports.util.MOHReportUtil;
import org.openmrs.module.reporting.data.converter.DataConverter;

import java.util.Date;

/**
 * Converter for Patient Snapshots
 */
public class ARVPatientSnapshotDateConverter implements DataConverter {

	/**
	 * @see DataConverter#convert(Object)
	 *
	 * @should return null if snapshot is null
	 * @should return null if no reason exists
	 * @should return null if no lastDate exists
	 * @should return lastDate as Date
	 */
	@Override
	public Object convert(Object original) {
		ARVPatientSnapshot s = (ARVPatientSnapshot) original;

		if (s == null)
			return null;

		// do not report a date if there is no reason
		if (!s.hasProperty("reason"))
			return null;

		// do not report a date if there is no date
		if (!s.hasProperty("lastDate"))
			return null;

		return s.get("lastDate");
	}

	@Override
	public Class<?> getInputDataType() {
		return ARVPatientSnapshot.class;
	}

	@Override
	public Class<?> getDataType() {
		return Date.class;
	}
}
