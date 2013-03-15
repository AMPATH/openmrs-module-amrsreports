package org.openmrs.module.amrsreports.web.propertyeditor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.springframework.util.StringUtils;

import java.beans.PropertyEditorSupport;

/**
 * Custom property editor for getting a location by its name
 */
public class LocationNameEditor extends PropertyEditorSupport {

	private Log log = LogFactory.getLog(this.getClass());

	public void setAsText(String text) throws IllegalArgumentException {
		LocationService ls = Context.getLocationService();
		if (StringUtils.hasText(text)) {
			try {
				setValue(ls.getLocation(text));
			}
			catch (Exception ex) {
				log.error("Error setting text: " + text, ex);
				throw new IllegalArgumentException("Location not found: " + ex.getMessage());
			}
		} else {
			setValue(null);
		}
	}

	public String getAsText() {
		Location t = (Location) getValue();
		if (t == null && Context.isAuthenticated()) {
			return null;
		} else {
			return t.getName();
		}
	}
}
