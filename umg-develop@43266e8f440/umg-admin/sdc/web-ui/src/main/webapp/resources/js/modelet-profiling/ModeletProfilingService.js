'use strict';

var ModeletProfilingService = ['$http', '$q', function($http, $q) {
	
	this.getProfileParameters = function(){
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'modelet/profiler/list/all',
		}).success(function(data, status, headers, config) {
			deferred.resolve(data, status, headers, config);
		}).error(function(data, status, headers, config) {
			deferred.reject(data, status, headers, config);
		});
		return deferred.promise;
	};
	
	this.getProfileDetails = function(id){
		var deferred = $q.defer();
	
		$http({
			method : 'GET',
			url : 'modelet/profiler/get/' + id
		}).success(function(data, status, headers, config) {
			deferred.resolve(data, status, headers, config);
		}).error(function(data, status, headers, config) {
			deferred.reject(data, status, headers, config);
		});
		return deferred.promise;
	};
	
	this.populateDefaultProfilerData = function() {
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'modelet/profiler/default-data'
		}).success(function(data, status, headers, config) {
			deferred.resolve(data, status, headers, config);
		}).error(function(data, status, headers, config) {
			deferred.reject(data, status, headers, config);
		});
		return deferred.promise;
	}
	
	this.getExecutionEnvList = function(){
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'modelet/profiler/execution/environments',
		}).success(function(data, status, headers, config) {
			deferred.resolve(data, status, headers, config);
		}).error(function(data, status, headers, config) {
			deferred.reject(data, status, headers, config);
		});
		return deferred.promise;
	};
	
	this.updateProfile = function(modelet){
		var deferred = $q.defer();
		var formData = new FormData();
		formData.append('modelet', modelet);
		$http({
			method : 'POST',
			url : 'modelet/profiler/update',
			data: modelet
		}).success(function(data, status, headers, config) {
			deferred.resolve(data, status, headers, config);
		}).error(function(data, status, headers, config) {
			deferred.reject(data, status, headers, config);
		});
		return deferred.promise;
	};
	
	this.createNewProfile =  function(newModelet){
		var deferred = $q.defer();
		var formData = new FormData();
		formData.append('newModelet', newModelet);
		$http({
			method : 'POST',
			url : 'modelet/profiler/create',
			data: newModelet
		}).success(function(data, status, headers, config) {
			deferred.resolve(data, status, headers, config);
		}).error(function(data, status, headers, config) {
			deferred.reject(data, status, headers, config);
		});
		return deferred.promise;
	};
	
	this.deleteModelet = function(profileID){
		var deferred = $q.defer();
		$http({
			method : 'POST',
			url : 'modelet/profiler/delete',
			data: {id: profileID}
		}).success(function(data, status, headers, config) {
			deferred.resolve(data, status, headers, config);
		}).error(function(data, status, headers, config) {
			deferred.reject(data, status, headers, config);
		});
		return deferred.promise;
	};
	
}];