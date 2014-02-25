package org.openmrs.module.amrsreports.reporting.data;

import org.openmrs.module.amrsreports.model.SortedObsFromDate;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.MappedData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/**
 * TB Status evaluation
 */
@Caching(strategy = ConfigurationPropertyCachingStrategy.class)
public class TBStatusDataDefinition extends BaseDataDefinition implements PersonDataDefinition {

	@ConfigurationProperty(required = false)
	private MappedData<? extends PersonDataDefinition> effectiveDateDefinition;

	/**
	 * @see org.openmrs.module.reporting.data.DataDefinition#getDataType()
	 */
	public Class<?> getDataType() {
		return SortedObsFromDate.class;
	}

	/**
	 * @return the effectiveDateDefinition
	 */
	public MappedData<? extends PersonDataDefinition> getEffectiveDateDefinition() {
		return effectiveDateDefinition;
	}

	/**
	 * @param effectiveDateDefinition the effectiveDateDefinition to set
	 */
	public void setEffectiveDateDefinition(MappedData<? extends PersonDataDefinition> effectiveDateDefinition) {
		this.effectiveDateDefinition = effectiveDateDefinition;
	}

}
