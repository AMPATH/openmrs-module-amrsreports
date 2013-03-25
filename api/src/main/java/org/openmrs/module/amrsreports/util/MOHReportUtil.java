package org.openmrs.module.amrsreports.util;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.AmrsReportsConstants;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.indicator.IndicatorResult;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.RenderingException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Helper utility for running reports and not overloading the system
 */
public class MOHReportUtil {

	private static final int MAX_RECORDS = 10;
	private static final Log log = LogFactory.getLog(MOHReportUtil.class);

	public static ReportData evaluate(ReportDefinition reportDefinition, EvaluationContext evaluationContext) throws EvaluationException {
		ReportDefinitionService reportDefinitionService = Context.getService(ReportDefinitionService.class);

		Map<String, List<DataSet>> dataSets = new HashMap<String, List<DataSet>>();
		Cohort cohort = evaluationContext.getBaseCohort();
		Set<Integer> memberIds = cohort.getMemberIds();
		Integer[] members = memberIds.toArray(new Integer[]{});

		while (members.length > 0) {

			int size = Math.min(MAX_RECORDS, members.length);
			Integer[] subset = Arrays.copyOfRange(members, 0, size);

			if (size == members.length) {
				members = new Integer[]{};
			} else {
				members = Arrays.copyOfRange(members, size, members.length);
			}

			Cohort subCohort = new Cohort(Arrays.asList(subset));
			evaluationContext.setBaseCohort(subCohort);
			ReportData tempReportData = reportDefinitionService.evaluate(reportDefinition, evaluationContext);

			Map<String, DataSet> tmpDataSets = tempReportData.getDataSets();
			for (String key: tmpDataSets.keySet()) {
				if (!dataSets.containsKey(key))
					dataSets.put(key, new ArrayList<DataSet>());
				dataSets.get(key).add(tmpDataSets.get(key));
			}
		}

		// at this point, dataSets has a map of lists of datasets
		Map<String, DataSet> finalDataSets = new HashMap<String, DataSet>();

		ReportData reportData = new ReportData();
		reportData.setDefinition(reportDefinition);
		reportData.setContext(evaluationContext);
		// reportData.setDataSets(dataSets);
		return reportData;
	}

	public static String joinAsSingleCell(Collection<String> entries) {
		return StringUtils.join(entries, AmrsReportsConstants.INTER_CELL_SEPARATOR);
	}

	public static String joinAsSingleCell(String... entries) {
		return joinAsSingleCell(Arrays.asList(entries));
	}

	public static DataSet getFirstDataSetForReportData(ReportData reportData) {
		return reportData.getDataSets().values().iterator().next();
	}

	public static List<String> getColumnsFromReportData(ReportData results) {
		DataSet dataset = getFirstDataSetForReportData(results);
		List<DataSetColumn> columns = dataset.getMetaData().getColumns();

		List<String> columnNames = new ArrayList<String>();
		for (DataSetColumn column : columns) {
			columnNames.add(column.getLabel());
		}

		return columnNames;
	}

	/**
	 * builds a CSV from a {@link ReportData} object, writing to the reference OutputStream
	 */
	public static void renderCSVFromReportData(ReportData results, OutputStream out) throws IOException {

		CSVWriter w = new CSVWriter(new OutputStreamWriter(out,"UTF-8"), AmrsReportsConstants.DEFAULT_CSV_DELIMITER);

		DataSet dataset = getFirstDataSetForReportData(results);

		List<DataSetColumn> columns = dataset.getMetaData().getColumns();

		String[] outRow = new String[columns.size()];

		// header row
		int i = 0;
		for (DataSetColumn column : columns) {
			outRow[i++] = column.getLabel();
		}
		w.writeNext(outRow);

		// data rows
		for (DataSetRow row : dataset) {
			i = 0;
			for (DataSetColumn column : columns) {
				Object colValue = row.getColumnValue(column);
				if (colValue != null) {
					if (colValue instanceof Cohort) {
						outRow[i] = Integer.toString(((Cohort) colValue).size());
					} else if (colValue instanceof IndicatorResult) {
						outRow[i] = ((IndicatorResult) colValue).getValue().toString();
					} else {
						outRow[i] = colValue.toString();
					}
				} else {
					outRow[i] = "";
				}
				i++;
			}
			w.writeNext(outRow);
		}

		w.flush();
	}

	/**
	 * renders a map containing column headers and records, defaulting to empty array lists, from an input stream
	 */
	public static Map<String, Object> renderDataSetFromCSV(InputStream in) throws IOException {
		CSVReader r = new CSVReader(new InputStreamReader(in, "UTF-8"), AmrsReportsConstants.DEFAULT_CSV_DELIMITER);
		List<String[]> contents = r.readAll();

		Map<String, Object> results = new HashMap<String, Object>();
		results.put("columnHeaders", contents != null && contents.size() > 0 ? contents.remove(0) : new ArrayList());
		results.put("records", contents != null ? contents : new ArrayList());
		return results;
	}

}
