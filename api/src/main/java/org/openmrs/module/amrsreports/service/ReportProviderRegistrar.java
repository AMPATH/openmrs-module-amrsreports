package org.openmrs.module.amrsreports.service;

import org.openmrs.module.amrsreports.reporting.provider.ReportProvider;

import java.util.HashMap;
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
}
