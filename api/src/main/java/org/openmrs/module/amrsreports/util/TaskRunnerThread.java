package org.openmrs.module.amrsreports.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.UserContext;
import org.openmrs.module.amrsreports.task.AMRSReportsTask;
import org.openmrs.util.PrivilegeConstants;

public class TaskRunnerThread extends Thread {

	private static final long THREAD_SLEEP_PERIOD = 2000l;

	private Log log = LogFactory.getLog(this.getClass());

	/**
	 * Whether or not activity should continue with this thread
	 */
	private boolean active = false;

	/**
	 * Required UserContext for running tasks
	 */
	private UserContext userContext = null;

	/**
	 * Task to be executed by this thread
	 */
	private AMRSReportsTask task = null;

	/**
	 * Flag to keep track of the status of the migration process
	 */
	private Status taskStatus = Status.NONE;

	/**
	 * instance used for singleton
	 */
	private static volatile TaskRunnerThread instance = null;


	/**
	 * The different states this thread can be in at a given point during task running
	 */
	public enum Status {
		RUNNING, STOPPED, COMPLETED, ERROR, NONE
	}

	/**
	 * Constructor to initialize variables
	 */
	public void initialize(AMRSReportsTask task, UserContext userContext) {
		this.task = task;
		this.userContext = userContext;
		this.active = false;
		this.taskStatus = Status.NONE;
	}

	/**
	 * stop running the task
	 */
	public static void destroyInstance() throws Throwable {
		if (instance != null) {
			instance.getTask().stopExecuting();
			instance.getTask().shutdown();
			instance.interrupt();
			instance = null;
		}
	}

	/**
	 * getter for instance
	 */
	public static TaskRunnerThread getInstance() {
		if (instance == null)
			instance = new TaskRunnerThread();
		return instance;
	}

	/**
	 * @return the active
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @param active the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	public AMRSReportsTask getTask() {
		return task;
	}

	public void setTask(AMRSReportsTask task) {
		this.task = task;
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {

		Context.openSession();
		Context.setUserContext(userContext);
		Context.addProxyPrivilege(PrivilegeConstants.SQL_LEVEL_ACCESS);

		taskStatus = Status.RUNNING;

		while (isActive() && taskStatus == Status.RUNNING) {
			try {
				// run the task
				if (isActive())
					task.execute();

				// if task is finished ...
				if (taskStatus != Status.STOPPED)
					taskStatus = Status.COMPLETED;

			} catch (APIException api) {
				// log this as a debug, because we want to swallow minor errors
				log.debug("Unable to run task", api);

				try {
					Thread.sleep(THREAD_SLEEP_PERIOD);
				} catch (InterruptedException e) {
					log.warn("Task runner thread has been abnormally interrupted", e);
				}

			} catch (Exception e) {
				taskStatus = Status.ERROR;
				log.warn("Some error occurred while running a task", e);
			}
		}
		// clean up
		Context.removeProxyPrivilege(PrivilegeConstants.SQL_LEVEL_ACCESS);
		Context.closeSession();
		setActive(false);
	}

	/**
	 * @return the taskStatus
	 */
	public Status getTaskStatus() {
		return taskStatus;
	}

	/**
	 * @return the class name of the current task
	 */
	public String getCurrentTaskClassname() {
		if (task == null)
			return null;

		return task.getClass().getSimpleName();
	}

}
