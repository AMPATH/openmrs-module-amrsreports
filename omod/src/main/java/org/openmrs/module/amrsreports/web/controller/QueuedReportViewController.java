package org.openmrs.module.amrsreports.web.controller;

import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.QueuedReport;
import org.openmrs.module.amrsreports.service.QueuedReportService;
import org.openmrs.module.amrsreports.util.MOHReportUtil;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

/**
 * Controller for viewing queued reports ... at least compeleted ones.
 */
@Controller
public class QueuedReportViewController {

	@RequestMapping(method = RequestMethod.GET, value = "module/amrsreports/viewReport.form")
	public void processForm(ModelMap map, @RequestParam(required = true, value = "reportId") Integer reportId) {

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

		///end of interface population after submitting
		try {
			File amrsFile = new File(fileDir, report.getCsvFilename());
			FileInputStream fstream = new FileInputStream(amrsFile);
			Map<String, Object> csv = MOHReportUtil.renderDataSetFromCSV(fstream);
			fstream.close();

			map.addAttribute("columnHeaders", csv.get("columnHeaders"));
			map.addAttribute("records", csv.get("records"));
			map.addAttribute("report", report);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
