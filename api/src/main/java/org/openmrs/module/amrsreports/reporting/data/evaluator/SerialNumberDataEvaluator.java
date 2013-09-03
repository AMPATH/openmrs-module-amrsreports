package org.openmrs.module.amrsreports.reporting.data.evaluator;

import org.openmrs.PatientIdentifier;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.AmrsReportsConstants;
import org.openmrs.module.amrsreports.MOHFacility;
import org.openmrs.module.amrsreports.reporting.data.SerialNumberDataDefinition;
import org.openmrs.module.amrsreports.service.MOHFacilityService;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.evaluator.PersonDataEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

import java.util.Map;

/**
 * Handler for enrollment date column
 */
@Handler(supports = SerialNumberDataDefinition.class, order = 50)
public class SerialNumberDataEvaluator implements PersonDataEvaluator {

	@Override
	public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {
		EvaluatedPersonData ret = new EvaluatedPersonData(definition, context);

		if (context.getBaseCohort().isEmpty())
			return ret;

		// find the facility number
		MOHFacility facility = (MOHFacility) context.getParameterValue("facility");

		// request all CCC numbers for that facility
		Map<Integer, String> cccMap = Context.getService(MOHFacilityService.class)
				.getSerialNumberMapForFacility(facility);

		// populate them, leaving the rest null
		for (Integer personId : context.getBaseCohort().getMemberIds()) {
			ret.addData(personId,
					cccMap.containsKey(personId) ? cccMap.get(personId) : AmrsReportsConstants.TRANSFER_IN);
		}

		return ret;
	}

}
