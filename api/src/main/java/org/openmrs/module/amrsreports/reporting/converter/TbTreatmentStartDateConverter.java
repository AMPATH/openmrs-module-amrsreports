package org.openmrs.module.amrsreports.reporting.converter;

import org.apache.commons.lang.StringUtils;
import org.openmrs.module.amrsreports.AmrsReportsConstants;
import org.openmrs.module.amrsreports.model.PatientTBTreatmentData;
import org.openmrs.module.reporting.data.converter.DataConverter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * converter class for TbTreatmentStartDateDataDefinition
 */
public class TbTreatmentStartDateConverter implements DataConverter {

	@Override
	public Object convert(Object original) {
		if (original == null)
			return null;

		PatientTBTreatmentData details = (PatientTBTreatmentData) original;
		Set<Date> listOfDates = details.getEvaluationDates();

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/yyyy");
		List finalList = new ArrayList();

		if (listOfDates != null) {
			for (Date d : listOfDates) {
				finalList.add(simpleDateFormat.format(d));
			}
		}

		if (StringUtils.isNotBlank(details.getTbRegNO())) {
            finalList.add(details.getTbRegNO());
		}

		return StringUtils.join(finalList, AmrsReportsConstants.INTER_CELL_SEPARATOR);
	}

	@Override
	public Class<?> getInputDataType() {
		return PatientTBTreatmentData.class;
	}

	@Override
	public Class<?> getDataType() {
		return String.class;
	}
}
