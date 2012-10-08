package org.openmrs.module.amrsreport;

import org.openmrs.BaseOpenmrsObject;
import org.openmrs.User;

public class UserReport extends BaseOpenmrsObject {

	private User amrsReportsUser;
	private String reportDefinitionUuid;
	private Integer userReportId;

	public User getAmrsReportsUser() {
		return amrsReportsUser;
	}

	public void setAmrsReportsUser(User amrsReportsUser) {
		this.amrsReportsUser = amrsReportsUser;
	}

	public String getReportDefinitionUuid() {
		return reportDefinitionUuid;
	}

	public void setReportDefinitionUuid(String reportDefinitionUuid) {
		this.reportDefinitionUuid = reportDefinitionUuid;
	}

	public Integer getUserReportId() {
		return userReportId;
	}

	public void setUserReportId(Integer userReportId) {
		this.userReportId = userReportId;
	}

	@Override
	public Integer getId() {
		return this.getUserReportId();
	}

	@Override
	public void setId(Integer id) {
		this.setUserReportId(id);
	}

}