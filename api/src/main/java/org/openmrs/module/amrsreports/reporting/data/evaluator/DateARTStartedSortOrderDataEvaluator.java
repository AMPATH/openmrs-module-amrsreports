package org.openmrs.module.amrsreports.reporting.data.evaluator;

import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.AmrsReportsConstants;
import org.openmrs.module.amrsreports.reporting.data.DateARTStartedDataDefinition;
import org.openmrs.module.amrsreports.reporting.data.DateARTStartedSortOrderDataDefinition;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.evaluator.PersonDataEvaluator;
import org.openmrs.module.reporting.data.person.service.PersonDataService;
import org.openmrs.module.reporting.dataset.query.service.DataSetQueryService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Evaluator for DateARTStartedSortOrderDataDefinition
 */
@Handler(supports = DateARTStartedSortOrderDataDefinition.class, order = 50)
public class DateARTStartedSortOrderDataEvaluator implements PersonDataEvaluator {

	@Override
	public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {


		EvaluatedPersonData c = new EvaluatedPersonData(definition, context);

		if (context.getBaseCohort() == null || context.getBaseCohort().isEmpty()) {
			return c;
		}

        DateARTStartedSortOrderDataDefinition dateARTStartedSortOrderDataDefinition = new DateARTStartedSortOrderDataDefinition();

        EvaluatedPersonData artStartDateData = Context.getService(PersonDataService.class).evaluate(dateARTStartedSortOrderDataDefinition, context);

        Map<Integer,Object> actualData = artStartDateData.getData();

        //define date pattern
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
        for(Integer personID: context.getBaseCohort().getMemberIds()){

            Date dateArtStarted = (Date)actualData.get(personID);
            String fDate = simpleDateFormat.format(dateArtStarted);
            c.addData(personID, fDate);

        }

		return c;
	}
}
