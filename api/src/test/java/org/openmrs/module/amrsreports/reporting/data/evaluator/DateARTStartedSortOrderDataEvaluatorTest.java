package org.openmrs.module.amrsreports.reporting.data.evaluator;

import junit.framework.Assert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Patient;
import org.openmrs.module.amrsreports.reporting.data.DateARTStartedSortOrderDataDefinition;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.context.PersonEvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Test class for DateARTStartedSortOrderDataEvaluator
 */
public class DateARTStartedSortOrderDataEvaluatorTest extends BaseModuleContextSensitiveTest {

	protected Patient patient;
	private Date evaluationDate;
	private PersonEvaluationContext evaluationContext;
	private DateARTStartedSortOrderDataEvaluator evaluator;
	private DateARTStartedSortOrderDataDefinition definition;

	private Log log = LogFactory.getLog(this.getClass());

	@Before
	public void setUp() throws Exception {

		executeDataSet("datasets/art-start-date.xml");

		Cohort c = new Cohort("6,7,8,9");
		c.setId(1);
		evaluationDate = new Date();

		evaluationContext = new PersonEvaluationContext(evaluationDate);
		evaluationContext.setBaseCohort(c);

		definition = new DateARTStartedSortOrderDataDefinition();
		evaluator = new DateARTStartedSortOrderDataEvaluator();
	}

	@Test
	public void shouldReturnAListOfFormattedDates() throws EvaluationException {

		EvaluatedPersonData actual = evaluator.evaluate(definition, evaluationContext);
		Map<Integer, Object> data = actual.getData();

		ArrayList<String> expected = new ArrayList<String>(Arrays.asList("2004-07", "2004-08", "2004-09", "2005-07"));

		List finalList = new ArrayList();

		for (Integer patientID : evaluationContext.getBaseCohort().getMemberIds()) {
			finalList.add(data.get(patientID));
		}

		Assert.assertEquals(expected, finalList);
	}
}
