'use strict';

var VersionImportWizardCtrl = ['$scope', '$log','$dialogs', 'umgVersionService', function($scope, $log, $dialogs, umgVersionService){
	
	$scope.versionDetails = {
		zipPackage: '',
		name: '',
		description: '',
		versionType: 'MAJOR',
		majorVersion: '',
		versionDescription: ''
	};
	
	$scope.Math = window.Math;
	$scope.imported = false;
	
	$scope.msg = "";
	$scope.showErrMsg = false;
	$scope.showSuccessMsg = false;
	$scope.tenantModels = [];
	$scope.majorVersions = [];

	/**
	 * This function is to set Tenant Model Names on Load
	 * */
	var setTenantModelNames = function(){
		$log.info("Setting Tenant Model Names ...");
		$scope.msg = "";
		$scope.showErrMsg = false;
		$scope.showSuccessMsg = false;

		umgVersionService.getAllTenantModelNames().then(
				function(responseData){
					angular.forEach(responseData.response, function(tmn){
						$scope.tenantModels.push(tmn);
					});
				},
				function(errorData){
					$log.error(errorData.message);
					$scope.showErrMsg = true;
					$scope.msg = errorData;
				}
		);
	};
	
	/**
	 * This method will set Major Versions and Description, while we select any Tenant Model.
	 */
	$scope.setMajorVersions = function(tenantModelName){
		$log.info("Setting Major Versions ...");
		$scope.msg = "";
		$scope.showErrMsg = false;
		$scope.showSuccessMsg = false;
        $scope.versionDetails.description = "";
        $scope.majorVersions = [];
		
		if(tenantModelName != ""){
			umgVersionService.getMajorVersions(tenantModelName).then(
					function(responseData) {
						if(responseData.error){
							$log.error(responseData.message);
							$scope.showErrMsg = true;
							$scope.msg = responseData.message;
						}else{
							$scope.majorVersions = responseData.response.majorVersions;
							$scope.versionDetails.description = responseData.response.description;
						}
					},
					function(errorData) {
						$scope.showErrMsg = true;
						$scope.msg = errorData;
					},
					function(responseData) {
						 alert('System Failed: ' + responseData);
					 }
			);
		}
	};
	
	/*Main method to import version*/
	
	$scope.importVersion = function(){
		$log.info("Request received to import Version");
		$scope.msg = "";
		$scope.showErrMsg = false;
		$scope.showSuccessMsg = false;
		$scope.versionInfo = [];
		$scope.errInfo = [];
		if(validateVersionDetails($scope.versionDetails)){
			
			umgVersionService.importVersion($scope.versionDetails).then(
					function(responseData) {
						if(responseData.error){
							$log.error(responseData.message);
							$scope.showErrMsg = true;
							$scope.msg = responseData.message;
							angular.forEach(responseData.response, function(data){
								$scope.errInfo.push(data);
							});
						}else{
							$log.info(responseData.message);
							$scope.showSuccessMsg = true;
							$scope.msg = responseData.message;
							angular.forEach(responseData.response, function(data){
								$scope.versionInfo.push(data);
							});
							$scope.imported = true;
						}
					},
					function(errorData) {
						$scope.showErrMsg = true;
						$scope.msg = errorData;
					},
					function(responseData) {
						 alert('System Failed: ' + responseData);
					 }
			);
			
		}
	};
	 
	 
	 $scope.$watch('versionDetails',function(n,o){
		 if(n != o){
			 $scope.imported = false;
		 }
	 },true);
	
	/*Validation*/
	
	var validateVersionDetails = function(versionDetails){

		$scope.msg = "";
		$scope.showErrMsg = false;
		$scope.showSuccessMsg = false;
		$log.info("Validation started ...");
		
		if(versionDetails.zipPackage === ''){
			$scope.showErrMsg = true;
			$scope.msg = "Please select a Zip file.";
			$log.warn("Please select a Zip file.");
			//$scope.setCurrentStep(0);
			return false;
		}
		
		var lastDotAt = versionDetails.zipPackage.name.lastIndexOf('.');
		var extension = versionDetails.zipPackage.name.substring(lastDotAt+1).trim().toLowerCase();
		
		if(extension !== "zip"){
			$scope.showErrMsg = true;
			$scope.msg = "Invalid Zip File.";
			$log.warn("Invalid Zip File.");
			//$scope.setCurrentStep(0);
			return false;
		}
		
		if(versionDetails.name.trim() === ''){
			$scope.showErrMsg = true;
			$scope.msg = "Tenant Model Name is Mandatory!";
			$log.warn("Tenant Model Name is Mandatory!");
			//$scope.setCurrentStep(1);
			return false;
		}
		
		if(versionDetails.description.trim() === ''){
			$scope.showErrMsg = true;
			$scope.msg = "Tenant Model Description is Mandatory!";
			$log.warn("Tenant Model Description is Mandatory!");
			//$scope.setCurrentStep(1);
			return false;
		}
		
		if(versionDetails.versionType.trim() === "MINOR" && $scope.majorVersions.length == 0){
			$scope.showErrMsg = true;
			$scope.msg = "There is no version for this Tenant Model Name. you can't create Minor Version";
			$log.warn("There is no version for this Tenant Model Name. you can't create Minor Version.");
			//$scope.setCurrentStep(2);
			return false;
		}
		
		if(versionDetails.versionType.trim() === "MINOR" && $scope.majorVersions.length > 0 && versionDetails.majorVersion.trim() === ""){
			$scope.showErrMsg = true;
			$scope.msg = "Please select any version under Minor Version.";
			$log.warn("Please select any version under Minor Version.");
			//$scope.setCurrentStep(2);
			return false;
		}
		
		if(versionDetails.versionDescription.trim() === ''){
			$scope.showErrMsg = true;
			$scope.msg = "Version Description is Mandatory!";
			$log.warn("Version Description is Mandatory!");
			//$scope.setCurrentStep(2);
			return false;
		}
		
		return true;
	};
	
	
	/*Method Call on Load*/
	setTenantModelNames();
	
	/*------------- Handle Next and Previous ---------------------*/
    
	$scope.clear = function(){
		$scope.verImportForm.$setPristine();
		$scope.versionDetails = {
				zipPackage: '',
				name: '',
				description: '',
				versionType: 'MAJOR',
				majorVersion: '',
				versionDescription: ''
			};
			$scope.imported = false;
			$scope.msg = "";
			$scope.showErrMsg = false;
			$scope.showSuccessMsg = false;
			$scope.tenantModels = [];
			$scope.majorVersions = [];
			//$scope.verImportForm.$setPristine();
	};
	
	$scope.showTips = function(){
		$dialogs.notify('Tips','Jar will not be imported if its name and corresponding checksum value matches with any of the existing jars in the system. Existing Jar will be used to continue with import process.');
	};
	
}];