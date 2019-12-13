'use strict';

var ModelPublishingCtrl = ['$scope', '$log', '$location', '$timeout', 'mps', 'sharedPropertiesService', 'tidListDisplayService', '$dialogs', function($scope, $log, $location, $timeout, mps, sharedPropertiesService, tidListDisplayService, $dialogs){
	
	
	$scope.smp = {id : '', name : '', description : '', versionType : 'MAJOR', majorVersion : '', minorVersion : '', status : '', versionDescription : '', publishedOn : '', publishedBy : '', requestedOn: '', requestedBy: '', deactivatedOn : '', deactivatedBy : '', hasReportTemplate : false, reportTemplateName : '', jarName : '', manifestName : '',
				   	 mapping : {id : '', name : '', description : '', active : false , modelName : '', umgName : '', status : '',
					   model : {id : '', name : '', description : '', umgName : '', ioDefinitionName : '', ioDefExcelName : '', documentationName : '', xml: '', excel: '', allowNull : true}
				   	 },
					 modelLibrary : {id : '', name : '', executionLanguage: 'Matlab-7.16', executionType: 'Internal', description : '', umgName: '', jarName: '', rmanifestFileName: '', checksum : '', jar: '', manifestFile: '',
						 			modelExecEnvName : '', execEnv : ''}
	};
	
	$scope.modelType = 'NEW';
	$scope.modelDefType = 'NEW';
	$scope.showSuccess = false;
	$scope.showErr = false;
	$scope.messages = [];
	$scope.existingVersions = [];
	$scope.versionSaved = false;
	$scope.versionFailed = false;
	$scope.versionSubmitted=false;
	$scope.execversion ='';
	$scope.execLanguage='';
	// Variable to indicate version  is published or deactivated
	$scope.publishedOrDeactivated = false;
	/** Below code will close model if someone press backspace */
	
	$scope.$on('$stateChangeStart', function (event) {
		$('#newRModal').modal('hide');
		$('#libModal').modal('hide');
		$('#defModal').modal('hide');
    });
	
	$scope.modelError={errorMsg : {},step:'testVersion',errorCode:'MODELEXCEPTION'};
	
	var addModelAPI = null;
	
	var setModelAPI = function(versionId){
		$log.info('Setting Model API ...'+versionId);
		mps.getModeAPI(versionId).then(
			function(responseData){
				$scope.smp = responseData.response;
			},
			function(errorData){
				$log.error('Error in Fetching Data for version id : '+versionId);
			}
		);
    };
	
    var setModelAPIForTestAndMapping = function(versionId){
    	$log.info('Setting Model API For Testing And Mapping ...'+versionId);
		mps.getModeAPI(versionId).then(
			function(responseData){
				var info = responseData.response;
				$scope.smp.id = info.id;
				$scope.smp.name = info.name;
				$scope.smp.description = info.description;
				$scope.smp.versionDescription = info.versionDescription;
				$scope.smp.majorVersion = info.majorVersion;
				$scope.smp.minorVersion = info.minorVersion;
				$scope.smp.status = info.status;
				$scope.smp.mapping.id = info.mapping.id;
				$scope.smp.mapping.name = info.mapping.name;
				$scope.smp.mapping.model.id = info.mapping.model.id;
				$scope.smp.mapping.model.name = info.mapping.model.name;
				$scope.smp.modelLibrary.id = info.modelLibrary.id;
				$scope.smp.modelLibrary.name = info.modelLibrary.name;
				$scope.smp.modelLibrary.modelExecEnvName = info.modelLibrary.modelExecEnvName;
			},
			function(errorData){
				$log.error('Error in Fetching Data for version id : '+versionId);
			}
		);
    };
    
	var viewModelApi = null;
	
	viewModelApi = sharedPropertiesService.get("viewModelApi");
    if(viewModelApi != null){
    	setModelAPI(viewModelApi);
        sharedPropertiesService.remove("viewModelApi");
    }
	
	/** Method to Save Version Data */
	
	$scope.saveVersion = function(){
		$log.info('Request received to save version');
		$scope.versionSaved = false; $scope.versionFailed = false;
		$scope.smp.mapping.model.name = $scope.smp.name;
		$scope.smp.mapping.model.description = $scope.smp.description;
		$scope.smp.modelLibrary.name = $scope.smp.name;
		$scope.smp.modelLibrary.description = $scope.smp.description;
		$scope.smp.modelLibrary.modelExecEnvName = $scope.smp.modelLibrary.executionLanguage;
		
		$scope.showSuccess = false;
		$scope.showErr = false;
		$scope.messages = [];
		
		$scope.errorMessages = [];
		$scope.txnId = '';
		
		if(validate($scope.smp))
		mps.saveVersion($scope.smp,$scope.modelType,$scope.modelDefType).then(
				function(responseData){
					if(responseData.response.success){
						$log.info('Version Saved Successfully.');
						$scope.expandOutput();
						setModelAPIForTestAndMapping(responseData.response.versionId);
						$scope.txnId = responseData.response.transactionId;
						$scope.versionSaved = true;
					}else{
						$log.error('Failed to Save Version.');
						$scope.expandOutput();				
						if(responseData.response.modelExceptions!=null){
							$scope.modelError.errorMsg = responseData.response.modelExceptions;
							if(responseData.response.modelExceptions.header!=null){
							$scope.modelError.errorCode = responseData.response.modelExceptions.header.errorCode;
							}else if(responseData.response.modelExceptions.responseHeaderInfo!=null){
								$scope.modelError.errorCode = responseData.response.modelExceptions.responseHeaderInfo.errorCode;
								
							}
							$scope.errorMessages.push($scope.modelError);
						
						}else{
							$scope.errorMessages = responseData.response.errors;
						}
						$scope.txnId = responseData.response.transactionId;
						$scope.versionFailed = true;
					}
					$scope.versionSubmitted = true;
				},
				function(errorData){
					$log.error('Internal Server Error.'+errorData);
					$scope.expandOutput();
					$scope.modelError = {};
					$scope.modelError.step="globalError";
					if(errorData != null && errorData.errorCode != null){
						$scope.modelError.errorCode=errorData.errorCode;
						$scope.modelError.errorMsg=errorData.message;					
					}else{						
						$scope.modelError.errorMsg = errorData;
					}					
					$scope.errorMessages.push($scope.modelError);		
					
					$scope.showErr = true;
					$scope.versionSubmitted = true;
				}
		);
		else
			$scope.expandOutput();
			$scope.smp.modelLibrary.executionLanguage=$scope.smp.modelLibrary.modelExecEnvName ;
	};
	
	var setExistingVersions = function(){
		$log.info('Setting Existing Versions for '+$scope.smp.name);
		mps.getExistingVersions($scope.smp.name).then(
				function(responseData){
					$scope.existingVersions = responseData.response;
				},
				function(errorData){
					$log.error('Failed to Set Existing Versions.');
				}
		);
	};
	
	/** Method to Roll-Back Version Data */
	
	$scope.rollback = function(){
		$log.info('Request received to rollback version');
	};
	
	
	
	/** Auto-Suggest Operations*/
	
	var setTenantModelNames = function(){
		$scope.tenantModelNames = [];
		mps.getAllTenantModelNames().then(
				function(responseData){
					angular.forEach(responseData.response, function(tmn){
						$scope.tenantModelNames.push(tmn);
					});
				},
				function(errorData){
					$log.error(errorData.message);
				}
		);
	};
	
	setTenantModelNames();
	
	/** Method to Set Description */
	
	$scope.setTenantModelDesc = function(tenantModelName){
		$log.info('Setting API Desc for '+tenantModelName);
		$scope.verDescExist = false;
		$scope.smp.description = '';
		if(!(tenantModelName == null || tenantModelName ==''))
		{	
		 mps.getTenantModelDesc(tenantModelName).then(
				function(responseData){
					if(responseData.response != null)
					{	$scope.smp.description = responseData.response;
						$scope.verDescExist = true;
					}
				},
				function(errorData){
					$log.error('Failed to Set Description.');
				}
		 );
		}
	};
	
	var setModelApiDetails = function(){
		$log.info('Setting Model API Details ...');
		mps.getModelApiDetails($scope.smp.modelLibrary.executionLanguage).then(
				function(responseData){
					if(!responseData.error){
						$scope.modelApiDetails = buildModelApiDetails(responseData.response);
					}
				},
				function(errorData){
					$scope.modelApiDetails = [];
					$log.error('Error');
				}
		); 
	};
	
	
	$scope.pickedLib = [];
	
	$scope.modelLibGrid = {
	        data: 'modelApiDetails',
	        enableRowSelection: true,
	        multiSelect: false,
	        selectedItems: $scope.pickedLib,
	        afterSelectionChange : function (rowItem, event){
	        	$scope.smp.modelLibrary.id = rowItem.entity.modelLibId;
	        	$scope.smp.modelLibrary.jarName = rowItem.entity.jarName;
	        	$scope.smp.modelLibrary.rmanifestFileName = rowItem.entity.manifestFileName;
	        },
	        columnDefs: [{field:'name', displayName:'Model API Name'}, 
	                     {field:'version', displayName:'Version'},
	                     {field:'jarName', displayName:'Library Name'},
	                     {field:'status', displayName:'Status'},
	                     {field:'createdBY', displayName:'Created By'},
	                     {field:'createdDate', displayName:'Created Date'}
	                     ]
	    };
	
	$scope.launchLibDailog = function(){
		$scope.dialogType = 'LIB';
		$timeout(function(){
			$scope.modelLibGrid.$gridServices.DomUtilityService.RebuildGrid(
                    $scope.modelLibGrid.$gridScope,
                    $scope.modelLibGrid.ngGrid
                );
		},200);
	};
	
	$scope.pickedDef = [];
	
	$scope.modelDefGrid = {
	        data: 'modelApiDetails',
	        enableRowSelection: true,
	        multiSelect: false,
	        selectedItems: $scope.pickedDef,
	        afterSelectionChange : function (rowItem, event){
	        	$scope.smp.mapping.model.id = rowItem.entity.modelId;
	        	$scope.smp.mapping.model.ioDefinitionName = rowItem.entity.ioDefinitionName;
	        },
	        columnDefs: [{field:'name', displayName:'Model API Name'}, 
	                     {field:'version', displayName:'Version'},
	                     {field:'ioDefinitionName', displayName:'IO Definition'},
	                     {field:'status', displayName:'Status'},
	                     {field:'createdBY', displayName:'Created By'},
	                     {field:'createdDate', displayName:'Created Date'}
	                     ]
	    };
	
	$scope.launchDefDailog = function(){
		$scope.dialogType = 'DEF';
		$timeout(function(){
			$scope.modelDefGrid.$gridServices.DomUtilityService.RebuildGrid(
                    $scope.modelDefGrid.$gridScope,
                    $scope.modelDefGrid.ngGrid
                );
		},200);
	};
	
	$scope.newRLibDetails = [];
	
	var setNewRLibraries = function(){
		$log.info('Setting new R Libraries.');
		$scope.newRLibDetails  = [];
		mps.getNewRLibraries($scope.smp.modelLibrary.executionLanguage).then(
				function(responseData){
					if(!responseData.error && responseData.response != null){
						$scope.newRLibDetails = responseData.response;
					}
				},
				function(errorData){
					$scope.newRLibDetails  = [];
					$log.error('Error');
				}
		); 
	};
	
	$scope.setFilteredRLibraries = function(searchText){
		
		$scope.pageInfo = {
				searchText : searchText,
				fromDate : '',
				toDate : '',
				pageSize: 50,
				page: 0,
				sortColumn : 'createdDate',
				descending : true
		};
		
		$log.info('Setting Filtered New R Libraries ...');
		$scope.newRLibDetails  = [];
		mps.getFilteredRLibraries($scope.pageInfo, $scope.smp.modelLibrary.executionLanguage).then(
				function(responseData){
					if(!responseData.error && responseData.response != null){
						$scope.newRLibDetails = responseData.response;
					}
					$('#searchRLib').focus();
				},
				function(errorData){
					$scope.newRLibDetails  = [];
					$log.error('Error');
				}
		);
	};
	
	$scope.pickedRLib = [];
	
	$scope.rModelLibGrid = {
	        data: 'newRLibDetails',
	        enableRowSelection: true,
	        multiSelect: false,
	        selectedItems: $scope.pickedRLib,
	        afterSelectionChange : function (rowItem, event){
	        	$scope.smp.modelLibrary.id = rowItem.entity.id;
	        	$scope.smp.modelLibrary.jarName = rowItem.entity.tarName;
	        	$scope.smp.modelLibrary.checksum=rowItem.entity.checksum;
	        	$scope.smp.modelLibrary.encodingType=rowItem.entity.encodingType;	        	
	        },
	        columnDefs: [{field:'tarName', displayName:'Tar Name', width: '400px'},
	                     {field:'createdDate', displayName:'Added On', cellTemplate: '<div class="ngCellText">{{row.getProperty(col.field)  | date:"yyyy-MM-dd HH:mm"}}</div>'},
	                     {field:'createdBy', displayName:'Added By'}]
	    };
	
	$scope.launchRLibDailog = function(){
		$scope.dialogType = 'R_LIB';
		$timeout(function(){
			$scope.rModelLibGrid.$gridServices.DomUtilityService.RebuildGrid(
                    $scope.rModelLibGrid.$gridScope,
                    $scope.rModelLibGrid.ngGrid
                );
		},200);
	};
	
	$scope.setFilteredLibraries = function(searchText){
		
		$scope.pageInfo = {
				searchText : searchText,
				fromDate : '',
				toDate : '',
				pageSize: 50,
				page: 0,
				sortColumn : 'createdDate',
				descending : true
		};
		
		$log.info('Setting Filtered Libraries ...');
		mps.getFilteredLibraries($scope.pageInfo, $scope.smp.modelLibrary.executionLanguage).then(
				function(responseData){
					if(!responseData.error){
						$scope.modelApiDetails = buildModelApiDetails(responseData.response);
						$('#searchLib').focus();
					}
				},
				function(errorData){
					$scope.modelApiDetails = [];
					$log.error('Error');
				}
		);
	};
	
	$scope.setFilteredDefinitions = function(searchText){
		
		$scope.pageInfo = {
				searchText : searchText,
				fromDate : '',
				toDate : '',
				pageSize: 50,
				page: 0,
				sortColumn : 'createdDate',
				descending : true
		};
		
		$log.info('Setting Filtered Definitions ...');
		mps.getFilteredDefinitions($scope.pageInfo, $scope.smp.modelLibrary.executionLanguage).then(
				function(responseData){
					if(!responseData.error){
						$scope.modelApiDetails = buildModelApiDetails(responseData.response);
						$('#searchDef').focus();
					}
				},
				function(errorData){
					$scope.modelApiDetails = [];
					$log.error('Error');
				}
		);
	};
	
	var buildModelApiDetails = function(response){
		
		var modelAPIDetails = [];
		
		angular.forEach(response, function(smp){
			
			var model_id = ''; var definition_name = ''; var model_lib_id = ''; var jar_name = ''; var manifest_file_name = '';
			
			if(smp.mapping != null){
			model_id = smp.mapping.model.id;
			definition_name = smp.mapping.model.ioDefinitionName;}
			
			if(smp.modelLibrary != null){
			model_lib_id = smp.modelLibrary.id;
			jar_name = smp.modelLibrary.jarName;
			manifest_file_name = smp.modelLibrary.rmanifestFileName;}
			
			var mad = {
					id : '',
					name : smp.name,
					version: smp.majorVersion+'.'+smp.minorVersion,
					status : smp.status,
					modelId : model_id,
					ioDefinitionName : definition_name,
					modelLibId : model_lib_id,
					jarName : jar_name,
					manifestFileName : manifest_file_name,
					createdBY : smp.createdBy,
					createdDate : smp.createdDateTime
			};
			modelAPIDetails.push(mad);
		});
		
		return modelAPIDetails;
	};
	
	
	/** Watch Operations */
	
	$scope.$watch('smp.versionType', function (newVal, oldVal) {
		if(newVal == 'MINOR')
			setExistingVersions();
	},true);
	
	$scope.$watch('modelType', function (newVal, oldVal) {
		if(newVal == 'OLD')
			setModelApiDetails();
		else
			$scope.modelApiDetails = [];
		
		if(newVal == 'NEW' && $scope.smp.modelLibrary.executionLanguage.indexOf('R') != -1){
			setNewRLibraries();
		}
		
		$scope.smp.modelLibrary.manifestFile = '';
		$scope.smp.modelLibrary.jar = '';
		$scope.smp.modelLibrary.jarName = '';
		$scope.smp.modelLibrary.rmanifestFileName = '';
		
	},true);
	
	$scope.$watch('modelDefType', function (newVal, oldVal) {
		if(newVal == 'OLD')
			setModelApiDetails();
		else
			$scope.modelApiDetails = [];
	},true);
	
	$scope.$watch('srchDefTxt', function (newVal, oldVal) {
		if(newVal == '')
			setModelApiDetails();
	},true);
	
	$scope.$watch('srchLibTxt', function (newVal, oldVal) {
		if(newVal == '')
			setModelApiDetails();
	},true);
	
	$scope.$watch('srchRLib', function (newVal, oldVal) {
		if(newVal == '')
			setNewRLibraries();
	},true);
	
	$scope.$watch('smp.modelLibrary.executionLanguage', function (newVal, oldVal) {
		if($scope.modelType == 'OLD' || $scope.modelDefType == 'OLD'){
			setModelApiDetails();
		}
		
		if($scope.modelType == 'NEW' && newVal.indexOf('R') != -1){
			setNewRLibraries();
		}
		
		$scope.smp.modelLibrary.manifestFile = '';
		$scope.smp.modelLibrary.jar = '';
		$scope.smp.modelLibrary.jarName = '';
		//$scope.smp.modelLibrary.rmanifestFileName = '';
		
	},true);
    
    /** Validation Operations */
    
    var validate = function(smp){
    	$scope.showSuccess = false;
    	$scope.showErr = false;
    	$scope.messages = [];
    	
    	if(smp.versionType == 'MINOR' && smp.majorVersion == ''){
    		$scope.messages.push('Version is Not Selected');
		}
    	
    	if($scope.modelType == 'NEW'){
    		if(smp.modelLibrary.executionLanguage.indexOf('Matlab') != -1){
    			if(angular.isUndefined(smp.modelLibrary.jar) || smp.modelLibrary.jar == ''){
        			var msg = 'Please upload .jar file';
            		$scope.messages.push(msg);
        		}else{
        			var index = smp.modelLibrary.jar.name.lastIndexOf('.');
            		var extension = smp.modelLibrary.jar.name.substring(index);
            		if(extension.toLowerCase() != '.jar'){
            			$scope.messages.push('Invalid file type for library, only jar file allowed.');
            		}
            		
            		if(angular.isUndefined(smp.modelLibrary.checksum) || smp.modelLibrary.checksum == ''){
                		$scope.messages.push('Checksum is mandatory');
            		}
        		}
    		}else{
    			if(angular.isUndefined(smp.modelLibrary.jar) || smp.modelLibrary.jar == ''){
            		$scope.messages.push('Please choose R library');
        		}else {
        			       			
            		var tokns = smp.modelLibrary.jar.name.split('.').reverse();
            		var gzExtn;
            		var tarExtn;
            		if(tokns.length > 2){
            			gzExtn = tokns[0];
            			tarExtn = tokns[1];
	            		if(gzExtn.toLowerCase() != 'gz' || tarExtn.toLowerCase() != 'tar'){
	            			$scope.messages.push('Invalid file type for library, only tar.gz file allowed.');
	            		}
        			}else{
        				$scope.messages.push('Invalid file type for library, only tar.gz file allowed.');
        			}
            		
            		if(angular.isUndefined(smp.modelLibrary.checksum) || smp.modelLibrary.checksum == ''){
                		$scope.messages.push('Checksum is mandatory');
            		}
        		}
    			
    			if(!angular.isUndefined(smp.modelLibrary.manifestFile) && smp.modelLibrary.manifestFile != ''){
    				var index = smp.modelLibrary.manifestFile.name.lastIndexOf('.');
            		var extension = smp.modelLibrary.manifestFile.name.substring(index);
            		if(extension.toLowerCase() != '.csv'){
            			$scope.messages.push('Invalid File Type for Manifest, Only .csv File Allowed.');
            		}
    			}
    		}
    	}else{
    		if(angular.isUndefined(smp.modelLibrary.jarName) || smp.modelLibrary.jarName == ''){
        		$scope.messages.push('Please Choose Library');
    		}
    	}
    	
    	if($scope.modelDefType == 'NEW'){
    		
    		if(angular.isUndefined(smp.mapping.model.excel) || smp.mapping.model.excel == ''){
        		$scope.messages.push('Please Upload Definition');
    		}else{
    			var index = smp.mapping.model.excel.name.lastIndexOf('.');
        		var extension = smp.mapping.model.excel.name.substring(index);
        		if(!(extension.toLowerCase() == '.xlsx' || extension.toLowerCase() == '.xml')){
        			$scope.messages.push('Invalid File Type for Definition, only .xlsx or .xml allowed');
        		}
    		}
    		
    		if(angular.isUndefined(smp.mapping.model.documentationName) || smp.mapping.model.documentationName == ''){
        		$scope.messages.push('Please Upload Document');
    		}
    		
    	}else{
    		if(angular.isUndefined(smp.mapping.model.ioDefinitionName) || smp.mapping.model.ioDefinitionName == ''){
        		$scope.messages.push('Please Choose Definition');
    		}
    	}
    	
    	if($scope.messages.length > 0){
    		$log.error('Validation Failed !');
    		$scope.showErr = true;
    		return false;
    	}
    		
    return true;
    };
    
    
    /** This method will redirect to TestBed. */
    $scope.testModelAPI = function(){
        $log.info("Redirecting to TestBed ...");
        var info = $scope.smp;
        var testVersion = {"versionId": info.id, "tidName": info.mapping.name,"modelName":info.name,"majorVersion":info.majorVersion,"minorVersion":info.minorVersion,"hasReportTemplate":info.hasReportTemplate};
        sharedPropertiesService.put("testVersion",testVersion);
        $location.path('version/testbed');
     };
     
     /** This method will redirect to mapping page */
     
     $scope.editMapping = function(apiName,majorVersion,minorVersion) {    	 
    	 $scope.publishedOrDeactivated = $scope.smp.status === 'PUBLISHED' || $scope.smp.status === 'DEACTIVATED' ? true:false;
    	 callEditTid("edit",apiName,$scope.smp.mapping.name,majorVersion+'.'+minorVersion,$scope.smp.id);
     };
     
 
     
     var callEditTid = function(type,apiName,tidName,versionNo,versionId){     	
    	sharedPropertiesService.put('tidCallingType',type);
     	sharedPropertiesService.put('tidName',tidName);
     	sharedPropertiesService.put("versionNo",versionNo);
     	sharedPropertiesService.put("versionId",versionId);
     	sharedPropertiesService.put("apiName",apiName);
     	sharedPropertiesService.put("publishedOrDeactivated",$scope.publishedOrDeactivated);
     	tidListDisplayService.getTidMappingStatus(tidName).then(
     			function(responseData) {
 				if (responseData != null && !responseData.error) {
 					if (responseData.response != null)
 						showEditPopUpForVerList(responseData.response,tidName);
 					else
 						getVersionStatus(tidName);
 					
 				} else 
 					alert(" \n Error while checking the tid in versioning "+ responseData.message);
     			}, 
     			function(errorData) {
     				alert('Failed: ' + errorData);
     			}
     		);
     };
     
     var showEditPopUpForVerList = function(listOfVersions,tidName) {
    	$log.info("Request received to edit version list");
 		$dialogs.confirm('Please Confirm','<span class="confirm-body">TID update is disallowed. Do you want to view ?</span>')
 		.result.then(function(btn){
 				$log.warn("Moving to Mapping screen");
				sharedPropertiesService.put('VersionMapped',true);
				$location.path('addTid');
         });
     };
     
     var getVersionStatus = function(tidName) {
     	tidListDisplayService.getVersionStatus(tidName).then(function(responseData) {
 			if (responseData != null && !responseData.error) {
 				if (responseData.response != null){
 					sharedPropertiesService.put('hideSaveButton',true);
 					$location.path('addTid');
 				}else
 					$location.path('addTid');
 			} else 
 				alert(" \n Error while checking the tid in versioning "+ responseData.message);
 		}, function(responseData) {
 			alert('Failed: ' + responseData);
 		});
     };
     
    /** Method to Add Model API in for Exiting */
     
     addModelAPI = sharedPropertiesService.get("addModelAPI");
     if(addModelAPI != null){
     	$scope.smp.name = addModelAPI;
     	$scope.setTenantModelDesc(addModelAPI);
         sharedPropertiesService.remove("addModelAPI");
     }
     
     //----- UMG-3520 Show Exception Stack Trace ------
     
     $scope.openOutput = false;
     //$scope.closeOutput = false;
     
     $scope.expandOutput = function(){
    	 	/*$("#smpInput").removeClass();
			$("#smpInput").addClass('smpInputHalf');
			$("#smpOutput").removeClass();
			$("#smpOutput").addClass('smpOutputHalf');*/
			$scope.openOutput = true;
			//$scope.closeOutput = false;
     };
     
     $scope.collapseOutput = function(){
    	 	/*$("#smpInput").removeClass();
			$("#smpInput").addClass('smpInputFull');
			$("#smpOutput").removeClass();
			$("#smpOutput").addClass('smpOutputNone');*/
			$scope.openOutput = false;
			//$scope.closeOutput = true;
     };
     
     $scope.modelExecNew = false;
     
     /**
      * initial value to be set here if required
      */  
     $scope.intialSetup=function(){
    	 mps.getAllEnvironments().then(
    			 function(responseData){
    				 if(!responseData.error){
    					 $scope.modelexecnames = responseData.response;					
    				 }
    			 },
    			 function(errorData){
    				 $scope.modelexecnames = [];
    				 $log.error('Error');					
    			 }
    	 );
     };
     $scope.intialSetup();
    
   //$scope.modelExecNew= false;
     
     $scope.modelExecToOld = function(){
    	 $scope.modelType = 'OLD';
     };
     
     $scope.modelExecToNew = function(){
    	 $scope.modelType = 'NEW';
     };
     
     //$scope.modelIoDefNew = true;
      
     $scope.modelIoDefToOld = function(){
    	 $scope.modelDefType = 'OLD';
     };
     
     $scope.modelIoDefToNew = function(){
    	 $scope.modelDefType = 'NEW';
     };

}];