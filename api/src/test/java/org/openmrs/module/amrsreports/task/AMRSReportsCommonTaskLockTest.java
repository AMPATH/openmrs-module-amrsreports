package org.openmrs.module.amrsreports.task;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Unit tests for {@link AMRSReportsCommonTaskLock}
 */
public class AMRSReportsCommonTaskLockTest {
	/**
	 * @verifies return true if a lock is obtained
	 * @see AMRSReportsCommonTaskLock#getLock(Class)
	 */
	@Test
	public void getLock_shouldReturnTrueIfALockIsObtained() throws Exception {
		// ensure the common lock is not locked yet
		Boolean actual = AMRSReportsCommonTaskLock.getInstance().isLocked();
		assertThat(actual, is(Boolean.FALSE));

		// attempt to get a lock using the test class
		actual = AMRSReportsCommonTaskLock.getInstance().getLock(this.getClass());
		assertThat(actual, is(Boolean.TRUE));

		// release lock for the next test
		// TODO figure out why this is necessary
		AMRSReportsCommonTaskLock.getInstance().releaseLock(this.getClass());
	}

	/**
	 * @verifies return false if already locked
	 * @see AMRSReportsCommonTaskLock#getLock(Class)
	 */
	@Test
	public void getLock_shouldReturnFalseIfAlreadyLocked() throws Exception {
		// ensure the common lock is not locked yet
		Boolean actual = AMRSReportsCommonTaskLock.getInstance().isLocked();
		assertThat(actual, is(Boolean.FALSE));

		// attempt to get a lock using the test class
		actual = AMRSReportsCommonTaskLock.getInstance().getLock(this.getClass());
		assertThat(actual, is(Boolean.TRUE));

		// attempt to get a lock using a different class
		actual = AMRSReportsCommonTaskLock.getInstance().getLock(String.class);
		assertThat(actual, is(Boolean.FALSE));

		// even when the class is the same as the locking class ... should say it is already locked
		actual = AMRSReportsCommonTaskLock.getInstance().getLock(this.getClass());
		assertThat(actual, is(Boolean.FALSE));

		// release lock for the next test
		// TODO figure out why this is necessary
		AMRSReportsCommonTaskLock.getInstance().releaseLock(this.getClass());
	}

	/**
	 * @verifies release a lock if the lockingClass matches
	 * @see AMRSReportsCommonTaskLock#releaseLock(Class)
	 */
	@Test
	public void releaseLock_shouldReleaseALockIfTheLockingClassMatches() throws Exception {
		// ensure the common lock is not locked yet
		Boolean actual = AMRSReportsCommonTaskLock.getInstance().isLocked();
		assertThat(actual, is(Boolean.FALSE));

		// attempt to get a lock using the test class
		actual = AMRSReportsCommonTaskLock.getInstance().getLock(this.getClass());
		assertThat(actual, is(Boolean.TRUE));

		// attempt to release the lock as a different class
		actual = AMRSReportsCommonTaskLock.getInstance().releaseLock(String.class);
		assertThat(actual, is(Boolean.FALSE));

		// verify the lock is still in place
		actual = AMRSReportsCommonTaskLock.getInstance().isLocked();
		assertThat(actual, is(Boolean.TRUE));

		// attempt to release the lock as the correct class
		actual = AMRSReportsCommonTaskLock.getInstance().releaseLock(this.getClass());
		assertThat(actual, is(Boolean.TRUE));

		// verify the lock is removed
		actual = AMRSReportsCommonTaskLock.getInstance().isLocked();
		assertThat(actual, is(Boolean.FALSE));
	}

}
