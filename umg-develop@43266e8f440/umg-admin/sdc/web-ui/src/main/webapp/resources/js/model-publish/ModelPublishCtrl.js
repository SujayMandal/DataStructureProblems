'use strict';

var ModelPublishCtrl = [
		'$scope',
		'$window',
		'$http',
		'$log',
		'$location',
		'$timeout',
		'modelPublishService',
		'sharedPropertiesService',
		'tidListDisplayService',
		'$dialogs',
		function($scope, $window, $http, $log, $location, $timeout, modelPublishService, sharedPropertiesService, tidListDisplayService, $dialogs) {
			/** ######## variable declaration starts here ### * */
			/** Variable to show create model/version from scratch form* */
			$scope.createNewModelMode = 'CreateFromScratch';
			/** Variable to show or hide import from file form * */
			$scope.createNewVersionMode = 'CreateFromScratch';
			/** variable to show or hide additional artifacts form * */
			$scope.showAdditionalArtifactsForm = false;
			/** holds programming languages supported by the system * */
			$scope.programmingLanguages = [];
			/** holds programming language and it's version information * */
			$scope.languageVersionMap = {};
			/** holds language version for the selected programming language * */
			$scope.languageVersions = [];
			$scope.activeLanguageVersion = null;
			$scope.activeTab = 'CreateNewModel';
			$scope.uniqueModelNames = [];
			$scope.majorVersionsForModel = [];
			$scope.versionImport = {
				versionZipFile : ''
			};
			$scope.importedFileName = '';
			$scope.modelApiDetails = [];
			$scope.pickedDef = [];
			$scope.pickedLib = [];
			$scope.pickedReport = [];
			$scope.migrationId = '';
//			$scope.websocketBaseURL = '';
			$scope.showStatusBlock = true;
			$scope.showManifestFileStatus = false;
			$scope.showReportTemplateStatus = false;

			
			$scope.versionDetail = {
				importFileName : '',
				migrationId : '',
				name : '',
				description : '',
				versionType : 'MAJOR',
				majorVersion : '',
				versionDescription : '',
				modelType : 'Online'
			};
			
			/** model variable to hold modelType **/
			$scope.modelType = 'Online';
			/*$scope.modelTypes = [];
			$scope.modelTypes.push("Online");
			$scope.modelTypes.push("Bulk");
*/
			/** variable to hold create model version * */
			$scope.smp = {
				id : '',
				name : '',
				description : '',
				versionType : 'MAJOR',
				majorVersion : '',
				minorVersion : '',
				status : '',
				modelType : 'Online',
				versionDescription : '',
				publishedOn : '',
				publishedBy : '',
				deactivatedOn : '',
				deactivatedBy : '',
				clientID : '',
				mapping : {
					id : '',
					name : '',
					description : '',
					active : false,
					modelName : '',
					umgName : '',
					status : '',
					model : {
						id : '',
						name : '',
						description : '',
						umgName : '',
						ioDefinitionName : '',
						ioDefExcelName : '',
						documentationName : '',
						xml : '',
						excel : '',
						allowNull : true
					}
				},
				modelLibrary : {
					id : '',
					name : '',
					executionLanguage : '',
					executionType : 'Internal',
					description : '',
					umgName : '',
					jarName : '',
					rmanifestFileName : '',
					checksum : '',
					jar : '',
					manifestFile : '',
					modelExecEnvName : '',
					programmingLanguage : ''
				},
				reportTemplateInfo : {
					id : '',
					name : '',
					reportTemplate : ''
				}				
			};

			// store original modal variable to reset the form
			$scope.smpInitial = angular.copy($scope.smp);
			
			$scope.reportDownloadStatus = null;
			$scope.reportDownloadError = false;
			$scope.versionSaved = false;
			$scope.publishingInProgress = false;
			$scope.actionsCompleted = -1;
			$scope.versionImportInProgress = false;
			$scope.openOutput = false;
			$scope.pickExistingLib = false;
			$scope.pickExistingIODefn = false;
			$scope.pickExistingReport = false;
			$scope.showSuccess = false;
			$scope.showErr = false;
			$scope.reportSuccess = false;
			$scope.reportUrl = null;
			$scope.versionFailed = false;
			$scope.showReportURL = false;
			$scope.versionSubmitted = false;
			$scope.tenantOutput = null;
			$scope.txnId = '';
			$scope.versionStatus = '';
			$scope.modelError = {
				errorMsg : {},
				step : 'testVersion',
				errorCode : 'MODELEXCEPTION'
			};
			$scope.isJson = false;
			$scope.modalVar = '';
			$scope.tabOneActive = true;
			$scope.tabTwoActive = false;
			$scope.inputValidationErrors = [];
			$scope.outputValidationErrors = [];
			$scope.supportingDocValidationErrors = [];
			$scope.execFailureErrors = [];

			/** ######## variable declaration ends here ### * */

			var getActiveTab = function(tabToActivate) {

				switch (tabToActivate) {
				case 'CreateNewModel':
					$scope.tabOneActive = true;
					$scope.tabTwoActive = false;
					break;
				case 'CreateNewVersion':
					$scope.tabTwoActive = true;
					$scope.tabOneActive = false;
					break;
				}

			};
			
			/** This method resets the values while navigating between "Create New Model" and "Create New Version" tabs. * */
			$scope.clearAndSwitchTab = function(currentTab, tabToActivate, $event, tabNumber) {
				if ($scope.activeTab != tabToActivate) {
					$scope.smpInitial.modelType = $scope.smp.modelType;
					if (JSON.stringify($scope.smpInitial) != JSON.stringify($scope.smp) || $scope.versionImport.versionZipFile != '') {
						$event.stopPropagation();
						var dlg = $dialogs.confirm('Please Confirm',
								'<span class="confirm-body">Data in the form will be lost. Click on Yes to continue?</span>');

						dlg.result.then(function(yesBtn) {
							$scope.switchTab(tabToActivate);
							getActiveTab(tabToActivate);
						}, function(noBtn) {
							getActiveTab(currentTab);
						});
					} else {
						$scope.switchTab(tabToActivate);
						getActiveTab(tabToActivate);
					}
				}
				$scope.activeLanguageVersion = null;
			};
			
			$scope.downloadIO = function(status){
				$log.warn("Dowloading IO file for transaction id "+$scope.txnId);
				window.location.href = '..'+window.location.pathname+'txnDashBoard/downloadModelAndTntIO?idList='+$scope.txnId+'&apiName='+$scope.smp.name+'&status='+status;
			};
			
			
			$scope.switchTab = function(tabToActivate) {
				$scope.createNewModelMode = 'CreateFromScratch';
				$scope.createNewVersionMode = 'CreateFromScratch';
				$scope.versionStatus='';
				$scope.resetOnToggle();
				$scope.activeTab = tabToActivate;
				if (tabToActivate == 'CreateNewVersion') {
					$scope.loadAllModelNames();
				}
			}

			$scope.expandOutput = function() {
				$scope.openOutput = true;
			};

			$scope.collapseOutput = function() {
				$scope.openOutput = false;
			};

			/** validated whether given model name is present in system or not * */
			$scope.validateModelName = function() {
				$scope.supportingDocValidationErrors = [];
				$scope.versionFailed = false;
				$scope.versionStatus = '';
				$scope.versionSubmitted = false;
				$scope.showErr = false;
				$scope.openOutput = false;
				$scope.isJson = false;
				if ($scope.activeTab == 'CreateNewModel') {
					var modelname = $scope.smp.name;
					if(modelname.indexOf("-") !== -1 || modelname.indexOf("/") !== -1 ||modelname.indexOf("\\") !== -1 ){
						var errorMsgTmp = 'Model name cannot include - (hyphen). Please correct and retry.'; 
						$scope.supportingDocValidationErrors
						.push(modelname.indexOf("-") !== -1?errorMsgTmp:errorMsgTmp.replace("hyphen","forward or backward slash"));
						$scope.versionFailed = true;
						$scope.versionStatus = "Validation Failed";
						$scope.versionSubmitted = true;
						$log.error('Validation Failed !');
						$scope.showErr = true;
						return false;					
						
					}
					modelPublishService
							.validateModelName($scope.smp.name)
							.then(
									function(responseData) {
										if (!responseData.error) {
											var versionCount = responseData.response;
											if (versionCount == 0) {
												$scope.showAdditionalArtifactsForm = true;
											} else {
												$scope.supportingDocValidationErrors
														.push('Model with the name already exist. To create a new version, select "Create New Version"');

											}
										} else {
											alert("failed to save");
										}

										if ($scope.supportingDocValidationErrors.length > 0) {
											$scope.versionFailed = true;
											$scope.versionStatus = "Validation Failed";
											$scope.versionSubmitted = true;
											$log.error('Validation Failed !');
											$scope.showErr = true;
											return false;
										}
										return true;
									}, function(errorData) {
										alert("error data" + errorData)
									});
				} else {
					$scope.showAdditionalArtifactsForm = true;
					return true;
				}

			};

			$scope.setModelDescription = function() {
				if ($scope.smp.name != null || $scope.smp.name != '') {
					modelPublishService.getModelDetails($scope.smp.name).then(
							function(responseData) {
								if (responseData.response != null) {
									var versionInfo = responseData.response;
									$scope.smp.description = versionInfo.description;
									$scope.smp.versionDescription = versionInfo.description;
									$scope.smp.modelLibrary.executionLanguage = versionInfo.modelLibrary.executionLanguage;
									if (versionInfo.modelLibrary.programmingLanguage == null
											|| versionInfo.modelLibrary.programmingLanguage == '') {
										if (versionInfo.modelLibrary.executionLanguage.indexOf('Matlab') > -1) {
											$scope.smp.modelLibrary.programmingLanguage = "Matlab";
										} else if (versionInfo.modelLibrary.executionLanguage.indexOf('Excel') > -1) {
											$scope.smp.modelLibrary.programmingLanguage = "Excel";
										} else {
											$scope.smp.modelLibrary.programmingLanguage = "R";
										}
									}
									$scope.smp.modelLibrary.executionLanguage = versionInfo.modelLibrary.modelExecEnvName;
									$scope.loadLanguageVersions();
									$scope.activeLanguageVersion = $scope.languageVersions[0]; 
								}
							}, function(errorData) {
								$log.error('Failed to Set Description.');
							});
				}
			};

			$scope.setProgrammingLanguages = function() {
				// TODO populates programming languages from server
				$scope.programmingLanguages.push('Matlab');
				$scope.programmingLanguages.push('R');
				$scope.programmingLanguages.push('Excel');
			};

			$scope.loadLanguageVersions = function() {
				$scope.languageVersions = $scope.languageVersionMap[$scope.smp.modelLibrary.programmingLanguage.toUpperCase()];
			};
			
			/*$scope.resetModeltype = function(){
				if($scope.smp.modelLibrary.executionLanguage == 'R-3.2.1'){
					$scope.modelTypes[1] = 'Bulk';
					$scope.modelType = 'Bulk';
					}
				else{
					$scope.modelTypes.pop();
					$scope.modelType = 'Online';
				}
				alert($scope.modelType);
			}*/
			
			$scope.updateModelType = function(modelType){
				$scope.smp.modelType = modelType;
				$scope.resetLaunchExistingIODefn(true);
				$scope.resetPickIODefn();
			}
			
			$scope.resetImportStatus = function() {
				// $scope.importedFileName = '';
				// $scope.versionSaved = false;
				// $scope.versionFailed = false;
				// $scope.versionImport.versionZipFile = '';
				// $scope.versionImportInProgress = false;
				$scope.versionImport.versionZipFile = '';
				$scope.resetOnToggle();
			};

			$scope.clearModal = function() {
				$scope.modalVar = "modal";
				// $('#libModal').hide();
				$("#defModal").modal("hide");
				$("#reportModal").modal("hide");
				$("#libModal").modal("hide");
				// $('#libModal').close();
			};

			$scope.resetOnToggle = function() {
				$scope.smp = angular.copy($scope.smpInitial);
				$scope.showAdditionalArtifactsForm = false;
				$scope.importedFileName = '';
				$scope.migrationId = '';
				$scope.versionSaved = false;
				$scope.versionFailed = false;
				$scope.versionImportInProgress = false;
				$scope.pickExistingLib = false;
				$scope.pickExistingIODefn = false;
				$scope.pickExistingReport = false;
				$scope.openOutput = false;
				$scope.versionDetail.importFileName = '';
				$scope.versionDetail.migrationId = '';
				$scope.versionDetail.name = '';
				$scope.versionDetail.description = '';
				$scope.versionDetail.versionType = 'MAJOR';
				$scope.versionDetail.majorVersion = '';
				$scope.versionDetail.versionDescription = '';
				$scope.versionDetail.modelType = 'Online';
				$('#matlab_jar_upload_btn')[0].value='';
				$('#r_tar_upload_btn')[0].value='';
				$('#io_defn_upload_btn')[0].value='';
				$('#model_release_notes_btn')[0].value='';
				$scope.versionImport.versionZipFile ='';
				$('#version_zip_browse')[0].value='';
				$('#r_manifest_upload_btn')[0].value = '';
			};
			
			$scope.scrollToTop = function() {
			    document.body.scrollTop = 0;
			    document.documentElement.scrollTop = 0;
			}
			
			$scope.clearForm = function() {
				this.mpForm.$setPristine();
				$scope.smp = angular.copy($scope.smpInitial);
				$scope.showAdditionalArtifactsForm = false;
				$scope.resetOnToggle();
				$scope.importedFileName = '';
				$scope.activeLanguageVersion = null;
				$scope.versionImportInProgress = false;
				$scope.showErr = false;
				$scope.isJson = false;
				$('#model_release_notes_btn').val('');
				// $scope.clearAndSwitchTab("CreateNewModel");
			};

			$scope.cancelForm = function() {
				$scope.createNewModelMode = 'CreateFromScratch';
				$scope.createNewVersionMode = 'CreateFromScratch';
				$scope.resetOnToggle();
			};

			$scope.saveVersion = function() {
				$scope.actionsCompleted = -1;
				$scope.showStatusBlock = true;
				$scope.reportDownloadStatus = null;
				$scope.reportDownloadError = false;
				$scope.supportingDocValidationErrors = [];
				/*$scope.smp.modelType = $scope.modelType;*/
				if ($scope.activeTab == 'CreateNewModel') {
					modelPublishService
							.validateModelName($scope.smp.name)
							.then(
									function(responseData) {
										if (!responseData.error) {
											var versionCount = responseData.response;
											if (versionCount == 0) {
												$scope.save()
											} else {
												$scope.supportingDocValidationErrors
														.push('Model with the name already exist. To create a new version, select Create New Version');
											}
										} else {
											alert("failed to save");
										}

										if ($scope.supportingDocValidationErrors.length > 0) {
											$scope.versionFailed = true;
											$scope.versionStatus = "Validation Failed";
											$scope.versionSubmitted = true;
											$log.error('Validation Failed !');
											$scope.showErr = true;
										}
									}, function(errorData) {
										alert("error data" + errorData)
									});
				} else {
					$scope.save();
				}

			};

			$scope.save = function() {
				$scope.versionSaved = false;
				$scope.showErr = false;
				$scope.versionSubmitted = false;
				$scope.versionFailed = false;
				$scope.openOutput = false;
				$scope.showSuccess = false;
				$scope.showErr = false;
				$scope.reportSuccess = false;
				$scope.reportUrl = null;
				$scope.errorMessages = [];
				$scope.txnId = '';
				$scope.inputValidationErrors = [];
				$scope.outputValidationErrors = [];
				$scope.supportingDocValidationErrors = [];
				$scope.execFailureErrors = [];
				$scope.tenantOutput = null;

				if ($scope.versionImportInProgress) {
					$scope.importVersion();
				} else {
					$log.info('Request received to save version');
					$scope.smp.mapping.model.name = $scope.smp.name;
					$scope.smp.modelLibrary.name = $scope.smp.name;
					$scope.smp.modelLibrary.description = $scope.smp.description;
					$scope.smp.mapping.model.description = $scope.smp.description;
					$scope.smp.modelLibrary.modelExecEnvName = $scope.smp.modelLibrary.executionLanguage;

					if (validateNewModel($scope.smp)) {
						$scope.smp.clientID = Math.floor((Math.random() * 100000));
						$scope.actionsCompleted = 0;
						$scope.publishingInProgress = true;
//						var webSocket = new WebSocket($scope.websocketBaseURL + "/counter/" + $scope.smp.clientID);
//				        
//				        webSocket.onmessage = function(event) {
//				        	if(parseInt(event.data) > $scope.actionsCompleted){
//				        		$scope.actionsCompleted = parseInt(event.data);  
//				        	}
//				        };
//				        
//				        webSocket.onerror = function(evt) {
//				               //alert(evt) 
//				        };
//				        
//				        webSocket.onclose = function(evt){ 
//				               //alert(evt);
//				        };
						modelPublishService.createNewModel($scope.smp, $scope.versionImportInProgress, $scope.pickExistingLib,
								$scope.pickExistingIODefn).then(function(responseData) {
							$scope.reportUrl = "";
							$scope.reportExecutionStatus = "";
							$scope.reportErrorMessage = '';
							$scope.showReportURL = false;
							if (responseData.response.success) {
								$log.info('Version Saved Successfully.');
								$scope.tenantOutput = responseData.response.versionInfo.testBedOutputInfo.outputJson;
								$scope.txnId = responseData.response.transactionId;
								$scope.versionSaved = true;
								$scope.versionStatus = "Tested & Saved";
								$scope.showSuccess = true;
								//UMG-4337
								$scope.openOutput = true;								
								if (responseData.response.reportInfo != null) {
									$scope.showReportURL = true;
									if (responseData.response.reportInfo.reportExecutionStatus == 'SUCCESS') {
										$scope.reportSuccess = true;
										$scope.reportUrl = responseData.response.reportInfo.reportURL;	
										$scope.reportExecutionStatus = responseData.response.reportInfo.reportExecutionStatus;	
										$scope.transactionId=responseData.response.reportInfo.transactionId;
										$scope.reportName=responseData.response.reportInfo.reportName;
									} else if (responseData.response.reportInfo.reportExecutionStatus == 'FAILED') {
										$scope.reportSuccess = false;
										$scope.reportUrl = null;
										$scope.reportErrorMessage = responseData.response.reportInfo.errorMessage;
										$scope.reportExecutionStatus = responseData.response.reportInfo.reportExecutionStatus;
									}
								}
								$scope.showStatusBlock = false;
							} else {
								$log.error('Failed to Save Version.');
								if (responseData.response.modelExceptions != null && (responseData.response.modelExceptions.payload == undefined || (responseData.response.modelExceptions.payload != undefined && responseData.response.modelExceptions.payload.localizedMessage != undefined))) {
									if(responseData.response.modelExceptions.payload != undefined){
										$scope.modelError.errorMsg = responseData.response.modelExceptions.payload.localizedMessage;
									} else if(responseData.response.modelExceptions.errorDescription != undefined){
										$scope.modelError.errorMsg = responseData.response.modelExceptions.errorDescription;
									} else {
										for(var i in responseData.response.errors){
											$scope.modelError.errorMsg = responseData.response.errors[i].errorMsg;
										}
									}
									$scope.execFailureErrors.push($scope.modelError.errorMsg);
								} else {
									$scope.errorMessages = responseData.response.errors;
									for(var i in $scope.errorMessages){
										if($scope.errorMessages[i].step == "createMapping" || $scope.errorMessages[i].code.includes("EXPL")){
											if($scope.errorMessages[i].errorMsg.includes("Inputs definition") || $scope.errorMessages[i].errorMsg.toLowerCase().includes("inputs sheet")){
												$scope.inputValidationErrors.push($scope.errorMessages[i].errorMsg);
											} else {
												$scope.outputValidationErrors.push($scope.errorMessages[i].errorMsg);
											}
										} else if($scope.errorMessages[i].code.includes("BSE")){
											$scope.supportingDocValidationErrors.push($scope.errorMessages[i].errorMsg);
										} else if($scope.errorMessages[i].step.includes("validateRManifestFile")){
											$scope.supportingDocValidationErrors.push($scope.errorMessages[i].errorMsg);
										} else {
											$scope.execFailureErrors.push($scope.errorMessages[i].errorMsg);
										}
									}
								}
								$scope.txnId = responseData.response.transactionId;
								$scope.versionFailed = true;
								$scope.versionStatus = "Validation Failed";
								//UMG-4337
								$scope.openOutput = true;
							}
							$scope.versionSubmitted = true;
							$scope.publishingInProgress = false;
						}, function(errorData) {
							$log.error('Internal Server Error.' + errorData);
							$scope.modelError = {};
							$scope.modelError.step = "globalError";
							if (errorData != null && errorData.errorCode != null) {
								$scope.modelError.errorCode = errorData.errorCode;
								$scope.modelError.errorMsg = errorData.message;
							} else if (errorData != null) {
								$scope.modelError.errorMsg = errorData;
							} else {
								$scope.modelError.errorMsg = "Communication failure.";
							}
							$scope.execFailureErrors.push($scope.modelError.errorMsg);
							$scope.showErr = true;
							$scope.versionSubmitted = true;
							$scope.versionFailed = true;
							$scope.versionStatus = "Validation Failed";
							$scope.publishingInProgress = false;
						});
					}
				}
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

			var validateNewModel = function(smp) {
				$scope.showSuccess = false;
				$scope.reportSuccess = false;
				$scope.reportUrl = null;
				$scope.showErr = false;
				$scope.supportingDocValidationErrors = [];
				if (!$scope.versionImportInProgress) {
					if (smp.modelLibrary.executionLanguage.indexOf('Matlab') != -1) {
						if (!$scope.pickExistingLib) {
							if (angular.isUndefined(smp.modelLibrary.jar) || smp.modelLibrary.jar == '') {
								var msg = 'Please upload .jar file';
								$scope.supportingDocValidationErrors.push(msg);
							} else {
								var index = smp.modelLibrary.jar.name.lastIndexOf('.');
								var extension = smp.modelLibrary.jar.name.substring(index);
								if (extension.toLowerCase() != '.jar') {
									$scope.supportingDocValidationErrors.push('Invalid file type for library, only jar file allowed.');
								}

								if (angular.isUndefined(smp.modelLibrary.checksum) || smp.modelLibrary.checksum == '') {
									$scope.supportingDocValidationErrors.push('Checksum is mandatory');
								}
							}
						}
					} else if (smp.modelLibrary.executionLanguage.indexOf('Excel') != -1) {
						if (!$scope.pickExistingLib) {
							if (angular.isUndefined(smp.modelLibrary.jar) || smp.modelLibrary.jar == '') {
								var msg = 'Please upload .xls/.xlsx/.xlsm file';
								$scope.supportingDocValidationErrors.push(msg);
							} else {
								var index = smp.modelLibrary.jar.name.lastIndexOf('.');
								var extension = smp.modelLibrary.jar.name.substring(index);
								if (extension.toLowerCase() != '.xls' && extension.toLowerCase() != '.xlsx' && extension.toLowerCase() != '.xlsm') {
									$scope.supportingDocValidationErrors.push('Invalid file type for library, only xls/xlsx/xlsm file allowed.');
								}

								if (angular.isUndefined(smp.modelLibrary.checksum) || smp.modelLibrary.checksum == '') {
									$scope.supportingDocValidationErrors.push('Checksum is mandatory');
								}
							}
						}
					} else {
						if (!$scope.pickExistingLib) {
							if (angular.isUndefined(smp.modelLibrary.jar) || smp.modelLibrary.jar == '') {
								$scope.supportingDocValidationErrors.push('Please choose R library');
							} else {

								var tokns = smp.modelLibrary.jar.name.split('.').reverse();
								var gzExtn;
								var tarExtn;
								if (tokns.length > 2) {
									gzExtn = tokns[0];
									tarExtn = tokns[1];
									if ((gzExtn.toLowerCase() != 'gz' || tarExtn.toLowerCase() != 'tar') && (gzExtn.toLowerCase() != 'zip') && (gzExtn.toLowerCase() != 'xlsx') && (gzExtn.toLowerCase() != 'xls') && (gzExtn.toLowerCase() != 'xlsm')) {
										$scope.supportingDocValidationErrors.push('Invalid file type for library, only tar.gz/zip/xls/xlsx/xlsm file allowed.');
									}
								} else if((tokns[0].toLowerCase() != 'zip') && (tokns[0].toLowerCase() != 'xlsx') && (tokns[0].toLowerCase() != 'xls') && (tokns[0].toLowerCase() != 'xlsm')){
									$scope.supportingDocValidationErrors.push('Invalid file type for library, only tar.gz/zip/xls/xlsx/xlsm file allowed.');
								}

								if (angular.isUndefined(smp.modelLibrary.checksum) || smp.modelLibrary.checksum == '') {
									$scope.supportingDocValidationErrors.push('Checksum is mandatory');
								}
							}
							if (!angular.isUndefined(smp.modelLibrary.manifestFile) && smp.modelLibrary.manifestFile != '') {
								var index = smp.modelLibrary.manifestFile.name.lastIndexOf('.');
								var extension = smp.modelLibrary.manifestFile.name.substring(index);
								if (extension.toLowerCase() != '.csv') {
									$scope.supportingDocValidationErrors.push('Invalid File Type for Manifest, Only .csv File Allowed.');
								}
							}
						}

					}
				}

				if (!$scope.versionImportInProgress) {
					if (!$scope.pickExistingIODefn) {
						if (angular.isUndefined(smp.mapping.model.excel) || smp.mapping.model.excel == '') {
							$scope.supportingDocValidationErrors.push('Please Upload Definition');
						} else {
							var index = smp.mapping.model.excel.name.lastIndexOf('.');
							var extension = smp.mapping.model.excel.name.substring(index);
							if (!(extension.toLowerCase() == '.xlsx' || extension.toLowerCase() == '.xml')) {
								$scope.supportingDocValidationErrors.push('Invalid File Type for Definition, only .xlsx or .xml allowed');
							}
						}
					} else {
						if (angular.isUndefined(smp.mapping.model.documentationName) || smp.mapping.model.documentationName == '') {
							$scope.supportingDocValidationErrors.push('Please Upload Document');
						}
					}
				}
					
				if (!angular.isUndefined(smp.reportTemplateInfo.reportTemplate) && smp.reportTemplateInfo.reportTemplate != '' && !$scope.pickExistingReport) {
					var index = smp.reportTemplateInfo.reportTemplate.name.lastIndexOf('.');
					var extension = smp.reportTemplateInfo.reportTemplate.name.substring(index);
					if (extension.toLowerCase() != '.jrxml') {
						$scope.supportingDocValidationErrors.push('Invalid File Type for Report Template, Only .jrxml File Allowed.');
					}
				}
				
				if ($scope.supportingDocValidationErrors.length > 0) {
					$log.error('Validation Failed !');
					$scope.showErr = true;
					$scope.versionFailed = true;
					$scope.versionStatus = "Validation Failed";
					$scope.versionSubmitted = true;
					return false;
				}
				return true;
			};

			$scope.extractVersionPackage = function() {
				modelPublishService.extractVersionPackage($scope.versionImport.versionZipFile).then(function(responseData) {
					if (!responseData.error) {
						$scope.smp = responseData.response.versionMigrationWrapper.migrationAuditInfo.version;
						$scope.importedFileName = responseData.response.importFileName;
						$scope.migrationId = responseData.response.versionMigrationWrapper.migrationAuditInfo.id;

						$scope.smp.versionType = 'MAJOR';
						//$scope.smp.mapping.model.allowNull = true;
						$scope.smp.mapping.model.allowNull = responseData.response.versionMigrationWrapper.versionMigrationInfo.allowNull;

						if ($scope.activeTab == 'CreateNewVersion') {
							$scope.loadAllModelNamesByEnv();
						} else {
							// override version name and version description
							$scope.smp.name = '';
							$scope.smp.description = '';
						}

						if ($scope.smp.modelLibrary.programmingLanguage == null || $scope.smp.modelLibrary.programmingLanguage == '') {
							if ($scope.smp.modelLibrary.executionLanguage.indexOf('Matlab') > -1) {
								$scope.smp.modelLibrary.programmingLanguage = "Matlab";
							} else if ($scope.smp.modelLibrary.executionLanguage.indexOf('Excel') > -1) {
								$scope.smp.modelLibrary.programmingLanguage = "Excel";
							}else {
								$scope.smp.modelLibrary.programmingLanguage = "R";
							}
							$scope.smp.modelLibrary.executionLanguage = $scope.smp.modelLibrary.modelExecEnvName;
							$scope.loadLanguageVersions();
						}
						// display complete form
						$scope.showAdditionalArtifactsForm = true;
						$scope.versionImportInProgress = true;
					} else {
						alert("Failed to extract the version package.");
					}
				}, function(errorData) {
					alert("Failed to extract version package.")
				});

			};

			$scope.importVersion = function() {
				$log.info('Request received to import version');
				$scope.versionDetail.migrationId = $scope.migrationId;
				$scope.versionDetail.importFileName = $scope.importedFileName;
				$scope.versionDetail.description = $scope.smp.description;
				$scope.versionDetail.name = $scope.smp.name;
				$scope.versionDetail.versionDescription = $scope.smp.versionDescription;
				$scope.versionDetail.modelType = 'Online';
				$scope.txnId = '';
				if ($scope.smp.versionType == 'MINOR') {
					$scope.versionDetail.versionType = 'MINOR';
					$scope.versionDetail.majorVersion = $scope.smp.majorVersion;
				}

				modelPublishService.importVersion($scope.versionDetail).then(function(responseData) {
					if (!responseData.error) {
						$log.info('Version Imported Successfully.');
						$scope.versionStatus = "Saved";
						$scope.versionSaved = true;
						//UMG-4337
						$scope.openOutput = true;
					} else {
						$log.error('Failed to Impoort Version.');
						$scope.supportingDocValidationErrors = responseData.response;
						$scope.versionFailed = true;
						$scope.versionStatus = "Validation Failed";
						$scope.showErr = true;
						$scope.openOutput = true;
						$scope.isJson = true;
					}
					$scope.versionSubmitted = true;
				}, function(errorData) {
					$log.error('Internal Server Error.' + errorData);
					$scope.modelError = {};
					$scope.modelError.step = "globalError";
					if (errorData != null && errorData.errorCode != null) {
						$scope.modelError.errorCode = errorData.errorCode;
						$scope.modelError.errorMsg = errorData.message;
					} else if (errorData != null) {
						$scope.modelError.errorMsg = errorData;
					} else {
						$scope.modelError.errorMsg = "Communication failure.";
					}
					$scope.supportingDocValidationErrors.push($scope.modelError.errorMsg);
					$scope.showErr = true;
					$scope.versionSubmitted = true;
					$scope.versionFailed = true;
					$scope.versionStatus = "Validation Failed";
					$scope.isJson = true;
				});

			};

			$scope.loadAllModelNames = function() {
				modelPublishService.loadAllModelNames().then(function(responseData) {
					if (!responseData.error) {
						$scope.uniqueModelNames = responseData.response;
					} else {
						alert("Failed to load unique version names.");
					}
				}, function(errorData) {
					alert("Failed to load unique version names.")
				});
			};

			$scope.loadAllModelNamesByEnv = function() {
				modelPublishService.loadAllModelNamesByEnv($scope.smp.modelLibrary.modelExecEnvName).then(function(responseData) {
					if (!responseData.error) {
						$scope.uniqueModelNames = responseData.response;
					} else {
						alert("Failed to load unique version names by environment.");
					}
				}, function(errorData) {
					alert("Failed to load unique version names by environment.")
				});
			};

			$scope.loadAllMajorVersionNumbersForModel = function() {
				$log.info('Setting Existing Versions for ' + $scope.smp.name);
				modelPublishService.getExistingVersions($scope.smp.name).then(function(responseData) {
					$scope.majorVersionsForModel = responseData.response;
				}, function(errorData) {
					$log.error('Failed to Set Existing Versions.');
				});
			};

			$scope.setFilteredLibraries = function(searchText) {
				$scope.pageInfo = {
					searchText : searchText,
					fromDate : '',
					toDate : '',
					pageSize : 50,
					page : 0,
					sortColumn : 'createdDate',
					descending : true
				};

				modelPublishService.getFilteredLibraries($scope.pageInfo, $scope.smp.modelLibrary.executionLanguage).then(
						function(responseData) {
							if (!responseData.error) {
								$scope.modelApiDetails = buildModelApiDetails(responseData.response);
								$('#searchLib').focus();
							}
						}, function(errorData) {
							$scope.modelApiDetails = [];
							$log.error('Error');
						});
			};

			$scope.setFilteredDefinitions = function(searchText) {
				$scope.pageInfo = {
					searchText : searchText,
					fromDate : '',
					toDate : '',
					pageSize : 50,
					page : 0,
					sortColumn : 'createdDate',
					descending : true
				};
				modelPublishService.getFilteredDefinitions($scope.pageInfo, $scope.smp.modelLibrary.executionLanguage,$scope.smp.modelType).then(
						function(responseData) {
							if (!responseData.error) {
								$scope.modelApiDetails = buildModelApiDetails(responseData.response);
								$('#searchDef').focus();
							}
						}, function(errorData) {
							$scope.modelApiDetails = [];
							$log.error('Error');
						});
			};

			$scope.modelDefGrid = {
				data : 'modelApiDetails',
				enableRowSelection : true,
				multiSelect : false,
				selectedItems : $scope.pickedDef,
				afterSelectionChange : function(rowItem, event) {
					$scope.smp.mapping.model.id = rowItem.entity.modelId;
					$scope.smp.mapping.model.ioDefinitionName = rowItem.entity.ioDefinitionName;
				},
				columnDefs : [ {
					field : 'name',
					displayName : 'Model API Name'
				}, {
					field : 'version',
					displayName : 'Version'
				},
				{
					field : 'executionLanguage',
					displayName : 'Programming Language'
				},{
					field : 'modelType',
					displayName : 'ModelType'
				},{
					field : 'ioDefinitionName',
					displayName : 'IO Definition'
				}, {
					field : 'status',
					displayName : 'Status'
				}, {
					field : 'createdBY',
					displayName : 'Created By'
				}, {
					field : 'createdDate',
					displayName : 'Created Date'
				} ]
			};

			$scope.modelLibGrid = {
				data : 'modelApiDetails',
				enableRowSelection : true,
				multiSelect : false,
				selectedItems : $scope.pickedLib,
				afterSelectionChange : function(rowItem, event) {
					$scope.smp.modelLibrary.id = rowItem.entity.modelLibId;
					$scope.smp.modelLibrary.jarName = rowItem.entity.jarName;
					$scope.smp.modelLibrary.rmanifestFileName = rowItem.entity.manifestFileName;
					$scope.smp.modelLibrary.checksum = rowItem.entity.checksum;
				},
				columnDefs : [ {
					field : 'name',
					displayName : 'Model API Name'
				}, {
					field : 'version',
					displayName : 'Version'
				}, 
				{
					field : 'executionLanguage',
					displayName : 'Programming Language'
				}, 
				
				{
					field : 'jarName',
					displayName : 'Library Name'
				},
				 {
					field : 'execEnv',
					displayName : 'Execution Environment'
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

			var buildModelApiDetails = function(response) {
				var modelAPIDetails = [];
				angular.forEach(response, function(smp) {
					var model_id = '';
					var definition_name = '';
					var model_lib_id = '';
					var jar_name = '';
					var manifest_file_name = '';
					var checksum = '';
					var report_name = '';
					var report_id = '';
					var execEnv = '';

					if (smp.mapping != null) {
						model_id = smp.mapping.model.id;
						definition_name = smp.mapping.model.ioDefinitionName;
					}

					if (smp.modelLibrary != null) {
						model_lib_id = smp.modelLibrary.id;
						jar_name = smp.modelLibrary.jarName;
						execEnv = smp.modelLibrary.execEnv;
						manifest_file_name = smp.modelLibrary.rmanifestFileName;
						checksum = smp.modelLibrary.checksum;
					}
					if (smp.reportTemplateInfo != null) {
						report_name = smp.reportTemplateInfo.name;		
						report_id=smp.reportTemplateInfo.id;
					}

					var mad = {
						id : '',
						name : smp.name,
						version : smp.majorVersion + '.' + smp.minorVersion,
						status : smp.status,
						modelId : model_id,
						ioDefinitionName : definition_name,
						modelLibId : model_lib_id,
						jarName : jar_name,
						execEnv : execEnv,
						manifestFileName : manifest_file_name,
						createdBY : smp.createdBy,
						createdDate : smp.createdDateTime,
						checksum : checksum,
						modelType : smp.modelType,
						reportName : report_name,
						reportId : report_id,
						executionLanguage : smp.executionLanguage
					};
					modelAPIDetails.push(mad);
				});

				return modelAPIDetails;
			};

			$scope.$watch('srchDefTxt', function(newVal, oldVal) {
				if (newVal == '')
					setModelApiDetails($scope.smp.modelType);
			}, true);

			$scope.$watch('srchLibTxt', function(newVal, oldVal) {
				if (newVal == '')
					setModelApiDetails('All');
			}, true);
			
			$scope.$watch('srchReportTxt', function(newVal, oldVal) {
				if (newVal == '')
					setModelReportDetails('All');
			}, true);

			var setModelApiDetails = function(modelType) {
				modelPublishService.getModelApiDetails($scope.smp.modelLibrary.executionLanguage, modelType).then(function(responseData) {
					if (!responseData.error) {
						$scope.modelApiDetails = buildModelApiDetails(responseData.response);
					}
				}, function(errorData) {
					$scope.modelApiDetails = [];
				});
			};
			var setModelReportDetails = function(modelType) {
				modelPublishService.getModelReportDetails($scope.smp.modelLibrary.executionLanguage, modelType).then(function(responseData) {
					if (!responseData.error) {
						$scope.modelApiDetails = buildModelApiDetails(responseData.response);
					}
				}, function(errorData) {
					$scope.modelApiDetails = [];
				});
			};

			$scope.launchExistingLibDialog = function() {
				$scope.smp.modelLibrary.jar = '';
				$scope.smp.modelLibrary.checksum = '';
				
				modelPublishService.getModelApiDetails($scope.smp.modelLibrary.executionLanguage, 'All').then(function(responseData) {
					if (!responseData.error) {
						$scope.pickExistingLib = true;
						$scope.modelApiDetails = buildModelApiDetails(responseData.response);
						/*setTimeout(function() {
		            		  $scope.modelLibGrid.$gridServices.DomUtilityService.RebuildGrid(
		            				  $scope.modelLibGrid.$gridScope,
		            				  $scope.modelLibGrid.ngGrid
		            		  );
		            	  },0);*/
						setTimeout(function(){
				            $(window).resize();
				            $(window).resize();
				        }, 0);
					}
				}, function(errorData) {
					$scope.modelApiDetails = [];
				});
			};

			$scope.resetPickLibFlag = function() {
				$('#matlab_jar_upload_btn')[0].value='';
				$('#r_tar_upload_btn')[0].value='';
				$('#excel_upload_btn')[0].value='';
				$('#r_manifest_upload_btn')[0].value = '';
				$scope.smp.modelLibrary.jar = null;
				$scope.smp.modelLibrary.jarName = '';
				$scope.pickExistingLib = false;
			};
			

			$scope.resetPickIODefn = function() {
				$('#io_defn_upload_btn').val(null);
				$scope.smp.mapping.model.excel = '';
				$scope.pickExistingIODefn = false;
				$scope.smp.mapping.model.ioDefinitionName = '';
			};

			$scope.resetReleaseNotes = function(){
				$('#model_release_notes_btn')[0].value='';
				//$('#model_release_notes_btn').val(null);
			};
			
			$scope.resetR_tar_upload_btn = function(){
				$('#r_tar_upload_btn')[0].value='';
			};
			
			$scope.resetImportVersion = function(){
				$('#version_zip_browse')[0].value='';
			};
			
			$scope.resetMatlab_jar_upload_btn = function(){
				$('#matlab_jar_upload_btn')[0].value='';
			};
			
			
			$scope.resetExcel_upload_btn = function(){
				$('#excel_upload_btn')[0].value='';
			};
			$scope.resetR_manifest_upload_btn = function(){
				$('#r_manifest_upload_btn')[0].value='';
			};
			
			$scope.resetIo_defn_upload_btn = function(){
				$('#io_defn_upload_btn')[0].value='';
			};
			
			$scope.resetReportTemplate_upload_btn = function(){
				$('#reportTemplate_upload_btn')[0].value='';
			};
			
			$scope.launchExistingReportTempDialog = function() {
				$scope.smp.reportTemplateInfo.reportTemplate = '';
				modelPublishService.getModelApiDetails($scope.smp.modelLibrary.executionLanguage, $scope.smp.modelType).then(function(responseData) {
					if (!responseData.error) {
						$scope.pickExistingReport = true;
						$scope.modelApiDetails = buildModelApiDetails(responseData.response);
						/*setTimeout(function() {
		            		  $scope.modelDefGrid.$gridServices.DomUtilityService.RebuildGrid(
		            				  $scope.modelDefGrid.$gridScope,
		            				  $scope.modelDefGrid.ngGrid
		            		  );
		            	  },2000);*/
						setTimeout(function(){
				            $(window).resize();
				            $(window).resize();
				        }, 0);
					}
				}, function(errorData) {
					$scope.modelApiDetails = [];
				});
			};			
		
			
			$scope.launchExistingIODefnDialog = function() {
				$scope.smp.mapping.model.excel = '';
				modelPublishService.getModelApiDetails($scope.smp.modelLibrary.executionLanguage, $scope.smp.modelType).then(function(responseData) {
					if (!responseData.error) {
						$scope.pickExistingIODefn = true;
						$scope.modelApiDetails = buildModelApiDetails(responseData.response);
						/*setTimeout(function() {
		            		  $scope.modelDefGrid.$gridServices.DomUtilityService.RebuildGrid(
		            				  $scope.modelDefGrid.$gridScope,
		            				  $scope.modelDefGrid.ngGrid
		            		  );
		            	  },2000);*/
						setTimeout(function(){
				            $(window).resize();
				            $(window).resize();
				        }, 0);
					}
				}, function(errorData) {
					$scope.modelApiDetails = [];
				});
			};

			$scope.resetLaunchExistingIODefn = function(clrSelection) {
				if (clrSelection) {
					$scope.smp.mapping.model.ioDefinitionName = '';
				}
				if ($scope.smp.mapping.model.ioDefinitionName == null || $scope.smp.mapping.model.ioDefinitionName == '') {
					$scope.pickExistingIODefn = false;
				}
				$scope.srchDefTxt = '';
			};

			$scope.resetManifestFile = function(){
				$('#r_manifest_upload_btn')[0].value = '';
				$scope.smp.modelLibrary.manifestFile = '';
			};
		
			
			$scope.resetpickExistingLibFlag = function(clrSelection) {
				if (clrSelection) {
					$scope.smp.modelLibrary.jarName = '';
				}
				if ($scope.smp.modelLibrary.jarName == null || $scope.smp.modelLibrary.jarName == '') {
					$scope.pickExistingLib = false;
				}
				$scope.srchLibTxt = '';
			};

			/** Sets up all the model variables by invoking different methods required for the page * */
			$scope.intialSetup = function() {
				$scope.languageVersionMap = {};
				modelPublishService.getEnvironments().then(function(responseData) {
					if (!responseData.error) {
						$scope.languageVersionMap = responseData.response;
						setTimeout(function(){
				            $(window).resize();
				            $(window).resize();
				        }, 0);
					}
				}, function(errorData) {
					$scope.languageVersionMap = {};
				});
				$scope.setProgrammingLanguages();
//				modelPublishService.getWebsocketURL().then(function(responseData) {
//					if (!responseData.error) {
//						$scope.websocketBaseURL = responseData.response;
//					}
//				}, function(errorData) {
//					$scope.websocketBaseURL = '';
//				});
//				$scope.showStatusBlock = true;
				$scope.showManifestFileStatus = false;
				$scope.showReportTemplateStatus = false;
			};
			
			$scope.decideManifestStatus = function() {
				$scope.showManifestFileStatus = !(($scope.smp.modelLibrary.manifestFile.name == null || $scope.smp.modelLibrary.manifestFile.name == '') && ($scope.smp.modelLibrary.rmanifestFileName == null || $scope.smp.modelLibrary.rmanifestFileName == ''));
			};
			
			$scope.decideReportStatus = function() {
				$scope.showReportTemplateStatus = !($scope.smp.reportTemplateInfo.reportTemplate == null || $scope.smp.reportTemplateInfo.reportTemplate == '');
			};

			$scope.intialSetup();
			
			$scope.launchExistingReportDialog = function() {
				$scope.smp.reportTemplateInfo.id = '';
				$scope.smp.reportTemplateInfo.reportTemplate = '';
				
				modelPublishService.getModelReportDetails($scope.smp.modelLibrary.executionLanguage, 'All').then(function(responseData) {
					if (!responseData.error) {
						$scope.pickExistingReport = true;
						$scope.modelApiDetails = buildModelApiDetails(responseData.response);
						/*setTimeout(function() {
		            		  $scope.modelLibGrid.$gridServices.DomUtilityService.RebuildGrid(
		            				  $scope.modelLibGrid.$gridScope,
		            				  $scope.modelLibGrid.ngGrid
		            		  );
		            	  },0);*/
						setTimeout(function(){
				            $(window).resize();
				            $(window).resize();
				        }, 0);
					}
				}, function(errorData) {
					$scope.modelApiDetails = [];
				});
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
					},{
						field : 'executionLanguage',
						displayName : 'Programming Language'
					}, 
					{
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

					modelPublishService.getFilteredReport($scope.pageInfo, $scope.smp.modelLibrary.executionLanguage).then(
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

		} ];
