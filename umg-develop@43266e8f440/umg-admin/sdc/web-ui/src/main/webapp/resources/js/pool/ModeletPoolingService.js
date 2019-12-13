'use strict';

var ModeletPoolingService = ['$http', '$q', function($http, $q) {
	
	this.getModeletPoolingDetails = function(){
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'modeletPooling/getModeletPoolingDetails'
		}).success(function(data, status, headers, config) {
			deferred.resolve(data, status, headers, config);
		}).error(function(data, status, headers, config) {
			deferred.reject(data, status, headers, config);
		});
		return deferred.promise;
	};
	
	this.getPoolDetails = function(){
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'modeletPooling/getAllPoolDetails'
		}).success(function(data, status, headers, config) {
			deferred.resolve(data, status, headers, config);
		}).error(function(data, status, headers, config) {
			deferred.reject(data, status, headers, config);
		});
		return deferred.promise;
	};
	
	this.createPool = function(poolConfig){
		var deferred = $q.defer();
		$http({
			method : 'POST',
			url : 'modeletPooling/createPool',
			data : poolConfig
		}).success(function(data, status, headers, config) {
			deferred.resolve(data, status, headers, config);
		}).error(function(data, status, headers, config) {
			deferred.reject(data, status, headers, config);
		});
		return deferred.promise;
	};
	
	this.switchModeletStatus = function(modelet){
		var deferred = $q.defer();
		$http({
			method : 'POST',
			url : 'modeletPooling/switchStatus',
			data : modelet
		}).success(function(data, status, headers, config) {
			deferred.resolve(data, status, headers, config);
		}).error(function(data, status, headers, config) {
			deferred.reject(data, status, headers, config);
		});
		return deferred.promise;
	};
	
	this.deletePool = function(poolId){
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'modeletPooling/deletePool/'+poolId
		}).success(function(data, status, headers, config) {
			deferred.resolve(data, status, headers, config);
		}).error(function(data, status, headers, config) {
			deferred.reject(data, status, headers, config);
		});
		return deferred.promise;
	};
	
	this.updatePoolConfig = function(poolConfig){
		var deferred = $q.defer();
		$http({
			method : 'POST',
			url : 'modeletPooling/updatePool',
			data : poolConfig
		}).success(function(data, status, headers, config) {
			deferred.resolve(data, status, headers, config);
		}).error(function(data, status, headers, config) {
			deferred.reject(data, status, headers, config);
		});
		return deferred.promise;
	};
	
	this.searchPool = function(searchString){
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'modeletPooling/searchPool/'+searchString
		}).success(function(data, status, headers, config) {
			deferred.resolve(data, status, headers, config);
		}).error(function(data, status, headers, config) {
			deferred.reject(data, status, headers, config);
		});
		return deferred.promise;
	};
	
	this.getModeletRestartDetails = function(){
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'modeletPooling/getModeletRestartDetails'
		}).success(function(data, status, headers, config) {
			deferred.resolve(data, status, headers, config);
		}).error(function(data, status, headers, config) {
			deferred.reject(data, status, headers, config);
		});
		return deferred.promise;
	};
	
	this.updateModeletRestartDetails = function(modeletRestartInfoList){
		var deferred = $q.defer();
		$http({
			method : 'POST',
			url : 'modeletPooling/addModeletRestartDetails',
			data : modeletRestartInfoList
		}).success(function(data, status, headers, config) {
			deferred.resolve(data, status, headers, config);
		}).error(function(data, status, headers, config) {
			deferred.reject(data, status, headers, config);
		});
		return deferred.promise;
	};
	
	this.deleteModeletRestartDetails = function(modeletRestartInfo){
		var deferred = $q.defer();
		$http({
			method : 'POST',
			url : 'modeletPooling/deleteModeletRestartDetails',
			data : modeletRestartInfo
		}).success(function(data, status, headers, config) {
			deferred.resolve(data, status, headers, config);
		}).error(function(data, status, headers, config) {
			deferred.reject(data, status, headers, config);
		});
		return deferred.promise;
	};

}];