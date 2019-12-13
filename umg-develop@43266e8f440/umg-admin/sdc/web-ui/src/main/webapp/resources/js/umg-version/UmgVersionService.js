'use strict';
var UmgVersionService = function($http, $q) {
	
	/**
	 * This method will send request to server to update version.
	 **/
	this.updateVersion = function(singleMap){
		var deferred = $q.defer();
		$http({
			method : 'POST',
			url : 'version/update',
			data : singleMap
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("Error came with status code :" + status);
		});
		return deferred.promise;
	};
	
	/**
	 * This method will fetch all library names from server
	 **/
	this.getLibraryNames = function(){
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'version/listAllLibraryNames'
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			alert("An error occured with error code:" + data.errorCode+"\nWith error message:"+data.message);
		});
		return deferred.promise;
	};
	
	/**
	 * This method will fetch records for given library.
	 **/
	this.getRecordsForLibrary = function(libraryName){
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'version/listAllLibraryRecNameDescs/'+libraryName
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("error in ajax call:" + data);
		});
		return deferred.promise;
	};
	
	/**
	 * This method will fetch all alias names for models
	 **/
	this.getAliasModelNames = function(){
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'version/listAllModelNames'
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("error in ajax call:" + data);
		});
		return deferred.promise;
	};
		
	/**
	 * This method will fetch all models for given library
	 * @param library name
	 * @param sort direction
	 * @param search string
	 **/
	this.getFilteredModelNames = function(libraryName, sortDirection, searchString){
		var deferred = $q.defer();
		var serverUrl = '';
		searchString.trim() == "" ? (serverUrl = 'version/listAllModelNames/'+libraryName+'/'+sortDirection) : (serverUrl = 'version/listAllModelNames/'+libraryName+'/'+sortDirection+'/'+searchString);
		$http({
			method : 'GET',
			url : serverUrl
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("error in ajax call:" + data);
		});
		return deferred.promise;
	};
	
	/**
	 * This method will fetch TID versions for particular model name
	 **/
	this.getTidVersions = function(modelName){
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'version/listAllTidVersions/'+modelName
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("error in ajax call:" + data);
		});
		return deferred.promise;
	};
	
	/**
	 * This method will fetch All Tenant Model Names
	 **/
	this.getAllTenantModelNames = function(){
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'version/listAllTenantModelNames'
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("error in ajax call:" + data);
		});
		return deferred.promise;
	};
	
	/**
	 * This method will fetch all versions for that umg
	 **/
	this.getMajorVersions = function(tenantModelName){
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'version/versionSummary/'+tenantModelName
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("error in ajax call:" + data);
		});
		return deferred.promise;
	};
	
	/**
	 * This method will send request to server to save version.
	 **/
	this.saveVersion = function(singleMap){
		var deferred = $q.defer();
		$http({
			method : 'POST',
			url : 'version/create',
			data : singleMap
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("Error came with status code :" + status);
		});
		return deferred.promise;
	};
	
	/**
	 * This method will send request to server to publish version.
	 **/
	this.publishVersion = function(versionId){
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'version/publishVersion/'+versionId
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("Error came with status code :" + status);
		});
		return deferred.promise;
		
	};
	
	/**
	 * This method will send publish approval request mail to publish a tested version
	 **/
	this.sendPublishApproval = function(versionId){
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'version/sendModelApprovalEmail/'+versionId
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("Error came with status code :" + status);
		});
		return deferred.promise;
		
	};
	
	/**
	 * This method will send request to server to deactivate version.
	 **/
	this.deactivateVersion = function(versionId){
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'version/deactivateVersion/'+versionId
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("Error came with status code :" + status);
		});
		return deferred.promise;
		
	};
	
	/**
	 * This method will send request to server to import version.
	 **/
	this.importVersion = function(versionDetails){
		var deferred = $q.defer();
		var formData = new FormData();
		formData.append('zipFile', versionDetails.zipPackage);
		var versionDetailsCopy = angular.copy(versionDetails);
		delete versionDetailsCopy.zipPackage;
		formData.append('versionInfo', JSON.stringify(versionDetailsCopy));
		$http({
			method : 'POST',
			url : 'version/import',
			data :formData,
			headers:{'Content-Type':undefined},
			transformRequest: angular.identity
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("Error came with status code :" + status);
		});
		return deferred.promise;
		
	};
	
	/**
	 * This method send request to server to fetch paged data of versions based on particular library and model name.
	 * */
	this.getPagedTenantModelVersions = function(libraryName, modelName, pageInfo){
		var deferred = $q.defer();
		$http({
			method : 'POST',
			url : 'version/listAllVersions/'+libraryName+'/'+modelName,
			data : pageInfo
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("Error came with status code :" + status);
		});
		return deferred.promise;
		
	};
	
	/**
	 * This method will send request to server to delete version.
	 **/
	this.deleteVersion = function(versionId){
		var deferred = $q.defer();
		$http({
			method : 'DELETE',
			url : 'version/deleteVersion/'+versionId
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("Error came with status code :" + status);
		});
		return deferred.promise;
	};
	
	
	
	/**
	 * This method will fetch all minor version with search criteria
	 * 
	 **/
	this.findAllVersionName = function(pagingOptions, searchData, sortDirection){
		var deferred = $q.defer();
		var searchOption={};
		searchOption.page=pagingOptions.currentPage;
		searchOption.pageSize=pagingOptions.pageSize;
		searchOption.descending=sortDirection;
		
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
		$http({
			method : 'POST',
			url : "version/listAllVersionName",
			data : searchOption	
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("error in ajax call:" + data);
		});
		return deferred.promise;
	};
	/**
	 * This method will retrieve all the major and minor version 
	 */
	this.findAllversionByVersionName = function(searchData){
		var deferred = $q.defer();
		var searchOption={};
		/*searchOption.page=pagingOptions.currentPage;
		searchOption.pageSize=pagingOptions.pageSize;*/
		searchOption.descending=false;
		
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
		$http({
			method : 'POST',
			url : 'version/findAllVersions/',
			data : searchOption	
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("error in ajax call:" + data);
		});
		return deferred.promise;
	};
	
	this.fetchVersionMetric = function(versionMetricRequestInfo) {
		var deferred = $q.defer();
		$http({
			method : 'POST',
			url : 'version/versionMetrics',
			data : versionMetricRequestInfo
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("error in ajax call:" + data);
		});
		return deferred.promise;
	};
	
	this.uploadTemplate = function(id, file, templateId){
		var deferred = $q.defer();
		var formData = new FormData();
		formData.append('versionId', id);
		formData.append('reportTemplate', file);
		formData.append('templateId',templateId);
		$http({
			method : 'POST',
			headers : {
				'Content-Type' : undefined
			},
			url : 'report/uploadReportTemplate',
			data : formData
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("Error came with status code :" + status);
		});
		return deferred.promise;
	};	

};