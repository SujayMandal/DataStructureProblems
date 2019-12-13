'use strict';

var BatchDashboardCtrl = ['$scope', '$log', '$location', '$filter', '$timeout', '$window', 'batchDashBoardService', 'sharedPropertiesService', '$dialogs', function($scope, $log, $location, $filter, $timeout, $window, batchDashBoardService, sharedPropertiesService, $dialogs){
	
	$scope.pagedData = [];
	$scope.mySelection = [];
	$scope.uploadBatchExcel = false;
	//batchExcel can contain either excel or json file depending on batch or bulk modeltype respectively
	$scope.batchExcel = {
			batchExcelFile : ''
		};
	$scope.fileUploadModelType='Bulk';
	$scope.fileAcceptType = '.json';
	$scope.showErrorMessage = false;
	$scope.showSuccesssMessage = false;
	$scope.showMessage = '';
	$scope.showUploadBatchErrorMsg='';
	$scope.showUploadBatchError = false;
	$scope.searchResultMessage = '';
	$scope.totalCount = 0;
	$scope.searchResultMessageWarnning = false;
	$scope.uploads=[];
	$scope.totalSearchedTransactions;
	
	/** Pagination Options */
	
	$scope.pagingOptions = {
	        pageSizes: [500, 1000, 2500, 5000],
	        pageSize: 500,
            currentPage: 1
	}; 
	
	/** Default properties for the page */
	
	$scope.pageInfo = {
			fromDate: null,
			toDate:null,
			batchId:null,
			inputFileName:null,
			pageSize: $scope.pagingOptions.pageSize,
			page: $scope.pagingOptions.currentPage,
			sortColumn : '',
			descending : false
	};
	
	/** Main method */
	
	$scope.getPagedBatchTxns = function(){
		$log.info('Request received to fetch transactions as per page informations.');
		$scope.showErrorMessage = false;
		$scope.showSuccesssMessage = false;
		if($scope.pageInfo.fromDate != null)
		$scope.pageInfo.fromDate = $filter('date')($scope.pageInfo.fromDate,"yyyy-MMM-dd HH:mm");
		if($scope.pageInfo.toDate != null)
		$scope.pageInfo.toDate = $filter('date')($scope.pageInfo.toDate,"yyyy-MMM-dd HH:mm");
		
		batchDashBoardService.fetchPagedDataAsync($scope.pageInfo).then(
				function(responseData){					
					$scope.searchResultMessage = '';
					$scope.totalCount = 0;
					$scope.searchResultMessageWarnning = false;
					if(!responseData.error){
						angular.forEach(responseData.response.batchTransactionInfoList, function(data){
							if(data.batchInputFile == undefined || data.batchInputFile == null || data.batchInputFile.trim() == ''){
								data.batchInputFile = 'Not Applicable';
							}
							if(data.batchOutputFile == undefined || data.batchOutputFile == null || data.batchOutputFile.trim() == ''){
								data.batchOutputFile = 'Not Applicable';
							}
						});
						$scope.pagedData = responseData.response.batchTransactionInfoList;
						$scope.totalPages = responseData.response.pageInfo.totalPages;
						$scope.batchTxns = responseData.response.pageInfo.totalRecords;
						$scope.gridOptions.rowData = $scope.pagedData;
			            $scope.gridOptions.api.onNewRows();
			            $scope.searchResultMessage = responseData.response.searchResultMessage;
			            $scope.totalCount = responseData.response.toatlCount;
			            if(responseData.response.tenantConfigsMap.BULK_ENABLED == "true"){
			            	$scope.uploads.push('Bulk');
			            	$scope.fileUploadModelType='Bulk';
			            	$scope.fileAcceptType = '.json';
			            }
			            if(responseData.response.tenantConfigsMap.BATCH_ENABLED == "true"){
			            	$scope.uploads.push('Batch');
			            	$scope.fileUploadModelType='Batch';
			            	$scope.fileAcceptType = '.xls, .xlsx,.json,.txt';
			            }
			            
			            if ($scope.searchResultMessage.indexOf("Search is resulting in more than") > -1 || 
			            		$scope.searchResultMessage.indexOf("Search is taking longer than") > -1) {
			            	$scope.searchResultMessageWarnning = true;
			            }
			            $timeout(function () {
							$scope.gridOptions.api.sizeColumnsToFit();
			            }, 0);
					}
					else{
						$log.error(responseData.message);
						$scope.showErrorMessage = true;
						$scope.showMessage = " \n Error in retrieving batch transaction details : "+ responseData.message;
					}
				},
				function(errorData){
					$log.error(errorData);
					$scope.showErrorMessage = true;
					$scope.showMessage = " \n Error in retrieving batch transaction details : "+ errorData;
				}
		);
	};
	
	
	/** Method call on page load */
	
	$scope.getPagedBatchTxns();
	
	
	/** Sorting Logic*/
	
	$scope.sortTransaction = function(columnName, descending){
		$log.info('Sorting Column for : '+columnName+' in Descending : '+descending);
		$scope.pageInfo.sortColumn = columnName;
		$scope.pageInfo.descending = descending;
		$scope.getPagedBatchTxns();
	};
	
	
	/** Main Grid */
	
	$scope.gridOptions = {	
			rowHeight: 24,
			headerHeight: 24,
			rowData: 'pagedData',
			rowSelection: 'multiple',
			suppressRowClickSelection: true,
			enableCellExpressions: true,
			enableColResize: true,
			enableFilter: true,
			enableSorting: true,
	        angularCompileRows : true,
	        angularCompileHeaders: true,
			pinnedColumnCount: 1,
			columnDefs : [
			              {headerName: "",checkboxSelection:true,suppressResize:true,suppressMenu:true,suppressSorting:true,width:30,
			            	  suppressSizeToFit: true,headerCellRenderer: selectAllRows},
			              { headerName : 'Batch ID', field : 'id', filter: 'text', comparator: stringIgnoreCaseComparator},
						  { headerName : 'Input File', field : 'batchInputFile', filter: 'text', comparator: stringIgnoreCaseComparator},
						  { headerName : 'Output File', field : 'batchOutputFile', filter: 'text', hide:true, comparator: stringIgnoreCaseComparator},
						  { headerName : 'Transaction Type', field : 'test', width: 90, hide:true, valueGetter: transTypeValueGetter, comparator: stringIgnoreCaseComparator},
						  { headerName : 'Status', cellClassRules: {
		                      'rag-red': function(params) { return params.data.status == "TIMEOUT"}
		                  }, field : 'status', width: 100, comparator: stringIgnoreCaseComparator},
						  { headerName : 'RA Execution Date', field: 'createdDateTime',filter: 'text', comparator: stringIgnoreCaseComparator},
						  { headerName : 'Total Count', field : 'totalRecords', width: 120, filter: 'number', cellRenderer : setTotalCountStatus},
						  { headerName : 'Success Count', field : 'successCount', width: 130, filter: 'number', cellRenderer : setSuccessStatus},
						  { headerName : 'Failure Count', field : 'failCount', width: 120, filter: 'number', cellRenderer : setFailureStatus},
						  { headerName : 'Not Picked Count', field : 'notPickedCount', width: 120, filter: 'number', hide:false,cellRenderer : setNotPickedStatus},
						  { headerName : 'In-Progress Count', field : 'txnInProgressCount', width: 120, filter: 'number', hide:false,cellRenderer:setInProgressStatus},
						  { headerName : 'Start Time', field : 'fromDate', width: 120, filter: 'text', hide:true, comparator: dateComparator},
						  { headerName : 'End Time', field : 'toDate', width: 120, filter: 'text',hide:true, comparator: dateComparator},
						  { headerName : 'Execution Time', field : 'batchExecTime', width: 140, filter: 'number', comparator: execTimeComparator}, 
						  { headerName : 'Transaction Mode', field : 'transactionMode', width: 140, filter: 'set', hide:false},
						  { headerName : 'Modelling Environment', field : 'modellingEnv', width: 140, filter: 'set', hide:true},
						  { headerName : 'Execution Environment', field : 'execEnv', width: 140, filter: 'set', hide:true}]
	};
	
	
	function transTypeValueGetter(params) {
        if (params.data.test==true){
        	return "Test";
        } else{
        	return "Prod";
        }
    };
    
    function isBulkInpogress(params) {
    	return params.data.transactionMode == 'Bulk' && params.data.status == 'IN_PROGRESS';
    }
    
    function isBatchAndZeroCount(params, count) {
    	return params.data.transactionMode == 'Batch' && count == 0;
    }
    
  //sets the total count
	function setTotalCountStatus(params) {
		if (isBulkInpogress(params) || isBatchAndZeroCount(params, params.data.totalRecords)) {
			return '0';
		}
		else{
			return params.data.totalRecords;
		}
	};
	
	 //sets the not picked count
	function setNotPickedStatus(params) {
		if (isBulkInpogress(params) || isBatchAndZeroCount(params, params.data.notPickedCount)) {
			return '0';
		}
		else{
			return params.data.notPickedCount;
		}
	};
	
	//sets the clickable function for in progress count
	function setInProgressStatus(params) {
		if (isBulkInpogress(params) || isBatchAndZeroCount(params, params.data.txnInProgressCount)) {
			return '0';
		}
		
	    var html = '<a ng-click= "redirectToInProgressTranDashBrd()" class="btn btn-sm" style="padding : 0px;">' +params.data.txnInProgressCount+ '</a>';
        // we could return the html as a string, however we want to add a 'onfocus' listener, which is no possible in AngularJS
        var domElement = document.createElement("span");
        domElement.innerHTML = html;
        params.$scope.redirectToInProgressTranDashBrd = function() {
            // put this into $timeout, so it happens AFTER the digest cycle,
            // otherwise the item we are trying to focus is not visible
            $timeout(function () {
                var batchInfo = {'batchId' : params.data.id, 
                		'showTestTxn' : params.data.test,
                		'isTransModeBulk': false,
                		'tranStatus' : 'In-Progress'};
                if(params.data.transactionMode == 'Bulk')
                {	batchInfo.tranStatus = 'Any';
                	batchInfo.isTransModeBulk = true;
                }
                $scope.loadTransactions(batchInfo); 
            }, 0);
        };
        return domElement;
	};
	
	//sets the clickable function for success count
	function setSuccessStatus(params) {
		if (isBulkInpogress(params) || isBatchAndZeroCount(params, params.data.successCount)) {
			return '0';
		}
		
	    var html = '<a ng-click= "redirectToSuccTranDashBrd()" class="btn btn-sm" style="padding : 0px;">' +params.data.successCount+ '</a>';
        // we could return the html as a string, however we want to add a 'onfocus' listener, which is no possible in AngularJS
        var domElement = document.createElement("span");
        domElement.innerHTML = html;
        params.$scope.redirectToSuccTranDashBrd = function() {
            // put this into $timeout, so it happens AFTER the digest cycle,
            // otherwise the item we are trying to focus is not visible
            $timeout(function () {
                var batchInfo = {'batchId' : params.data.id, 
                		'showTestTxn' : params.data.test,
                		'isTransModeBulk': false,
                		'tranStatus' : 'Success'};
                if(params.data.transactionMode == 'Bulk')
                {	batchInfo.tranStatus = 'Any';
                	batchInfo.isTransModeBulk = true;
                }
                $scope.loadTransactions(batchInfo); 
            }, 0);
        };
        return domElement;
	};
	
	//sets the clickable function for failure count
	function setFailureStatus(params) {
		if (isBulkInpogress(params) || isBatchAndZeroCount(params, params.data.failCount)) {
			return '0';
		}
		
	    var html = '<a ng-click= "redirectToFailTranDashBrd()" class="btn btn-sm" style="padding : 0px;">' +params.data.failCount+ '</a>';
        // we could return the html as a string, however we want to add a 'onfocus' listener, which is no possible in AngularJS
        var domElement = document.createElement("span");
        domElement.innerHTML = html;
        params.$scope.redirectToFailTranDashBrd = function() {
            // put this into $timeout, so it happens AFTER the digest cycle,
            // otherwise the item we are trying to focus is not visible
            $timeout(function () {
                var batchInfo = {'batchId' : params.data.id, 
                		'showTestTxn' : params.data.test,
                		'isTransModeBulk': false,
                		'tranStatus' : 'Failure'};
                if(params.data.transactionMode == 'Bulk')
                {	batchInfo.tranStatus = 'Any';
                	batchInfo.isTransModeBulk = true;
                }
                $scope.loadTransactions(batchInfo); 
            }, 0);
        };
        return domElement;
	};
	
	/**
	 * This method is to load Transactions in Transaction Dash-board, based on Batch id
	 * */
	$scope.loadTransactions = function(batch){
		$log.info("Redirecting to Transaction Dash-board.");
		sharedPropertiesService.put("batchInfo",batch);
		$location.path('dashboard');
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
    
	$scope.columnRed = [{ headerName : 'Batch ID',field : 'id', flag:true},
				  { headerName : 'Input File',field : 'batchInputFile', flag:true},
				  { headerName : 'Output File',field : 'batchOutputFile', flag:false},
				  { headerName : 'Transaction Type',field : 'test', flag:false},
				  { headerName : 'Status',field : 'status', flag:true},
				  { headerName : 'Total Count',field : 'totalRecords', flag:true},
				  { headerName : 'Success Count',field : 'successCount', flag:true},
				  { headerName : 'Failure Count',field : 'failCount', flag:true},
				  { headerName : 'Not Picked Count',field : 'notPickedCount', flag:true},
				  { headerName : 'In-Progress Count',field : 'txnInProgressCount', flag:true},
				  { headerName : 'Start Time',field : 'fromDate', flag:false},
				  { headerName : 'End Time',field : 'toDate', flag:false},
				  { headerName : 'Execution Time',field : 'batchExecTime', flag:true},
				  { headerName : 'RA Execution Date',field : 'createdDateTime', flag:true},
				  { headerName : 'Transaction Mode',field : 'transactionMode', flag:true},
				  { headerName : 'Modelling Environment',field : 'modellingEnv', flag:false},
				  { headerName : 'Execution Environment',field : 'execEnv', flag:false}];
	
	/**
     * This method will always keep eye on any change for pagination request.
     * And will set the paged data accordingly.
     */
    $scope.$watch('pagingOptions', function (newVal, oldVal) {
    	
    	!angular.isNumber(newVal.currentPage) ? (newVal.currentPage = 1): (newVal.currentPage = newVal.currentPage); 
        
    	if (newVal !== oldVal) {
        	$scope.pageInfo.page = newVal.currentPage;
        	$scope.pageInfo.pageSize = newVal.pageSize;
        	if(newVal.currentPage * newVal.pageSize >= $scope.batchTxns){
        		$scope.pagingOptions.currentPage = Math.ceil($scope.batchTxns / $scope.pagingOptions.pageSize);
        	}
        	if(newVal.currentPage * newVal.pageSize <= 0){
        		$scope.pagingOptions.currentPage = 1;
        	}
        	$scope.getPagedBatchTxns();
        }
    }, true);
	
	
	
	
	/** column select toggle flag **/
	
	$scope.colSel = 0;
	
	/** column selection logic **/
	
	$scope.columnSelect = function(){
		if($scope.colSel === 0)
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
		$scope.gridOptions.api.sizeColumnsToFit();
	};
	
	/*upload batch methods*/
	
	$scope.showUploadBatchFile = function()
	{	
		$scope.uploadBatchExcel = true;
		$scope.showErrorMessage = false;
		$scope.showSuccesssMessage = false;
		$scope.batchExcel.batchExcelFile = '';
		$scope.showUploadBatchErrorMsg='';
		$scope.showUploadBatchError = false;
		if(document.getElementById("batchExcel_browse") != null)
			document.getElementById("batchExcel_browse").value = ""; 
	};
	
	$scope.modalVar = '';
	$scope.clearModal = function() {
		$scope.modalVar = "modal";
		$scope.uploadBatchExcel = false;
		$scope.batchExcel.batchExcelFile = '';
		// $('#libModal').hide();
		$("#uploadBatchExcelId").modal("hide");
		//$('#libModal').close();
	};	
	
	$scope.changeUploadModelType = function(fileUploadModelType){
		if(fileUploadModelType == 'Bulk'){
			$scope.fileAcceptType = '.json';
		}else {
			$scope.fileAcceptType = '.xls, .xlsx,.json,.txt';
		}
		if(document.getElementById("batchExcel_browse") != null)
			document.getElementById("batchExcel_browse").value = ""; 
		$scope.batchExcel.batchExcelFile = '';
		$scope.showUploadBatchError = false;
	}
	
	$scope.uploadBatchFunc = function() {
		$scope.batchExcel;
		$scope.uploadBatchExcel = false;
		
		if($scope.batchExcel == undefined || $scope.batchExcel.batchExcelFile==""){
			 /*$scope.showMessage="Please select the file";
			 $scope.showErrorMessage = true;*/
			$scope.showUploadBatchErrorMsg="Please select a " + $scope.fileUploadModelType + " file";
			 $scope.showUploadBatchError = true;
			 $scope.uploadBatchExcel = true;
		 }else{
			 //128*1024*1024=134217728
			 if($scope.batchExcel.batchExcelFile.size>134217728){
				 $scope.showMessage="File size should not be more than 128 MB";
				 $scope.showErrorMessage = true;
			 }
			 else if($scope.fileUploadModelType == 'Bulk' && $scope.batchExcel.batchExcelFile.name.split('.').pop()!='json'){
				 $scope.showUploadBatchErrorMsg="invalid file type for Bulk upload" ;
				 $scope.showUploadBatchError = true;
				 $scope.uploadBatchExcel = true;
			 }
			 else if($scope.fileUploadModelType == 'Batch'  && $scope.batchExcel.batchExcelFile.name.split('.').pop()!='xls' && $scope.batchExcel.batchExcelFile.name.split('.').pop()!='xlsx' && $scope.batchExcel.batchExcelFile.name.split('.').pop()!='txt' && $scope.batchExcel.batchExcelFile.name.split('.').pop()!='json'){
					//alert($scope.batchExcel.batchExcelFile.name.split('.').pop());
					 $scope.showUploadBatchErrorMsg="invalid file type for Batch upload" ;
					 $scope.showUploadBatchError = true;
					 $scope.uploadBatchExcel = true;
				 }
			 else{
				 if($scope.fileUploadModelType == 'Batch'){
					 batchDashBoardService.executeBatchFile($scope.batchExcel.batchExcelFile).then(
							 function(responseData) {
								 if(responseData.error){
									 $scope.showMessage=" ErrorCode: "+responseData.errorCode+" Error in loading data : " +responseData.message.replace("\n", "<BR>");
									 $scope.showErrorMessage = true;
								 }else{
									 $scope.showMessage="Execution Batch Id :: " +responseData.response;
									 $scope.getPagedBatchTxns();
								 }
							 }, 
							 function(responseData) {
								 alert('System Failed: ' + JSON.stringify(responseData));
							 }
					 );	
				 }
				 else{
					 batchDashBoardService.executeBulkFile($scope.batchExcel.batchExcelFile).then(
							 function(responseData) {
								 if(responseData.error){
									 $scope.showMessage=" ErrorCode: "+responseData.errorCode+" Error in loading data : " +responseData.message.replace("\n", "<BR>");
									 $scope.showErrorMessage = true;
								 }else{
									 $scope.showMessage="Execution Batch Id :: " +responseData.response;
									 $scope.getPagedBatchTxns();
								 }
							 }, 
							 function(responseData) {
								 alert('System Failed: ' + JSON.stringify(responseData));
							 }
					 );	
				 }
			 }
		 }
	};
	
	
	$scope.batchSelectionRecordCntLimit;
	$scope.batchDwnldIOShow = false;
	
	/**
	 * This method is used to download selected input/output for the batch transactions
	 **/
	$scope.downloadSelectedBatchItems = function() { 
		if($scope.gridOptions.selectedRows.length>0){
			var model = $scope.gridOptions.api.getModel();
			var count = model.getVirtualRowCount();
			var txnIds = "";
			var countSelTrans = 0;
			for (var i = 0; i < count; i++) {
                var row = model.getVirtualRow(i);
                if ($scope.gridOptions.api.isNodeSelected(row) && row.data.transactionMode!='Bulk') {
                	countSelTrans = countSelTrans+1;
    				txnIds += row.data.id+",";                                              	
                }
			}

			if (countSelTrans > $scope.batchSelectionRecordCntLimit) {
				$scope.sameModelVerErrorMsg = '';
				 $scope.tranCntErrorMsg = "Please select upto maximum " + $scope.batchSelectionRecordCntLimit + " transactions for 'Download I/O Files'.";
				 $scope.batchDwnldIOShow = true;
			}  else if (countSelTrans == 0) {
				$scope.tranCntErrorMsg = '';
				 $scope.sameModelVerErrorMsg = "Please select atleast one batch transaction for 'Download I/O Files'.";
				 $scope.batchDwnldIOShow = true;
			}else {
				batchDashBoardService.downloadSelectedBatchItems(txnIds).then(
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
			 $scope.sameModelVerErrorMsg = "Please select atleast one batch transaction for 'Download I/O Files'.";
			 $scope.batchDwnldIOShow = true;
		}
	};
	
	// for setting the record count limit 
	$scope.setBatchSelectRecordLimit = function () {
		batchDashBoardService.getBtchSelectionRecordCntLimit().then(
			function(responseData) {
				if (responseData.error) {
					alert(" ErrorCode: " + responseData.errorCode
							+ " \n Error in retrieving the operator list : "
							+ responseData.message);
				} else {
					$scope.batchSelectionRecordCntLimit = responseData.response;
				}
			}, function(responseData) {
				alert('Failed: ' + responseData);
			}
		);
	};
	
	$scope.setBatchSelectRecordLimit();
	
	$scope.modalVarBtchIoErr = '';
	$scope.clearModalBtchDwnlIoErr = function() {
		$scope.modalVarBtchIoErr = "modal";
		$scope.batchDwnldIOShow = false;
		// $('#libModal').hide();
		$("#batchDwnldIOPopId").modal("hide");
		//$('#libModal').close();
	};
	
 /** Paging Operations */
    
    $scope.setToFirstPage = function(){
		$scope.pagingOptions.currentPage = 1;
	};
	
	$scope.setPreviousPage = function(){
		var page = $scope.pagingOptions.currentPage;
        $scope.pagingOptions.currentPage = Math.max(page - 1, 1);
	};
	
	$scope.setNextPage = function(){
		var page = $scope.pagingOptions.currentPage;
        if ($scope.batchTxns > 0) {
            $scope.pagingOptions.currentPage = Math.min(page + 1, $scope.totalPages);
        } else {
            $scope.pagingOptions.currentPage++;
        }
	};
	
	$scope.setToLastPage = function(){
        $scope.pagingOptions.currentPage = $scope.totalPages;
	};
	
	/** To invalidate batch */
	
	/*$scope.invalidateBatch=function(){    	
    	if($scope.mySelection.length==0 ){
            var dialogOptions = {
                                headerText: 'No Batch selected ....',
                                bodyText: 'Please select the batch to invalidate',
                                };
                  dialogService.showModalDialog({}, dialogOptions);    		
    	}else{   
    		if($scope.mySelection[0].status=='INVALID' || $scope.mySelection[0].status=='IN_PROGRESS'){
    			 var dialogOptions = {
							closeButtonText: 'Cancel',
							actionButtonText: 'Confirm',
							headerText: ' Action confirming ....',
							bodyText: ' Warning:  Please note that this Batch is currently is In-Progress state, You must be absolutely'
								+ ' sure that this is not getting running before invalidating it. If the batch is still running then it'
								+ ' will get the batch into an inconsistent state and might provide incorrect status information.'
							    + ' Please use caution before using Invalidate option. Would you like to continue and Invalidate? '
,
							callback: function () {
								batchDashBoardService.invalidateBatch($scope.mySelection[0].id).then(
					       				 function(responseData) {
					       					 if(responseData.error){
					       						 $scope.showMessage=true;
					       						 $scope.message=" Error in while invalidating the batch  : " +responseData.message;
					       					 }else{    						
					       						 $scope.showMessage=true;
					       						 $scope.message=" Successfuly invalidated the batch  : " +$scope.mySelection[0].id;
					       					 }
					       			 }, 
					       			 function(responseData) {
					       				 alert('System Failed: ' + responseData);
					       			 }
					       			
					       		);
								
							}
					};
					dialogService.showModalDialog({}, dialogOptions);
    			
    		}else{
    			var dialogOptions = {
                        headerText: ' ERROR ',
                        bodyText: 'Cannot invalidate a batch which has finished processing '
                        };
          dialogService.showModalDialog({}, dialogOptions); 		
    		}
    	}
    };*/
	
	function execTimeComparator(execTime1, execTime2) {
		var execTime1Number = getExecTimeNumber(execTime1);
		var execTime2Number = getExecTimeNumber(execTime2);
		
		if (execTime1Number===null && execTime2Number===null) {
            return 0;
        }
        if (execTime1Number===null) {
            return -1;
        }
        if (execTime2Number===null) {
            return 1;
        }

        return execTime1Number - execTime2Number;
	}
	
	function getExecTimeNumber(execTime) {
		if (execTime == null || execTime == "") {
			return null;
		} else {
			return execTime.split(' ')[0];
		}
	}
	
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
    
/** Sorting Logic*/
	
    $scope.terminnateSelectedItems = function() {    	
		$scope.showErrorMessage = false;
		$scope.showMessage = '';
		$scope.showSuccesssMessage = false;
		
		if($scope.gridOptions.selectedRows.length == 1){
			var finalData = angular.copy($scope.gridOptions.selectedRows);
			
			if ((finalData[0].status != 'Queued' && finalData[0].status != 'In Execution') || finalData[0].transactionMode=='Bulk') {
				$scope.showErrorMessage = true;
				$scope.showMessage = 'Please select a single batch transaction row with \'In progress\' status.';				
			} else {
				$dialogs.confirm('Please Confirm','<span class="confirm-body">Terminating will stop further transaction executions for this batch record. Do you want to continue?</span>')
				.result.then(function(btn){				
					batchDashBoardService.terminnateBatch(finalData[0].id).then(
							function(responseData){					
								if(!responseData.error){
									$scope.getPagedBatchTxns();
									$log.error(responseData.message);
									$scope.showSuccesssMessage = true;
									$scope.showMessage = responseData.message;
								} else {
									$log.error(responseData.message);
									$scope.showErrorMessage = true;
									$scope.showMessage = responseData.message;
								}
							},
							function(errorData){
								$log.error(errorData);
								$scope.showErrorMessage = true;
								$scope.showMessage = errorData;
							}
					);
		        });
			}
		}else{
			$scope.showErrorMessage = true;
			$scope.showMessage = 'Please select a single batch transaction row with \'In progress\' status.';
		}
	};
	
	// download usage report
	$scope.showUsgRprtRerunPopup = false;
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
            	$scope.selectedTransForUsgRprt.push(row.data.id);
            }
		}
	};
	
	//methods for showing the download usage report pop up
	$scope.downloadbatchUsageReportModalShow = function() {
		$scope.showUsgRprtRerunPopup = true;
		$scope.getSelectedTransactions();
		$scope.totalSearchedTransactions = $scope.totalCount;
		$scope.selectedTransForUsgRprtCnt = $scope.selectedTransForUsgRprt.length;
		document.getElementById("all").disabled = false;
		if ($scope.selectedTransForUsgRprt.length > 0) {
			document.getElementById("selected").disabled = false;
		} else {
			document.getElementById("selected").disabled = true;
		}
		$scope.all.transactions = '';
	};
	
	//method for download-button in downloading usage report pop-up to download usage report 
	$scope.downloadBatchUsageReport = function() { 
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
        		batchDashBoardService.downldbatchUsageRprtForSelected($scope.selectedTransForUsgRprt,sortColumn, descending, cancelRequestId).then(
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
        $("#batchdwnldUsgRprt").modal("hide");
    };
    
	$scope.getAllTransactions = function() {
		$scope.message="";
		$scope.errorMessage="";
		var valid = true;
		$scope.downldUsageRprtUsnfFltr(null);
	};
	
	 //method for downloading usage reportusing search filter
    $scope.downldUsageRprtUsnfFltr= function(advanceTransactionFilter) {
    	batchDashBoardService.downldUsageRprtUsngFltr($scope.pageInfo, advanceTransactionFilter, $scope.pagingOptions).then(
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
    
    
}];