package org.openmrs.module.amrsreports.reporting.data;

import org.openmrs.module.reporting.common.Age;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.person.definition.AgeDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

import java.util.Date;

/**
 * Evaluates age of a person at the date of report evaluation, provided by the context
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
public class AgeAtEvaluationDateDataDefinition extends BaseDataDefinition implements PersonDataDefinition {

	public static final long serialVersionUID = 1L;

	/**
	 * Default Constructor
	 */
	public AgeAtEvaluationDateDataDefinition() {
		super();
	}

	/**
	 * Constructor to populate name only
	 */
	public AgeAtEvaluationDateDataDefinition(String name) {
		super(name);
	}

	//***** INSTANCE METHODS *****

	/**
	 * @see org.openmrs.module.reporting.data.DataDefinition#getDataType()
	 */
	public Class<?> getDataType() {
		return Age.class;
	}

}
