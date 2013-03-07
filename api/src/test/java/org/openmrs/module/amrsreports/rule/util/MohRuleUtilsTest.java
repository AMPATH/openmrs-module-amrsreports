package org.openmrs.module.amrsreports.rule.util;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.amrsreports.rule.MohEvaluableNameConstants;

import java.lang.String;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created with IntelliJ IDEA.
 * User: oliver
 * Date: 11/14/12
 * Time: 11:13 AM
 * To change this template use File | Settings | File Templates.
 */
public class MohRuleUtilsTest extends TestCase {

    /*calculates age and assigns age group*/
    @Test
    public void testGetAgeGroupAtDate() throws Exception {

        GregorianCalendar getBirthdate = new GregorianCalendar(1960,8,1) ;
        Date birthdate = getBirthdate.getTime();

        Date evalDate = new Date();

        MohEvaluableNameConstants.AgeGroup ageGroup = MohRuleUtils.getAgeGroupAtDate(birthdate, evalDate);
        String testStr1 = ageGroup.toString();

        Assert.assertTrue("Age group never matched the passed string",ageGroup.toString().equals(MohEvaluableNameConstants.AgeGroup.ABOVE_TWELVE_YEARS.toString()));



    }
}
