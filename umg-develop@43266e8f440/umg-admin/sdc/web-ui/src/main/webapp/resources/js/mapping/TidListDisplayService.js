'use strict';
var TidListDisplayService = function($http, $q) {

	this.deleteTidMapping = function(tidName) {
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'mapping/deleteTidMapping/' + tidName,
			transformRequest : angular.identity
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("Error:" + data);
			alert("error in ajax call:" + data);
		});
		return deferred.promise;
	};
	
	this.getTidMappingStatus = function(tidName) {
		var deferred = $q.defer();
		var formData = new FormData();
		formData.append('tidName', tidName);
		$http({
			method : 'POST',
			headers : {
				'Content-Type' : undefined
			},
			url : 'version/getTidMappingStatus',
			transformRequest : angular.identity,
			data : formData
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("Error:" + data);
			alert("error in ajax call:" + data);
		});
		return deferred.promise;
	};
	
	this.getVersionStatus = function(tidName) {
		var deferred = $q.defer();
		var formData = new FormData();
		formData.append('tidName', tidName);
		$http({
			method : 'POST',
			headers : {
				'Content-Type' : undefined
			},
			url : 'version/getVersionStatus',
			transformRequest : angular.identity,
			data : formData
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("Error:" + data);
			alert("error in ajax call:" + data);
		});
		return deferred.promise;
	};
	
	this.getNotDeletedVersions = function(tidName) {
		var deferred = $q.defer();
		var formData = new FormData();
		formData.append('tidName', tidName);
		$http({
			method : 'POST',
			headers : {
				'Content-Type' : undefined
			},
			url : 'version/getNotDeletedVersions',
			transformRequest : angular.identity,
			data : formData
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("Error:" + data);
			alert("error in ajax call:" + data);
		});
		return deferred.promise;
	};
	
	
	
	
	/**
	 * This method will fetch all mapping  with search criteria and pagination
	 **/
	this.findAllMappings = function(pagingOptions, searchData){
		var deferred = $q.defer();
		var searchOption={};
		searchOption.page=pagingOptions.currentPage;
		searchOption.pageSize=pagingOptions.pageSize;
		searchOption.descending=pagingOptions.descending;
		searchOption.sortColumn=pagingOptions.sortColumn;
		
		
		if(searchData.fromDate!=undefined && searchData.fromDate!=null && searchData.fromDate!=""){
			searchOption.fromDate=searchData.fromDate+" 00:00:00";
		}else{
			searchOption.fromDate=searchData.fromDate;
		}
		
		if(searchData.toDate!=undefined && searchData.toDate!=null && searchData.toDate!=""){
			searchOption.toDate=searchData.toDate+" 23:59:59";
		}else{
			searchOption.toDate=searchData.toDate;
		}
		
		searchOption.searchText=searchData.searchText;	
		//alert(JSON.stringify(searchOption));
		$http({
			method : 'POST',
			url : "mapping/listAllMapping",
			data : searchOption	
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("error in ajax call:" + data);
		});
		return deferred.promise;
	};
	
};