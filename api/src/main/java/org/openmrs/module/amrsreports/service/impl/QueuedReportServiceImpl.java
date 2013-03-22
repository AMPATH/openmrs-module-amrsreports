package org.openmrs.module.amrsreports.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Location;
import org.openmrs.api.APIException;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.QueuedReport;
import org.openmrs.module.amrsreports.db.QueuedReportDAO;
import org.openmrs.module.amrsreports.reporting.provider.ReportProvider;
import org.openmrs.module.amrsreports.service.QueuedReportService;
import org.openmrs.module.amrsreports.service.ReportProviderRegistrar;
import org.openmrs.module.amrsreports.util.MOHReportUtil;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.util.OpenmrsUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Implementation of {@link QueuedReportService}
 */
public class QueuedReportServiceImpl implements QueuedReportService {

	private QueuedReportDAO dao;
	private final Log log = LogFactory.getLog(this.getClass());

	public void setDao(QueuedReportDAO dao) {
		this.dao = dao;
	}

	@Override
	public QueuedReport getNextQueuedReport() {
		return dao.getNextQueuedReport(new Date());
	}

	@Override
	public void processQueuedReport(QueuedReport queuedReport) throws EvaluationException, IOException {

		// validate
		if (queuedReport.getReportName() == null)
			throw new APIException("The queued report must reference a report provider by name.");

		if (queuedReport.getFacility() == null)
			throw new APIException("The queued report must reference a facility.");

		// find the report provider
		ReportProvider reportProvider = ReportProviderRegistrar.getInstance().getReportProviderByName(queuedReport.getReportName());
		ReportDefinition reportDefinition = reportProvider.getReportDefinition();
		CohortDefinition cohortDefinition = reportProvider.getCohortDefinition();

		// try rendering the report
		EvaluationContext evaluationContext = new EvaluationContext();

		// set up evaluation context values
		List<Location> locations = new ArrayList<Location>();
		locations.addAll(queuedReport.getFacility().getLocations());
		evaluationContext.addParameterValue("locationList", locations);
		evaluationContext.addParameterValue("facility", queuedReport.getFacility());
		evaluationContext.setEvaluationDate(queuedReport.getEvaluationDate());

		// get the cohort
		CohortDefinitionService cohortDefinitionService = Context.getService(CohortDefinitionService.class);
		Cohort cohort = cohortDefinitionService.evaluate(cohortDefinition, evaluationContext);
		evaluationContext.setBaseCohort(cohort);

		// get the time the report was started (not finished)
		Date startTime = Calendar.getInstance().getTime();
		String formattedStartTime = new SimpleDateFormat("yyyy-MM-dd").format(startTime);
		String formattedEvaluationDate = new SimpleDateFormat("yyyy-MM-dd").format(queuedReport.getEvaluationDate());
		ReportData reportData = Context.getService(ReportDefinitionService.class)
		        .evaluate(reportDefinition, evaluationContext);

		// find the directory to put the file in
		AdministrationService as = Context.getAdministrationService();
		String folderName = as.getGlobalProperty("amrsreports.file_dir");

		// create a new file
		String code = queuedReport.getFacility().getCode();

		String fileURL = ""
				+ queuedReport.getReportName().replaceAll(" ", "-")
				+ "_"
				+ code
				+ "_"
				+ queuedReport.getFacility().getName().replaceAll(" ", "-")
				+ "_as-of_"
				+ formattedEvaluationDate
				+ "_run-on_"
				+ formattedStartTime
				+ ".csv";

		File loaddir = OpenmrsUtil.getDirectoryInApplicationDataDirectory(folderName);
		File amrsreport = new File(loaddir, fileURL);
		BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(amrsreport));

		// renderCSVFromReportData the CSV
		MOHReportUtil.renderCSVFromReportData(reportData, outputStream);
		outputStream.close();

		Context.getService(QueuedReportService.class).purgeQueuedReport(queuedReport);
	}

	@Override
	public QueuedReport saveQueuedReport(QueuedReport queuedReport) {
		if (queuedReport == null)
			return queuedReport;

		if (queuedReport.getStatus() == null)
			queuedReport.setStatus(QueuedReport.STATUS_NEW);

		return dao.saveQueuedReport(queuedReport);
	}

	@Override
	public void purgeQueuedReport(QueuedReport queuedReport) {
		dao.purgeQueuedReport(queuedReport);
	}

	@Override
	public List<QueuedReport> getAllQueuedReports() {
		return dao.getAllQueuedReports();
	}

	@Override
	public List<QueuedReport> getQueuedReportsWithStatus(String status) {
		return dao.getQueuedReportsWithStatus(status);
	}
}