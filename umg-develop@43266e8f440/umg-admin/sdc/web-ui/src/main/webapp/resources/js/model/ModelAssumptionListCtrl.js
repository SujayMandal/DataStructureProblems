'use strict';

var ModelAssumptionListCtrl = ['$scope','$log', '$filter' ,'$http','$dialogs','$location', 'syndicateDataService', 'sharedPropertiesService', function($scope, $log, $filter, $http, $dialogs,$location, syndicateDataService, sharedPropertiesService){
	
	
	// Variable to hold all the container information based on the search criteria
	$scope.pagedContainerData = [];
	// Variable to hold the selected container in the grid
	$scope.selectedContainerVersion="";
	// Variable to indicate whether is grid is loaded or not
	$scope.ready = false;
	// Variable to hold all container names for auto complete feature
	$scope.containerNames = [];
	// Flag to inform the load the containers only for the first time of page load 
	$scope.firstTime = true;
	// Variable to display the message in case of any issues
	$scope.msg = [];
	
	/** Default properties of Syndicate Data Listing Page */
	
	$scope.pageInfo = {
			searchText : '',
			fromDate : '',
			toDate : '',
			pageSize: 50000, // Not Used
			page: 1,
			sortColumn : 'lastModifiedDate',
			descending : true
	};
	
	$scope.showMessage = function(content, cl) {
		$scope.msg = content;
		$scope.clazz = cl;
		$timeout(function() {
			$scope.$apply('msg = []');
			$scope.$apply('clazz = []');
		}, 10000);
	};
	

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

    // Ag-grid Column Definitions
    var columnDefs = [
                  	 {field:'containerName', headerName:'Look-up Table Name', width: 175, suppressSizeToFit: true,unSortIcon: true},
					 {field:'versionName', headerName:'Version',filter: 'text',hide: false, suppressSizeToFit: true,unSortIcon: true}, 
					/* {field:'versionDescription', headerName:'Version Description',filter: 'text',hide: false, suppressSizeToFit: true,unSortIcon: true}, */
					 {field:'validFromString', headerName:'Active From',filter: 'text', width: 140, hide: false,unSortIcon: true,comparator: dateComparator},
					 {field:'validToString', headerName:'Active Till',filter: 'text', width: 140,  hide: false,unSortIcon: true,comparator: dateComparator},
					 {field:'lastModifiedBy', headerName:'Last Updated By',filter: 'text', hide: false,width: 116,unSortIcon: true},
					 {field:'lastModifiedDateTime', headerName:'Last Update On',filter: 'text', width: 140, hide: false,unSortIcon: true,comparator: dateComparator}
              ];

	
	// Updating the node details based on the row selection event
	var rowSelected = function(event) {
		$scope.selectedContainerVersion = event;
    };
	
	var readyEvent = function(event) {
	   	 $scope.gridOptions.api.onNewRows();
	   	 $scope.gridOptions.api.sizeColumnsToFit();
	   	 if($scope.selectedContainerVersion!=null && $scope.selectedContainerVersion!=""){
	   		 $scope.gridOptions.api.selectIndex(0);
	   	 }
	   	$scope.ready = true;
	};
	
	// Ag-grid Grid Definitions
	$scope.gridOptions ={
			rowHeight: 24,
			headerRowHeight:24,
	        rowData: 'pagedContainerData',
	        rowSelection: 'single',
	        enableFilter: true,
	        enableColResize: true,
	       	columnDefs : columnDefs,
	       	rowSelected: rowSelected,
	        ready: readyEvent,
	       	enableSorting: true,
	       	sortingOrder: ['desc','asc']

	};
	

	/** Main method will be called during onLoad / Searching / Sorting / Pagination */
	
    
    $scope.getFilteredContainer = function(){
    	
    	$log.info('Fetching containers based on Page info and search criteria ...');
    	
    	if(angular.isDefined($scope.pageInfo.fromDate) && $scope.pageInfo.fromDate != ''){
    		$scope.pageInfo.fromDate = $filter('date')($scope.pageInfo.fromDate,"MMM-dd-yyyy").split(' ')[0] + ' 00:00:00';
    	}
    	if(angular.isDefined($scope.pageInfo.toDate) && $scope.pageInfo.toDate != ''){
    		$scope.pageInfo.toDate = $filter('date')($scope.pageInfo.toDate,"MMM-dd-yyyy").split(' ')[0] + ' 23:59:59';
    	}

    	syndicateDataService.fetchPagedDataAsync($scope.pageInfo).then(
    	
    			function(responseData){
    				
    				if(!responseData.error){
    					var branches = [];
    					angular.forEach(responseData.response.content,function(data){
    	        			 var branch = {
    	            				 containerName : data.containerName,
    	            				 versionName : data.versionName,
    	            				 versionId : data.versionId,
    	            				 versionDescription : data.versionDescription,
    	            				 validFromString : data.validFromString,
    	            				 validToString : data.validToString,
    	            				 lastModifiedBy : data.lastModifiedBy,
    	            				 lastModifiedDateTime : data.lastModifiedDateTime
    	            				 };
    	        			 var containerName = data.containerName;
    	        			 branches.push(branch);
    	        			 if($scope.firstTime){
    	        				 $scope.containerNames.push(containerName);
    	        				
    	        			 }
    	        			
    	        			 });
    					$scope.firstTime = false;
    					$scope.pagedContainerData = branches;
    					$scope.gridOptions.rowData = $scope.pagedContainerData;
    					$scope.gridOptions.api.onNewRows();
		                $scope.selectedContainerVersion= ($scope.pagedContainerData[0]);
		                $scope.ready = true;
	                	
	                	
	                	$scope.gridOptions.ready = function() {
	                		 $scope.gridOptions.api.onNewRows();
	                	   	 $scope.gridOptions.api.sizeColumnsToFit();
	                	   	 if($scope.selectedContainerVersion!=null && $scope.selectedContainerVersion!=""){
	                	   		 $scope.gridOptions.api.selectIndex(0);
	                	   	 }
	                	   	$scope.ready = true;
	                	};
	                	
	                	
	                	if($scope.selectedContainerVersion!=null && $scope.selectedContainerVersion!=""){
	                		 $scope.gridOptions.api.selectIndex(0);
	                	}
	                	
	                	$scope.gridOptions.api.sizeColumnsToFit();
    					
    				}else{
    					$scope.pagedContainerData = [];
    				}
    				$log.info('Container data....'+$scope.pagedContainerData);
    			},
    			function(errorData){
    				$scope.error = true;
					$scope.msg = " Unable retrieve data, Please contact System Administrator.";
    				$scope.showMessage(errorData,'alert alert-error');
    			}
    	);
    	
    };
    
   
   
    
	$scope.getFilteredContainer();
	 
	
    $scope.clearFilters = function(){
    	$log.info('Resetting Page ...');
    	$scope.pageInfo = {
    			searchText : '',
    			fromDate : '',
    			toDate : '',
    			pageSize: 1000,
    			page: 1,
    			sortColumn : 'lastModifiedDate',
    			descending : true
    	};
    	$scope.selectedItems="";
    	$scope.error = false;
    	$scope.msg = [];
    	$scope.getFilteredContainer();
    	
    };
    
    $scope.searchContainers = function(){
    	$log.info('Searching Container ...');
    	$scope.expandAll = false;
    	$scope.msg = [];
    	$scope.pageInfo.page = 1;
    	$scope.getFilteredContainer();
    };
    
    /**
     * This method use to edit version information.
     */
    $scope.editVersionInfo = function(container){
    	$log.info("Request received to edit version information for Version Name: "+ container.versionName + " under container with Name: "+container.containerName);
    	var editVersion = {"operation": "editVersion", "containerName":container.containerName, "versionId":container.versionId};
    	sharedPropertiesService.setObject(editVersion);
    	$location.path('syndicateDataCrud');
    };
    
    
    /**
     * This method use to delete version..
     */
    $scope.deleteVersion = function(containerVersion){
    	$log.warn("Request received to delete version for Version Name: "+ containerVersion.versionName + " under container with Name: "+containerVersion.containerName);
    	$dialogs.confirm('Please Confirm','<span class="confirm-body">Are you sure to delete this Version: <strong>'+containerVersion.versionName +'</strong> under Container: <strong>'+containerVersion.containerName+'</strong> ?</span>')
		.result.then(function(btn){
			$log.warn("You confirmed version deletion ...");
			$http({
				method: 'DELETE',
				url: 'syndicateData/'+containerVersion.containerName+'/'+containerVersion.versionId
				}).success(function(result){
				$log.info(result.message);
				$scope.delMsg = result.message;
				$log.info("Fetching All Containers");
				$scope.getFilteredContainer();
				
				if(result.error){
					$scope.showMessage(result.message,'alert alert-warning');
				}
				else{
					$scope.showMessage(result.message,'alert alert-success');
					$log.info(result.message);
				}
				
			}).error(function(errorData, status){
				$scope.error = true;
				//$scope.msg = " Unable to Delete Version, Please contact System Administrator.";
				$log.error("Error came with status code: "+status);
				$scope.showMessage('Unable to Delete Version, Please contact System Administrator. Error came with status code: '+status,'alert alert-error');
			});
        });
    };
    
    /**add new version*/
    
    $scope.addNewVersion = function(container){
    	$log.info("Request received to add new version for "+container);
    	var addVersion = {"operation": "addVersion", "containerName":container};
    	sharedPropertiesService.setObject(addVersion);
    	$location.path('syndicateDataCrud');
    };

}];