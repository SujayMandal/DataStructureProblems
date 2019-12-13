package com.fa.dp.business.constant;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public interface DPAConstants {

	public static final String CLASSIFICATION_FILTER = "week0RRClassification";
	public static final String DUPLICATE_FILTER = "week0DuplicateFilter";
	public static final String INVESTOR_FILTER = "week0InvestorCodeFilter";
	public static final String UNSUPPORTEDASSET_FILTER = "week0AssetValueFilter";
	public static final String UNSUPPORTEDPROP_FILTER = "week0PropertyTypeFilter";
	public static final String FAILED_REAL_RESOL_REAL_FILTER = "week0RRRtngAggregator";
	public static final String RA_FILTER = "week0RAInputPayload";
	public static final String PROCESSED_FILTER = "processdList";
	public static final String WEEK0 = "week0";
	public static final String WEEKN = "weekn";
	public static final String OCN_OUTPUT_APPENDER = "_output_OCN.xlsx";
	public static final String NRZ_OUTPUT_APPENDER = "_output_NRZ.xlsx";
	public static final String PHH_OUTPUT_APPENDER = "_output_PHH.xlsx";
	public static final String OCN = "OCN";
	public static final String NRZ = "NRZ";
	public static final String DATA_FETCH_FAILURE = "weekNFetchData";
	public static final String ACTIVE_LISTINGS_FILTER = "weekNActiveListingsFilter";
	public static final String SUCCESSFUL_UNDERREVIEW_FILTER = "weekNSuccessfulUnderreviewFilter";
	public static final String PAST12_CYCLES_FILTER = "weekNPast12CyclesFilter";
	public static final String ODD_LISTING_FILTER = "weekNOddListingsFilter";
	public static final String ASSIGNMNT_FILTER = "weekNAssignmentFilter";
	public static final String UNSUPPORTED_STATE_OR_ZIP = "weekNZipStateFilter";
	public static final String SS_AND_PMI = "weekNSSPmiFilter";
	public static final String SOP = "weekNSOPFilter";
	public static final String RA_FAIL_FILTER = "weekNRAIntegrarion";
	public static final String ODD_PROPERTIES_FILTER = "weekNOddListingsFilter";
	public static final String PASSED_12_CYCLES_FILTER = "weekNPast12CyclesFilter";
	public static final String UNABLE_TO_DOWNLOAD_FILE = "Unable to generate file.Please contact administrator";
	public static final String OUTPUTSTREAM_ERROR = "Unable to close response for file output stream : ";
	public static final String PROCESSED_RECORDS = "Recommendation List";
	public static final String SHEET_SOP_WEEK0_DB = "SOP_Week0_DB";
	public static final String FAILED_RECORDS = "Exclusion List";
	public static final String PASSED_12_CYCLES = "Properties Past 12 Cycles";
	public static final String PRIOR_RECOMMENDED = "Prior Recommended";
	public static final String SUCCESS_UNDERREVIEW = "Successful Or Underreview";
	public static final String SOP_DUPLICATE_FILTER = "sopWeek0DuplicateFilter";
	public static final String SOP_ASSETVALUE_FILTER = "sopWeek0AssetValueFilter";
	public static final String SOP_WEEK0 = "sopWeek0";
	public static final String SOP_WEEKN = "sopWeekn";
	public static final String SOPWEEKN_ACTIVE_LISTINGS_FILTER = "sopWeekNActiveListingsFilter";
	public static final String SOPWEEKN_PAST12_CYCLES_FILTER = "sopWeekNPast12CyclesFilter";
	public static final String SOPWEEKN_SUCCESSFUL_UNDERREVIEW_FILTER = "sopWeekNSuccessfulUnderreviewFilter";
	public static final String SOPWEEKN_ASSIGNMNT_FILTER = "sopWeekNAssignmentFilter";
	public static final String SOPWEEKN_ODD_LISTING_FILTER = "sopWeekNOddListingsFilter";
	public static final String SOPWEEKN_SS_AND_PMI = "sopWeekNSSPmiFilter";
	public static final String SOPWEEKN_VACANT_FILTER = "sopWeekNVacantFilter";
	public static final String SOPWEEKN_RA_FAIL_FILTER = "sopWeekNRAIntegrarion";
	public static final String SOPWEEKN_UNSUPPORTED_STATE_OR_ZIP = "sopWeekNStateFilter";
	public static final String SOPWEEKN_DATA_FETCH_FAILURE = "sopWeekNFetchData";
	public static final String SOPWEEKN_SOPFILTER = "sopWeekNSOPFilter";
	public static final String OUT_OF_SCOPE = "Out of scope";
	public static final String PHH = "PHH";
	public static final String PHH_ACNT_ID = "891";
	public static final String NRZ_ACNT_ID = "900";
	public static final String OCN_ACNT_ID = "000";
	public static final String ACTIVE = "ACTIVE";
	public static final String NULL = "null";

	public static final Map<String, String> ACCNT_ID_CLASSIFICATION_MAP = Collections.unmodifiableMap(new HashMap() {{
		put(OCN_ACNT_ID, DPProcessParamAttributes.OCN.getValue());
		put(NRZ_ACNT_ID, DPProcessParamAttributes.NRZ.getValue());
		put(PHH_ACNT_ID, DPProcessParamAttributes.PHH.getValue());
	}});

	public static final String LIST_WKN_OCN = "listOfDPProcessWeekNParamOCN";
	public static final String LIST_WKN_NRZ = "listOfDPProcessWeekNParamNRZ";
	public static final String LIST_WKN_PHH = "listOfDPProcessWeekNParamPHH";
	public static final String LIST_SCCS_UDR_OCN = "listOfSuccessUnderReviewOCN";
	public static final String LIST_SCCS_UDR_NRZ = "listOfSuccessUnderReviewNRZ";
	public static final String LIST_SCCS_UDR_PHH = "listOfSuccessUnderReviewPHH";

	public static final String CONSOLIDATED_REPORT = "CONSOLIDATED REPORT";
	public static final String VACANT = "Vacant";
	public static final String OCCUPIED = "SOP";
	public static final String YES = "Yes";
	public static final String NO = "No";
	public static final String LIST_END_DATE_DT_NN_CONDITION = "LIST_END_DATE_DT_NN_CONDITION";
	public static final String REPLACED_LIST_END_DATE_CONDITION = "AND  TO_DATE(CURRENT_LIST_END_DATE) <= TO_DATE(:endDateCondition)";
	public static final String REPLACED_LIST_END_DATE_CONDITION_WITHOUT_EQUALS = "AND  TO_DATE(CURRENT_LIST_END_DATE) < TO_DATE(:endDateCondition)";

	public static final String WEEK0_SOP_DUPLICATE_FILTER = "sopWeek0DuplicateFilter";
	public static final String WEEK0_SOP_ASSETVALUE_FILTER = "sopWeek0AssetValueFilter";
	public static final String NRZ_SOP_DUPLICATE_FILTER = "nrzSopWeek0DuplicateFilter";
	public static final String NRZ_SOP_ASSETVALUE_FILTER = "nrzSopWeek0AssetValueFilter";
	public static final String NRZ_SOP_WEEK0_MODELED_BENCHMARK_CRITERIA = "nrzSopWeek0ModeledBenchmarkCriteria";
	public static final String OCN_SOP_DUPLICATE_FILTER = "ocnSopWeek0DuplicateFilter";
	public static final String OCN_SOP_ASSETVALUE_FILTER = "ocnSopWeek0AssetValueFilter";
	public static final String OCN_SOP_WEEK0_MODELED_BENCHMARK_CRITERIA = "ocnSopWeek0ModeledBenchmarkCriteria";
	public static final String PHH_SOP_DUPLICATE_FILTER = "phhSopWeek0DuplicateFilter";
	public static final String PHH_SOP_ASSETVALUE_FILTER = "phhSopWeek0AssetValueFilter";
	public static final String PHH_SOP_WEEK0_MODELED_BENCHMARK_CRITERIA = "phhSopWeek0ModeledBenchmarkCriteria";

	public static final String OCN_SOPWEEKN_SUCCESSFUL_UNDERREVIEW_FILTER = "ocnSOPWeekNSuccessfulUnderreviewFilter";
	public static final String OCN_SOPWEEKN_ACTIVE_LISTINGS_FILTER = "ocnSOPWeekNActiveListingsFilter";
	public static final String OCN_SOPWEEKN_ODD_LISTINGS_FILTER = "ocnSOPWeekNOddListingsFilter";
	public static final String OCN_SOPWEEKN_OUTPUT_FILE_CREATE = "ocnSOPWeekNOutputFileCreate";
	public static final String OCN_SOPWEEKN_PAST_12_CYCLES_FILTER = "ocnSOPWeekNPast12CyclesFilter";
	public static final String OCN_SOPWEEKN_RAINTEGRARION = "ocnSOPWeekNRAIntegrarion";
	public static final String OCN_SOPWEEKN_SOPFILTER = "ocnSOPWeekNSOPFilter";
	public static final String OCN_SOPWEEKN_SSPMI_FILTER = "ocnSOPWeekNSSPmiFilter";
	public static final String OCN_SOPWEEKN_ZIP_STATE_FILTER = "ocnSOPWeekNZipStateFilter";
	public static final String OCN_SOPWEEKN_ASSIGNMENT_FILTER = "ocnSOPWeekNAssignmentFilter";

	public static final String PHH_SOPWEEKN_SUCCESSFUL_UNDERREVIEW_FILTER = "phhSOPWeekNSuccessfulUnderreviewFilter";
	public static final String PHH_SOPWEEKN_ACTIVE_LISTINGS_FILTER = "phhSOPWeekNActiveListingsFilter";
	public static final String PHH_SOPWEEKN_ODD_LISTINGS_FILTER = "phhSOPWeekNOddListingsFilter";
	public static final String PHH_SOPWEEKN_OUTPUT_FILE_CREATE = "phhSOPWeekNOutputFileCreate";
	public static final String PHH_SOPWEEKN_PAST_12_CYCLES_FILTER = "phhSOPWeekNPast12CyclesFilter";
	public static final String PHH_SOPWEEKN_RAINTEGRARION = "phhSOPWeekNRAIntegrarion";
	public static final String PHH_SOPWEEKN_SOPFILTER = "phhSOPWeekNSOPFilter";
	public static final String PHH_SOPWEEKN_SSPMI_FILTER = "phhSOPWeekNSSPmiFilter";
	public static final String PHH_SOPWEEKN_ZIP_STATE_FILTER = "phhSOPWeekNZipStateFilter";
	public static final String PHH_SOPWEEKN_ASSIGNMENT_FILTER = "phhSOPWeekNAssignmentFilter";

	public static final String NRZ_SOPWEEKN_SUCCESSFUL_UNDERREVIEW_FILTER = "nrzSOPWeekNSuccessfulUnderreviewFilter";
	public static final String NRZ_SOPWEEKN_ACTIVE_LISTINGS_FILTER = "nrzSOPWeekNActiveListingsFilter";
	public static final String NRZ_SOPWEEKN_ODD_LISTINGS_FILTER = "nrzSOPWeekNOddListingsFilter";
	public static final String NRZ_SOPWEEKN_OUTPUT_FILE_CREATE = "nrzSOPWeekNOutputFileCreate";
	public static final String NRZ_SOPWEEKN_PAST_12_CYCLES_FILTER = "nrzSOPWeekNPast12CyclesFilter";
	public static final String NRZ_SOPWEEKN_RAINTEGRARION = "nrzSOPWeekNRAIntegrarion";
	public static final String NRZ_SOPWEEKN_SOPFILTER = "nrzSOPWeekNSOPFilter";
	public static final String NRZ_SOPWEEKN_SSPMI_FILTER = "nrzSOPWeekNSSPmiFilter";
	public static final String NRZ_SOPWEEKN_ZIP_STATE_FILTER = "nrzSOPWeekNZipStateFilter";
	public static final String NRZ_SOPWEEKN_ASSIGNMENT_FILTER = "nrzSOPWeekNAssignmentFilter";

	// Empty File validation
	public static final String EMPTY_FILE = "Input cannot be null";
	public static final String SUCCESS = "SUCCESS";
	public static final String FAIL = "FAIL";
	public static final String BLANK = "";
	public static final String SOP_WEEK0DB_INITIAL = "SOP_WEEK0DB_INITIAL";
	public static final String PROP_TEMP = "PROP_TEMP";
	public static final String OLD_RR_LOAN_NUM = "OLD_RR_LOAN_NUM";
	public static final String LOAN_NUM = "LOAN_NUM";
	public static final String SOP_WEEKN_FILE_NAME = "SOP_Week_N_Potential_";
	public static final String FILE_SOP_WEEKN = "Sop_WeekN_";

	public static final String SOP_WEEKN_PROCESS_REPORT_FOLDER = "sopWeekNProcessReport";
	public static final String ZIP_EXTENSION = ".zip";
}
