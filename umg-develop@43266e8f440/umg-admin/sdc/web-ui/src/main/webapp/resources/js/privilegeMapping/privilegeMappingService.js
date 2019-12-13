'use strict';

var PrivilegeMappingService = ['$http', '$q', function($http, $q) {
	this.getRolesforTenant  = function(tenantCode){
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'accessPrivilege/getRolesPrivilegesMap/'+tenantCode
		}).success(function(data, status, headers, config) {
			deferred.resolve(data, status, headers, config);
		}).error(function(data, status, headers, config) {
			deferred.reject(data, status, headers, config);
		});
		return deferred.promise;
	};

	this.setRolesforTenant = function(tenantCode,role,privilegeList){
		var deferred = $q.defer();
		var formData = new FormData();
		formData.append('tenantCode', tenantCode);	
		formData.append('role', role);
		formData.append('privilegeList', privilegeList);
		$http({
			method : 'POST',
			headers : {
				'Content-Type' : undefined
			},
			url : 'accessPrivilege/setRolesPrivilegesMap' ,
			transformRequest: angular.identity,
			data : formData	
		}).success(function(data, status, headers, config) {
			deferred.resolve(data, status, headers, config);
		}).error(function(data, status, headers, config) {
			deferred.reject(data, status, headers, config);
		});
		return deferred.promise;
	};
}];