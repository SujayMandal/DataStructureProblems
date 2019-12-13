'use strict';

var QueryEditorCtrl = function($scope, $timeout, $filter, $log, $location, queryEditorService, sharedPropertiesService){
	//this flag identify the mapping is used any published or deactivated version 	
	$scope.publishedOrDeactivated=false;
	
	//this flag used to mark change in grid data
	$scope.gridDataChange=0;
		
		/* Initial Setup */
	
	    var testdate = null;
		var initDate= function() {

			Date.prototype.stdTimezoneOffset = function() {
	    	    var jan = new Date(this.getFullYear(), 0, 1);
	    	    var jul = new Date(this.getFullYear(), 6, 1);
	    	    return Math.max(jan.getTimezoneOffset(), jul.getTimezoneOffset());
	    	};

	    	Date.prototype.dst = function() {
	    	    return this.getTimezoneOffset() < this.stdTimezoneOffset();
	    	};

	    	var TIMEZONE_OFFSET = ((new Date()).dst()) ? '-4.00' : '-4.00';
	    	
		    var d = new Date();
		    var  utc = d.getTime() + (d.getTimezoneOffset() * 60000);
		    testdate = $filter('date')(new Date(utc + (3600000*TIMEZONE_OFFSET)),"MMM-dd-yyyy HH:mm a");
		};
		
		initDate();		
		
		
		
		$scope.initSetup= function() {
			if(sharedPropertiesService.get("publishedOrDeactivated")!=undefined){
				$scope.publishedOrDeactivated=sharedPropertiesService.get("publishedOrDeactivated");
				sharedPropertiesService.remove("publishedOrDeactivated");				
			}
			$scope.versionId = sharedPropertiesService.get("versionId");
			$scope.versionNo = sharedPropertiesService.get("versionNo");
			$scope.apiName = sharedPropertiesService.get("apiName");
			sharedPropertiesService.remove("versionId");
			sharedPropertiesService.remove("versionNo");
			sharedPropertiesService.remove("apiName");
		};
		$scope.initSetup();
		$scope.qe = {
						name: "",
						description: "",
						mapping: {name: ""},
					    queryObject: {fromString: "",selectString: "",whereClause: "",orderByString: ""},
					    rowType: "",
					    dataType: "",
					    inputParameters: [{name: "TESTDATE", dataType: "DATE-MMM-DD-YYYY", sampleValue: testdate}],
					    outputParameters: [], 	
					    mappingType: "",
					    queryLaunchInfo: {},
					    execSequence: 1
					};
		
		$scope.formValidated = false;
		$scope.showSuccessMsg = false;
		$scope.showErrMsg = false;
		$scope.message = "";
		$scope.saveQueryFlag = false;
		$scope.showResult = false;
		$scope.queryInputMap = null;
		$scope.addQuery = null;
		$scope.viewQuery = null;
		$scope.editQuery = null;
		$scope.viewQueryFlag = false;
		$scope.editQueryFlag = false;
		$scope.isColDefValid = true;
		$scope.enableTest = true;
				
		$scope.rows = [{displayName: "SINGLEROW", value: "SINGLEROW" },{displayName: "MULTIPLEROW", value: "MULTIPLEROW" }];
		$scope.qe.rowType = $scope.rows[0].value;
		
		$scope.datatypes = [{displayName: "PRIMITIVE", value: "PRIMITIVE" },{displayName: "ARRAY", value: "ARRAY" },{displayName: "OBJECT", value: "OBJECT" },{displayName: "SINGLE_DIM_ARRAY", value: "SINGLE_DIM_ARRAY" }];
		$scope.qe.dataType = $scope.datatypes[0].value;
		
		//get the save button status from tid mapping page
		var saveButtonListpageStatus = sharedPropertiesService.get('hideSaveButtonListPage');
		sharedPropertiesService.remove('hideSaveButtonListPage');
		
		
		/**
		 * This method is to set Query Editor Form based on request.
		 * */
		$scope.fromSecToUpper=function(){
			$scope.qe.queryObject.fromString=$scope.qe.queryObject.fromString.toUpperCase();
		};
		$scope.setupVerIdandNo=function(){			
			 sharedPropertiesService.put("versionId",$scope.versionId); 
			 sharedPropertiesService.put("versionNo",$scope.versionNo);
			 sharedPropertiesService.put("apiName",$scope.apiName);
		};
	
		
		var setQueryEditor = function(queryInfo,qeLaunchInfo){
			$log.info("Setting Query Editor...");
			$scope.qe = queryInfo;
			$scope.qe.mapping.name = qeLaunchInfo.tidName;
			$scope.qe.mappingType = qeLaunchInfo.type; 
			$scope.qe.queryLaunchInfo = qeLaunchInfo;
			if(qeLaunchInfo.type === "INPUTMAPPING"){
				$scope.queryInputMap = qeLaunchInfo.tidInput;
			}
			if(qeLaunchInfo.type === "OUTPUTMAPPING"){
				$scope.queryInputMap = qeLaunchInfo.midOutput;
			}
			setInputParams(queryInfo.inputParameters,$scope.queryInputMap);
			$scope.setupVerIdandNo();
		};

		var setInputParams = function(inputParameters,tidMidInput){
			var inputParams = [];
			angular.forEach(inputParameters, function(data){
				$log.info("Input Parameter is : "+data.name);
				angular.forEach(tidMidInput, function(tid){
					if(angular.equals(data.name, tid.flatenedName)){
						$log.info("Input Parameter Matched");
						data.dataType = tid.dataTypeStr;
					}
					if(angular.equals(data.name, 'TESTDATE')){
						data.dataType = 'DATE-MMM-DD-YYYY';
					}
				});
				var inputParam = {name: data.name, dataType: data.dataType, sampleValue: data.sampleValue};
				inputParams.push(inputParam);
			});
			$scope.qe.inputParameters = inputParams;			
		};

		
		/**
		 * This code will get call when user will request to add new query for particular TID mapping.
		 * */
		
		$scope.addQuery = sharedPropertiesService.get("queryInputMap");
		if($scope.addQuery != null){
			setQueryEditor($scope.qe,$scope.addQuery);	
			$scope.setupVerIdandNo();
		}
		
		/**
		 * This code will get call when user will request to view and test query for particular TID mapping.
		 * */
		
		$scope.viewQuery = sharedPropertiesService.get("viewQuery");
		if($scope.viewQuery != null){
				$scope.viewQueryFlag = true;
				setQueryEditor($scope.viewQuery.queryInfo,$scope.viewQuery.queryLaunchInfo);
				sharedPropertiesService.remove("viewQuery");
				$scope.setupVerIdandNo();
		}
		
		/**
		 * This code will get call when user will request to update and test query for particular TID mapping.
		 * */
		
		$scope.editQuery = sharedPropertiesService.get("editQuery");
		if($scope.editQuery != null){				
				$scope.editQueryFlag = true;
				setQueryEditor($scope.editQuery.queryInfo,$scope.editQuery.queryLaunchInfo);
				sharedPropertiesService.remove("editQuery");
				$scope.setupVerIdandNo();
		}
		
		/**
		 * This code transfer the request back to TID mapping screen.
		 * */
		
		$scope.viewTidMapping = function(){
			sharedPropertiesService.put("tidCallingType","edit");
			sharedPropertiesService.put("tidName",$scope.qe.mapping.name);
			sharedPropertiesService.put("publishedOrDeactivated", $scope.publishedOrDeactivated);
			
			//setting the save button status if it was set as true when this page was visited
			if (saveButtonListpageStatus == true) {
				sharedPropertiesService.put('hideSaveButton',true);
			}
			$scope.setupVerIdandNo();
			$location.path('addTid');
		};
		
		/**
		 * This code transfer the request to view existing queries for particular TID mapping.
		 * */
		
		$scope.viewQueriesForTID = function(){
			sharedPropertiesService.put("queryLaunchInfo",$scope.qe.queryLaunchInfo);
			//setting the save button status if it was set as true when this page was visited also fixing for bug UMG-1447
			if (saveButtonListpageStatus == true) {
				sharedPropertiesService.put('hideSaveButton',true);
			}
			sharedPropertiesService.put("publishedOrDeactivated", $scope.publishedOrDeactivated);
			$scope.setupVerIdandNo();
			$location.path('queryView');
		};
		
		/**
		 * Below code set pagination for result Grid.
		 * */
		
		$scope.totalResultItems = 0;
		
		$scope.pagingOptions = {
		        pageSizes: [10, 20, 50, 500],
		        pageSize: 10,
		        currentPage: 1
		    };
	
		$scope.setPagingData = function(data, outputParameters, page, pageSize){	
			$scope.allRecords = data;
			$scope.pagedResultData = data.slice((page - 1) * pageSize, page * pageSize);
	        $scope.myData = $scope.pagedResultData;
	        $scope.colDef = queryEditorService.getCoulmnDefs(outputParameters);
	        $scope.totalResultItems = data.length;
	        
	        if($scope.gridDataChange==0)
	        	$scope.gridDataChange=1;
	        else
	        	$scope.gridDataChange=0;
	        
	        if (!$scope.$$phase) {
	            $scope.$apply();
	        }
	    };
        
	    /**
		 * Below object is the Grid, which shows result after test pass.
		 * */
	    
	    $scope.resultSetGrid = { data: 'myData',
    			 columnDefs:'colDef',
				 pagingOptions: $scope.pagingOptions,
				 showFilter: true,
				 enablePaging: true,
				 footerTemplate:'resources/partial/query/result-set-footer.html'
			};
	    
	    /**this function is for grid loading* audhyabh */
	    $scope.launchQueryResultDailog = function(){
			/*$timeout(function(){
				$scope.resultSetGrid.$gridServices.DomUtilityService.RebuildGrid(
	                    $scope.resultSetGrid.$gridScope,
	                    $scope.resultSetGrid.ngGrid
	                );
			},10);*/
		};
	    /**
		 * This method is responsible for query testing.
		 * */
	    
		$scope.testQE = function(){
			$log.info("Request received to Test Query.");
			$scope.showSuccessMsg = false;
			$scope.showErrMsg = false;
			//$scope.message = "";
			$scope.showResult = false;
			if($scope.formValidated && $scope.isColDefValid && validateInputParams()){
				$log.info("Test Bed is Validated");
				queryEditorService.testQuery($scope.qe).then(
						function(responseData){
							if(responseData.error){
								$scope.enableTest = false;
								$scope.showErrMsg = true;								
								$scope.message = responseData.message;
								$scope.showResult = false;
							}else{
							$scope.enableTest=true;
							$log.info("Query Successfully Passed");
							$scope.showSuccessMsg = true;
							$scope.showResult = true;
							$scope.message = "Successfully Passed and Time Taken : "+responseData.response.queryExecutionTime+" ms";
							$scope.qe.outputParameters = responseData.response.syndicateDataQryOutput;
							if(responseData.response.queryResponse.length > 0){
								$scope.saveQueryFlag = true;
							}
							else{
								$scope.saveQueryFlag = false;
							}
							$scope.setPagingData(responseData.response.queryResponse,responseData.response.syndicateDataQryOutput, $scope.pagingOptions.currentPage,$scope.pagingOptions.pageSize);
							$log.info('Executed Query : ' + responseData.response.executedQuery);
							}
						},
						function(errorData){
							$scope.showErrMsg = true;
							$scope.message = errorData;
							$scope.showResult = false;
						},
						function(responseData) {
							 alert('System Failed: ' + responseData);
						 }
						
				);
			}else{
				$scope.showErrMsg = true;
				//$scope.message = "Query is not Appropriate!";
				$log.error("Test Bed Validation is Failed !");
			}
			$scope.setupVerIdandNo();
		};
		
		 /**
		 * This method is responsible for query saving.
		 * */
	
	
		$scope.saveQE = function(){
			$log.info("Request received to Save Query Editor");
			$scope.showSuccessMsg = false;
			$scope.showErrMsg = false;
			$scope.message = "";
			if($scope.formValidated){
			queryEditorService.saveQuery($scope.qe).then(
					function(responseData) {
						if(responseData.error){
							$log.error(responseData.message);
							$scope.showErrMsg = true;
							$scope.message = responseData.message;
							
						}else{
						$log.info("Successfully Saved");
						$scope.showSuccessMsg = true;
						$scope.message = "Successfully Saved";
						}
					},
					function(errorData) {
						$scope.showErrMsg = true;
						$scope.message = errorData;
					},
					function(responseData) {
						 alert('System Failed: ' + responseData);
					 }
			);
			}else{
				$log.error("Form Validation Failed !");
			}
			$scope.setupVerIdandNo();
		};
	
		 /**
		 * This method is responsible for query update.
		 * */
		
		$scope.updateQE = function(){
			$log.info("Request received to Update Query Editor");
			$scope.showSuccessMsg = false;
			$scope.showErrMsg = false;
			$scope.message = "";
			if($scope.formValidated){
				$log.info("Updated QE : "+$scope.qe);
				queryEditorService.updateQuery($scope.qe).then(
						function(responseData) {
							$log.info("Successfully Updated");
							$scope.showSuccessMsg = true;
							$scope.message = "Successfully Updated";
						},
						function(errorData) {
							$scope.showErrMsg = true;
							$scope.message = errorData;
						},
						function(responseData) {
							 alert('System Failed: ' + responseData);
						 }
				);
			}else{
				$log.error("Form Validation Failed !");
			}
			$scope.setupVerIdandNo();
		};
		
		 /**
		 * This method set input parameters from where clause and with respective TID mappings.
		 * */
	
		$scope.setInputParameters = function(whereClause){
			$log.info("Setting Input Parameters ...");
			$scope.qe.inputParameters = [{name: "TESTDATE", dataType: "DATE-MMM-DD-YYYY", sampleValue: testdate}];
			$scope.showSuccessMsg = false;
			$scope.showErrMsg = false;
			$scope.message = "";
			$scope.formValidated = true;
			
			var invalidParams = [];
			
			if(!angular.isUndefined(whereClause)){
				do{
					var i1 = whereClause.indexOf('#');
					whereClause = whereClause.substring(i1+1);
					var i2 = whereClause.indexOf('#');
					if(i2==-1 && i1!=-1){
						$scope.showErrMsg = true;
						$scope.message = "Tid input parameter(s) :"+whereClause+" should start and end with #.";
						$scope.formValidated = false;
						break;			
					}
					
					if(i2 != -1)
					{
						var column = whereClause.substring(0,i2).trim();
						var isMatchingFound = false;
						angular.forEach($scope.queryInputMap, function(data){
							
							if(column === data.flatenedName){
								isMatchingFound = true;
								var coldef = {name: column, dataType: data.dataTypeStr, sampleValue: ""};
								$scope.qe.inputParameters.push(coldef);
								return 0 ;
							}
						});
						
						if(!isMatchingFound){
							invalidParams.push(column);
						}
						whereClause = whereClause.substring(i2+1);
					}
				}while(whereClause.indexOf('#') != -1);
			}
			
			if(invalidParams.length > 0){
				$scope.showErrMsg = true;
				$scope.message = invalidParams+" parameter(s) Not Available in TID Mapping";
				$scope.formValidated = false;
			}
			$scope.setupVerIdandNo();
		};
		
		/**
		 * This method will validate input parameters and will change the format of date
		 * */
		
		var validateInputParams = function(){			
			var isValidate = true;
			var formatedInputParamns = [];
			angular.forEach($scope.qe.inputParameters, function(data){
				if(data.dataType.indexOf("DATE") != -1){
					var incomingdate = new Date(data.sampleValue);
					$log.info("Incoming Date : "+incomingdate);
					if(Object.prototype.toString.call(incomingdate) === "[object Date]" &&  isNaN( incomingdate.getTime() ) ){
						$scope.showErrMsg = true;
						$scope.message = data.name+" have Invalid Date.";
						isValidate = false;
					}else{
						var dateFormat = data.dataType.substring(5).toLocaleLowerCase();
						do{
							dateFormat = dateFormat.replace('m','M');
						}
						while(dateFormat.indexOf('m') != -1);
						if(data.name === 'TESTDATE'){
							dateFormat = 'MMM-dd-yyyy';
						}
						var dateTimeFormat = dateFormat + ' HH:mm';
						data.sampleValue = $filter('date')(incomingdate,dateTimeFormat); 
					}
				}
				formatedInputParamns.push({name: data.name, dataType: data.dataType, sampleValue: data.sampleValue});
			});
			$scope.qe.inputParameters = formatedInputParamns;
			$scope.setupVerIdandNo();
			return isValidate;
		};
		
		/**
		 * This method set coulumns from select statement.
		 * */
	
		$scope.setOutputParameters = function(selectStmt){
			$log.info("Setting Output Parameters ...");
			$scope.showSuccessMsg = false;
			$scope.showErrMsg = false;
			$scope.message = "";	
			$scope.isColDefValid = true;
			$scope.qe.outputParameters = [];
			if(typeof queryEditorService.getColumns(selectStmt) === 'boolean'){
				$scope.showErrMsg = true;
				$scope.isColDefValid = false;
				$scope.message = "Column's alias can't contain any special character except UNDERSCORE";
			}
			else{
				$scope.qe.outputParameters = queryEditorService.getColumns(selectStmt);
			}		
		};
	
		/**
		 * This watch keeps eye on pagination operation.
		 * */
		
		$scope.$watch('pagingOptions', function (newVal, oldVal) {
		    	
		    	!angular.isNumber(newVal.currentPage) ? (newVal.currentPage = 0): (newVal.currentPage = newVal.currentPage); 
		        
		    	if (newVal !== oldVal) {
		        	$scope.currentMaxPages = $scope.maxPages();
		        	if(newVal.currentPage * newVal.pageSize >= $scope.totalResultItems){
		        		$scope.pagingOptions.currentPage = $scope.currentMaxPages;
		        	}
		        	if(newVal.currentPage * newVal.pageSize <= 0){
		        		$scope.pagingOptions.currentPage = 1;
		        	}
		        	$scope.setPagingData($scope.allRecords,$scope.qe.outputParameters, $scope.pagingOptions.currentPage,$scope.pagingOptions.pageSize);
		        }
		    }, true);
	 
		/**
		 * If there is any change query, this code will disable save and update button.
		 * */
		
		 $scope.$watch('qe',function(n,o){
			 if(n.dataType != o.dataType || n.name != o.name || n.queryObject.fromString != o.queryObject.fromString || n.queryObject.selectString != o.queryObject.selectString || n.queryObject.whereClause != o.queryObject.whereClause || n.queryObject.orderByString != o.queryObject.orderByString || n.rowType != o.rowType){
				 $scope.saveQueryFlag = false;
			 }
		 },true);
	
		 /**
		 * If there is any change in gridData it will resize the grid
		 * */
		 $scope.$watch('gridDataChange', function(n, o){
			    if (n != o) {
			        window.setTimeout(function(){
			            $(window).resize();
			            $(window).resize();
			        }, 100);
			    }
			});
		 
		 /* Paging Operations */
    
	    $scope.maxRows = function () {
	        var ret = Math.max($scope.totalResultItems, $scope.pagedResultData.length);
	        return ret;
	    };
	    
	    $scope.$on('$destroy', $scope.$watch('totalResultItems',function(n,o){
	    	if(n > 0){
	    		$scope.currentMaxPages = $scope.maxPages();
	    		}
	    }));
	    	
	    $scope.maxPages = function () {
	        if($scope.maxRows() === 0) {
	            return 1;
	        }
	        return Math.ceil($scope.maxRows() / $scope.pagingOptions.pageSize);
	    };
	    
	    $scope.pageToFirst = function(){
			$scope.pagingOptions.currentPage = 1;
		};
		
		$scope.pageBackward = function(){
			var page = $scope.pagingOptions.currentPage;
	        $scope.pagingOptions.currentPage = Math.max(page - 1, 1);
		};
		
		$scope.pageForward = function(){
			var page = $scope.pagingOptions.currentPage;
	        if ($scope.totalResultItems > 0) {
	            $scope.pagingOptions.currentPage = Math.min(page + 1, $scope.maxPages());
	        } else {
	            $scope.pagingOptions.currentPage++;
	        }
		};
		
		$scope.goToLastPage = function(){
			var maxPages = $scope.maxPages();
	        $scope.pagingOptions.currentPage = maxPages;
		};
		
		$scope.cantPageForward = function() {
	        var curPage = $scope.pagingOptions.currentPage;
	        var maxPages = $scope.maxPages();
	        if ($scope.totalResultItems > 0) {
	            return curPage >= maxPages;
	        } else {
	            return $scope.pagedResultData.length < 1;
	        }
	    };
	    
	    $scope.cantPageToLast = function() {
	        if ($scope.totalResultItems > 0) {
	            return $scope.cantPageForward();
	        } else {
	            return true;
	        }
	    };
	    
	    $scope.cantPageBackward = function() {
	        var curPage = $scope.pagingOptions.currentPage;
	        return curPage <= 1;
	    };
	    
		
};