package org.openmrs.module.amrsreports.reporting.data.evaluator;

import org.openmrs.Location;
import org.openmrs.PatientIdentifier;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.MOHFacility;
import org.openmrs.module.amrsreports.reporting.data.ARTTransferStatusDataDefinition;
import org.openmrs.module.amrsreports.reporting.data.TransferStatusDataDefinition;
import org.openmrs.module.amrsreports.service.MOHFacilityService;
import org.openmrs.module.amrsreports.service.MohCoreService;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.evaluator.PersonDataEvaluator;
import org.openmrs.module.reporting.dataset.query.service.DataSetQueryService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Handler for enrollment date column
 */
@Handler(supports = ARTTransferStatusDataDefinition.class, order = 50)
public class ARTTransferStatusDataEvaluator implements PersonDataEvaluator {

	@Override
	public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {
		EvaluatedPersonData ret = new EvaluatedPersonData(definition, context);

		if (context.getBaseCohort().isEmpty())
			return ret;

        DataSetQueryService qs = Context.getService(DataSetQueryService.class);

        Map<String, Object> m = new HashMap<String, Object>();


        String sql = "select patient_id " +
                "from amrsreports_hiv_care_enrollment " +
                "where first_arv_date <= :reportDate " +
                "and not first_arv_location_id in (:locationList)";

        // find the facility number
        MOHFacility facility = (MOHFacility) context.getParameterValue("facility");
        Set<Location> locationList = facility.getLocations();

        m.put("reportDate", context.getEvaluationDate());
        m.put("locationList", locationList);

        List<Object> queryResult = Context.getService(MohCoreService.class).executeSqlQuery(sql, m);

        for(Integer patientId: context.getBaseCohort().getMemberIds()){
            boolean ti = false ;
            if(queryResult.contains(patientId)){
                ti = true;
            }
            ret.addData(patientId,ti);
        }

        return ret;
	}
}
