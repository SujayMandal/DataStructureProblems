'use strict';

var VersionListCtrl = ['$scope','$log', '$http', '$filter', 'umgVersionService', '$dialogs', '$window', '$location','sharedPropertiesService', 'tidListDisplayService','modelPublishService', function($scope, $log, $http, $filter, umgVersionService, $dialogs, $window, $location, sharedPropertiesService, tidListDisplayService,modelPublishService) {
	 
	// Variable to hold the search option details
	$scope.searchOption={};
	// Variable to display the message in case of any issues
	$scope.msg = '';
	// Variable to indicate whether is grid is loaded or not
	$scope.ready = false;
	// Variable to indicate the errors
	$scope.error = false;
	// Model Variable to hold all the version details
	$scope.modelVersionDetails = [];
	// Model Variable to current selected version
	$scope.selectedVersions = [];
	// Model Variable to model names for auto complete feature
	$scope.modelNames = [];
	// Variable to indicate version  is published or deactivated
	$scope.publishedOrDeactivated = false;
	$scope.selectedVersionId = '';
	 $scope.pickedReport =[];	 
	 $scope.modelExecEnvName = 'R-3.1.2';
	 $scope.emailNotificationEnabled = false;
	 $scope.modelPublishApproval = false;

	$scope.myCellTemplate= function(status){
		'<div class="ngCellText"><span ng-class="row.getProperty(\'status\') === \'SAVED\' ? \'label status-saved\':\'\' || row.getProperty(\'status\') === \'TESTED\' ? \'label status-tested\':\'\' ||  row.getProperty(\'status\') === \'PUBLISHED\' ? \'label status-published\':\'\' || row.getProperty(\'status\') === \'DEACTIVATED\' ? \'label status-deactivated\':\'\'">{{row.getProperty(col.field)}}</span></div>'
	};
	
	// Populate the models for the auto complete search
	$scope.autocompleteVersions = function(){
		
		umgVersionService.getAllTenantModelNames().then(
				
				function(responseData){
					if(!responseData.error){
						var serverData = responseData.response;
						
						angular.forEach(responseData.response, function(modelName){
							$scope.modelNames.push(modelName);
						});
					}
					$log.info("Model names:"+$scope.modelNames);
				},
				
				function(errorData){
					$log.error("Error came with : "+errorData);
				}
		);
	}
	
	// Populate all the  model versions into Angular - Grid
	$scope.findAllVersionName = function(){
			$log.info("getting Model names ");
			
			if(angular.isDefined($scope.searchOption.fromDate)){
				$scope.searchOption.fromDate = $filter('date')($scope.searchOption.fromDate,"MMM-dd-yyyy"); 
			}
			if(angular.isDefined($scope.searchOption.toDate)){
				$scope.searchOption.toDate = $filter('date')($scope.searchOption.toDate,"MMM-dd-yyyy"); 
			}			
			

			umgVersionService.findAllversionByVersionName($scope.searchOption).then(
					function(responseData){
						//$log.info("Response Data:"+responseData);
						if(!responseData.error){
							$scope.emailNotificationEnabled = responseData.response["emailNotificationEnabled"];
							$scope.modelPublishApproval = responseData.response["modelPublishApproval"];
							var branches = [];
							angular.forEach(responseData.response["versionList"],function(data){
			        			 var branch = {
			        					 	   modelName : data.name,
			        					 	   tenantModelDesc : data.description,
			        					 	   version : data.majorVersion+'.'+data.minorVersion,
			        					 	   versionDescription : data.description,
			        					 	   createdBy : data.lastModifiedBy,
			        					 	   createdDate : data.lastModifiedDateTime,
			        					 	   status : data.status,
			        					 	   id:data.id,
			        					 	   tidName:data.mapping.name,
			        					 	   hasReportTemplate:data.hasReportTemplate,
			        					 	   reportTemplateName:data.reportTemplateName,
			        					 	   modelType:data.modelType,
			        					 	   modelExecEnvName :data.modelLibrary.modelExecEnvName
			        					 	   };
			        			 branches.push(branch);
			        		});
			                $scope.modelVersionDetails = branches;
			                $scope.gridOptions.rowData = $scope.modelVersionDetails;
			                //$scope.gridOptions.api.onNewRows();
			                $scope.selectedVersions.push($scope.modelVersionDetails[0]);
			                if($scope.modelVersionDetails.length==0){
								$scope.showGrid=false;
								$scope.showNoGrid=true;
							}else{
								$scope.showGrid= true;
								$scope.showNoGrid= false;
							}
			                $scope.gridOptions.ready = function() {
			                	 $scope.gridOptions.api.onNewRows();
			                	 $scope.gridOptions.api.sizeColumnsToFit();
			                	 if($scope.selectedVersions.length > 0){
			                		 $scope.gridOptions.api.selectIndex(0);
			                	 }
			                	 $scope.ready = true;
		                	};
		                	$scope.gridOptions.api.onNewRows();
		                	if($scope.selectedVersions.length > 0){
		                		 $scope.gridOptions.api.selectIndex(0);
		                	}
		                	$scope.gridOptions.api.sizeColumnsToFit();
						}
					},
					function(errorData){
						$log.error("Error came with : "+errorData);
					}
			);
	};
	
	
	
	
	// Reset Search
	
	$scope.clearSearch=function(){
		 $scope.searchOption.toDate="";
		 $scope.searchOption.fromDate="";
		 $scope.searchOption.searchText="";
		 $scope.findAllVersionName();
	 };	
	 
	 // Search
	 
	 $scope.search=function(){
		 $scope.msg ="";
		 $scope.findAllVersionName();
		 
	 }; 
	
	 
	 function dateComparator(date1, date2) {
        var date1Number = monthToComparableNumber(date1);
        var date2Number = monthToComparableNumber(date2);
       
        if (date1Number===null && date2Number===null) {
            return 0;
        }
        if (date1Number===null) {
            return -1;
        }
        if (date2Number===null) {
            return 1;
        }

        return date1Number - date2Number;
    }
	
	// eg 29/08/2004 gets converted to 20040829
    function monthToComparableNumber(date) {
	    if (date === undefined || date === null || date.length !== 17) {
	        return null;
	    }
	   
	    var yearNumber = date.substring(0,4);
	    var monthNumber= getMonthNumber(date.substring(5,8));
	    var dayNumber= date.substring(9,11);
	    var hour= date.substring(12,14);
	    var minute= date.substring(15,17);
	    var result = (yearNumber*100000000) + (monthNumber*1000000) + (dayNumber*10000)+(hour*100)+minute;
	   
	    return result;
    }
    
    function getMonthNumber(month){
        if(month.toUpperCase() === "JAN"){
          return "01";
        }else if(month.toUpperCase() === "FEB"){
          return "02";
        }else if(month.toUpperCase() === "MAR"){
          return "03";
        }else if(month.toUpperCase() === "APR"){
          return "04";
        }else if(month.toUpperCase() === "MAY"){
          return "05";
        }else if(month.toUpperCase() === "JUN"){
          return "06";
        }else if(month.toUpperCase() === "JUL"){
          return "07";
        }else if(month.toUpperCase() === "AUG"){
          return "08";
        }else if(month.toUpperCase() === "SEP"){
          return "09";
        }else if(month.toUpperCase() === "OCT"){
          return "10";
        }else if(month.toUpperCase() === "NOV"){
          return "11";
        }else if(month.toUpperCase() === "DEC"){
          return "12";
        }
     }
	 
	/** 
	 * Comparator for Version Id Sorting
	 */
	
	function versionComparator(value1, value2) {
		
//		var Version1Major = value1.toString().substring(0,value1.toString().indexof("."));
//		var Version2Major = value1.toString().substring(value1.toString().indexof("."),value1.toString().length);
		
        var version1 = parseFloat(value1);
        var version2 = parseFloat(value2);
        
        if (version1===null && version2===null) {
            return 0;
        }
        if (version1===null) {
            return -1;
        }
        if (version2===null) {
            return 1;
        }

        return version1 - version2;
    }
	
	// Ag-grid Column Definitions
	var columnDefs = [
	                  	 {field:'modelName', headerName:'Model Name',width: 240, suppressSizeToFit: true,unSortIcon: true},
						 {field:'version', headerName:'Version',filter: 'text',hide: false,width: 85, suppressSizeToFit: true,comparator: versionComparator,unSortIcon: true}, 
						 {field:'versionDescription', headerName:'Description',filter: 'text', hide: false,unSortIcon: true},
						 {field:'createdBy', headerName:'Last Updated By',filter: 'text',  hide: false,unSortIcon: true},
						 {field:'createdDate', headerName:'Last Updated On',filter: 'text', hide: false,width: 116,unSortIcon: true,comparator: dateComparator},
						 {field:'status', headerName:'Status',filter: 'text', hide: false, width: 120, maxWidth:120,unSortIcon: true},
						 {field:'modelType', headerName:'Model Type',filter: 'set', hide: false, width: 120, maxWidth:120,unSortIcon: true}
                  ];
	
	// Updating the node details based on the row selection event
	var rowSelected = function(event) {
		$scope.selectedVersions[0] = event;
    }
	// Ag-grid Grid Definitions
	$scope.gridOptions ={
			rowHeight: 24,
			headerRowHeight:24,
	        rowData: 'modelVersionDetails',
	        rowSelection: 'single',
	        enableFilter: true,
	        enableColResize: true,
	       	columnDefs : columnDefs,
	       	rowSelected: rowSelected,
	       	enableSorting: true,
	       	sortingOrder: ['desc','asc']

	};
	
	// On Load trigger of version details from server
	$scope.findAllVersionName();	
	// On Load trigger of auto complete details from server
	$scope.autocompleteVersions();
	
	$scope.expandModel= function(){
		$scope.findAllVersionName();
	}
	

	
	//----------- Version Related Function ----------------------
	
	$scope.viewVersionInfo = function(version){
		sharedPropertiesService.put("viewModelApi",version.id);
		$location.path('modelApiView');
	};
	
	/**
	 * This method will send request to server to publish version.
	 * */
    $scope.publishVersion = function(versionId){
    	$log.info("Request Received to Publish Version with ID : "+versionId);
    	var dlg = $dialogs.confirm('Please Confirm','<span class="confirm-body">Are you sure to publish this Version ?</span>');
        dlg.result.then(function(btn){
        	$log.info("You confirmed to publish this version...");
			umgVersionService.publishVersion(versionId).then(
					function(responseData) {
						$scope.msg = responseData.message;
						$scope.error = responseData.error;
						if(!responseData.error){
							$scope.expandModel();
						}
					},
					function(errorData) {
						$scope.msg = errorData;
						$scope.error = true;
					}
			);
        });
    };
    
    /**
	 * This method will send mail to get approval for publishing a tested version
	 **/
    $scope.sendPublishApproval = function(versionId){
    	$log.info("Request Received to send Approval Mail : "+versionId);
    	var dlg = $dialogs.confirm('Please Confirm','<span class="confirm-body">Are you sure to send publish approval request ?</span>');
    	dlg.result.then(function(btn){
    		$log.info("You confirmed to send publish approval request email...");
    		umgVersionService.sendPublishApproval(versionId).then(
    				function(responseData){
    					$scope.msg = responseData.message;
						$scope.error = responseData.error;
						if(!responseData.error){
							$scope.expandModel();
						}
    				},
    				function(errorData){
    					$scope.msg = errorData;
						$scope.error = true;
    				}
    			);
    	});
    }
	
    /**
	 * This method will send request to server to deactivate version.
	 * */
    $scope.deactivateVersion = function(versionId){
    	$log.info("Request Received to Deactivate Version with ID : "+versionId);
    	$dialogs.confirm('Please Confirm','<span class="confirm-body">Do you want to deactivate this version ?</span>')
    	.result.then(function(btn){
    		$log.warn("You confirmed to deactivate this version...");
    		umgVersionService.deactivateVersion(versionId).then(
					function(responseData) {
						$scope.msg = responseData.message;
						$scope.error = responseData.error;
						if(!responseData.error){
							$scope.expandModel();
						}
					},
					function(errorData) {
						$scope.msg = errorData;
						$scope.error = true;
					}
			);
        });
    };
    
    
    /**
	 * This method will send request to server to Export Version.
	 * */
    $scope.exportVersion = function(version){
		$log.info("Please confirm to Export Version : "+version.modelName);
		$dialogs.confirm('Please Confirm','<span class="confirm-body">Do you want to export <strong>'+version.modelName+'-'+version.version+'<strong> ?</span>')
		.result.then(function(btn){
        	$log.info("Confirmation received to Export Version : "+version.modelName);
			$window.location.href = 'version/export/'+version.modelName+'/'+version.version+'/'+version.id;
        });
	};
	
	/**
	 * This method will send request to server to Export Version API.
	 * */
    $scope.exportVersionAPI = function(version){
		$log.info("Please confirm to Export Version API: "+version.modelName);
		$dialogs.confirm('Please Confirm','<span class="confirm-body">Do you want to export API for <strong>'+version.modelName+'-'+version.version+'<strong> ?</span>')
		.result.then(function(btn){
        	$log.info("Confirmation received to Export Version API for : "+version.modelName);
        	$window.location.href = 'versiontest/downloadAPI/'+version.id;
        });
	};
	
	/**
	 * This method will send request to server Delete Version.
	 * */
	$scope.deleteVersion = function(version){
		$log.warn("You requested to delete version : "+version.modelName);
		$dialogs.confirm('Please Confirm','<span class="confirm-body">Do you want to delete <strong>'+version.modelName+'-'+version.version+'<strong> ?</span>')
		.result.then(function(btn){
			$log.info("Confirmation received to Delete Version : "+version.modelName);
			umgVersionService.deleteVersion(version.id).then(
					function(responseData) {
						$scope.msg = responseData.message;
						$scope.error = responseData.error;
						if(!responseData.error){
							$scope.expandModel();
						}
					},
					function(errorData) {
						$scope.msg = errorData;
						$scope.error = true;
					});
        });
	};
	
	/**
	 * This method will send request to server to Download Tenant Input in excel.
	 * */
    $scope.exportExcel = function(version){
		$log.info("Please confirm to Download Version in Excel file");
		$dialogs.confirm('Please Confirm','<span class="confirm-body">Do you want to export Excel for <strong>'+version.modelName+'-'+version.version+'<strong> ?</span>')
		.result.then(function(btn){
        	$log.info("Confirmation received to download excel for version : "+version.modelName);
        	$window.location.href = 'version/downloadExcel/'+version.tidName+'/'+version.modelName+'/'+version.version;
        });
	};
	
	/**
	 * This method will redirect to TestBed.
	 * */
    $scope.testVersion = function(version){
        $log.info("Redirecting to TestBed ...");
        var testVersion = {"versionId": version.id, "tidName": version.tidName,"modelName":version.modelName,"majorVersion":parseInt(version.version.split('.')[0]),"minorVersion":parseInt(version.version.split('.')[1]),"hasReportTemplate":version.hasReportTemplate,"storeRLogs":false,"isRModel":version.modelExecEnvName.includes("R")};
        sharedPropertiesService.put("testVersion",testVersion);
        $location.path('version/testbed');
     };
     
     /**
      * 
      * Added this code to edit mapping.This code is same as that of ModelPublisshingCtrl.js file.Need to put it into some common js : START
  	 * This method will redirect to update mapping page
  	 * */
        $scope.editMapping = function(tidName,versionNo,verName){    	
      	sharedPropertiesService.put('tidCallingType',"edit");
       	sharedPropertiesService.put('tidName',tidName);
       	sharedPropertiesService.put("versionNo",versionNo);  
       	sharedPropertiesService.put("apiName",verName);
       	$scope.publishedOrDeactivated = $scope.selectedVersions[0].status === 'PUBLISHED' || $scope.selectedVersions[0].status === 'DEACTIVATED' ? true:false
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
            
            var showEditPopUpForVerList = function(listOfVersions,tidName) {
            	$dialogs.confirm('TID is referenced in Published/Deactivated Versions','<span class="confirm-body">TID update is disallowed. Do you want to view ?</span>')
        		.result.then(function(btn){
        			$log.warn("Moving to Mapping screen");
 					sharedPropertiesService.put('VersionMapped',true);
 					$location.path('addTid');
                });
             };
             
             
             /**
              * function for version metric
              */
             $scope.versionMetricRequestInfo = {
            	    	versionName : '',
            		    majorVersion : '',
            		    minorVersion : '',
            		    fromDate : '',
            		    toDate : '',
            		    isTest: 0
            	    };
            	    $scope.versionMetricResponse;	
            	    $scope.versionMetric = function(ver) {
            	    	$log.info("version metric : "+ver.modelName);
            	    	$scope.versionMetricRequestInfo.versionName = ver.modelName;
            	    	$scope.versionMetricRequestInfo.majorVersion = ver.version.split(".")[0];
            	    	$scope.versionMetricRequestInfo.minorVersion = ver.version.split(".")[1];
            	    	umgVersionService.fetchVersionMetric($scope.versionMetricRequestInfo).then(
            				function(responseData){
            					if(!responseData.error){
            						$scope.versionMetricResponse = responseData.response;
            						$log.warn("Moving to Metric screen");
                 					sharedPropertiesService.put('MetricData',$scope.versionMetricResponse);
                 					sharedPropertiesService.put('VersionData',ver);
                 					$location.path('metrics');
            					}else{
            						$scope.allModels = [];
            						$log.error("Error came with : "+responseData.message);
            						$scope.msg(responseData.message,'no data found');
            					}
            				},
            				function(errorData){
            					$log.error("Error came with : "+errorData);
            					$scope.msg(errorData,'no data found');
            				}
            	    	);
            	    };
        			
        			$scope.resetReportTemplateFile = function() {
        				$scope.pickExistingReport = false;
        				$('#jrxml_upload_btn')[0].value='';        				
        				$scope.smp.reportTemplateInfo.reportTemplate = null;
        				$('#rep_temp_txt')[0].value='';        				
        				
        			};
             
            	    $scope.smp = {
        				modelLibrary : {
        					jrxml : ''
        					},
            	    	reportTemplateInfo :{
            	    		id : '',
            	    		reportTemplate : '',
            	    		name : ''
            	    	}
            		};
        	    
            	    $scope.reportTemplate = function(version) {
            	    	$scope.resetReportTemplateFile();
            			$('#myModal').modal('show');
            			$scope.selectedModel = version.modelName;
            			$scope.selectedVersionId = version.id;
            		};
            		
            		$scope.uploadReportTemplate = function(versionId){
            			umgVersionService.uploadTemplate(versionId, $scope.smp.reportTemplateInfo.reportTemplate,$scope.smp.reportTemplateInfo.id).then(
	            			function(responseData) {
	            				$scope.msg = responseData.message;
	            				$scope.error = responseData.error;
	            				if ($scope.error == false) {
		            				$scope.modelVersionDetails.forEach(function(obj) { 
		            					if (obj.id==$scope.selectedVersions[0].id) 
		            						obj.hasReportTemplate=true;	
		            				});
	            				}
	            		},
            				function(errorData) {
            					$scope.msg = errorData;
            					$scope.error = true;
            				});
            		};
            		
            		$scope.launchExistingReportDialog = function(modelExecEnvName) {   
            			$scope.modelExecEnvName = modelExecEnvName;     			
        				$scope.smp.reportTemplateInfo.id = '';
        				$scope.smp.reportTemplateInfo.reportTemplate = '';        				
        				modelPublishService.getModelReportDetails(modelExecEnvName, 'All').then(function(responseData) {
        					if (!responseData.error) {
        						$scope.pickExistingReport = true;
        						$scope.modelApiDetails = buildModelApiDetails(responseData.response);        					
        						setTimeout(function(){
        				            $(window).resize();
        				            $(window).resize();
        				        }, 0);
        					}
        				}, function(errorData) {
        					$scope.modelApiDetails = [];
        				});
        			};
        			
        			var buildModelApiDetails = function(response) {
        				var modelAPIDetails = [];
        				angular.forEach(response, function(smp) {        					
        					var report_name = '';
        					var report_id = '';
        					if (smp.reportTemplateInfo != null) {
        						report_name = smp.reportTemplateInfo.name;		
        						report_id=smp.reportTemplateInfo.id;
        					}

        					var mad = {
        						id : '',
        						name : smp.name,
        						version : smp.majorVersion + '.' + smp.minorVersion,
        						status : smp.status,        						
        						createdBY : smp.createdBy,
        						createdDate : smp.createdDateTime,        					
        						modelType : smp.modelType,
        						reportName : report_name,
        						reportId : report_id
        					};
        					modelAPIDetails.push(mad);
        				});

        				return modelAPIDetails;
        			};
        			
        			$scope.modelReportGrid = {
        					data : 'modelApiDetails',
        					enableRowSelection : true,
        					multiSelect : false,
        					selectedItems : $scope.pickedReport,
        					afterSelectionChange : function(rowItem, event) {
        						$scope.smp.reportTemplateInfo.id = rowItem.entity.reportId;
        						$scope.smp.reportTemplateInfo.reportTemplate = rowItem.entity.reportName;
        					
        					},
        					columnDefs : [ {
        						field : 'name',
        						displayName : 'Model API Name'
        					}, {
        						field : 'version',
        						displayName : 'Version'
        					}, {
        						field : 'reportName',
        						displayName : 'Report Name'
        					},{
        						field : 'modelType',
        						displayName : 'ModelType'
        					}, {
        						field : 'status',
        						displayName : 'Status'
        					}, {
        						field : 'createdBY',
        						displayName : 'Created By'
        					}, {
        						field : 'createdDate',
        						displayName : 'Created Date'
        					},{
        						field : 'checksum',
        						displayName : 'Check Sum',
        						visible:false
        					} ]
        				};
        			
        			$scope.resetpickExistingReportFlag = function(clrSelection) {
        				if (clrSelection) {
        					$scope.smp.reportTemplateInfo.reportTemplate = '';
        				}
        				if ($scope.smp.reportTemplateInfo.reportTemplate== null || $scope.smp.reportTemplateInfo.reportTemplate == '') {
        					$scope.pickExistingReport = false;
        				}		
        				$scope.srchReportTxt = '';
        			};     
        			
        			$scope.clearModal = function() {
        				$scope.modalVar = "modal";        			
        				$("#reportModal").modal("hide");        			
        			};
        			
        			$scope.resetPickReportTemplateFlag = function(){				
        				$scope.smp.reportTemplateInfo.reportTemplate = '';
        				$scope.pickExistingReport = false;
        			};
        			
        			$scope.setFilteredReports = function(searchText) {
    					$scope.pageInfo = {
    						searchText : searchText,
    						fromDate : '',
    						toDate : '',
    						pageSize : 50,
    						page : 0,
    						sortColumn : 'createdDate',
    						descending : true
    					};

    					modelPublishService.getFilteredReport($scope.pageInfo, $scope.modelExecEnvName).then(
    							function(responseData) {
    								if (!responseData.error) {
    									$scope.modelApiDetails = buildModelApiDetails(responseData.response);
    									$('#searchReport').focus();
    								}
    							}, function(errorData) {
    								$scope.modelApiDetails = [];
    								$log.error('Error');
    							});
    				};    				
    				
    				$scope.$watch('srchReportTxt', function(newVal, oldVal) {    					
    					if (newVal == '')
    						setModelReportDetails('All');
    				}, true);
    				
    				var setModelReportDetails = function(modelType) {
    					modelPublishService.getModelReportDetails($scope.modelExecEnvName, modelType).then(function(responseData) {
    						if (!responseData.error) {
    							$scope.modelApiDetails = buildModelApiDetails(responseData.response);
    						}
    					}, function(errorData) {
    						$scope.modelApiDetails = [];
    					});
    				};



        			
        			


        			
}];
			