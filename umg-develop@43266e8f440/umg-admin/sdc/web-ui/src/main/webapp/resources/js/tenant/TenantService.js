'use strict';
var TenantService = ['$http', '$q', function($http, $q) {
	
	this.getTenantsList = function() {
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'tenant/listAll'
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject(data, status, headers, config);
		});			
		return deferred.promise;	
	};

	this.getTenantDetails = function(tenant) {
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'tenant/tenantDetails/' + tenant,
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject(data, status, headers, config);
		});			
		return deferred.promise;	
	};
	
	this.getSystemKeys = function() {
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'tenant/systemKeys' ,
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject(data, status, headers, config);
		});			
		return deferred.promise;	
	};
	
	this.addTenant = function(tenant) {
		var deferred = $q.defer();
		$http({
			method : 'POST',
			url : 'tenant/create',
			data : tenant
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject(data, status, headers, config);
		});			
		return deferred.promise;	
	};

	this.updateTenant = function(tenant) {
		var deferred = $q.defer();
		$http({
			method : 'POST',
			url : 'tenant/update',
			data : tenant
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject(data, status, headers, config);
		});			
		return deferred.promise;	
	};

	this.batchDeploy = function(tenantCode) {
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'tenant/batchDeploy/'+ tenantCode
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject(data, status, headers, config);
		});			
		return deferred.promise;	
	};

	this.batchUndeploy = function(tenantCode) {
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'tenant/batchUndeploy/'+ tenantCode
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject(data, status, headers, config);
		});			
		return deferred.promise;	
	};
	
	this.getAuthTokensList = function(tenant) {
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'authToken/listAll/' + tenant
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject(data, status, headers, config);
		});			
		return deferred.promise;	
	};
	
	this.createNewAuthToken = function(tenant) {
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'authToken/createAuthToken/' + tenant
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject(data, status, headers, config);
		});			
		return deferred.promise;	
	};
	
	this.activateAuthToken = function(tenant,authCode,comment) {
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'authToken/activateAuthToken/' + tenant+ '/'+authCode+'/'+comment
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject(data, status, headers, config);
		});			
		return deferred.promise;	
	};
	
	/*this.resendAuthFunc = function(code){
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'tenant/resendAuth/' + code
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject(data, status, headers, config);
		});			
		return deferred.promise;
	}*/
}];