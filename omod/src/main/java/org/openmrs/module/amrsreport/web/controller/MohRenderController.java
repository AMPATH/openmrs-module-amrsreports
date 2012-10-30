package org.openmrs.module.amrsreport.web.controller;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Location;
import org.openmrs.User;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreport.render.AmrReportRender;
import org.openmrs.module.amrsreport.service.MohCoreService;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * controller for Run AMRS Reports page
 */
@Controller
public class MohRenderController {

	private static final Log log = LogFactory.getLog(MohRenderController.class);
	List<String> cohortdfnList = null;

	@RequestMapping(method = RequestMethod.GET, value = "module/amrsreport/mohRender.form")
	public void preparePage(ModelMap map, @RequestParam(required = false, value = "includeRetired") Boolean includeRetired) {

		CohortDefinitionService cohortDefinitionService = Context.getService(CohortDefinitionService.class);
        MohCoreService userlocservice=Context.getService(MohCoreService.class);
		// Get list of existing reports
		boolean includeRet = (includeRetired == Boolean.TRUE);
		List<ReportDefinition> reportDefinitions = Context.getService(ReportDefinitionService.class).getAllDefinitions(includeRet);

		map.addAttribute("reportDefinitions", reportDefinitions);

		List<CohortDefinition> listOfCohorts = cohortDefinitionService.getAllDefinitions(false);

		//get a list of all the locations
        User currUser = Context.getAuthenticatedUser();
		List<Location> locationList =  userlocservice.getAllowedLocationsForUser(currUser);

		//log.info("all cohorts listed "+listOfCohorts.size()+"All definition "+reportDefinitions.size());
		map.addAttribute("cohortdefinitions", listOfCohorts);
		map.addAttribute("location", locationList);
	}

	@RequestMapping(method = RequestMethod.POST, value = "module/amrsreport/mohRender.form")
	public void processForm(ModelMap map, HttpServletRequest request,
		@RequestParam(required = false, value = "definition") String definitionuuid,
		@RequestParam(required = true, value = "cohortdef") String cohortdefuuid,
		@RequestParam(required = true, value = "location") Integer location) {

		HttpSession httpSession = request.getSession();
		Integer httpSessionvalue = httpSession.getMaxInactiveInterval();
		httpSession.setMaxInactiveInterval(-1);

		log.info("The sesion value is " + httpSessionvalue);

		Location loc = Context.getLocationService().getLocation(location);
		ReportDefinition reportDefinition = Context.getService(ReportDefinitionService.class).getDefinitionByUuid(definitionuuid);
		CohortDefinition cohortDefinition = Context.getService(CohortDefinitionService.class).getDefinitionByUuid(cohortdefuuid);

		//here to allow the reloading of the screen
		List<CohortDefinition> listOfCohorts = Context.getService(CohortDefinitionService.class).getAllDefinitions(false);
		List<ReportDefinition> reportDefinitions = Context.getService(ReportDefinitionService.class).getAllDefinitions(true);
		List<Location> locationList = Context.getLocationService().getAllLocations();

		//create a flat file here for storing our report data
		map.addAttribute("reportDefinitions", reportDefinitions);
		map.addAttribute("cohortdefinitions", listOfCohorts);
		map.addAttribute("location", locationList);

		try {
			EvaluationContext evaluationContext = new EvaluationContext();

			//add loctation to be displayed here
			evaluationContext.addParameterValue("locationList", Arrays.asList(loc));
			CohortDefinitionService cohortDefinitionService = Context.getService(CohortDefinitionService.class);

			//evaluation
			Cohort cohort = cohortDefinitionService.evaluate(cohortDefinition, evaluationContext);

			evaluationContext.setBaseCohort(cohort);

			Date d = Calendar.getInstance().getTime();
			String TIME;

			Format formatter = new SimpleDateFormat("yyyy:MM:dd hh:mm:ss a");

			TIME = formatter.format(d);

			AdministrationService as = Context.getAdministrationService();
			String folderName = as.getGlobalProperty("amrsreport.file_dir");

			File loaddir = OpenmrsUtil.getDirectoryInApplicationDataDirectory(folderName);

			ReportDefinitionService reportDefinitionService = Context.getService(ReportDefinitionService.class);

			ReportData reportData = reportDefinitionService.evaluate(reportDefinition, evaluationContext);

			AmrReportRender amrReportRender = new AmrReportRender();
			String fileURL = loc.getName() + "-" + TIME + "-MOH-Register-361A.csv";
			File amrsreport = new File(loaddir, fileURL);
			log.info("This is the file " + fileURL);
			BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(amrsreport));

			amrReportRender.render(reportData, "Report information ", outputStream);

			//normal file operations to follow here

			BufferedReader input = new BufferedReader(new FileReader(amrsreport));

			map.addAttribute("fileToManipulate", fileURL);

			//add the splitted one per the credentials
			String[] splitFileLocTime = fileURL.split("-");
			String loci = splitFileLocTime[0];
			String time = splitFileLocTime[1];

			map.addAttribute("loci", loci);
			map.addAttribute("time", time);

			String line = "";
			List<List<String>> records = new ArrayList<List<String>>();
			List<String> columnHeaders = new ArrayList<String>();
			String[] linedata = null;

			String lineColumn = input.readLine();
			String[] lineColumnArray = lineColumn.split(",");

			//add the columns on the jsp
			for (int p = 0; p < lineColumnArray.length; p++) {
				columnHeaders.add(StringUtils.defaultString(stripLeadingAndTrailingQuotes(lineColumnArray[p])));
			}

			map.addAttribute("columnHeaders", columnHeaders);

			while ((line = input.readLine()) != null) {
				List<String> intlist = new ArrayList<String>();
				linedata = line.split(",");// values of every row in an array

				/////////////////////////////////////////////////////////////////////////////////////////
				for (int pp = 0; pp < linedata.length; pp++) {
					intlist.add(StringUtils.defaultString(stripLeadingAndTrailingQuotes(linedata[pp])));
				}
				records.add(intlist);
			}
			//records.remove(0);

			map.addAttribute("records", records);
			input.close();
			outputStream.close();

		} catch (EvaluationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		} catch (ClassCastException ex) {
			ex.printStackTrace();
		} finally {
			httpSession.setMaxInactiveInterval(httpSessionvalue);
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

	@RequestMapping(value = "/module/amrsreport/downloadcsvR")
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
}
