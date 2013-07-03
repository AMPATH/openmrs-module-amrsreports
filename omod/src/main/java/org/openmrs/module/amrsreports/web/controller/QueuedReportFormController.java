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
import org.openmrs.util.OpenmrsUtil;
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

	@ModelAttribute("facilities")
	public List<MOHFacility> getFacilities() {
		return Context.getService(UserFacilityService.class).getAllowedFacilitiesForUser(Context.getAuthenticatedUser());
	}

	@ModelAttribute("reportProviders")
	public List<ReportProvider> getReportProviders() {
		return ReportProviderRegistrar.getInstance().getAllReportProviders();
	}

	@ModelAttribute("datetimeFormat")
	public String getDatetimeFormat() {
		SimpleDateFormat sdf = Context.getDateFormat();
		String format = sdf.toPattern();
		format += " h:mm a";
		return format;
	}

	@ModelAttribute("now")
	public String getNow() {
		SimpleDateFormat sdf = new SimpleDateFormat(getDatetimeFormat());
		return sdf.format(new Date());
	}

	@RequestMapping(method = RequestMethod.GET, value = "module/amrsreports/queuedReport.form")
	public String preparePage() {
		return FORM_VIEW;
	}

	@RequestMapping(method = RequestMethod.POST, value = "module/amrsreports/queuedReport.form")
	public String processForm(HttpServletRequest request,
							  @RequestParam(value = "immediate", required = false) Boolean immediate,
							  @RequestParam("reportDate") Date reportDate,
							  @RequestParam("scheduleDate") String scheduleDate,
							  @RequestParam("facility") Integer facilityId,
							  @RequestParam("reportName") String reportName,
							  @RequestParam(value = "repeatIntervalUnits", required = false) String repeatIntervalUnits,
							  @RequestParam(value = "repeatInterval", required = false) Integer repeatInterval
	) throws Exception {

		// find the facility
		MOHFacility facility = Context.getService(MOHFacilityService.class).getFacility(facilityId);

		// create a queued report
		QueuedReport queuedReport = new QueuedReport();
		queuedReport.setFacility(facility);
		queuedReport.setReportName(reportName);
		queuedReport.setEvaluationDate(reportDate);

		SimpleDateFormat sdf = new SimpleDateFormat(getDatetimeFormat());
		Date scheduledDate = sdf.parse(scheduleDate);
		if (immediate == null)
			queuedReport.setDateScheduled(scheduledDate);
		else
			queuedReport.setDateScheduled(new Date());

		if (repeatInterval == null || repeatInterval < 0)
			repeatInterval = 0;

		if (OpenmrsUtil.nullSafeEquals(repeatIntervalUnits, "minutes")) {
			repeatInterval = repeatInterval * 60;
		} else if (OpenmrsUtil.nullSafeEquals(repeatIntervalUnits, "hours")) {
			repeatInterval = repeatInterval * 60 * 60;
		} else {
			repeatInterval = repeatInterval * 60 * 60 * 24;
		}

		queuedReport.setRepeatInterval(repeatInterval);

		// save it
		QueuedReportService queuedReportService = Context.getService(QueuedReportService.class);
		queuedReportService.saveQueuedReport(queuedReport);

		// kindly respond
		HttpSession httpSession = request.getSession();
		httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Report queued for processing.");

		return SUCCESS_VIEW;
	}

}
