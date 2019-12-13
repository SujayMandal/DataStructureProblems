'use strict';

var ModeletProfilingCtrl = ['$scope', '$log', 'modeletProfilingService', function ($scope, $log, modeletProfilingService) {

	$scope.showEditPopup = false;
	$scope.showNewPopup = false;
	$scope.deleteModal = false;
	$scope.platformList = ['Linux', 'Windows'];
	$scope.logLevelList = ['error', 'info', 'debug'];
	$scope.message = '';

	$scope.openNewModelAdd = function () {
		$scope.showMessage('', '', null);
		$log.info("Request received to add new Modelet Profile.");
		$scope.envList = [];
		angular.forEach($scope.executionEnvObject.response, function (exEnv) {
			$scope.envList.push(exEnv);
		});
		$scope.showNewPopup = true;
		$scope.saveErrorMsg = [];
		$scope.newModelet = angular.copy($scope.defaultProfiler);
	};

	$scope.closeNewModal = function () {
		$scope.showMessage('', '', null);
		$scope.newModelet = angular.copy($scope.defaultProfiler);
	};

	$scope.showMessage = function (content, cl, responseMessage) {
		$scope.message = (responseMessage != null && responseMessage != '') ? responseMessage : content;
		$scope.clazz = cl;
	};

	$scope.deleteProfileModal = function (profile) {
		$scope.currentProfile = profile;
		$scope.profileName = profile.name;
		$log.info("Request received to delete Modelet Profile.");
		$scope.showMessage('', '', null);
		$scope.deleteModal = true;
	}

	$scope.deleteProfile = function (id) {
		console.log(" Request received to delete the modelet :" + id);
		$scope.showMessage('', '', null);
		modeletProfilingService.deleteModelet(id).then(
			function (responseData) {
				$scope.init();
				if (!responseData.error) {
					console.log(responseData);
					console.log("data deleted ", +id);
					$scope.showMessage('Modelet profile deleted successfully.', 'text-left text-success', responseData.response);
				} else {
					$scope.showMessage('Modelet profile deletion failed!', 'text-left text-danger', responseData.message);
				}
			}, function (errorData) {
				$scope.init();
				$log.error(errorData);
				$scope.showMessage(errorData, 'text-left text-danger', null);
			}

		);
		//$scope.init();
		//window.location.reload();

	}

	$scope.showModeletProfileEditnPopup = function (profileID) {
		$log.info("Request received to update Modelet Parameter.");
		$scope.showMessage('', '', null);
		$scope.showEditPopup = true;
		$scope.profID = profileID;
		$scope.envList = [];
		modeletProfilingService.getProfileDetails(profileID).then(
			function (responseData) {
				console.log("entered 2nd method");
				if (!responseData.error) {
					console.log(responseData);
					$scope.profileParams = '';
					$scope.profileParams = responseData;
					$scope.paramInfo = '';
					$scope.paramInfo = $scope.profileParams.response;
					$scope.paramInfo.params['X:+UseConcMarkSweepGC'] = $scope.paramInfo.params['X:+UseConcMarkSweepGC'] ? ($scope.paramInfo.params['X:+UseConcMarkSweepGC'].toLocaleString().toLowerCase() == 'true' ? true : false) : false;
					$scope.paramInfo.params['X:+UseParNewGC'] = $scope.paramInfo.params['X:+UseParNewGC'] ? ($scope.paramInfo.params['X:+UseParNewGC'].toLocaleString().toLowerCase() == 'true' ? true : false) : false;
					$scope.paramInfo.params['X:+UseCMSInitiatingOccupancyOnly'] = $scope.paramInfo.params['X:+UseCMSInitiatingOccupancyOnly'] ? ($scope.paramInfo.params['X:+UseCMSInitiatingOccupancyOnly'].toLocaleString().toLowerCase() == 'true' ? true : false) : false;
					angular.forEach($scope.executionEnvObject.response, function (exEnv) {
						if (exEnv.executionEnvironment == $scope.paramInfo.executionEnvironment) {
							$scope.envList.push(exEnv);
						}
					});
					console.log("profile data ", $scope.paramInfo);
				}
			}, function (errorData) {
				$log.error(errorData);
			}
		);
	};

	$scope.validateProfilEdit = function(){
		$scope.editErrorMsg = [];
		$scope.editErrorMsg.params = [];
		$scope.editErrorMsg.test = false;
		const pattern1 =  new RegExp('^[0-9]+[m|g]{1}$');
		const pattern2 =  new RegExp('^[0-9]+$');
		const pattern3 =  new RegExp('^(error|info|debug)$');

		//validation code
		if ($scope.paramInfo.name == undefined || $scope.paramInfo.name.length == 0) {
			$scope.editErrorMsg.name = "Profile Name is required.";
			$scope.editErrorMsg.test = true;
		} else if ($scope.paramInfo.name.length > 20) {
			$scope.editErrorMsg.name = "Profile Name cannot be more than 20 characters.";
			$scope.editErrorMsg.test = true;
		}
		if ($scope.paramInfo.description == undefined || $scope.paramInfo.description.length == 0) {
			$scope.editErrorMsg.description = "Description is required.";
			$scope.editErrorMsg.test = true;
		}
		if ($scope.paramInfo.params.workspace == undefined || $scope.paramInfo.params.workspace.length == 0) {
			$scope.editErrorMsg.params.workspace = "Workspace is required.";
			$scope.editErrorMsg.test = true;
		} else if ($scope.paramInfo.params.workspace.length > 1500) {
			$scope.editErrorMsg.params.workspace = "Workspace cannot be more than 1500 characters.";
			$scope.editErrorMsg.test = true;
		}
		if ($scope.paramInfo.params['X:MaxPermSize'] == undefined || $scope.paramInfo.params['X:MaxPermSize'].length == 0) {
			$scope.editErrorMsg.params['X:MaxPermSize'] = "Max perm size is required.";
			$scope.editErrorMsg.test = true;
		} else if ($scope.paramInfo.params['X:MaxPermSize'].length > 15) {
			$scope.editErrorMsg.params['X:MaxPermSize'] = "Max perm size cannot be more than 15 characters.";
			$scope.editErrorMsg.test = true;
		} else if(!pattern1.test($scope.paramInfo.params['X:MaxPermSize'])) {
			$scope.editErrorMsg.params['X:MaxPermSize'] = "Max perm size should be in number(g|m) format.";
			$scope.editErrorMsg.test = true;
		}

		if ($scope.paramInfo.params['X:MaxHeapFreeRatio'] == undefined || $scope.paramInfo.params['X:MaxHeapFreeRatio'].length == 0) {
			$scope.editErrorMsg.params['X:MaxHeapFreeRatio'] = "Max Heap free ratio is required.";
			$scope.editErrorMsg.test = true;
		} else if ($scope.paramInfo.params['X:MaxHeapFreeRatio'].length > 4) {
			$scope.editErrorMsg.params['X:MaxHeapFreeRatio'] = "Max Heap free ratio cannot be more than 4 characters.";
			$scope.editErrorMsg.test = true;
		} else if(!pattern2.test($scope.paramInfo.params['X:MaxHeapFreeRatio'])) {
			$scope.editErrorMsg.params['X:MaxHeapFreeRatio'] = "Max Heap free ratio should be number only.";
			$scope.editErrorMsg.test = true;
		} else if($scope.paramInfo.params['X:MaxHeapFreeRatio'] > 100) {
			$scope.editErrorMsg.params['X:MaxHeapFreeRatio'] = "Max Heap free ratio should be less then or equal to 100.";
			$scope.editErrorMsg.test = true;
		}

		if ($scope.paramInfo.params['mx'] == undefined || $scope.paramInfo.params['mx'].length == 0) {
			$scope.editErrorMsg.params['mx'] = "Max Memory is required.";
			$scope.editErrorMsg.test = true;
		} else if ($scope.paramInfo.params['mx'].length > 15) {
			$scope.editErrorMsg.params['mx'] = "Max Memory cannot be more than 15 characters.";
			$scope.editErrorMsg.test = true;
		} else if(!pattern1.test($scope.paramInfo.params['mx'])) {
			$scope.editErrorMsg.params['mx'] = "Max Memory sould be in number(g|m) format.";
			$scope.editErrorMsg.test = true;
		}

		if ($scope.paramInfo.params['ms'] == undefined || $scope.paramInfo.params['ms'].length == 0) {
			$scope.editErrorMsg.params['ms'] = "Minimum Memory is required.";
			$scope.editErrorMsg.test = true;
		} else if ($scope.paramInfo.params['ms'].length > 15) {
			$scope.editErrorMsg.params['ms'] = "Minimum Memory cannot be more than 15 characters.";
			$scope.editErrorMsg.test = true;
		} else if(!pattern1.test($scope.paramInfo.params['ms'])) {
			$scope.editErrorMsg.params['ms'] = "Minimum Memory sould be in number(g|m) format.";
			$scope.editErrorMsg.test = true;
		}

		if ($scope.paramInfo.params.LD_LIBRARY_PATH == undefined || $scope.paramInfo.params.LD_LIBRARY_PATH.length == 0) {
			$scope.editErrorMsg.params.LD_LIBRARY_PATH = "LD Library Path is required.";
			$scope.editErrorMsg.test = true;
		} else if ($scope.paramInfo.params.LD_LIBRARY_PATH.length > 1500) {
			$scope.editErrorMsg.params.LD_LIBRARY_PATH = "LD Library Path cannot be more than 1500 characters.";
			$scope.editErrorMsg.test = true;
		}

		if ($scope.paramInfo.params['hazelcast.config'] == undefined || $scope.paramInfo.params['hazelcast.config'].length == 0) {
			$scope.editErrorMsg.params['hazelcast.config'] = "Hazelcast Config is required.";
			$scope.editErrorMsg.test = true;
		} else if ($scope.paramInfo.params['hazelcast.config'].length > 1500) {
			$scope.editErrorMsg.params['hazelcast.config'] = "Hazelcast Config cannot be more than 1500 characters.";
			$scope.editErrorMsg.test = true;
		}

		if ($scope.paramInfo.params.JAVA_HOME == undefined || $scope.paramInfo.params.JAVA_HOME.length == 0) {
			$scope.editErrorMsg.params.JAVA_HOME = "JAVA Home is required.";
			$scope.editErrorMsg.test = true;
		} else if ($scope.paramInfo.params.JAVA_HOME.length > 1500) {
			$scope.editErrorMsg.params.JAVA_HOME = "JAVA Home cannot be more than 1500 characters.";
			$scope.editErrorMsg.test = true;
		}

		if ($scope.paramInfo.params.R_HOME == undefined || $scope.paramInfo.params.R_HOME.length == 0) {
			$scope.editErrorMsg.params.R_HOME = "R Home is required.";
			$scope.editErrorMsg.test = true;
		} else if ($scope.paramInfo.params.R_HOME.length > 1500) {
			$scope.editErrorMsg.params.R_HOME = "R Home cannot be more than 1500 characters.";
			$scope.editErrorMsg.test = true;
		}

		if ($scope.paramInfo.params.loglevel == undefined || $scope.paramInfo.params.loglevel.length == 0) {
			$scope.editErrorMsg.params.loglevel = "Log Level is required.";
			$scope.editErrorMsg.test = true;
		} else if ($scope.paramInfo.params.loglevel.length > 15) {
			$scope.editErrorMsg.params.loglevel = "Log Level cannot be more than 15 characters.";
			$scope.editErrorMsg.test = true;
		} else if(!pattern3.test($scope.paramInfo.params.loglevel)) {
			$scope.editErrorMsg.params.loglevel = "Log Level should be error, info or debug.";
			$scope.editErrorMsg.test = true;
		}

		if ($scope.paramInfo.executionEnvironmentId == undefined || $scope.paramInfo.executionEnvironmentId.length == 0) {
			$scope.editErrorMsg.executionEnvironmentId = "Execution Environment is required.";
			$scope.editErrorMsg.test = true;
		}

		if ($scope.paramInfo.params['X:CMSInitiatingOccupancyFraction'] == undefined || $scope.paramInfo.params['X:CMSInitiatingOccupancyFraction'].length == 0) {
			$scope.editErrorMsg.params['X:CMSInitiatingOccupancyFraction'] = "CMS Initiating occupancy is required.";
			$scope.editErrorMsg.test = true;
		} else if ($scope.paramInfo.params['X:CMSInitiatingOccupancyFraction'].length > 5) {
			$scope.editErrorMsg.params['X:CMSInitiatingOccupancyFraction'] = "CMS Initiating occupancy cannot be more than 5 characters.";
			$scope.editErrorMsg.test = true;
		} else if(!pattern2.test($scope.paramInfo.params['X:CMSInitiatingOccupancyFraction'])) {
			$scope.editErrorMsg.params['X:CMSInitiatingOccupancyFraction'] = "CMS Initiating occupancy should be number only.";
			$scope.editErrorMsg.test = true;
		}

		if ($scope.paramInfo.params['httpConnectionPooling.properties'] == undefined || $scope.paramInfo.params['httpConnectionPooling.properties'].length == 0) {
			$scope.editErrorMsg.params['httpConnectionPooling.properties'] = "Http Connection Pool Properties is required.";
			$scope.editErrorMsg.test = true;
		} else if ($scope.paramInfo.params['httpConnectionPooling.properties'].length > 1500) {
			$scope.editErrorMsg.params['httpConnectionPooling.properties'] = "Http Connection Pool Properties cannot be more than 1500 characters.";
			$scope.editErrorMsg.test = true;
		}

		if ($scope.paramInfo.params.executionEnvironment == undefined || $scope.paramInfo.params.executionEnvironment.length == 0) {
			$scope.editErrorMsg.params.executionEnvironment = "Execution Platform is required.";
			$scope.editErrorMsg.test = true;
		}
	}

	$scope.updateModProfileParam = function () {
		console.log(" params :" + $scope.paramInfo);
		if ($scope.paramInfo == undefined) {
			$scope.paramInfo = {};
			$scope.paramInfo.params = {};
		}
		$scope.validateProfilEdit();
		if (!$scope.editErrorMsg.test) {

			//$('#myModal').modal('hide');
			modeletProfilingService.updateProfile($scope.paramInfo).then(
				function (responseData) {
					$scope.init();
					if (!responseData.error) {
						$log.info('Modelet Updated Successfully.');
						$scope.showMessage('Modelet Updated Successfully.', 'text-left text-success', responseData.response);
					} else {
						$scope.showMessage('Modelet Updation Failed !', 'text-left text-danger', responseData.message);
					}
				},
				function (errorData) {
					$scope.init();
					$log.error("Error came with : " + errorData);
					$scope.showMessage(errorData, 'text-danger', null);
				}
			);
		}

		//window.location.reload();
	};

	$scope.validateProfilSave = function() {
		$scope.saveErrorMsg = [];
		$scope.saveErrorMsg.params = [];
		$scope.saveErrorMsg.test = false;
		const pattern1 =  new RegExp('^[0-9]+[m|g]{1}$');
		const pattern2 =  new RegExp('^[0-9]+$');
		const pattern3 =  new RegExp('^(error|info|debug)$');

		//validation code
		if ($scope.newModelet.name == undefined || $scope.newModelet.name.length == 0) {
			$scope.saveErrorMsg.name = "Profile Name is required.";
			$scope.saveErrorMsg.test = true;
		} else if ($scope.newModelet.name.length > 20) {
			$scope.saveErrorMsg.name = "Profile Name cannot be more than 20 characters.";
			$scope.saveErrorMsg.test = true;
		}
		if ($scope.newModelet.description == undefined || $scope.newModelet.description.length == 0) {
			$scope.saveErrorMsg.description = "Description is required.";
			$scope.saveErrorMsg.test = true;
		}
		if ($scope.newModelet.params.workspace == undefined || $scope.newModelet.params.workspace.length == 0) {
			$scope.saveErrorMsg.params.workspace = "Workspace is required.";
			$scope.saveErrorMsg.test = true;
		} else if ($scope.newModelet.params.workspace.length > 1500) {
			$scope.saveErrorMsg.params.workspace = "Workspace cannot be more than 1500 characters.";
			$scope.saveErrorMsg.test = true;
		}
		if ($scope.newModelet.params['X:MaxPermSize'] == undefined || $scope.newModelet.params['X:MaxPermSize'].length == 0) {
			$scope.saveErrorMsg.params['X:MaxPermSize'] = "Max perm size is required.";
			$scope.saveErrorMsg.test = true;
		} else if ($scope.newModelet.params['X:MaxPermSize'].length > 15) {
			$scope.saveErrorMsg.params['X:MaxPermSize'] = "Max perm size cannot be more than 15 characters.";
			$scope.saveErrorMsg.test = true;
		} else if(!pattern1.test($scope.newModelet.params['X:MaxPermSize'])) {
			$scope.saveErrorMsg.params['X:MaxPermSize'] = "Max perm size should be in number(g|m) format.";
			$scope.saveErrorMsg.test = true;
		}

		if ($scope.newModelet.params['X:MaxHeapFreeRatio'] == undefined || $scope.newModelet.params['X:MaxHeapFreeRatio'].length == 0) {
			$scope.saveErrorMsg.params['X:MaxHeapFreeRatio'] = "Max Heap free ratio is required.";
			$scope.saveErrorMsg.test = true;
		} else if ($scope.newModelet.params['X:MaxHeapFreeRatio'].length > 4) {
			$scope.saveErrorMsg.params['X:MaxHeapFreeRatio'] = "Max Heap free ratio cannot be more than 4 characters.";
			$scope.saveErrorMsg.test = true;
		} else if(!pattern2.test($scope.newModelet.params['X:MaxHeapFreeRatio'])) {
			$scope.saveErrorMsg.params['X:MaxHeapFreeRatio'] = "Max Heap free ratio should be number only.";
			$scope.saveErrorMsg.test = true;
		}

		if ($scope.newModelet.params['mx'] == undefined || $scope.newModelet.params['mx'].length == 0) {
			$scope.saveErrorMsg.params['mx'] = "Max Memory is required.";
			$scope.saveErrorMsg.test = true;
		} else if ($scope.newModelet.params['mx'].length > 15) {
			$scope.saveErrorMsg.params['mx'] = "Max Memory cannot be more than 15 characters.";
			$scope.saveErrorMsg.test = true;
		} else if(!pattern1.test($scope.newModelet.params['mx'])) {
			$scope.saveErrorMsg.params['mx'] = "Max Memory sould be in number(g|m) format.";
			$scope.saveErrorMsg.test = true;
		}

		if ($scope.newModelet.params['ms'] == undefined || $scope.newModelet.params['ms'].length == 0) {
			$scope.saveErrorMsg.params['ms'] = "Minimum Memory is required.";
			$scope.saveErrorMsg.test = true;
		} else if ($scope.newModelet.params['ms'].length > 15) {
			$scope.saveErrorMsg.params['ms'] = "Minimum Memory cannot be more than 15 characters.";
			$scope.saveErrorMsg.test = true;
		} else if(!pattern1.test($scope.newModelet.params['ms'])) {
			$scope.saveErrorMsg.params['ms'] = "Minimum Memory sould be in number(g|m) format.";
			$scope.saveErrorMsg.test = true;
		}

		if ($scope.newModelet.params.LD_LIBRARY_PATH == undefined || $scope.newModelet.params.LD_LIBRARY_PATH.length == 0) {
			$scope.saveErrorMsg.params.LD_LIBRARY_PATH = "LD Library Path is required.";
			$scope.saveErrorMsg.test = true;
		} else if ($scope.newModelet.params.LD_LIBRARY_PATH.length > 1500) {
			$scope.saveErrorMsg.params.LD_LIBRARY_PATH = "LD Library Path cannot be more than 1500 characters.";
			$scope.saveErrorMsg.test = true;
		}

		if ($scope.newModelet.params['hazelcast.config'] == undefined || $scope.newModelet.params['hazelcast.config'].length == 0) {
			$scope.saveErrorMsg.params['hazelcast.config'] = "Hazelcast Config is required.";
			$scope.saveErrorMsg.test = true;
		} else if ($scope.newModelet.params['hazelcast.config'].length > 1500) {
			$scope.saveErrorMsg.params['hazelcast.config'] = "Hazelcast Config cannot be more than 1500 characters.";
			$scope.saveErrorMsg.test = true;
		}

		if ($scope.newModelet.params.JAVA_HOME == undefined || $scope.newModelet.params.JAVA_HOME.length == 0) {
			$scope.saveErrorMsg.params.JAVA_HOME = "JAVA Home is required.";
			$scope.saveErrorMsg.test = true;
		} else if ($scope.newModelet.params.JAVA_HOME.length > 1500) {
			$scope.saveErrorMsg.params.JAVA_HOME = "JAVA Home cannot be more than 1500 characters.";
			$scope.saveErrorMsg.test = true;
		}

		if ($scope.newModelet.params.R_HOME == undefined || $scope.newModelet.params.R_HOME.length == 0) {
			$scope.saveErrorMsg.params.R_HOME = "R Home is required.";
			$scope.saveErrorMsg.test = true;
		} else if ($scope.newModelet.params.R_HOME.length > 1500) {
			$scope.saveErrorMsg.params.R_HOME = "R Home cannot be more than 1500 characters.";
			$scope.saveErrorMsg.test = true;
		}

		if ($scope.newModelet.params.loglevel == undefined || $scope.newModelet.params.loglevel.length == 0) {
			$scope.saveErrorMsg.params.loglevel = "Log Level is required.";
			$scope.saveErrorMsg.test = true;
		} else if ($scope.newModelet.params.loglevel.length > 15) {
			$scope.saveErrorMsg.params.loglevel = ">Log Level cannot be more than 15 characters.";
			$scope.saveErrorMsg.test = true;
		} else if(!pattern3.test($scope.newModelet.params.loglevel)) {
			$scope.saveErrorMsg.params.loglevel = "Log Level should be error, info or debug.";
			$scope.saveErrorMsg.test = true;
		}

		if ($scope.newModelet.executionEnvironmentId == undefined || $scope.newModelet.executionEnvironmentId.length == 0) {
			$scope.saveErrorMsg.executionEnvironmentId = "Execution Environment is required.";
			$scope.saveErrorMsg.test = true;
		}

		if ($scope.newModelet.params['X:CMSInitiatingOccupancyFraction'] == undefined || $scope.newModelet.params['X:CMSInitiatingOccupancyFraction'].length == 0) {
			$scope.saveErrorMsg.params['X:CMSInitiatingOccupancyFraction'] = "CMS Initiating occupancy is required.";
			$scope.saveErrorMsg.test = true;
		} else if ($scope.newModelet.params['X:CMSInitiatingOccupancyFraction'].length > 5) {
			$scope.saveErrorMsg.params['X:CMSInitiatingOccupancyFraction'] = "CMS Initiating occupancy cannot be more than 5 characters.";
			$scope.saveErrorMsg.test = true;
		} else if(!pattern2.test($scope.newModelet.params['X:CMSInitiatingOccupancyFraction'])) {
			$scope.saveErrorMsg.params['X:CMSInitiatingOccupancyFraction'] = "CMS Initiating occupancy should be number only.";
			$scope.saveErrorMsg.test = true;
		}

		if ($scope.newModelet.params['httpConnectionPooling.properties'] == undefined || $scope.newModelet.params['httpConnectionPooling.properties'].length == 0) {
			$scope.saveErrorMsg.params['httpConnectionPooling.properties'] = "Http Connection Pool Properties is required.";
			$scope.saveErrorMsg.test = true;
		} else if ($scope.newModelet.params['httpConnectionPooling.properties'].length > 1500) {
			$scope.saveErrorMsg.params['httpConnectionPooling.properties'] = "Http Connection Pool Properties cannot be more than 1500 characters.";
			$scope.saveErrorMsg.test = true;
		}

		if ($scope.newModelet.params.executionEnvironment == undefined || $scope.newModelet.params.executionEnvironment.length == 0) {
			$scope.saveErrorMsg.params.executionEnvironment = "Execution Platform is required.";
			$scope.saveErrorMsg.test = true;
		}
	}

	$scope.saveNewModeletProfile = function () {
		if ($scope.newModelet == undefined) {
			$scope.newModelet = angular.copy($scope.defaultProfiler);
		}
		if ($scope.newModelet.params == undefined) {
			$scope.newModelet.params = angular.copy($scope.defaultProfiler.params);
		}
		$scope.validateProfilSave();
		if (!$scope.saveErrorMsg.test) {
			if (!$scope.newModelet.params['X:+UseConcMarkSweepGC'])
				$scope.newModelet.params['X:+UseConcMarkSweepGC'] = false;
			if (!$scope.newModelet.params['X:+UseParNewGC'])
				$scope.newModelet.params['X:+UseParNewGC'] = false;
			if (!$scope.newModelet.params['X:+UseCMSInitiatingOccupancyOnly'])
				$scope.newModelet.params['X:+UseCMSInitiatingOccupancyOnly'] = false;
			modeletProfilingService.createNewProfile($scope.newModelet).then(
				function (responseData) {
					$scope.init();
					if (!responseData.error) {
						angular.copy($scope.defaultProfiler, $scope.newModelet);
						$log.info('Modelet profile created successfully.');
						$scope.showMessage('Modelet profile created Successfully.', 'text-left text-success', responseData.response);
					} else {
						$scope.showMessage('Modelet Profile creation Failed !', 'text-left text-danger', responseData.message);
						//$('#newModal').modal('hide');
					}
				},
				function (errorData) {
					$scope.init();
					$log.error("Error came with : " + errorData);
					$scope.showMessage(errorData, 'text-left text-danger', null);
					//$('#newModal').modal('hide');
				}
			);
			
			//$('#newModal').modal('hide');


			//window.location.reload();
		}


	};


	$scope.init = function () {
		$scope.showMessage('', '', null);
		modeletProfilingService.getProfileParameters().then(
			function (responseData) {
				if (!responseData.error) {
					console.log(responseData);
					$scope.profileData = '';
					$scope.dataParams = '';
					$scope.profileData = responseData;
					$scope.dataParams = $scope.profileData.response;
				}
			}, function (errorData) {
				$log.error(errorData);
			}

		);

		modeletProfilingService.getExecutionEnvList().then(
			function (responseData) {
				if (!responseData.error) {
					console.log(responseData);
					$scope.executionEnvObject = responseData;
					//$scope.envList='';
					//$scope.envList=$scope.executionEnvObject.response;
					$scope.envList = [];

					angular.forEach($scope.executionEnvObject.response, function (exEnv) {
						if (!$scope.showEditPopup) {
							$scope.envList.push(exEnv);
						} else if (exEnv.executionEnvironment == $scope.paramInfo.executionEnvironment) {
							$scope.envList.push(exEnv);
						}
					});

					/*angular.forEach($scope.executionEnvObject.response, function(exEnv){
						$scope.envList.push(exEnv);
					});*/
				}
			}, function (errorData) {
				$log.error(errorData);
			}
		);

		modeletProfilingService.populateDefaultProfilerData().then(
			function (responseData) {
				if (!responseData.error) {
					console.log(responseData);
					$scope.defaultProfiler = responseData.response;
					$scope.defaultProfiler.params['X:+UseConcMarkSweepGC'] = $scope.defaultProfiler.params['X:+UseConcMarkSweepGC'] ? ($scope.defaultProfiler.params['X:+UseConcMarkSweepGC'].toLocaleString().toLowerCase() == 'true' ? true : false) : false;
					$scope.defaultProfiler.params['X:+UseParNewGC'] = $scope.defaultProfiler.params['X:+UseParNewGC'] ? ($scope.defaultProfiler.params['X:+UseParNewGC'].toLocaleString().toLowerCase() == 'true' ? true : false) : false;
					$scope.defaultProfiler.params['X:+UseCMSInitiatingOccupancyOnly'] = $scope.defaultProfiler.params['X:+UseCMSInitiatingOccupancyOnly'] ? ($scope.defaultProfiler.params['X:+UseCMSInitiatingOccupancyOnly'].toLocaleString().toLowerCase() == 'true' ? true : false) : false;
					
					console.log($scope.defaultProfiler);
				}
			}, function (errorData) {
				$log.error(errorData);
			}
		);


	}

	$scope.init();



}];