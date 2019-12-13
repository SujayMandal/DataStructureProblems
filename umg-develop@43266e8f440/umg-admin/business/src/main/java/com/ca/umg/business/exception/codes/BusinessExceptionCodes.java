package com.ca.umg.business.exception.codes;

/**
 * @author audhyabh
 */
public final class BusinessExceptionCodes {

	public static final String BSE000001 = "BSE000001".intern();

	/**
	 * Code to identify system key not defined exception
	 */
	public static final String BSE000002 = "BSE000002".intern();

	/**
	 * Code to identify schema creation failed exception
	 */
	public static final String BSE000003 = "BSE000003".intern();

	/**
	 * Code to identify datasource initialization error
	 */
	public static final String BSE000004 = "BSE000004".intern();

	public static final String BSE000005 = "BSE000005".intern();
	/**
	 * Code to identify xsd error
	 */
	public static final String BSE000006 = "BSE000006".intern();

	/**
	 * Code to identify invalid model xml error.
	 */
	public static final String BSE000007 = "BSE000007".intern();
	/**
	 * Code to identify resouce not found.
	 */
	public static final String BSE000008 = "BSE000008".intern();

	/**
	 * Code to identify model artifact fetch error
	 */
	public static final String BSE000010 = "BSE000010".intern();

	/**
	 * Code to identify model artifact fetch input validation error
	 */
	public static final String BSE000011 = "BSE000011".intern();

	/**
	 * Container Name is empty or null.
	 */
	public static final String BSE000012 = "BSE000012".intern();
	/**
	 * No syndicate data available for the given container.
	 */
	public static final String BSE000013 = "BSE000013".intern();
	/**
	 * container name or version id is empty or null
	 */
	public static final String BSE000014 = "BSE000014".intern();
	/**
	 * SyndicateData is null or empty.
	 */
	public static final String BSE000015 = "BSE000015".intern();
	/**
	 * Exception occured accessing table metadata
	 */
	public static final String BSE000016 = "BSE000016".intern();
	/**
	 * Exception occured accessing table indexes.
	 */
	public static final String BSE000017 = "BSE000017".intern();

	/**
	 * SyndicateDataList is empty for a given container.
	 */
	public static final String BSE000018 = "BSE000018".intern();

	/**
	 * TableName Empty is empty or null.
	 */
	public static final String BSE000019 = "BSE000019".intern();

	/**
	 * SyndicateDataInfo is Null.
	 */
	public static final String BSE000020 = "BSE000020".intern();

	/**
	 * 'Active From' date time should be greater than current date time.
	 */
	public static final String BSE000021 = "BSE000021".intern();

	/**
	 * Exception while deleting model artifact from SAN
	 */
	public static final String BSE000022 = "BSE000022".intern();

	/**
	 * Exception while deleting syndicate data container
	 */
	public static final String BSE000023 = "BSE000023".intern();

	/**
	 * Exception while converting string date format to milliseconds
	 */
	public static final String BSE000024 = "BSE000024".intern();

	/**
	 * To identify syndicate data create validation errors.
	 */
	public static final String BSE000025 = "BSE000025".intern();

	/**
	 * To identify incorrect syndicate data file upload.
	 */
	public static final String BSE000026 = "BSE000026".intern();

	/**
	 * To check if uploaded file converted to bytes to save as blob in DB
	 */
	public static final String BSE000027 = "BSE000027".intern();

	/**
	 * Creation of table/index/insertion into Syndicated Data table
	 */
	public static final String BSE000028 = "BSE000028".intern();

	/**
	 * No Syndicate Provider data available with the given name.
	 */
	public static final String BSE000029 = "BSE000029".intern();

	/**
	 * No syndicate providers available.
	 */
	public static final String BSE000030 = "BSE000030".intern();

	/**
	 * create model error
	 */
	public static final String BSE000031 = "BSE000031".intern();

	/**
	 * delete model error
	 */
	public static final String BSE000032 = "BSE000032".intern();

	/**
	 * Syndicate data creation, date validation error, date/time overlap
	 */
	public static final String BSE000033 = "BSE000033".intern();

	/**
	 * Syndicate data creation, date validation error, date/time gap exists
	 */
	public static final String BSE000034 = "BSE000034".intern();

	/**
	 * Model/Model library already exists
	 */
	public static final String BSE000035 = "BSE000035".intern();

	/**
	 * provided container name or version id not available in database.
	 */
	public static final String BSE000036 = "BSE000036".intern();

	/**
	 * Error in fetching syndicate data queries.
	 */
	public static final String BSE000037 = "BSE000037".intern();

	/**
	 * Error in creating syndicate data queries.
	 */
	public static final String BSE000038 = "BSE000038".intern();

	/**
	 * Query Name already exists.
	 */
	public static final String BSE000039 = "BSE000039".intern();

	/**
	 * Unable to update syndicate data query execution sequence.
	 */
	public static final String BSE000040 = "BSE000040".intern();

	/**
	 * Cannot have two queries with same sequence number.
	 */
	public static final String BSE000041 = "BSE000041".intern();

	/**
	 * Query execution sequence have to be positive number starting from one, in increments of one.
	 */
	public static final String BSE000042 = "BSE000042".intern();

	/**
	 * Query name cannot be empty.
	 */
	public static final String BSE000043 = "BSE000043".intern();

	/**
	 * Sequence number has to be a positive integer.
	 */
	public static final String BSE000044 = "BSE000044".intern();

	/**
	 * Exception occurred while parsing MID IO
	 */
	public static final String BSE000045 = "BSE000045".intern();

	/**
	 * Exception for query test
	 */
	public static final String BSE000046 = "BSE000046".intern();

	/**
	 * Exception saving mapping details
	 */
	public static final String BSE000047 = "BSE000047".intern();

	/**
	 * No TID Mapping with name as {} is available.
	 */
	public static final String BSE000048 = "BSE000048".intern();

	/**
	 * Error in TID operations
	 */
	public static final String BSE000049 = "BSE000049".intern();

	/**
	 * Error in fetching syndicate data queries by mapping name.
	 */
	public static final String BSE000050 = "BSE000050".intern();

	/**
	 * Error in fetching syndicate data queries by mapping name and type.
	 */
	public static final String BSE000051 = "BSE000051".intern();

	/**
	 * Error during datatype validation for input params.
	 */
	public static final String BSE000052 = "BSE000052".intern();

	/**
	 * no table aliases were found in input parameters.
	 */
	public static final String BSE000053 = "BSE000053".intern();

	/**
	 * Bad params in where clause in syndicate data query
	 */
	public static final String BSE000054 = "BSE000054".intern();

	/**
	 * Table aliases are not matching with the input parameters
	 */
	public static final String BSE000055 = "BSE000055".intern();

	/**
	 * Deleted output parameters are mapped in TID
	 */
	public static final String BSE000056 = "BSE000056".intern();

	/**
	 * Data format is not matching.
	 */
	public static final String BSE000057 = "BSE000057".intern();

	/**
	 * Table Alias for column not found.
	 */
	public static final String BSE000058 = "BSE000058".intern();

	/**
	 * CheckSum is not matching.
	 */
	public static final String BSE000059 = "BSE000059".intern();

	/**
	 * To identify UMG version create validation errors.
	 */
	public static final String BSE000060 = "BSE000060".intern();

	/**
	 * Unable to create new UMG version.
	 */
	public static final String BSE000061 = "BSE000061".intern();

	/**
	 * Version already exists.
	 */
	public static final String BSE000062 = "BSE000062".intern();

	/**
	 *
	 */
	public static final String BSE000063 = "BSE000063".intern();

	/**
	 * Unable to update UMG version.
	 */
	public static final String BSE000064 = "BSE000064".intern();
	/**
	 * Version is not tested
	 */
	public static final String BSE000065 = "BSE000065".intern();

	/**
	 * Version is not published
	 */
	public static final String BSE000066 = "BSE000066".intern();

	/**
	 * TID copy error.
	 */
	public static final String BSE000067 = "BSE000067".intern();

	/**
	 * Version doesnot exists
	 */
	public static final String BSE000068 = "BSE000068".intern();

	/**
	 * tenant base url not defined for deployment
	 */
	public static final String BSE000069 = "BSE000069".intern();

	/**
	 * Runtime url not defined
	 */
	public static final String BSE000070 = "BSE000070".intern();

	/**
	 * Deploy fail
	 */
	public static final String BSE000071 = "BSE000071".intern();

	/**
	 * undeploy fail
	 */
	public static final String BSE000072 = "BSE000072".intern();

	/**
	 * UMG version creation : Major version cannot be blank when creating minor version.
	 */
	public static final String BSE000073 = "BSE000073".intern();

	/**
	 * Validation error during Query Edit Test.
	 */
	public static final String BSE000074 = "BSE000074".intern();

	/**
	 * Validation error during Query Edit Test with data type.
	 */
	public static final String BSE000075 = "BSE000075".intern();

	/**
	 * error in date range
	 */
	public static final String BSE000076 = "BSE000076".intern();

	/**
	 * error in version data format
	 */
	public static final String BSE000077 = "BSE000077".intern();

	/**
	 * Cannot have ToDate without having From Date.
	 */
	public static final String BSE000078 = "BSE000078".intern();

	/**
	 * Testing version fail message
	 */
	public static final String BSE000079 = "BSE000079".intern();

	/**
	 * Input parameters validation information at Query Editor.
	 */
	public static final String BSE000080 = "BSE000080".intern();

	/**
	 * Input parameters validation information at Query Editor.
	 */
	public static final String BSE000081 = "BSE000081".intern();

	/**
	 * checksum mismatch for model library import
	 */
	public static final String BSE000083 = "BSE000083".intern();

	/**
	 * mapping import error
	 */
	public static final String BSE000084 = "BSE000084".intern();

	/**
	 * Error during inserting records for syndicate data.
	 */
	public static final String BSE000085 = "BSE000085".intern();

	/**
	 * mapping doesnot exists
	 */
	public static final String BSE000086 = "BSE000086".intern();

	/**
	 * Version Not found with the Tenant Model name and Version
	 */
	public static final String BSE000087 = "BSE000087".intern();

	/**
	 * Error during encryption.
	 */
	public static final String BSE000088 = "BSE000088".intern();

	/**
	 * Error during Tenant Input file content is corrupted
	 */
	public static final String BSE000089 = "BSE000089".intern();

	/**
	 * model library record does not exists
	 */
	public static final String BSE000090 = "BSE000090";

	/**
	 * Error during converting tenant input to JSON
	 */
	public static final String BSE000091 = "BSE000091".intern();

	/**
	 * Error when transaction not found with the txnId
	 */
	public static final String BSE000092 = "BSE000092".intern();

	/**
	 * Error when it found same checksum in DB
	 */
	public static final String BSE000093 = "BSE000093".intern();
	/**
	 * Error when admin updates tenant with out creating the tenant
	 */
	public static final String BSE000094 = "BSE000094".intern();

	/**
	 * Error when it found same checksum in DB
	 */
	public static final String BSE000095 = "BSE000095".intern();

	/**
	 * To identify incorrect excel file upload for test bed.
	 */
	public static final String BSE000096 = "BSE000096".intern();
	public static final String BSE000996 = "BSE000996".intern();

	/**
	 * To indicate Output mapping is not saved before finalizing input mapping
	 */
	public static final String BSE000097 = "BSE000097";
	/**
	 * If any exceptions thrown while getting the records based on the batch id
	 */
	public static final String BSE000098 = "BSE000098".intern();

	/**
	 * If any exceptions thrown execution of batch
	 */
	public static final String BSE000099 = "BSE000099".intern();

	/**
	 * If no plugins are found
	 */
	public static final String BSE000100 = "BSE000100".intern();

	/**
	 * If no plugins mapped for tenant
	 */
	public static final String BSE000101 = "BSE000101".intern();

	/**
	 * If tenant type changed from both to other
	 */
	public static final String BSE000102 = "BSE000102".intern();

	/**
	 * If Batch deployment failed
	 */
	public static final String BSE000103 = "BSE000103".intern();
	/**
	 * If Batch Undeployment failed
	 */
	public static final String BSE000104 = "BSE000104".intern();
	/**
	 * If Batch deployment failed and havinng the error message
	 */
	public static final String BSE000105 = "BSE000105".intern();
	/**
	 * If Batch undeployment failed and havinng the error message
	 */
	public static final String BSE000106 = "BSE000106".intern();

	/**
	 * Failure in file movement during file invalidation
	 */
	public static final String BSE000107 = "BSE000107".intern();

	/**
	 * Failure while creation of folder in batch folder
	 */
	public static final String BSE000108 = "BSE000108".intern();

	/**
	 * Failure while fetching value for System Key
	 */
	public static final String BSE000109 = "BSE000109".intern();

	/**
	 * Error in reading container definition file.
	 */
	public static final String BSE000110 = "BSE000110".intern();

	/**
	 * The column names in the key definition doesn't match the column names defined for the table.
	 */
	public static final String BSE000111 = "BSE000111".intern();

	/**
	 * The nullable row (5th row) has a invalid value defined.
	 */
	public static final String BSE000112 = "BSE000112".intern();

	/**
	 * The double size is not defined properly. Use "|" to separate the whole number from decimal. Ex: (10|2)
	 */
	public static final String BSE000113 = "BSE000113".intern();

	/**
	 * The double size & precision should be defined as integers.
	 */
	public static final String BSE000114 = "BSE000114".intern();

	/**
	 * The size format is defined for double type, whereas the type defined is something else.
	 */
	public static final String BSE000115 = "BSE000115".intern();

	/**
	 * Invalid size format. Only integers allowed.
	 */
	public static final String BSE000116 = "BSE000116".intern();

	/**
	 * Invalid column data type defined. The allowed values are : Integer, String, Double, Date & Boolean.
	 */
	public static final String BSE000117 = "BSE000117".intern();

	/**
	 * Incorrect definition loaded. The definition doesn't match the data.
	 */
	public static final String BSE000118 = "BSE000118".intern();

	/**
	 * Table structure is inconsistent. Check whether data in some row is missing or in excess.
	 */
	public static final String BSE000119 = "BSE000119".intern();

	/**
	 * The precision size cannot exceed the whole size defined for double.
	 */
	public static final String BSE000120 = "BSE000120".intern();

	/**
	 * The column cannot have spaces or special characters or can be greater than 64 characters.
	 */
	public static final String BSE000121 = "BSE000121".intern();

	/**
	 * The description length cannot be empty or exceed 200 characters.
	 */
	public static final String BSE000122 = "BSE000122".intern();

	/**
	 * To identify incorrect json file upload for bulk bed.
	 */
	public static final String BSE0000151 = "BSE0000151".intern();
	/**
	 * To identify incorrect json file formet for bulk bed.
	 */
	public static final String BSE0000152 = "BSE0000152".intern();

	/**
	 * Authentication Exception
	 */
	public static final String BSE000812 = "BSE000812".intern();

	/**
	 * If any exceptions thrown while getting the records based on the filter criteria for the excel reports
	 */
	public static final String BSE0000111 = "BSE0000111".intern();

	/**
	 * Error in updating syndicate data queries.
	 */
	public static final String BSE000123 = "BSE000123".intern();

	/**
	 * If any exception whilea converting datein download syndicate data
	 */
	public static final String BSE000124 = "BSE0001124".intern();

	/**
	 * If any exception occurs when getting transactions with the search criteria
	 */
	public static final String BSE000126 = "BSE000126".intern();

	/**
	 * Duplicate column names found in the container definition.
	 */
	public static final String BSE000127 = "BSE000127".intern();

	/**
	 * Exception when duplicate columns are present in syndicate data file
	 */
	public static final String BSE000128 = "BSE000128".intern();

	/**
	 * Key Validation Failed.
	 */
	public static final String BSE000129 = "BSE000129".intern();

	public static final String BSE000130 = "BSE000130".intern();

	/**
	 * Version name cannot be empty.
	 */
	public static final String BSE000131 = "BSE000131".intern();

	/**
	 * Exception during fetching metrics for a version.
	 */
	public static final String BSE000132 = "BSE000132".intern();

	/**
	 * Exception while getting version and model
	 */
	public static final String BSE000133 = "BSE000133".intern();
	/**
	 * error code in case of exception while converting excel to xml
	 */
	public static final String BSE000134 = "BSE000134".intern();

	/**
	 * error code in case of modelexecution environment is not present in db
	 */
	public static final String BSE000135 = "BSE000135".intern();

	public static final String BSE000136 = "BSE000136".intern();

	/**
	 * Error code while checking the mediatemodellibrary using checksum and tarName
	 */
	public static final String BSE000137 = "BSE000137".intern();

	public static final String BSE000137_1 = "BSE000137_1".intern();

	/**
	 * Error code when duplicate Mediate library found using checksum and tarName
	 */
	public static final String BSE000138 = "BSE000138".intern();

	/**
	 * Error code when duplicate Model library found using checksum and tarName
	 */
	public static final String BSE000139 = "BSE000139".intern();
	/**
	 * Error code when inserting Mediate model library into DB
	 */
	public static final String BSE000140 = "BSE000140".intern();

	/**
	 * Error while extracting tar.gz file for DESCRIPTION
	 */
	public static final String BSE000141 = "BSE000141".intern();

	/**
	 * Error while extracting tar.gz file for DESCRIPTION
	 */
	public static final String BSE000142 = "BSE000142".intern();

	/**
	 * Error while setting execution environment names from db to hazelcast
	 */
	public static final String BSE000143 = "BSE000143".intern();

	/**
	 * Error while creating default roles for Tenant
	 */
	public static final String BSE000144 = "BSE000144".intern();

	/**
	 * Error while start/stop Modelet
	 */
	public static final String BSE000201 = "BSE000201".intern();

	/**
	 * Refresh Modelet Allocation Failed
	 */
	public static final String BSE000202 = "BSE000202".intern();

	/**
	 * Fetch Modelet command result Failed
	 */
	public static final String BSE000203 = "BSE000203".intern();

	/**
	 * Fetch Modelet logs failed
	 */
	public static final String BSE000204 = "BSE000204".intern();

	/**
	 * Restart Moelet(s) failed
	 */
	public static final String BSE000205 = "BSE000205".intern();


	/**
	 * Error in Mongo query creation
	 */
	public static final String BSE000501 = "BSE000501".intern();

	/**
	 * corrupt file uploaded in syndicate data upload
	 */
	public static final String BSE000555 = "BSE000555".intern();

	public static final String BSE000556 = "BSE000556".intern();

	public static final String BSE000651 = "BSE000651".intern();
	public static final String BSE000652 = "BSE000652".intern();

	// version container builder error codes
	public static final String BSE000601 = "BSE000601".intern();

	public static final String BSE000602 = "BSE000602".intern();

	public static final String BSE000603 = "BSE000603".intern();

	/**
	 * Error code for related to user login issues
	 */
	public static final String BSE000502 = "BSE000502".intern();

	/**
	 * If any exceptions thrown while getting the records based on the filter criteria for the Usage reports and Usage Grid
	 */
	public static final String BSE0000503 = "BSE0000503".intern();

	public static final String BSE0000504 = "BSE0000504".intern();

	public static final String BSE0000505 = "BSE0000505".intern();

	public static final String BSE0000506 = "BSE0000506".intern();

	public static final String BSE0000510 = "BSE0000510".intern();

	public static final String BSE0000511 = "BSE0000511".intern();

	public static final String BSE0000512 = "BSE0000512".intern();

	public static final String BSE0000513 = "BSE0000513".intern();

	public static final String BSE0000514 = "BSE0000514".intern();

	public static final String BSE0000515 = "BSE0000515".intern();

	/**
	 * If any exceptions thrown while getting the records based on the filter criteria for the Batch Usage reports and Usage Grid
	 */

	public static final String BSE0000603 = "BSE0000603".intern();

	/**
	 * Message to display nuber of records count is more than the defined limit
	 */
	public static final String BSE000700 = "BSE000700".intern();

	/**
	 * Message for timeout during mongo query on transaction dashboard
	 */
	public static final String BSE000701 = "BSE000701".intern();

	public static final String BSE000702 = "BSE000702".intern();

	public static final String BSE0000810 = "BSE0000810".intern();
	public static final String BSE0000820 = "BSE0000820".intern();

	/* Error code for external API */
	public static final String BSE000751 = "BSE000751".intern();
	public static final String BSE000752 = "BSE000752".intern();
	public static final String BSE000753 = "BSE000753".intern();
	public static final String BSE000754 = "BSE000754".intern();
	public static final String BSE000755 = "BSE000755".intern();
	public static final String BSE000756 = "BSE000756".intern();
	public static final String BSE000757 = "BSE000757".intern();
	public static final String BSE000758 = "BSE000758".intern();
	public static final String BSE000759 = "BSE000759".intern();
	public static final String BSE000760 = "BSE000760".intern();
	public static final String BSE000761 = "BSE000761".intern();
	public static final String BSE000762 = "BSE000762".intern();

	public static final String BSE000653 = "BSE000653".intern();
	public static final String BSE000654 = "BSE000654".intern();
	public static final String BSE000655 = "BSE000655".intern();
	public static final String BSE000656 = "BSE000656".intern();

	public static final String BSE000200 = "BSE000200".intern();

	public static final String BSE000301 = "BSE000301".intern();
	public static final String BSE000302 = "BSE000302".intern();
	public static final String BSE000303 = "BSE000304".intern();
	public static final String BSE000304 = "BSE000304".intern();
	public static final String BSE000305 = "BSE000305".intern();
	public static final String BSE000306 = "BSE000306".intern();
	public static final String BSE000307 = "BSE000307".intern();
	public static final String BSE000308 = "BSE000308".intern();
	public static final String BSE000309 = "BSE000309".intern();
	public static final String BSE000310 = "BSE000310".intern();

	public static final String BSE000377 = "BSE000377".intern();
	public static final String BSE000900 = "BSE000900".intern();
	public static final String BSE000901 = "BSE000901".intern();
	public static final String BSE000902 = "BSE000902".intern();
	public static final String BSE000903 = "BSE000903".intern();
	public static final String BSE000904 = "BSE000904".intern();
	public static final String BSE000905 = "BSE000905".intern();

	public static final String BSE000401 = "BSE000401".intern();
	public static final String BSE000402 = "BSE000402".intern();
	public static final String BSE000403 = "BSE000403".intern();
	public static final String BSE000404 = "BSE000404".intern();
	public static final String BSE000405 = "BSE000405".intern();
	public static final String BSE000406 = "BSE000406".intern();

	public static final String BSE000407 = "BSE000407".intern();

	public static final String BSE001001 = "BSE001001".intern();
	public static final String BSE001002 = "BSE001002".intern();
	public static final String BSE001003 = "BSE001003".intern();
	public static final String BSE001004 = "BSE001004".intern();
	public static final String BSE001005 = "BSE001005".intern();
	public static final String BSE001006 = "BSE001006".intern();
	public static final String BSE001007 = "BSE001007".intern();
	public static final String BSE001008 = "BSE001008".intern();
	public static final String BSE001009 = "BSE001009".intern();
	public static final String BSE001010 = "BSE001010".intern();
	public static final String BSE001011 = "BSE001011".intern();
	public static final String BSE001012 = "BSE001012".intern();
	public static final String BSE001013 = "BSE001013".intern();
	public static final String BSE001014 = "BSE001013".intern();
	public static final String BSE001015 = "BSE001015".intern();
	public static final String BSE001016 = "BSE001016".intern();
	public static final String BSE001017 = "BSE001017".intern();
	public static final String BSE001018 = "BSE001018".intern();
	public static final String BSE001019 = "BSE001019".intern();
	public static final String BSE001020 = "BSE001020".intern();
	public static final String BSE001021 = "BSE001021".intern();
	public static final String BSE001022 = "BSE001022".intern();
	public static final String BSE001023 = "BSE001023".intern();
	public static final String BSE001024 = "BSE001024".intern();
	public static final String BSE001025 = "BSE001025".intern();

	private BusinessExceptionCodes() {

	}
}
