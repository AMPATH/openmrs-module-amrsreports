package org.openmrs.module.amrsreports.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.MOHFacility;
import org.openmrs.module.amrsreports.QueuedReport;
import org.openmrs.module.amrsreports.service.QueuedReportService;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * controller for View AMRS Reports page
 */
@Controller
public class QueuedReportListController {

	private static final Log log = LogFactory.getLog(QueuedReportListController.class);

	@ModelAttribute("queuedReports")
	public Map<MOHFacility,List<QueuedReport>> getQueuedReports() {
		return getFacilityReportMap(QueuedReport.STATUS_NEW);
	}

	@ModelAttribute("runningReports")
	public Map<MOHFacility,List<QueuedReport>> getRunningReport() {
		return getFacilityReportMap(QueuedReport.STATUS_RUNNING);
	}

	@ModelAttribute("errorReports")
	public Map<MOHFacility,List<QueuedReport>> getErrorReport() {
		return getFacilityReportMap(QueuedReport.STATUS_ERROR);
	}

	@ModelAttribute("completeReports")
	public Map<MOHFacility,List<QueuedReport>> getCompleteReport() {
		return getFacilityReportMap(QueuedReport.STATUS_COMPLETE);
	}

	@ModelAttribute("datetimeFormat")
	public String getDatetimeFormat() {
		SimpleDateFormat sdf = Context.getDateFormat();
		String format = sdf.toPattern();
		format += " hh:mm a";
		return format;
	}

	@RequestMapping(method = RequestMethod.GET, value = "module/amrsreports/queuedReport.list")
	public String preparePage() {
		return "module/amrsreports/queuedReportList";
	}

	@RequestMapping(value = "/module/amrsreports/downloadxls")
	public void downloadXLS(HttpServletResponse response,
	                        @RequestParam(required = true, value = "reportId") Integer reportId) throws IOException {

		if (reportId == null) {
			// TODO say something ...
			return;
		}

		QueuedReport report = Context.getService(QueuedReportService.class).getQueuedReport(reportId);

		if (report == null) {
			// TODO say something ...
			return;
		}

		String folderName = Context.getAdministrationService().getGlobalProperty("amrsreports.file_dir");

		File fileDir = OpenmrsUtil.getDirectoryInApplicationDataDirectory(folderName);
		File amrsFileToDownload = new File(fileDir, report.getXlsFilename());

		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "attachment; filename=" + report.getXlsFilename());
		response.setContentLength((int) amrsFileToDownload.length());

		FileCopyUtils.copy(new FileInputStream(amrsFileToDownload), response.getOutputStream());
	}

    public Map<MOHFacility,List<QueuedReport>> getFacilityReportMap(String status){


        Map<MOHFacility,List<QueuedReport>> finalMap = new HashMap<MOHFacility, List<QueuedReport>>();
        List<QueuedReport> completeReports = Context.getService(QueuedReportService.class).getQueuedReportsWithStatus(status);

        Set<MOHFacility> requiredFacilities = new HashSet<MOHFacility>();

        for(QueuedReport thisReport:completeReports){
            MOHFacility thisMohFacility = thisReport.getFacility();

            if (!finalMap.containsKey(thisMohFacility))
               finalMap.put(thisMohFacility,new ArrayList<QueuedReport>());

            finalMap.get(thisMohFacility).add(thisReport);

        }

        return finalMap;

    }


}
