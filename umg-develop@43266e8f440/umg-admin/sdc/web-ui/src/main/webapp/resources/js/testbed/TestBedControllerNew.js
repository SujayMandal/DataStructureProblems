/**
 * 
 */
'use strict';
var TestBedControllerNew = function($scope, $http, $window, $q, $location, sharedPropertiesService, testBedService, $filter, $dialogs) {
	var testVersion = null;
	$scope.storeRLogs=false;
	$scope.isRModel=false;
	$scope.generateReport = false;
	$scope.reportDownloadStatus = null;
	$scope.reportDownloadError = false;
	$scope.date = {};
	$scope.datetime = {};
	$scope.date.placeholder = 'yyyy-MM-dd';
	$scope.date.min = '1000-01-01';
	$scope.date.max = '9999-12-31';
	$scope.datetime.placeholder = 'yyyy-MM-ddTHH:mm';
	$scope.datetime.min = '1000-01-01T00:00';
	$scope.datetime.max = '9999-12-31T23:59';
	$scope.tidVersion = {};
	$scope.tidVersion.finalList = {};
	$scope.outputs = '';
	$scope.hasError = false;
	$scope.isExecuted = false;
	$scope.isExecutedSucessfully = false;
	$scope.tidVersion.versionTestContainer = {};
	$scope.tidVersion.versionTestContainer.tidName = {};
	$scope.tidVersion.versionTestContainer.generateReport= false;
	$scope.tidVersion.versionTestContainer.hasModelOpValidation=false;
	$scope.tidVersion.versionTestContainer.hasAcceptableValuesValidation=false;
	$scope.showOutput = false;
	$scope.showDashBrdInfo = false;
	$scope.showMessageForTstRunFile = false;
	$scope.messageForTstRunFile = '';
	$scope.reportSuccess = false;
	$scope.showReportURL = false;
	$scope.reportUrl = '';
	$scope.reportErrorMessage = '';
	$scope.reportExecutionStatus = '';
	testVersion = sharedPropertiesService.get("testVersion");
	$scope.initSetup = function() {
		};
	$scope.booleanDropDownList = [{value:null,text:"None"},
	                          {value:"true",text:"True"},
	                          {value:"false",text:"False"}];
	var loadTestBed = function() {
		if (testVersion != null) {
			if(testVersion.source!="dashBoardTestRunData"){
			var todayDate = new Date();
			var dateString = (todayDate.getDate() < 10) ? '0' + todayDate.getDate() : todayDate.getDate();
			//todayDate.setMonth(todayDate.getMonth() + 1);
			var monthString = (todayDate.getMonth() < 10) ? '0' + (todayDate.getMonth() + 1) : (todayDate.getMonth() + 1);
			var hourString = (todayDate.getHours() < 10) ? '0' 	+ todayDate.getHours() : todayDate.getHours();
			var minuteString = (todayDate.getMinutes() < 10) ? '0' + todayDate.getMinutes() : todayDate.getMinutes();			
			$scope.tidVersion.versionTestContainer.tidName = testVersion.tidName;
			$scope.tidVersion.versionTestContainer.modelName = testVersion.modelName;
			$scope.tidVersion.versionTestContainer.majorVersion = testVersion.majorVersion;
			$scope.tidVersion.versionTestContainer.minorVersion = testVersion.minorVersion;
			$scope.tidVersion.versionTestContainer.versionId = testVersion.versionId;
			$scope.tidVersion.versionTestContainer.hasReportTemplate=testVersion.hasReportTemplate;
			$scope.generateReport = $scope.tidVersion.versionTestContainer.hasReportTemplate;
			$scope.modelOpValidation = $scope.tidVersion.versionTestContainer.hasModelOpValidation;
			$scope.acceptableValuesValidation =  $scope.tidVersion.versionTestContainer.hasAcceptableValuesValidation;
			$scope.storeRLogs= testVersion.isRModel;
			$scope.isRModel= testVersion.isRModel;
		  }
		}
		
		if(testVersion.source=="dashBoardTestRunData"){
			testBedService.getTestBedDataByTransactionId(testVersion.transactionId).then( 
					function(responseData) {
						if (!responseData.error) {
							$scope.showDashBrdInfo = true;
						var todayDate=new Date();
						var dateString = (todayDate.getDate() < 10) ? '0' + todayDate.getDate():todayDate.getDate();
						//todayDate.setMonth(todayDate.getMonth() + 1);
						var monthString = (todayDate.getMonth() < 10) ? '0' + (todayDate.getMonth() + 1) : (todayDate.getMonth() + 1);
						var hourString = (todayDate.getHours() < 10) ? '0' + todayDate.getHours():todayDate.getHours();
						var minuteString = (todayDate.getMinutes() < 10) ? '0' + todayDate.getMinutes():todayDate.getMinutes();
						$scope.tidVersion.versionTestContainer.hasReportTemplate=responseData.response.hasReportTemplate;
						$scope.tidVersion.versionTestContainer.asOnDate=responseData.response.asOnDate;
						$scope.tidVersion.versionTestContainer.tidName = responseData.response.tidName;
						$scope.tidVersion.versionTestContainer.modelName = responseData.response.modelName;
						$scope.tidVersion.versionTestContainer.majorVersion = responseData.response.majorVersion;
						$scope.tidVersion.versionTestContainer.minorVersion = responseData.response.minorVersion;
						$scope.tidVersion.versionTestContainer.versionId = responseData.response.versionId;
						$scope.tidVersion.versionTestContainer.tidIoDefinitions = responseData.response.tidIoDefinitions;		
						$scope.tidVersion.versionTestContainer.hasModelOpValidation = responseData.response.hasModelOpValidation;
						$scope.tidVersion.versionTestContainer.hasAcceptableValuesValidation = responseData.response.hasAcceptableValuesValidation;
						if(responseData.response.additionalPropsList.length>0){
							$scope.additionalPropsList=responseData.response.additionalPropsList.toString().replace(/,/g,',  ');
							$scope.message='<div>  <a style="cursor:pointer;" ng-click="testrunWarningUnmatched()">'+ responseData.response.additionalPropsList.length+"</a> extra field(s) are present in Tenant Txn Input, but not in test-bed TID.";												
						}
						if(responseData.response.defaultValuesList.length>0){	
							$scope.defaultValuesList=responseData.response.defaultValuesList.toString().replace(/,/g,',  ');
							$scope.message=$scope.message+ '<p><a style="cursor:pointer;" ng-click="testrunWarningDefault()">' +responseData.response.defaultValuesList.length+"</a> extra field(s) are present in test-bed TID, but not in Tenant Txn Input. </div></p> ";
						}
						if(responseData.response.payloadStorage != undefined && !responseData.response.payloadStorage){
							$scope.error=true;
							$scope.showMessageForTstRunFile = true;
							$scope.messageForTstRunFile = 'Payload not available for the transaction';
						}
						prepareTidInput(responseData.response.tidIoDefinitions);		
						$scope.generateReport = $scope.tidVersion.versionTestContainer.hasReportTemplate;
						$scope.modelOpValidation = $scope.tidVersion.versionTestContainer.hasModelOpValidation
						$scope.acceptableValuesValidation = $scope.tidVersion.versionTestContainer.hasAcceptableValuesValidation
						$scope.storeRLogs=testVersion.storeRLogs;
						$scope.isRModel=testVersion.isRModel;
						
					}else{	
						$scope.error = responseData.error;						
						$scope.message=responseData.message;
						$scope.toggleOutput();
					}
					}
			);
			
		}else{
			$http
				.get(
						'versiontest/loadTestVersion/'
								+ $scope.tidVersion.versionTestContainer.tidName
								+ "?id=" + Math.random())
				.success(
						function(result) {
							if (!result.errorCode) {	
								$scope.tidVersion.versionTestContainer.asOnDate = result.response.asOnDate;
								$scope.tidVersion.versionTestContainer.tidIoDefinitions = result.response.tidIoDefinitions;
								prepareTidInput(result.response.tidIoDefinitions);
							}
						});
		}	

	};
	$scope.testrunWarningUnmatched = function() {
		$("#draggable1").show();
	};
	$scope.testrunWarningDefault = function() {
		$("#draggable2").show();
	};
	$scope.showInfo = function() {
		alert($scope.message);
	};
	function prepareTidInput(tidIoDefinitions) {
		$scope.tidVersion.finalList = tidIoDefinitions;
	}
	
	loadTestBed();
	$scope.validateInput = function(ele) {
		$scope.hasError = false;
		validateElement(ele);
	};
	function validateElement(ele) {
		ele.error = false;
		ele.errorMessage = '';
		if (ele.value == null || ele.value == '') {
			if (ele.mandatory) {
				ele.error = true;
				ele.errorMessage = 'Mandatory Field';
			}
		} else {
			if(ele.validationMethod != "validate_array") {
				$scope.validate_nonArray(ele);
			} else {
				$scope[ele.validationMethod](ele);
			}
		}
	}
	$scope.execute = function() {
		$scope.tidVersion.versionTestContainer.generateReport = $scope.generateReport;
		$scope.tidVersion.versionTestContainer.hasModelOpValidation = $scope.modelOpValidation;
		$scope.tidVersion.versionTestContainer.hasAcceptableValuesValidation = $scope.acceptableValuesValidation;
		$scope.tidVersion.versionTestContainer.storeRLogs = $scope.storeRLogs;
		$scope.reportDownloadStatus = null;
		$scope.reportDownloadError = false;
		$scope.showMessageForTstRunFile = false;
		$scope.messageForTstRunFile = '';
		$scope.isExecuted = false;
		$scope.showOutput=false;
		var hasError = false;
		var deferred = $q.defer();
		$scope.validateAsOnDate();
		$scope.tidVersion.versionTestContainer.asOnDate=$filter('date')($scope.tidVersion.versionTestContainer.asOnDate,"yyyy-MMM-dd HH:mm");
		if ($scope.isAsOnDateValid) {
			angular.forEach(
					$scope.tidVersion.versionTestContainer.tidIoDefinitions,
					function(input) {
						validateElement(input);
						if (input.error) {
							hasError = true;
						}
					});
		} else {
			hasError = true;
		}
		if (!hasError) {
			$scope.isExecutedSucessfully = false;
			$http({
				method : 'POST',
				url : 'versiontest/executeVersion',
				data : $scope.tidVersion.versionTestContainer
			}).success(function(data, status, headers, config) {
				$scope.outputs = data.response.outputJson;
				$scope.isExecuted = true;
				$scope.isExecutedSucessfully = !data.error;
				$scope.reportSuccess = false;
				$scope.showReportURL = false;
				$scope.reportUrl = '';
				$scope.reportErrorMessage = '';
				$scope.reportExecutionStatus = '';
				if (data.response.reportInfo != null) {
					$scope.showReportURL = true;
					if (data.response.reportInfo.reportExecutionStatus == 'SUCCESS') {
						$scope.reportSuccess = true;
						$scope.reportUrl = data.response.reportInfo.reportURL;		
						$scope.transactionId=data.response.reportInfo.transactionId;
						$scope.reportName=data.response.reportInfo.reportName;
					} else if (data.response.reportInfo.reportExecutionStatus == 'FAILED') {
						$scope.reportSuccess = false;
						$scope.reportErrorMessage = data.response.reportInfo.errorMessage;		
					}
					$scope.reportExecutionStatus = data.response.reportInfo.reportExecutionStatus;
				}
				$scope.error = data.error;
				$scope.errorCode = data.errorCode;
				$scope.message = data.message;
				deferred.resolve(data);
				$('#testBedColpButtn').show();
				$scope.toggleOutput();
			}).error(function(data, status, headers, config) {				
				if (data != null) {
					deferred.reject("Error came with status code :" + status);
					$('#testBedColpButtn').show();
					$scope.toggleOutput();
				} else {
					$scope.outputs = ' ';
					$scope.isExecuted = true;
					$scope.isExecutedSucessfully = false;
					$scope.reportSuccess = false;
					$scope.showReportURL = false;
					$scope.reportUrl = '';
					$scope.reportErrorMessage = '';
					$scope.reportExecutionStatus = '';
					$scope.error = true;					
					$scope.message = 'Failed to load resource: net::ERR_CONNECTION_RESET.';
					$('#testBedColpButtn').show();
					$scope.toggleOutput();		
				}								
			});
		} else {
			$scope.hasError = true;
		}
		
		return deferred.promise;
	};
	
	$scope.downloadReport = function() {
		$scope.reportDownloadStatus = null;
		$scope.reportDownloadError = false;
		var url = "report/download/" + $scope.transactionId + "/" + $scope.reportName;
        $http.get(url).
        success(function(data, status, headers, config) {
        	$window.location.href = url;
        }).
        error(function(data, status, headers, config) {
        	$scope.reportDownloadError = true;
        	$scope.reportDownloadStatus = data;
        });
	};
	
	$scope.cancel = function() {
		if(testVersion.source=="dashBoardTestRunData"){
			$location.path("/dashboard");
		}else{
			$location.path("version/umgVersionView");
		}
	};
	$scope.test = function() {
		$http(
				{
					method : 'POST',
					url : 'versiontest/markastested/'
							+ $scope.tidVersion.versionTestContainer.tidName
				}).success(function(data, status, headers, config) {
			deferred.resolve(data);
		}).error(function(data, status, headers, config) {
			deferred.reject("Error came with status code :" + status);
		});
	};
	$scope.validate_string = function(ele) {
		var dataType = ele.datatype.type;
		var minLen, maxLen, value, length;
		var isValid = true;
		if (dataType) {
			var mandatory = ele.mandatory;
			minLen = dataType.minlength;
			maxLen = dataType.maxlength;
			value = ele.value;
			length = ele.datatype.length;
			if (value) {
				var noOfQuotes = 0;
				if(value.match(/"/g) != null) {
					noOfQuotes = value.match(/"/g).length;
				}
				if (minLen && maxLen) {
					if (!(value.length <= minLen && value.length >= maxLen)) {
						isValid = false;
						ele.error = true;
						ele.errorMessage = 'String entered requires minuimum length of '
								+ minLen
								+ ' and maximum length of '
								+ maxLen
								+ ' charaters';
					}
				} else if (length && (value.length - noOfQuotes) > length) {
					isValid = false;
					ele.error = true;
					ele.errorMessage = 'Length should be <= '+length;
				}
			} else if (!value && mandatory) {
				isValid = false;
				ele.error = true;
				ele.errorMessage = 'Please enter data in '+dataType+' format';
			}

			if (isValid) {
				ele.error = false;
				ele.errorMessage = '';
			}
		}
		// var str = "^[A-Za-z0-9]{"+minlength+","+maxlength+"}$";
		// var regex = new RegExp(str);
		// isValid = regex.test(value);
	};

	$scope.validate_double = function(ele) {
		var isValid = true;
		var dataType = ele.datatype.type;
		var maxExclusive, maxInclusive, minExclusive, minInclusive, totalDigits, fractionDigits, value;
		var regex;
		var str = "^([-+]?)([0-9]+)(\[.][0-9]+)?$";
		var description = 'Please enter data in '+dataType+' format';
		if (dataType) {
			maxExclusive = dataType.maxExclusive;
			maxInclusive = dataType.maxInclusive;
			minExclusive = dataType.minExclusive;
			minInclusive = dataType.minInclusive;
			totalDigits = dataType.totalDigits;
			fractionDigits = dataType.fractionDigits;

			// to check if it contains proper decimal numbers - start
			regex = new RegExp(str);
			if (!regex.test(ele.value)) {
				ele.error = true;
				ele.errorMessage = description;
				return;
			}
			// end
			if (ele.value) {
				value = parseFloat(ele['value'].slice());
			}
			var mandatory = ele.mandatory;
			if (totalDigits && fractionDigits) {
				var wholeNo = totalDigits - fractionDigits;
				if (wholeNo > 0) {
					str = "^[-+]?([0-9]{0," + wholeNo + "})(\.[0-9]{0,"
							+ fractionDigits + "})?$";
				} else {
					str = "^[-+]?([0]{1})(\.[0-9]{0," + fractionDigits + "})?$";
				}
				description = description + ', Double(' + totalDigits + ', '
						+ fractionDigits + ')';
			}
			regex = new RegExp(str);
			if (ele.value) {
				if (maxInclusive && minInclusive && minInclusive <= value
						&& maxInclusive >= value) {
					// str = "[0-9]{0,20}.[0-9]{1,20}$";
					isValid = regex.test(value);
				} else if (maxExclusive && minExclusive && minExclusive < value
						&& maxExclusive > value) {
					// str = "[0-9]{0,20}.[0-9]{1,20}$";
					isValid = regex.test(value);
				} else {
					isValid = regex.test(value);
				}
			} else if (!value && mandatory) {
				isValid = false;
			}
			if (!isValid) {
				ele.error = true;
				ele.errorMessage = description;
			} else {
				ele.error = false;
				ele.errorMessage = '';
			}
		}
	};
	$scope.validate_bigdecimal = function(ele) {
        var isValid = true;
        var dataType = ele.datatype.type;
        var maxExclusive, maxInclusive, minExclusive, minInclusive, totalDigits, fractionDigits, value;
        var regex;
        var str = "^([-+]?)([0-9]+)(\[.][0-9]+)?$";
        var description = 'Please enter data in '+dataType+' format';
        if (dataType) {
            maxExclusive = dataType.maxExclusive;
            maxInclusive = dataType.maxInclusive;
            minExclusive = dataType.minExclusive;
            minInclusive = dataType.minInclusive;
            totalDigits = dataType.totalDigits;
            fractionDigits = dataType.fractionDigits;

            // to check if it contains proper decimal numbers - start
            regex = new RegExp(str);
            if (!regex.test(ele.value)) {
                ele.error = true;
                ele.errorMessage = description;
                return;
            }
            // end
            if (ele.value) {
                value = parseFloat(ele['value'].slice());
            }
            var mandatory = ele.mandatory;
            if (totalDigits && fractionDigits) {
                var wholeNo = totalDigits - fractionDigits;
                if (wholeNo > 0) {
                    str = "^[-+]?([0-9]{0," + wholeNo + "})(\.[0-9]{0,"
                            + fractionDigits + "})?$";
                } else {
                    str = "^[-+]?([0]{1})(\.[0-9]{0," + fractionDigits + "})?$";
                }
                description = description + ', Double(' + totalDigits + ', '
                        + fractionDigits + ')';
            }
            regex = new RegExp(str);
            if (ele.value) {
                if (maxInclusive && minInclusive && minInclusive <= value
                        && maxInclusive >= value) {
                    // str = "[0-9]{0,20}.[0-9]{1,20}$";
                    isValid = regex.test(value);
                } else if (maxExclusive && minExclusive && minExclusive < value
                        && maxExclusive > value) {
                    // str = "[0-9]{0,20}.[0-9]{1,20}$";
                    isValid = regex.test(value);
                } else {
                    isValid = regex.test(value);
                }
            } else if (!value && mandatory) {
                isValid = false;
            }
            if (!isValid) {
                ele.error = true;
                ele.errorMessage = description;
            } else {
                ele.error = false;
                ele.errorMessage = '';
            }
        }
    };
	$scope.validate_integer = function(ele) {
		var dataType = ele.datatype.type;
		var maxExclusive, maxInclusive, minExclusive, minInclusive, totalDigits, value;
		var str;
		var isValid = true;
		var description;
		if (dataType) {
			var mandatory = ele.mandatory;
			maxExclusive = dataType.maxExclusive;
			maxInclusive = dataType.maxInclusive;
			minExclusive = dataType.minExclusive;
			minInclusive = dataType.minInclusive;
			totalDigits = dataType.totalDigits;
			value = ele.value;
			if (totalDigits) {
				str = "^-?[0-9]{0," + totalDigits + "}$";
				description = 'Please enter Integer in correct format with atmost of '
						+ totalDigits + ' digits';
			} else {
				str = "^-?[0-9]+$";
				description = 'Please enter data in '+dataType+' format';
			}

			var regex = new RegExp(str);
			if (value) {
				if (value && maxInclusive && minInclusive
						&& minInclusive <= value && maxInclusive >= value) {
					isValid = regex.test(value);
				} else if (value && maxExclusive && minExclusive
						&& minExclusive < value && maxExclusive > value) {
					isValid = regex.test(value);
				} else {
					isValid = regex.test(value);
				}
				if (isValid) {
					isValid = $scope.validate_value(ele);
					if(!isValid) {
						description = 'Data outside '+dataType+' range.';
					}
				}
			} else if (!value && mandatory) {
				isValid = false;
			}

			if (!isValid) {
				ele.error = true;
				ele.errorMessage = description;
			} else {
				ele.error = false;
				ele.errorMessage = '';
			}
		}
	};
	$scope.validate_long = function(ele) {
	    $scope.validate_integer(ele);
	};
	$scope.validate_biginteger = function(ele) {
        var dataType = ele.datatype.type;
        var maxExclusive, maxInclusive, minExclusive, minInclusive, totalDigits, value;
        var str;
        var isValid = true;
        var description;
        if (dataType) {
            var mandatory = ele.mandatory;
            maxExclusive = dataType.maxExclusive;
            maxInclusive = dataType.maxInclusive;
            minExclusive = dataType.minExclusive;
            minInclusive = dataType.minInclusive;
            totalDigits = dataType.totalDigits;
            value = ele.value;
            if (totalDigits) {
                str = "^-?[0-9]{0," + totalDigits + "}$";
                description = 'Please enter Integer in correct format with atmost of '
                        + totalDigits + ' digits';
            } else {
                str = "^-?[0-9]+$";
                description = 'Please enter data in '+dataType+' format';
            }

            var regex = new RegExp(str);
            if (value) {
                if (value && maxInclusive && minInclusive
                        && minInclusive <= value && maxInclusive >= value) {
                    isValid = regex.test(value);
                } else if (value && maxExclusive && minExclusive
                        && minExclusive < value && maxExclusive > value) {
                    isValid = regex.test(value);
                } else {
                    isValid = regex.test(value);
                }
                if (isValid) {
					isValid = $scope.validate_value(ele);
					if(!isValid) {
						description = 'Data outside '+dataType+' range.';
					}
				}
            } else if (!value && mandatory) {
                isValid = false;
            }

            if (!isValid) {
                ele.error = true;
                ele.errorMessage = description;
            } else {
                ele.error = false;
                ele.errorMessage = '';
            }
        }
    };
    
    $scope.validate_value = function(ele) {
    	var dataType = ele.datatype.type;
		var value = ele.value;
    	if (dataType.toUpperCase() == "INTEGER" && (value > 2147483647  || value < -2147483648)) { // as per  Integer.MAX_VALUE of java
    		return false;
    	} else if (dataType.toUpperCase() == "LONG" && (value > 9223372036854775807 || value < -9223372036854775808)) { // as per  Long.MAX_VALUE of java
    		return false;
    	} else if (value.length > 19) {
    		return false;
    	}
    	return true;
    };
    
	$scope.validate_boolean = function(ele) {

	};

	$scope.validate_array = function(ele) {
		var mandatory = ele.mandatory;
		var dataType = ele.datatype.type;
		var inputArrayDimensions = ele['datatype']['dimensions'].slice();

		// check if entry is there in case if mandatory
		if (mandatory && !ele.value) {
			ele.error = true;
			ele.errorMessage = 'Mandatory';
			return false;
		}
	
		// clearing error messages, had they been set earlier
		ele.error = false;
		ele.errorMessage = '';
		
		// parsing user input
		try {
			var parsedInputArray = JSON.parse(ele.value);
		} catch (err) {
			ele.error = true;
			ele.errorMessage = 'Mal-formed data array';
			return false;
		}
		//fixing for bug UMG-1428 
		if (!(parsedInputArray instanceof Array)){
			ele.error = true;
			ele.errorMessage = 'Input is not of type array';
			return false;
		}
		
		//check if input array dimensions are equal to the prescribed array dimensions
		var i;
		for (i=0;i<ele.value.length;i++) { 
			if (ele.value[i]!='['){
				break;
			} 
		}
		
		if ((inputArrayDimensions[0]!=-1) && (i!=inputArrayDimensions[0])){
			ele.error = true;
			ele.errorMessage ="Please enter data in "+inputArrayDimensions[0]+ "D Array format";
			return false;
		} else if ((inputArrayDimensions[0]==-1) && (i!=inputArrayDimensions.length)){
			ele.error = true;
			ele.errorMessage ="Please enter data in "+ inputArrayDimensions.length +"D Array format";
			return false;
		}
		
		//fixing for bug UMG-1439 
		if (parsedInputArray.length == 0){
			ele.error = true;
			ele.errorMessage = 'Array cannot be empty please enter some values';
			return false;
		}
		
		// clearing error messages, had they been set earlier
		ele.error = false;
		ele.errorMessage = '';

		// flipping rows and columns
		/*if (inputArrayDimensions.length >= 2) {
			var x = inputArrayDimensions[0];
			inputArrayDimensions[0] = inputArrayDimensions[1];
			inputArrayDimensions[1] = x;
		}*/


		//inputArrayDimensions = inputArrayDimensions.reverse();
		var inputArrayIsOfDimension = inputArrayDimensions[0];
		var currentArrayLength = inputArrayDimensions[0];
		var isValid = true;
		/*var isValid = validateArray(parsedInputArray, inputArrayIsOfDimension,
				currentArrayLength, inputArrayDimensions, 0);*/
		if (mandatory && !isValid) {
			ele.error = true;
			ele.errorMessage = 'Mandatory input array is incorrect';
		} else {
			ele.error = false;
			ele.errorMessage = '';
			ele.arrayValue = parsedInputArray;
		}
	}
	
	$scope.validate_nonArray = function(ele) {
		try {
			var parsedInput = JSON.parse(ele.value);
		} catch (err) {
			// Nothing to do
		}
		//fixing for bug UMG-1428 
		if (parsedInput instanceof Array){
			ele.error = true;
			ele.errorMessage = 'Please enter data in Non Array format';
			return false;
		} else {
			$scope[ele.validationMethod](ele);
		}
	}

	function validateArray(passedArray, arrayDimension, currentArrayLength,
			inputArrayDimensions, dimIndex) {
		var status = true;
		// length check
		if (passedArray == null || passedArray.length != currentArrayLength) {
			return false;
		}
		// start validating once you have drilled down to the last dimension
		if (arrayDimension == 1) {
			return validateOneDimArray(passedArray, currentArrayLength);
		} else {
			// if you've not reached the single dimension yet, keep recursing
			for (var i = 0; i < currentArrayLength; i++) {
				if (!(passedArray[i] instanceof Array)) {
					return false;
				}
				// AND'ing status to determine final validation outcome
				status = status
						&& validateArray(passedArray[i], arrayDimension - 1,
								inputArrayDimensions[dimIndex + 1],
								inputArrayDimensions, dimIndex + 1);
			}
		}
		return status;
	}

	function validateOneDimArray(arr, length) {
		var oneDimStatus = true;
		for (var j = 0; j < length; j++) {
			oneDimStatus = oneDimStatus && true; /* validateArrayElement(arr[j]); */
		}
		return oneDimStatus;
	}

	$scope.validate_date = function(ele) {
		var value = ele.value;
		var mandatory = ele.mandatory;
		if (!value && mandatory) {
			ele.error = true;
			ele.errorMessage = 'Please select a date';
		} else {
			ele.error = false;
			ele.errorMessage = '';
		}
	}

	$scope.validateAsOnDate = function() {
		$scope.hasError = false;
		if (!$scope.tidVersion.versionTestContainer.asOnDate) {
			$scope.isAsOnDateValid = false;
			$scope.asOnDateErrorMessage = 'Please enter As On Date';
		} else {
			$scope.isAsOnDateValid = true;
			$scope.asOnDateErrorMessage = '';
		}
	};
	
	
	$scope.toggleOutput = function() {
		if ($scope.showOutput) {
			$("#testInputOutputContainer").removeClass();
			$("#testInputOutputContainer").addClass('col-sm-10');
			$("#testInputOutputContainer").css('width','87.333333%');
			$("#testbedInput").children().addClass('col-lg-4');
			$("#testbedOutput").removeClass();
			$("#testbedOutput").hide();
			$("#testbedhiddenoutput").show();
			$("#testBedColpButtn").empty();
			$("#testBedColpButtn").append('<img src="resources/images/arrow-left.png" style="width: 120%;">');
			//$("#testBedColpButtn").addClass('fa fa-fw fa-chevron-left');
			$scope.showOutput = false;
		} else {
			$("#testInputOutputContainer").css('width','');
			$("#testInputOutputContainer").removeClass();
			$("#testInputOutputContainer").addClass('col-sm-7');
			$("#expander").addClass('col-xs-1');
			$("#testbedInput").children().removeClass();
			$("#testbedInput").children().addClass('col-sm-6');
			$("#testbedhiddenoutput").hide();
			$("#testbedOutput").removeClass();
			$("#testbedOutput").addClass('col-sm-4');
			$("#testbedOutput").show();
			$("#testBedColpButtn").empty();
			$("#testBedColpButtn").append('<img src="resources/images/arrow-right.png" style="width: 120%;">');
			//$("#testBedColpButtn").addClass('fa fa-fw fa-chevron-right');
			$scope.showOutput = true;
		}
	};
	
	$scope.removeOutput = function() {
			$("#testInputOutputContainer").removeClass();
			$("#testbedInput").children().removeClass();
			$("#testbedOutput").removeClass();
			$("#testbedOutput").hide();
			$("#testbedhiddenoutput").hide();
			$("#testBedColpButtn").empty();
	};
	/**==============================================
	 * uploading test run file and receives the data as JSON object
	 */	
	$scope.downloadTestRunFile = function() {
		if($scope.downloadRunFile==undefined || $scope.downloadRunFile=={} ){
			$scope.error=true;
			 $scope.messageForTstRunFile="Please select a file.....";
			 $scope.showMessageForTstRunFile = true;
		 }else{
		if($scope.downloadRunFile.name.split('.').pop()=='txt'){			 
			 $scope.additionalPropsList="";
			 $scope.defaultValuesList="";			 
			 $scope.error=false;
			 $scope.errorCode="";
			 $scope.message="";
			 $scope.messageForTstRunFile = '';
				$scope.date = {};
				$scope.datetime = {};
				$scope.date.placeholder = 'yyyy-MM-dd';
				$scope.date.min = '1000-01-01';
				$scope.date.max = '9999-12-31';
				$scope.datetime.placeholder = 'yyyy-MM-ddTHH:mm';
				$scope.datetime.min = '1000-01-01T00:00';
				$scope.datetime.max = '9999-12-31T23:59';
				$scope.tidVersion = {};
				$scope.tidVersion.finalList = {};
				$scope.outputs = '';
				$scope.hasError = false;
				$scope.isExecuted = false;
				$scope.isExecutedSucessfully = false;
				$scope.tidVersion.versionTestContainer = {};				
				$scope.showOutput = false;
				$scope.showDashBrdInfo = false;
				$scope.removeOutput();
		testBedService.downloadTestRunFile($scope.downloadRunFile).then(  
		 function(responseData) {
				if (!responseData.error) {
					$scope.showDashBrdInfo = true;
				$scope.tidVersion.versionTestContainer.hasReportTemplate=responseData.response.hasReportTemplate;
				$scope.tidVersion.versionTestContainer.asOnDate=responseData.response.asOnDate;
				$scope.tidVersion.versionTestContainer.tidName = responseData.response.tidName;
				$scope.tidVersion.versionTestContainer.modelName = responseData.response.modelName;
				$scope.tidVersion.versionTestContainer.majorVersion = responseData.response.majorVersion;
				$scope.tidVersion.versionTestContainer.minorVersion = responseData.response.minorVersion;
				$scope.tidVersion.versionTestContainer.versionId = responseData.response.versionId;
				$scope.tidVersion.versionTestContainer.tidIoDefinitions = responseData.response.tidIoDefinitions;
				if(responseData.response.additionalPropsList.length>0){
					$scope.additionalPropsList=responseData.response.additionalPropsList.toString().replace(/,/g,',  ');
					$scope.messageForTstRunFile='<div>  <a style="cursor:pointer;" ng-click="testrunWarningUnmatched()">'+ responseData.response.additionalPropsList.length+"</a> extra field(s) are present in Tenant Txn Input, but not in test-bed TID.";												
				}
				if(responseData.response.defaultValuesList.length>0){	
					$scope.defaultValuesList=responseData.response.defaultValuesList.toString().replace(/,/g,',  ');
					$scope.messageForTstRunFile=$scope.messageForTstRunFile+ '<p><a style="cursor:pointer;" ng-click="testrunWarningDefault()">' +responseData.response.defaultValuesList.length+"</a> extra field(s) are present in test-bed TID, but not in Tenant Txn Input. </div> </p>";
				}			
			
				prepareTidInput(responseData.response.tidIoDefinitions);
				$scope.messageForTstRunFile=$scope.messageForTstRunFile+responseData.message;
				$scope.showMessageForTstRunFile = true;
			}else{	
				$scope.error = responseData.error;				
				$scope.messageForTstRunFile=responseData.errorCode+ " " + responseData.message;
				$scope.showMessageForTstRunFile = true;
				//$scope.toggleOutput();
			}
			} 
		 );
		 }else{
			 $scope.error=true;
			 $scope.messageForTstRunFile="Please Upload a text file";		
			 $scope.showMessageForTstRunFile = true;
		 }
		 }
	};
	
	/**==============================================
	 * Tips/Hint
	 */	
	$scope.showTips = function(value){
		if(value==null)
			$dialogs.notify('Tips','Jar will not be imported if its name and corresponding checksum value matches with any of the existing jars in the system. Existing Jar will be used to continue with import process.');
		else
			$dialogs.notify('Tips',value);
	};
	
	
	
	$scope.initSetup();
};
