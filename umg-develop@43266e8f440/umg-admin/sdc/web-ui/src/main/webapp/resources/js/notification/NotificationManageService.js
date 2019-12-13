'use strict';

var NotificationManageService = function($http, $q) {
	
	this.getEventList = function () {
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'mappingNotification/getAllMappingData/'
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("error in ajax call:" + data);
		});
		return deferred.promise;
	};
	
	this.deleteEvent = function(eventData){
		var deferred = $q.defer();
		var formData = new FormData();
		formData.append('id', eventData);
		$http({
			method : 'POST',
			headers : {
				'Content-Type' : undefined
			},
			url : 'mappingNotification/deleteMapping/',
			transformRequest : angular.identity,
			data : formData
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("error in ajax call:" + data);
		});
		return deferred.promise;
	}
}