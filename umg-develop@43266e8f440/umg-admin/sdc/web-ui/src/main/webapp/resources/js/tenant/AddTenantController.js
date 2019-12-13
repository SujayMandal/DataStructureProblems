'use strict';
var AddTenantController = ['$scope','$location', 'tenantService','sharedPropertiesService', function($scope, $location, tenantService, sharedPropertiesService) {
	$scope.message = "";
	$scope.showSuccessMessage=false;
	$scope.showMessage = false;
	$scope.batchEnabled=false;
	$scope.messageClass="error-msg";
	
	/* function to decide mandatory field */
	$scope.showMandatoryStar=function(field){
		switch(field){
		case "DRIVER" : 
		case "URL" :
		case "maxIdleTime" :
		case "minPoolSize" :
		case "maxPoolSize" :
		case "USER" :
		case "maxConnectionAge" :
		case "connectionTimeout" :
		case "RUNTIME_BASE_URL" :
			return true;
		default :
			return false;
		}
	}
		
	/*==========================================
	  * save function for saving the tenant in the database
	  */  
	 $scope.save=function(){
			 $scope.checkValue($scope.newTenant);
			 $scope.disableSaveButton = true;
			 tenantService.addTenant($scope.newTenant).then(
					 function(responseData) {
						 if(responseData.error){
							 $scope.showMessage = true;							 
							 $scope.messageClass="error-msg center-div";						
							 $scope.message = "Tenant Save failed. Error Code : " +responseData.errorCode+"-"+responseData.message;
						 }else{
							 $scope.showMessage = true;
							 $scope.messageClass="success-msg center-div";							
							 $scope.message = responseData.message;	
							 if($scope.batchEnabled) {
								 $scope.batchDeploy($scope.newTenant.code);
							 }
						 }
					 }, 
					 function(responseData) {
						 $scope.showMessage = true;
						 $scope.message = "Connection to Server failed. Please try again later.";
					 }
			 );	
	 };
	 
	 /*==========================================
	  * function for deploying the batch
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
	  * loading System Keys	  */  
	 $scope.loadSystemKeys=function(){		
			 tenantService.getSystemKeys().then(
					 function(responseData) {
						 if(responseData.error){
							 $scope.showMessage = true;
							 $scope.message = " Error in loading data : " +responseData.message;		
							 $scope.messageClass="error-msg center-div";
						 }else{
							 $scope.showMessage = false;						
							 for(var i in responseData.response){
								 $scope.tenantConfig.systemKey.key = responseData.response[i].key;
								 $scope.tenantConfig.systemKey.type = responseData.response[i].type;
								 if($scope.tenantConfig.systemKey.key.toUpperCase() == "DRIVER" )
									 $scope.tenantConfig.value = "com.mysql.jdbc.Driver";
								 $scope.newTenant.tenantConfigs.push($scope.tenantConfig);
								 $scope.tenantConfig = {
											systemKey : {
												key : '',
												type : ''
											},
											value :'',
											tenantInfo : null,
											role : ''
										};
							 }
						 }
					 }, 
					 function(responseData) {
						 $scope.showMessage = true;
						 $scope.message = "Connection to Server failed. Please try again later.";
					 }
					 );	
	 };
	 
	 $scope.checkValue = function(tenant) {
		 $scope.showMessage = true;
		 $scope.messageClass = "error-msg center-div";
		 $scope.newTenant.code = $scope.newTenant.code.toLowerCase();
		for(var index in tenant.tenantConfigs){	
			switch (tenant.tenantConfigs[index].systemKey.key.toUpperCase()){
			case "BATCH_ENABLED" :
			case "BULK_ENABLED" :
			case "EMAIL_NOTIFICATIONS_ENABLED" :
			case "FTP" :
			case "EXCEL" :
			case "DEFAULTAUTOCOMMIT" : 
				if (tenant.tenantConfigs[index].systemKey.key == 'defaultAutoCommit')
					tenant.tenantConfigs[index].value = "true";
				else if(tenant.tenantConfigs[index].value == true){
					tenant.tenantConfigs[index].value = "true";
					if (tenant.tenantConfigs[index].systemKey.key.toUpperCase() == 'BATCH_ENABLED'){
						$scope.batchEnabled=true;
					}
				}
				else {
					tenant.tenantConfigs[index].value = "false";
					if (tenant.tenantConfigs[index].systemKey.key.toUpperCase() == 'BATCH_ENABLED'){
						$scope.batchEnabled=false;
					}
				}
			default :
				break;
			}
		}
	};
	 
	 
	
	 /*
	  *  Inital setup on page load
	  */
	 $scope.initialSetup = function() {
		 $scope.showMessage = false;
		 $scope.showSuccessMessage = false;
		 $scope.message = '';	
		 $scope.newTenant = {
					code : '',
					name : '',
					description : '',
					tenantType : "both",
					addresses : [{
						address1 : '',
						address2 : '',
						city : '',
						state : '',
						country : '',
						zip : ''
					}],
					tenantConfigs : [],
					batchEnabled : false,
					authToken : ''
			};
		 $scope.tenantConfig = {
				systemKey : {
					key : '',
					type : ''
				},
				value :'',
				tenantInfo : null,
				role : ''
			};
		 $scope.batchEnabled=false;
		 $scope.loadSystemKeys();
	 };	
	 
	 $scope.pasteTenantCode = function (event) {
		 var item = event.clipboardData.items[0];
		    item.getAsString(function (data) {
		    $scope.newTenant.code = data.trim();
		    var validCode = /^[0-9a-zA-Z]+$/.test(data.trim());
		    if(!validCode)
		    	$scope.newTenant.code = '';
		    });   
		  }; 
		  
     $scope.pasteSysKeyValue = function (event,sysKey) {
    	 var item = event.clipboardData.items[0];
		 item.getAsString(function (data) {
	    	for(var i in $scope.newTenant.tenantConfigs) {
		    	if($scope.newTenant.tenantConfigs[i].systemKey.key == sysKey) {
		    		$scope.newTenant.tenantConfigs[i].value = data.trim();
				    var validValue = /^[0-9]+$/.test(data.trim());
				    if(!validValue) {
				    	$scope.newTenant.tenantConfigs[i].value = '';
				    }
				    break;
		    	}
		    }
		 }); 
     };  
		  
	 $scope.checkIntField = function (fieldKey) {
				switch(fieldKey.toUpperCase()){
					case "MAXIDLETIME" :
					case "MINPOOLSIZE" :
					case "MAXPOOLSIZE" :
					case "MAXCONNECTIONAGE" :
					case "CONNECTIONTIMEOUT" :
					case "FTP_PORT" :
						return true;
					default :
						return false;
			}
	 };
	 
	$scope.showTimeUnit = function (sysKey) {
		switch(sysKey.toUpperCase()){
		case "MAXIDLETIME" :
		case "MAXCONNECTIONAGE" :
		case "CONNECTIONTIMEOUT" :
			return true;
		default :
			return false;
		}
	};
	
	$scope.initialSetup();
	
}];