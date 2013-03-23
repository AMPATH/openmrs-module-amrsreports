package org.openmrs.module.amrsreports.web.controller;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.QueuedReport;
import org.openmrs.module.amrsreports.service.QueuedReportService;
import org.openmrs.module.amrsreports.util.MOHReportUtil;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * controller for View AMRS Reports page
 */
@Controller
public class MohHistoryController {

	private static final Log log = LogFactory.getLog(MohHistoryController.class);

	@ModelAttribute("queuedReports")
	public List<QueuedReport> getQueuedReports() {
		return Context.getService(QueuedReportService.class).getQueuedReportsWithStatus(QueuedReport.STATUS_NEW);
	}

	@ModelAttribute("currentReport")
	public List<QueuedReport> getCurrentReport() {
		return Context.getService(QueuedReportService.class).getQueuedReportsWithStatus(QueuedReport.STATUS_RUNNING);
	}

	@RequestMapping(method = RequestMethod.GET, value = "module/amrsreports/mohHistory.form")
	public void preparePage(ModelMap map) {

		AdministrationService as = Context.getAdministrationService();
		String folderName = as.getGlobalProperty("amrsreports.file_dir");

		File fileDir = OpenmrsUtil.getDirectoryInApplicationDataDirectory(folderName);

		String[] children = fileDir.list(new WildcardFileFilter("*csv"));

		List<String> filenames = children != null ? Arrays.asList(children) : new ArrayList<String>();
		Collections.sort(filenames);
		map.addAttribute("reportHistory", filenames);
	}

	@RequestMapping(method = RequestMethod.POST, value = "module/amrsreports/mohHistory.form")
	public void processForm(ModelMap map, @RequestParam(required = true, value = "history") String history) {

		map.addAttribute("historyToCSV", history);

		////////to be used for populating the interface
		List<String> filenames = new ArrayList<String>();

		AdministrationService as = Context.getAdministrationService();
		String folderName = as.getGlobalProperty("amrsreports.file_dir");

		File fileDir = OpenmrsUtil.getDirectoryInApplicationDataDirectory(folderName);
		String[] children = fileDir.list(new WildcardFileFilter("*csv"));
		if (children != null) {
			filenames = Arrays.asList(children);
		}

		Collections.sort(filenames);
		map.addAttribute("reportHistory", filenames);

		///end of interface population after submitting
		try {
			File amrsFile = new File(fileDir, history);
			FileInputStream fstream = new FileInputStream(amrsFile);
			Map<String, Object> csv = MOHReportUtil.renderDataSetFromCSV(fstream);
			fstream.close();

			map.addAttribute("columnHeaders", csv.get("columnHeaders"));
			map.addAttribute("records", csv.get("records"));

			map.addAttribute("historyURL", history);
			map.addAttribute("filetodownload", amrsFile);

			//add the splitted one per the credentials
			String[] splitFileLocTime = history.split("_");
			String loci = splitFileLocTime[0];
			String time = splitFileLocTime[1];

			map.addAttribute("loci", loci);
			map.addAttribute("time", time);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/module/amrsreports/downloadcsv")
	public void downloadCSV(HttpServletResponse response,
		@RequestParam(required = true, value = "fileToImportToCSV") String fileToImportToCSV) throws IOException {

		AdministrationService as = Context.getAdministrationService();
		String folderName = as.getGlobalProperty("amrsreports.file_dir");

		File fileDir = OpenmrsUtil.getDirectoryInApplicationDataDirectory(folderName);
		String filename = FilenameUtils.getBaseName(fileToImportToCSV) + ".xls";
		File amrsFileToDownload = new File(fileDir, filename);

		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "attachment; filename=" + filename);
		response.setContentLength((int) amrsFileToDownload.length());

		FileCopyUtils.copy(new FileInputStream(amrsFileToDownload), response.getOutputStream());
	}

	@RequestMapping(value = "/module/amrsreports/downloadpdf")
	public void downloadPDF(HttpServletResponse response,
		@RequestParam(required = true, value = "fileToImportToCSV") String fileToImportToCSV) throws IOException {
		AdministrationService as = Context.getAdministrationService();
		String folderName = as.getGlobalProperty("amrsreports.file_dir");

		File fileDir = OpenmrsUtil.getDirectoryInApplicationDataDirectory(folderName);
		File amrsFileToDownload = new File(fileDir, fileToImportToCSV);

		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "attachment; filename=" + amrsFileToDownload);
		response.setContentLength((int) amrsFileToDownload.length());
		FileCopyUtils.copy(new FileInputStream(amrsFileToDownload), response.getOutputStream());
	}
}
