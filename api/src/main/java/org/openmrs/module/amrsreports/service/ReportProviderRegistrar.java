package org.openmrs.module.amrsreports.service;

import org.openmrs.module.amrsreports.reporting.provider.ReportProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Registrar for handling ReportProvider instances
 */
public class ReportProviderRegistrar {

	private static ReportProviderRegistrar instance;
	private static Map<String, ReportProvider> providerMap;

	public ReportProviderRegistrar() {
		providerMap = new HashMap<String, ReportProvider>();
	}

	public static ReportProviderRegistrar getInstance() {
		if (instance == null)
			instance = new ReportProviderRegistrar();
		return instance;
	}

	public void registerReportProvider(ReportProvider reportProvider) {
		if (reportProvider != null)
			providerMap.put(reportProvider.getName(), reportProvider);
	}

	public ReportProvider getReportProviderByName(String reportName) {
		return providerMap.get(reportName);
	}

	public Set<String> getAllReportProviderNames() {
		return new TreeSet<String>(providerMap.keySet());
	}

	public List<ReportProvider> getAllReportProviders() {
		if (providerMap == null)
			providerMap = new HashMap<String, ReportProvider>();

		List<ReportProvider> result = new ArrayList<ReportProvider>(providerMap.values());
		Collections.sort(result, new SortByNameComparator());
		return result;
	}

	private class SortByNameComparator implements Comparator {

		@Override
		public int compare(Object o, Object o2) {
			ReportProvider a = (ReportProvider) o;
			ReportProvider b = (ReportProvider) o2;

			if (a == null && b == null)
				return 0;

			if (a == null)
				return -1;

			if (b == null)
				return 1;

			if (a.getName() == null && b.getName() == null)
				return 0;

			if (a.getName() == null)
				return -1;

			if (b.getName() == null)
				return 1;

			return (a.getName().compareTo(b.getName()));
		}
	}
}
