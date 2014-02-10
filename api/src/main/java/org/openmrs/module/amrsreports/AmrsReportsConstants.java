package org.openmrs.module.amrsreports;

/**
 * Primary container for all static constants used in this module.  Note: other constant files exist, but will hopefully
 * be migrated into this one eventually or clearly separated by purpose.
 */
public class AmrsReportsConstants {

	// separator used to delineate multiple values within a single cell
	public static final String INTER_CELL_SEPARATOR = "\n";

	public static final char DEFAULT_CSV_DELIMITER = ',';

    public static final String TB_REGISTRATION_NO_ATTRIBUTE_TYPE = "amrsreports.tbRegistrationAttributeType";
	public static final Integer TB_REGISTRATION_NO_ATTRIBUTE_TYPE_DEFAULT = 17;

	public static final String GP_CCC_NUMBER_IDENTIFIER_TYPE = "amrsreports.cccIdentifierType";

	public static final String GP_PRODUCTION_SERVER_URL = "amrsreports.productionServerURL";

	public static final String TRANSFER_IN = "Transfer In";

	public static final String SAVED_COHORT_UUID = "AMRSReportsTemporaryCohort000000000000";

	public static final Integer DEFAULT_BATCH_SIZE = 1000;
}
