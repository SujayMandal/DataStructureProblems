'use strict';
var DashBoardService = function($http, $filter, $q, $log) {
	
	/**
	 * This method will get all transaction base on filter option and sorting order
	 */
	this.listTransactions = function(txnFilter, advanceTransactionFilter, pagingOptions) {
		var txnFilterData=angular.copy(txnFilter);
		txnFilterData.page=pagingOptions.currentPage;
		txnFilterData.pageSize=pagingOptions.pageSize;
		txnFilterData.sortColumn=pagingOptions.sortColumn;
		txnFilterData.descending=pagingOptions.descending;
		if(txnFilterData.runAsOfDateFromString!=undefined){
			txnFilterData.runAsOfDateFromString=$filter('date')(txnFilterData.runAsOfDateFromString,"yyyy-MMM-dd HH:mm");
		}
		if(txnFilterData.runAsOfDateToString!=undefined){
		txnFilterData.runAsOfDateToString=$filter('date')(txnFilterData.runAsOfDateToString,"yyyy-MMM-dd HH:mm");
		}
		
		var deferred = $q.defer();
		var formData = new FormData();
		formData.append('txnFilterData', JSON.stringify(txnFilterData));
		formData.append('advanceTransactionFilter', JSON.stringify(advanceTransactionFilter));
		$http({
			method : 'POST',
			headers : {
				'Content-Type' : undefined
			},
			url : 'txnDashBoard/listAll',
			transformRequest : angular.identity,
			data : formData
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			alert("error in ajax call:" + data);
		});
		return deferred.promise;	
	};	
	
	this.listTransactionsDefault = function(pagingOptions) {
		var pageSize=pagingOptions.pageSize;
		var formData = new FormData();
		formData.append('pagesize', JSON.stringify(pagingOptions.pageSize));
		var deferred = $q.defer();
		$http({
			method : 'POST',
			headers : {
				'Content-Type' : undefined
			},
			url : 'txnDashBoard/listAllDefault',
			transformRequest : angular.identity,
			data : formData
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			alert("error in ajax call:" + data);
		});
		return deferred.promise;	
	};	
	
	this.getOperatorList = function () {
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'txnDashBoard/getOperatorList'
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			alert("error in ajax call:" + data);
		});
		return deferred.promise;
	};
	
	this.getSelectedRecordsCountLimit = function () {
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'txnDashBoard/getSelectedRecordsCountLimit'
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			alert("error in ajax call:" + data);
		});
		return deferred.promise;
	};
	
	//This method is not in use
	this.filterTransactions = function(txnFilter) {
		var deferred = $q.defer();
		var formData = new FormData();
	    formData.append('txnFilter', txnFilter);
		$http({
			method : 'POST',
			url : 'txnDashBoard/filter',
			data : txnFilter
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			alert("error in ajax call:" + data);
		});
		return deferred.promise;	
	};
	
	// not in use
	this.downloadTenantIO = function(txnId) {
		var deferred = $q.defer();
		$http({
			method : 'POST',
			url : 'txnDashBoard/downloadTenantIO/' + txnId
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			alert("error in ajax call:" + data);
		});
		return deferred.promise;	
	};

	// not in use
	this.downloadModelIO = function(txnId) {
		var deferred = $q.defer();
		$http({
			method : 'POST',
			url : 'txnDashBoard/downloadModelIO/' + txnId
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			alert("error in ajax call:" + data);
		});
		return deferred.promise;	
	};
	
	
	this.getVersionDetails = function(versionName, fullVersion) {
		var deferred = $q.defer();
		var formData = new FormData();
		formData.append('versionName', versionName);
		formData.append('fullVersion', fullVersion);
		$http({
			method : 'POST',
			headers : {
				'Content-Type' : undefined
			},
			url : 'txnDashBoard/versionDetails',
			transformRequest : angular.identity,
			data : formData
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			alert("error in ajax call:" + data);
		});
		return deferred.promise;	
	};
	
	
	//keeping this for future use to download using post method
	this.downloadSelectedItems = function(txnIds){
		var deferred = $q.defer();
		var fd =  new FormData();
		fd.append('idList',txnIds);
		$http({
			method : 'POST',
			url : 'txnDashBoard/downloadSelectedItems',
			headers : {
				'Content-Type' : undefined,
				accept: 'application/zip'
			},
			data : fd,
			responseType: 'arraybuffer',
			cache: false,
			transformResponse: function(data, headers){
				var zip = null;
				if (data) {
					zip = new Blob([data],{type: 'application/zip'});
				}
				var fileName = getFileNameFromHeader(headers('content-disposition'));
				var result = { blob: zip, fileName: fileName };
				return {response: result};
			}
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			alert("error in ajax call:" + data);
		});
		return deferred.promise;
	};
	
	this.downldUsageRprtUsngFltr = function(txnFilter, advanceTransactionFilter, pagingOptions) {
		var txnFilterData=angular.copy(txnFilter);
		//txnFilterData.page=pagingOptions.currentPage;
		txnFilterData.pageSize='50000';
		txnFilterData.sortColumn=pagingOptions.sortColumn;
		txnFilterData.descending=pagingOptions.descending;
		if(txnFilterData.runAsOfDateFromString!=undefined){
			txnFilterData.runAsOfDateFromString=$filter('date')(txnFilterData.runAsOfDateFromString,"yyyy-MMM-dd HH:mm");
		}
		if(txnFilterData.runAsOfDateToString!=undefined){
		txnFilterData.runAsOfDateToString=$filter('date')(txnFilterData.runAsOfDateToString,"yyyy-MMM-dd HH:mm");
		}
		
		var deferred = $q.defer();
		var formData = new FormData();
		formData.append('txnFilterData', JSON.stringify(txnFilterData));
		formData.append('advanceTransactionFilter', JSON.stringify(advanceTransactionFilter));
		$http({
			method : 'POST',
			url : 'txnDashBoard/downloadUsageReportByFilter',
			headers : {
				'Content-Type' : undefined,
				accept: 'application/vnd.ms-excel'
			},
			data : formData,
			responseType: 'arraybuffer',
			cache: false,
			//transformRequest : angular.identity,
			transformResponse: function(data, headers){
				var zip = null;
				if (data) {
					zip = new Blob([data],{type: 'application/vnd.ms-excel'});
				}
				var fileName = getFileNameFromHeader(headers('content-disposition'));
				var result = { blob: zip, fileName: fileName };
				return {response: result};
			}
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			alert("error in ajax call:" + data);
		});
		return deferred.promise;	
	};	
	
	//for execution report download
	this.downldExecRprtUsngFltr = function(txnFilter, advanceTransactionFilter, pagingOptions) {
		var txnFilterData=angular.copy(txnFilter);
		//txnFilterData.page=pagingOptions.currentPage;
		txnFilterData.pageSize='50000';
		txnFilterData.sortColumn=pagingOptions.sortColumn;
		txnFilterData.descending=pagingOptions.descending;
		if(txnFilterData.runAsOfDateFromString!=undefined){
			txnFilterData.runAsOfDateFromString=$filter('date')(txnFilterData.runAsOfDateFromString,"yyyy-MMM-dd HH:mm");
		}
		if(txnFilterData.runAsOfDateToString!=undefined){
		txnFilterData.runAsOfDateToString=$filter('date')(txnFilterData.runAsOfDateToString,"yyyy-MMM-dd HH:mm");
		}
		
		var deferred = $q.defer();
		var formData = new FormData();
		formData.append('txnFilterData', JSON.stringify(txnFilterData));
		formData.append('advanceTransactionFilter', JSON.stringify(advanceTransactionFilter));
		$http({
			method : 'POST',
			url : 'txnDashBoard/downloadExecReportByFilter',
			headers : {
				'Content-Type' : undefined,
				accept: 'application/vnd.ms-excel'
			},
			data : formData,
			responseType: 'arraybuffer',
			cache: false,
			//transformRequest : angular.identity,
			transformResponse: function(data, headers){
				var zip = null;
				if (data) {
					zip = new Blob([data],{type: 'application/vnd.ms-excel'});
				}
				var fileName = getFileNameFromHeader(headers('content-disposition'));
				var result = { blob: zip, fileName: fileName };
				return {response: result};
			}
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			alert("error in ajax call:" + data);
		});
		return deferred.promise;	
	};
	
	this.downldUsageRprtForSelected = function(selectedTransactions, sortColumn, descending, cancelRequestId) {
		var deferred = $q.defer();
		var formData = new FormData();
		formData.append('selectedTransactionList', selectedTransactions);
		formData.append('sortColumn', sortColumn);
		formData.append('cancelRequestId', cancelRequestId);
		formData.append('descending', descending);
		$http({
			method : 'POST',
			url : 'tenantUsage/downloadUsageReportByTransactionList',
			headers : {
				'Content-Type' : undefined,
				accept: 'application/vnd.ms-excel'
			},
			data : formData,
			responseType: 'arraybuffer',
			cache: false,
			//transformRequest : angular.identity,
			transformResponse: function(data, headers){
				var zip = null;
				if (data) {
					zip = new Blob([data],{type: 'application/vnd.ms-excel'});
				}
				var fileName = getFileNameFromHeader(headers('content-disposition'));
				var result = { blob: zip, fileName: fileName };
				return {response: result};
			}
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			alert("error in ajax call:" + data);
		});
		return deferred.promise;	
	};	
	
	this.downldExecRprtForSelected = function(selectedTransactions, sortColumn, descending, cancelRequestId) {
		var deferred = $q.defer();
		var formData = new FormData();
		formData.append('selectedTransactionList', selectedTransactions);
		formData.append('sortColumn', sortColumn);
		formData.append('cancelRequestId', cancelRequestId);
		formData.append('descending', descending);
		$http({
			method : 'POST',
			url : 'tenantUsage/downloadExecReportByTransactionList',
			headers : {
				'Content-Type' : undefined,
				accept: 'application/vnd.ms-excel'
			},
			data : formData,
			responseType: 'arraybuffer',
			cache: false,
			//transformRequest : angular.identity,
			transformResponse: function(data, headers){
				var zip = null;
				if (data) {
					zip = new Blob([data],{type: 'application/vnd.ms-excel'});
				}
				var fileName = getFileNameFromHeader(headers('content-disposition'));
				var result = { blob: zip, fileName: fileName };
				return {response: result};
			}
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			alert("error in ajax call:" + data);
		});
		return deferred.promise;	
	};	
	
	this.downloadSelectedItemsForRerun = function(selectedTransactionList){
		var deferred = $q.defer();
		var fd =  new FormData();
		fd.append('selectedTransactionList',selectedTransactionList);
		$http({
			method : 'POST',
			url : 'modelReport/rerun',
			headers : {
				'Content-Type' : undefined,
				accept: 'application/zip'
			},
			data : fd,
			responseType: 'arraybuffer',
			cache: false,
			transformResponse: function(data, headers){
				var zip = null;
				if (data) {
					zip = new Blob([data],{type: 'application/zip'});
				}
				var fileName = getFileNameFromHeader(headers('content-disposition'));
				var result = { blob: zip, fileName: fileName };
				return {response: result};
			}
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			alert("error in ajax call:" + data);
		});
		return deferred.promise;
	};
	
	function getFileNameFromHeader(header){ 
		if (!header) 
			return null; 
		var result = header.split(";")[1].trim().split("=")[1]; 
		return result.replace(/"/g, ''); 
	}
	
	this.generateReport = function(versionName, fullVersion, transactionId) {
		var deferred = $q.defer();
		var formData = new FormData();
		formData.append('versionName', versionName);
		formData.append('fullVersion', fullVersion);
		formData.append('transactionId',transactionId);
		$http({
			method : 'POST',
			headers : {
				'Content-Type' : undefined
			},
//			url : 'report/generateReport',
			url : 'report/generateReport/versionName/fullVersion/transactionId',
			transformRequest : angular.identity,
//			data : formData
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			alert("error in ajax call:" + data);
		});
		return deferred.promise;	
	};
	
	this.checkReportTemplate = function(versionName, fullVersion) {
		var deferred = $q.defer();
		var formData = new FormData();
		formData.append('modelName', versionName);
		formData.append('fullVersion', fullVersion);
		$http({
			method : 'POST',
			headers : {
				'Content-Type' : undefined
			},
			url : 'report/hasTransactionReportTemplate',
			data : formData
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			alert("error in ajax call:" + data);
		});
		return deferred.promise;	
	};

};