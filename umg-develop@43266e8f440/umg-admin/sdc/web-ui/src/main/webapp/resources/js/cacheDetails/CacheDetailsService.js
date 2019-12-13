'use strict';

var CacheDetailsService = ['$http', '$q', function($http, $q) {
	
	this.getAllSystemParameters = function(){
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'hazelCastStatus/listAll',
		}).success(function(data, status, headers, config) {
			deferred.resolve(data, status, headers, config);
		}).error(function(data, status, headers, config) {
			deferred.reject(data, status, headers, config);
		});
		return deferred.promise;
	};
	
	this.getIndexes = function(){
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'hazelCastStatus/showIndexes',
		}).success(function(data, status, headers, config) {
			deferred.resolve(data, status, headers, config);
		}).error(function(data, status, headers, config) {
			deferred.reject(data, status, headers, config);
		});
		return deferred.promise;
	};
	
}];