package org.openmrs.module.amrsreports.extension;

import java.util.LinkedHashMap;
import java.util.Map;
import org.openmrs.api.context.Context;
import org.openmrs.module.Extension;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.web.extension.AdministrationSectionExt;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.util.PrivilegeConstants;

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
			map.put("module/amrsreports/mohRender.form", "Run AMRS Reports");
		}

		if (Context.hasPrivilege(ReportingConstants.PRIV_VIEW_REPORTS)) {
			map.put("module/amrsreports/mohHistory.form", "View AMRS Reports");
		}

		if (Context.hasPrivilege(PrivilegeConstants.VIEW_LOCATIONS)) {
			map.put("module/amrsreports/facility.list", "View MOH Facilities");
		}

		if (Context.hasPrivilege(ReportingConstants.PRIV_RUN_REPORTS)) {
			map.put("/module/amrsreports/cohortCounts.list", "View Cohort Counts");
		}

		if (Context.hasPrivilege(ReportingConstants.PRIV_RUN_REPORTS)) {
			map.put("module/amrsreports/facilityPrivileges.form", "Manage User/Facility Privileges");
		}

		map.put("module/amrsreports/settings.form", "Settings");

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
