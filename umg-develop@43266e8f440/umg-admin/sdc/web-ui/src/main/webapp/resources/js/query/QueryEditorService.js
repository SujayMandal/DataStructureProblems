'use strict';
var QueryEditorService = function($http, $q) {

	/**
	 * ============================================== uploading csv file and
	 * receives the data as JSON object
	 */
	this.fetchQueryList = function(TIDName) {
		var deferred = $q.defer();
		$http({
			method : 'GET',
			url : 'syndicateDataQueries/list/' + TIDName
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("error in ajax call:" + data);
		});
		return deferred.promise;
	};

	/**
	 * Below method will send request to server to save Query
	 */
	this.saveQuery = function(query) {

		var deferred = $q.defer();

		$http({
			method : 'POST',
			url : 'syndicateDataQueries/save',
			data : query
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("Error came with status code :" + status);
		});

		return deferred.promise;

	};

	this.updateQuery = function(query) {

		var deferred = $q.defer();

		$http({
			method : 'POST',
			url : 'syndicateDataQueries/update',
			data : query
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("Error came with status code :" + status);
		});

		return deferred.promise;

	};

	this.testQuery = function(query) {

		var deferred = $q.defer();

		$http({
			method : 'POST',
			url : 'syndicateDataQueries/testQuery',
			data : query
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("Error came with status code :" + status);
		});

		return deferred.promise;

	};

	this.getColumns = function(selectStmt) {
		var outputParams = [];
		var invalidAlias = false;
		if (!angular.isUndefined(selectStmt)) {
			selectStmt = selectStmt.toUpperCase().trim();
			var tokens = selectStmt.split(',');
			var sequence = 1;
			angular.forEach(tokens, function(token) {
				token = token.replace(' AS ', ' ').trim();
				var col = token.substring(0, token.indexOf(' ')).trim();
				var alias = token.substring(token.indexOf(' ')).trim();
				if (col == "" && alias.indexOf('.') != -1) {
					var dotIndex = alias.indexOf('.');
					alias = alias.substring(dotIndex + 1);
				}
				if (alias !== "" && !alias.match(/^(_*)\w*[a-zA-Z0-9]+(_*)$/)) {
					invalidAlias = true;
				} else {
					var coldef = {
						name : alias,
						dataType : "",
						sequence : sequence++
					};
					outputParams.push(coldef);
				}
			});
		}
		if (invalidAlias) {
			return invalidAlias;
		}
		return outputParams;
	};

	this.getCoulmnDefs = function(outputParameters) {
		var colDefs = [];
		angular.forEach(outputParameters, function(data) {
			colDefs.push({
				field : data.name,
				displayName : data.name
			});
		});
		return colDefs;
	};

	/**
	 * ========================================== This method will invoke the
	 * saving squence method of the rest service with data
	 */
	this.saveSequence = function(sequencedQueries) {
		var deferred = $q.defer();
		$http({
			method : 'POST',
			url : 'syndicateDataQueries/updateSequence',
			data : sequencedQueries
		}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("Error came with status code :" + status);
		});
		return deferred.promise;
	};

};