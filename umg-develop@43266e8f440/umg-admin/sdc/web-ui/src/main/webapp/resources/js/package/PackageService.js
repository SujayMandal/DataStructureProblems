'use strict';

var PackageService = ['$http', '$q', function($http, $q) {
	
	this.addSupportPackage = function(fileDetail){
		var deferred = $q.defer();
		var fd = new FormData();
		fd.append('supportPackage',fileDetail.file);
		$http({
			method : 'POST',
			url : 'executionPackage/R/add',
			data : fd,
			headers : {
				'Content-Type' : undefined
			},
			transformRequest : angular.identity
		}).success(function(data, status, headers, config) {
			deferred.resolve(data, status, headers, config);
		}).error(function(data, status, headers, config) {
			deferred.reject(data, status, headers, config);
		});
		return deferred.promise;
	};
	
	
	this.getLargePackage = function(){
		console.log("entered syncLargePackage");
		var deferred = $q.defer();
		var fd = new FormData();
		$http({
			method : 'POST',
			url : 'executionPackage/R/sync/moveDelete',
			data : fd,
			headers : {
				'Content-Type' : undefined
			},
			transformRequest : angular.identity
		}).success(function(data, status, headers, config) {
			deferred.resolve(data, status, headers, config);
		}).error(function(data, status, headers, config) {
			deferred.reject(data, status, headers, config);
		});
		return deferred.promise;
	};
	
	this.getLargeFileCount = function(){
		var deferred = $q.defer();
		console.log("large files count")
		$http({
			method : 'GET',
			url : 'executionPackage/count'
		}).success(function(data, status, headers, config) {
			deferred.resolve(data, status, headers, config);
		}).error(function(data, status, headers, config) {
			deferred.reject(data, status, headers, config);
		});
		return deferred.promise;
	};
	
	this.getAllEnv = function(){
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'plugin/allRModelExecEnv'
		}).success(function(data, status, headers, config) {
			deferred.resolve(data, status, headers, config);
		}).error(function(data, status, headers, config) {
			deferred.reject(data, status, headers, config);
		});
		return deferred.promise;
	};

	this.getAllAddons = function(env){
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'executionPackage/listNames/'+env,
		}).success(function(data, status, headers, config) {
			deferred.resolve(data, status, headers, config);
		}).error(function(data, status, headers, config) {
			deferred.reject(data, status, headers, config);
		});
		return deferred.promise;
	};
	
	this.getPagedPackages = function(env, addonName, pageInfo){
		var deferred = $q.defer();
		$http({
			method : 'POST',
			url : 'executionPackage/listAllSupportPackages/'+env+'/'+addonName,
			data : pageInfo
		}).success(function(data, status, headers, config) {
			deferred.resolve(data, status, headers, config);
		}).error(function(data, status, headers, config) {
			deferred.reject(data, status, headers, config);
		});
		return deferred.promise;
	};
}];