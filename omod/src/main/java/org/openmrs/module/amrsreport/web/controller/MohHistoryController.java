package org.openmrs.module.amrsreport.web.controller;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * controller for View AMRS Reports page
 */
@Controller
public class MohHistoryController {

	private static final Log log = LogFactory.getLog(MohHistoryController.class);

	@RequestMapping(method = RequestMethod.GET, value = "module/amrsreport/mohHistory.form")
	public void preparePage(ModelMap map) {

		AdministrationService as = Context.getAdministrationService();
		String folderName = as.getGlobalProperty("amrsreport.file_dir");

		File fileDir = OpenmrsUtil.getDirectoryInApplicationDataDirectory(folderName);

		String[] children = fileDir.list();

		List<String> filenames = children != null ? Arrays.asList(children) : new ArrayList<String>();
		Collections.sort(filenames);
		map.addAttribute("reportHistory", filenames);
	}

	@RequestMapping(method = RequestMethod.POST, value = "module/amrsreport/mohHistory.form")
	public void processForm(ModelMap map, @RequestParam(required = true, value = "history") String history) {

		map.addAttribute("historyToCSV", history);

		////////to be used for populating the interface
		List<String> filenames = new ArrayList<String>();

		AdministrationService as = Context.getAdministrationService();
		String folderName = as.getGlobalProperty("amrsreport.file_dir");

		File fileDir = OpenmrsUtil.getDirectoryInApplicationDataDirectory(folderName);
		String[] children = fileDir.list();
		if (children != null) {
			filenames = Arrays.asList(children);
		}

		Collections.sort(filenames);
		map.addAttribute("reportHistory", filenames);

		///end of interface population after submitting
		List<List<String>> records = new ArrayList<List<String>>();
		List<String> columnHeaders = new ArrayList<String>();
		String[] linedata;
		try {
			File amrsFile = new File(fileDir, history);
			FileInputStream fstream = new FileInputStream(amrsFile);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line;

			//get the columns to be used here
			String lineColumn = br.readLine();
			String[] lineColumnArray = lineColumn.split(",");

			log.info("This the file to be displayed " + amrsFile);

			for (int p = 0; p < lineColumnArray.length; p++) {
				columnHeaders.add(StringUtils.defaultString(stripLeadingAndTrailingQuotes(lineColumnArray[p])));
			}

			map.addAttribute("columnHeaders", columnHeaders);

			while ((line = br.readLine()) != null) {
				List<String> intlist = new ArrayList<String>();
				linedata = line.split(",");// values of every row in an array

				/////////////////////////////////////////////////////////////////////////////////////////
				for (int pp = 0; pp < linedata.length; pp++) {
					intlist.add(StringUtils.defaultString(stripLeadingAndTrailingQuotes(linedata[pp])));
				}
				records.add(intlist);
			}
			//process for downloading the csv

			map.addAttribute("records", records);
			fstream.close();
			in.close();
			br.close();
			// bw.flush();
			//bw.close();
			map.addAttribute("historyURL", history);
			map.addAttribute("filetodownload", amrsFile);

			//add the splitted one per the credentials
			String[] splitFileLocTime = history.split("-");
			String loci = splitFileLocTime[0];
			String time = splitFileLocTime[1];

			map.addAttribute("loci", loci);
			map.addAttribute("time", time);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static String stripLeadingAndTrailingQuotes(String str) {
		if (str.startsWith("\"")) {
			str = str.substring(1, str.length());
		}
		if (str.endsWith("\"")) {
			str = str.substring(0, str.length() - 1);
		}
		return str;
	}

	@RequestMapping(value = "/module/amrsreport/downloadcsv")
	public void downloadCSV(HttpServletResponse response,
		@RequestParam(required = true, value = "fileToImportToCSV") String fileToImportToCSV) throws IOException {

		AdministrationService as = Context.getAdministrationService();
		String folderName = as.getGlobalProperty("amrsreport.file_dir");

		File fileDir = OpenmrsUtil.getDirectoryInApplicationDataDirectory(folderName);
		File amrsFileToDownload = new File(fileDir, fileToImportToCSV);

		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "attachment; filename=" + amrsFileToDownload);
		response.setContentLength((int) amrsFileToDownload.length());

		FileCopyUtils.copy(new FileInputStream(amrsFileToDownload), response.getOutputStream());
	}

	@RequestMapping(value = "/module/amrsreport/downloadpdf")
	public void downloadPDF(HttpServletResponse response,
		@RequestParam(required = true, value = "fileToImportToCSV") String fileToImportToCSV) throws IOException {
		AdministrationService as = Context.getAdministrationService();
		String folderName = as.getGlobalProperty("amrsreport.file_dir");

		File fileDir = OpenmrsUtil.getDirectoryInApplicationDataDirectory(folderName);
		File amrsFileToDownload = new File(fileDir, fileToImportToCSV);

		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "attachment; filename=" + amrsFileToDownload);
		response.setContentLength((int) amrsFileToDownload.length());
		FileCopyUtils.copy(new FileInputStream(amrsFileToDownload), response.getOutputStream());
	}
}
