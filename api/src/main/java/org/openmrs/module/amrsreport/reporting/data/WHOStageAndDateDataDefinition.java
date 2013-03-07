package org.openmrs.module.amrsreport.reporting.data;

import org.openmrs.module.amrsreport.model.WHOStageAndDate;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;

/**
 * finds the current WHO Stage and date determined for anyone in the cohort
 */
public class WHOStageAndDateDataDefinition extends BaseDataDefinition implements PersonDataDefinition {
	@Override
	public Class<?> getDataType() {
		return WHOStageAndDate.class;
	}
}
