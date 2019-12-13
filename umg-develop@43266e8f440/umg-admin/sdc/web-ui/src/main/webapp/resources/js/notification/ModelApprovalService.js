'use strict';

var ModelApprovalService = function($http, $q) {
	
	this.checkTenantMatch = function(version){
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'model/checkTenancy/' + version,
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("Error came with status code :" + status);
		});
		return deferred.promise;
	}
	
	this.getApprovalResponse = function(version){
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'model/approval/' + version,
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("Error came with status code :" + status);
		});
		return deferred.promise;
	}
}
