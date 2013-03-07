package org.openmrs.module.amrsreports.model;

import java.util.Date;

/**
 * Utility model for communicating a WHO stage and a date in a single object
 */
public class WHOStageAndDate {

	private String stage = null;
	private Date date = null;

	public WHOStageAndDate() {
		// pass
	}

	public WHOStageAndDate(String stage, Date date) {
		this.stage = stage;
		this.date = date;
	}

	public String getStage() {
		return stage;
	}

	public void setStage(String stage) {
		this.stage = stage;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}
