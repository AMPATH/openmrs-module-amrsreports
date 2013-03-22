package org.openmrs.module.amrsreports.reporting.converter;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.module.amrsreports.cache.MohCacheUtils;
import org.openmrs.module.amrsreports.rule.MohEvaluableNameConstants;
import org.openmrs.module.amrsreports.rule.util.MohRuleUtils;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.util.OpenmrsUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Converter for formatting WHO Stage and Date column data
 */

public class WHOStageAndDateConverter implements DataConverter {

	private static final List<Concept> STAGE_1_CONCEPTS = Arrays.asList(
			MohCacheUtils.getConcept(MohEvaluableNameConstants.WHO_STAGE_1_ADULT),
			MohCacheUtils.getConcept(MohEvaluableNameConstants.WHO_STAGE_1_PEDS));

	private static final List<Concept> STAGE_2_CONCEPTS = Arrays.asList(
			MohCacheUtils.getConcept(MohEvaluableNameConstants.WHO_STAGE_2_ADULT),
			MohCacheUtils.getConcept(MohEvaluableNameConstants.WHO_STAGE_2_PEDS));

	private static final List<Concept> STAGE_3_CONCEPTS = Arrays.asList(
			MohCacheUtils.getConcept(MohEvaluableNameConstants.WHO_STAGE_3_ADULT),
			MohCacheUtils.getConcept(MohEvaluableNameConstants.WHO_STAGE_3_PEDS));

	private static final List<Concept> STAGE_4_CONCEPTS = Arrays.asList(
			MohCacheUtils.getConcept(MohEvaluableNameConstants.WHO_STAGE_4_ADULT),
			MohCacheUtils.getConcept(MohEvaluableNameConstants.WHO_STAGE_4_PEDS));

	@Override
	public Object convert(Object original) {
		Obs o = (Obs) original;

		if (o == null)
			return "";

		Concept answer = o.getValueCoded();
		Integer stage = null;

		if (OpenmrsUtil.isConceptInList(answer, STAGE_1_CONCEPTS))
			stage = 1;
		else if (OpenmrsUtil.isConceptInList(answer, STAGE_2_CONCEPTS))
			stage = 2;
		else if (OpenmrsUtil.isConceptInList(answer, STAGE_3_CONCEPTS))
			stage = 3;
		else if (OpenmrsUtil.isConceptInList(answer, STAGE_4_CONCEPTS))
			stage = 4;
		else
			return "";

		return String.format("WHO Stage %d - %s", stage, MohRuleUtils.formatdates(o.getObsDatetime()));
	}

	@Override
	public Class<?> getInputDataType() {
		return Obs.class;
	}

	@Override
	public Class<?> getDataType() {
		return String.class;
	}
}
