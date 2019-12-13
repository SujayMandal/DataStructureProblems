'use strict';

var NotificationAddEditService = function($http, $q) {
	
	this.getAddEventList = function(){
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'mappingNotification/getNotificationEventNames/'
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("error in ajax call:" + data);
		});
		return deferred.promise;
	}
	
	this.getModelList = function(){
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'mappingNotification/getAllModelNames/'
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("error in ajax call:" + data);
		});
		return deferred.promise;
	}
	
	this.getMailDetails = function(mapId,flag){
		var deferred = $q.defer();
		var formData = new FormData();
		formData.append('id', mapId);
		if(flag){
			$http({
				method : 'POST',
				headers : {
					'Content-Type' : undefined
				},
				url : 'mappingNotification/getTemplateDataByEventId/',
				transformRequest : angular.identity,
				data : formData
			}).success(function(data, status, headers, config) {
				deferred.resolve(data);
			}).error(function(data, status, headers, config) {
				deferred.reject("error in ajax call:" + data);
			});
		}
		else{
			$http({
				method : 'POST',
				headers : {
					'Content-Type' : undefined
				},
				url : 'mappingNotification/getMappingDataByMappingId/',
				transformRequest : angular.identity,
				data : formData
			}).success(function(data, status, headers, config) {
				deferred.resolve(data);
			}).error(function(data, status, headers, config) {
				deferred.reject("error in ajax call:" + data);
			});
		}
		return deferred.promise;
	}
	
	this.saveEditEvent = function(mailDetails,eventInfo){
		var dataObj = {};
		dataObj = mailDetails;
		dataObj.id = eventInfo.id;
		var deferred = $q.defer();
		$http({
			method : 'POST',
			url : 'mappingNotification/updateMapping/',
			data : dataObj
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("error in ajax call:" + data);
		});
		return deferred.promise;
	}
	
	this.saveAddEvent = function(mailDetails, event){
		var dataObj = {};
		dataObj = mailDetails;
		dataObj.notificationEventId = event.id;
		dataObj.name = event.eventName;
		var deferred = $q.defer();
		$http({
			method : 'POST',
			url : 'mappingNotification/createMapping/',
			data : dataObj
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("error in ajax call:" + data);
		});
		return deferred.promise;
	}
}