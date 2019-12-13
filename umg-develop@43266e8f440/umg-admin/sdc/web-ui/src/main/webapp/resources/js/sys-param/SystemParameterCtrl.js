'use strict';

var SystemParameterCtrl = ['$scope', '$log', '$timeout' , 'sysParamService', function($scope, $log, $timeout, sysParamService){
	
	$scope.searchString = "";
	$scope.searchMsg = "";
	$scope.message = [];
	$scope.clazz = [];
	
	$scope.sysParam = {
			id : '',
			sysKey : '',
			description : '',
			sysValue : '',
			isActive : 'Y'
	};
	
	$scope.showMessage = function(content, cl) {
		$scope.message = content;
		$scope.clazz = cl;
		$timeout(function() {
			$scope.$apply('message = []');
			$scope.$apply('clazz = []');
		}, 1800000);
	};
	
	$scope.pagingOptions = {
	        pageSizes: [10, 20, 30, 50],
	        pageSize: 50,
	        currentPage: 1
	    }; 
	
	$scope.pagedSysParams = [];
	$scope.filteredSysParams = [];
	
	$scope.showUpdationPopup = false;
	
	
	/**
     * This method will set up paged data from filtered Server items.
     */
	$scope.setFilteredPagingData = function(data, page, pageSize){
		$scope.allfilteredSysParams = data;
		$scope.pagedSysParams = data.slice((page - 1) * pageSize, page * pageSize);
        $scope.totalfilteredSysParams = data.length;
    };
    
	/**
     * This method will set up paged data from total Server items.
     */
	$scope.setPagingData = function(data, page, pageSize){
		$scope.allSysParams = data;
		$scope.pagedSysParams = data.slice((page - 1) * pageSize, page * pageSize);
        $scope.totalSysParams = data.length;
    };
    
    /**
     * This method will fetch all System Parameters from server based on URL mapping.
     * It will also filter data from Server data, for any search request.
     */
    $scope.getPagedDataAsync = function (pageSize, page, searchText) {

    	if (searchText) {
        	var filtereddata = [];
        	var ft = angular.lowercase(searchText);
        	angular.forEach($scope.allSysParams,function(data){
        		if(JSON.stringify(data.sysKey).toLowerCase().indexOf(ft) != -1 || JSON.stringify(data.sysValue).toLowerCase().indexOf(ft) != -1){
        			filtereddata.push(data);
        		}
        	});
        	if(filtereddata.length == 0){
        	       $scope.searchMsg = "No System Parameter Found for this Search.";
        	}
        	$scope.setFilteredPagingData(filtereddata,page,pageSize);
            
         } else {
        	 sysParamService.fetchAllSysParams().then(
        			 function(responseData){
        				 if(!responseData.error){
                			 $scope.setPagingData(responseData.response,page,pageSize);
                		 }
     				},
     				function(errorData){
     					$log.error("Error came with : "+errorData);
     					$scope.showMessage(errorData,'alert alert-error');
     				}
        	);
         }
    };
    
    $scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage);
    
    /**
     * This method will always keep eye on any change for pagination request.
     * And will set the paged data accordingly.
     */
    $scope.$watch('pagingOptions', function (newVal, oldVal) {
    	
    	!angular.isNumber(newVal.currentPage) ? (newVal.currentPage = 0): (newVal.currentPage = newVal.currentPage); 
        
    	if (newVal !== oldVal) {
        	$scope.currentMaxPages = $scope.maxPages();
        	if(newVal.currentPage * newVal.pageSize >= $scope.totalSysParams){
        		$scope.pagingOptions.currentPage = $scope.currentMaxPages;
        	}
        	if(newVal.currentPage * newVal.pageSize <= 0){
        		$scope.pagingOptions.currentPage = 1;
        	}
        	$scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage, $scope.searchString);
        }
    }, true);
    
    /**
     * This method will always keep eye on Search request.
     * And will set the resultant data in paged format.
     */
    $scope.$watch('searchString', function (newVal, oldVal) {
        if (newVal !== oldVal) {
        	if(newVal == "")
        	{$scope.totalfilteredSysParams = 0;}
        	$scope.pagingOptions.currentPage = 1;
        	$scope.searchMsg = "";
        	$scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage, $scope.searchString);
        }
    }, true);
    
    
    $scope.addNewSysParam = function(){
    	sysParamService.createNewSysParam($scope.sysParam).then(
   			 function(responseData){
   				 if(!responseData.error){
   					 $log.info('System Parameter Updated Successfully.');
   					 $scope.showMessage('System Parameter Updated Successfully.','alert alert-success');
   					 $scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage);
   				 }else{
   					$scope.showMessage('Parameter Updation Failed !','alert alert-error');
   				 }
				},
				function(errorData){
					$log.error("Error came with : "+errorData);
					$scope.showMessage(errorData,'alert alert-error');
				}
    	);
    };
    
    $scope.showParamUpdationPopup = function(sp){
    	$log.info("Request received to update System Parameter.");
    	$scope.sysParam = {id : '',sysKey : '', description: '', sysValue : '',isActive : 'Y'};
    	$scope.sysParam.id = sp.id;
    	$scope.sysParam.sysKey = sp.sysKey;
    	$scope.sysParam.description = sp.description,
    	$scope.sysParam.sysValue = sp.sysValue;
    	$scope.sysParam.isActive = sp.isActive;
    	$scope.showUpdationPopup = true;
    };
    
    
    
    
     /* Paging Operations */
    
    $scope.maxRows = function () {
        var ret = Math.max($scope.totalSysParams, $scope.pagedSysParams.length);
        if($scope.totalfilteredSysParams > 0){
        	ret = Math.max($scope.totalfilteredSysParams, $scope.pagedSysParams.length);
        }
        return ret;
    };
    
    $scope.$on('$destroy', $scope.$watch('totalSysParams',function(n,o){
        $scope.currentMaxPages = $scope.maxPages();
    }));
    
    $scope.$on('$destroy', $scope.$watch('totalfilteredSysParams',function(n,o){
        $scope.currentMaxPages = $scope.maxPages();
    }));
	
    $scope.maxPages = function () {
        if($scope.maxRows() === 0) {
            return 1;
        }
        return Math.ceil($scope.maxRows() / $scope.pagingOptions.pageSize);
    };
    
    $scope.pageToFirst = function(){
		$scope.pagingOptions.currentPage = 1;
	};
	
	$scope.pageBackward = function(){
		var page = $scope.pagingOptions.currentPage;
        $scope.pagingOptions.currentPage = Math.max(page - 1, 1);
	};
	
	$scope.pageForward = function(){
		var page = $scope.pagingOptions.currentPage;
        if ($scope.totalSysParams > 0) {
            $scope.pagingOptions.currentPage = Math.min(page + 1, $scope.maxPages());
        } else {
            $scope.pagingOptions.currentPage++;
        }
	};
	
	$scope.pageToLast = function(){
		var maxPages = $scope.maxPages();
        $scope.pagingOptions.currentPage = maxPages;
	};
	
	$scope.cantPageForward = function() {
        var curPage = $scope.pagingOptions.currentPage;
        var maxPages = $scope.maxPages();
        if ($scope.totalSysParams > 0) {
            return curPage >= maxPages;
        } else {
            return $scope.pagedSysParams.length < 1;
        }
    };
    
    $scope.cantPageToLast = function() {
        if ($scope.totalSysParams > 0) {
            return $scope.cantPageForward();
        } else {
            return true;
        }
    };
    
    $scope.cantPageBackward = function() {
        var curPage = $scope.pagingOptions.currentPage;
        return curPage <= 1;
    };
}];