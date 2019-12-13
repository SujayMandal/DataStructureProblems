'use strict';

var AuthTokenCtrl = ['$scope','$log', '$filter' ,'$http','$dialogs','$location', 'tenantService', 'sharedPropertiesService', function($scope, $log, $filter, $http, $dialogs,$location, tenantService, sharedPropertiesService){
	
	
	// Variable to hold all the auth codes information 
	$scope.authTokenDetailsData = [];
	// Variable to hold the selected auth code in the grid
	$scope.selectedAuthToken = "";
	// Variable to indicate whether is grid is loaded or not
	$scope.ready = false;
	// Flag to inform the load the containers only for the first time of page load 
	$scope.firstTime = true;
	// Variable to display the message in case of any issues
	$scope.msg = [];
	// reason to activate a auth token
	$scope.comment = "";
	
	$scope.flag =  true;
	
	$scope.changeFunc = function(){
		//$scope.flag = $scope.comment.length == 0 ? false : true;
		if($scope.comment)
			$scope.flag = false;
		else
			$scope.flag = true;
	}; 
	// flag to indicate the selected auth code is active
	$scope.activeState=true;
	//selected tenant
	$scope.currentTenant = sharedPropertiesService.get("selectedTenantCode");
	//activation request flag
	$scope.activationReq = false;
	
    // Ag-grid Column Definitions
    var columnDefs = [
                     {field:'id', headerName:'id',filter: 'text', hide: true,unSortIcon: true},
					 {field:'createdDateTime', headerName:'Created Date',filter: 'text', unSortIcon: true}, 
					 {field:'createdBy', headerName:'Created By',filter: 'text', hide: false,unSortIcon: true},
					 {field:'activeFromStr', headerName:'Active From',filter: 'text', hide: false,unSortIcon: true},
					 {field:'activeUntilStr', headerName:'Active Until',filter: 'text', hide: false,unSortIcon: true},
					 {headerName:'Auth_Code',filter: 'text', hide: false,unSortIcon: true,cellRenderer: concatTenantCode,
						 width:400},
					 {field:'status', headerName:'Status',filter: 'text', hide: false,unSortIcon: true},
					 {field:'comment', headerName:'Comment',filter: 'text', hide: false,unSortIcon: true}
              ];

    function concatTenantCode(params) {
        var data = params.data;
        var cellValue = document.createElement('div');
        cellValue.style = 'user-select:auto';
        cellValue.innerHTML = $scope.currentTenant + '.' + data.authCode ;
        return cellValue;
    }
	
    var rowSelected = function(event) {
    	$scope.selectedAuthToken = event;
		sharedPropertiesService.put("selectedAuthTokenId",$scope.selectedAuthToken.id);
		$scope.activeState = $scope.selectedAuthToken.status == "Active" ? true : false;
    }
	
	var readyEvent = function(event) {
	   	 $scope.gridOptions.api.onNewRows();
	   	 $scope.gridOptions.api.sizeColumnsToFit();
	   	 if($scope.selectedAuthToken != null && $scope.selectedAuthToken != ""){
	   		 $scope.gridOptions.api.selectIndex(0);
	   	 }
	   	 $scope.ready = true;
	};
	
	// Ag-grid Grid Definitions
	$scope.gridOptions ={
			rowHeight: 24,
			headerRowHeight:24,
	        rowData: 'authTokenDetailsData',
	        rowSelection: 'single',
	        rowSelected: rowSelected,
	        enableFilter: true,
	        enableColResize: true,
	       	columnDefs : columnDefs,
	        ready: readyEvent,
	       	enableSorting: true,
	       	sortingOrder: ['desc','asc']
	};

	
/** Main method will be called during onLoad  */    
    $scope.getAuthTokenDetails = function(){
    	$log.info('Fetching Auth Token Details for tenant : {}',sharedPropertiesService.get("selectedTenantCode"));
			 tenantService.getAuthTokensList(sharedPropertiesService.get("selectedTenantId")).then(
					 function(responseData) {
						 $scope.loadTokenGrid(responseData);
					 }, 
					 function(responseData) {
						 $scope.error = true;
						 $scope.msg = responseData.errorCode + " " + responseData.message;
					 }
					 );		
	 };    	
	 
	 $scope.loadTokenGrid = function(responseData){
		 if(responseData.error){
			 $scope.authTokenDetailsData = [];
		 }else{
			 var branches = [];
				angular.forEach(responseData.response,function(data){
        			 var branch = {
        					 id : data.id,
        					 createdDate : data.createdDateTime,
        					 createdBy : data.createdBy,
        					 activeFrom : data.activeFromStr,
        					 activeUntil : data.activeUntilStr,
        					 authCode : data.authCode,
        					 status : data.status,
        					 comment : data.comment
            				 };
        			 branches.push(branch);
        			 if($scope.firstTime){
        				 // TO DO 
        			 }
        			
        			 });
				$scope.firstTime = false;
				$scope.authTokenDetailsData = responseData.response;
				$scope.gridOptions.rowData = $scope.authTokenDetailsData;
				$scope.gridOptions.api.onNewRows();
                $scope.selectedAuthToken= ($scope.authTokenDetailsData[0]);
                $scope.ready = true;
            	
            	
            	$scope.gridOptions.ready = function() {
            		 $scope.gridOptions.api.onNewRows();
            	   	 $scope.gridOptions.api.sizeColumnsToFit();
            	   	 if($scope.selectedAuthToken!=null && $scope.selectedAuthToken!=""){
            	   		 $scope.gridOptions.api.selectIndex(0);
            	   	 }
            	   	$scope.ready = true;
            	};
            	
            	
            	if($scope.selectedAuthToken!=null && $scope.selectedAuthToken!=""){
            		 $scope.gridOptions.api.selectIndex(0);
            	}
            	
            	$scope.gridOptions.api.sizeColumnsToFit();
		 }
		 $log.info('Container data....'+$scope.authTokenDetailsData);
	 };
    
    $scope.getAuthTokenDetails();
    
    /**
     * This method use to activate selected auth token.
     */
    $scope.activateAuthToken = function(){
    	$log.info("Request received to activate "+ $scope.selectedAuthToken.authToken +" auth token for tenant : "+sharedPropertiesService.get("selectedTenantCode"));
		sharedPropertiesService.put("selectedAuthTokenId",$scope.selectedAuthToken.id);
		tenantService.activateAuthToken(sharedPropertiesService.get("selectedTenantId"),sharedPropertiesService.get("selectedAuthTokenId"),$scope.comment).then(
				 function(responseData) {
					 $scope.loadTokenGrid(responseData);
				 }, 
				 function(responseData) {
					 $scope.error = true;
					 $scope.msg = responseData.errorCode + " " + responseData.message;
				 }
				 );	
		$scope.comment = "";
    };
    
    /**add new auth token*/
    $scope.createNewAuthToken = function(){
    	$log.info("Request received to add new auth token for tenant : "+sharedPropertiesService.get("selectedTenantCode"));
    	$dialogs.confirm('Please confirm ','<span class="confirm-body"><strong>Any pending auth codes will be deactivated.<strong></span>')
		.result.then(function(btn){
    	tenantService.createNewAuthToken(sharedPropertiesService.get("selectedTenantId")).then(
				 function(responseData) {
					 $scope.loadTokenGrid(responseData);
				 }, 
				 function(responseData) {
					 $scope.error = true;
					 $scope.msg = responseData.errorCode + " " + responseData.message;
				 }
				 );		
		});
    };
    
    $scope.modalVar = '';
	$scope.clearModal = function() {
		$scope.modalVar = "modal";
		$scope.activationReq = false;
		$scope.comment = "";
		$("#activateConfirmation").modal("hide");
	};	
    
	$scope.activationReqEnblr = function() {
		$scope.activationReq = true;
	}
}];