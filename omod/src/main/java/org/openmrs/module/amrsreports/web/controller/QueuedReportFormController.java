package org.openmrs.module.amrsreports.web.controller;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.AmrsReportsConstants;
import org.openmrs.module.amrsreports.MOHFacility;
import org.openmrs.module.amrsreports.QueuedReport;
import org.openmrs.module.amrsreports.reporting.provider.ReportProvider;
import org.openmrs.module.amrsreports.service.MOHFacilityService;
import org.openmrs.module.amrsreports.service.QueuedReportService;
import org.openmrs.module.amrsreports.service.ReportProviderRegistrar;
import org.openmrs.module.amrsreports.service.UserFacilityService;
import org.openmrs.util.OpenmrsUtil;
import org.openmrs.web.WebConstants;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * controller for Run AMRS Reports page
 */
@Controller
@SessionAttributes("queuedReports")
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

	@RequestMapping(method = RequestMethod.POST, value = "module/amrsreports/queuedReport.form")
	public String processForm(HttpServletRequest request,
							  @ModelAttribute("queuedReports") QueuedReport editedReport,
							  BindingResult errors,
							  @RequestParam(value = "repeatIntervalUnits", required = false) String repeatIntervalUnits
	) throws Exception {

        HttpSession httpSession = request.getSession();
        QueuedReportService queuedReportService = Context.getService(QueuedReportService.class);
		// determine the repeat interval by units
		repeatIntervalUnits = repeatIntervalUnits.toLowerCase().trim();

		// ignore if the repeat interval is empty / zero
		if (editedReport.getRepeatInterval() == null || editedReport.getRepeatInterval() < 0) {
			editedReport.setRepeatInterval(0);

		} else if (editedReport.getRepeatInterval() > 0) {

			if (OpenmrsUtil.nullSafeEquals(repeatIntervalUnits, "minutes")) {
				editedReport.setRepeatInterval(editedReport.getRepeatInterval() * 60);
			} else if (OpenmrsUtil.nullSafeEquals(repeatIntervalUnits, "hours")) {
				editedReport.setRepeatInterval(editedReport.getRepeatInterval() * 60 * 60);
			} else {
				// assume days
				editedReport.setRepeatInterval(editedReport.getRepeatInterval() * 60 * 60 * 24);
			}
		}

		// save it

		queuedReportService.saveQueuedReport(editedReport);

		// kindly respond

		httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Report queued for processing.");

		return SUCCESS_VIEW;
	}

	@RequestMapping(method = RequestMethod.GET, value = "module/amrsreports/queuedReport.form")
	public String editQueuedReport(
			@RequestParam(value = "queuedReportId", required = false) Integer queuedReportId,
            @RequestParam(value = "status", required = false) String status,
			ModelMap modelMap
                                ) {

		QueuedReport queuedReport = null;
        String inlineInstruction ="";


		if (queuedReportId != null)
			queuedReport = Context.getService(QueuedReportService.class).getQueuedReport(queuedReportId);

		if (queuedReport == null) {
			queuedReport = new QueuedReport();
		}

        if (OpenmrsUtil.nullSafeEquals("ERROR", status)) {
            inlineInstruction = "Check the new scheduled date and submit when finished";
            queuedReport.setDateScheduled(new Date());/*
            queuedReport.setStatus(QueuedReport.STATUS_NEW);*/
        }

		modelMap.put("queuedReports", queuedReport);
        modelMap.put("inlineInstruction",inlineInstruction);

		Integer interval = queuedReport.getRepeatInterval();
		Integer repeatInterval;

		if (interval < 60) {
			modelMap.put("units", "seconds");
			repeatInterval = interval;
		} else if (interval < 3600) {
			modelMap.put("units", "minutes");
			repeatInterval = interval / 60;
		} else if (interval < 86400) {
			modelMap.put("units", "hours");
			repeatInterval = interval / 3600;
		} else {
			modelMap.put("units", "days");
			repeatInterval = interval / 86400;
		}

		modelMap.put("repeatInterval", repeatInterval.toString());

		return FORM_VIEW;
	}

    @RequestMapping(method = RequestMethod.GET, value = "module/amrsreports/removeQueuedReport.form")
    public String removeQueuedReport( HttpServletRequest request,
            @RequestParam(value = "queuedReportId", required = false) Integer queuedReportId,
            ModelMap modelMap
    ) {

        HttpSession httpSession = request.getSession();
        QueuedReportService queuedReportService = Context.getService(QueuedReportService.class);

        QueuedReport queuedReport = null;


        if (queuedReportId != null)
            queuedReport = Context.getService(QueuedReportService.class).getQueuedReport(queuedReportId);



        try {
            queuedReportService.purgeQueuedReport(queuedReport);
            httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "The report was successfully removed");
            return SUCCESS_VIEW;
        }
        catch (DataIntegrityViolationException e) {
            httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "error.object.inuse.cannot.purge");
            return FORM_VIEW;
        }
        catch (APIException e) {
            httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "error.general: " + e.getLocalizedMessage());
            return FORM_VIEW;
        }

    }

	@InitBinder
	private void dateBinder(WebDataBinder binder) {
		// The date format to parse or output your dates
		SimpleDateFormat dateFormat = Context.getDateFormat();

		// Another date format for datetime
		SimpleDateFormat datetimeFormat = new SimpleDateFormat(getDatetimeFormat());

		// Register them as custom editors for the Date type
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
		binder.registerCustomEditor(Date.class, new CustomDateEditor(datetimeFormat, true));
	}

}
