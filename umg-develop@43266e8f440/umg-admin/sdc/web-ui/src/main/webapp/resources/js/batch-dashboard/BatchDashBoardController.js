'use strict';
var BatchDashBoardController = function($scope, $log, $location, $filter, dialogService, batchDashBoardService, sharedPropertiesService) {

	$scope.showGrid=true;
	$scope.batchTransactionList=[];	
	$scope.mySelection = [];
	$scope.searchResultMessage = '';
	
	$scope.filterOption = {
			fromDate: "",
			toDate:"",
			batchId:"",
			fileName:"",
	};
	
	$scope.pagedData = {};
	$scope.grid={};
	$scope.grid.totalServerItems=0;
	$scope.grid.pagingOptions = {
            pageSizes: [50, 100,250,500],
            pageSize: 50,
            currentPage: 1,
            totalPages:1
        }; 
	
	$scope.gridOptions = {	
		rowHeight: 24,
		headerRowHeight:24,
		data: 'pagedData', 	
		pagingOptions: $scope.grid.pagingOptions,
		enableCellSelection : true,
		enableRowSelection : true,
		multiSelect : false,
		enableColumnResize : true,
		showFooter: true,
		selectedItems: $scope.mySelection,

		footerTemplate:'resources/app/batch-dashboard/batch-dashbrd-grid-footer.html',
		columnDefs : [
		              
				{
					field : 'id',
					displayName : 'Batch ID',
					cellTemplate : '<div class="ngCellText" ng-cell-text ng-class="col.colIndex()" ng-dblclick="loadTransactions(row.entity)">{{row.getProperty(col.field)}}</div>'
				},
				{
					field : 'batchInputFile',
					displayName : 'Batch Input File',
					cellTemplate : '<a href="batchDashBoard/downloadInputFile/{{row.entity.id}}/{{row.entity.batchInputFile}}">{{row.getProperty(col.field)}}</a>'
				},
				{
					field : 'batchOutputFile',
					displayName : 'Batch Output File',
					cellTemplate : '<a href="batchDashBoard/downloadOutputFile/{{row.entity.id}}/{{row.entity.batchOutputFile}}">{{row.getProperty(col.field)}}</a>'
				},
				{
					field : 'test',
					displayName : 'IS TEST'
				},
				{
					field : 'status',
					displayName : 'Status'
				},
				{
					field : 'totalRecords',
					displayName : 'Total Records'
				},
				
				{
					field : 'successCount',
					displayName : 'Success Count'
				},
				{
					field : 'failCount',
					displayName : 'Failed Count'
				},
				{
					field : 'fromDate',
					displayName : 'Start Time'
				},
				{
					field : 'toDate',
					displayName : 'End Time'
				},
				{
					displayName : 'Batch Exec Time',
					cellTemplate : '<div class="ngCellText" ng-cell-text ng-class="col.colIndex()">{{row.entity.endTime-row.entity.startTime}} ms</div>'
				},
				{
					field : 'execEnv',
					displayName : 'Execution Environment'
				},
				{
					field : 'modellingEnv',
					displayName : 'Modelling Environment'
				},

		]
	};
	/**
	 * all the initial settings will come in this method
	 */	
	$scope.intialSetup = function() {
		$scope.showMessage=false;
		
	};
	
	/**
	 * This method is to load Transactions in Transaction Dash-board, based on Batch id
	 * */
	
	$scope.loadTransactions = function(batch){
		$log.info("Redirecting to Transaction Dash-board.");
		var batchInfo = {'batchId' : batch.id};
		sharedPropertiesService.put("batchInfo",batchInfo);
		$location.path('dashboard');
	};
		
	/**
	 * This method will return batch transaction details 
	 * based on the filterOption shwon in the screen 
	 */	
    $scope.filteredBatchTransactions=function(){  
    	$scope.filterOption.fromDate = $filter('date')($scope.filterOption.fromDate,"yyyy-MMM-dd HH:mm");
		$scope.filterOption.toDate = $filter('date')($scope.filterOption.toDate,"yyyy-MMM-dd HH:mm");
    	 batchDashBoardService.filteredBatchTransactions($scope.filterOption).then( function(responseData) {
				 if(responseData.error){
					 $scope.showMessage=true;
					 $scope.message=" Error in loading data : " +responseData.message.replace("\n", "<BR>");
				 }else{					 					
					if (responseData.response.length == 0) {	
							$scope.showMessage=true;
							$scope.batchTransactionList=responseData.response;
							$scope.message = " No Records Found";
						}else{		
							$scope.showMessage=false;
							$scope.batchTransactionList=responseData.response;
							$scope.searchResultMessage = responseData.response.searchResultMessage;
						}
					 $scope.setPaging();
				 }
		 }, 
		 function(responseData) {
			 alert('System Failed: ' + responseData);
		 });	
    };
    
    
    
    /**
	 * This method will return batch transaction details 
	 * based on the filterOption show in the screen 
	 */	
    $scope.invalidateBatch=function(){    	
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
                        bodyText: 'Cannot invalidate a batch which has finished processing ',
                        };
          dialogService.showModalDialog({}, dialogOptions); 		
    		}
    	}
    };
    
	/**
	 * This method will return batch transaction details 
	 * based on the filterOption show in the screen 
	 */	
    $scope.getAllBatchData=function(){
    	 batchDashBoardService.getAllBatchData().then( function(responseData) {
				 if(responseData.error){
					 $scope.showMessage=true;
					 $scope.message=" ErrorCode: "+responseData.errorCode+" <BR> Error in loading data : " +responseData.message.replace("\n", "<BR>");
				 }else{
					 $scope.batchTransactionList=responseData.response;
					 
					 if($scope.batchTransactionList.length==0){
							$scope.showGrid=false;
							$scope.showNoGrid=true;
						}else{
							$scope.showGrid= true;
							$scope.showNoGrid= false;
							$scope.searchResultMessage = responseData.response.searchResultMessage;
						}
					 $scope.setPaging();
				 }
		 }, 
		 function(responseData) {
			 alert('System Failed: ' + responseData);
		 });	
    };
  
    
    /**==========================================
     * this method should be called for pagination
     */
	 $scope.setPaging = function(){	
		 $scope.grid.totalServerItems = $scope.batchTransactionList.length;
		 $scope.grid.pagingOptions.totalPages=Math.ceil($scope.grid.totalServerItems/$scope.grid.pagingOptions.pageSize);
		 $scope.setPagingData($scope.batchTransactionList,$scope.grid.pagingOptions.currentPage,$scope.grid.pagingOptions.pageSize); 
		 
	 };
	 /**==========================================
	  * page change handler
	  */
	 $scope.$watch('grid.pagingOptions', function (newVal, oldVal) {
	     if (newVal !== oldVal && newVal.currentPage !== oldVal.currentPage) {
	    	 if(newVal.currentPage<=0)
	    		 newVal.currentPage=oldVal.currentPage;
	    	 else if(newVal.currentPage>newVal.totalPages){
	    		 newVal.currentPage=oldVal.currentPage;
	    	 }
	    	 $scope.setPaging();
	     }
	     }, true);
		
	 /**==========================================
	  * triming the data based on the page number
	  */
	 $scope.setPagingData = function(data, page, pageSize){	
	     var myPagedData = data.slice((page - 1) * pageSize, page * pageSize);
	     $scope.pagedData = myPagedData;
	     $scope.grid.totalServerItems = data.length;
	     if (!$scope.$$phase) {
	         $scope.$apply();
	     }
	 };	 
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
		 var totalPage=Math.ceil($scope.grid.totalServerItems/$scope.grid.pagingOptions.pageSize);
		 if(totalPage>$scope.grid.pagingOptions.currentPage){
		 $scope.grid.pagingOptions.currentPage=$scope.grid.pagingOptions.currentPage+1;
		 }
	 };	
	 /**==========================================
	  * previous page
	  */
	 $scope.previousPage=function(){
		 if($scope.grid.pagingOptions.currentPage>1){
		 $scope.grid.pagingOptions.currentPage=$scope.grid.pagingOptions.currentPage-1;
		 }
	 };	
	 /**==========================================
	  * last page
	  */
	 $scope.lastPage=function(){
		 $scope.grid.pagingOptions.currentPage=Math.ceil($scope.grid.totalServerItems/$scope.grid.pagingOptions.pageSize);
	 };	
	 /**==========================================
	  * change number of row in the page
	  */
	 $scope.pageSizeChanged=function(){
		 $scope.grid.pagingOptions.currentPage=1;
		 $scope.setPaging();
	 };	

	$scope.intialSetup();
	$scope.getAllBatchData();

};
