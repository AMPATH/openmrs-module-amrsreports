package org.openmrs.module.amrsreport.reporting.data;

import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;

import java.util.Date;

/**
 * Enrollment Date column
 */
public class EnrollmentDateDataDefinition extends BaseDataDefinition implements PersonDataDefinition {

	@Override
	public Class<?> getDataType() {
		return Date.class;
	}
}
