'use strict';
var SyndicateDataService = function($http, $q) {
	
	/**==============================================
	 * uploading csv file and receives the data as JSON object
	 */
	this.fetchCSVDataList = function(csvFile) {
		var deferred = $q.defer();
		var formData = new FormData();
		formData.append('syndicateDataFile', csvFile);	
		$http({
			method : 'POST',
			headers: {'Content-Type': undefined},
			url : 'syndicateData/parseFileData',
			transformRequest: angular.identity,
			data :formData
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("error in ajax call:" + data);
		});
		return deferred.promise;
	};
	
	/**==============================================
	 * uploading csv file and receives the container definition as JSON object
	 */
	this.fetchCSVContainerDefinition = function(syndicateData) {
		var deferred = $q.defer();
		var formData = new FormData();
		formData.append('syndContainerDefFile', syndicateData.syndicateCsvFile);
		var metadata = angular.copy(syndicateData.metaData);
		formData.append('syndContainerDataFile', JSON.stringify(metadata));
		$http({
			method : 'POST',
			headers: {'Content-Type': undefined},
			url : 'syndicateData/parseDefinition',
			transformRequest: angular.identity,
			data :formData
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("error in ajax call:" + data);
		});
		return deferred.promise;
	};
	
	/**==============================================
	 * saving syndicate data along with file
	 */
	this.save = function(syndicateData) {
		var deferred = $q.defer();
		var formData = new FormData();
	    formData.append('syndData', syndicateData.syndicateXlsxFile);
		 var syndicateDataCopy = angular.copy(syndicateData);				 
		 delete syndicateDataCopy.syndicateXlsxFile;
		 delete syndicateDataCopy.syndicateCsvFile;
		 delete syndicateDataCopy.columnHeaderCounter;
		 delete syndicateDataCopy.validFrom;
		 delete syndicateDataCopy.validTo;
		 if(syndicateDataCopy.id!=undefined){
		 delete syndicateDataCopy.id;
		 }
		 
		 formData.append('containerInfo', JSON.stringify(syndicateDataCopy));
		$http({
			method : 'POST',
			url : 'syndicateData/version/create',
			data :formData,
			headers:{'Content-Type':undefined},
			transformRequest: angular.identity
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("error in ajax call:" + data);
		});
		return deferred.promise;
	};
	
	this.updateVersion = function(syndicateData) {
		var deferred = $q.defer();
		var syndicateDataCopy = angular.copy(syndicateData);
		 
		 delete syndicateDataCopy.columnHeaderCounter;
		 delete syndicateDataCopy.metaData;
		 delete syndicateDataCopy.validFrom;
		 delete syndicateDataCopy.validTo;
		 if(syndicateDataCopy.id!=undefined){
			 delete syndicateDataCopy.id;
			 }
		 
		$http({
			method : 'PUT',
			url : 'syndicateData/update/version',
			data :syndicateDataCopy
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("error in ajax call:" + data);
		});
		return deferred.promise;
    };

	/**==============================================
	 * saving syndicate version data along with file
	 */
	this.saveNewVersion = function(syndicateData) {
		var deferred = $q.defer();
		var formData = new FormData();
	    formData.append('syndData', syndicateData.syndicateXlsxFile);	    
		 var syndicateDataCopy = angular.copy(syndicateData);		
		 delete syndicateDataCopy.syndicateXlsxFile;
		 delete syndicateDataCopy.columnHeaderCounter;
		 delete syndicateDataCopy.validFrom;
		 delete syndicateDataCopy.validTo;
		 if(syndicateDataCopy.id!=undefined){
			 delete syndicateDataCopy.id;
		 }
		 formData.append('containerInfo', JSON.stringify(syndicateDataCopy));
		$http({
			method : 'POST',
			url : 'syndicateData/create/version',
			data :formData,
			headers:{'Content-Type':undefined},
			transformRequest: angular.identity
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("error in ajax call:" + data);
		});
		return deferred.promise;
	};

	this.fetchSyndicateDataByName = function(containerName) {
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'syndicateData/container/'+containerName,
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("error in ajax call:" + data);
		});
		return deferred.promise;
	};

	/**
	 * This method will fetch Version Information for Unique Container Name
	 * */
	this.fetchVersionInfo = function(containerName, versionId){
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'syndicateData/version/'+versionId+'/'+containerName,
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("error in ajax call:" + data);
		});
		return deferred.promise;		
	};
	
	this.fetchPagedDataAsync = function(pageInfo){
		var deferred = $q.defer();
		$http({
			method : 'POST',
			url : 'syndicateData/container/listFilteredContainer',
			data : pageInfo
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("Error:" + data);
			alert("error in ajax call:" + data);
		});
		return deferred.promise;
	};

};
