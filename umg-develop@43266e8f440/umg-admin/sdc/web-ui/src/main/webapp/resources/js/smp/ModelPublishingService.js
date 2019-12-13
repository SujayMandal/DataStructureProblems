'use strict';

var ModelPublishingService = function($http, $q) {

	this.getModeAPI = function(id) {
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'publishVersion/getModelApi/' + id
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("error in ajax call:" + data);
		});
		return deferred.promise;
	};

	this.getAllTenantModelNames = function() {
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'publishVersion/listVersionNames/'
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("error in ajax call:" + data);
		});
		return deferred.promise;
	};

	this.getTenantModelDesc = function(tenantModelName) {
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'publishVersion/getVersionDescription/' + tenantModelName
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("error in ajax call:" + data);
		});
		return deferred.promise;
	};

	this.getExistingVersions = function(tenantModelName) {
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'publishVersion/getMajorVersions/' + tenantModelName
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("error in ajax call:" + data);
		});
		return deferred.promise;
	};

	this.getModelApiDetails = function(executionLanguage) {
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'publishVersion/listLibraryDetails/' + executionLanguage
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("error in ajax call:" + data);
		});
		return deferred.promise;
	};

	
	this.getModelReportDetails = function(executionLanguage) {
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'publishVersion/listReportDetails/' + executionLanguage
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("error in ajax call:" + data);
		});
		return deferred.promise;
	};

	this.getNewRLibraries = function(executionLanguage) {
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'publishVersion/rModelPackageLibraries/' + executionLanguage
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("error in ajax call:" + data);
		});
		return deferred.promise;
	};

	this.getFilteredRLibraries = function(pageInfo, executionLanguage) {
		var deferred = $q.defer();
		$http({
			method : 'POST',
			url : 'publishVersion/searchNewLibrary/' + executionLanguage,
			data : pageInfo
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("error in ajax call:" + data);
		});
		return deferred.promise;
	};

	this.getFilteredLibraries = function(pageInfo, executionLanguage) {
		var deferred = $q.defer();
		$http({
			method : 'POST',
			url : 'publishVersion/searchLibrary/' + executionLanguage,
			data : pageInfo
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("error in ajax call:" + data);
		});
		return deferred.promise;
	};

	this.getFilteredDefinitions = function(pageInfo, executionLanguage) {
		var deferred = $q.defer();
		$http({
			method : 'POST',
			url : 'publishVersion/searchIoDefn/' + executionLanguage,
			data : pageInfo
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("error in ajax call:" + data);
		});
		return deferred.promise;
	};

	this.saveVersion = function(version, modelType, modelDefType) {
		var deferred = $q.defer();
		var versionCopy = angular.copy(version);
		var formData = new FormData();

		if (modelType == 'NEW') {
			if (versionCopy.modelLibrary.executionLanguage.indexOf('Matlab') != -1) {
				formData.append('jar', version.modelLibrary.jar);
				versionCopy.modelLibrary.id = '';
			} else {
				formData.append('jar', version.modelLibrary.jar);
				if (versionCopy.modelLibrary.manifestFile != '') {
					formData.append('manifestFile', version.modelLibrary.manifestFile);
				}
			}
		}

		if (modelDefType == 'NEW') {
			formData.append('excel', version.mapping.model.excel);
			formData.append('documentation', version.mapping.model.documentationName);
			versionCopy.mapping.model.id = '';
		}

		delete versionCopy.modelLibrary.jar;
		delete versionCopy.modelLibrary.manifestFile;
		delete versionCopy.mapping.model.xml;
		delete versionCopy.mapping.model.excel;
		delete versionCopy.mapping.model.documentationName;
		delete versionCopy.versionType;

		formData.append('versionInfo', JSON.stringify(versionCopy));

		$http({
			method : 'POST',
			headers : {
				'Content-Type' : undefined
			},
			url : 'publishVersion/saveAllVersionData',
			transformRequest : angular.identity,
			data : formData
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject(data);
		});
		return deferred.promise;
	};

	this.getAllEnvironments = function() {
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'plugin/allExecutionEnvironments'
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject(data, status, headers, config);
		});
		return deferred.promise;
	};

	this.downloadIO = function(txnId, versionName, status) {
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'txnDashBoard/downloadModelAndTntIO?idList=' + txnId + '&apiName=' + versionName + '&status=' + status
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject(data, status, headers, config);
		});
		return deferred.promise;
	};
};