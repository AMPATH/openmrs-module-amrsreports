package org.openmrs.module.amrsreports.reporting.data.evaluator;

import junit.framework.Assert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.module.amrsreports.reporting.data.DateARTStartedDataDefinition;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.context.PersonEvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.TestUtil;

import java.util.Date;
import java.util.Map;

/**
 * test class for ART Start date definition
 */
public class DateARTStartedDataEvaluatorTest extends BaseModuleContextSensitiveTest {

	private Date evaluationDate;
	private PersonEvaluationContext evaluationContext;
	private DateARTStartedDataEvaluator evaluator;
	private DateARTStartedDataDefinition definition;

	private Log log = LogFactory.getLog(this.getClass());

	@Before
	public void setUp() throws Exception {

		executeDataSet("datasets/art-start-date.xml");

		Cohort c = new Cohort("6,7,8,9");

		evaluationDate = new Date();

		evaluationContext = new PersonEvaluationContext(evaluationDate);
		evaluationContext.setBaseCohort(c);

		definition = new DateARTStartedDataDefinition();
		evaluator = new DateARTStartedDataEvaluator();
	}

	@Test
	public void shouldCheckContentOfTestDataset() throws Exception {
		TestUtil.printOutTableContents(getConnection(), "amrsreports_hiv_care_enrollment");
	}

	@Test
	public void shouldReturnAListOfARTStartDatesforAPatient() throws EvaluationException {

		EvaluatedPersonData actual = evaluator.evaluate(definition, evaluationContext);
		Map<Integer, Object> data = actual.getData();

		Assert.assertNotNull(data);
		Assert.assertNotNull(data.get(6));
		Assert.assertEquals(data.get(6).toString(), "2004-07-15 00:00:00.0");
	}
}
