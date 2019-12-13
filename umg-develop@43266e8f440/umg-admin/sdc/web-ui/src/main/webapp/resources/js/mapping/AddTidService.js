'use strict';
var AddTidService = ['$http', '$q', function($http, $q) {	

	this.extractTidParams = function(derivedModelName,derivedTidName,tidName) {
		var deferred = $q.defer();
		var formData = new FormData();
		formData.append('derivedModelName', derivedModelName);
		formData.append('tidName', tidName);	
		formData.append('derivedTidName',derivedTidName);
		$http({
			method : 'POST',
			headers : {
				'Content-Type' : undefined
			},
			url : 'mapping/getMappingsForModel',
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

	this.saveMapping = function(modelDescriptor, validate) {
		var deferred = $q.defer();
		var formData = new FormData();
		formData.append('mappingDescriptorJson', modelDescriptor);
		formData.append('validate', validate);
		$http({
			method : 'POST',
			headers : {
				'Content-Type' : undefined
			},
			url : 'mapping/saveMapping',
			transformRequest : angular.identity,
			data : formData
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("error in ajax call:" + data);
		});
		return deferred.promise;
	};
	
	this.getTidMidMapping = function(tidName) {
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'mapping/getMappingDetails/' + tidName,
			transformRequest : angular.identity
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("Error:" + data);
			alert("error in ajax call:" + data);
		});
		return deferred.promise;
	};
	
	this.createInputMapForQuery = function(type, tidName) {	
		var deferred = $q.defer();
		var formData = new FormData();
		formData.append('type', type);
		formData.append('tidName', tidName);
		$http({
			method : 'POST',
			headers : {
				'Content-Type' : undefined
			},
			url : 'mapping/createInputMapForQuery',
			transformRequest : angular.identity,
			data : formData
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("error in ajax call:" + data);
		});
		return deferred.promise;
	};
	
	this.getTidListForCopy = function() {
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'mapping/getTidListForCopy',
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("Error came with status code :" + status);
		});
		return deferred.promise;
	};
	
	this.getMappingStatus = function(tidName) {	
		var deferred = $q.defer();
		var formData = new FormData();
		formData.append('tidName', tidName);
		$http({
			method : 'POST',
			headers : {
				'Content-Type' : undefined
			},
			url : 'mapping/getMappingStatus',
			transformRequest : angular.identity,
			data : formData
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("error in ajax call:" + data);
		});
		return deferred.promise;
	};
}];