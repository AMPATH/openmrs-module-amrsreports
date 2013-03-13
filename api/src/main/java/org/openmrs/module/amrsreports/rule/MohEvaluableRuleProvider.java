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

package org.openmrs.module.amrsreports.rule;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.logic.rule.provider.ClassRuleProvider;
import org.openmrs.module.ModuleClassLoader;
import org.openmrs.module.ModuleFactory;
import org.springframework.asm.ClassReader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 */
@Component
public class MohEvaluableRuleProvider extends ClassRuleProvider {

	private static final Log log = LogFactory.getLog(MohEvaluableRuleProvider.class);

	private static final String AMRS_REPORT_MODULE = "amrsreports";

	private static final String AMRS_REPORT_MODULE_API = "amrsreports-api";

	private static final String RULE_CLASS_SUFFIX = "Rule";

	private static final String CLASS_SUFFIX = "class";

	/**
	 * @see org.openmrs.logic.rule.provider.RuleProvider#afterStartup()
	 */
	@Override
	public void afterStartup() {

		if (log.isDebugEnabled())
			log.debug("Registering AMRS reports summary rules ...");

		try {
			ModuleClassLoader moduleClassLoader = ModuleFactory.getModuleClassLoader(AMRS_REPORT_MODULE);
			for (URL url : moduleClassLoader.getURLs()) {
				String filename = url.getFile();
				if (StringUtils.contains(filename, AMRS_REPORT_MODULE_API)) {
					ZipFile zipFile = new ZipFile(filename);
					for (Enumeration list = zipFile.entries(); list.hasMoreElements(); ) {
						ZipEntry entry = (ZipEntry) list.nextElement();
						String zipFileName = FilenameUtils.getBaseName(entry.getName());
						String zipFileExtension = FilenameUtils.getExtension(entry.getName());
						if (StringUtils.endsWith(zipFileName, RULE_CLASS_SUFFIX)
								&& StringUtils.equals(zipFileExtension, CLASS_SUFFIX)) {
							MohEvaluableRuleVisitor visitor = new MohEvaluableRuleVisitor();
							ClassReader classReader = new ClassReader(zipFile.getInputStream(entry));
							classReader.accept(visitor, false);
						}
					}
					zipFile.close();
				}
			}
		} catch (IOException e) {
			log.error("Processing rule classes failed. Rule might not get registered.");
		}
	}

	}
