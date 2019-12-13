
'use strict';
var ReportController = function($scope, $log, $timeout, $window, $filter, reportService, freezeServices) {
	$scope.meassageClass="error-msg";
	$scope.excelClass="disable-html";
	$scope.meassage="";
	$scope.currentYear= new Date().getFullYear();
	$scope.modelList=[];
	$scope.versionList=[];
	$scope.searchOption={};
	$scope.searchOption.searchUID=null;
	$scope.searchOption.reportType="Tenant Usage Report";
	$scope.searchOption.model="All";
	$scope.searchOption.version="All";
	$scope.searchOption.status="All";
	$scope.searchOption.startDate="";
	$scope.searchOption.endDate="";
	$scope.searchOption.isCustomDate=false;
	$scope.searchOption.includeTest=false;
	$scope.selectAll = false;
	

	$scope.cancelAjax=false;
	$scope.columnMsg="";
	
	$scope.grid={};
	$scope.grid.data=[];
	$scope.grid.pagingOptions = {
            pageSizes: [50, 100, 200],
            pageSize: 50,
            currentPage: 1,
            pageSet:1,
            totalPages:0,
            totalServerItems:0,
            sortColumn:"",
            descending:false
        };
	$scope.grid.columnDefs = [
	                        {field : 'runDateTime', displayName : 'DATE TIME', visible: true, order:0},
	  						{field : 'tenantTransactionId', displayName : 'TENANT TRANSACTION ID', visible: true, order:0 },
							{field : 'model', displayName : 'MODEL', visible: true, order:0 },
	  						{field : 'modelVersion', displayName : 'MODEL VERSION', visible: true, order:0 },
	  						{field : 'processingStatus', displayName : 'PROCESSING STATUS', visible: true, order:0 },
	  						{field : 'umgTransactionId', displayName : 'UMG TRANSACTION ID', visible: true, order:0 },
	  						{field : 'tenantId', displayName : 'TENANT ID', visible: false, order:0 },
	  						{field : 'transactionMode', displayName : 'TRANSACTION MODE', visible: false, order:0 },
	  						{field : 'batchId', displayName : 'BATCH ID', visible: false, order:0 },
	  						{field : 'failureReason', displayName : 'REASON', visible: false, order:0 },
	  						{field : 'processingTime', displayName : 'PROCESSING TIME', visible: false, order:0 },
	  						{field : 'transactionType', displayName : 'TRANSACTION TYPE', visible: true, order:0 }
	  						];
	
	
	/**
	 * watching the start date for formating 
	 */
	 $scope.$watch('searchOption.startDate', function (newVal, oldVal) {
		 $scope.searchOption.startDate=$filter('date')(newVal,"MMM-dd-yyyy");
	     }, true);
	 /**
		 * watching the end date for formating 
		 */
	 $scope.$watch('searchOption.endDate', function (newVal, oldVal) {
		 $scope.searchOption.endDate=$filter('date')(newVal,"MMM-dd-yyyy");
	     }, true);
	
	 /**
	* watching the column definition, to restrict at lease one column will be visible all the time 
	*/
	 $scope.$watch('grid.columnDefs', function (newVal, oldVal) {
		   var minVisibleFlag=false;
		   for(var index in newVal ){
			   var col=$scope.grid.columnDefs[index];
			  if(col.visible){
				  minVisibleFlag=true; 
				  break;
			  } 
		   }
		   if(minVisibleFlag==false){
			   $scope.grid.columnDefs=oldVal;
			   $scope.columnMsg=" Minimum one column should be slected ";
			   mytimeout = $timeout(function(){
				   $scope.columnMsg="";
			   },5000);
		   }
		   
	     }, true);
	
	
	 /**
	  * init method
	  ***************************************************************/
    $scope.initSetup = function() {

	};
	 
	 
	 /**
	  * this method will invoke the sorting for each column
	  */
	 $scope.sort = function(sortField){
		   $scope.grid.pagingOptions.currentPage=1;
		   $scope.grid.pagingOptions.pageSet=1;
			
		   for(var index in $scope.grid.columnDefs ){
			   var col=$scope.grid.columnDefs[index];
			  if(col.displayName===sortField){
				   $scope.searchOption.sortColumn=sortField;
				  if(col.order==0 || col.order==2){
					  $scope.searchOption.descending=false;
					  col.order =1;
				  }else{
					  $scope.searchOption.descending=true;
					  col.order =2;
				  }
			  }else{
				  col.order = 0; 
			  } 
		   }
		   
		   if($scope.grid.data.length > 0){
			   if($scope.searchType == 0)
			    	$scope.search();
			    else
			    	$scope.searchByTxnId($scope.searchOption.searchString);
		   }
	 };	
		
	 /**
	  * timeout for the search loading button
	  */
	  var mytimeout;
	    $scope.onTimeout = function(){
				$scope.cancelAjax=true;
				$timeout.cancel(mytimeout);
	   };
	    
	    
	 /**
	  * This method will search usage report based on search option
	  */
	 $scope.search = function(){
		 $scope.selectedTransactions.length = 0;
		 $scope.meassage="";
		 if($scope.searchOption.isCustomDate==false){
		 var monthSelected=false;
		 if($scope.searchOption.month!=undefined && $scope.searchOption.month!=""){
			 monthSelected=true;
		 }
		 var yearSelected=false;
		 if($scope.searchOption.year!=undefined && $scope.searchOption.year!=""){
			 yearSelected=true;
		 }
		 if( (monthSelected==true&&yearSelected==false)||((monthSelected==false&&yearSelected==true))){
			 $scope.meassage="Click on search after selecting both month and year";
			 return;
		 }
		 
		 if(monthSelected && yearSelected){
				 $scope.searchOption.startDate=$scope.searchOption.month+$scope.searchOption.year;
			 }
			
		 }

		 mytimeout = $timeout($scope.onTimeout,2000); 
		 $scope.cancelAjax=false;
		 //getting the UID from server
		 reportService.getSearchUID().then(
				 function(responseData) {
					 	if(responseData.error){
    					var msg= responseData.message.replace("\n", "<BR>");
    					 $scope.meassage=msg.trim();
					 	}else{
					 		$scope.searchOption.searchUID=responseData.response;
					 		// search will happen only after getting the searchUID from server
						    reportService.search($scope.searchOption, $scope.grid.pagingOptions).then(
								 function(responseData) {
									 if(responseData.error){
										var msg=responseData.message.replace("\n", "<BR>");
				    					 $scope.meassage=msg.trim();
				    					 $scope.grid.data=[];
									 }else{
										//alert(JSON.stringify(responseData));
										 if(responseData.message=="Cancelled"){
											// this is cancelled request, do nothing 
										 }else if(responseData.message=="Done"){
											 $scope.selectAll = false;
											 $scope.grid.data = responseData.response.transactionInfoList; 
											 $('#data_grid').scrollTop(0);
											 //alert(JSON.stringify(responseData));
											 if(responseData.response.pagingInfo.resetDatesAtUI!=undefined&&responseData.response.pagingInfo.resetDatesAtUI!=""){
												 if(responseData.response.pagingInfo.resetDatesAtUI){
													 $scope.searchOption.isCustomDate=true;
													 $scope.searchOption.startDate=responseData.response.pagingInfo.startDate;
													 $scope.searchOption.endDate=responseData.response.pagingInfo.endDate;
												 }
											 }
											 $scope.grid.pagingOptions.totalServerItems=responseData.response.pagingInfo.totalElements;
											 $scope.grid.pagingOptions.totalPages=responseData.response.pagingInfo.totalPages;
											 $scope.grid.pagingOptions.currentPage=responseData.response.pagingInfo.page;
											 $scope.grid.pagingOptions.pageSet=responseData.response.pagingInfo.pageSet;
											 if($scope.grid.data.length<=0){
												 $scope.meassage="No record found ...";
											 }
										 }else{
											 var msg=responseData.message.replace("\n", "<BR>");
					    					 $scope.meassage=msg.trim();
					    					 $scope.grid.data=[];
										 }
										 
										 if($scope.grid.data.length<=0){
											 $scope.grid.pagingOptions.pageSet=1;
											 $scope.grid.pagingOptions.currentPage=1;
											 $scope.grid.pagingOptions.totalPages=0;
											 $scope.excelClass="disable-html";
										 }else{
											 $scope.excelClass="";
										 }
									 }
								 }, 
								 function(responseData) {
									 alert('System Failed: ' + responseData);
								 });
					 	 
					 	 
					 	 
					    }
				 }, 
				 function(responseData) {
					 alert('System Failed: ' + responseData);
				 });

	 };
	 
	 /**
	  * This method will search usage report based on search option
	  */
	 $scope.cancel = function(){
		if($scope.searchOption.searchUID!=undefined && $scope.searchOption.searchUID!=""){
			freezeServices.unfreezeScreen('searchFreezeScreen');
		 reportService.cancelSearch($scope.searchOption.searchUID).then(
				 function(responseData) {
					 	if(responseData.error){
    					var msg= responseData.message.replace("\n", "<BR>");
    					 $scope.meassage=msg.trim();
					 	}
				 }, 
				 function(responseData) {
					 alert('System Failed: ' + responseData);
				 });	
		}else {
			$scope.meassage="no valid Request Id found ..";
		}
	 };

	 /**
	  * This method will search usage report based on search option
	  */
     $scope.clearSearchOption = function(){
    	    $scope.meassage="";
    	    $scope.searchOption={};
    		$scope.searchOption.reportType="Tenant Usage Report";
    		$scope.searchOption.model="All";
    		$scope.searchOption.version="All";
    		$scope.searchOption.status="All";
    		$scope.searchOption.includeTest=false;
    		$scope.searchOption.isCustomDate=false;
    		$scope.selectedTransactions=[];
    		$scope.selectedTransactionModelNames=[];
    		$scope.excelClass="disable-html";
	 };
	 
	 /**
	  * This method will search usage report based on search option
	  */
	 $scope.getAllModels = function(){
		 $scope.cancelAjax=false;
		 reportService.getAllModels().then(
				 function(responseData) {
					 	if(responseData.error){
    					var msg= responseData.message.replace("\n", "<BR>");
    					 $scope.meassage=msg.trim();
    					 
					 	}else{
					 	$scope.modelList=responseData.response;
					    }
				 }, 
				 function(responseData) {
					 alert('System Failed: ' + responseData);
				 });	
	 };
	 /**
	  * This method will search usage report based on search option
	  */
	 $scope.getVerionsforTheModel = function(){
		 $scope.cancelAjax=false;
		 reportService.getVerionsforTheModel($scope.searchOption.model).then(
				 function(responseData) {
					 	if(responseData.error){
    					var msg= responseData.message.replace("\n", "<BR>");
    					 $scope.meassage=msg.trim();
					 	}else{
					 	$scope.versionList=responseData.response;
					 	$scope.searchOption.version="All";
					    }
				 }, 
				 function(responseData) {
					 alert('System Failed: ' + responseData);
				 });
	 };
	 
     $scope.initSetup = function(){
    	 $scope.getAllModels();
	 };
	 
	 $scope.rerunDisabledStatus = true;
	 
	 $scope.disableAndEnableExportOptionsAll = function() {
		 
		 if (document.getElementById("all").checked) {
			 document.getElementById("billing").disabled = false;
			 document.getElementById("rerun").disabled = false;
			 
			 if ($scope.grid.pagingOptions.totalServerItems == 0) {
				 document.getElementById("rerun").disabled = true;
				 document.getElementById("rerun").checked = false;
				 
				 document.getElementById("billing").disabled = true;
				 document.getElementById("billing").checked = false;
			 } else {
				 var modelNameAndVersion = null;
				 var differentModels = true;
				 
				 angular.forEach($scope.grid.data,function(txn){
					 if (modelNameAndVersion == null) {
						 modelNameAndVersion = txn.model + txn.modelVersion;
					 } else {
						 if (modelNameAndVersion != null && modelNameAndVersion != (txn.model + txn.modelVersion)) {
							 differentModels = false;
						 }
					 }
					 
					 if (modelNameAndVersion != null && differentModels == true) {
						 document.getElementById("rerun").disabled = false;
					 } else {			
						 document.getElementById("rerun").disabled = true;
						 document.getElementById("rerun").checked = false;
					 }
				 });
			 }			 
		 }		 
	 };
	 
	 $scope.disableAndEnableExportOptionsSelected = function() {
		 if (document.getElementById("selected").checked) {
			 if ($scope.selectedTransactions.length == 0) {
				 document.getElementById("rerun").disabled = true;
				 document.getElementById("rerun").checked = false;
				 
				 document.getElementById("billing").disabled = true;
				 document.getElementById("billing").checked = false;
			 } else {
				 document.getElementById("rerun").disabled = false;

				 var modeleName = null;
				 var differentModels = true;
				 
				 angular.forEach($scope.selectedTransactionModelNames, function(model){
					 if (modeleName == null) {
						 modeleName = model;
					 } else {
						 if (modeleName != null && model != null && modeleName != model) {
							 differentModels = false;
						 }
					 }
				 	});
				 
				 if (modeleName != null && differentModels == true) {
					 document.getElementById("rerun").disabled = false;
				 } else {			
					 //$scope.exportMsg = 'Re-run cannot be executed for multiple models.';
					 document.getElementById("rerun").disabled = true;
					 document.getElementById("rerun").checked = false;
				 }
			 }
		 }
	 };
	 
	 $scope.exportBillingOrRerunReport = function() {
		 $scope.exportMsg = null;
		 
		 var hasTrnasctions = true;
		 if (document.getElementById("selected").checked) {
			 if ($scope.selectedTransactions.length == 0) {
				 hasTrnasctions = false;
			 }
		 } 
		 
		 if (hasTrnasctions == true) {
			 if (document.getElementById("billing").checked) {
				 this.downloadUsageReport();
				 $("#exportModal").modal("hide");
			 } else if (document.getElementById("rerun").checked) {
				 this.downloadRerunReport();
			 } else {
				 $scope.exportMsg = 'Please select a report.';
			 }
		 } else {
			 $scope.exportMsg = 'Please select atleast one transaction.';
		 }
	 };
	 
	 $scope.downloadRerunReport = function() {
    	 var url = "modelReport/rerun?selectedTransactionList="+$scope.selectedTransactions;
    	 $window.location.href=url;		
		 $("#exportModal").modal("hide");
	 };
	 
	    /**
	     * This method is used to download the excel report 
	     *************************************************************************/
	     $scope.downloadUsageReport = function() { 
	    	 
	    	 var url = '';
	    	 var cancelRequestId="1";                   
	         var sortColumn=($scope.searchOption.sortColumn!=undefined)?$scope.searchOption.sortColumn:"Date Time";
	         var descending=($scope.searchOption.descending!=undefined)?$scope.searchOption.descending:false;
	    	 
	         if (document.getElementById("all").checked) {
		    	 if($scope.searchType == 0){
			    	 
			    	 $scope.meassage="";
					 if($scope.searchOption.isCustomDate==false){
					 var monthSelected=false;
					 if($scope.searchOption.month!=undefined && $scope.searchOption.month!=""){
						 monthSelected=true;
					 }
					 var yearSelected=false;
					 if($scope.searchOption.year!=undefined && $scope.searchOption.year!=""){
						 yearSelected=true;
					 }
					 if( (monthSelected==true&&yearSelected==false)||((monthSelected==false&&yearSelected==true))){
						 $scope.meassage="Date selected is not  properly";
						 return;
					 }
					 
					 if(monthSelected && yearSelected){
							 $scope.searchOption.startDate=$scope.searchOption.month+$scope.searchOption.year;
						 }
						
					 }
					 
					 var startDate=null; 
					 var endDate=null;
					 if($scope.searchOption.startDate!=undefined && $scope.searchOption.startDate!=""){
						 startDate = $scope.searchOption.startDate+" 00:00";
					 }
					 if($scope.searchOption.endDate!=undefined && $scope.searchOption.endDate!=""){
						 endDate = $scope.searchOption.endDate+" 23:59";
					 }
			         
			         url = "tenantUsage/downloadUsageReportByFilter?tenantModelName="+$scope.searchOption.model
			             														+"&fullVersion="+$scope.searchOption.version
			             														+"&runAsOfDateFromString="+startDate
			             														+"&runAsOfDateToString="+endDate  
			             														+"&transactionStatus="+$scope.searchOption.status
			             														+"&sortColumn="+sortColumn
			             														+"&descending="+descending
			             														+"&isCustomDate="+$scope.searchOption.isCustomDate
			             														+"&cancelRequestId="+cancelRequestId
			             														+"&includeTest="+$scope.searchOption.includeTest;
			    	 }
			    	 else{url = "tenantUsage/downloadUsageReportBySearch?searchString="+$scope.searchOption.searchString
						+"&sortColumn="+sortColumn
						+"&descending="+descending
						+"&cancelRequestId="+cancelRequestId;
			    	 }	        	 
	         } else {
	        	 url = "tenantUsage/downloadUsageReportByTransactionList?selectedTransactionList="+$scope.selectedTransactions
					+"&sortColumn="+sortColumn
					+"&descending="+descending
					+"&cancelRequestId="+cancelRequestId;
	         }
	         $window.location.href=url;
	     };

		 /**
			 * ========================================== 
			 * changing the setting 
			 */
		 $scope.dateSettingChanges=function(){
			 $scope.searchOption.isCustomDate=!$scope.searchOption.isCustomDate;
				$scope.searchOption.startDate="";
				$scope.searchOption.endDate="";
				$scope.searchOption.year="";
				$scope.searchOption.month="";
		 };	
		 
		//----------- Main pagination starts ---------------------------

		 $scope.pages = [];
		 $scope.pages[0] = $scope.grid.pagingOptions.currentPage + 1;
		 $scope.pages[1] = $scope.pages[0] + 1;
		 $scope.pages[2] = $scope.pages[1] + 1;
		
		$scope.resetSearch = function(){
			 $scope.grid.pagingOptions.currentPage = 1;
			 $scope.pages[0] = $scope.grid.pagingOptions.currentPage + 1;
			 $scope.pages[1] = $scope.pages[0] + 1;
			 $scope.pages[2] = $scope.pages[1] + 1;
		 };
		
		
		var pageNumbers = $scope.pages.length;
		
		$scope.setNext = function(){
			$log.info('Setting Next Page ...');
			if($scope.grid.pagingOptions.currentPage != $scope.grid.pagingOptions.totalPages){
			$scope.grid.pagingOptions.currentPage += 1;
				if(!pageExist($scope.grid.pagingOptions.currentPage) && $scope.grid.pagingOptions.currentPage != $scope.grid.pagingOptions.totalPages)
					for(var i = 0; i < pageNumbers; i++)
						$scope.pages[i] = $scope.grid.pagingOptions.currentPage + i;
				
				if($scope.searchType == 0)
			    	$scope.search();
			    else
			    	$scope.searchByTxnId($scope.searchOption.searchString);
			}
		};
		
		$scope.setNextPages = function(){
			$log.info('Setting Next Page Set ...');
			if(!pageExist($scope.grid.pagingOptions.totalPages-1))
			for(var i = 0; i < pageNumbers ; i++)
				$scope.pages[i] += pageNumbers;
		};
		
		$scope.setPrevious = function(){
			$log.info('Setting Previous Page ...');
			if($scope.grid.pagingOptions.currentPage != 1){
			$scope.grid.pagingOptions.currentPage -= 1;
			if(!pageExist($scope.grid.pagingOptions.currentPage) && $scope.grid.pagingOptions.currentPage != 1)
				for(var i = 0, j = pageNumbers -1; i < pageNumbers; i++, j--)
					$scope.pages[i] = $scope.grid.pagingOptions.currentPage - j;
			
			if($scope.searchType == 0)
		    	$scope.search();
		    else
		    	$scope.searchByTxnId($scope.searchOption.searchString);
			}
		};
		 
		$scope.setPreviousPages = function(){
			$log.info('Setting Previous Page Set ...');
			if(!pageExist(2))
			for(var i = 0; i < pageNumbers; i++)
				$scope.pages[i] -= pageNumbers;
		};
		
		var pageExist = function(page){
			for(var i = 0; i < pageNumbers; i++){
				if($scope.pages[i] ==  page)
					return true;
			}
			return false;
		};
		
	
     /**
	 * ========================================== change number of row in
	 * the page
	 */
	 $scope.pageSizeChanged=function(){
		 	$scope.resetSearch();
		    $scope.grid.pagingOptions.pageSet=1;
		    if($scope.searchType == 0)
		    	$scope.search();
		    else
		    	$scope.searchByTxnId($scope.searchOption.searchString);
	 };	
	 
	 $scope.fetchPagedData = function(){
		 $log.info('Fetching Data for Page : '+$scope.grid.pagingOptions.currentPage);
		 if($scope.searchType == 0)
		    	$scope.search();
		    else
		    	$scope.searchByTxnId($scope.searchOption.searchString);
	 };
	
	//----------- Main pagination ends ---------------------------
	 
	 $scope.steps = ['one', 'two', 'three', 'four', 'five', 'six'];
	 $scope.step = 0;
	 
	 $scope.isCurrentStep = function(step) {
	        return $scope.step === step;
	    };
	 
	 $scope.setCurrentStep = function(step) {
	        $scope.step = step;
	        $('#viewport').scrollTop(0);
	    };
	 
	 $scope.getCurrentStep = function() {
	        return $scope.steps[$scope.step];
	    };
	 
	 $scope.selectedTxn = '';
	 
	 $scope.launchReportDialog = function(txn, index){
		 $scope.selectedTxn = txn;
		 $scope.clientTxnId = $scope.selectedTxn.tenantTransactionId;
		 $scope.umgTxnId = $scope.selectedTxn.umgTransactionId;
		 $scope.fromIndex = ($scope.grid.pagingOptions.currentPage-1)*$scope.grid.pagingOptions.pageSize;
		 $scope.tillIndex = Math.min($scope.fromIndex + $scope.grid.pagingOptions.pageSize, $scope.grid.pagingOptions.totalServerItems) -1;
		 $scope.currentIndex = $scope.fromIndex + index;
		 setReports($scope.selectedTxn);
		 $scope.setCurrentStep(0);
	 };
	 
	 $scope.setNextTxn = function(index){
		 $log.info('Setting Next Transaction...');
		 $scope.currentIndex = Math.min(index+1, $scope.grid.pagingOptions.totalServerItems-1);
		 if($scope.currentIndex >= $scope.fromIndex && $scope.currentIndex <= $scope.tillIndex){
			 $scope.selectedTxn = $scope.grid.data[$scope.currentIndex-$scope.fromIndex];
			 $scope.clientTxnId = $scope.selectedTxn.tenantTransactionId;
			 $scope.umgTxnId = $scope.selectedTxn.umgTransactionId;
			 setReports($scope.selectedTxn);
			 $scope.setCurrentStep(0);
		 }
		 else
			 setNextPreviousReports();
	 };
	 
	 $scope.setPreviousTxn = function(index){
		 $log.info('Setting Previous Transaction...');
		 $scope.currentIndex = Math.max(index-1, 0);
		 if($scope.currentIndex >= $scope.fromIndex && $scope.currentIndex <= $scope.tillIndex){
			 $scope.selectedTxn = $scope.grid.data[$scope.currentIndex-$scope.fromIndex];
			 $scope.clientTxnId = $scope.selectedTxn.tenantTransactionId;
			 $scope.umgTxnId = $scope.selectedTxn.umgTransactionId;
			 setReports($scope.selectedTxn);
			 $scope.setCurrentStep(0);
		 }
		 else
			 setNextPreviousReports();
	 };
	 
	 var setNextPreviousReports = function(){
		 $log.info('Setting reports for UMG Transaction which is not loaded...');
		 clearTxnReports();
		 reportService.getIndexedTxn($scope.searchOption, $scope.grid.pagingOptions,$scope.currentIndex,$scope.searchType).then(
				 function(responseData){
					 if(!responseData.error && responseData.response != null){
						 $scope.clientTxnId = responseData.response.clientTransactionID;
						 $scope.umgTxnId = responseData.response.transactionId;
						 $scope.tabularInputData = responseData.response.inputTabularInfo;
						 $scope.tabularOutputData = responseData.response.outputTabularInfo;
						 $scope.tenantInputJson = responseData.response.tenantInput;
						 $scope.tenantOutputJson = responseData.response.tenantOutput;
						 $scope.modelInputJson = responseData.response.modelInput;
						 $scope.modelOutputJson = responseData.response.modelOutput;
						 $('#viewport').scrollTop(0);
					 }
					 else{
						 $scope.clientTxnId = 'NOT FOUND';
						 $scope.umgTxnId = '';
						 clearTxnReports();
					 }
					 $scope.setCurrentStep(0);
				 },
				 function(errorData){
					 $log.error('Error in Fecting Record');
				 }
		 );
	 };
	 
	 var setReports = function(txn){
		 $log.info('Setting reports for UMG Transaction ID : '+txn.umgTransactionId);
		 clearTxnReports();
		 reportService.getReportsForTxn(txn.umgTransactionId).then(
				 function(responseData){
					 if(!responseData.error && responseData.response != null){
						 $scope.tabularInputData = responseData.response.inputTabularInfo;
						 $scope.tabularOutputData = responseData.response.outputTabularInfo;
						 $scope.tenantInputJson = responseData.response.tenantInput;
						 $scope.tenantOutputJson = responseData.response.tenantOutput;
						 $scope.modelInputJson = responseData.response.modelInput;
						 $scope.modelOutputJson = responseData.response.modelOutput;
						 $('#viewport').scrollTop(0);
					 }
					 else{
						 clearTxnReports();
					 }
				 },
				 function(errorData){
					 $log.error(errorData);
				 }
		 );
	 };
	 
	 
	 var clearTxnReports = function(){
		 $scope.tabularInputData = [];
		 $scope.tabularOutputData = [];
		 $scope.tenantInputJson = 'NO DATA FOUND';
		 $scope.tenantOutputJson = 'NO DATA FOUND';
		 $scope.modelInputJson = 'NO DATA FOUND';
		 $scope.modelOutputJson = 'NO DATA FOUND';
	 };
	 
	 $scope.exportReport = function(){
		 var umgTxnId =  $scope.umgTxnId;
		 switch($scope.step){
		 case 0: case 1: 
			 $log.info('Exporting Tabular Input Output For UMG Transaction Id : '+umgTxnId);
			 $window.location.href="modelReport/export?reportName=tabularInputOutput&txnId="+umgTxnId;
			 break;
		 case 2 : 
			 $log.info('Exporting Tenant Input For UMG Transaction Id : '+umgTxnId);
			 $window.location.href="modelReport/export?reportName=tenantInput&txnId="+umgTxnId;
			 break;
		 case 3 : 
			 $log.info('Exporting Model Input For UMG Transaction Id : '+umgTxnId);
			 $window.location.href="modelReport/export?reportName=modelInput&txnId="+umgTxnId;
			 break;
		 case 4 : 
			 $log.info('Exporting Tenant Output For UMG Transaction Id : '+umgTxnId);
			 $window.location.href="modelReport/export?reportName=tenantOutput&txnId="+umgTxnId;
			 break;
		 case 5 : 
			 $log.info('Exporting Model Output For UMG Transaction Id : '+umgTxnId);
			 $window.location.href="modelReport/export?reportName=modelOutput&txnId="+umgTxnId;
			 break;
		 }
	 };
	 
	 
	 /** ....................... UMG-2847 [ BULK DOWNLOAD FUNCTIONALITY ] ................... */
	 
	 $scope.selectedTransactions = [];
	 $scope.selectedTransactionModelNames = [];
	 
	 $scope.selectAllTransactions = function(){
		 
		 if($scope.selectAll){
			 $log.info('Selecting All Transactions...');
			 angular.forEach($scope.grid.data,function(txn){
				 txn.selected = true;
				 if ($scope.selectedTransactions.indexOf(txn.umgTransactionId) == -1) {
					 $scope.selectedTransactions.push(txn.umgTransactionId);
				 }
				 
				 if ($scope.selectedTransactionModelNames.indexOf(txn.model + txn.modelVersion) == -1) {
					 $scope.selectedTransactionModelNames.push(txn.model + txn.modelVersion);				
				 }

			 });
		 }
		 else{
			 $log.info('De-selecting All Transactions...');
			 angular.forEach($scope.grid.data,function(txn){
				 txn.selected = false;
			 });
			 
			 while($scope.selectedTransactions.length != 0 ){
				 $scope.selectedTransactions.pop();
				 $scope.selectedTransactionModelNames.pop();
			 }
		 }
		 
	 };
	 
	 $scope.selectTransaction = function(txn){
		// var txnInfo = {umgTransactionId : txn.umgTransactionId, model : txn.model, modelVersion : txn.modelVersion};
		 if(txn.selected){
			 $log.info('Selecting Transaction with UMG Transaction Id : '+txn.umgTransactionId);
			 
			 if ($scope.selectedTransactions.indexOf(txn.umgTransactionId) == -1) {
				 $scope.selectedTransactions.push(txn.umgTransactionId);
			 }
			 
			 if ($scope.selectedTransactionModelNames.indexOf(txn.model + txn.modelVersion) == -1) {
				 $scope.selectedTransactionModelNames.push(txn.model + txn.modelVersion);
			 }
			 
			 if($scope.selectedTransactions.length == $scope.grid.data.length)
				 $scope.selectAll = true;
		 }else{
			 $log.info('De-selecting Transaction with UMG Transaction Id : '+txn.umgTransactionId);
			 var index = $scope.selectedTransactions.indexOf(txn.umgTransactionId);

			 if (index > -1) {
				 $scope.selectedTransactions.splice(index, 1);
				 $scope.selectedTransactionModelNames.splice(index, 1);
		 		}
			 
			 if($scope.selectedTransactions.length < $scope.grid.data.length)
				 $scope.selectAll = false;
		 }
	 };
	 
	 
	 /** --------- UMG-3050 [ Search on Tenant Portal ] -------------*/
	 
	 //$scope.txnIds = '';
	 
	 $scope.searchTypes = ['filter', 'search'];
	 $scope.searchType = 0;
	 
	 $scope.isCurrentSearchType = function(searchType) {
	        return $scope.searchType === searchType;
	    };
	 
	 $scope.setCurrentSearchType = function(searchType) {
	        $scope.searchType = searchType;
	        $scope.grid.data=[];
			$scope.grid.pagingOptions.totalPages = 0;
	        switch($scope.searchType){
			 case 0:  
				 $scope.searchOption.searchString = '';
				 $scope.excelClass="disable-html";
				 break;
			 case 1:
				 $scope.clearSearchOption();
				 break;
	        }
	    };
	 
	 $scope.getCurrentSearchType = function() {
	        return $scope.searchTypes[$scope.searchType];
	    };
	 
	 $scope.searchByTxnId = function(ids){
		 $scope.selectedTransactions.length = 0;
		 $log.info('Searcing for : '+ ids);
		 $scope.searchOption.searchString = ids;
		 if(ids != '' && angular.isDefined(ids)){
		 $scope.selectAll = false;
		 reportService.searchForTransactionId($scope.searchOption, $scope.grid.pagingOptions).then(
				 function(responseData) {
					 if(responseData.response.transactionInfoList.length == 0){
						var msg=responseData.message.replace("\n", "<BR>");
    					 $scope.meassage=msg.trim();
    					 $scope.grid.data=[];
    					 $scope.excelClass="disable-html";
					 }else{
						 $scope.grid.data = responseData.response.transactionInfoList; 
						 $('#data_grid').scrollTop(0);
						 $scope.grid.pagingOptions.totalServerItems=responseData.response.pagingInfo.totalElements;
						 $scope.grid.pagingOptions.totalPages=responseData.response.pagingInfo.totalPages;
						 $scope.grid.pagingOptions.currentPage=responseData.response.pagingInfo.page;
						 $scope.grid.pagingOptions.pageSet=responseData.response.pagingInfo.pageSet;
						 $scope.excelClass="";
					 }
				 },
				 function(errorData){
					 alert('System Failed: ' + errorData);
				 }
		 );
		 }
	 };
	 
	 $scope.launchExportDailog = function() {
		 if ($scope.selectedTransactions.length == $scope.grid.pagingOptions.totalServerItems || 
				 $scope.selectedTransactions.length == $scope.grid.pagingOptions.pageSize ||
				 $scope.selectedTransactions.length == 0) {
			 document.getElementById("all").checked = true;
			 document.getElementById("billing").checked = true;
		 } else {
			 document.getElementById("selected").checked = true;
			 document.getElementById("billing").checked = true;
		 }
         $scope.exportMsg = '';
         
         this.disableAndEnableExportOptionsAll();
         this.disableAndEnableExportOptionsSelected();
	 };

	 
	 //------------------------------------------------
	 //Default execution methods while loading the controller
	 $scope.initSetup();  	 
	 
};



