'use strict';
var RAService = ['$http', '$q', function($http, $q) {
	
	this.getAllUniqueModels = function(){
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'businessDashboard/listAllUniqueModels',
		}).success(function(data, status, headers, config) {
			deferred.resolve(data, status, headers, config);
		}).error(function(data, status, headers, config) {
			deferred.reject(data, status, headers, config);
		});
		return deferred.promise;
	};

	this.getModelStatistics = function(){
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'businessDashboard/modelStatistics',
		}).success(function(data, status, headers, config) {
			deferred.resolve(data, status, headers, config);
		}).error(function(data, status, headers, config) {
			deferred.reject(data, status, headers, config);
		});
		return deferred.promise;
	};
	
	this.getTransactionCount = function(days){
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'businessDashboard/transactionCount/'+days,
		}).success(function(data, status, headers, config) {
			deferred.resolve(data, status, headers, config);
		}).error(function(data, status, headers, config) {
			deferred.reject(data, status, headers, config);
		});
		return deferred.promise;
	};
	
	this.getLookupData = function(){
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'businessDashboard/lookupData'
		}).success(function(data, status, headers, config) {
			deferred.resolve(data, status, headers, config);
		}).error(function(data, status, headers, config) {
			deferred.reject(data, status, headers, config);
		});
		return deferred.promise;
	};
	
	this.getSuccessFailureCount = function(filters){
		var deferred = $q.defer();
		$http({
			method : 'POST',
			url : 'businessDashboard/successFailureCount',
			data : filters
		}).success(function(data, status, headers, config) {
			deferred.resolve(data, status, headers, config);
		}).error(function(data, status, headers, config) {
			deferred.reject(data, status, headers, config);
		});
		return deferred.promise;
	};
	
	this.getUsageReport = function(){
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'businessDashboard/usageReport',
		}).success(function(data, status, headers, config) {
			deferred.resolve(data, status, headers, config);
		}).error(function(data, status, headers, config) {
			deferred.reject(data, status, headers, config);
		});
		return deferred.promise;
	};
	
	this.getStatusMetrics = function(filters){
		var deferred = $q.defer();
		$http({
			method : 'POST',
			url : 'businessDashboard/statusMetrics',
			data : filters
		}).success(function(data, status, headers, config) {
			deferred.resolve(data, status, headers, config);
		}).error(function(data, status, headers, config) {
			deferred.reject(data, status, headers, config);
		});
		return deferred.promise;
	};
	
	this.getUsageDynamics = function(filters){
		var deferred = $q.defer();
		$http({
			method : 'POST',
			url : 'businessDashboard/usageDynamics',
			data : filters
		}).success(function(data, status, headers, config) {
			deferred.resolve(data, status, headers, config);
		}).error(function(data, status, headers, config) {
			deferred.reject(data, status, headers, config);
		});
		return deferred.promise;
	};
	
	this.getTopHundredFailTxn = function(filters){
		var deferred = $q.defer();
		$http({
			method : 'POST',
			url : 'businessDashboard/errorTxnList',
			data : filters
		}).success(function(data, status, headers, config) {
			deferred.resolve(data, status, headers, config);
		}).error(function(data, status, headers, config) {
			deferred.reject(data, status, headers, config);
		});
		return deferred.promise;
	};
	
	this.getUsageDynamicsGrid = function(filters){
		var deferred = $q.defer();
		$http({
			method : 'POST',
			url : 'businessDashboard/getUsageDynamicsGrid',
			data : filters
		}).success(function(data, status, headers, config) {
			deferred.resolve(data, status, headers, config);
		}).error(function(data, status, headers, config) {
			deferred.reject(data, status, headers, config);
		});
		return deferred.promise;
	};
	
	this.getRaUsageTrendData = function(filters){
		var deferred = $q.defer();
		$http({
			method : 'POST',
			url : 'businessDashboard/usageTrendLine',
			data : filters
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
			url : 'plugin/allExecutionEnvironments'
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject(data, status, headers, config);
		});
		return deferred.promise;
	};
	
	this.switchTenant = function(toTenant){
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'switchTenant/switch/'+toTenant
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject(data, status, headers, config);
		});
		return deferred.promise;
	};
}];