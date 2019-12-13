'use strict';

var ModeletProcessService = ['$http', '$q', function($http, $q) {

	this.getModeletClientDetails = function(){
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'modeletPooling/getAllModeletClients'
		}).success(function(data, status, headers, config) {
			deferred.resolve(data, status, headers, config);
		}).error(function(data, status, headers, config) {
			deferred.reject(data, status, headers, config);
		});
		return deferred.promise;
	};

	this.showProcessList = function(modelet){
        var deferred = $q.defer();
        $http({
            method : 'POST',
            url : 'modeletPooling/fetch-modelet-response',
            data : modelet
        }).success(function(data, status, headers, config) {
            deferred.resolve(data, status, headers, config);
        }).error(function(data, status, headers, config) {
            deferred.reject(data, status, headers, config);
        });
        return deferred.promise;
    };


}];