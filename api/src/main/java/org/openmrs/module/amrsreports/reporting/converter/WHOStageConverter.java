package org.openmrs.module.amrsreports.reporting.converter;

import org.openmrs.module.amrsreports.cache.MohCacheUtils;
import org.openmrs.module.amrsreports.reporting.common.ObsRepresentation;
import org.openmrs.module.amrsreports.rule.MohEvaluableNameConstants;
import org.openmrs.module.reporting.data.converter.DataConverter;

import java.util.Arrays;
import java.util.List;

/**
 * Converter for formatting WHO Stage column data
 */

public class WHOStageConverter implements DataConverter {

	private static final List<Integer> STAGE_1_CONCEPTS = Arrays.asList(
			MohCacheUtils.getConceptId(MohEvaluableNameConstants.WHO_STAGE_1_ADULT),
			MohCacheUtils.getConceptId(MohEvaluableNameConstants.WHO_STAGE_1_PEDS));

	private static final List<Integer> STAGE_2_CONCEPTS = Arrays.asList(
			MohCacheUtils.getConceptId(MohEvaluableNameConstants.WHO_STAGE_2_ADULT),
			MohCacheUtils.getConceptId(MohEvaluableNameConstants.WHO_STAGE_2_PEDS));

	private static final List<Integer> STAGE_3_CONCEPTS = Arrays.asList(
			MohCacheUtils.getConceptId(MohEvaluableNameConstants.WHO_STAGE_3_ADULT),
			MohCacheUtils.getConceptId(MohEvaluableNameConstants.WHO_STAGE_3_PEDS));

	private static final List<Integer> STAGE_4_CONCEPTS = Arrays.asList(
			MohCacheUtils.getConceptId(MohEvaluableNameConstants.WHO_STAGE_4_ADULT),
			MohCacheUtils.getConceptId(MohEvaluableNameConstants.WHO_STAGE_4_PEDS));

	@Override
	public Object convert(Object original) {
		ObsRepresentation o = (ObsRepresentation) original;

		if (o == null)
			return "";

		Integer answer = o.getValueCodedId();
		Integer stage;

		if (STAGE_1_CONCEPTS.contains(answer))
			stage = 1;
		else if (STAGE_2_CONCEPTS.contains(answer))
			stage = 2;
		else if (STAGE_3_CONCEPTS.contains(answer))
			stage = 3;
		else if (STAGE_4_CONCEPTS.contains(answer))
			stage = 4;
		else
			return "";

		return String.format("WHO Stage %d", stage);
	}

	@Override
	public Class<?> getInputDataType() {
		return ObsRepresentation.class;
	}

	@Override
	public Class<?> getDataType() {
		return String.class;
	}
}
