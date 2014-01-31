/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.amrsreports.reporting.converter;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.ConceptNumeric;
import org.openmrs.Obs;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ObsValueNumericConverterTest {

	/**
	 * @verifies return a blank string if valueNumeric is null
	 * @see ObsValueNumericConverter#convert(Object)
	 */
	@Test
	public void convert_shouldReturnABlankStringIfValueNumericIsNull() throws Exception {
		ConceptNumeric cn = new ConceptNumeric();
		cn.setUnits("units");

		Obs o = new Obs();
		o.setConcept(cn);
		o.setValueNumeric(null);

		ObsValueNumericConverter c = new ObsValueNumericConverter();
		assertThat((String) c.convert(o), is(""));
	}
}
