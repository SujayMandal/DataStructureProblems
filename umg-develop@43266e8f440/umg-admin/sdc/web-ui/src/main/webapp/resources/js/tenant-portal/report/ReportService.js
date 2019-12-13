'use strict';
var ReportService = function($http, $q, freezeServices) {
	
	/**==============================================
	 *  retrieve all model for a tenant
	 */
	this.getAllModels = function() {
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'tenantUsage/getAllModelNames'
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("error in ajax call:" + data);
		});
		return deferred.promise;
	};
	
	/**==============================================
	 *  retrieve unique id from server
	 */
	this.getSearchUID = function() {
		var deferred = $q.defer();
		freezeServices.freezeScreen('searchFreezeScreen');
		$http({
			method : 'GET',
			url : 'tenantUsage/createSearchRequest'
		}).success(function(data, status, headers, config) {
			freezeServices.unfreezeScreen('searchFreezeScreen');
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			freezeServices.unfreezeScreen('searchFreezeScreen');
			deferred.reject("error in ajax call:" + data);
		});
		return deferred.promise;
	};
	
	/**==============================================
	 *  retrieve all model for a tenant
	 */
	this.cancelSearch = function(uId ) {
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'tenantUsage/canceSearchlRequest?cancelRequestId='+uId
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("error in ajax call:" + data);
		});
		return deferred.promise;
	};
	/**==============================================
	 * retrieve all version for a model
	 */
	this.getVerionsforTheModel = function(tenantModelName) {
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'tenantUsage/getAllModelVersion?tenantModelName='+tenantModelName,
			//data:tenantModelName
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("error in ajax call:" + data);
		});
		return deferred.promise;
	};
	
	
	
	/**==============================================
	 * method to seach the data
	 */
	this.search = function(searchData, pagingOptions) {
	
	var searchOption={};	
	searchOption.page=pagingOptions.currentPage;
	searchOption.pageSize=pagingOptions.pageSize;
	searchOption.pageSet=pagingOptions.pageSet;
	searchOption.tenantModelName=searchData.model;
	searchOption.fullVersion=searchData.version;
	searchOption.runAsOfDateFromString=searchData.startDate;
	searchOption.runAsOfDateToString=searchData.endDate;
	searchOption.transactionStatus=searchData.status;
	searchOption.sortColumn=searchData.sortColumn;
	searchOption.descending=searchData.descending;
	searchOption.customDate=searchData.isCustomDate;
	searchOption.includeTest=searchData.includeTest;
	searchOption.cancelRequestId=searchData.searchUID;
	
	
	if(searchOption.runAsOfDateFromString!=undefined && searchOption.runAsOfDateFromString!=""){
		 searchOption.runAsOfDateFromString=searchOption.runAsOfDateFromString+" 00:00";
	 }else{
		 searchOption.runAsOfDateFromString=null; 
	 }
	 if(searchOption.runAsOfDateToString!=undefined && searchOption.runAsOfDateToString!=""){
		 searchOption.runAsOfDateToString=searchOption.runAsOfDateToString+" 23:59";
	 }else{
		 searchOption.runAsOfDateToString=null;
	 }
	//alert(JSON.stringify(searchOption));
		var deferred = $q.defer();
		freezeServices.freezeScreen('searchFreezeScreen');
		$http({
			method : 'POST',
			url : 'tenantUsage/filterTransactions',
			data:searchOption
		}).success(function(data, status, headers, config) {
			freezeServices.unfreezeScreen('searchFreezeScreen');
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			freezeServices.unfreezeScreen('searchFreezeScreen');
			deferred.reject("error in ajax call:" + data);
		});
		return deferred.promise;
	};
	
	/** This method is to do server call for fetching transaction reports */
	this.getReportsForTxn = function(txnID){
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'modelReport/view/'+txnID
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("error in ajax call:" + data);
		});
		return deferred.promise;
	};
	
	 
	this.getIndexedTxn = function(searchData, pagingOptions, actualIndex, searchType) {
		
		var searchOption={};
		var serviceURL = '';
		
		searchOption.page=actualIndex+1;
		searchOption.pageSize=1;
		searchOption.pageSet=pagingOptions.pageSet;
		searchOption.sortColumn=searchData.sortColumn;
		searchOption.descending=searchData.descending;
		
		if(searchType == 0){
		
		searchOption.tenantModelName=searchData.model;
		searchOption.fullVersion=searchData.version;
		searchOption.runAsOfDateFromString=searchData.startDate;
		searchOption.runAsOfDateToString=searchData.endDate;
		searchOption.transactionStatus=searchData.status;
		searchOption.customDate=searchData.isCustomDate;
		searchOption.includeTest=searchData.includeTest;
		searchOption.cancelRequestId=searchData.searchUID;
		if(searchOption.runAsOfDateFromString!=undefined && searchOption.runAsOfDateFromString!=""){
			 searchOption.runAsOfDateFromString=searchOption.runAsOfDateFromString+" 00:00";
		 }else{
			 searchOption.runAsOfDateFromString=null; 
		 }
		 if(searchOption.runAsOfDateToString!=undefined && searchOption.runAsOfDateToString!=""){
			 searchOption.runAsOfDateToString=searchOption.runAsOfDateToString+" 23:59";
		 }else{
			 searchOption.runAsOfDateToString=null;
		 }
		
		serviceURL = 'modelReport/indexedTxnByFilter';
		}
		else{
			searchOption.searchString=searchData.searchString;
			serviceURL = 'modelReport/indexedTxnBySearch';
		}
		
			var deferred = $q.defer();
			$http({
				method : 'POST',
				url : serviceURL,
				data:searchOption
			}).success(function(data, status, headers, config) {
				deferred.resolve(data);
			}).error(function(data, status, headers, config) {
				deferred.reject("error in ajax call:" + data);
			});
			return deferred.promise;
		};
		
		
	this.searchForTransactionId = function(searchData, pagingOptions){
		
		var searchOption={};	
		searchOption.page=pagingOptions.currentPage;
		searchOption.pageSize=pagingOptions.pageSize;
		searchOption.pageSet=pagingOptions.pageSet;
		searchOption.searchString=searchData.searchString;
		searchOption.sortColumn=searchData.sortColumn;
		searchOption.descending=searchData.descending;
		
		var deferred = $q.defer();
		freezeServices.freezeScreen('searchFreezeScreen');
		$http({
			method : 'POST',
			url : 'tenantUsage/searchTransactions',
			data:searchOption
		}).success(function(data, status, headers, config) {
			freezeServices.unfreezeScreen('searchFreezeScreen');
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			freezeServices.unfreezeScreen('searchFreezeScreen');
			deferred.reject("error in ajax call:" + data);
		});
		return deferred.promise;
		
	};
	 
};



