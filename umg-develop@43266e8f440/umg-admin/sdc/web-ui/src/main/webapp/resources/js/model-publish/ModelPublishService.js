'use strict';

var ModelPublishService = function($http, $q) {

	this.createNewModel = function(version, isImportVersion, isPickExistingLib, isPickExistingDef) {
		var deferred = $q.defer();
		var versionCopy = angular.copy(version);
		var formData = new FormData();

		if (!isImportVersion) {
			if (!isPickExistingLib) {
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

			if (!isPickExistingDef) {
				formData.append('excel', version.mapping.model.excel);
				formData.append('documentation', version.mapping.model.documentationName);
				versionCopy.mapping.model.id = '';
			}
			
			formData.append('reportTemplate', version.reportTemplateInfo.reportTemplate);
		}

		delete versionCopy.modelLibrary.jar;
		delete versionCopy.modelLibrary.manifestFile;
		delete versionCopy.reportTemplateInfo.reportTemplate;
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

	this.extractVersionPackage = function(versionZipFile) {
		var deferred = $q.defer();
		var formData = new FormData();
		formData.append('versionZipFile', versionZipFile);
		$http({
			method : 'POST',
			headers : {
				'Content-Type' : undefined
			},
			url : 'version/getVersionFromImportFile',
			transformRequest : angular.identity,
			data : formData
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("error in ajax call:" + data);
		});
		return deferred.promise;
	};

	this.validateModelName = function(versionName) {
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'version/validateVersionName/' + versionName,
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("error in ajax call:" + data);
		});
		return deferred.promise;
	};

	this.loadAllModelNames = function() {
		var deferred = $q.defer();
		$http({
			method : 'GET',
			headers : {
				'Content-Type' : undefined
			},
			url : 'version/listAllTenantModelNames',
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("error in ajax call:" + data);
		});
		return deferred.promise;
	};
	
	this.getEnvironments = function() {
		var deferred = $q.defer();
		$http({
			method : 'GET',
			headers : {
				'Content-Type' : undefined
			},
			url : 'version/getEnvironments',
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("error in ajax call:" + data);
		});
		return deferred.promise;
	};
	
	this.getWebsocketURL = function() {
		var deferred = $q.defer();
		$http({
			method : 'GET',
			headers : {
				'Content-Type' : undefined
			},
			url : 'version/getWebsocketURL',
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("error in ajax call:" + data);
		});
		return deferred.promise;
	};
	
	this.loadAllModelNamesByEnv = function(execEnv) {
		var deferred = $q.defer();
		$http({
			method : 'GET',
			headers : {
				'Content-Type' : undefined
			},
			url : 'version/listAllTenantModelNamesByEnv?execLangauge='+execEnv,
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

	this.getModelApiDetails = function(executionLanguage,modelType) {
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'publishVersion/listLibraryDetails/' + executionLanguage + '/' + modelType
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("error in ajax call:" + data);
		});
		return deferred.promise;
	};

	this.getFilteredDefinitions = function(pageInfo, executionLanguage, modelType) {
		var deferred = $q.defer();
		$http({
			method : 'POST',
			url : 'publishVersion/searchIoDefn/' + executionLanguage + '/' + modelType,
			data : pageInfo
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("error in ajax call:" + data);
		});
		return deferred.promise;
	};

	this.getModelDetails = function(versionName) {
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'publishVersion/getVersionDetails/' + versionName
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("error in ajax call:" + data);
		});
		return deferred.promise;
	};

	this.importVersion = function(versionDetail) {
		var deferred = $q.defer();

		$http({
			method : 'POST',
			url : 'version/importNew',
			data : versionDetail
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject(data);
		});
		return deferred.promise;

	};
	
	this.getModelReportDetails = function(executionLanguage,modelType) {
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'publishVersion/listModelReports/' + executionLanguage + '/' + modelType
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("error in ajax call:" + data);
		});
		return deferred.promise;
	};
	this.getFilteredReport = function(pageInfo, executionLanguage) {
		var deferred = $q.defer();
		$http({
			method : 'POST',
			url : 'publishVersion/searchReport/' + executionLanguage,
			data : pageInfo
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("error in ajax call:" + data);
		});
		return deferred.promise;
	};
};