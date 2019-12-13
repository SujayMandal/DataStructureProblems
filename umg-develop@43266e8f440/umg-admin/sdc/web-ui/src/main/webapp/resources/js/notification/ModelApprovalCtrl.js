'use strict';

var ModelApprovalCtrl = ['$scope','$http','$log','$dialogs','modelApprovalService','$stateParams','$sce',
                        function($scope, $http, $log,$dialogs,modelApprovalService,$stateParams,$sce) {
	
	$scope.approveMessage = '';
	$scope.version = $stateParams.version;
	console.log($scope.version);
	$scope.modelDetails = {};
	$scope.noError = false;
	$scope.validErrorResponse = false;
	
	$scope.publishApprovalResponse = function(){
		modelApprovalService.checkTenantMatch($scope.version).then(
				function(responseData){
					if(responseData.tenantMismatchFlag){
						var dlg = $dialogs.confirm('Please Confirm','<span class="confirm-body">This will change the active Tenant from ' + responseData.currentTenantCode +' to '+ responseData.switchToTenantCode +'?</span>');
						dlg.result.then(function(btn){
							getApprovalResponseService(responseData.switchToTenantCode);
						},
						function(btn){
								window.location = "#/home";
					        }
						);
					}
					else
					{
						getApprovalResponseService(responseData.switchToTenantCode);
					}
				},
				function(errorData){
					$log.error("Error came with : "+errorData);
					$scope.noError = false;
					$scope.validErrorResponse = false;
					$scope.approveMessage = "Failure in establishing connection. Please try again after sometime";
				}
			);
		}
	
	//onload call this function 
	$scope.publishApprovalResponse();
	
	function getApprovalResponseService(toTenant){
		modelApprovalService.getApprovalResponse($scope.version).then(
	   			 function(responseData){
	   				 if(responseData.response.switchTenantFlag){
	   					 location.reload();
	   					localStorage.setItem('tenant', toTenant);
	   				 }
	   				 else{
	   					 if(!responseData.error){
	   						 $scope.noError = true;
	   						 $scope.validErrorResponse = false;
	   						 $scope.approveMessage = $sce.trustAsHtml(responseData.response.notificationStatus);
	   						 $scope.modelDetails = responseData.response;
	   					 }
	   					 else{
	   						 if(responseData.response.responseSuccessFailureMsg)
	   						 {	$scope.noError = true;
	   						 $scope.validErrorResponse = true;
	   						 $scope.approveMessage = $sce.trustAsHtml(responseData.response.notificationStatus);
	   						 $scope.modelDetails = responseData.response;
	   						 }
	   						 else
	   						 {
	   							 $log.error("Error came with : "+responseData.errorCode + ':' + responseData.message);
	   							 $scope.noError = false;
	   							 $scope.validErrorResponse = false;
	   							 $scope.approveMessage = responseData.message;
	   						 }
	   					 }
	   				 }
	   			 },
					function(errorData){
						$log.error("Error came with : "+errorData);
						$scope.noError = false;
						$scope.validErrorResponse = false;
						$scope.approveMessage = "Failure in establishing http connection. Please try again after sometime";
					}
				);
	}
	
}];
			