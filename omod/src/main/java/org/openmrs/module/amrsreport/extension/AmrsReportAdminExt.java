package org.openmrs.module.amrsreport.extension;

import java.util.LinkedHashMap;
import java.util.Map;
import org.openmrs.api.context.Context;
import org.openmrs.module.Extension;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.web.extension.AdministrationSectionExt;

/**
 * admin page extension
 */
public class AmrsReportAdminExt extends AdministrationSectionExt {

	/**
	 * Defines the privilege required to the see the Administration section
	 * for the module
	 */
	@Override
	public String getRequiredPrivilege() {
		return ReportingConstants.PRIV_VIEW_REPORTS;
	}

	/**
	 * 
	 */
	@Override
	public Extension.MEDIA_TYPE getMediaType() {
		return Extension.MEDIA_TYPE.html;
	}

	/**
	 * @see org.openmrs.module.web.extension.AdministrationSectionExt#getLinks()
	 */
	@Override
	public Map<String, String> getLinks() {
		Map<String, String> map = new LinkedHashMap<String, String>();
		
		if (Context.hasPrivilege(ReportingConstants.PRIV_RUN_REPORTS)) {
			map.put("module/amrsreport/mohRender.form", "Run AMRS Reports");
			map.put("/module/amrsreport/cohortCounts.list", "View Cohort Counts");
		}

		if (Context.hasPrivilege(ReportingConstants.PRIV_VIEW_REPORTS)) {
			map.put("module/amrsreport/mohHistory.form", "View AMRS Reports");
		}

		if (Context.hasPrivilege(ReportingConstants.PRIV_RUN_REPORTS)) {
			map.put("module/amrsreport/locationPrivileges.form", "Location Privileges");
		}

		return map;
	}

	/**
	 * @see org.openmrs.module.web.extension.AdministrationSectionExt#getTitle()
	 */
	@Override
	public String getTitle() {
		return "AMRS Reports";
	}
}
