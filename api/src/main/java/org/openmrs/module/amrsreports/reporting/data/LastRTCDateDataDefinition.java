package org.openmrs.module.amrsreports.reporting.data;

import org.openmrs.Obs;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;

/**
 * finds the last RTC date
 */
public class LastRTCDateDataDefinition extends BaseDataDefinition implements PersonDataDefinition {

	@Override
	public Class<?> getDataType() {
		return Obs.class;
	}

}