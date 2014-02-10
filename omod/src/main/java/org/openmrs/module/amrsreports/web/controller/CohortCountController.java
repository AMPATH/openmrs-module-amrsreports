package org.openmrs.module.amrsreports.web.controller;

import org.openmrs.Cohort;
import org.openmrs.Location;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.MOHFacility;
import org.openmrs.module.amrsreports.reporting.cohort.definition.Moh361ACohortDefinition;
import org.openmrs.module.amrsreports.reporting.provider.ReportProvider;
import org.openmrs.module.amrsreports.service.MOHFacilityService;
import org.openmrs.module.amrsreports.service.ReportProviderRegistrar;
import org.openmrs.module.amrsreports.service.UserFacilityService;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Controller for the cohortCount page
 */
@Controller
public class CohortCountController {

    @ModelAttribute("facilities")
    public List<MOHFacility> getAllFacilities() {
        User currentUser = Context.getAuthenticatedUser();
        List<MOHFacility> relevantFacilities = Context.getService(UserFacilityService.class).getAllowedFacilitiesForUser(currentUser);

        return relevantFacilities;
    }

    @ModelAttribute("reportProviders")
    public List<ReportProvider> getAllProviders() {
        return ReportProviderRegistrar.getInstance().getAllReportProviders();
    }

	@RequestMapping(value = "module/amrsreports/cohortCounts", method = RequestMethod.GET)
	public void setup() {
		// do nothing
	}

	@RequestMapping(value = "module/amrsreports/downloadCohortCounts.htm")
	public void download(HttpServletResponse response,
	                     @RequestParam(required = true, value = "locations") List<Integer> locations,
	                     @RequestParam(required = true, value = "evaluationDate") Date evaluationDate) throws IOException {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");

		String filename = "MOH361ACohort-" + sdf.format(evaluationDate) + ".csv";

		response.setContentType("text/csv; filename=\"" + filename + "\"");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

		PrintWriter writer = new PrintWriter(response.getOutputStream());
		writer.println("location_id,person_id");

		EvaluationContext context = new EvaluationContext();
		context.setEvaluationDate(evaluationDate);

		for (Integer locationId : locations) {
			Location location = Context.getLocationService().getLocation(locationId);
			if (location != null) {

				Moh361ACohortDefinition definition = new Moh361ACohortDefinition();
//				definition.addLocation(location);

				context.addParameterValue("location", location);

				try {
					Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(definition, context);
					for (Integer personId : cohort.getMemberIds()) {
						writer.println(locationId + "," + personId);
					}
				} catch (EvaluationException e) {
					writer.println("-1,ERROR! Check with system administrator for more information.");
				}
			}
		}

		writer.flush();
		writer.close();
	}
}
