'use strict';
var TestBedService = function($http, $q) {
	
	/**
	 * This method will fetch all library names from server
	 **/
	this.getTestBedDataByTransactionId = function(tranId){
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'versiontest/loadTestBed/'+tranId
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("error in ajax call:" + data);
		});
		return deferred.promise;
	};
	
	
	/**==============================================
	 * uploading test run file and receives the data as JSON object
	 */
	this.downloadTestRunFile = function(testRunFile) {
		var deferred = $q.defer();
		var formData = new FormData();
		formData.append('tenantInputfile', testRunFile);	
		$http({
			method : 'POST',
			headers: {'Content-Type': undefined},
			url : 'versiontest/parseFileData',
			transformRequest: angular.identity,
			data :formData
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("error in ajax call:" + data);
		});
		return deferred.promise;
	};
	
};