'use strict';

var ModeletPoolingCtrl = ['$scope', '$log', '$dialogs', 'modeletPoolingService','sharedPropertiesService', function($scope, $log, $dialogs, modeletPoolingService,  sharedPropertiesService){
	
	$scope.showPoolCreationForm = false;
	
	$scope.modeletPools = [];
	$scope.modeletPoolsCopy = [];
	$scope.transactionTypes = [];
	$scope.transactionModes = [];
	$scope.channels = [];
	$scope.tenants = [];
	$scope.environments = [];
	$scope.executionEnvironments = [];
	$scope.modelNamesByTenantAndEnv = [];
	$scope.tenantSpecificModel = [];
	$scope.tenantSpecificModel.push("Any_Any");
	$scope.tempModelmodelArray = [];
	$scope.modeletServerCount = '';
	$scope.matlabModeletsCount = '';
	$scope.rModeletsCount = '';
	$scope.search_string = '';
	$scope.showErrorMessage = false;
	$scope.showSuccesssMessage = false;
	$scope.showFailureMessage = false;
	$scope.invalidMove = false;
	$scope.showMessage = '';
	$scope.dragObject = '';
	$scope.dropObject = '';
	$scope.openPopUp = false;
	$scope.modalVar = '';
	$scope.restartMapDetails = [];
	$scope.showRowCreationForm = false;
	$scope.tenantsWithoutAny = [];
	$scope.modelNamesByTenant = [];
	$scope.modelNamesBySelectedTenant = [];
	$scope.restartConfigRows = [];
	$scope.restartConfigRowsCopy = [];
	$scope.updateConfigRows = [];
	$scope.createRestartError = false;
	$scope.restartError = false;
	$scope.restartMessage = '';
	$scope.createRestartErrorMessage = '';
	$scope.newRow = {
			id: null,
			tenantId: '',
			modelNameAndVersion: '',
			restartCount: null
	};
	$scope.newPool = {
			pool : {
				poolName: '',
				poolDesc: '',
				defaultPool: 0,
				modeletCount: 0,
				inactiveModeletCount: 0,
				priority: 0,
				waitTimeout: null,
				modeletAdded : 0,
				modeletRemoved : 0
			},
			poolCriteriaDetails:{
				tenant: 'Any',
				executionLanguage: 'R',
				executionEnvironment: 'Linux',
				transactionType: 'Any',
				transactionMode: 'Any',
				model: 'Any_Any',
				channel: 'Any'
			},
			poolCriteria : null,
			poolUsageOrderMapping : null,
			modeletClientInfoList : null			
	};
	
	
	function setModeletPoolingDetails(){
		modeletPoolingService.getModeletPoolingDetails().then(
		
				function(responseData){
					$log.info('Received Modelet Pooling Details.');
					setPoolDetails();
					if(!responseData.error) {
						$scope.transactionTypes = responseData.response.transactionTypes;
						$scope.transactionModes = responseData.response.transactionModes;
						$scope.channels = responseData.response.channels;
						$scope.tenants = responseData.response.tenants;
						$scope.environments = responseData.response.environments;
						$scope.executionEnvironments = responseData.response.executionEnvironments;
						$scope.modelNamesByTenantAndEnv = responseData.response.modelNamesByTenantAndEnv;
						$scope.modeletServerCount = responseData.response.modeletServerCount;
						$scope.matlabModeletsCount = responseData.response.matlabModeletsCount;
						$scope.rModeletsCount = responseData.response.rModeletsCount;
					}
				},
				function(errorData){
					$log.error('Error while loading Modelet Pooling Details');
				}
		);
		
		
	};
	
	function setPoolDetails(){
		
		modeletPoolingService.getPoolDetails().then(
				function(responseData){
					$log.info('Received Pooling Details.');
					if(!responseData.error){
						$scope.modeletPools = [];
						$scope.modeletPoolsCopy = [];
						angular.forEach(responseData.response, function(poolDetail){
							
							if(poolDetail !=null && poolDetail.pool !=null && poolDetail.pool.waitTimeout != null) {
								poolDetail.pool.waitTimeout = poolDetail.pool.waitTimeout / 1000 ;
								$scope.selectInitialModel(poolDetail);
							}
							if(poolDetail !=null && poolDetail.modeletClientInfoList != null) {
								for (var i in poolDetail.modeletClientInfoList) {
									if (poolDetail.modeletClientInfoList[i].modeletStatus !=null && poolDetail.modeletClientInfoList[i].modeletStatus != 'Unavailable' && poolDetail.modeletClientInfoList[i].modeletStatus != 'Failed')
										poolDetail.modeletClientInfoList[i].switchMessage = "stop";
									else
										poolDetail.modeletClientInfoList[i].switchMessage = "start";
									poolDetail.modeletClientInfoList[i].stateSwitch = false;
								}
							}
							$scope.modeletPools.push(poolDetail);
							$scope.modeletPoolsCopy.push(JSON.parse(JSON.stringify(poolDetail)));
						});
					}
					
					$scope.pools = responseData.response;
				},
				function(errorData){
					$log.error('Error');
				}
		);
	};
	
	
	
	
	
	$scope.getModeletClass = function(status){
		
		var clazz = 'btn-default';
		
		switch(status){
			case 'Free' : 
				clazz = 'btn-success'; 
				break;
		
			case 'Registered With System Default Pool' : 
				clazz = 'btn-success'; 
				break;
		
			case 'Failed' : 
			case 'Unavailable' :
				clazz = 'btn-danger';  
				break;
		
			case 'Registration Inprogress' : 
				clazz = 'btn-info'; 
				break;
			
			case null :
				clazz = 'btn-danger';  
				break;
		}
		
		return clazz;
	};
	
	/** This method is to make the modelet draggable */
	
	$scope.isModeletDraggable = function(modelet){
		var draggable = true;
		switch(modelet.modeletStatus){
		case 'Registration Inprogress':
		case 'Busy':
			draggable = false;  
			break;
		}
		return draggable;
	};

	$scope.getTotalModeletCount = function(pool){
		if (pool.pool.defaultPool == 1)
			return pool.modeletClientInfoList.length;
		else 
			return pool.pool.modeletCount;
	};
	$scope.getTotalActiveModeletCount = function(pool){
		return pool.modeletClientInfoList.length;
	};
	$scope.toggleBox = function(element){
		element.removeClass();
		element.addClass('fa fa-plus');
	};
	
	$scope.getInactiveModeletCount = function(pool){
		var inactiveModelets = 0;
		inactiveModelets = pool.pool.modeletCount - pool.modeletClientInfoList.length;
		 if(inactiveModelets < 0){
			 inactiveModelets = 0; 
		 }
	pool.pool.inactiveModeletCount = inactiveModelets;
		return inactiveModelets;
	};

	/** This method is to save the changes */
	
	$scope.save = function(){
		$log.info('Saving Changes...');
		$scope.showErrorMessage = false;
		$scope.showSuccesssMessage = false;
		$scope.showFailureMessage = false;
		$scope.showMessage = '';	
		
		$dialogs.confirm('Please Confirm update ?','<span class="confirm-body"><strong>Do you want to change the pool configuration?<strong></span>')
		.result.then(function(btn){
			for(var i in $scope.modeletPoolsCopy) {
				$scope.modeletPools[i].pool.modeletAdded = 0;
				$scope.modeletPools[i].pool.modeletRemoved = 0;
				var modeletDifference = $scope.modeletPools[i].modeletClientInfoList.length - $scope.modeletPoolsCopy[i].modeletClientInfoList.length;
				if(modeletDifference > 0)
					$scope.modeletPools[i].pool.modeletAdded = modeletDifference;
				else
					$scope.modeletPools[i].pool.modeletRemoved = -modeletDifference;
				
				if($scope.modeletPools[i].pool.poolName != "SYSTEM_TEMP_POOL" && $scope.modeletPools[i].pool.waitTimeout < 60) {
					$scope.showFailureMessage = true;
					$scope.showMessage = "WaitTimeout value should be >=60.";
					return;
				} else {
					$scope.modeletPools[i].pool.waitTimeout = $scope.modeletPools[i].pool.waitTimeout * 1000;
				}
			}
			modeletPoolingService.updatePoolConfig($scope.modeletPools).then(
					function(responseData){
						if(responseData.error == false) {
							$log.info('Saved Changes');
							$scope.init();
							$scope.showSuccesssMessage = true;
							$scope.showMessage = "Updated successfully.";
						} else {
								$scope.init();
								$scope.showFailureMessage = true;
								$log.info('Saving changes Failed');
								var errorLength = responseData.message.indexOf(",");
								if(responseData.message.indexOf(",") == -1) {
									var errorLength = responseData.message.indexOf("]");
								}
								if (responseData.message.indexOf("[") != -1) {
									$scope.showMessage = responseData.message.substring(responseData.message.indexOf("[")+1 , errorLength);
								} else {
									$scope.showMessage = responseData.message;
								}							
						}
					
					},
					function(errorData){
						$log.error('Error');
					}
			);
		});
	};
	
	
	$scope.createPool = function(){
		$scope.showErrorMessage = false;
		$scope.showSuccesssMessage = false;
		$scope.showFailureMessage = false;
		$scope.showMessage = '';
		for (var i in $scope.modeletPools) {
			if($scope.newPool.pool.poolName.toUpperCase() == $scope.modeletPools[i].pool.poolName.toUpperCase()) {
				$scope.showErrorMessage = true;
				$scope.showMessage = "Pool name "+ $scope.newPool.pool.poolName +" already exists.Please key in a new pool name.";
				return;
			}	
		}
		if($scope.newPool.pool.waitTimeout<60 ){
			$scope.showErrorMessage = true;
			$scope.showMessage = "WaitTimeout value should be >=60.";
			return;
		}
		
		$scope.newPool.pool.poolDesc = $scope.newPool.pool.poolName;
		$log.info('Creating New Pool...');
		if($scope.newPool.pool.waitTimeout == null)
			$scope.newPool.pool.waitTimeout = 0;
		else
			$scope.newPool.pool.waitTimeout = $scope.newPool.pool.waitTimeout * 1000;
		modeletPoolingService.createPool($scope.newPool).then(
				function(responseData){
					if(responseData.error == false) {
						$log.info('Created');
						sharedPropertiesService.put("newPoolName",$scope.newPool.pool.poolName);
						$scope.init();
						$scope.showPoolCreationForm = false;
						$scope.clearNewPool();
						$scope.showSuccesssMessage = true;
						$scope.showMessage = "Pool with name " + sharedPropertiesService.get("newPoolName") +" successfully created.";
					} else {
							$scope.newPool.pool.waitTimeout = $scope.newPool.pool.waitTimeout / 1000;
							$scope.showErrorMessage = true;
							$log.info('Creation Failed');
							var errorLength = responseData.message.indexOf(",");
							if(responseData.message.indexOf(",") == -1) {
								var errorLength = responseData.message.indexOf("]");
							}
							if (responseData.message.indexOf("[") != -1) {
								$scope.showMessage = responseData.message.substring(responseData.message.indexOf("[")+1 , errorLength);
							} else {
								$scope.showMessage = responseData.message;
							}							
					}
				},
				function(errorData){
					$log.error('Error');
				}
		);
	};
	
	
	
	/** This method is to drop the pool */
	
	$scope.deletePool = function(pool){
		$scope.showSuccesssMessage = false;
		$scope.showErrorMessage = false;
		$scope.showFailureMessage = false;
		if(pool.pool.defaultPool == 1) {
			$log.warn(pool.poolName + ' is default Pool, it Can not be deleted.');	
			$dialogs.notify('Please Confirm delete ?','<span class="error-body"><strong>Default Pool cannot be deleted.<strong></span>');			
		} else if($scope.isSystemTempPool(pool)) {
			$log.warn('SYSTEM_TEMP_POOL is system temp pool, it Can not be deleted.');
			$dialogs.notify('Please Confirm delete ?','<span class="error-body"><strong>System Temp Pool cannot be deleted.<strong></span>');			
		} else {
			$log.warn('Do you want the '  + pool.pool.poolName + ' pool removed permanently?');
			$dialogs.confirm('Please Confirm delete ?','<span class="confirm-body"><strong>Do you want the '  + pool.pool.poolName + ' pool removed permanently? <strong></span>')
			.result.then(function(btn){
				$log.info('Pool deleted with id : '+pool.pool.id);
				modeletPoolingService.deletePool(pool.pool.id).then(
						function(responseData){
							if(responseData.error == false) {
								$log.info('Deleted');
								sharedPropertiesService.put("deletedPoolName",pool.pool.poolName);
								$scope.init();
								$scope.showSuccesssMessage = true;
								$scope.showMessage = "Pool with name " + sharedPropertiesService.get("deletedPoolName") +" successfully deleted.";
							} else {
								$log.info('Deletion Failed');
									$scope.showFailureMessage = true;
									var errorLength = responseData.message.indexOf(",");
									if(responseData.message.indexOf(",") == -1) {
										var errorLength = responseData.message.indexOf("]");
									}
									if (responseData.message.indexOf("[") != -1) {
										$scope.showMessage = responseData.message.substring(responseData.message.indexOf("[")+1 , errorLength);
									} else {
										$scope.showMessage = responseData.message;
									}							
							}							
						},
						function(errorData){
							$log.error('Error');
						}
				);
	        });
		}
	};
	
	
	$scope.init = function(){
		setModeletPoolingDetails();
		$scope.search_string = '';
		$scope.showSuccesssMessage = false;
		$scope.showFailureMessage = false;
	};
	
	$scope.init();
	
	$scope.newSelectEnvironment = function(e){
		$scope.newPool.poolCriteriaDetails.tenant = 'Any';
		$scope.newPool.poolCriteriaDetails.transactionType = 'Any';
		$scope.newPool.poolCriteriaDetails.transactionMode = 'Any';
		$scope.newPool.poolCriteriaDetails.channel = 'Any';
		$scope.newPool.poolCriteriaDetails.model = 'Any_Any';
		if(e.toUpperCase().indexOf("MATLAB") != -1){
			$scope.newPool.poolCriteriaDetails.executionEnvironment = 'Linux';
			$scope.executionEnvironments = ['Linux'];
		}
		else if(e.toUpperCase().indexOf("EXCEL") != -1){
			$scope.newPool.poolCriteriaDetails.executionEnvironment = 'Windows';
			$scope.executionEnvironments = ['Windows'];
		} else if(e.toUpperCase().indexOf("R") != -1){
			$scope.executionEnvironments = ['Linux','Windows'];
		}
		$scope.newSelectTypeList($scope.newPool.poolCriteriaDetails.transactionMode, $scope.newPool.poolCriteriaDetails.executionEnvironment);
	}
	
	$scope.newSelectExcEnvironment = function(ee){
		$scope.newPool.poolCriteriaDetails.executionEnvironment == ee;
		$scope.newSelectTypeList($scope.newPool.poolCriteriaDetails.transactionMode, $scope.newPool.poolCriteriaDetails.executionEnvironment);
	}
	
	$scope.selectEnvironment = function(pool, e){
		pool.poolCriteriaDetails.executionLanguage = e;
	}
	
	$scope.newSelectTenant = function(t){
		$scope.newPool.poolCriteriaDetails.transactionType = 'Any';
		$scope.newPool.poolCriteriaDetails.transactionMode = 'Any';
		$scope.newPool.poolCriteriaDetails.channel = 'Any';
		$scope.newPool.poolCriteriaDetails.model = 'Any_Any';
		$scope.newSelectTypeList($scope.newPool.poolCriteriaDetails.transactionMode, $scope.newPool.poolCriteriaDetails.executionEnvironment);
	}
	
	$scope.newSelectMode = function(tt){
		$scope.newPool.poolCriteriaDetails.transactionType = 'Any';
		$scope.newPool.poolCriteriaDetails.channel = 'Any';
		$scope.newPool.poolCriteriaDetails.model = 'Any_Any';
		$scope.newSelectTypeList(tt, $scope.newPool.poolCriteriaDetails.executionEnvironment);
	}

	
	
	$scope.newSelectTypeList = function(tt, ee){
		$scope.tenantSpecificModel = [];
		if ($scope.newPool.poolCriteriaDetails.tenant.toUpperCase() == 'ANY') {
			$scope.tenantSpecificModel.push("Any_Any");
		} else {
			$scope.tempModelmodelArray = [];
			$scope.duplicateModelmodelArray = [];
			var excEnv = $scope.newPool.poolCriteriaDetails.executionLanguage;
			if($scope.modelNamesByTenantAndEnv[$scope.newPool.poolCriteriaDetails.tenant.toLowerCase()] != null) {
				if(excEnv[0] == "Excel" && $scope.modelNamesByTenantAndEnv[$scope.newPool.poolCriteriaDetails.tenant.toLowerCase()].Excel != null) {
					$scope.tempModelmodelArray = $scope.modelNamesByTenantAndEnv[$scope.newPool.poolCriteriaDetails.tenant.toLowerCase()].Excel.Windows;
				} else if(excEnv[0] == "Matlab" && $scope.modelNamesByTenantAndEnv[$scope.newPool.poolCriteriaDetails.tenant.toLowerCase()].Matlab != null) {
					$scope.tempModelmodelArray = $scope.modelNamesByTenantAndEnv[$scope.newPool.poolCriteriaDetails.tenant.toLowerCase()].Matlab.Linux;
				} else {
					if(ee.toUpperCase() == 'LINUX') {
						$scope.tempModelmodelArray = $scope.modelNamesByTenantAndEnv[$scope.newPool.poolCriteriaDetails.tenant.toLowerCase()].R.Linux;
					} else {
						$scope.tempModelmodelArray = $scope.modelNamesByTenantAndEnv[$scope.newPool.poolCriteriaDetails.tenant.toLowerCase()].R.Windows;
					}
				}
			}
			if($scope.newPool != null && $scope.newPool.poolCriteriaDetails != null && excEnv[0] != "Matlab") {
				if(tt.toUpperCase() == 'BULK') {
					if($scope.tempModelmodelArray != null) {
						$scope.tempModelmodelArray = $scope.tempModelmodelArray.Bulk;
					}
				} else {
					$scope.duplicateModelmodelArray = $scope.tempModelmodelArray;
					$scope.tempModelmodelArray = [];
					for(var i in $scope.duplicateModelmodelArray) {
						for(var j in $scope.duplicateModelmodelArray[i]) {
							$scope.tempModelmodelArray.push($scope.duplicateModelmodelArray[i][j]);
						}
					}
				}
				$scope.tenantSpecificModel.push("Any_Any");
				for (var i in $scope.tempModelmodelArray) {
					$scope.tenantSpecificModel.push($scope.tempModelmodelArray[i]);
				}
		}
			else
					$scope.tenantSpecificModel.push("Any_Any");
		}
	}
	
	$scope.selectChannelAndModel = function(pool){
		$scope.selectModel(pool);
		pool.poolCriteriaDetails.channel = 'Any';
	}
	
	$scope.selectModel = function(pool){
		pool.poolCriteriaDetails.model="Any_Any";
		pool.tenantSpecificModel = [];
		if (pool.poolCriteriaDetails.tenant.toUpperCase() == 'ANY') {
			pool.tenantSpecificModel.push("Any_Any");
		} else {
			$scope.tempModelmodelArray = [];
			$scope.duplicateModelmodelArray = [];
			var excEnv = pool.poolCriteriaDetails.executionLanguage;
			if($scope.modelNamesByTenantAndEnv[pool.poolCriteriaDetails.tenant.toLowerCase()] != null) {
				if(excEnv[0] == "Excel" && $scope.modelNamesByTenantAndEnv[pool.poolCriteriaDetails.tenant.toLowerCase()].Excel != null) {
					$scope.tempModelmodelArray = $scope.modelNamesByTenantAndEnv[pool.poolCriteriaDetails.tenant.toLowerCase()].Excel.Windows;
				} else if(excEnv[0] == "Matlab" && $scope.modelNamesByTenantAndEnv[pool.poolCriteriaDetails.tenant.toLowerCase()].Matlab != null) {
					$scope.tempModelmodelArray = $scope.modelNamesByTenantAndEnv[pool.poolCriteriaDetails.tenant.toLowerCase()].Matlab.Linux;
				} else {
					if(pool.poolCriteriaDetails.executionEnvironment.toUpperCase() == 'LINUX') {
						$scope.tempModelmodelArray = $scope.modelNamesByTenantAndEnv[pool.poolCriteriaDetails.tenant.toLowerCase()].R.Linux;
					} else {
						$scope.tempModelmodelArray = $scope.modelNamesByTenantAndEnv[pool.poolCriteriaDetails.tenant.toLowerCase()].R.Windows;
					}
				}
			}
			if(pool != null && pool.poolCriteriaDetails != null && excEnv[0] != "Matlab") {
				if(pool.poolCriteriaDetails.transactionMode.toUpperCase() == 'BULK') {
					if($scope.tempModelmodelArray != null) {
						$scope.tempModelmodelArray = $scope.tempModelmodelArray.Bulk;
					}
				} else {
					$scope.duplicateModelmodelArray = $scope.tempModelmodelArray;
					$scope.tempModelmodelArray = [];
					for(var i in $scope.duplicateModelmodelArray) {
						for(var j in $scope.duplicateModelmodelArray[i]) {
							$scope.tempModelmodelArray.push($scope.duplicateModelmodelArray[i][j]);
						}
					}
				}
				pool.tenantSpecificModel.push("Any_Any");
				for (var i in $scope.tempModelmodelArray) {
					pool.tenantSpecificModel.push($scope.tempModelmodelArray[i]);
				}
			}
			else
					pool.tenantSpecificModel.push("Any_Any");
		}
	}
	
	$scope.newSelectTransactionType = function(tt){
		$scope.newPool.poolCriteriaDetails.transactionType = tt;
	}
	
	$scope.selectTransactionType = function(pool, tt){
		pool.poolCriteriaDetails.transactionType = tt;
	}
	
	$scope.newSelectTransactionMode = function(tm){
		$scope.newPool.poolCriteriaDetails.transactionMode = tm;
	}
	
	$scope.selectTransactionMode = function(pool, tm){
		pool.poolCriteriaDetails.transactionMode = tm;
	}
	
	$scope.newSelectChannel = function(c){
		$scope.newPool.poolCriteriaDetails.channel = c;
	}
	
	$scope.newSelectModel = function(m){
		$scope.newPool.poolCriteriaDetails.model = m;
	}
	
	
	$scope.selectInitialModel = function(pool){
		var t = pool.poolCriteriaDetails.tenant;
		var tt = pool.poolCriteriaDetails.transactionMode;
		var ee = pool.poolCriteriaDetails.executionEnvironment;
		pool.tenantSpecificModel = [];		
		if (t.toUpperCase() == 'ANY') {
			pool.tenantSpecificModel.push("Any_Any");
		} else {
			$scope.tempModelmodelArray = [];
			$scope.duplicateModelmodelArray = [];
			var excEnv = pool.poolCriteriaDetails.executionLanguage;
			if($scope.modelNamesByTenantAndEnv[t.toLowerCase()] != null) {
				if(excEnv[0] == "Excel" && $scope.modelNamesByTenantAndEnv[t.toLowerCase()].Excel != null) {
					$scope.tempModelmodelArray = $scope.modelNamesByTenantAndEnv[t.toLowerCase()].Excel.Windows;
				} else if(excEnv[0] == "Matlab" && $scope.modelNamesByTenantAndEnv[t.toLowerCase()].Matlab != null) {
					$scope.tempModelmodelArray = $scope.modelNamesByTenantAndEnv[t.toLowerCase()].Matlab.Linux;
				} else {
					if(ee.toUpperCase() == 'LINUX') {
						if($scope.tempModelmodelArray = $scope.modelNamesByTenantAndEnv[t.toLowerCase()].R != undefined) {
							$scope.tempModelmodelArray = $scope.modelNamesByTenantAndEnv[t.toLowerCase()].R.Linux;	
						}
					} else if($scope.tempModelmodelArray = $scope.modelNamesByTenantAndEnv[t.toLowerCase()].R != undefined) {
						$scope.tempModelmodelArray = $scope.modelNamesByTenantAndEnv[t.toLowerCase()].R.Windows;
					}
				}
			}
			if(pool != null && pool.poolCriteriaDetails != null && excEnv[0] != "Matlab") {
				if(tt.toUpperCase() == 'BULK') {
					if($scope.tempModelmodelArray != null) {
						$scope.tempModelmodelArray = $scope.tempModelmodelArray.Bulk;
					}
				} else {
					$scope.duplicateModelmodelArray = $scope.tempModelmodelArray;
					$scope.tempModelmodelArray = [];
					for(var i in $scope.duplicateModelmodelArray) {
						for(var j in $scope.duplicateModelmodelArray[i]) {
							$scope.tempModelmodelArray.push($scope.duplicateModelmodelArray[i][j]);
						}
					}
				}
				pool.tenantSpecificModel.push("Any_Any");
				for (var i in $scope.tempModelmodelArray) {
					pool.tenantSpecificModel.push($scope.tempModelmodelArray[i]);
				}
			}	
			else
					pool.tenantSpecificModel.push("Any_Any");
		}
	}
	
	$scope.searchPool = function(){		
		$scope.showErrorMessage = false;
		$scope.showSuccesssMessage = false;
		$scope.showFailureMessage = false;
		$scope.showMessage = '';
		modeletPoolingService.searchPool($scope.search_string).then(
				function(responseData){
					$log.info('Received Search Result');
					if(!responseData.error){
						$scope.modeletPools = [];
						angular.forEach(responseData.response, function(poolDetail){
							poolDetail.pool.waitTimeout = poolDetail.pool.waitTimeout /1000;
							$scope.modeletPools.push(poolDetail);							
						});						
					}					
					$scope.pools = responseData.response;
				},
				
				
				function(errorData){
					$log.error('Error');
				}			
			);
	};
	
	$scope.isSystemTempPool = function(pool) {
		if (pool.pool.poolName === 'SYSTEM_TEMP_POOL') {
			return true;
		} else {
			return false;
		}
	};
	
	$scope.modeletTrackingId = function(modelet) {
		if(modelet != null)
			return modelet.host + ":" + modelet.port;
	};
	
	$scope.pastePoolName = function (event) {
		 var item = event.clipboardData.items[0];
		    item.getAsString(function (data) {
		    $scope.newPool.pool.poolName = data.trim();
		    var validName = /^[0-9a-zA-Z_]+$/.test(data.trim());
		    if(!validName)
		    	$scope.newPool.pool.poolName = '';
		    });   
		  };
		  
	$scope.pasteWaitTime = function (event) {
			  var item = event.clipboardData.items[0];
			  item.getAsString(function (data) {
				  $scope.newPool.pool.waitTimeout = data.trim();
				  var validName = /^\d+$/.test(data.trim());
				  if(!validName)
					  $scope.newPool.pool.waitTimeout = null;
			  });   
		  };	  
		  
	$scope.pasteExistingWaitTime = function (event,index) {
		  var item = event.clipboardData.items[0];
		  item.getAsString(function (data) {
			  $scope.modeletPools[index].pool.waitTimeout = data.trim();
			  var validName = /^\d+$/.test(data.trim());
			  if(!validName)
				  $scope.modeletPools[index].pool.waitTimeout = null;
		  });   
	  };	  	  
		  
	$scope.clearNewPool = function() {
		$scope.showErrorMessage = false;
		$scope.showSuccesssMessage = false;
		$scope.showFailureMessage = false;
		$scope.showMessage = '';
		$scope.executionEnvironments = ['Linux','Windows'];
		$scope.tenantSpecificModel = [];
		$scope.tenantSpecificModel.push("Any_Any");
		$scope.newPool = {
				pool : {
					poolName: '',
					poolDesc: '',
					defaultPool: 0,
					modeletCount: 0,
					inactiveModeletCount: 0,
					priority: 0,
					waitTimeout: null,
					modeletAdded : 0,
					modeletRemoved : 0
				},
				poolCriteriaDetails:{
					tenant: 'Any',
					executionLanguage: 'R',
					executionEnvironment: 'Linux',
					transactionType: 'Any',
					transactionMode: 'Any',
					model: 'Any_Any',
					channel: 'Any'
				},
				poolCriteria : null,
				poolUsageOrderMapping : null,
				modeletClientInfoList : null			
		};
	};
	
	$(document).tooltip({ selector: "[title]",
        placement: "bottom",
        trigger: "focus",
        animation: true}); 
	
	$scope.dragStart = function (event, uiObject, dragObject) {
		$scope.dragObject = dragObject;
		$scope.showFailureMessage = false;
		$scope.showSuccesssMessage = false;
		$scope.showMessage = "";
		$scope.validateDrop();
	};
	
	$scope.dragEnd = function (dropObject) {
		$scope.dropObject = dropObject;
		if ($scope.dropObject.executionLanguage == "" || $scope.dragObject.executionLanguage != $scope.dropObject.executionLanguage || $scope.dragObject.execEnvironment != $scope.dropObject.executionEnvironment) { 
			$scope.invalidMove = true;
			return false;
		}
		$scope.invalidMove = false;
		return true;
	};
	
	$scope.validateMove = function (dropObject) {
		 $scope.showFailureMessage = false;
	};
	
	$scope.validateDrop = function () {
			 $scope.showFailureMessage = true;
			 $scope.showMessage = $scope.dragObject.executionLanguage + " " + $scope.dragObject.execEnvironment + " Modelets can be allocated to " + $scope.dragObject.executionLanguage + " " + $scope.dragObject.execEnvironment + " pools only.";
		};
	
	$scope.switchState = function (modelet) {
		$scope.showErrorMessage = false;
		$scope.showSuccesssMessage = false;
		$scope.showFailureMessage = false;
		$scope.showMessage = '';
		$dialogs.confirm('Please Confirm modelet state switch ','<span class="confirm-body"><strong>Do you want to '+ modelet.switchMessage +' the modelet ? <strong></span>')
		.result.then(function(btn){
		modelet.stateSwitch = true;
		modeletPoolingService.switchModeletStatus(modelet).then(
				function(responseData){
					if(responseData.error == false) {
						$log.info('Request sent for switching status of modelet: '+ modelet.host + ':'+ modelet.port);
						$scope.init();
						$scope.showSuccesssMessage = true;
						$scope.showMessage = "Switching state of modelet: "+ modelet.host + ":"+ modelet.port + " is successful. " ;
					} else {
							modelet.stateSwitch = false;
							$scope.showFailureMessage = true;
							$log.info('Sending request failed for switching status of modelet: '+ modelet.host + ':'+ modelet.port);
							if (responseData.message.indexOf("[") != -1) {
								$scope.showMessage = responseData.message.substring(responseData.message.indexOf("[")+1 , errorLength);
							} else {
								$scope.showMessage = responseData.message;
							}
					}
				},
				function(errorData){
					$log.error('Error');
				}
		);
		});
	};
	
	$scope.launchRestartPopUp = function() {
		$scope.restartError = false;
		$scope.restartMessage = '';
		modeletPoolingService.getModeletRestartDetails().then(
				function(responseData){
					$log.info('Received Modelet Restart Details.');
					if(!responseData.error) {
						$scope.tenantsWithoutAny = responseData.response.tenants;
						$scope.modelNamesByTenant = responseData.response.modelNamesByTenant;
						$scope.restartConfigRows = responseData.response.modeletRestartInfoList;
						$scope.restartConfigRowsCopy = responseData.response.modeletRestartInfoList;
						$scope.openPopUp = true;
					}
				},
				function(errorData){
					$log.error('Error while loading Modelet Restart Details');
				}
		);
	};
	
	$scope.closeRestartPopUp = function() {
		$scope.showRowCreationForm = false;
		$scope.clearNewRow();
		$scope.restartError = false;
		$scope.restartMessage = '';
		$scope.openPopUp = false;
	};
	
	$scope.clearNewRow = function() {
		$scope.createRestartError = false;
		$scope.createRestartErrorMessage = '';
		$scope.newRow = {
					id: null,
					tenantId: '',
					modelNameAndVersion: '',
					restartCount: null
		};
	};
	
	$scope.pasteCount = function (event) {
		var item = event.clipboardData.items[0];
		item.getAsString(function (data) {
			$scope.newRow.restartCount = data.trim();
			var validName = /^\d+$/.test(data.trim());
			if(!validName)
				$scope.newRow.restartCount = null;
		});   
	};
	
	$scope.pasteExistingCount = function (event,index) {
		var item = event.clipboardData.items[0];
		item.getAsString(function (data) {
			$scope.restartConfigRows[index].restartCount = data.trim();
			var validName = /^\d+$/.test(data.trim());
			if(!validName)
				$scope.restartConfigRows[index].restartCount = null;
		});   
	};

	$scope.newRowSelectTenant = function(t){
		$scope.newRow.modelNameAndVersion = '';
		$scope.newRow.restartCount = null;
		$scope.modelNamesBySelectedTenant = [];
		$scope.modelNamesBySelectedTenant=$scope.modelNamesByTenant[$scope.tenantsWithoutAny[t]];
	}
	
	$scope.newRowSelectModel = function(){
		$scope.newRow.restartCount = null;
	}
	
	$scope.addRestartConfig = function() {
		$scope.restartMessage = '';
		$scope.restartError = false;
		if($scope.newRow.restartCount > 9999 || $scope.newRow.restartCount < 1 ) {
			$scope.newRow.restartCount = null;
			$scope.createRestartErrorMessage = "Restart Transaction Count must be greater than 0 and less than 10000";
			$scope.createRestartError = true;
		} else {
			$scope.createRestartError = false;
			angular.forEach($scope.restartConfigRowsCopy, function(configDetails){
				if(configDetails.tenantId == $scope.newRow.tenantId && configDetails.modelNameAndVersion == $scope.newRow.modelNameAndVersion) {
					$scope.createRestartErrorMessage = "Transaction count is already set-up for Model "+ $scope.newRow.modelNameAndVersion +" and Tenant "+ $scope.newRow.tenantId +".";
					$scope.createRestartError = true;
				}
			});
			if(!$scope.createRestartError) {
				$scope.updateConfigRows = [];
				$scope.updateConfigRows.push($scope.newRow);
				modeletPoolingService.updateModeletRestartDetails($scope.updateConfigRows).then(
						function(responseData){
							if(responseData.error == false) {
								$log.info('Restart config row created');
								$scope.showRowCreationForm = false;
								$scope.clearNewRow();
								$scope.launchRestartPopUp();
								$scope.restartMessage = "Add Successful";
							} else {
								$log.info('Restart config row creation Failed');
								$scope.createRestartErrorMessage = "Restart Transaction Count must be greater than 0 and less than 10000";
								$scope.createRestartError = true;
							}
						},
						function(errorData){
							$log.error('Error');
						}
				);
			}
		}
	};
	
	$scope.updateRestartConfig = function() {
		$scope.restartMessage = '';
		$scope.restartError = false;
		angular.forEach($scope.restartConfigRows, function(configDetails){
			if(configDetails.restartCount == null || configDetails.restartCount > 9999 || configDetails.restartCount < 1 ) {
				$scope.restartError = true;
				$scope.restartMessage = "Restart Transaction Count must be greater than 0 and less than 10000";
			}
		});
		if(!$scope.restartError) {
			modeletPoolingService.updateModeletRestartDetails($scope.restartConfigRows).then(
					function(responseData){
						if(responseData.error == false) {
							$log.info('Restart config row updated');
							$scope.launchRestartPopUp();
							$scope.restartMessage = "Update Successful";
						} else {
							$log.info('Restart config row update Failed');
							$scope.launchRestartPopUp();
							$scope.restartError = true;
							$scope.restartMessage = "Update Failed";
						}
					},
					function(errorData){
						$log.error('Error');
					}
			);
		}
	};
	
	$scope.deleteRestartConfig = function(configRow) {
		$scope.restartMessage = '';
		$scope.restartError = false;
		$dialogs.confirm('Please Confirm delete ?','<span class="confirm-body"><strong>Do you want to remove the modelet restart configuration ? <strong></span>')
		.result.then(function(btn){
			modeletPoolingService.deleteModeletRestartDetails(configRow).then(
					function(responseData){
						if(responseData.error == false) {
							$log.info('Restart config row deleted');
							$scope.launchRestartPopUp();
							$scope.restartMessage = "Delete Successful";
						} else {
							$log.info('Restart config row deletion Failed');
							$scope.restartError = true;
							$scope.restartMessage = "Delete Failed";
						}
					},
					function(errorData){
						$log.error('Error');
					}
			);
        });
	};
	
}];