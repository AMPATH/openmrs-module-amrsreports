package org.openmrs.module.amrsreports.util;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.AmrsReportsConstants;
import org.openmrs.module.amrsreports.builder.DrugEventBuilder;
import org.openmrs.module.amrsreports.cache.MohCacheUtils;
import org.openmrs.module.amrsreports.reporting.common.DrugSnapshotDateComparator;
import org.openmrs.module.amrsreports.reporting.common.SortedSetMap;
import org.openmrs.module.amrsreports.rule.MohEvaluableNameConstants;
import org.openmrs.module.amrsreports.service.MohCoreService;
import org.openmrs.module.drughistory.DrugSnapshot;
import org.openmrs.module.drughistory.api.DrugSnapshotService;
import org.openmrs.module.reporting.data.person.service.PersonDataService;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.indicator.IndicatorResult;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.util.OpenmrsUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;


/**
 * Helper utility for running reports and not overloading the system
 */
public class MOHReportUtil {

	private static final Log log = LogFactory.getLog(MOHReportUtil.class);
	public static final String DATE_FORMAT = "dd/MM/yyyy";

	private static Map<Integer, String> tableDrugs;

	static {
		tableDrugs = new HashMap<Integer, String>();
		tableDrugs.put(1, DrugEventBuilder.DRUG_ABACAVIR);
		tableDrugs.put(2, DrugEventBuilder.DRUG_ATAZANAVIR);
		tableDrugs.put(3, DrugEventBuilder.DRUG_DARUNAVIR);
		tableDrugs.put(4, DrugEventBuilder.DRUG_DIDANOSINE);
		tableDrugs.put(5, DrugEventBuilder.DRUG_EFAVIRENZ);
		tableDrugs.put(6, DrugEventBuilder.DRUG_EMTRICITABINE);
		tableDrugs.put(7, DrugEventBuilder.DRUG_ETRAVIRINE);
		tableDrugs.put(8, DrugEventBuilder.DRUG_INDINAVIR);
		tableDrugs.put(9, DrugEventBuilder.DRUG_LAMIVUDINE);
		tableDrugs.put(10, DrugEventBuilder.DRUG_LOPINAVIR);
		tableDrugs.put(11, DrugEventBuilder.DRUG_NELFINAVIR);
		tableDrugs.put(12, DrugEventBuilder.DRUG_NEVIRAPINE);
		tableDrugs.put(13, DrugEventBuilder.DRUG_RALTEGRAVIR);
		tableDrugs.put(14, DrugEventBuilder.DRUG_RITONAVIR);
		tableDrugs.put(15, DrugEventBuilder.DRUG_STAVUDINE);
		tableDrugs.put(16, DrugEventBuilder.DRUG_TENOFOVIR);
		tableDrugs.put(17, DrugEventBuilder.DRUG_ZIDOVUDINE);
		tableDrugs.put(18, DrugEventBuilder.DRUG_OTHER);
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

		CSVWriter w = new CSVWriter(new OutputStreamWriter(out, "UTF-8"), AmrsReportsConstants.DEFAULT_CSV_DELIMITER);

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

	public static String formatdates(Date date) {
		if (date == null)
			return "Unknown";

		Format formatter;
		formatter = new SimpleDateFormat(DATE_FORMAT);
		String s = formatter.format(date);

		return s;

	}

	/**
	 * determine the age group for a patient at a given date
	 *
	 * @param birthdate birth date of the patient whose age is used in the calculations
	 * @param when      the date upon which the age should be identified
	 * @return the appropriate age group
	 * @should determine the age group for a patient at a given date
	 */
	public static MohEvaluableNameConstants.AgeGroup getAgeGroupAtDate(Date birthdate, Date when) {

		if (birthdate == null) {
			return null;
		}

		Calendar now = Calendar.getInstance();
		if (when != null) {
			now.setTime(when);
		}

		Calendar then = Calendar.getInstance();
		then.setTime(birthdate);

		int ageInMonths = 0;
		while (!then.after(now)) {
			then.add(Calendar.MONTH, 1);
			ageInMonths++;
		}
		ageInMonths--;

		if (ageInMonths < 18) {
			return MohEvaluableNameConstants.AgeGroup.UNDER_EIGHTEEN_MONTHS;
		}

		if (ageInMonths < 60) {
			return MohEvaluableNameConstants.AgeGroup.EIGHTEEN_MONTHS_TO_FIVE_YEARS;
		}

		if (ageInMonths < 144) {
			return MohEvaluableNameConstants.AgeGroup.FIVE_YEARS_TO_TWELVE_YEARS;
		}

		return MohEvaluableNameConstants.AgeGroup.ABOVE_TWELVE_YEARS;
	}

	/**
	 * helper method to reduce code for validation methods
	 *
	 * @param concept
	 * @param name
	 * @return
	 */
	public static boolean compareConceptToName(Concept concept, String name) {
		return OpenmrsUtil.nullSafeEquals(concept, MohCacheUtils.getConcept(name));
	}


	public static SortedSetMap<Integer, DrugSnapshot> getARVSnapshotsMap() {
		return getARVSnapshotsMap(null);
	}

	/**
	 * gets ARV-related DrugSnapshots mapped to persons
	 */
	public static SortedSetMap<Integer, DrugSnapshot> getARVSnapshotsMap(Cohort cohort) {

		Properties params = new Properties();
		params.put("drugs", DrugEventBuilder.ARV_DRUGS);
		if (cohort != null) {
			params.put("cohort", cohort);
		}

		List<DrugSnapshot> snapshots = Context.getService(DrugSnapshotService.class).getDrugSnapshots(params);

		SortedSetMap<Integer, DrugSnapshot> m = new SortedSetMap<Integer, DrugSnapshot>();
		m.setSetComparator(new DrugSnapshotDateComparator());

		for (DrugSnapshot snapshot : snapshots) {
			Set<Concept> s = new HashSet<Concept>(snapshot.getConcepts());
			s.retainAll(DrugEventBuilder.ARV_DRUGS);
			if (s.size() >= 3) {
				m.putInList(snapshot.getPerson().getPersonId(), snapshot);
			}
		}
		return m;
	}

	/**
	 * creates ARV-related DrugSnapshots mapped to persons based on drug tables ...
	 */
	public static SortedSetMap<Integer, DrugSnapshot> getARVSnapshotsMapFromTables(Cohort cohort, Date reportDate) {

		String startSQL = "select patient_id, encounter_id, encounter_date, " +
				"   ABACAVIR, " +
				"   ATAZANAVIR, " +
				"   DARUNAVIR, " +
				"   DIDANOSINE, " +
				"   EFAVIRENZ, " +
				"   EMTRICITABINE, " +
				"   ETRAVIRINE, " +
				"   INDINAVIR, " +
				"   LAMIVUDINE, " +
				"   LOPINAVIR, " +
				"   NELFINAVIR, " +
				"   NEVIRAPINE, " +
				"   RALTEGRAVIR, " +
				"   RITONAVIR, " +
				"   STAVUDINE, " +
				"   TENOFOVIR, " +
				"   ZIDOVUDINE, " +
				"   OTHER " +
				" from amrsreports_arv_current" +
				" where on_ART=1" +
				" and patient_id in (:personIds)" +
				" and encounter_date <= :reportDate";

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("personIds", cohort);
		params.put("reportDate", reportDate);

		SortedSetMap<Integer, DrugSnapshot> m = new SortedSetMap<Integer, DrugSnapshot>();
		m.setSetComparator(new DrugSnapshotDateComparator());

		for (Object o : Context.getService(MohCoreService.class).executeSqlQuery(startSQL, params)) {

			// cast the result into an array
			Object[] parts = (Object[]) o;

			// start a new snapshot
			DrugSnapshot ds = new DrugSnapshot();

			// first three columns are person, encounter, and encounter date
			ds.setPerson(new Person((Integer) parts[0]));
			ds.setEncounter(new Encounter((Integer) parts[1]));
			ds.setDateTaken((Date) parts[2]);

			// loop through table drug columns and add concepts as they appear
			for (Integer i : tableDrugs.keySet()) {
				if ((Integer) parts[i+2] == 1) {
					ds.addConcept(MohCacheUtils.getConcept(tableDrugs.get(i)));
				}
			}

			// add the new snapshot to the list of return data
			m.putInList((Integer) parts[0], ds);
		}

		return m;
	}
}
