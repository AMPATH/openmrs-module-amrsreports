package org.openmrs.module.amrsreports.reporting.data.evaluator;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.reporting.data.ARTTransferStatusDataDefinition;
import org.openmrs.module.amrsreports.reporting.data.EligibilityForARTDataDefinition;
import org.openmrs.module.amrsreports.reporting.data.EligibilityForARTIncludingTransferDataDefinition;
import org.openmrs.module.amrsreports.snapshot.ARVPatientSnapshot;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.evaluator.PersonDataEvaluator;
import org.openmrs.module.reporting.data.person.service.PersonDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

import java.util.Map;

/**
 * Evaluator class for Eligibility for ART and Pre-ART
 */
@Handler(supports = EligibilityForARTIncludingTransferDataDefinition.class, order = 50)
public class EligibilityForARTIncludingTransferEvaluator implements PersonDataEvaluator {

    @Override
    public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {

        PersonDataService pdservice = Context.getService(PersonDataService.class);
        EvaluatedPersonData results = new EvaluatedPersonData(definition, context);

        // get transfer status for people
        EvaluatedPersonData transferStatus = pdservice.evaluate(new ARTTransferStatusDataDefinition(), context);

        // make a list for non-transfers
        Cohort nonTransfers = new Cohort();

        // create snapshots for people who transferred in, and add the rest to a list
        for (Map.Entry<Integer, Object> e : transferStatus.getData().entrySet()) {

            // if the person transferred
            if (((Boolean) e.getValue()) == Boolean.TRUE) {

                ARVPatientSnapshot s = new ARVPatientSnapshot();
                s.set("transfer", true);
                results.addData(e.getKey(), s);

            } else {
                nonTransfers.addMember(e.getKey());
            }
        }

        EvaluationContext eligibilityContext = context.shallowCopy();
        eligibilityContext.setBaseCohort(nonTransfers);

        // evaluate eligibility for non-transfers
        EvaluatedPersonData eligibility = pdservice.evaluate(new EligibilityForARTDataDefinition(), eligibilityContext);
        // add the results from eligibility to the final results

        for(Integer pid: eligibilityContext.getBaseCohort().getMemberIds()){
            results.addData(pid,eligibility.getData().get(pid));
        }

        return results;

    }

}