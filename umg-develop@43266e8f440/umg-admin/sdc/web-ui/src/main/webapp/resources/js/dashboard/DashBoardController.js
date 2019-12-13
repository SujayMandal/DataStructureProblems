/**
 * 
 */
'use strict';
var DashBoardController = /**
 * @param $scope
 * @param $log
 * @param $timeout
 * @param $stateParams
 * @param $location
 * @param $filter
 * @param $window
 * @param $http
 * @param dashboardService
 * @param sharedPropertiesService
 */
function($scope, $log, $timeout, $stateParams, $location, $filter, $window, $http, dashboardService, sharedPropertiesService, $dialogs) {
	$scope.sysAdmin = sysAdmin;
	$scope.flag_tenantIoDownload = (pages.indexOf("Dashboard.Transaction.DownloadTenantIO") == -1 && buttonArray.indexOf("tenantIoDownload") > -1 ) ? true : false;
	$scope.flag_modelIoDownload = (pages.indexOf("Dashboard.Transaction.DownloadModelIO") == -1 && buttonArray.indexOf("modelIoDownload") > -1 ) ? true : false;
	$scope.flag_reportGeneration = (pages.indexOf("Dashboard.Transaction.DownloadReport") == -1 && buttonArray.indexOf("reportGeneration") > -1 ) ? true : false;
	$scope.flag_testBedRedirect = (pages.indexOf("Dashboard.Transaction.Re-run") == -1 && buttonArray.indexOf("testBedRedirect") > -1 )? true : false;
	$scope.defaultFlag = true;
	$scope.searchDisable = false;
	$scope.libraryNames = [];
	$scope.tenantModelNames = [];
	$scope.myData = [];
	$scope.selectedItems = [];
	$scope.versionDetails;
	$scope.transactionStatusList=["","Success","Failure"];
	$scope.transactionTypeList=["","Prod","Test"];
	$scope.transactionModeList=["","Online","Batch"];
	$scope.executionGroupList=["","Benchmark","Modeled","Ineligible"];
	$scope.searchTypeList=["Default Search","Model Name","Transaction Type","Transaction Status","Tenant Transaction Id","RA Transaction Id"];
	$scope.selectAllChecked = false;
	$scope.showUsageReport = false;
	$scope.selectAllDwnldUsgRprt = false;
	$scope.totalSearchedTransactions;
	$scope.allOnlineTransModelNamesForRerun = [];
	$scope.allOnlineTranIdsForRerun = [];
	$scope.selectionRecordCntLimit;
	
	// refer this link for regex: http://stackoverflow.com/questions/9038522/regular-expression-for-any-number-greater-than-0
	var doubleFormat = /^0*[1-9][0-9]*(\.[0-9]+)?/;

	function defaultInit(){
		//$scope.searchFlag = true;
		$scope.searchDisable = false;
		$scope.filter = {
				clientTransactionID : "",
				raTransactionID : "",
				libraryName : "",
				majorVersion : undefined,
				minorVersion : undefined,
				runAsOfDateFromString : "",
				runAsOfDateToString : "",
				tenantModelName : "",
				fullVersion : "",
				runAsOfDateFrom : undefined,
				runAsOfDateTo : undefined,
				transactionType : "",
				transactionStatus : "",
				errorType:"",
				errorDescription:"",
				transactionMode : "",
				batchId:"",
				executionGroup:"",
				searchType:$scope.searchTypeList[0]
		};
		
		$scope.flags = {
				clientTransactionID : true,
				raTransactionID : true,
				runAsOfDateFromString : true,
				runAsOfDateToString : true,
				tenantModelName : true,
				fullVersion : true,
				transactionType : true,
				transactionStatus : true,
				errorType:true,
				errorDescription:true,
		}
		
		$scope.modelNameMndt = false;
		$scope.majorVersionMndt = false;
		$scope.transactionStatusMndt = false;
		$scope.datetimepicker1Mndt = false;
		$scope.datetimepicker2Mndt = false;
		$scope.searchTypeMndt = false;
		$scope.clientTransactionIDMndt = false;
		$scope.raTransactionIDMndt = false;
		$scope.errorTypeMndt = false;
		$scope.errorDescriptionMndt = false;
		$scope.transactionTypeMndt = false;
		$scope.showMessage = false;
		$scope.errorMessage = "";
	}
	
	defaultInit();
	
	$( "#runAsOfDateFromString")
	  .mouseleave(function() {
		  $scope.filter.runAsOfDateFromString = $( "#runAsOfDateFromString").val();
	  })
	
	$( "#runAsOfDateFromString")
	  .focusout(function() {
		  $scope.filter.runAsOfDateFromString = $( "#runAsOfDateFromString").val();
	  })
	
	$( "#runAsOfDateToString")
	  .mouseleave(function() {
		  $scope.filter.runAsOfDateToString = $( "#runAsOfDateToString").val();
	  })
	  
	$( "#runAsOfDateToString")
	  .focusout(function() {
		  $scope.filter.runAsOfDateToString = $( "#runAsOfDateToString").val();
	  })
	  
	//optionSwitch events
	$scope.searchChange = function(value){
		defaultInit();
		$scope.TransactionDashboardForm.$setPristine();
		$scope.filter.searchType = value;
		$scope.searchDisable = true;
		switch(value) {
	    case "Model Name":
	    	//mandatory and optional fields enable flag
	    	$scope.flags.tenantModelName = false;
	    	$scope.flags.transactionType = false;
	    	$scope.flags.runAsOfDateFromString = false;
	    	$scope.flags.runAsOfDateToString = false;
	    	$scope.flags.fullVersion = false;
	    	$scope.flags.transactionStatus = false;
	    	//mandatory fields flag
			$scope.modelNameMndt = true;
			$scope.transactionTypeMndt = true;
			$scope.datetimepicker1Mndt = true;
			$scope.datetimepicker2Mndt = true;
	        break;
	    case "Transaction Type":
	    	//mandatory and optional fields enable flag
	    	$scope.flags.transactionType = false;
	    	$scope.flags.runAsOfDateFromString = false;
	    	$scope.flags.runAsOfDateToString = false;
	    	$scope.flags.transactionStatus = false;
	    	//mandatory fields flag
			$scope.transactionTypeMndt = true;
			$scope.datetimepicker1Mndt = true;
			$scope.datetimepicker2Mndt = true;
	        break;
	    case "Transaction Status":
	    	//mandatory and optional fields enable flag
	    	$scope.flags.transactionType = false;
	    	$scope.flags.runAsOfDateFromString = false;
	    	$scope.flags.runAsOfDateToString = false;
	    	$scope.flags.transactionStatus = false;
	    	$scope.flags.errorType = false;
	    	$scope.flags.errorDescription = false;
	    	//mandatory fields flag
			$scope.transactionTypeMndt = true;
			$scope.transactionStatusMndt = true;
			$scope.datetimepicker1Mndt = true;
			$scope.datetimepicker2Mndt = true;
	        break;
	    case "Tenant Transaction Id":
	    	//mandatory and optional fields enable flag
	    	$scope.flags.clientTransactionID = false;
	    	$scope.flags.runAsOfDateFromString = false;
	    	$scope.flags.runAsOfDateToString = false;
	    	//mandatory fields flag
			$scope.clientTransactionIDMndt = true;
			$scope.datetimepicker1Mndt = true;
			$scope.datetimepicker2Mndt = true;
	        break;
	    case "RA Transaction Id":
	    	//mandatory and optional fields enable flag
	    	$scope.flags.raTransactionID = false;
	    	$scope.flags.runAsOfDateFromString = false;
	    	$scope.flags.runAsOfDateToString = false;
	    	//mandatory fields flag
			$scope.raTransactionIDMndt = true;
			$scope.datetimepicker1Mndt = true;
			$scope.datetimepicker2Mndt = true;
	        break;   
	     default :
	    	$scope.searchDisable = false;
		}
	};

	var loadTransactions = null;
	$scope.grid={};
	$scope.grid.pagingOptions = {
			pageSizes: [100, 500, 1000, 2500, 5000],
			pageSize: 500,
			currentPage: 1,
			totalPages:1,
			totalServerItems:0,
			sortColumn:"",
			descending:true
	};

	$scope.ErrorTypeList=[
	                      {code:"",displayName:""},
	                      {code:"validation",displayName:"Validation"},
	                      {code:"systemException",displayName:"System Exception"},
	                      {code:"modelException",displayName:"Model Exception"}
	                      ];

	$scope.intialSetup = function() {
		// for draggable for alert box 
		$(function() {
			$( ".draggable" ).draggable();
		});

		$scope.ViewDashaboardVersion = {
				umgLibraryName : "",
				umgModelName : "",
				umgTidName : "",
				status : "",
				publishedOn : "",
				publishedBy : ""
		};
		$scope.showDashaboardVersion = false;
		$scope.showMessage = false;
		$scope.searchResultMessage = '';
		$scope.searchResultMessageWarnning = false;
		$scope.totalCount = '';
		$scope.showNote = false;
	};
	
	// for setting the record count limit 
	$scope.setSelectionRecordLimit = function () {
		dashboardService.getSelectedRecordsCountLimit().then(
			function(responseData) {
				if (responseData.error) {
					alert(" ErrorCode: " + responseData.errorCode
							+ " \n Error in retrieving the operator list : "
							+ responseData.message);
				} else {
					$scope.selectionRecordCntLimit = responseData.response;
				}
			}, function(responseData) {
				alert('Failed: ' + responseData);
			}
		);
	};
	
	$scope.setSelectionRecordLimit();

	//for advanced search 
	$scope.showAdvSrch = false;
	$scope.clause1Empty = false;
	$scope.clause2Empty = false;
	$scope.setAdvSearchParams = function () {
		$scope.operatorList = [];
		$scope.inputTypeMap=[];
		$scope.showAdvSrch = false;
		$scope.showAdvSrchSecondRow = false;
		$scope.clause1Empty = false;
		$scope.clause2Empty = false;
		$scope.showAdvfilterCriteria1 = false;
		$scope.showAdvfilterCriteria2 = false;
		$scope.hideRemoveButton = false;
		$scope.filter.transactionType = "";
		$scope.filter.transactionMode = "";
		$scope.filter.executionGroup = "";
		$scope.rowCount = 0;
		$scope.Advfilter = {
				key1 : "",
				searchIn1 : "",
				parameter1 : "",
				operator1 : "",
				value1 : "",
				key2 : "",
				searchIn2 : "",
				parameter2 : "",
				operator2 : "",
				value2 : "",
				qryCriteria : ""
		};
	};

	$scope.setAdvSearchParams ();

	//event for showing and hiding advance search fields
	$scope.showAdvanceSearch = function () {
		$scope.clause1Empty = false;
		$scope.clause2Empty = false;
		if ($scope.showAdvSrch == false) {
			$scope.showAdvSrch = true;
			$scope.showAdvfilterCriteria1 = false;
			$scope.showAdvfilterCriteria2 = false;
			$scope.hideRemoveButton = false;
			$scope.rowCount = 0;
			 $scope.inputTypeMap = [
	              { id: 'tenantInput.data.', name: 'Tenant Input' },
	              { id: 'tenantOutput.data.', name: 'Tenant Ouput' }];
		} else {
			$scope.showAdvSrch = false;
			$scope.setAdvSearchParams ();
		}
	};
	
	$scope.showAdvanceSearch();
	
	//function for add field button in advanced search
	$scope.showAdvanceSearchCrit = function () {
		if ($scope.rowCount == 0) {
			$scope.showAdvfilterCriteria1 = true;
			$scope.rowCount = 1;
			$scope.Advfilter.searchIn1 = 'tenantInput.data.';
			dashboardService.getOperatorList().then(
				function(responseData) {
					if (responseData.error) {
						alert(" ErrorCode: " + responseData.errorCode
								+ " \n Error in retrieving the operator list : "
								+ responseData.message);
					} else {
						$scope.operatorList = responseData.response;
					}
				}, function(responseData) {
					alert('Failed: ' + responseData);
				}
			);
		} else {
			$scope.showAdvfilterCriteria2 = true;
			$scope.rowCount = 2;
			$scope.hideRemoveButton = true;
			$scope.Advfilter.qryCriteria = 'AND';
			$scope.Advfilter.searchIn2 = 'tenantInput.data.';
		}
		
	};
	
	//function for remove field button for added row in advanced search 
	$scope.removeAdvanceSearchCrit = function () {
		if ($scope.rowCount == 1) {
			$scope.showAdvfilterCriteria1 = false;
			$scope.rowCount = 0;
			$scope.Advfilter = {
					key1 : "",
					searchIn1 : "",
					parameter1 : "",
					operator1 : "",
					value1 : ""
			};
		} else {
			$scope.showAdvfilterCriteria2 = false;
			$scope.hideRemoveButton = false;
			$scope.rowCount = 1;
			$scope.Advfilter.key2 = "";
			$scope.Advfilter.searchIn2 = "";
			$scope.Advfilter.parameter2 = "";
			$scope.Advfilter.operator2 = "";
			$scope.Advfilter.value2 = "";
			$scope.Advfilter.qryCriteria = "";
		}
	};

	//to show the version details when clicked on version number in grid
	$scope.umgVersionDetails = function (row) {
		dashboardService.getVersionDetails(row.versionName.trim(), row.fullVersion.trim())
		.then(
			function(responseData) {
				$scope.showMessage = true;
				if (responseData.error) {
					$scope.errorMessage = " \n Error in retrieving the version details : " + responseData.message;
				} else {
					$scope.showMessage = false;
					$scope.message = responseData.message;

					$scope.versionDetails = responseData.response;

					$scope.ViewDashaboardVersion = {
							umgLibraryName :$scope.versionDetails.umgLibraryName,
							umgModelName :$scope.versionDetails.umgModelName,
							umgTidName : $scope.versionDetails.umgTidName,
							status : $scope.versionDetails.status,
							publishedOn : $filter('date')($scope.versionDetails.publishedOn,"yyyy-MMM-dd HH:mm"),
							publishedBy :$scope.versionDetails.publishedBy
					};

					$scope.showDashaboardVersion = true;
					$('#myModal').modal('show');
				}
			},
			function(responseData) {
				$scope.showMessage = true;
				$scope.message = "Connection to Server failed. Please try again later.";
			}
		);

	};

	$scope.searchTransactions = function(){
		$scope.defaultFlag = !$scope.searchDisable;
		$scope.grid.pagingOptions.currentPage=1;
		$scope.selectAllChecked = false;
		if($scope.defaultFlag == false){
			$scope.getAllTransactions(); 
		}else if($scope.defaultFlag == true){
			$scope.listTransactionsByDefault();
		}
		//$scope.getAllTransactions();		 
	};
	
	$scope.getAllTransactionsDef = function() {
	    $scope.listTransactionsByDefault();
	};
	
	$scope.getAllTransactions = function() {
		$scope.message="";
		$scope.errorMessage="";
		$scope.clause1Empty = false;
		$scope.clause2Empty = false;
		//$scope.selectAllChecked = false;
		
		//calling validation for advance search filter
		var valid = true;
		if ($scope.showAdvfilterCriteria1 == true || $scope.showAdvfilterCriteria2 == true) {
			valid = $scope.validateAdvanceSrchFilter();
		} else {
			$scope.clause1Empty = true; 
			$scope.clause2Empty = true;
		}
		
		if (!valid){
			$scope.showMessage = true;
			$scope.errorMessage = "Please input appropriate values for Advanced Search fields";
		} else {
			$scope.showMessage = false;
			$scope.errorMessage="";
			var advanceTransactionFilter = {};
			var clause2 = {};
			if ($scope.clause1Empty == true && $scope.clause2Empty == true) {
				advanceTransactionFilter = null;
			} else {
				if ($scope.clause2Empty == true) {
					clause2 = null;
				} else {
					clause2 = {searchKey : $scope.Advfilter.searchIn2 + $scope.Advfilter.parameter2,
							searchValue : $scope.Advfilter.value2,
							searchOperator : $scope.Advfilter.operator2};					
				}

				if ($scope.showAdvSrch && ($scope.filter.tenantModelName == "" || $scope.filter.tenantModelName == "" ||  $scope.filter.runAsOfDateFromString == "" || $scope.filter.runAsOfDateToString == "")) {
					$scope.showMessage = true;
					$scope.errorMessage="Please provide 'Model Name', 'Run-Date from' and 'Run-Date to' values along with Advanced Search fields ";
					
				}else{
					advanceTransactionFilter = {
							clause1 : {searchKey : $scope.Advfilter.searchIn1 + $scope.Advfilter.parameter1,
								searchValue : $scope.Advfilter.value1,
								searchOperator : $scope.Advfilter.operator1},
								clause2 : clause2,
								criteria : $scope.Advfilter.qryCriteria
					};
				}
			}
			if(!$scope.showMessage){		
				if ($scope.selectAllDwnldUsgRprt) {
					$scope.selectAllDwnldUsgRprt = false;
					$scope.downldUsageRprtUsnfFltr(advanceTransactionFilter);
				} else {
					$scope.listTransactionsUsingBothFilters(advanceTransactionFilter);
				}
			}
		}
	};
	
	//for execution report
	
	$scope.getAllTransactionsExec = function() {
		$scope.message="";
		$scope.errorMessage="";
		$scope.clause1Empty = false;
		$scope.clause2Empty = false;
		//$scope.selectAllChecked = false;
		
		//calling validation for advance search filter
		var valid = true;
		if ($scope.showAdvfilterCriteria1 == true || $scope.showAdvfilterCriteria2 == true) {
			valid = $scope.validateAdvanceSrchFilter();
		} else {
			$scope.clause1Empty = true; 
			$scope.clause2Empty = true;
		}
		
		if (!valid){
			$scope.showMessage = true;
			$scope.errorMessage = "Please input appropriate values for Advanced Search fields";
		} else {
			$scope.showMessage = false;
			$scope.errorMessage="";
			var advanceTransactionFilter = {};
			var clause2 = {};
			if ($scope.clause1Empty == true && $scope.clause2Empty == true) {
				advanceTransactionFilter = null;
			} else {
				if ($scope.clause2Empty == true) {
					clause2 = null;
				} else {
					clause2 = {searchKey : $scope.Advfilter.searchIn2 + $scope.Advfilter.parameter2,
							searchValue : $scope.Advfilter.value2,
							searchOperator : $scope.Advfilter.operator2};					
				}

				if ($scope.showAdvSrch && ($scope.filter.tenantModelName == "" || $scope.filter.tenantModelName == "" ||  $scope.filter.runAsOfDateFromString == "" || $scope.filter.runAsOfDateToString == "")) {
					$scope.showMessage = true;
					$scope.errorMessage="Please provide 'Model Name', 'Run-Date from' and 'Run-Date to' values along with Advanced Search fields ";
					
				}else{
					advanceTransactionFilter = {
							clause1 : {searchKey : $scope.Advfilter.searchIn1 + $scope.Advfilter.parameter1,
								searchValue : $scope.Advfilter.value1,
								searchOperator : $scope.Advfilter.operator1},
								clause2 : clause2,
								criteria : $scope.Advfilter.qryCriteria
					};
				}
			}
			if(!$scope.showMessage){		
				if ($scope.selectAllDwnldUsgRprt) {
					$scope.selectAllDwnldUsgRprt = false;
					$scope.downldExecRprtUsnfFltr(advanceTransactionFilter);
				} else {
					$scope.listTransactionsUsingBothFilters(advanceTransactionFilter);
				}
			}
		}
	};

	//sever side sorting
	/*$scope.sortingClicked = false;
	$scope.fromSearchTrans = false;*/
	$scope.listTransactionsByDefault = function(){
		dashboardService.listTransactionsDefault($scope.grid.pagingOptions)
		.then(
				function(responseData) {
					$scope.showMessage = true;
					$scope.searchResultMessage = '';
					$scope.searchResultMessageWarnning = false;					
					$scope.totalCount = 0;
					
					if (responseData.error) {
						$scope.errorMessage = " \n Error in retrieving the transaction details : "
							+ responseData.message;
						$scope.showGrid=false;
						$scope.showNoGrid=false;
					} else {
						if (!responseData.message)
							$scope.showMessage = false;
						$scope.message = responseData.message;
						$scope.searchResultMessage = responseData.response.searchResultMessage;
						if ($scope.searchResultMessage.indexOf("Search is resulting in more than") > -1 || 
			            		$scope.searchResultMessage.indexOf("Search is taking longer than") > -1
			            		|| $scope.searchResultMessage.indexOf("No records found") > -1) {
			            	$scope.searchResultMessageWarnning = true;
			            }
						$scope.totalCount = responseData.response.totalCount;
						$scope.allOnlineTransModelNamesForRerun = [];
						$scope.allOnlineTranIdsForRerun = [];
						angular.forEach( responseData.response.transactionDocumentInfos,	function(data) {
							data.fullVersion = data.majorVersion + '.' + data.minorVersion;
							data.runAsOfDate = $filter('date')(data.runAsOfDateTime,"yyyy-MMM-dd HH:mm");
							data.createdDate = $filter('date')(data.createdDateTime,"yyyy-MMM-dd HH:mm");
							if(data.transactionMode.toUpperCase() == "Online".toUpperCase()){
								if ($scope.allOnlineTransModelNamesForRerun.indexOf(data.versionName + data.fullVersion) == -1) {
									//$scope.selectedTransactionModelNames.push(txn.model + txn.modelVersion);
									$scope.allOnlineTransModelNamesForRerun.push(data.versionName + data.fullVersion);
								}
								$scope.allOnlineTranIdsForRerun.push(data.transactionId);
							}
						});
						if (responseData.response.transactionDocumentInfos.length == 0) {
							$scope.showMessage = false;
							$scope.errorMessage = " No Records Found";
						}
						// removing existing selected items 
						$scope.selectedItems.splice(0,$scope.selectedItems.length);
						$scope.grid.pagingOptions.totalPages=responseData.response.pagingInfo.totalPages;
						$scope.grid.pagingOptions.totalServerItems=responseData.response.pagingInfo.totalElements;
						
						$scope.myData = responseData.response.transactionDocumentInfos;
						//$scope.myData = txnData;
						$scope.gridOptions.rowData = $scope.myData;
						$scope.gridOptions.api.refreshHeader();
						$scope.gridOptions.api.onNewRows();
						if($scope.myData.length==0){
							$scope.showGrid=false;
							$scope.showNoGrid=true;
						}else{
							$scope.showGrid= true;
							$scope.showNoGrid= false;
						}

						if(responseData.response.libraryNameList!=undefined){
							$scope.libraryNames = responseData.response.libraryNameList;
							$scope.libraryNames.unshift("");
						}
						if(responseData.response.tenantModelNameList!=undefined){
							$scope.tenantModelNames = responseData.response.tenantModelNameList;
							$scope.tenantModelNames.unshift("");
						}
						
						//sever side sorting
						/*if (!$scope.sortingClicked) {
							$scope.fromSearchTrans = true;
							$scope.setDataSource();
						}*/
						
						
						/*$timeout(function() {
							$scope.gridOptions.$gridServices.DomUtilityService.RebuildGrid(
									$scope.gridOptions.$gridScope, 
									$scope.gridOptions.ngGrid
							);								   
						}, 60);*/
						$timeout(function () {
							//sever side sorting
							//$scope.fromSearchTrans = false;
							$scope.gridOptions.api.sizeColumnsToFit();
			            }, 0);
					}
				},
				function(responseData) {
					$scope.showMessage = true;
					$scope.message = "Connection to Server failed. Please try again later.";
				}
		);
	};
	
	
	$scope.listTransactionsUsingBothFilters = function (advanceTransactionFilter) {
		dashboardService.listTransactions($scope.filter, advanceTransactionFilter, $scope.grid.pagingOptions)
		.then(
				function(responseData) {
					$scope.showMessage = true;
					$scope.searchResultMessage = '';
					$scope.searchResultMessageWarnning = false;					
					$scope.totalCount = 0;
					
					if (responseData.error) {
						$scope.errorMessage = " \n Error in retrieving the transaction details : "
							+ responseData.message;
						$scope.showGrid=false;
						$scope.showNoGrid=false;
					} else {
						if (!responseData.message)
							$scope.showMessage = false;
						$scope.message = responseData.message;
						$scope.searchResultMessage = responseData.response.searchResultMessage;
						if ($scope.searchResultMessage.indexOf("Search is resulting in more than") > -1 || 
			            		$scope.searchResultMessage.indexOf("Search is taking longer than") > -1
			            		|| $scope.searchResultMessage.indexOf("No records found") > -1) {
			            	$scope.searchResultMessageWarnning = true;
			            }
						$scope.totalCount = responseData.response.totalCount;
						$scope.allOnlineTransModelNamesForRerun = [];
						$scope.allOnlineTranIdsForRerun = [];
						angular.forEach( responseData.response.transactionDocumentInfos,	function(data) {
							data.fullVersion = data.majorVersion + '.' + data.minorVersion;
							data.runAsOfDate = $filter('date')(data.runAsOfDateTime,"yyyy-MMM-dd HH:mm");
							data.createdDate = $filter('date')(data.createdDateTime,"yyyy-MMM-dd HH:mm");
							if(data.transactionMode.toUpperCase() == "Online".toUpperCase()){
								if ($scope.allOnlineTransModelNamesForRerun.indexOf(data.versionName + data.fullVersion) == -1) {
									//$scope.selectedTransactionModelNames.push(txn.model + txn.modelVersion);
									$scope.allOnlineTransModelNamesForRerun.push(data.versionName + data.fullVersion);
								}
								$scope.allOnlineTranIdsForRerun.push(data.transactionId);
							}
						});
						if (responseData.response.transactionDocumentInfos.length == 0) {
							$scope.showMessage = false;
							$scope.errorMessage = " No Records Found";
						}
						// removing existing selected items 
						$scope.selectedItems.splice(0,$scope.selectedItems.length);
						$scope.grid.pagingOptions.totalPages=responseData.response.pagingInfo.totalPages;
						$scope.grid.pagingOptions.totalServerItems=responseData.response.pagingInfo.totalElements;
						
						$scope.myData = responseData.response.transactionDocumentInfos;
						//$scope.myData = txnData;
						$scope.gridOptions.rowData = $scope.myData;
						$scope.gridOptions.api.refreshHeader();
						$scope.gridOptions.api.onNewRows();
						if($scope.myData.length==0){
							$scope.showGrid=false;
							$scope.showNoGrid=true;
						}else{
							$scope.showGrid= true;
							$scope.showNoGrid= false;
						}

						if(responseData.response.libraryNameList!=undefined){
							$scope.libraryNames = responseData.response.libraryNameList;
							$scope.libraryNames.unshift("");
						}
						if(responseData.response.tenantModelNameList!=undefined){
							$scope.tenantModelNames = responseData.response.tenantModelNameList;
							$scope.tenantModelNames.unshift("");
						}
						
						//sever side sorting
						/*if (!$scope.sortingClicked) {
							$scope.fromSearchTrans = true;
							$scope.setDataSource();
						}*/
						
						
						/*$timeout(function() {
							$scope.gridOptions.$gridServices.DomUtilityService.RebuildGrid(
									$scope.gridOptions.$gridScope, 
									$scope.gridOptions.ngGrid
							);								   
						}, 60);*/
						$timeout(function () {
							//sever side sorting
							//$scope.fromSearchTrans = false;
							$scope.gridOptions.api.sizeColumnsToFit();
			            }, 0);
					}
				},
				function(responseData) {
					$scope.showMessage = true;
					$scope.message = "Connection to Server failed. Please try again later.";
				}
		);

	};
	
	//sever side sorting
	/*$scope.setDataSource = function () {
		var dataSource = {
                //rowCount: 100, // behave as infinite scroll
                pageSize: $scope.grid.pagingOptions.pageSize,
                overflowSize: 100,
                maxConcurrentRequests: 1,
                maxPagesInCache: 1,
                getRows: function (params) {
                    console.log('asking for ' + params.startRow + ' to ' + params.endRow);
                    // At this point in your code, you would call the server, using $http if in AngularJS.
                    // To make the demo look real, wait for 500ms before returning
                    setTimeout( function() {
                        // take a slice of the total rows
                        var dataAfterSortingAndFiltering = sortAndFilter(allOfTheData, params.sortModel, params.filterModel);
                        var rowsThisPage = dataAfterSortingAndFiltering.slice(params.startRow, params.endRow);
                        // if on or after the last page, work out the last row.
                        var lastRow = -1;
                        if (dataAfterSortingAndFiltering.length <= params.endRow) {
                            lastRow = dataAfterSortingAndFiltering.length;
                        }
                    	$scope.sortingClicked = true;
                        // call the success callback
                        params.successCallback($scope.gridOptions.rowData, 100);
                    }, 5);
                    
                    // call the success callback
                    if (!$scope.fromSearchTrans) {
                    	$scope.sortingClicked = true;
                    	if (params.sortModel.length > 0 ){
                    		$scope.grid.pagingOptions.sortColumn = params.sortModel[0].field;
                        	if (params.sortModel[0].sort == "asc") {
                        		$scope.grid.pagingOptions.descending = false;
                        	} else {
                        		$scope.grid.pagingOptions.descending = true;
                        	}
                    	} else {
                    		$scope.grid.pagingOptions.sortColumn = "";
                    		$scope.grid.pagingOptions.descending = true;
                    	}
                    	
                    	
                    	$scope.getAllTransactions();
                    }
                    
                    params.successCallback($scope.gridOptions.rowData, 100);
                }
            };

            $scope.gridOptions.api.setDatasource(dataSource);
	};*/
	
	//validates the advance search criteria fields
	$scope.validateAdvanceSrchFilter = function () {
		var valid = false;
		var clause1Valid = false;
		var clause2Valid = false;
		//checking if clause1 is empty
		if ($scope.Advfilter.searchIn1 == "" && $scope.Advfilter.parameter1 == ""  
			&& $scope.Advfilter.operator1 == ""  && $scope.Advfilter.value1 == "") {
			$scope.clause1Empty = true;
		} else {
			//checking if all data entered for clause1 
			if ($scope.Advfilter.searchIn1 != "" && $scope.Advfilter.parameter1 != "" 
				&& $scope.Advfilter.operator1 != "" &&  $scope.Advfilter.value1 != "") {
				clause1Valid = true;
			}		
		}

		if ($scope.showAdvfilterCriteria2 == false) {
			$scope.clause2Empty = true;
			$scope.Advfilter.qryCriteria = "";
		} else {
			if ($scope.Advfilter.searchIn2 == "" && $scope.Advfilter.parameter2 == "" 
				&& $scope.Advfilter.operator2 == "" && $scope.Advfilter.value2 == "") {
				$scope.clause2Empty = true;
			}
		}

		if ($scope.clause1Empty) {
			if ($scope.Advfilter.qryCriteria == "") {
				if ($scope.clause2Empty) {
					valid = true;
				} 
			} 
		} else {
			if (clause1Valid) {
				if ($scope.Advfilter.qryCriteria == "") {
					if ($scope.clause2Empty) {
						valid = true;
					} 
				} else {
					//checking if all data entered for clause2
					if ($scope.Advfilter.searchIn2 != "" && $scope.Advfilter.parameter2 != "" 
						&& $scope.Advfilter.operator2 != "" &&  $scope.Advfilter.value2 != "") {
						clause2Valid = true;
					}
					if (clause2Valid){
						valid = true;
					}	
				}
			}
		}
		return valid;
	};
	
	// clear button functionality
	$scope.clear = function(){
		//clearing the fields in first row of search fields and advanced search fields
		$scope.filter = {
				clientTransactionID : "",
				libraryName : "",
				majorVersion : undefined,
				minorVersion : undefined,
				runAsOfDateFromString : "",
				runAsOfDateToString : "",
				tenantModelName : "",
				fullVersion : "",
				runAsOfDateFrom : undefined,
				runAsOfDateTo : undefined,
				transactionType : "",
				transactionStatus : "",
				errorType:"",
				errorDescription:"",
				transactionMode : "",
				batchId:"",
				executionGroup : ""
					
		};	
		//clearing the row added by add field buttons
		if ($scope.showAdvfilterCriteria2 == true){
			$scope.Advfilter = {
					key1 : "",
					searchIn1 : "",
					parameter1 : "",
					operator1 : "",
					value1 : "",
					key2 : "",
					searchIn2 : "",
					parameter2 : "",
					operator2 : "",
					value2 : "",
					qryCriteria : ""
			};
		} else {
			$scope.Advfilter = {
					key1 : "",
					searchIn1 : "",
					parameter1 : "",
					operator1 : "",
					value1 : ""
			};
		}
		defaultInit();
		$('#runAsOfDateFromString').val("");
		$('#runAsOfDateToString').val("");
		$scope.filter.runAsOfDateFromString = $('#runAsOfDateFromString').val();
		$scope.filter.runAsOfDateToString = $('#runAsOfDateToString').val();
		$scope.TransactionDashboardForm.$setPristine();
	};
	
	//{headerName: "Run-Date", field: "runAsOfDate",suppressMenu : true, hide: false},
	
	var columnDefs = [
	                  {headerName: "",checkboxSelection:true,suppressResize:true,suppressMenu:true,suppressSorting:true,width:30,
	                	  suppressSizeToFit: true,headerCellRenderer: selectAllRows},
	                  {headerName: "Tenant Transaction ID", field: "clientTransactionID",width:300,filter: 'text', hide: false, comparator: stringIgnoreCaseComparator},
	                  {headerName: "Model Name", field: "versionName",width:300, hide: false, comparator: stringIgnoreCaseComparator},
	                  {headerName: "Version", field: "fullVersion",width:80, hide: false},
	                  {headerName: "Run-Date", field: "runAsOfDate", filter: 'text', hide: false, comparator: dateComparator},
	                  {headerName: "Status", cellClassRules: {
	                      'rag-red': function(params) { return params.data.errorCode == "RSE000930"}
	                  }, field: "status",width:120, hide: false, cellRenderer : setStatusForCell, valueGetter: valueGetterForStatusFilter},
	                  {headerName: "RA Transaction ID", field: "transactionId",filter: 'text', hide: true, comparator: stringIgnoreCaseComparator},
	                  {headerName: "RA Execution Date", field: "createdDate",filter: 'text', hide: true, comparator: dateComparator},
	                  {headerName: "Model Execution Time", field: "modelExecutionTime",filter: 'number', hide: true},
	                  {headerName: "Platform Execution Time", field: "modeletExecutionTime",filter: 'number', hide: true},
	                  {headerName: "Transaction Type", valueGetter: transTypeValueGetter,field: "transactionType", hide: false, width:150, comparator: stringIgnoreCaseComparator},
	                  {headerName: "Transaction Mode", field: "transactionMode", hide: true},
	                  {headerName: "Batch ID", field: "", hide: true, comparator: stringIgnoreCaseComparator},
	                  {headerName: "User Name", field: "createdBy",filter: 'text', hide: true, comparator: stringIgnoreCaseComparator},
					  {headerName: "Execution Mode",field: "executionGroup",filter: 'text', hide: true, comparator: stringIgnoreCaseComparator},
					  {headerName: "Executed Modelet", field: "modeletHostPortInfo",width:120, hide: true},
					  {headerName: "Executed Pool", field: "modeletPoolName",width:120, hide: true},
					  {headerName: "Environment", field: "environment",width:120, hide: true},
					  {headerName: "Payload Storage", field: "payloadStorage",width:120, hide: true},
					  {headerName: "Modelling Environment", field: "modellingEnv",width:120, hide: true},
					  {headerName: "Execution Environment", field: "execEnv",width:120, hide: true},
	                  {headerName: "Actions", field: "",width:80,suppressResize:true,suppressMenu:true,
	                	  suppressSorting:true, suppressSizeToFit: true, hide: false, 
	                	  template : '<span id="action" style="cursor: pointer; padding: 0px;" data-toggle="dropdown" role="button" aria-expanded="false" ng-click="checkReportTemplate(data);"><span class="glyphicon glyphicon-align-justify" style="top: 3px;"></span></span><ul ng-if="flag_tenantIoDownload == false || flag_modelIoDownload == false || flag_reportGeneration == false || flag_testBedRedirect == false" class="dropdown-menu" style="margin-top: 0px; margin-right: 40px; right : 0;left : auto;background-color: #EEEEEE; border: 1px solid;border-radius: 0px;border-color: #AAAAAA;" role="menu"><li ng-if="flag_tenantIoDownload == false"><a id="tenantIoDownload" style="cursor: pointer;color: #444; font-weight:400; font-size: 14px;font-family: Source Sans Pro,Helvetica Neue,Helvetica,Arial,sans-serif;" data-toggle="modal" ng-class="flag_tenantIoDownload == true ? \'disable-html-permission\' : \'\' " ng-click="downloadTenantIO(data)">Download Tenant I/O </a></li><li ng-if="flag_modelIoDownload  == false"><a id="modelIoDownload" style="cursor: pointer; color: #444; font-weight:400; font-size: 14px;font-family: Source Sans Pro,Helvetica Neue,Helvetica,Arial,sans-serif;" data-toggle="modal" ng-class="flag_modelIoDownload ? \'disable-html-permission\' : \'\' " ng-click="downloadModelIO(data)">Download Model I/O </a></li><li ng-if="flag_testBedRedirect == false"><a id="testBedRedirect" style="cursor: pointer; color: #444; font-weight:400; font-size: 14px;font-family: Source Sans Pro,Helvetica Neue,Helvetica,Arial,sans-serif;" data-toggle="modal" ng-class="flag_testBedRedirect == true ? \'disable-html-permission\' : isBulkTrans ?  \'disable-html\' : \'\' " ng-click="redirectToTestBed(data)">Re-run transaction </a></li><li ng-if="flag_reportGeneration == false"><a id="reportGeneration" style="cursor: pointer; color: #444; font-weight:400; font-size: 14px;font-family: Source Sans Pro,Helvetica Neue,Helvetica,Arial,sans-serif;" data-toggle="modal"  ng-class="flag_reportGeneration == true ? \'disable-html-permission\' : isSuccess ? \'\' :  \'disable-html\'" ng-click="generateReport(data)">Download Report </a></li><li><a id="rLogDownload" style="cursor: pointer; color: #444; font-weight:400; font-size: 14px;font-family: Source Sans Pro,Helvetica Neue,Helvetica,Arial,sans-serif;" data-toggle="modal" ng-class="downloadRLogs ? \'\' :  \'disable-html\'" ng-click="downloadRLog(data)">Download RLog </a></li></ul>'},
	              ];
	
	
	$scope.checkReportTemplate = function(data) {
		//alert(data.versionName);
		$scope.isSuccess = false;
		$scope.isBulkandSuccess = false;
		$scope.isBulkTrans = false;
		$scope.downloadRLogs = data.storeRLogs && (data.modellingEnv==null || data.modellingEnv.includes("R"));
		if(data.transactionMode=='Bulk'){
			$scope.isBulkTrans = true;
		}
		else{
			$scope.isBulkTrans = false;
		}
		
		if((data.status == "SUCCESS" || data.status == "Success") && data.transactionMode=='Bulk'){
			$scope.isBulkandSuccess = true;
		}
		else{
			$scope.isBulkandSuccess = false;
		}
		if(data.status == "SUCCESS" || data.status == "Success") {
			dashboardService.checkReportTemplate(data.versionName,data.fullVersion).then(
				function(responseData) {
					if (responseData.error) {
						alert(" ErrorCode: " + responseData.errorCode
								+ " \n Error in retrieving the hasReportTemplate flag : "
								+ responseData.message);
					} else {
						if (responseData.message == "true" || responseData.message == "TRUE") {
							$scope.isSuccess = true;							
						}
					}
				}, function(responseData) {
					alert('Failed: ' + responseData);
				}
			);
		} else
			$scope.isSuccess=false;
	}
	
	//sets the clickable function for version number field in grid
	/*function getActions(params) {
	    var html = '<div class="btn-group"> <button ng-click = "showVersionDetais()" class="btn btn-sm dropdown-toggle" data-toggle="dropdown" aria-expanded="false"><i class="fa fa-bars"></i></button> <ul class="dropdown-menu pull-right" role="menu"><li><a >Add new event</a></li><li><a >Clear events</a></li><li class="divider"></li><li><a >View calendar</a></li></ul></div>';
        var domElement = document.createElement("actions");
        domElement.innerHTML = html;
        
        params.$scope.showVersionDetais = function() {
            $timeout(function () {
                var innerdrop = '<div> <ul class="dropdown-menu pull-right" role="menu"><li><a >Add new event</a></li><li><a >Clear events</a></li><li class="divider"></li><li><a >View calendar</a></li></ul></div>';
                var domElement = document.createElement("actions1");
                domElement.innerHTML = innerdrop;
                return domElement;
            	$scope.dropdownshow = true;
            }, 0);
        };
        
        return domElement;
	};*/
	$scope.f= function() {
		alert("fefwe");
	}
	
	$scope.downloadTenantIO = function(data) {
		$window.location.href="txnDashBoard/downloadTenantIO/"+data.transactionId;
	};
	
	$scope.downloadRLog = function(data) {
		$window.location.href="txnDashBoard/downloadRLog/"+data.transactionId;
	};
	
	
	$scope.downloadModelIO = function(data) {
		if(data.transactionMode.toUpperCase() == 'BULK' && data.status.toUpperCase() == 'SUCCESS'){
			$dialogs.notify('','<span class="error-body"><strong>Model IO not stored for Successful Bulk transactions.<strong></span>');
		} else if(data.transactionMode.toUpperCase() == 'ONLINE' && (data.payloadStorage != undefined && !data.payloadStorage) && data.status.toUpperCase() == 'SUCCESS'){
			$dialogs.notify('','<span class="error-body"><strong>Model IO not stored as payloadStorage parameter was false in Tenant Input.<strong></span>');
		} else {
			$window.location.href="txnDashBoard/downloadModelIO/"+data.transactionId+'/'+data.transactionMode;
		}
	};
	
	$scope.redirectToTestBed = function(data) {
		$scope.runTestBed(data);
		//$scope.showMessage = true;
		//txnDashBoard/downloadTenantIO/{{row.entity.transactionId}}
		//$window.location.href="txnDashBoard/downloadTenantIO/"+data.transactionId;
		//return true;
	};
	
	
	$scope.generateReport = function(data) {
		var reportData = "";
		reportData = reportData.concat(data.majorVersion).concat(",")
			.concat(data.minorVersion).concat(",")
			.concat(data.transactionId.trim()).concat(",")
			.concat(data.versionName.trim());		
		
//		$window.location.href="report/generateReport/"+reportData;
		
		var url = "report/generateReport/"+reportData;
		
        $http.get(url).
        success(function(data, status, headers, config) {
        	$window.location.href=url;
        }).
        error(function(data, status, headers, config) {
        	$scope.searchResultMessageWarnning = true;
        	$scope.searchResultMessage=data;
        });
	};

	function transTypeValueGetter(params) {
        if (params.data.test==true){
        	return "Test";
        } else{
        	return "Prod";
        }
    };
    
    //selects all the rows if check box selected in header
    function selectAllRows (params) {
    	var html = '<input type="checkbox" ng-model="selectAllChecked" ng-change="selectAllRowsOfPage()" id="headerCheckbox" style="margin-left: 6px;">';
        // we could return the html as a string, however we want to add a 'onfocus' listener, which is no possible in AngularJS
        var domElement = document.createElement("headerCheckbox");
        domElement.innerHTML = html;
        params.$scope.selectAllRowsOfPage = function() {
            // put this into $timeout, so it happens AFTER the digest cycle,
            // otherwise the item we are trying to focus is not visible
            $timeout(function () {
            	if ($scope.selectAllChecked) {
            		$scope.selectAllChecked = false;
            		$scope.gridOptions.api.deselectAll();
            	} else {
            		$scope.selectAllChecked = true;
            		$scope.gridOptions.api.selectAll();
            	}
            }, 0);
        };
        return domElement;
    };
	
  //filtering of status is done on values of this valueGetter
	function valueGetterForStatusFilter(params){
		if (params.data.status == "ERROR" || params.data.status == "Error" ) {
			return params.data.errorCode;
		} else {
			return params.data.status;
		    
		}
	}
    
    //sets the clickable function for status field in grid
	function setStatusForCell(params) {
		
		if (params.data.status == "ERROR" || params.data.status == "Error") {	
			var errorCode = params.data.errorCode;
			if(errorCode=='RSE000930'){
				errorCode = "Time-Out(RSE000930)";				
			}else if(errorCode=='RSE000830'){
				errorCode = "Terminated(RSE000830)";			
			}			
		    var html = '<a ng-click= "showErrorDescription()" class="btn btn-sm" style="padding : 0px;">' +errorCode+ '</a>';
	        // we could return the html as a string, however we want to add a 'onfocus' listener, which is no possible in AngularJS
	        var domElement = document.createElement("span");
	        domElement.innerHTML = html;
	        params.$scope.showErrorDescription = function() {
	            // put this into $timeout, so it happens AFTER the digest cycle,
	            // otherwise the item we are trying to focus is not visible
	            $timeout(function () {
	                $dialogs.notify('Error Description',params.data.errorDescription);
	            }, 0);
	        };
	        return domElement;
		}else{
			return params.data.status;
		}
	};
	
	//sets the clickable function for version number field in grid
	function getVersionDetails(params) {
	    var html = '<a ng-click= "showVersionDetails()" class="btn btn-sm" style="padding : 0px;">' +params.data.fullVersion+ '</a>';
        var domElement = document.createElement("span");
        domElement.innerHTML = html;
        params.$scope.showVersionDetails = function() {
            $timeout(function () {
                $scope.umgVersionDetails(params.data);
            }, 0);
        };
        return domElement;
	};
	
	$scope.gridOptions ={
			rowHeight: 24,
			headerHeight: 24,
			columnDefs: columnDefs,
	        rowData: 'myData',
	        rowSelection: 'multiple',
	        enableSorting: true,
	        //enableServerSideSorting: true,
	        enableFilter: true,
	        suppressRowClickSelection: true,
	        enableColResize: true,
	        angularCompileRows : true,
	        angularCompileHeaders: true,
	        /*showToolPanel: true,
	        toolPanelSuppressValues: true,
	        toolPanelSuppressPivot: true,*/
	        pinnedColumnCount: 1,
	        groupSelectsChildren : false

	};
	
    /** column select toggle flag **/
	
	$scope.colSel = 0;
	
	/** column selection logic **/
	
	$scope.columnSelect = function(){
		
		if($scope.colSel == 0)
		{
			$scope.colSel = 1;
			document.getElementById('colDisp').style.display = 'block';
		}	
		else
		{
			$scope.colSel = 0;
			document.getElementById('colDisp').style.display = 'none';
		}
	};
	
	$scope.colHide = function(field,flag)
	{	
		$scope.gridOptions.api.hideColumn(field, !flag);
		//if (!flag) {
			$scope.gridOptions.api.sizeColumnsToFit();
		//}
	};
	
	$scope.columnRed = [
						{headerName: "Tenant Transaction ID", field: "clientTransactionID",flag:true, comparator: stringIgnoreCaseComparator},
						{headerName: "Model Name", field: "versionName",flag:true, comparator: stringIgnoreCaseComparator},
						{headerName: "Version", field: "fullVersion",flag:true},
						{headerName: "Run-Date", field: "runAsOfDate",flag:true, comparator: dateComparator},
						{headerName: "Status", field: "status",flag:true, comparator: stringIgnoreCaseComparator},
						{headerName: "RA Transaction ID", field: "transactionId",flag:false, comparator: stringIgnoreCaseComparator},
						{headerName: "RA Execution Date", field: "createdDate",flag:false, comparator: dateComparator},
						{headerName: "Model Execution Time", field: "modelExecutionTime",flag:false},
						{headerName: "Platform Execution Time", field: "modeletExecutionTime",flag:false},
						{headerName: "Executed Modelet", field: "modeletHostPortInfo",flag: false},
						{headerName: "Executed Pool", field: "modeletPoolName",flag: false},					
						{headerName: "Transaction Type",flag:true,field: "transactionType", comparator: stringIgnoreCaseComparator},
						{headerName: "User Name", field: "createdBy",flag:false},
						{headerName: "Transaction Mode", field: "transactionMode", hide: false},
						{headerName: "Execution Mode",flag:false,field: "executionGroup", comparator: stringIgnoreCaseComparator},
						{headerName: "Execution Environment", field: "execEnv",flag: false},
						{headerName: "Modelling Environment", field: "modellingEnv",flag: false}						
	                   ];

	

	/**
	 * Validate entered value in the dashboard form
	 */
	$scope.validateFilter = function(filter) {
		$scope.showMessage = true;
		if (filter.fullVersion.length >= 1 && !(doubleFormat.test(filter.fullVersion))) {
			$scope.errorMessage = "Please enter valid version number. Sample valid version values are : [2, 10.2, 1.0] ";
			return false;
		}
		return true;
	};	

	/**
	 * This method is used to download selected input/output datas for transactions
	 **/
	$scope.downloadIoErrorShow = false;
	$scope.downloadSelectedItems = function() {    	
		//if($scope.selectedItems.length>0){
		if($scope.gridOptions.selectedRows.length>0){
			             
			var model = $scope.gridOptions.api.getModel();
			var count = model.getVirtualRowCount();
			var txnIds = "";
			var countSelTrans = 0;
			for (var i = 0; i < count; i++) {
                var row = model.getVirtualRow(i);
                if ($scope.gridOptions.api.isNodeSelected(row)) {
                	countSelTrans = countSelTrans+1;
    				txnIds += row.data.transactionId+":"+row.data.transactionMode+":"+row.data.status+",";                                              	
                }
			}

			if (countSelTrans > $scope.selectionRecordCntLimit) {
				$scope.sameModelVerErrorMsg = '';
				 $scope.tranCntErrorMsg = "Please select upto maximum " + $scope.selectionRecordCntLimit + " transactions for 'Download I/O Files'.";
				 $scope.downloadIoErrorShow = true;
				 $scope.reRunPopUpShow = true;
			}  else if (countSelTrans == 0) {
				$scope.tranCntErrorMsg = '';
				 $scope.sameModelVerErrorMsg = "Please select atleast one transaction for 'Download I/O Files'.";
				 $scope.downloadIoErrorShow = true;
				 $scope.reRunPopUpShow = true;
			}else {
				dashboardService.downloadSelectedItems(txnIds).then(
					function(responseData) {
						$scope.showMessage = true;
						if (responseData.error) {
							$scope.errorMessage = " \n Error in retrieving the details : " + responseData.message;
						} else {
							$scope.showMessage = false;
							$scope.message = responseData.message;
	
							var result = responseData.response;
							saveAs(result.blob, result.fileName);
	
						}
					},
					function(responseData) {
						$scope.showMessage = true;
						$scope.message = "Connection to Server failed. Please try again later.";
					}
				);
			}
		}else{
			$scope.tranCntErrorMsg = '';
			 $scope.sameModelVerErrorMsg = "Please select atleast one transaction for 'Download I/O Files'.";
			 $scope.downloadIoErrorShow = true;
			 $scope.reRunPopUpShow = true;
		}
	};
	
	
	/**
	 * This method will redirect to TestBed.
	 * */
	$scope.runTestBed = function(data){
		$log.info("Redirecting to TestBed from Dashboard ...");
		var testVersion = {
				"transactionId":data.transactionId,
				"source":"dashBoardTestRunData",
				"storeRLogs":data.storeRLogs,
				"isRModel":data.modellingEnv==null || data.modellingEnv.includes("R")
		};
		sharedPropertiesService.put("testVersion", testVersion);
		$location.path('version/testbed');
	};
	/**
	 * This method will alert the error code for the failed transactions
	 **/
	$scope.showErrorDetails = function(data){
		var errorCode=(data.errorCode)?data.errorCode:"code not available";
		var errorMessage=(data.errorDescription)?data.errorDescription:"Message not available ";
		/* var dialogOptions = {
					headerText: 'Error!'+ errorCode,
					bodyText: errorMessage,
					colseButton:false
			};
			dialogService.showModalDialog({}, dialogOptions);*/
	};     

	$scope.intialSetup();
	
	// setting the values on load from batch dashboard
	loadTransactions = sharedPropertiesService.get("batchInfo");
	if(loadTransactions != null){
		$scope.filter.batchId = loadTransactions.batchId;
		$scope.filter.transactionStatus = loadTransactions.tranStatus;
		if (loadTransactions.isTransModeBulk){
        	$scope.filter.transactionType = "";
        }		
		$scope.getAllTransactions(); 
		sharedPropertiesService.remove("batchInfo");
	}else{
		$scope.getAllTransactionsDef();
	}


	/**==========================================
	 * sorting handler
	 */    

	//on sorting event fill out sortOptions in scope
	$scope.$on('ngGridEventSorted', function(event, data) {
		$scope.grid.pagingOptions.sortColumn=data.fields[0];
		if(data.directions[0]==='desc'){
			$scope.grid.pagingOptions.descending=true;
		}else{
			$scope.grid.pagingOptions.descending=false;
		}


	});


	/**==========================================
	 * page change handler
	 */
	$scope.$watch('grid.pagingOptions', function (newVal, oldVal) {
		if (newVal !== oldVal) {
			//page change
			if(newVal.currentPage !== oldVal.currentPage){
				if(newVal.currentPage<=0)
					newVal.currentPage=oldVal.currentPage;
				else if(newVal.currentPage>newVal.totalPages){
					newVal.currentPage=oldVal.currentPage;
				}
				$scope.getAllTransactions(); 
			}

			//sorting page
			if(newVal.sortColumn !== oldVal.sortColumn||newVal.descending !== oldVal.descending){
				//no generated column can sort with JPA

				if(newVal.sortColumn=='transactionId'|| 
						newVal.sortColumn=='libraryName'|| 
						newVal.sortColumn=='versionName'|| 
						newVal.sortColumn=='clientTransactionID'|| 
						newVal.sortColumn=='runAsOfDate'|| 
						newVal.sortColumn=='status'){
					$scope.getAllTransactions();
				}
			}
		}
	}, true);   
	
	/**==========================================
	 * first page
	 */
	$scope.firstPage=function(){
		
		$scope.grid.pagingOptions.currentPage=1;
	};	
	/**==========================================
	 * next page
	 */
	$scope.nextPage=function(){
		$scope.selectAllChecked = false;
		if($scope.grid.pagingOptions.totalPages>$scope.grid.pagingOptions.currentPage){
			$scope.grid.pagingOptions.currentPage=$scope.grid.pagingOptions.currentPage+1;
		}
	};	
	/**==========================================
	 * previous page
	 */
	$scope.previousPage=function(){
		$scope.selectAllChecked = false;
		if($scope.grid.pagingOptions.currentPage>1){
			$scope.grid.pagingOptions.currentPage=$scope.grid.pagingOptions.currentPage-1;
		}
	};	
	/**==========================================
	 * last page
	 */
	$scope.lastPage=function(){
		$scope.selectAllChecked = false;
		$scope.grid.pagingOptions.currentPage=$scope.grid.pagingOptions.totalPages;
	};	  

	/**==========================================
	 * page changed
	 */
	$scope.pageSizeChanged=function(){
		$scope.selectAllChecked = false;
		$scope.grid.pagingOptions.currentPage=1;
		if($scope.defaultFlag == false){
			$scope.getAllTransactions(); 
		}else if($scope.defaultFlag == true){
			$scope.listTransactionsByDefault();
		}
	};	

	$scope.showNote = false;
	/**==========================================
	 *  Method to display tool tip note
	 */
	$scope.advSrchNote=function(){
		$scope.showNote = true;
		/*umgDialog.setupDialog("advSrch-note",600,320);
			 umgDialog.openDialog(); */
	};

	$scope.showUsgRprtRerunPopup = false;
	$scope.showExecRprtRerunPopup = false;
	$scope.selectedTransForUsgRprt = [];
	$scope.selectedTransForUsgRprtCnt;
	
	$scope.getSelectedTransactions = function () {
		$scope.selectedTransForUsgRprt = [];
		var model = $scope.gridOptions.api.getModel();
		var count = model.getVirtualRowCount();
		var txnIds = "";
		for (var i = 0; i < count; i++) {
            var row = model.getVirtualRow(i);
            if ($scope.gridOptions.api.isNodeSelected(row)) {
				//txnIds += row.data.transactionId+",";  
            	$scope.selectedTransForUsgRprt.push(row.data.transactionId);
            }
		}
	};
	
	//methods for showing the download usage report pop up
	$scope.downloadUsageReportModalShow = function() {
		$scope.showUsgRprtRerunPopup = true;
		$scope.getSelectedTransactions();
		$scope.totalSearchedTransactions = $scope.grid.pagingOptions.totalServerItems;
		$scope.selectedTransForUsgRprtCnt = $scope.selectedTransForUsgRprt.length;
		document.getElementById("all").disabled = false;
		if ($scope.selectedTransForUsgRprt.length > 0) {
			document.getElementById("selected").disabled = false;
		} else {
			document.getElementById("selected").disabled = true;
		}
		$scope.all.transactions = '';
	};
	
	//methods for showing the download usage report pop up
	$scope.downloadExecutionReportModalShow = function() {
		$scope.showExecRprtRerunPopup = true;
		$scope.getSelectedTransactions();
		$scope.totalSearchedTransactions = $scope.grid.pagingOptions.totalServerItems;
		$scope.selectedTransForUsgRprtCnt = $scope.selectedTransForUsgRprt.length;
		document.getElementById("all_exec").disabled = false;
		if ($scope.selectedTransForUsgRprt.length > 0) {
			document.getElementById("selected_exec").disabled = false;
		} else {
			document.getElementById("selected_exec").disabled = true;
		}
		$scope.all.transactions = '';
	};
	
	//method for download-button in downloading usage report pop-up to download usage report 
	$scope.downloadUsageReport = function() { 
		var url = '';
   	 	var cancelRequestId="1";                   
     /*var sortColumn=($scope.searchOption.sortColumn!=undefined)?$scope.searchOption.sortColumn:"Date Time";
     var descending=($scope.searchOption.descending!=undefined)?$scope.searchOption.descending:false;*/
   	 	var sortColumn="Date Time";
   	 	var descending=true;
   	 
        if (document.getElementById("all").checked) {
        	$scope.selectAllDwnldUsgRprt = true;
        	$scope.getAllTransactions();
        } else {
        	if($scope.gridOptions.selectedRows.length>0){
    			dashboardService.downldUsageRprtForSelected($scope.selectedTransForUsgRprt,sortColumn, descending, cancelRequestId).then(
    					function(responseData) {
    						$scope.showMessage = true;
    						if (responseData.error) {
    							$scope.errorMessage = " \n Error in retrieving the details : " + responseData.message;
    						} else {
    							$scope.showMessage = false;
    							$scope.message = responseData.message;

    							var result = responseData.response;
    							saveAs(result.blob, result.fileName);

    						}
    					},
    					function(responseData) {
    						$scope.showMessage = true;
    						$scope.message = "Connection to Server failed. Please try again later.";
    					}
    				);
    			
    		} else {
	    		 //show error message 
	    	 }
        }
        $("#dwnldUsgRprt").modal("hide");
    };
    
  //method for download-button in downloading usage report pop-up to download usage report 
	$scope.downloadExecReport = function() { 
		var url = '';
   	 	var cancelRequestId="1";                   
     /*var sortColumn=($scope.searchOption.sortColumn!=undefined)?$scope.searchOption.sortColumn:"Date Time";
     var descending=($scope.searchOption.descending!=undefined)?$scope.searchOption.descending:false;*/
   	 	var sortColumn="Date Time";
   	 	var descending=true;
   	 
        if (document.getElementById("all_exec").checked) {
        	$scope.selectAllDwnldUsgRprt = true;
        	$scope.getAllTransactionsExec();
        } else {
        	if($scope.gridOptions.selectedRows.length>0){
    			dashboardService.downldExecRprtForSelected($scope.selectedTransForUsgRprt,sortColumn, descending, cancelRequestId).then(
    					function(responseData) {
    						$scope.showMessage = true;
    						if (responseData.error) {
    							$scope.errorMessage = " \n Error in retrieving the details : " + responseData.message;
    						} else {
    							$scope.showMessage = false;
    							$scope.message = responseData.message;

    							var result = responseData.response;
    							saveAs(result.blob, result.fileName);

    						}
    					},
    					function(responseData) {
    						$scope.showMessage = true;
    						$scope.message = "Connection to Server failed. Please try again later.";
    					}
    				);
    			
    		} else {
	    		 //show error message 
	    	 }
        }
        $("#dwnldExecRprt").modal("hide");
    };
    
  //method for downloading usage reportusing search filter
    $scope.downldUsageRprtUsnfFltr= function(advanceTransactionFilter) {
    	dashboardService.downldUsageRprtUsngFltr($scope.filter, advanceTransactionFilter, $scope.grid.pagingOptions).then(
			function(responseData) {
				$scope.showMessage = true;
				if (responseData.error) {
					$scope.errorMessage = " \n Error in retrieving the details : " + responseData.message;
				} else {
					$scope.showMessage = false;
					$scope.message = responseData.message;
					var result = responseData.response;
					saveAs(result.blob, result.fileName);
				}
			},
			function(responseData) {
				$scope.showMessage = true;
				$scope.message = "Connection to Server failed. Please try again later.";
			}
		);
    };
    
    //method for downloading execution reportusing search filter
    $scope.downldExecRprtUsnfFltr= function(advanceTransactionFilter) {
    	dashboardService.downldExecRprtUsngFltr($scope.filter, advanceTransactionFilter, $scope.grid.pagingOptions).then(
			function(responseData) {
				$scope.showMessage = true;
				if (responseData.error) {
					$scope.errorMessage = " \n Error in retrieving the details : " + responseData.message;
				} else {
					$scope.showMessage = false;
					$scope.message = responseData.message;
					var result = responseData.response;
					saveAs(result.blob, result.fileName);
				}
			},
			function(responseData) {
				$scope.showMessage = true;
				$scope.message = "Connection to Server failed. Please try again later.";
			}
		);
    };
    
    //methods for re-run functionality
    $scope.selectedTransForRerun = [];
    //$scope.all.transactions = '';
    $scope.reRunPopUpShow = false;
    $scope.sameModelVerErrorMsg = '';
    $scope.tranCntErrorMsg = '';
    $scope.bulkPresent = false;
    
    //method to show the re-run pop up and download if no errors
    $scope.exportForRerunShow = function() {
    	$scope.selectedTransForRerun = [];
    	$scope.sameModelVerErrorMsg = '';
        $scope.tranCntErrorMsg = '';
        $scope.bulkPresent = false;
    	if($scope.gridOptions.selectedRows.length>0){ // conditions check for selected transactions
    		var selectedTransactionModelNames = [];
    		var model = $scope.gridOptions.api.getModel();
    		var count = model.getVirtualRowCount();
    		for (var i = 0; i < count; i++) {
                var row = model.getVirtualRow(i);
                if ($scope.gridOptions.api.isNodeSelected(row)) {
    				//txnIds += row.data.transactionId+",";  
                	if(row.data.transactionMode.toUpperCase()=="Online".toUpperCase() || row.data.transactionMode.toUpperCase()=="Batch".toUpperCase()){
                		$scope.selectedTransForRerun.push(row.data.transactionId);
                		selectedTransactionModelNames.push(row.data.versionName + row.data.fullVersion);
                	} else{
                	 $scope.sameModelVerErrorMsg = "Please select only online transactions for 'Export for Re-run'.";      
                	 $scope.bulkPresent = true;
                	 $scope.downloadIoErrorShow = false;
        			 $scope.tranCntErrorMsg = '';
    				 $scope.reRunPopUpShow = true;
                	}
                }
    		}
    		
    		if ($scope.selectedTransForRerun.length == 0 && selectedTransactionModelNames.length == 0) {
    			$scope.sameModelVerErrorMsg = "Please select atleast one online transaction for 'Export for Re-run'.";
    			 $scope.downloadIoErrorShow = false;
    			 $scope.tranCntErrorMsg = '';
				 $scope.reRunPopUpShow = true;
    		} else  if(!$scope.bulkPresent){
				var modeleName = null;
				var areDifferentModels = false;
				angular.forEach(selectedTransactionModelNames, function(model){
					 if (modeleName == null) {
						 modeleName = model;
					 } else {
						 if (modeleName != null && model != null && modeleName != model) {
							 areDifferentModels = true;
						 }
					 }
				});
				 
				 if (modeleName != null && areDifferentModels == false) {
					 if($scope.selectedTransForRerun.length>0 && $scope.selectedTransForRerun.length <= $scope.selectionRecordCntLimit){  
						 $scope.downloadRerunReport ($scope.selectedTransForRerun);
			    	 } else if ($scope.selectedTransForRerun.length > $scope.selectionRecordCntLimit) {
						 $scope.tranCntErrorMsg = "Please select upto maximum " + $scope.selectionRecordCntLimit + " transactions for 'Export for Re-run'.";
						 $scope.downloadIoErrorShow = false;
						 $scope.reRunPopUpShow = true;
					 }
				 } else {
					 //show error message in pop up
					 $scope.showErrorPopupForRerun($scope.selectedTransForRerun);
				 }
    		}
		} else { // conditons check for all online transactions
			if($scope.allOnlineTransModelNamesForRerun.length>0){
				var modeleName = null;
				var areDifferentModels = false;
				angular.forEach($scope.allOnlineTransModelNamesForRerun, function(model){
					 if (modeleName == null) {
						 modeleName = model;
					 } else {
						 if (modeleName != null && model != null && modeleName != model) {
							 areDifferentModels = true;
						 }
					 }
				});
				
				if (modeleName != null && areDifferentModels == false) {
					 if($scope.allOnlineTranIdsForRerun.length>0 && $scope.allOnlineTranIdsForRerun.length <= $scope.selectionRecordCntLimit){  
			    		 $scope.downloadRerunReport ($scope.allOnlineTranIdsForRerun);
			    	 } else if($scope.allOnlineTranIdsForRerun.length > $scope.selectionRecordCntLimit) {
						 $scope.tranCntErrorMsg = "Please select upto maximum " + $scope.selectionRecordCntLimit + " transactions for 'Export for Re-run'.";
						 $scope.downloadIoErrorShow = false;
						 $scope.reRunPopUpShow = true;
			    	 }
				 } else {
					 //show error message in pop up
					 $scope.showErrorPopupForRerun($scope.allOnlineTranIdsForRerun);
				 }
			} 
		}
    };
    
	// for new re-run popup
	$scope.modalVar = '';
	$scope.clearModal = function() {
		$scope.modalVar = "modal";
		$scope.reRunPopUpShow = false;
		// $('#libModal').hide();
		$("#reRunPopId").modal("hide");
		//$('#libModal').close();
	};
	
	$scope.showErrorPopupForRerun = function (tranIds) {
		$scope.downloadIoErrorShow = false;
		 $scope.sameModelVerErrorMsg = "Please select transactions with same Model Name and Version for 'Export for Re-run'.";
		 if(tranIds.length > $scope.selectionRecordCntLimit) {
			 $scope.tranCntErrorMsg = " Please select upto maximum " + $scope.selectionRecordCntLimit + " transactions for 'Export for Re-run'.";
		 } else {
			 $scope.tranCntErrorMsg = '';
		 }
		 $scope.reRunPopUpShow = true;
	};
	
	//method calls the service for re-run report
	$scope.downloadRerunReport = function (selectedTransForRerun) {
		 dashboardService.downloadSelectedItemsForRerun(selectedTransForRerun).then(
			function(responseData) {
				$scope.showMessage = true;
				if (responseData.error) {
					$scope.errorMessage = " \n Error in retrieving the details : " + responseData.message;
				} else {
					$scope.showMessage = false;
					$scope.message = responseData.message;
					var result = responseData.response;
					saveAs(result.blob, result.fileName);
				}
			},
			function(responseData) {
				$scope.showMessage = true;
				$scope.message = "Connection to Server failed. Please try again later.";
			}
		);
	};
	 

	/*$('.add').click(function () {
    $('.row:last').before(' <div class="row" id="row1"> <div class="col-xs-2"> <span class="control-label"> Input Type</span> <select ng-model="Advfilter.inputType" id="Advfilter.inputType"  name="InputType" class="form-control" ng-options="inputType as inputType.name for inputType in inputTypeMap track by inputType.id"> <option value="">-- Select Input Type--</option></select></div><span class="remove">Remove Option</span></div>');
});

$('body').on('click', '.remove', function () {
    $(this).parent('#row1').remove();
});

$(document).on('click','.remove',function() {
    $(this).parent().remove();
});*/


/*$scope.deselectCheckBox = function(){
	// $scope.gridOptions.selectAll(false);
	$log.info("Total Selected Records Before : "+$scope.selectedItems.length);
	var selectAllHeader = angular.element(".ngSelectionHeader").scope();
	if(selectAllHeader) selectAllHeader.allSelected = false;
	$scope.selectedItems.length = 0;
	$log.info("Total Selected Records After : "+$scope.selectedItems.length);
};*/

	function dateComparator(date1, date2) {
        var date1Number = monthToComparableNumber(date1);
        var date2Number = monthToComparableNumber(date2);
        if (date1Number===null && date2Number===null) {
            return 0;
        }
        if (date1Number===null) {
            return -1;
        }
        if (date2Number===null) {
            return 1;
        }

        return date1Number - date2Number;
    }
	
	// eg 29/08/2004 gets converted to 20040829
    function monthToComparableNumber(date) {
	    if (date === undefined || date === null || date.length !== 17) {
	        return null;
	    }
	   
	    var yearNumber = date.substring(0,4);
	    var monthNumber= getMonthNumber(date.substring(5,8));
	    var dayNumber= date.substring(9,11);
	    var hour= date.substring(12,14);
	    var minute= date.substring(15,17);
	    var result = (yearNumber*100000000) + (monthNumber*1000000) + (dayNumber*10000)+(hour*100)+minute;
	    return result;
    }
    
    function getMonthNumber(month){
        if(month.toUpperCase() === "JAN"){
          return "01";
        }else if(month.toUpperCase() === "FEB"){
          return "02";
        }else if(month.toUpperCase() === "MAR"){
          return "03";
        }else if(month.toUpperCase() === "APR"){
          return "04";
        }else if(month.toUpperCase() === "MAY"){
          return "05";
        }else if(month.toUpperCase() === "JUN"){
          return "06";
        }else if(month.toUpperCase() === "JUL"){
          return "07";
        }else if(month.toUpperCase() === "AUG"){
          return "08";
        }else if(month.toUpperCase() === "SEP"){
          return "09";
        }else if(month.toUpperCase() === "OCT"){
          return "10";
        }else if(month.toUpperCase() === "NOV"){
          return "11";
        }else if(month.toUpperCase() === "DEC"){
          return "12";
        }
     }
    
    function stringIgnoreCaseComparator(string1, string2) {
    	if (string1 === null && string2 === null) {
            return 0;
        }
        
        if (string1 === null) {
            return -1;
        }
        
        if (string2 === null) {
            return 1;
        }

    	if (string1.toLowerCase() < string2.toLowerCase()) {
    		return -1;
    	}

    	if (string1.toLowerCase() > string2.toLowerCase()) {
    		return 1;
    	}
    	
    	return 0;
    }
    
    $scope.advSrchNote=function(){
		 $scope.showNote = true;
	 };
};