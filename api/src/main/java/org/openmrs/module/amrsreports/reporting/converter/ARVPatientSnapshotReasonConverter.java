package org.openmrs.module.amrsreports.reporting.converter;

import org.openmrs.module.amrsreports.snapshot.ARVPatientSnapshot;
import org.openmrs.module.amrsreports.util.MOHReportUtil;
import org.openmrs.module.reporting.data.converter.DataConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * Converter for Patient Snapshots
 */
public class ARVPatientSnapshotReasonConverter implements DataConverter {
    /**
     * @should test whether reason or transfer property exist
     * @should test for transfer as eligibility criteria
     * @should test for valid reason for eligibility
     * @param original
     * @return
     */
	@Override
	public Object convert(Object original) {
		ARVPatientSnapshot s = (ARVPatientSnapshot) original;

		if (s == null)
			return "";

		if (!s.hasProperty("reason") && !s.hasProperty("transfer"))
			return "";

		// do not report a date if there is no reason
		List<String> results = new ArrayList<String>();

        if(s.hasProperty("transfer") && (Boolean)s.get("transfer")){
            results.add("Transfer In");
        }
        else {
            results.add((String) s.get("reason"));
            if (s.hasProperty("extras"))
                results.addAll((List<String>) s.get("extras"));
        }

		return MOHReportUtil.joinAsSingleCell(results);
	}

	@Override
	public Class<?> getInputDataType() {
		return ARVPatientSnapshot.class;
	}

	@Override
	public Class<?> getDataType() {
		return String.class;
	}
}
