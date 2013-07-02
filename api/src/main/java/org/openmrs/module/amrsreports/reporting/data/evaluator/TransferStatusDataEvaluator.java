package org.openmrs.module.amrsreports.reporting.data.evaluator;

import org.openmrs.PatientIdentifier;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.MOHFacility;
import org.openmrs.module.amrsreports.reporting.data.TransferStatusDataDefinition;
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
@Handler(supports = TransferStatusDataDefinition.class, order = 50)
public class TransferStatusDataEvaluator implements PersonDataEvaluator {

	@Override
	public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {
		EvaluatedPersonData ret = new EvaluatedPersonData(definition, context);

		if (context.getBaseCohort().isEmpty())
			return ret;

		// find the facility number
		MOHFacility facility = (MOHFacility) context.getParameterValue("facility");

		// request all CCC numbers for that facility
		Map<Integer, PatientIdentifier> cccMap = Context.getService(MOHFacilityService.class)
				.getCCCNumberMapForFacility(facility);

		// set value to opposite of whether the person is not enrolled immediately in this facility
		for (Integer personId : context.getBaseCohort().getMemberIds()) {
			ret.addData(personId, !cccMap.containsKey(personId));
		}

		return ret;
	}
}
