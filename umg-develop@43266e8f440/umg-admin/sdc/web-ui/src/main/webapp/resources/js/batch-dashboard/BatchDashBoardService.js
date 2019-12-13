'use strict';
var BatchDashBoardService = function($http, $q, $filter) {
		
	this.fetchPagedDataAsync = function(pageInfo){
			var deferred = $q.defer();
			$http({
				method : 'POST',
				url : 'batchDashBoard/getPagedBatchData',
				data : pageInfo
			}).success(function(data, status, headers, config) {
				deferred.resolve(data);
			}).error(function(data, status, headers, config) {
				deferred.reject("Error:" + data);
				alert("error in ajax call:" + data);
			});
			return deferred.promise;
	};
	
	this.invalidateBatch = function(batchId) {
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'batchDashBoard/invalidateBatch/'+batchId			
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			alert("error in ajax call:" + data);
		});
		return deferred.promise;	
	};
	
	// for batch execution
	this.executeBatchFile = function(uploadExcelFile) {
		var deferred = $q.defer();
		var formData = new FormData();
		formData.append('excelFile', uploadExcelFile);	
		$http({
			method : 'POST',
			headers: {'Content-Type': undefined},
			url : 'batch/uploadRequestJSON',
			transformRequest: angular.identity,
			data :formData
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("error in ajax call:" + JSON.stringify(data));
		});
		return deferred.promise;
	};
	
	this.executeBulkFile = function(uploadJsonFile) {
		var deferred = $q.defer();
		var formData = new FormData();
		formData.append('jsonFile', uploadJsonFile);	
		$http({
			method : 'POST',
			headers: {'Content-Type': undefined},
			url : 'versiontest/executeBulkJsonFile',
			transformRequest: angular.identity,
			data :formData
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("error in ajax call:" + JSON.stringify(data));
		});
		return deferred.promise;
	};
	
	this.getBtchSelectionRecordCntLimit = function () {
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'batchDashBoard/getBtchSelectRcrdCntLimit'
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			alert("error in ajax call:" + data);
		});
		return deferred.promise;
	};
		
	this.downloadSelectedBatchItems = function(selectedBatchIds){
		var deferred = $q.defer();
		var fd =  new FormData();
		fd.append('selectedBatchIds',selectedBatchIds);
		$http({
			method : 'POST',
			url : 'batchDashBoard/downloadSelectedBatchIO',
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
	};
	
	this.terminnateBatch = function(batchId) {
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'batchDashBoard/terminateBatch/' + batchId			
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			alert("error in ajax call:" + data);
		});
		return deferred.promise;	
	};	
	
	this.downldbatchUsageRprtForSelected = function(selectedTransactions, sortColumn, descending, cancelRequestId) {
		var deferred = $q.defer();
		var formData = new FormData();
		formData.append('selectedTransactionList', selectedTransactions);
		formData.append('sortColumn', sortColumn);
		formData.append('cancelRequestId', cancelRequestId);
		formData.append('descending', descending);
		$http({
			method : 'POST',
			url : 'batchDashBoard/downloadBatchUsageReportByTransactionList',
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
	
	this.downldUsageRprtUsngFltr = function(txnFilter, advanceTransactionFilter, pagingOptions) {
		var txnFilterData=angular.copy(txnFilter);
		//txnFilterData.page=pagingOptions.currentPage;
		txnFilterData.pageSize='50000';
		txnFilterData.sortColumn=pagingOptions.sortColumn;
		txnFilterData.descending=pagingOptions.descending;
		if(txnFilterData.fromDate!=undefined){
			txnFilterData.fromDateToString=$filter('date')(txnFilterData.fromDate,"yyyy-MMM-dd HH:mm");
			txnFilterData.fromDate = null;
		}
		if(txnFilterData.toDate!=undefined){
			txnFilterData.toDateToString=$filter('date')(txnFilterData.toDate,"yyyy-MMM-dd HH:mm");
			txnFilterData.toDate = null;
		}
		
		var deferred = $q.defer();
		var formData = new FormData();
		formData.append('txnFilterData', JSON.stringify(txnFilterData));
		formData.append('advanceTransactionFilter', JSON.stringify(advanceTransactionFilter));
		$http({
			method : 'POST',
			url : 'batchDashBoard/downloadBatchUsageReportByFilter',
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
};
