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
package org.openmrs.module.amrsreports;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;

import java.io.FileOutputStream;
import java.util.Arrays;

/**
 * This class can be run like a junit test, but it is not actually a test. JUnit won't run it
 * because it does not have "Test" in its class name.
 */
@Ignore
public class CreateInitialDataSet extends BaseModuleContextSensitiveTest {
	
	/**
	 * This test creates an xml dbunit file from the current database connection information found
	 * in the runtime properties. This method has to "skip over the base setup" because it tries to
	 * do things (like initialize the database) that shouldn't be done to a standard mysql database.
	 * 
	 * @throws Exception
	 */
	@Test
	@SkipBaseSetup
	public void shouldCreateInitialTestDataSetXmlFile() throws Exception {
		
		// only run this test if it is being run alone.
		// this allows the junit-report ant target and the "right-
		// click-on-/test/api-->run as-->junit test" methods to skip
		// over this whole "test"
		if (getLoadCount() != 1)
			return;
		
		// database connection for dbunit
		IDatabaseConnection connection = new DatabaseConnection(getConnection());
		
		// partial database export
		QueryDataSet initialDataSet = new QueryDataSet(connection);

		for (String table : Arrays.asList(
				"concept",
				"concept_answer",
				"concept_class",
				"concept_complex",
				"concept_datatype",
				"concept_description",
				"concept_map",
				"concept_name",
				"concept_name_tag",
				"concept_name_tag_map",
				"concept_numeric",
				"concept_set",
				"concept_set_derived",
				"concept_source",
				"concept_state_conversion",
				"concept_word",
				"drug",
				"drug_ingredient"
				)) {
			initialDataSet.addTable(table, "SELECT * FROM " + table);
		}

		FlatXmlDataSet.write(initialDataSet, new FileOutputStream("concept-dictionary.xml"));
		
		// full database export
		//IDataSet fullDataSet = connection.createDataSet();
		//FlatXmlDataSet.write(fullDataSet, new FileOutputStream("full.xml"));
		
		// dependent tables database export: export table X and all tables that
		// have a PK which is a FK on X, in the right order for insertion
		//String[] depTableNames = TablesDependencyHelper.getAllDependentTables(connection, "X");
		//IDataSet depDataset = connection.createDataSet( depTableNames );
		//FlatXmlDataSet.write(depDataSet, new FileOutputStream("dependents.xml")); 
		
		//TestUtil.printOutTableContents(getConnection(), "encounter_type", "encounter");
	}
	
	/**
	 * Make sure we use the database defined by the runtime properties and not the hsql in-memory
	 * database
	 * 
	 * @see org.openmrs.test.BaseContextSensitiveTest#useInMemoryDatabase()
	 */
	@Override
	public Boolean useInMemoryDatabase() {
		return false;
	}
}
