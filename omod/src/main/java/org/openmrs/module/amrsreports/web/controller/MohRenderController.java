package org.openmrs.module.amrsreports.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Location;
import org.openmrs.User;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.reporting.MOH361AReport;
import org.openmrs.module.amrsreports.reporting.cohort.definition.Moh361ACohortDefinition;
import org.openmrs.module.amrsreports.service.MohCoreService;
import org.openmrs.module.amrsreports.util.MOHReportUtil;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.indicator.IndicatorResult;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * controller for Run AMRS Reports page
 */
@Controller
public class MohRenderController {

	private static final Log log = LogFactory.getLog(MohRenderController.class);

	@ModelAttribute("reportDates")
	public List<Date> getReportDates() {
		return Context.getService(MohCoreService.class).getAllEnrollmentReportDates();
	}

	@ModelAttribute("locations")
	public List<Location> getLocations() {
		MohCoreService mohCoreService = Context.getService(MohCoreService.class);
		User currUser = Context.getAuthenticatedUser();
		return mohCoreService.getAllowedLocationsForUser(currUser);
	}

	@RequestMapping(method = RequestMethod.GET, value = "module/amrsreports/mohRender.form")
	public void preparePage() {
		// pass
	}

	@RequestMapping(method = RequestMethod.POST, value = "module/amrsreports/mohRender.form")
	public void processForm(ModelMap map, HttpServletRequest request,
	                        @RequestParam("reportDate") Date reportDate,
	                        @RequestParam("location") Integer location,
	                        @RequestParam("hardcoded") String hardcoded) throws Exception {

		// set a keep-alive timeout so we don't lose connectivity
		HttpSession httpSession = request.getSession();
		Integer httpSessionvalue = httpSession.getMaxInactiveInterval();
		httpSession.setMaxInactiveInterval(-1);

		// find the specified values
		Location loc = Context.getLocationService().getLocation(location);
		ReportDefinition reportDefinition = MOH361AReport.getReportDefinition();
		CohortDefinition cohortDefinition = new Moh361ACohortDefinition();

		// TODO put something in here to deal with hardcoded pick list

		// try rendering the report
		try {
			EvaluationContext evaluationContext = new EvaluationContext();

			// set up evaluation context values
			evaluationContext.addParameterValue("locationList", Arrays.asList(loc));
			evaluationContext.setEvaluationDate(reportDate);

			// get the cohort
			CohortDefinitionService cohortDefinitionService = Context.getService(CohortDefinitionService.class);
			Cohort cohort = cohortDefinitionService.evaluate(cohortDefinition, evaluationContext);
			evaluationContext.setBaseCohort(cohort);

			// get the time the report was started (not finished)
			Date startTime = Calendar.getInstance().getTime();
			String formattedStartTime = new SimpleDateFormat("yyyy:MM:dd hh:mm:ss a").format(startTime);

			ReportDefinitionService reportDefinitionService = Context.getService(ReportDefinitionService.class);
			ReportData reportData = reportDefinitionService.evaluate(reportDefinition, evaluationContext);

			// find the directory to put the file in
			AdministrationService as = Context.getAdministrationService();
			String folderName = as.getGlobalProperty("amrsreports.file_dir");

			// create a new file
			String fileURL = loc.getName() + "-" + formattedStartTime + "-MOH-Register-361A.csv";
			File loaddir = OpenmrsUtil.getDirectoryInApplicationDataDirectory(folderName);
			File amrsreport = new File(loaddir, fileURL);
			BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(amrsreport));

			// renderCSVFromReportData the CSV
			MOHReportUtil.renderCSVFromReportData(reportData, outputStream);
			outputStream.close();

			// ------------ post-save ---------------

			map.addAttribute("fileToManipulate", fileURL);
			map.addAttribute("loci", loc.getName());
			map.addAttribute("time", formattedStartTime);

			DataSet dataset = MOHReportUtil.getFirstDataSetForReportData(reportData);
			List<DataSetColumn> columns = dataset.getMetaData().getColumns();
			List<List<String>> records = new ArrayList<List<String>>();

			for (DataSetRow row : dataset) {
				List<String> thisRow = new ArrayList<String>();
				for (DataSetColumn column : columns) {
					Object colValue = row.getColumnValue(column);
					if (colValue != null) {
						if (colValue instanceof Cohort) {
							thisRow.add(Integer.toString(((Cohort) colValue).size()));
						} else if (colValue instanceof IndicatorResult) {
							thisRow.add(((IndicatorResult) colValue).getValue().toString());
						} else {
							thisRow.add(colValue.toString());
						}
					}
				}
				records.add(thisRow);
			}

			map.addAttribute("columnHeaders", columns);
			map.addAttribute("records", records);

		} catch (Exception e) {
			log.error(e);
		} finally {
			httpSession.setMaxInactiveInterval(httpSessionvalue);
		}
	}

	@RequestMapping(value = "/module/amrsreports/downloadcsvR")
	public void downloadCSV(HttpServletResponse response,
	                        @RequestParam(required = true, value = "fileToImportToCSV") String fileToImportToCSV) throws IOException {

		AdministrationService as = Context.getAdministrationService();
		String folderName = as.getGlobalProperty("amrsreports.file_dir");

		File fileDir = OpenmrsUtil.getDirectoryInApplicationDataDirectory(folderName);
		File amrsFileToDownload = new File(fileDir, fileToImportToCSV);

		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "attachment; filename=" + amrsFileToDownload);
		response.setContentLength((int) amrsFileToDownload.length());

		FileCopyUtils.copy(new FileInputStream(amrsFileToDownload), response.getOutputStream());
	}
}
