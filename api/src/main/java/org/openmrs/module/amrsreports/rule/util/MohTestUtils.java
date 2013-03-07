package org.openmrs.module.amrsreports.rule.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptName;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;

/**
 *Contains utility methods for test classes
 */
public class MohTestUtils {
    private static final Log log = LogFactory.getLog(MohTestUtils.class);

    /**
     * create a question concept with associated set of answers from strings
     *
     * @param question
     * @param answers
     */
    public static void createQuestion(String question, String[] answers) {
        // create a new question concept
        ConceptService conceptService=Context.getConceptService();
        Concept q = new Concept();
        q.addName(new ConceptName(question, Context.getLocale()));
        q.setConceptClass(conceptService.getConceptClassByName("Question"));

        // loop over answers and add them one by one to the question
        for (String answer: answers) {
            // create a new concept for the answer
            Concept a = new Concept();
            a.addName(new ConceptName(answer, Context.getLocale()));
            conceptService.saveConcept(a);
            // create a ConceptAnswer and add it to the question
            ConceptAnswer ca = new ConceptAnswer();
            ca.setAnswerConcept(a);
            q.addAnswer(ca);
        }

        // save the question
        conceptService.saveConcept(q);
    }
}
