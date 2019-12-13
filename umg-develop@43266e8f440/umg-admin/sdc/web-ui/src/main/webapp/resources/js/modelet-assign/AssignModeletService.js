'use strict';

var AssignModeletService = ['$http', '$q', function($http, $q) {
	
	this.getAllModelets = function(){
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'modelet/profiler/system/modelets',
		}).success(function(data, status, headers, config) {
			deferred.resolve(data, status, headers, config);
		}).error(function(data, status, headers, config) {
			deferred.reject(data, status, headers, config);
		});
		return deferred.promise;
	};

    this.getModeletProfilers = function(){
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

    this.downloadModeletLogs = function(modelet){
        var deferred = $q.defer();
        var formData = new FormData();
        $http({
            method : 'POST',
            url : 'modelet/profiler/downloadModeletLog',
            headers : {
               'Content-Type' : undefined
           },
           data: modelet
        }).success(function(data, status, headers, config) {
            saveAs(new Blob([data], { type: 'text/plain;charset=utf-8' }), modelet.port+".txt");
            deferred.resolve(data, status, headers, config);
        }).error(function(data, status, headers, config) {
            deferred.reject(data, status, headers, config);
        });
        return deferred.promise;
    };
    
    this.restartModelets = function(modelets){
	    var deferred = $q.defer();
		$http({
			method : 'POST',
			url : 'modeletPooling/restartModelets',
			data : modelets
		}).success(function(data, status, headers, config) {
			deferred.resolve(data, status, headers, config);
		}).error(function(data, status, headers, config) {
			deferred.reject(data, status, headers, config);
		});
		return deferred.promise;
	};
	
	this.updateModeletProfilerLink = function(hostName, port, profilerId) {
	    var deferred = $q.defer();
	    var formData = new FormData();
        formData.append('hostName', hostName);
        formData.append('port', port);
        formData.append('profilerId', profilerId);
        $http({
            method : 'POST',
            url : 'modelet/profiler/modify-map',
             headers : {
                'Content-Type' : undefined
            },
            data: formData
        }).success(function(data, status, headers, config) {
            deferred.resolve(data, status, headers, config);
        }).error(function(data, status, headers, config) {
            deferred.reject(data, status, headers, config);
        });
        return deferred.promise;
	}
	
	
}]
	
