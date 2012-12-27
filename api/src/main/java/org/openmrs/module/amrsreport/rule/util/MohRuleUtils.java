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
package org.openmrs.module.amrsreport.rule.util;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreport.cache.MohCacheUtils;
import org.openmrs.module.amrsreport.rule.MohEvaluableNameConstants;
import org.openmrs.module.amrsreport.util.MohFetchOrdering;
import org.openmrs.util.OpenmrsUtil;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

/**
 */
public class MohRuleUtils {

	private static final Log log = LogFactory.getLog(MohRuleUtils.class);

	/**
	 * Check whether an object is a valid collection object. Valid here means it's not null, assignable to Collection class and not empty
	 *
	 * @param collection the collection
	 * @return true when the three condition are met by the object
	 */
	public static Boolean isValidCollectionObject(final Object collection) {
		if (collection == null)
			return Boolean.FALSE;

		if (!ClassUtils.isAssignable(collection.getClass(), Collection.class))
			return Boolean.FALSE;

		if (CollectionUtils.isEmpty((Collection<?>) collection))
			return Boolean.FALSE;

		return Boolean.TRUE;
	}

	/**
	 * Check whether an object is a valid fetch size. Valid fetch size object are Integer bigger than 0
	 *
	 * @param size the size
	 * @return true when the object is Integer bigger than 0
	 */
	public static Boolean isValidSizeObject(final Object size) {
		if (size == null)
			return Boolean.FALSE;

		if (!ClassUtils.isAssignable(size.getClass(), Integer.class))
			return Boolean.FALSE;

		if (NumberUtils.toInt(size.toString()) <= 0)
			return Boolean.FALSE;

		return Boolean.TRUE;
	}

	/**
	 * Check whether the ordering object is valid or not. Valid ordering is String "asc" or "desc"
	 *
	 * @param order the order
	 * @return true if the order object is a String "asc" or "desc"
	 */
	public static Boolean isValidOrderObject(final Object order) {
		if (order == null)
			return Boolean.FALSE;

		if (!ClassUtils.isAssignable(order.getClass(), String.class))
			return Boolean.FALSE;

		for (MohFetchOrdering mohFetchOrdering : MohFetchOrdering.values())
			if (StringUtils.equalsIgnoreCase(String.valueOf(order), mohFetchOrdering.getValue()))
				return Boolean.TRUE;

		return Boolean.FALSE;
	}

	/**
	 * Check whether the obs object is valid or not.
	 *
	 * @param obs the obs object
	 * @return true if the object is an Obs object
	 */
	public static Boolean isValidObsObject(final Object obs) {
		if (obs == null)
			return Boolean.FALSE;

		if (!ClassUtils.isAssignable(obs.getClass(), Obs.class))
			return Boolean.FALSE;

		return Boolean.TRUE;
	}

	/**
	 * Parse a date string or return a default date value when the parsing failed
	 *
	 * @param dateString  the date string
	 * @param defaultDate the default date
	 * @return date object based on the date string or default date when the parsing failed
	 */
	public static Date parse(final String dateString, final Date defaultDate) {
		Date date;
		try {
			date = Context.getDateFormat().parse(dateString);
		} catch (ParseException e) {
			log.error("Parsing " + dateString + " to Date object failed. Ignoring and using default value.");
			date = defaultDate;
		}
		return date;
	}
	
	public static String formatdates(Date date){
		if (date == null)
			return "Unknown";

		Format formatter;
		formatter = new SimpleDateFormat("dd-MMM-yy");
		String s = formatter.format(date);
		
		return s;
		
	}

    /**
     * determine the age group for a patient at a given date
     * @should determine the age group for a patient at a given date
     * @param birthdate birth date of the patient whose age is used in the calculations
     * @param when the date upon which the age should be identified
     * @return the appropriate age group
     */
    public static MohEvaluableNameConstants.AgeGroup getAgeGroupAtDate(Date birthdate, Date when) {
        //birthdate = patient.getBirthdate();
        if (birthdate == null) {
            return null;
        }

        Calendar now = Calendar.getInstance();
        if (when != null) {
            now.setTime(when);
        }

        Calendar then = Calendar.getInstance();
        then.setTime(birthdate);

        int ageInMonths = 0;
        while (!then.after(now)) {
            then.add(Calendar.MONTH, 1);
            ageInMonths++;
        }
        ageInMonths--;

        if (ageInMonths < 18) {
            return MohEvaluableNameConstants.AgeGroup.UNDER_EIGHTEEN_MONTHS;
        }

        if (ageInMonths < 60) {
            return MohEvaluableNameConstants.AgeGroup.EIGHTEEN_MONTHS_TO_FIVE_YEARS;
        }

        if (ageInMonths < 144) {
            return MohEvaluableNameConstants.AgeGroup.FIVE_YEARS_TO_TWELVE_YEARS;
        }

        return MohEvaluableNameConstants.AgeGroup.ABOVE_TWELVE_YEARS;
    }

	/**
	 * helper method to reduce code for validation methods
	 *
	 * @param concept
	 * @param name
	 * @return
	 */
	public static boolean compareConceptToName(Concept concept, String name) {
		return OpenmrsUtil.nullSafeEquals(concept, MohCacheUtils.getConcept(name));
	}
}
