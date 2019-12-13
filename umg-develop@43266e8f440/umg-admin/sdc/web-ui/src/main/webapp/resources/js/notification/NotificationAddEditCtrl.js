'use strict';

var NotificationAddEditCtrl = ['$state','$scope','$http','$log','$location','$dialogs','notificationAddEditService','$sce','sharedPropertiesService',
                        function($state,$scope, $http, $log,$location,$dialogs,notificationAddEditService,$sce,sharedPropertiesService) {
	
	$scope.modelList = [];
	
	$scope.eventListData = [];
	$scope.eventAddData = {};//will contain id of event selected from dropdown in Add state page
	$scope.eventEditData = {};
	
	//flag to maintain details in case any of the services fails
	$scope.serviceFailureDetails = {modelList:{
													errorFlag: false,
													reason : ''
												},
									mailDetails:{
													errorFlag: false,
													reason : ''
												}			
									};
	
	$scope.mailDetails = {
							bodyText: '',
							toAddress:'',
							fromAddress:'',
							ccAddress:'',
							subject:''
						};
	
	$scope.responseMsg = '';
	$scope.responseMsgFlag = false;
	$scope.modelAndMailDiv = false;
	
	$scope.errorStyleTo = '';
	$scope.errorStyleCC = '';
	$scope.errorStyleDropDown = '';
	$scope.errorResponseBackground = 'background-color: #dd4b39; color:white;';
	$scope.successResponseBackground = 'background-color: #00a65a; color:white;';
	$scope.responseBackground = '';
	
	//used to check whether error message in case of add save event contains this message and we set $scope.errorStyleDropDown value
	$scope.duplicateEvent = "Event already configured";

	//value true determines Add notification Page else Edit page
	//currentStateObj.name determines the flag value
	$scope.addFlag = false;
	
	$scope.multipleAddressNote = "Multiple email ids should be separated by semi colon (;)";
	$scope.httpFailureMsg = 'Connection Failure. Data could not be fetched. Try again in some time or contact RA support at RealAnalyticsSupport@altisource.com for assistance';
	
	//function called to determine add or edit page
	function isAddPage(){
		if(currentStateObj.name=='notificationAdd'){
			$scope.addFlag = true;
			return true;
		}
		else{
			$scope.addFlag = false;
			return false;
		}
	}
	
	//function to get Event list in order to populate select dropdown
	function getNotificationEventList(){
		if(isAddPage()){
			$scope.responseMsgFlag = false;
			notificationAddEditService.getAddEventList().then(
					function(responseData){
						if(!responseData.error){
							$scope.eventListData = responseData.response;
						}
						else{
							$log.error(responseData.message);
							$scope.responseMsg = responseData.message;
							$scope.responseMsgFlag = true;
							$scope.responseBackground = $scope.errorResponseBackground;
						}
					},
					function(errorData){
						$log.error(errorData + ": http failure");
						$scope.responseMsg = $scope.httpFailureMsg;
						$scope.responseMsgFlag = true;
						$scope.responseBackground = $scope.errorResponseBackground;
					});
		}
	}
	
	//function to get list of models for the current tenant
	function getModelList(){
		notificationAddEditService.getModelList().then(
				function(responseData){
					if(!responseData.error){
					$scope.modelList = responseData.response;
					$scope.serviceFailureDetails.modelList.errorFlag = false;
					}
					else{
						$log.error(responseData.message);
						$scope.serviceFailureDetails.modelList.errorFlag = true;
						$scope.serviceFailureDetails.modelList.reason = responseData.message;
					}
						checkResponseFlag();
				},
				function(errorData){
					$log.error(errorData + ': http failure for model list');
					$scope.serviceFailureDetails.modelList.errorFlag = true;
					$scope.serviceFailureDetails.modelList.reason = $scope.httpFailureMsg;
					checkResponseFlag();
				}
			);
	}
	
	//function to get mail details
	function getMailDetails(id,flag){
		var mapId = id;
		notificationAddEditService.getMailDetails(mapId,flag).then(
				function(responseData){
					if(!responseData.error){
						$scope.mailDetails = responseData.response;
						$scope.serviceFailureDetails.mailDetails.errorFlag = false;
					}
					else{
						$log.error(responseData.message);
						$scope.serviceFailureDetails.mailDetails.errorFlag = true;
						$scope.serviceFailureDetails.mailDetails.reason = responseData.message;
					}
						checkResponseFlag();
				},
				function(errorData){
					$log.error(errorData + ': http failure for mail details');
					$scope.serviceFailureDetails.mailDetails.errorFlag = true;
					$scope.serviceFailureDetails.mailDetails.reason = $scope.httpFailureMsg;
					checkResponseFlag();
				}
			);
	}
	
	//ng-change function for loading model and mail details in case of add state
	$scope.loadModelMailAdd = function(eventAddData){
		//$scope.eventAddData = JSON.parse(eventAddData);
		$scope.errorStyleDropDown = '';
		getModelList();
		getMailDetails($scope.eventAddData.id,true);
	}
	
	//function for loading model and mail details in case of edit state
	function loadModelMailEdit(){
		if(!isAddPage()){
			$scope.eventEditData = sharedPropertiesService.get("editNotificationInfo");
			sharedPropertiesService.remove("editNotificationInfo");
			getModelList();
			getMailDetails($scope.eventEditData.id,false);
		}
	}
	
	//function after response of both model and mail details in case of edit state
	function checkResponseFlag(){
		if(!$scope.serviceFailureDetails.modelList.errorFlag && !$scope.serviceFailureDetails.mailDetails.errorFlag){
			$scope.modelAndMailDiv = true;
			$scope.responseMsg = '';
			$scope.responseMsgFlag = false;
		}
		else if(!$scope.serviceFailureDetails.modelList.errorFlag && $scope.serviceFailureDetails.mailDetails.errorFlag){
			$scope.modelAndMailDiv = false;
			$scope.responseMsg = $scope.serviceFailureDetails.mailDetails.reason;
			$scope.responseMsgFlag = true;
			$scope.responseBackground = $scope.errorResponseBackground;
		}
		else if($scope.serviceFailureDetails.modelList.errorFlag && !$scope.serviceFailureDetails.mailDetails.errorFlag){
			$scope.modelAndMailDiv = false;
			$scope.responseMsg = $scope.serviceFailureDetails.modelList.reason;
			$scope.responseMsgFlag = true;
			$scope.responseBackground = $scope.errorResponseBackground;
		}
		else{
			$log.error("failure in both model list data and mail details data");
			$scope.modelAndMailDiv = false;
			$scope.responseMsg = $scope.serviceFailureDetails.mailDetails.reason;
			$scope.responseMsgFlag = true;
			$scope.responseBackground = $scope.errorResponseBackground;
		}
	}
	
	//to check if toAddress empty and set red border for the field
	function isNotEmpty(emailString){
		if(!emailString){
			$scope.errorStyleTo = 'border: 1px solid red';
			$scope.responseMsg = 'To email id is mandatory';
			$scope.responseMsgFlag = true;
			$scope.responseBackground = $scope.errorResponseBackground;
			return false;
		}	
		else
			return true;
	}
	
	//validation for to address and cc address
	function isValidEmail(emailString){
		var emailArray = [];
		var flag = true;
		var regex = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
		if(emailString){
			emailArray = emailString.split(";");
			$.each( emailArray, function( i, val ){
				if(flag){
					flag = regex.test(val);
				}
			});
		}
		return flag;
	}
	
	//validating and setting error background for toAddress and ccAddress
	function validateEmail(){
		var noErrorFlag = true;
		if(!isValidEmail($scope.mailDetails.toAddress)){
			$scope.errorStyleTo = 'border: 1px solid red';
			$scope.responseMsg = 'Email id invalid';
			$scope.responseMsgFlag = true;
			$scope.responseBackground = $scope.errorResponseBackground;
			noErrorFlag = false;
		}
		if(!isValidEmail($scope.mailDetails.ccAddress)){
			$scope.errorStyleCC = 'border: 1px solid red';
			$scope.responseMsg = 'Email id invalid';
			$scope.responseMsgFlag = true;
			$scope.responseBackground = $scope.errorResponseBackground;
			noErrorFlag = false;
		}
		return noErrorFlag;
	}
	
	//saving edit data to database service call
	function notificationEditSave(){
		notificationAddEditService.saveEditEvent($scope.mailDetails,$scope.eventEditData).then(
				function(responseData){
					if(!responseData.error){
						$scope.responseMsg = responseData.message;
						$scope.responseMsgFlag = true;
						$scope.responseBackground = $scope.successResponseBackground;
					}
					else{
						$log.error(responseData.message);
						$scope.responseMsg = 'Notification edit failed. Try again in some time or contact RA support at RealAnalyticsSupport@altisource.com for assistance';
						$scope.responseMsgFlag = true;
						$scope.responseBackground = $scope.errorResponseBackground;
					}
				},
				function(errorData){
					$log.error(errorData + ": http failure");
					$scope.responseMsg = $scope.httpFailureMsg;
					$scope.responseMsgFlag = true;
					$scope.responseBackground = $scope.errorResponseBackground;
				});
	}
	
	//saving add data to database service call
	function notificationAddSave(){
		$scope.errorStyleDropDown = '';
		notificationAddEditService.saveAddEvent($scope.mailDetails,$scope.eventAddData).then(
				function(responseData){
					if(!responseData.error){
						$scope.responseMsg = 'Notification added successfully';
						$scope.responseMsgFlag = true;
						$scope.responseBackground = $scope.successResponseBackground;
					}
					else{
						$log.error(responseData.message);
						$scope.responseMsg = responseData.message;
						$scope.responseMsgFlag = true;
						$scope.responseBackground = $scope.errorResponseBackground;
						if(responseData.message.indexOf($scope.duplicateEvent) > -1){
							$scope.errorStyleDropDown = 'border: 1px solid red';
						}
					}
				},
				function(errorData){
					$log.error(errorData + ": http failure");
					$scope.responseMsg = $scope.httpFailureMsg;
					$scope.responseMsgFlag = true;
					$scope.responseBackground = $scope.errorResponseBackground;
				});
	}
	
	//save function
	$scope.saveData = function(){
		$scope.errorStyleTo = '';
		$scope.errorStyleCC = '';
		$scope.errorStyleDropDown = '';
		$scope.responseMsg = '';
		$scope.responseMsgFlag = false;
		if(isNotEmpty($scope.mailDetails.toAddress) && validateEmail()){
			if(isAddPage()){
				var dlg = $dialogs.confirm('Please Confirm','<span class="confirm-body">Are you sure to add the ' + $scope.eventAddData.eventName + ' notification for ' + $scope.mailDetails.tenantCode + ' tenant?</span>');
				dlg.result.then(function(btn){
					notificationAddSave();
				});
			}
			else{
				var dlg = $dialogs.confirm('Please Confirm','<span class="confirm-body">Are you sure to edit the ' + $scope.eventEditData.eventName + ' notification for ' + $scope.eventEditData.tenantCode + ' tenant? <br> Changes will take effect immediately on Save.</span>');
				dlg.result.then(function(btn){
					notificationEditSave();
				});	
			}
		}
	}
	
	//cancel function
	$scope.cancelData = function(){
		if(isAddPage()){
			$scope.mailDetails.toAddress = '';
			$scope.mailDetails.ccAddress = '';
			//in case dropdown and modelMailDiv needs to be cleared
			$state.go($state.current, {}, {reload: true});
		}
		else{
			$scope.mailDetails.toAddress = '';
			$scope.mailDetails.ccAddress = '';
			$location.path('notificationManage');
		}
	}
	
	//onload function called to determine add or edit page and set $scope.addFlag
	isAddPage();
	
	//onload function call to get Event list in order to populate select dropdown in add state
	getNotificationEventList();
	
	//onload function for edit state model and mail data loading
	loadModelMailEdit();
}];