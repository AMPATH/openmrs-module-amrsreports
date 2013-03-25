package org.openmrs.module.amrsreports.reporting.converter;

import org.junit.Test;
import org.openmrs.module.amrsreports.snapshot.ARVPatientSnapshot;

import java.util.Date;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Unit tests for {@link ARVPatientSnapshotDateConverter}
 */
public class ARVPatientSnapshotDateConverterTest {
	/**
	 * @verifies return null if snapshot is null
	 * @see ARVPatientSnapshotDateConverter#convert(Object)
	 */
	@Test
	public void convert_shouldReturnNullIfSnapshotIsNull() throws Exception {
		ARVPatientSnapshot s = null;
		assertThat((Date) new ARVPatientSnapshotDateConverter().convert(s), is((Date) null));
	}

	/**
	 * @verifies return null if no reason exists
	 * @see ARVPatientSnapshotDateConverter#convert(Object)
	 */
	@Test
	public void convert_shouldReturnNullIfNoReasonExists() throws Exception {
		ARVPatientSnapshot s = new ARVPatientSnapshot();
		assertThat((Date) new ARVPatientSnapshotDateConverter().convert(s), is((Date) null));
	}

	/**
	 * @verifies return null if no lastDate exists
	 * @see ARVPatientSnapshotDateConverter#convert(Object)
	 */
	@Test
	public void convert_shouldReturnNullIfNoLastDateExists() throws Exception {
		ARVPatientSnapshot s = new ARVPatientSnapshot();
		s.set("reason", "something");
		assertThat((Date) new ARVPatientSnapshotDateConverter().convert(s), is((Date) null));
	}

	/**
	 * @verifies return lastDate as Date
	 * @see ARVPatientSnapshotDateConverter#convert(Object)
	 */
	@Test
	public void convert_shouldReturnLastDateAsDate() throws Exception {
		Date today = new Date();
		ARVPatientSnapshot s = new ARVPatientSnapshot();
		s.set("reason", "something");
		s.set("lastDate", today);
		assertThat((Date) new ARVPatientSnapshotDateConverter().convert(s), is(today));
	}
}
