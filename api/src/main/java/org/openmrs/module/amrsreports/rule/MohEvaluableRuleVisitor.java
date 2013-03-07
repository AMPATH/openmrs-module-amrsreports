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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.logic.token.TokenService;
import org.springframework.asm.AnnotationVisitor;
import org.springframework.asm.Attribute;
import org.springframework.asm.ClassVisitor;
import org.springframework.asm.FieldVisitor;
import org.springframework.asm.MethodVisitor;

public class MohEvaluableRuleVisitor implements ClassVisitor {

	private static final Log log = LogFactory.getLog(MohEvaluableRuleVisitor.class);

	private static final String RULE_PACKAGE_NAME = "org.openmrs.module.amrsreport.rule";

	private static final String TOKEN_FIELD_NAME = "TOKEN";

	private String name;

	private String token;

	/**
	 * Static method to replace classname in path format to java format (e,g. replacing "/" with ".").
	 * For example: if we pass "org/openmrs/module" the output will be "org.openmrs.module"
	 *
	 * @param name the full classname in path format
	 * @return full classname in java format
	 */
	private String convert(String name) {
		return StringUtils.replace(name, "/", ".");
	}


	public void visit(final int version, final int access, final String name, final String signature,
	                  final String superName, final String[] interfaces) {
		// only proccess rule that subclass of abstract rule or abstract post processor rule
		if (StringUtils.contains(convert(superName), RULE_PACKAGE_NAME))
			this.name = convert(name);
	}

	/**
	 * @see ClassVisitor#visitSource(String, String)
	 */
	public void visitSource(final String source, final String debug) {
	}

	/**
	 * @see ClassVisitor#visitOuterClass(String, String, String)
	 */
	public void visitOuterClass(final String owner, final String name, final String description) {
	}

	/**
	 * @see ClassVisitor#visitAnnotation(String, boolean)
	 */
	public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
		return null;
	}

	/**
	 * @see ClassVisitor#visitField(int, String, String, String, Object)
	 */
	public FieldVisitor visitField(final int access, final String name, final String description,
	                               final String signature, final Object value) {
		if (StringUtils.equals(TOKEN_FIELD_NAME, name))
			this.token = String.valueOf(value);
		return null;
	}

	/**
	 * @see ClassVisitor#visitMethod(int, String, String, String, String[])
	 */
	public MethodVisitor visitMethod(final int access, final String name, final String desc,
	                                 final String signature, final String[] exceptions) {
		return null;
	}

	/**
	 * @see ClassVisitor#visitInnerClass(String, String, String, int)
	 */
	public void visitInnerClass(final String name, final String outerName, final String innerName, final int access) {
	}

	/**
	 * @see ClassVisitor#visitAttribute(Attribute)
	 */
	public void visitAttribute(final Attribute attr) {
	}

	/**
	 * @see ClassVisitor#visitEnd()
	 */
	public void visitEnd() {
		// only register when the class is a subclass of the EvaluableRule class and token is not empty
		if (name != null && token != null)
			Context.getService(TokenService.class).registerToken(token, new MohEvaluableRuleProvider(), name);
	}
}
