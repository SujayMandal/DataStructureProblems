'use strict';

var SystemParameterService = ['$http', '$q', function($http, $q) {
	
	this.fetchAllSysParams = function(){
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'sysParam/listAll',
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("Error came with status code :" + status);
		});
		return deferred.promise;
	};
	
	
	this.createNewSysParam = function(sysParam){
		var deferred = $q.defer();
		$http({
			method : 'POST',
			url : 'sysParam/create',
			data : sysParam
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("Error came with status code :" + status);
		});
		return deferred.promise;
		
	};
	
}];