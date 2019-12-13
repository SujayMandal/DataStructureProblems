'use strict';

var TenantsListCtrl = ['$scope','$log', '$filter' ,'$http','$dialogs','$location', 'tenantService', 'sharedPropertiesService', function($scope, $log, $filter, $http, $dialogs,$location, tenantService, sharedPropertiesService){
	
	
	// Variable to hold all the container information 
	$scope.tenantsDetailsData = [];
	// Variable to hold the selected container in the grid
	$scope.selectedTenantDetails="";
	// Variable to indicate whether is grid is loaded or not
	$scope.ready = false;
	// Flag to inform the load the containers only for the first time of page load 
	$scope.firstTime = true;
	// Variable to display the message in case of any issues
	$scope.msg = [];
	
	$scope.showMessage = function(content, cl) {
		$scope.msg = content;
		$scope.clazz = cl;
		$timeout(function() {
			$scope.$apply('msg = []');
			$scope.$apply('clazz = []');
		}, 10000);
	};	
    
    // Ag-grid Column Definitions
    var columnDefs = [
                     {field:'id', headerName:'id',filter: 'text',hide: true, unSortIcon: true},
                  	 {field:'name', headerName:'Name',filter: 'text',unSortIcon: true},
					 {field:'description', headerName:'Description',filter: 'text', unSortIcon: true}, 
					 {field:'code', headerName:'Code',filter: 'text', hide: false,unSortIcon: true}					 
              ];

	
	// Updating the node details based on the row selection event
	var rowDoubleClicked = function(event) {
    	$location.path('tenant');
    };
    
    var rowSelected = function(event) {
    	$scope.selectedTenantDetails = event;
		sharedPropertiesService.put("selectedTenantCode",$scope.selectedTenantDetails.code);
		sharedPropertiesService.put("selectedTenantId",$scope.selectedTenantDetails.id);
    }
	
	var readyEvent = function(event) {
	   	 $scope.gridOptions.api.onNewRows();
	   	 $scope.gridOptions.api.sizeColumnsToFit();
	   	 if($scope.selectedTenantDetails!=null && $scope.selectedTenantDetails!=""){
	   		 $scope.gridOptions.api.selectIndex(0);
	   	 }
	   	 $scope.ready = true;
	};
	
	// Ag-grid Grid Definitions
	$scope.gridOptions ={
			rowHeight: 24,
			headerRowHeight:24,
	        rowData: 'tenantsDetailsData',
	        rowSelection: 'single',
	        rowSelected: rowSelected,
	        enableFilter: true,
	        enableColResize: true,
	       	columnDefs : columnDefs,
	       	cellDoubleClicked: rowDoubleClicked,
	        ready: readyEvent,
	       	enableSorting: true,
	       	sortingOrder: ['desc','asc']

	};

	
/** Main method will be called during onLoad  */    
    $scope.getTenantsDetails = function(){
    	
    	$log.info('Fetching  Tenants Details ...');
	
			 tenantService.getTenantsList().then(
					 function(responseData) {
						 if(responseData.error){
							 $scope.tenantsDetailsData = [];
						 }else{
							 var branches = [];
		    					angular.forEach(responseData.response,function(data){
		    	        			 var branch = {
		    	        					 id	: data.id,
		    	        					 name : data.name,
		    	        					 description : data.description,
		    	        					 code : data.code,
		    	        					 tenant_type : data.tenantType
		    	            				 };
		    	        			 branches.push(branch);
		    	        			 if($scope.firstTime){
		    	        				 // TO DO 
		    	        			 }
		    	        			
		    	        			 });
		    					$scope.firstTime = false;
		    					$scope.tenantsDetailsData = responseData.response;
		    					$scope.gridOptions.rowData = $scope.tenantsDetailsData;
		    					$scope.gridOptions.api.onNewRows();
				                $scope.selectedTenantDetails= ($scope.tenantsDetailsData[0]);
				                $scope.ready = true;
			                	
			                	
			                	$scope.gridOptions.ready = function() {
			                		 $scope.gridOptions.api.onNewRows();
			                	   	 $scope.gridOptions.api.sizeColumnsToFit();
			                	   	 if($scope.selectedTenantDetails!=null && $scope.selectedTenantDetails!=""){
			                	   		 $scope.gridOptions.api.selectIndex(0);
			                	   	 }
			                	   	$scope.ready = true;
			                	};
			                	
			                	
			                	if($scope.selectedTenantDetails!=null && $scope.selectedTenantDetails!=""){
			                		 $scope.gridOptions.api.selectIndex(0);
			                	}
			                	
			                	$scope.gridOptions.api.sizeColumnsToFit();
						 }
						 $log.info('Container data....'+$scope.tenantsDetailsData);
					 }, 
					 function(responseData) {
						 $scope.error = true;
						 $scope.msg = " Unable retrieve data, Please contact System Administrator.";
						 $scope.showMessage(errorData,'alert alert-error');
					 }
					 );		
	 };    	
    
    $scope.getTenantsDetails();
    
    /**
     * This method use to view/update Tenant
     */
    $scope.updateTenant = function(){
    	$log.info("Request received to update selected tenant");
		sharedPropertiesService.put("selectedTenantCode",$scope.selectedTenantDetails.code);
		sharedPropertiesService.put("selectedTenantId",$scope.selectedTenantDetails.id);
    	$location.path('tenant');
    };
    
    /**add new tenant*/
    
    $scope.addNewTenant = function(){
    	$log.info("Request received to add new tenant");
    	$location.path('addTenant');
    };
    
    $scope.getAuthTokensList = function(){
    	$log.info("Request received to list auth tokens for tenant : "+sharedPropertiesService.get("selectedTenantCode"));
		sharedPropertiesService.put("selectedTenantCode",$scope.selectedTenantDetails.code);
		sharedPropertiesService.put("selectedTenantId",$scope.selectedTenantDetails.id);
		$location.path('manageAuthTokens');
    };
    
    /** resend auth token */
    /*$scope.resendAuth = function(){
    	tenantService.resendAuthFunc($scope.selectedTenantDetails.code).then(
				 function(responseData) {
					 if(!responseData.error)
						 alert("success");
					 else
						 alert("failure response");
				 }, 
				 function(errorData) {
					alert(errorData);
				 }
				 );
    }*/

}];