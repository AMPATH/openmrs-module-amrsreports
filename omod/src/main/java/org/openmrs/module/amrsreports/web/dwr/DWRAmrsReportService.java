package org.openmrs.module.amrsreports.web.dwr;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Patient;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.HIVCareEnrollment;
import org.openmrs.module.amrsreports.MOHFacility;
import org.openmrs.module.amrsreports.reporting.cohort.definition.AMRSReportsCohortDefinition;
import org.openmrs.module.amrsreports.reporting.provider.ReportProvider;
import org.openmrs.module.amrsreports.service.HIVCareEnrollmentService;
import org.openmrs.module.amrsreports.service.MOHFacilityService;
import org.openmrs.module.amrsreports.service.ReportProviderRegistrar;
import org.openmrs.module.amrsreports.task.AMRSReportsTask;
import org.openmrs.module.amrsreports.task.RunQueuedReportsTask;
import org.openmrs.module.amrsreports.task.UpdateHIVCareEnrollmentTask;
import org.openmrs.module.amrsreports.util.TaskRunnerThread;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.scheduler.TaskDefinition;
import org.openmrs.util.OpenmrsUtil;
import org.openmrs.util.PrivilegeConstants;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * DWR service for AMRS Reports web pages
 */
public class DWRAmrsReportService {

	private static final Log log = LogFactory.getLog(DWRAmrsReportService.class);

	public String viewMoreDetails(String file, String id) {

		//open the file and do all the manipulation
		AdministrationService as = Context.getAdministrationService();
		String folderName = as.getGlobalProperty("amrsreport.file_dir");

		File fileDirectory = OpenmrsUtil.getDirectoryInApplicationDataDirectory(folderName);

		StringBuilder strColumnData = new StringBuilder();

		try {
			File amrsFile = new File(fileDirectory, file);
			FileInputStream fstream = new FileInputStream(amrsFile);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			//get the first row to be used as columns
			String columns = br.readLine();
			String[] columnsSplit = columns.split(",");

			String record;
			while ((record = br.readLine()) != null) {

				String[] splitByComma = record.split(",");

				if (stripLeadingAndTrailingQuotes(splitByComma[0]).equals(id)) {
					for (int i = 0; i < columnsSplit.length; i++) {
						String columnName = stripLeadingAndTrailingQuotes(columnsSplit[i]);
						String value = stripLeadingAndTrailingQuotes(splitByComma[i]);

						strColumnData.append(columnName).append("    :").append(value).append(",");
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		//log.info("We are returning now "+recordsAfterAll);
		log.info(strColumnData.toString());
		return strColumnData.toString();
	}

	public String viewMoreDetailsRender(String bff, String id) {
		String line;
		String columns;
		String[] columnsSplitDetails;
		String[] splitByCommaDetails;
		StringBuilder strColumnDataDetails = new StringBuilder();

		//open the file and do all the manipulation
		AdministrationService as = Context.getAdministrationService();
		String folderName = as.getGlobalProperty("amrsreport.file_dir");

		File fileDirectory = OpenmrsUtil.getDirectoryInApplicationDataDirectory(folderName);

		File amrsFile;
		FileInputStream fstream;
		DataInputStream in;
		BufferedReader br;

		//log.info("lets log the buffer here "+bff);

		try {
			amrsFile = new File(fileDirectory, bff);
			fstream = new FileInputStream(amrsFile);
			in = new DataInputStream(fstream);
			br = new BufferedReader(new InputStreamReader(in));
			columns = br.readLine();
			columnsSplitDetails = columns.split(",");
			while ((line = br.readLine()) != null) {

				splitByCommaDetails = line.split(",");

				if (stripLeadingAndTrailingQuotes(splitByCommaDetails[0]).equals(id)) {
					for (int v = 0; v < columnsSplitDetails.length; v++) {
						String columnName = stripLeadingAndTrailingQuotes(columnsSplitDetails[v]);
						String value = stripLeadingAndTrailingQuotes(splitByCommaDetails[v]);

						strColumnDataDetails.append(columnName).append("    :").append(value).append(",");
					}
				}
			}
		} catch (IOException e) {
			log.warn("error viewing more details", e);
		}

		return strColumnDataDetails.toString();
	}

	public void downloadCSV(String csvFile) {
		//open the file and do all the manipulation
		HttpServletResponse httpServletResponse = null;

		AdministrationService as = Context.getAdministrationService();
		String folderName = as.getGlobalProperty("amrsreport.file_dir");
		File fileDirectory = OpenmrsUtil.getDirectoryInApplicationDataDirectory(folderName);
		String urlToDownLoad = fileDirectory + "/" + csvFile;

		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(httpServletResponse.getOutputStream()));
			String mimeType = new MimetypesFileTypeMap().getContentType(urlToDownLoad);

			log.info("We should reach here " + mimeType);


			httpServletResponse.setContentType(mimeType);
			httpServletResponse.setHeader("Content-Disposition", "attachment; filename=" + csvFile);
			//httpServletResponse.setContentType(MimeConstants.MIME_PLAIN);
			//httpServletResponse.setContentLength((int) urlToDownLoad.length());
			bw.write(urlToDownLoad);

			//FileCopyUtils.copy(new FileInputStream(urlToDownLoad), httpServletResponse.getOutputStream());

			bw.flush();
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//return urlToDownLoad;
	}

	public void downloadPDF(String file) {
		AdministrationService as = Context.getAdministrationService();
		String folderName = as.getGlobalProperty("amrsreport.file_dir");
		File fileDirectory = OpenmrsUtil.getDirectoryInApplicationDataDirectory(folderName);
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

	/**
	 * helper method for determining cohort size per location and report date
	 */
	public Integer getCohortCountForFacility(Integer facilityId, Date evaluationDate) throws Exception {
		Set<Integer> cohort = this.getCohort(facilityId, evaluationDate);
		return cohort.size();
	}

	/**
	 * provide the list of patients in the MOH361A cohort for a given location and evaluation date
	 */
	public Set<Integer> getCohort(Integer facilityId, Date evaluationDate) throws Exception {

		EvaluationContext context = new EvaluationContext();
		context.setEvaluationDate(evaluationDate);

		MOHFacility mohFacility = Context.getService(MOHFacilityService.class).getFacility(facilityId);

		if (mohFacility == null)
			return new HashSet<Integer>();

		AMRSReportsCohortDefinition definition = new AMRSReportsCohortDefinition();
		definition.setFacility(mohFacility);

		try {
			Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(definition, context);
			if (cohort != null)
				return cohort.getMemberIds();
		} catch (EvaluationException e) {
			log.error(e);
		}

		log.warn("No cohort found for facility #" + facilityId);
		return new HashSet<Integer>();
	}

	/**
	 * helper method for determining cohort size per location and report date
	 */
	public Map<String, Integer> getCohortCountForFacilityPerProvider(Integer facilityId,
																	 Date evaluationDate) throws Exception {
		Map<String, Integer> cohort = this.getCohortByProviders(facilityId, evaluationDate);

        // sort the map to match criteria for fetching all report providers
		Map<String, Integer> treeMap = new TreeMap<String, Integer>(cohort);
		return treeMap;
	}

	/**
	 * provide the list of patients for a given location and evaluation date
	 */
	public Map<String, Integer> getCohortByProviders(Integer facilityId, Date evaluationDate) throws Exception {

		EvaluationContext context = new EvaluationContext();
		context.setEvaluationDate(evaluationDate);

		MOHFacility mohFacility = Context.getService(MOHFacilityService.class).getFacility(facilityId);

		if (mohFacility == null)
			return new HashMap<String, Integer>();

		Map<String, Integer> cohortResult = new HashMap<String, Integer>();

		// get report providers
		List<ReportProvider> allReportProviders = ReportProviderRegistrar.getInstance().getAllReportProviders();
		for (ReportProvider reportProvider : allReportProviders) {

			AMRSReportsCohortDefinition thisDef = (AMRSReportsCohortDefinition) reportProvider.getCohortDefinition();
			thisDef.setFacility(mohFacility);

			try {
				Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(thisDef, context);
				if (cohort != null) {
					cohortResult.put(reportProvider.getName(), cohort.getMemberIds().size());
				} else {
					cohortResult.put(reportProvider.getName(), 0);
				}
			} catch (EvaluationException e) {
				log.error(e);
			}
		}

		return cohortResult;
	}

	/**
	 * Handles the ajax call for starting a task
	 */
	public String startTaskRunner(String taskName) {

		// quickly give up if the task is already running
		if (TaskRunnerThread.getInstance().isActive())
			return "Task already running: " + TaskRunnerThread.getInstance().getCurrentTaskClassname();

		AMRSReportsTask task = null;

//		if (OpenmrsUtil.nullSafeEquals("arvs", taskName))
//			task = new UpdateARVEncountersTask();
		if (OpenmrsUtil.nullSafeEquals("enrollment", taskName))
			task = new UpdateHIVCareEnrollmentTask();

		if (task == null)
			return null;

		try {
			// create a new thread and get it started
			TaskRunnerThread.getInstance().initialize(task, Context.getUserContext());
			TaskRunnerThread.getInstance().setName("AMRS Reports Task Runner");
			TaskRunnerThread.getInstance().setActive(true);
			TaskRunnerThread.getInstance().start();

			return "Started task: " + TaskRunnerThread.getInstance().getCurrentTaskClassname();
		} catch (APIAuthenticationException e) {
			log.warn("Could not authenticate when trying to run a task.");
		}

		return null;
	}

	/**
	 * Handles the ajax call to stop running a task
	 */
	public String stopTaskRunner() {

		Boolean wasActive = TaskRunnerThread.getInstance().isActive();
		String wasRunning = TaskRunnerThread.getInstance().getCurrentTaskClassname();

		try {
			TaskRunnerThread.destroyInstance();
		} catch (Throwable throwable) {
			log.warn("problem destroying Task Runner Thread instance", throwable);
		}

		if (wasActive) {
			return "Task was supposedly not running, stopped anyway.";
		}

		return "Stopped running task: " + wasRunning;
	}

	/**
	 * Processes the ajax call for retrieving the progress and status
	 */
	public String getTaskRunnerStatus() {
		if (TaskRunnerThread.getInstance().isActive())
			return "Currently running task: " + TaskRunnerThread.getInstance().getCurrentTaskClassname();

		return null;
	}

	/**
	 * Returns a facility's name indicated by its internal id
	 */
	public String getFacilityName(Integer facilityId) {
		MOHFacility f = Context.getService(MOHFacilityService.class).getFacility(facilityId);

		if (f == null)
			return "";

		return f.getName();
	}

	/**
	 * Returns a facility's code indicated by its internal id
	 */
	public String getFacilityCode(Integer facilityId) {
		MOHFacility f = Context.getService(MOHFacilityService.class).getFacility(facilityId);

		if (f == null)
			return "";

		return f.getCode();
	}

	/**
	 * returns the missing ccc numbers count for a given facility
	 */
	public Integer getPatientCountMissingCCCNumbersInFacility(Integer facilityId) {
		MOHFacility f = Context.getService(MOHFacilityService.class).getFacility(facilityId);

		if (f == null)
			return -1;

		return Context.getService(MOHFacilityService.class).countPatientsInFacilityMissingCCCNumbers(f);
	}

	/**
	 * returns the patients missing ccc numbers for a given facility
	 */
	public List<String> getPatientUuidsMissingCCCNumbersInFacility(Integer facilityId) {
		MOHFacility f = Context.getService(MOHFacilityService.class).getFacility(facilityId);

		List<String> c = new ArrayList<String>();

		if (f != null) {
			for (Integer patientId : Context.getService(MOHFacilityService.class)
					.getPatientsInFacilityMissingCCCNumbers(f)) {
				c.add(Context.getPatientService().getPatient(patientId).getUuid());
			}
		}

		return c;
	}


	public String assignMissingIdentifiersForFacility(Integer facilityId) {
		MOHFacility f = Context.getService(MOHFacilityService.class).getFacility(facilityId);

		if (f == null)
			return "No facility specified.";

		Integer count = Context.getService(MOHFacilityService.class).assignMissingIdentifiersForFacility(f);

		return "Successfully created " + count + " identifiers.";
	}

	public String getPreARTEnrollmentLocationUuidForPatientUuid(String patientUuid) {

		if (StringUtils.isBlank(patientUuid))
			return null;

		Patient p = Context.getPatientService().getPatientByUuid(patientUuid);

		if (p == null)
			return null;

		HIVCareEnrollment hce = Context.getService(HIVCareEnrollmentService.class).getHIVCareEnrollmentForPatient(p);

		if (hce == null)
			return null;

		if (hce.getEnrollmentLocation() == null)
			return null;

		return hce.getEnrollmentLocation().getUuid();
	}

	public Boolean isReportRunnerScheduledTaskOn() {
		Context.addProxyPrivilege(PrivilegeConstants.MANAGE_SCHEDULER);

		for (TaskDefinition definition : Context.getSchedulerService().getScheduledTasks()) {
			if (OpenmrsUtil.nullSafeEquals(RunQueuedReportsTask.class.getCanonicalName(), definition.getTaskClass())
					&& definition.getStarted()) {
				Context.removeProxyPrivilege(PrivilegeConstants.MANAGE_SCHEDULER);
				return true;
			}
		}

		Context.removeProxyPrivilege(PrivilegeConstants.MANAGE_SCHEDULER);
		return false;
	}
}
