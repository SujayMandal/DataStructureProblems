'use strict';
var PluginService =['$http', '$q', function($http, $q) {
	
		this.getAllPlugins = function() {
		var deferred = $q.defer();
		$http({
			method : 'POST',
			url : 'plugin/getPlugins'
		}).success(function(data, status, headers, config) {
			deferred.resolve(data, status, headers, config);
		}).error(function(data, status, headers, config) {
			deferred.reject(data, status, headers, config);
		});
		return deferred.promise;
	};
	
	this.getRVersions = function(){
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
	
	
	this.doServerSideValidation = function(rModelMetadata){
		var deferred = $q.defer();
		$http({
			method : 'POST',
			headers: {'Content-Type': undefined},
			url : 'plugin/validateFileUpload',
			transformRequest: angular.identity,
			data :rModelMetadata
		}).success(function(data, status, headers, config) {
			deferred.resolve(data, status, headers, config);
		}).error(function(data, status, headers, config) {
			deferred.reject(data, status, headers, config);
		});
		return deferred.promise;
	};
}];