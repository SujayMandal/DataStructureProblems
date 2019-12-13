// Code goes here
'use strict';

var privilegeMappingManageCtrl = ['$scope', '$state', '$rootScope','$log', '$dialogs', '$window', 'sharedPropertiesService','privilegeMappingService', function($scope, $state, $rootScope, $log, $dialogs, $window, sharedPropertiesService, privilegeMappingService){
	var tenantCode = $('#tenantCode_current').val();
	$scope.errorResponseBackground = 'background-color: #dd4b39; color:white;';
	$scope.successResponseBackground = 'background-color: #00a65a; color:white;';
	$scope.getMap = function () {
		$scope.showErrorMessage = false;
		$scope.showSuccesssMessage = false;
		$scope.responseBackground = '';
		$scope.showMessage = '';
		$scope.result = [];
		privilegeMappingService.getRolesforTenant(tenantCode).then(
				function(responseData){
					if(responseData.error == false) {
						console.warn(responseData);
						$scope.result = responseData.response;
						$scope.init(responseData.response,0);
						//$scope.showSuccesssMessage = true;
					} else {
						$scope.showErrorMessage = true;
						$scope.responseBackground = $scope.errorResponseBackground;
						$scope.showMessage = "Failed with error code : "+responseData.errorCode;
					}
				},
				function(errorData){
					$scope.showErrorMessage = true;
					$scope.errorResponseBackground = $scope.errorResponseBackground;
					$scope.showMessage = "Error occured , please contact to system admin";
					$log.error('Error' + errorData);
				}
		);
	};
	
	$scope.getMap();
	$scope.init = function(result,index){
		if(index!=null){
			$scope.role = Object.keys(result)[0];
		}
		$scope.leftList = [];
        $scope.rightList = [];
		$scope.rolesPrivilegesMapping = result;
		$scope.toptions=new Array();
		$scope.tselected=[];
		var privileges = $scope.rolesPrivilegesMapping[$scope.role]!=null ? $scope.rolesPrivilegesMapping[$scope.role].split(",") : null;
		for(i in staticPermissionArray){
			if(privileges==null || privileges.indexOf(i) == -1){
				 $scope.leftList.push(i);
			}
			else{
				$scope.rightList.push(i);
			}
			if(($scope.leftList.length + $scope.rightList.length) == Object.keys(staticPermissionArray).length){
				$rootScope.$broadcast('changeText',[$scope.leftList,$scope.rightList]);
			}
				
		}
	};
	$scope.switchRole = function(role){
		$scope.init($scope.result);
		
	}
	$scope.saveList = function(){
		$scope.showErrorMessage = false;
		$scope.showSuccesssMessage = false;
		$scope.responseBackground = '';
		$dialogs.confirm('Please Confirm update ?','<span class="confirm-body"><strong>Are you sure to update the privileges for '+$scope.role+' <strong></span>')
		.result.then(function(btn){
			console.log($scope.rightList);
			privilegeMappingService.setRolesforTenant(tenantCode,$scope.role,$scope.rightList).then(
					function(responseData){
						if(responseData.error == false) {
							console.warn(responseData);
							$scope.rolesPrivilegesMapping[$scope.role] = $scope.rightList == [] || $scope.rightList == null ? "" : $scope.rightList.toString() ;
							$scope.showSuccesssMessage = true;
							$scope.responseBackground = $scope.successResponseBackground;
							$scope.showMessage = "Save Successful";
						} else {
								$scope.showErrorMessage = true;
								$scope.responseBackground = $scope.errorResponseBackground;
								$scope.showMessage = "Save failed. Try again in some time or contact RA support at RealAnalyticsSupport@altisource.com for assistance."
								cosole.log("Failed with error code : "+responseData.errorCode);
						}
					},
					function(errorData){
						$scope.showErrorMessage = true;
						$scope.responseBackground = $scope.errorResponseBackground;
						$scope.showMessage = "Save failed. Try again in some time or contact RA support at RealAnalyticsSupport@altisource.com for assistance.";
						$log.error('Error' + errorData);
					}
			);
			
		});
		
	}
	$scope.reset = function(){
	/*	$dialogs.confirm('Please Confirm update ?','<span class="confirm-body"><strong>All updates will be lost. Click Confirm to proceed or Cancel to Go back <strong></span>')
		.result.then(function(btn){
			$window.location.reload();
		});*/
		$state.go($state.current, {}, {reload: true});
		
	}


}];