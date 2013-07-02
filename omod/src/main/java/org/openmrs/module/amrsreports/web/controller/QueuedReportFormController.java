package org.openmrs.module.amrsreports.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.MOHFacility;
import org.openmrs.module.amrsreports.QueuedReport;
import org.openmrs.module.amrsreports.reporting.provider.ReportProvider;
import org.openmrs.module.amrsreports.service.MOHFacilityService;
import org.openmrs.module.amrsreports.service.QueuedReportService;
import org.openmrs.module.amrsreports.service.ReportProviderRegistrar;
import org.openmrs.module.amrsreports.service.UserFacilityService;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * controller for Run AMRS Reports page
 */
@Controller
public class QueuedReportFormController {

	private final Log log = LogFactory.getLog(getClass());

	private static final String FORM_VIEW = "module/amrsreports/queuedReportForm";
	private static final String SUCCESS_VIEW = "redirect:queuedReport.list";

	@ModelAttribute("queuedReports")
	public List<QueuedReport> getQueuedReports() {
		return Context.getService(QueuedReportService.class).getQueuedReportsWithStatus(QueuedReport.STATUS_NEW);
	}

	@ModelAttribute("currentReport")
	public List<QueuedReport> getCurrentReport() {
		return Context.getService(QueuedReportService.class).getQueuedReportsWithStatus(QueuedReport.STATUS_RUNNING);
	}

	@ModelAttribute("facilities")
	public List<MOHFacility> getFacilities() {
		return Context.getService(UserFacilityService.class).getAllowedFacilitiesForUser(Context.getAuthenticatedUser());
	}

	@ModelAttribute("reportProviders")
	public List<ReportProvider> getReportProviders() {
		return ReportProviderRegistrar.getInstance().getAllReportProviders();
	}

	@RequestMapping(method = RequestMethod.GET, value = "module/amrsreports/queuedReport.form")
	public String preparePage() {
		return FORM_VIEW;
	}

	@RequestMapping(method = RequestMethod.POST, value = "module/amrsreports/queuedReport.form")
	public String processForm(HttpServletRequest request,
							  @RequestParam(value = "immediate", required = false) Boolean immediate,
							  @RequestParam("reportDate") Date reportDate,
							  @RequestParam("dateScheduled") String dateScheduled,
							  @RequestParam("facility") Integer facilityId,
							  @RequestParam("reportName") String reportName,
							  @RequestParam("repeatIntervalUnits") String repeatIntervalUnits,
							  @RequestParam("repeatInterval") Integer repeatInterval
	) throws Exception {

		// find the facility
		MOHFacility facility = Context.getService(MOHFacilityService.class).getFacility(facilityId);

		// create a queued report
		QueuedReport queuedReport = new QueuedReport();
		queuedReport.setFacility(facility);
		queuedReport.setReportName(reportName);
		queuedReport.setEvaluationDate(reportDate);

		SimpleDateFormat df = new SimpleDateFormat("yyyy-mm-dd hh:mm a");
		Date scheduledDate = df.parse(dateScheduled);
		if (immediate == null)
			queuedReport.setDateScheduled(scheduledDate);
		else
			queuedReport.setDateScheduled(new Date());

		int repeatIntervalInSec;

		if (repeatInterval == null || repeatInterval == 0) {
			repeatIntervalInSec = 0;
		} else {

			if (repeatIntervalUnits.equals("minutes")) {
				repeatIntervalInSec = repeatInterval * 60;
			} else if (repeatIntervalUnits.equals("hours")) {
				repeatIntervalInSec = repeatInterval * 60 * 60;
			} else {
				repeatIntervalInSec = repeatInterval * 60 * 60 * 24;
			}

			queuedReport.setRepeatInterval(repeatIntervalInSec);

		}
		// save it
		QueuedReportService queuedReportService = Context.getService(QueuedReportService.class);
		queuedReportService.saveQueuedReport(queuedReport);

		// kindly respond
		HttpSession httpSession = request.getSession();
		httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Report queued for processing.");

		return SUCCESS_VIEW;
	}

}
