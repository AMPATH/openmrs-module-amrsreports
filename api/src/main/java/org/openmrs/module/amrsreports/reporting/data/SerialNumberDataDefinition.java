package org.openmrs.module.amrsreports.reporting.data;

import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;

import java.util.Date;

/**
 * Enrollment Date column
 */
public class SerialNumberDataDefinition extends BaseDataDefinition implements PersonDataDefinition {

	@Override
	public Class<?> getDataType() {
		return String.class;
	}
}
