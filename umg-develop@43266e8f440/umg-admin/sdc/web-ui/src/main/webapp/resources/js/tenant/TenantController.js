'use strict';
var TenantController = ['$scope', 'tenantService','sharedPropertiesService', function($scope, tenantService, sharedPropertiesService) {
	$scope.tenant = "";
	$scope.message = "";
	$scope.showSuccessMessage=false;
	$scope.showMessage = false;
	$scope.batchEnabled=false;
	$scope.batchEnabled_old=$scope.batchEnabled;
	$scope.messageClass="error-msg";

	/*==========================================
	  * save function for saving the model in the database
	  */  
	 $scope.update=function(tenant){
			if ($scope.validate(tenant)) {
			 prepareTenantConfigs(tenant);
			$scope.tenant = tenant;	
			 $scope.disableSaveButton = true;		
			 tenantService.updateTenant($scope.tenant).then(
					 function(responseData) {
						 if(responseData.error){
							 $scope.showMessage = true;							 
							 $scope.messageClass="error-msg center-div";
							 $scope.message = " Error in update : " +responseData.message;
							 prepareTenantInput($scope.tenant);
						 }else{
							 $scope.showMessage = true;
							 $scope.messageClass="success-msg center-div";							
						     $scope.message = responseData.message;		
						     $scope.tenant=responseData.response;
							 prepareTenantInput($scope.tenant);
							 if($scope.batchEnabled != $scope.batchEnabled_old){
								 if($scope.batchEnabled){
									 $scope.batchDeploy(tenant.code);
								 } else {
									 $scope.batchUndeploy(tenant.code);
								 }
							 }
						 }
					 }, 
					 function(responseData) {
						 $scope.showMessage = true;
						 $scope.message = "Connection to Server failed. Please try again later.";
					 }
					 );	
			}
			
		
	 };
	 
	 /*==========================================
	  * loading tenant details	  */  
	 $scope.loadTenantData=function(){		
			 tenantService.getTenantDetails($scope.tenant).then(
					 function(responseData) {
						 if(responseData.error){
							 $scope.showMessage = true;
							 $scope.message = " Error in loading data : " +responseData.message;		
							 $scope.messageClass="error-msg center-div";
						 }else{
							 $scope.showMessage = false;						
							 $scope.tenant=responseData.response;
							 prepareTenantInput($scope.tenant);
						 }
					 }, 
					 function(responseData) {
						 $scope.showMessage = true;
						 $scope.message = "Connection to Server failed. Please try again later.";
					 }
					 );	
		
		
	 };
	 $scope.validate = function(tenant) {
		 $scope.showMessage = true;
		 $scope.messageClass = "error-msg center-div";
		 /*if($scope.updateTenantForm.tenantName.$invalid){
			 if ($scope.updateTenantForm.tenantName.$error.maxlength == true) {				
				 $scope.message="Tenant Name length exceeded [3-45]....";
			 }
			 else {		
				 	
				 $scope.message="Tenant Name length is too small [3-45]....";
			 }
			 return false;
		}
		if($scope.updateTenantForm.tenantDesc.$invalid){
			 if ($scope.updateTenantForm.tenantDesc.$error.maxlength == true) {
				 $scope.message="Tenant Description length exceeded [3-255]....";
			 }
			 else {
				 $scope.message="Tenant Description length is too small [3-255]....";
			 }
			 return false;
		}
		if($scope.updateTenantForm.tenantType.$invalid){
				 if ($scope.updateTenantForm.tenantType.$error.maxlength == true) {
				 $scope.message="Tenant type length exceeded [2-45]....";
			 }
			 else {
				 $scope.message="Tenant type length is too small [2-45]....";
			 }
			 return false;
		}
	
		
		for(var index in tenant.addresses){				
			 if (tenant.addresses[index].address1 == undefined  || tenant.addresses[index].address1.length<2) {
				 $scope.message="Address1 length is too small [2-45]....";
				 return false;
			 }else if(tenant.addresses[index].address1 == undefined  || tenant.addresses[index].address1.length>45){
				 $scope.message="Address1 length exceeded [2-45]....";		
				 return false;
			 }
			 if (tenant.addresses[index].city == undefined  || tenant.addresses[index].city.length<2) {
				 $scope.message="City length is too small [2-45]....";
				 return false;
			 }else if(tenant.addresses[index].city == undefined  || tenant.addresses[index].city.length>45){
				 $scope.message="City length exceeded [2-45]....";		
				 return false;
			 }
			 if (tenant.addresses[index].state == undefined  || tenant.addresses[index].state .length<2) {
				 $scope.message="State length is too small [2-45]....";
				 return false;
			 }else if(tenant.addresses[index].state == undefined  || tenant.addresses[index].state.length>45){
				 $scope.message="State length exceeded [2-45]....";		
				 return false;
			 }			
			 if (tenant.addresses[index].country == undefined  || tenant.addresses[index].country.length<2) {
				 $scope.message="Country length is too small [2-45]....";
				 return false;
			 }else if(tenant.addresses[index].country == undefined  || tenant.addresses[index].country.length>45){
				 $scope.message="Country length exceeded [2-45]....";		
				 return false;
			 }			
			 if (tenant.addresses[index].zip == undefined  || tenant.addresses[index].zip.length<2) {
				 $scope.message="Zip length is too small [2-45]....";
				 return false;
			 }else if(tenant.addresses[index].zip == undefined  || tenant.addresses[index].zip.length>45 ){
				 $scope.message="Zip length exceeded [2-45]....";		
				 return false;
			 }					
		};*/
		
		
	
		 return true;
	};
	 
	 
	
	 /*
	  *  Inital setup on page load
	  */
	 $scope.initialSetup = function() {
		 $scope.tenant = sharedPropertiesService.get("selectedTenantCode");
		 $scope.showMessage = false;
		 $scope.showSuccessMessage = false;
		 $scope.message = '';		
	 };
	 
	function prepareTenantInput(tenant){
		var tenantConfigs = [];
		var element = [];
		var count = 0;
		angular.forEach(tenant.tenantConfigs, function(tenantConfig) {
			if(tenantConfig.systemKey.key == 'BATCH_ENABLED' && tenantConfig.value == 'true'){
				$scope.batchEnabled_old=$scope.batchEnabled;
				$scope.batchEnabled=true;		
			}else if(tenantConfig.systemKey.key == 'BATCH_ENABLED' && tenantConfig.value == 'false'){
				$scope.batchEnabled_old=$scope.batchEnabled;
				$scope.batchEnabled=false;	
			}
			if (count % 2 == 0) {
				element = [];
				element.push(tenantConfig);
				tenantConfigs.push(element);
			} else {
				element.push(tenantConfig);
			}
			count++;
		});
		$scope.tenant.tenantConfigs = tenantConfigs;
	};
	
	function prepareTenantConfigs(tenant){		
		var tenantConfigs = [];		
		angular.forEach(tenant.tenantConfigs, function(tenantConfigArr) {	
			angular.forEach(tenantConfigArr, function(tenantConfig){
				tenantConfigs.push(tenantConfig);				
			});			
		});
		$scope.tenant.tenantConfigs = tenantConfigs;
	};
	
	/*==========================================
	  * save function for deploying the batch
	  */  
	 $scope.batchDeploy=function(tenantCode){								
			 tenantService.batchDeploy(tenantCode).then(
					 function(responseData) {
						 if(responseData.error){
							 $scope.showMessage = true;
							 $scope.messageClass="error-msg center-div";
							 $scope.message = responseData.message;							 
						 }else{
							 $scope.showMessage = true;
							 $scope.messageClass="success-msg center-div";
						     $scope.message = responseData.message;
						 }
					 }, 
					 function(responseData) {
						 $scope.showMessage = true;
						 $scope.message = "Connection to Server failed. Please try again later.";
					 }
			);	
		
	 };
	 
	 /*==========================================
	  * save function for deploying the batch
	  */  
	 $scope.batchUndeploy=function(tenantCode){								
			 tenantService.batchUndeploy(tenantCode).then(
					 function(responseData) {
						 if(responseData.error){
							 $scope.showMessage = true;
							 $scope.messageClass="error-msg center-div";
							 $scope.message = responseData.message;							 
						 }else{
							 $scope.showMessage = true;
							 $scope.messageClass="success-msg center-div";
						     $scope.message = responseData.message;
						 }
					 }, 
					 function(responseData) {
						 $scope.showMessage = true;
						 $scope.message = "Connection to Server failed. Please try again later.";
					 }
			);	
		
	 };
	 
	 $scope.initialSetup();
	 $scope.loadTenantData();
}];

