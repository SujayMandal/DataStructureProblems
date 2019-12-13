'use strict';
var SyndicateCRUDController = [ '$scope', '$filter', '$q', '$dialogs', 'syndicateDataService', 'sharedPropertiesService', function($scope, $filter, $q, $dialogs, syndicateDataService, sharedPropertiesService) {
	$scope.meassageClass="alert-danger";
	$scope.meassage="";
	$scope.finalData="";
	$scope.pastDateFlag=false;
	$scope.viewFlag=false;
	$scope.addVersion=false;
	$scope.dataloadedFlag=false;
	$scope.definitionloadedFlag=false;
	$scope.editFlag=false;
	$scope.syndicateData={};
	$scope.syndicateData.columnHeaderCounter=0;
	$scope.syndicateData.metaData=[{field:"", displayName:""}];
	$scope.syndicateData.keyDefinitions=[];
	$scope.syndicateDataList = [];
	$scope.pagedData = {};
	$scope.row=[];
	$scope.selectedItems=[];
	$scope.syndicateCSVData={};
	$scope.oldValidFrom="";
	$scope.oldValidTo="";
	$scope.totalServerItems=0;
	$scope.filterOptions = {
			    filterText: '',
			    useExternalFilter: false
			  };
			    
	$scope.pagingOptions = {
	            pageSizes: [5, 10, 50, 100],
	            pageSize: 50,
	            currentPage: 1,
	            totalPages:1
	        }; 	
	$scope.metaDataForGrid=[{field:"", displayName:""}];
	$scope.containerNameToUpper=function(){
		$scope.syndicateData.containerName=$scope.syndicateData.containerName.toUpperCase();
	};

	$scope.cruOperation = sharedPropertiesService.getObject();
	
	$scope.dialogType = '';
	
	/**
     * This method will always keep eye for any CRU operation request if Lookup Data.
     */
    $scope.$watch('cruOperation.operation', function (n, o) {
    	if($scope.cruOperation.operation!=undefined){
    	if($scope.cruOperation.operation === "editVersion"){
    		$scope.editFlag=true;
    		$scope.syndicateData.containerName = $scope.cruOperation.containerName;
    		$scope.syndicateData.versionId=$scope.cruOperation.versionId;
    		$scope.fetchVersionInfo($scope.syndicateData.containerName, $scope.cruOperation.versionId);
    		sharedPropertiesService.setObject(null);
    	}else if ($scope.cruOperation.operation === "addVersion"){
    		$scope.addVersion=true;
    		$scope.definitionloadedFlag=true;
    		$scope.setupSyndicateData($scope.cruOperation.containerName);
    		sharedPropertiesService.setObject(null);
    	}  
    	}
    }, true);
    
    $scope.$watch('syndicateData.syndicateXlsxFile', function(n,o){
    	if(n!=o){
    		loadData();
    	}
    });
    
    $scope.$watch('syndicateData.syndicateCsvFile', function(n,o){
    	if(n!=o){
    		loadDefinition();
    	}
    });
    
    $scope.fetchVersionInfo = function(containerName, versionId){
    	syndicateDataService.fetchVersionInfo(containerName, versionId).then(
				 function(responseData) {
					//filtering, removing the value set for boolean and date
					 for(var metaIndex in responseData.response.metaData){
						 var data=responseData.response.metaData[metaIndex];
						 if(data.columnType== 'BOOLEAN' || data.columnType =='DATE' || data.columnType== 'INTEGER'){
							 responseData.response.metaData[metaIndex].columnSize='';
						 }
					 }
					 // filtering end
					$scope.syndicateData = responseData.response;
					 
					 $scope.syndicateData.versionId=$scope.cruOperation.versionId;							 
					 $scope.metaDataForGrid=angular.copy($scope.syndicateData.metaData);
					 $scope.syndicateCSVData=responseData.response.syndicateVersionData;
					 $scope.oldValidFrom=$scope.syndicateData.validFromString;
					 $scope.oldValidTo=$scope.syndicateData.validToString;
					 
	    			 $scope.setPaging();
				 }, 
				 function(responseData) {
					 alert('Failed: ' + responseData);
				 });	
    };
   
    $scope.setupSyndicateData = function(containerName){
    	syndicateDataService.fetchSyndicateDataByName(containerName).then( function(responseData) {
			 if(responseData.error){
				 $scope.meassageClass="alert-danger";
				 $scope.meassage=" ErrorCode: "+responseData.errorCode+" <BR>" +responseData.message.replace("\n", "<BR>");
			 }else{
				
				//removing the value set for boolean and date
				 for(var metaIndex in responseData.response.metaData){
					 var data=responseData.response.metaData[metaIndex];
					 if(data.columnType== 'BOOLEAN' || data.columnType =='DATE' || data.columnType== 'INTEGER'){
						 responseData.response.metaData[metaIndex].columnSize='';
					 }
				 }
				 // filtering end
				$scope.syndicateData = responseData.response;
				$scope.metaDataForGrid=angular.copy($scope.syndicateData.metaData);
		    	if($scope.addVersion){
				 $scope.syndicateData.validFromString = ""; 
				 $scope.syndicateData.validToString = "";
				 $scope.syndicateData.versionName="";
				 $scope.syndicateData.versionDescription="";
		    	}
			 }			  				 
		 } );	
    };
  
	 $scope.synDataGridOptions = { 
			 	rowHeight: 20,
			 	headerRowHeight:18,
				data: 'pagedData', 
				pagingOptions: $scope.pagingOptions,
				totalServerItems:'totalServerItems',
				columnDefs:'metaDataForGrid',
				multiSelect: true,
				selectedItems: $scope.selectedItems,
				enablePaging: true,
				filterOptions:$scope.filterOptions,
				showFilter:true,
				showMenuForEachColumn:false,
				footerTemplate:'resources/partial/model/assumption/ng-grid-syn-footer.html' 
	}; 
	
	 /**==========================================
	  * initial setup for default values
	  */
	 /*$scope.initSetup=function(){
		umgDialog.setupDialog("col-definition",600,500);
		umgDialog.destoryDialog(); 
		umgDialog.setupDialog("col-definition",600,500);
		umgDialog.setupDialog("key-definition",1000,600);
		umgDialog.destoryDialog();
		umgDialog.setupDialog("key-definition",1000,600);		
		umgDialog.setupDialog("upload-note",600,200);

	 };	*/
	 
	 /**==========================================
	  * This method will return column definition for the grid from the first row 
	  */	 
	 $scope.createColumnDefintion= function(firstRow){
		delete $scope.syndicateData.metaData;
		$scope.syndicateData.metaData=[];
		$scope.syndicateData.columnHeaderCounter=0;
		for (var key in firstRow) {
		  if (firstRow.hasOwnProperty(key) && key!= undefined && key!= null) {
			  $scope.syndicateData.metaData[$scope.syndicateData.columnHeaderCounter]=$scope.createGridColumn(key,key);
			  $scope.syndicateData.columnHeaderCounter+=1;
		  }
		}		
	 };	 
	 $scope.createGridColumn=function(fieldName,headerName){
		 return {field:fieldName, displayName:headerName};	
	 };
	 $scope.returnColumnDefintion= function(firstRow){
		 	var data={};
			data.metaData=[];
			data.columnHeaderCounter=0;
			for (var key in firstRow) {
			  if (firstRow.hasOwnProperty(key) && key!= undefined && key!= null) {
				  data.metaData[data.columnHeaderCounter]=$scope.createGridColumn(key,key);
				  data.columnHeaderCounter+=1;
			  }
			}	
			return data;
		 };	
	 /** ==========================================
	  * This method will open the column definition dialog box
	  */
	 $scope.loadColumnDefinition=function(){
		 $scope.dialogType = 'ColDef';
	 };	
	 /** ==========================================
	  * This method will open the column definition dialog box
	  */
	 $scope.updateColumnDefinition=function(){
		 $scope.metaDataForGrid=angular.copy($scope.syndicateData.metaData);
		 $scope.definitionloadedFlag = true;
	//	 umgDialog.closeDialog();
	 };	 
	 /**==========================================
	  * This method will open the key definition dialog box
	  */
	 $scope.loadKeyDefinition=function(){
		 if($scope.syndicateData.keyDefinitions==null || $scope.syndicateData.keyDefinitions.length<0){
			 $scope.keyDefinitionDataStructure();
		 }
		 $scope.dialogType = 'KeyDef';
	 };	
	 /**==========================================
	  * This method will create the keyDefinition data structure
	  */
	 $scope.keyDefinitionDataStructure=function(){
		 $scope.syndicateData.keyDefinitions= [$scope.createKey()];		 
	 };	
	 $scope.createKey=function(){
		 var key={ keyName:" ", sColumnInfos:[] };
		 console.log("$scope.syndicateData.metaData"+$scope.syndicateData.metaData);
		 for(var index in $scope.syndicateData.metaData ){
			var columnHeader=$scope.syndicateData.metaData[index];
			 key.sColumnInfos[index]=$scope.createKeyColumnDef(columnHeader.displayName,false);
		 }	
		 return key; 
	 };	
	 $scope.createKeyColumnDef=function(colName,stat){
		 return {columnName:colName, status:stat};	
	 };
	 /**==========================================
	  * This method will create the new key definition to the data structure
	  */
	 $scope.addNewKeyDefinition=function(){
		 var index=$scope.syndicateData.keyDefinitions.length;
		 $scope.syndicateData.keyDefinitions[index]=$scope.createKey();	
	 };		 
	 /**==========================================
	  * This method will delete a key definition from the list
	  */
	 $scope.deleteKeyDefinition=function(keyName){
		 var deleteIndex=0;
		 for(var index in $scope.syndicateData.keyDefinitions){
			 var key=$scope.syndicateData.keyDefinitions[index];
			 if(key.keyName==keyName){
				 $scope.syndicateData.keyDefinitions.splice(deleteIndex,1);
				 break;
			 }
			 deleteIndex++;
		 }
	 };		 
	 /**==========================================
	  *  Method to display tool tip note
	  */
	 $scope.uploadNote=function(row){
		// umgDialog.setupDialog("upload-note",600,200);
		// umgDialog.openDialog(); 
	 };
	 /**==========================================
	  * This method will return column definition for the grid from the first row,
	  * This method will internally call the  fetchCSVDataList of the controller 
	  */	 

	 function loadData(){
		 $scope.meassageClass="alert-danger";
		 if($scope.syndicateData.syndicateXlsxFile==undefined || $scope.syndicateData.syndicateXlsxFile=={} ){
			 $scope.meassage="Please select the file.....";
		 }else{
			 $scope.meassage="loading......";
			 $scope.fetchCSVDataList($scope.syndicateData.syndicateXlsxFile).then( function(responseData) {
    				 if(responseData.error){
    					 $scope.meassage=" ErrorCode: "+responseData.errorCode+" <BR> Error in loading data : " +responseData.message.replace("\n", "<BR>");
					 }else{
						 if(responseData.response.data.length>0){
	    		    		 var firstRow =responseData.response.data[0];
	    		    		 var colDef= $scope.returnColumnDefintion(firstRow);
	    		    		 if($scope.addVersion){
	    		    			if($scope.syndicateData.metaData.length!=colDef.metaData.length){
	    		    				$scope.meassage="Number of columns in Container version and new file are not matching.....";
	    		    			}else{
	    		    				var successflag=true;
	    		    				for(var i=0;i< colDef.columnHeaderCounter;i++){
	    		    					if(colDef.metaData[i].displayName.toUpperCase() !=$scope.syndicateData.metaData[i].displayName.toUpperCase()){
	    		    						successflag=false;
	    		    						$scope.meassage="Column Name of position "+(i+1)+" missmatch with the Container column definition........ <BR>";
	    		    						$scope.meassage+="Container Column Name: "+$scope.syndicateData.metaData[i].field+", files column Name :"+colDef.metaData[i].field;
	    		    						break;
	    		    					}
	    		    				}
	    		    				
	    		    				if(successflag){
	    		    				$scope.syndicateCSVData=responseData.response.data;
	    		    				$scope.setPaging();
	    		    				$scope.dataloadedFlag=true;
	    		    				$scope.meassageClass="alert-success";
	    		    				$scope.meassage=" Data uploaded successfully....";
	    		    				}
	    		    			}
	    		    		 }else{
	    		    			 $scope.createColumnDefintion(firstRow); 
	    		    			//coping the column definition to description 	
	    						 for(var metaIndex in colDef.metaData){
	    							 var data=colDef.metaData[metaIndex];
	    							 data.description=data.displayName;
	    						 }
	    						 $scope.syndicateData.metaData=colDef.metaData;
	    		    			 $scope.metaDataForGrid = angular.copy(colDef.metaData);
	    		    			 $scope.syndicateCSVData = responseData.response.data;
		    		    		 $scope.dataloadedFlag=true; 
		    		    		 $scope.setPaging();
		    		    		 $scope.meassageClass="alert-success";
		    		    		 $scope.meassage=" Data uploaded successfully...."; 
	    		    		 }
	    		    		 
	    		    	}else{
	    		    		$scope.meassage=" Data not found....";
	    		    	}
					 }
    				  				 
				 }, 
				 function(responseData) {
					 alert('System Failed: ' + responseData);
				 } 
				 );	
			 }
	 };

	 function loadDefinition(){
		 if($scope.syndicateData.syndicateCsvFile==undefined || $scope.syndicateData.syndicateCsvFile=={} ){
			 $scope.meassage="Please select the file.....";
		 } else {
			 $scope.meassage="loading......";
			 $scope.fetchCSVContainerDefinition($scope.syndicateData).then( function(responseData) {
    				 if(responseData.error){
    					 $scope.meassageClass="alert-danger";
    					 $scope.meassage=" ErrorCode: "+responseData.errorCode+" <BR> Error in loading definition : " +responseData.message.replace("\n", "<BR>");
					 }else{
						 if(responseData.response.metaData.length > 0){
							 $scope.meassageClass="alert-success";
							 $scope.meassage=" Definition data found....";
							 for(var metaIndex in responseData.response.metaData){
								 var data=responseData.response.metaData[metaIndex];
								 if(data.columnType== 'BOOLEAN' || data.columnType =='DATE' || data.columnType== 'INTEGER'){
									 responseData.response.metaData[metaIndex].columnSize='';
								 }
							 }
							 $scope.syndicateData.metaData = responseData.response.metaData;
							 $scope.syndicateData.keyDefinitions = responseData.response.keys;
							 $scope.definitionloadedFlag = true;
						 }else{
							$scope.meassageClass="alert-danger";
	    		    		$scope.meassage=" Definition data not found....";
	    		    		$scope.definitionloadedFlag = false;
	    		    	}
					 }
				 }, 
				 function(responseData) {
					 alert('System Failed: ' + responseData);
				 } 
				 );	
		 }
	 }
	 
	 /**==========================================
	  * This method will retrieve the CSV data from server 
	  */
	 $scope.fetchCSVDataList=function(csvFile){
		 var deferred = $q.defer();
		 syndicateDataService.fetchCSVDataList(csvFile).then(
				 function(responseData) {
					 deferred.resolve(responseData);
				 }, 
				 function(responseData) {
					 alert('System Failed: ' + responseData);
				 }
				 );	
		 return deferred.promise;
	 };
	 
	 $scope.fetchCSVContainerDefinition=function(syndicateData){
		 var deferred = $q.defer();
		 syndicateDataService.fetchCSVContainerDefinition(syndicateData).then(
				 function(responseData) {
					 deferred.resolve(responseData);
				 }, 
				 function(responseData) {
					 alert('System Failed: ' + responseData);
				 }
				 );	
		 return deferred.promise;
	 };	 
	 
	 /**==========================================
	  * save method is used for the final save
	  */
	 $scope.save=function(){
		 $scope.metaDataForGrid=angular.copy($scope.syndicateData.metaData);
		 if($scope.validate()){		
			 $scope.syndicateData.syndicateVersionData={};
			 $scope.syndicateData.syndicateVersionData=$scope.syndicateCSVData;
			 $scope.syndicateData.validFromString = $filter('date')($scope.syndicateData.validFromString,"yyyy-MMM-dd HH:mm"); 
			 $scope.syndicateData.validToString = $filter('date')($scope.syndicateData.validToString,"yyyy-MMM-dd HH:mm"); 
			 
			 
		 if($scope.addVersion){
			//Add new container version
			 syndicateDataService.saveNewVersion($scope.syndicateData).then(
					 function(responseData) {
						 if(responseData.error){
							 if(responseData.errorCode=="BSE000033"){
								 $dialogs.confirm('Active From overlaps with Active Until.','<span class="confirm-body"><strong>'+responseData.message+'<strong></span>')
									.result.then(function(btn){
										$scope.syndicateData.action="AGREED_TO_ADJUST_TIME_OVERLAP";
										$scope.save();
							        });
							 }else if(responseData.errorCode=="BSE000034"){
								 $dialogs.confirm('Active From GAP with Active Until.','<span class="confirm-body"><strong>'+responseData.message+'<strong></span>')
									.result.then(function(btn){
										$scope.syndicateData.action="AGREED_TO_ADJUST_TIME_GAP";
										$scope.save();
							        });
							 }
							 $scope.meassage=" ErrorCode: "+responseData.errorCode+" <BR>" +responseData.message.replace("\n", "<BR>");
						 }else{
							 $scope.meassageClass="alert-success";
							 $scope.meassage="Lookup data version has been saved successfully......";  
							 $scope.viewFlag=true;
							 addVersion=false;
						 }						 
					 }, 
					 function(responseData) {
						 alert('System Failed: ' + responseData);
					 }
					 );	
		 }else{
		 //create a new container	 
			 syndicateDataService.save($scope.syndicateData).then(
					 function(responseData) {
						 if(responseData.error){
							 $scope.meassageClass="alert-danger";
							 $scope.meassage=" ErrorCode: "+responseData.errorCode+" <BR>" +responseData.message.replace("\n", "<BR>");
						 }else{
							 $scope.meassageClass="alert-success";
							 $scope.meassage="Lookup data has been saved successfully......";  
							 $scope.viewFlag=true;
						 }						 
					 }, 
					 function(responseData) {
						 alert('System Failed: ' + responseData);
					 }
					 );	 
		 	}
		 }
	 };	 
	 

	 $scope.updateVersion = function(){
		 $scope.meassage="";
		 $scope.meassageClass="alert-danger";
		 $scope.syndicateData.validFromString = $filter('date')($scope.syndicateData.validFromString,"yyyy-MMM-dd HH:mm"); 
		 $scope.syndicateData.validToString = $filter('date')($scope.syndicateData.validToString,"yyyy-MMM-dd HH:mm"); 
		 $scope.syndicateData.oldValidFromStr=$scope.oldValidFrom;
		 $scope.syndicateData.oldValidToStr=$scope.oldValidTo;
		 	 if($scope.syndicateData.description.$invalid){
				 $scope.meassage="Description is mandatory...";
				 return false;
			 }
			 if($scope.syndicateData.validFromString.$invalid){
				 $scope.meassage="From Date is mandatory...";
				 return false;
			 }
			 //validation ends
			 syndicateDataService.updateVersion($scope.syndicateData).then(
					 function(responseData) {
						 if(responseData.error){
							 if(responseData.errorCode=="BSE000033"){
								 $dialogs.confirm('Active From overlaps with Active Until.','<span class="confirm-body"><strong>'+responseData.message+'<strong></span>')
									.result.then(function(btn){
										$scope.syndicateData.action="AGREED_TO_ADJUST_TIME_OVERLAP";
										if($scope.editFlag){
											$scope.updateVersion();	
										}else{
											$scope.save();
										}
							        });
								
							 }else if(responseData.errorCode=="BSE000034"){
								 $dialogs.confirm('Active From GAP with Active Until.','<span class="confirm-body"><strong>'+responseData.message+'<strong></span>')
									.result.then(function(btn){
										$scope.syndicateData.action="AGREED_TO_ADJUST_TIME_GAP";
										if($scope.editFlag){
											$scope.updateVersion();	
										}else{
											$scope.save();
										}
							        });
							 }
							 $scope.meassage=" ErrorCode: "+responseData.errorCode+" <BR>" +responseData.message.replace("\n", "<BR>");
						 }else{
							 $scope.meassageClass="alert-success";
							 $scope.meassage="Lookup data has been saved successfully......";  
							 $scope.viewFlag=true;
						 }						 
					 }, 
					 function(responseData) {
						 alert('System Failed: ' + responseData);
					 }
					 );	
			 
		 };
		 
		 $scope.showTip = function(){
			 var htmltxt = '<ol>'+
								'<li>First row of the sheet should contain the column headers</li>'+
								'<li>Date field should be in <B> DD-MMM-YYYY format </B></li>'+
								'<li>Boolean field should have only <B> True/False </B> values</li>'+
							'</ol>';
			 $dialogs.notify('Tips',htmltxt);
		 };
		
	 
	 /**==========================================
	  * validate method will be called before final save
	  * only basic validations are doing here, all the business validations are 
	  * done in the server side
	  */

	 $scope.validate=function(){
		 $scope.meassageClass="alert-danger";
		 if($scope.syndicateData.containerName == undefined || $scope.syndicateData.containerName.$invalid){
			 $scope.meassage="Lookup Name is mandatory....";
			 return false;
		 }
		 if($scope.syndicateData.description == undefined || $scope.syndicateData.description.$invalid){
			 $scope.meassage="Description is mandatory...";
			 return false;
		 }
		 if($scope.syndicateData.versionName == undefined || $scope.syndicateData.versionName.$invalid){
			 $scope.meassage="Lookup version Name is mandatory....";
			 return false;
		 }
		 if($scope.syndicateData.versionDescription == undefined ||  $scope.syndicateData.versionDescription.$invalid){
			 $scope.meassage="Version Description is mandatory...";
			 return false;
		 }
		 if(($scope.syndicateData.validFromString == undefined) || $scope.syndicateData.validFromString.$invalid){
			 $scope.meassage="From Date is mandatory...";
			 return false;
		 }		

		 if($scope.syndicateData.totalRows == undefined || $scope.syndicateData.totalRows.$invalid){
			 $scope.meassage="Please enter No. of Records field.....";
			 return false;
		 }
		 
		 if(!$scope.validateMetaData()){
			 $scope.meassage+="Column Definition is not completed.....";
			 return false;
		 }
		 if(!$scope.validatekeyDefinition()){
			 $scope.meassage="Key Definition is not completed.....";
			 return false;
		 }
		 if($scope.syndicateData.syndicateXlsxFile.$invalid){
			 $scope.meassage="File not selected properly.....";
			 return false;
		 }
		 $scope.meassage="";
		 return true;
	 };	 
	 /**==========================================
	  * validate method will be called before final save
	  * this method will validate the metadata part 
	  */
	 $scope.validateMetaData=function(){
		 if($scope.syndicateCSVData==undefined||$scope.syndicateCSVData=={}){
			 $scope.meassage="column definition is missing..., please upload the file again";
			 return false; 
		 }
		 
		 if($scope.syndicateData.metaData.length<=0){
			 $scope.meassage="column definition is missing..., please upload the file again";
			 return false;
		 }
		
		 for(var metaIndex in $scope.syndicateData.metaData){
			 var data=$scope.syndicateData.metaData[metaIndex];
			 if(data.displayName==undefined || data.displayName.trim()==""){
				 $scope.meassage="column Name is not defined properly ...... in the line "+(metaIndex) + " , ";
				 return false;
			 }
			 if(data.description==undefined || data.description.trim()==""){
				 $scope.meassage="Description for "+data.displayName + " is missing.., ";
				 return false;
			 }
			 if(data.columnType==undefined || data.columnType.trim()==""){
				 $scope.meassage="columnType for "+data.displayName + " is missing.., ";
				 return false;
			 }
			 if(data.columnSize==undefined && (data.columnType=="STRING")){
				 $scope.meassage="column Size for "+data.displayName + " is missing.., ";
				 return false;
			 }
			 if((data.columnType=="DOUBLE")){
				 if((data.columnSize==undefined||data.columnSize=="") && (data.precision==undefined||data.precision=="")){
					 $scope.meassage="Both total number & precision should not be empty for "+data.displayName+",  ";
					 return false;	 
				 }
			 }
		 }
		 		 
		 return true;
	 };
	 /**==========================================
	  * validate method will be called before final save
	  * this method will validate the key definition part 
	  */
	 $scope.validatekeyDefinition=function(){
		 return true;
	 };
	
    /**==========================================
     * this method should be called for pagination
     */
	 $scope.setPaging = function(){	
		 $scope.totalServerItems = $scope.syndicateCSVData.length;
		 $scope.pagingOptions.totalPages=Math.ceil($scope.totalServerItems/$scope.pagingOptions.pageSize);
		 $scope.setPagingData($scope.syndicateCSVData,$scope.pagingOptions.currentPage,$scope.pagingOptions.pageSize); 
		 
	 };
	 /**==========================================
	  * page change handler
	  */
	 $scope.$watch('pagingOptions', function (newVal, oldVal) {
	     if (newVal !== oldVal && newVal.currentPage !== oldVal.currentPage) {
	    	 $scope.setPaging();}
	     }, true);
		
	 /**==========================================
	  * triming the data based on the page number
	  */
	 $scope.setPagingData = function(data, page, pageSize){	
	     var myPagedData = data.slice((page - 1) * pageSize, page * pageSize);
	     $scope.pagedData = myPagedData;
	     $scope.totalServerItems = data.length;
	     if (!$scope.$$phase) {
	         $scope.$apply();
	     }
	 };	 
	 /**==========================================
	  * first page
	  */
	 $scope.firstPage=function(){
		 $scope.pagingOptions.currentPage=1;
	 };	
	 /**==========================================
	  * next page
	  */
	 $scope.nextPage=function(){
		 var totalPage=Math.ceil($scope.totalServerItems/$scope.pagingOptions.pageSize);
		 if(totalPage>$scope.pagingOptions.currentPage){
		 $scope.pagingOptions.currentPage=$scope.pagingOptions.currentPage+1;
		 }
	 };	
	 /**==========================================
	  * previous page
	  */
	 $scope.previousPage=function(){
		 if($scope.pagingOptions.currentPage>1){
		 $scope.pagingOptions.currentPage=$scope.pagingOptions.currentPage-1;
		 }
	 };	
	 /**==========================================
	  * last page
	  */
	 $scope.lastPage=function(){
		 $scope.pagingOptions.currentPage=Math.ceil($scope.totalServerItems/$scope.pagingOptions.pageSize);
	 };	
	 //------------------------------------------------
	 //Default execution methods while loading the controller
	 //$scope.initSetup(); 
}];

