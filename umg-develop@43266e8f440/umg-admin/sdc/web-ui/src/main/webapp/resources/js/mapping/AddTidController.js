'use strict';
var AddTidController = function($scope, $log, $location, $dialogs,sharedPropertiesService,
		addTidService, tidListDisplayService) {

	/*sujay*/
	$scope.selectedRow={};
	$scope.my_tree_handler = function(branch){
		$scope.selectedRow=branch;
    };
	
    $scope.getQueryOutputAsJson = function(tidSystemInput) {
    	var data = [];
    	if(tidSystemInput!=null)
    		{
		    	for (var i = 0; i < tidSystemInput.length; i++) {   
		    		data.push(tidSystemInput[i].flatenedName);
		    		for(var x = 0; x < tidSystemInput[i].children.length; x++) {
		    			data.push(tidSystemInput[i].children[x].flatenedName);
		    		}
		    	}
    		}
		data.push("                           ");
    	return data;
    }; 
	$scope.getNotApplicableJson = function() {
		var data = [];
		data.push("                           ");
		return data;
	}; 
    
    $scope.createMapDictionary = function(inputMappingViews) {
    	var map = [];
    	for (var i = 0; i < inputMappingViews.length; i++) {     
    			data.push({
    			    key:   inputMappingViews[i].mappedTo,
    			    value: inputMappingViews[i].mappingParam
    			});
    	}
    	function get(k) {
    	    return map[k];
    	}
    	return data;
    };
    
    
    
    $scope.testSelect='[{"description":null,"mandatory":true,"syndicate":false,"name":"PROPSYND","text":"PROPSYND","flatenedName":"PROPSYND","sequence":0,"datatype":{"type":"OBJECT","properties":{},"array":false},"dataTypeStr":"OBJECT","mapped":false,"dataFormat":null,"size":0,"precision":0,"userSelected":false,"nativeDataType":null,"children":[{"description":null,"mandatory":false,"syndicate":false,"name":"fclsNPVP","text":"fclsNPVP","flatenedName":"PROPSYND/fclsNPVP","sequence":0,"datatype":{"type":"DOUBLE","properties":{},"array":false},"dataTypeStr":"DOUBLE","mapped":false,"dataFormat":null,"size":0,"precision":0,"userSelected":false,"nativeDataType":null,"children":null,"value":null,"sqlOutput":false,"exprsnOutput":false,"sqlId":"PROPSYND","expressionId":null,"exposedToTenant":false,"userExposedToTenant":0},{"description":null,"mandatory":false,"syndicate":false,"name":"SOF","text":"SOF","flatenedName":"PROPSYND/SOF","sequence":0,"datatype":{"type":"STRING","properties":{},"array":false},"dataTypeStr":"STRING","mapped":false,"dataFormat":null,"size":0,"precision":0,"userSelected":false,"nativeDataType":null,"children":null,"value":null,"sqlOutput":false,"exprsnOutput":false,"sqlId":"PROPSYND","expressionId":null,"exposedToTenant":false,"userExposedToTenant":0},{"description":null,"mandatory":false,"syndicate":false,"name":"addC","text":"addC","flatenedName":"PROPSYND/addC","sequence":0,"datatype":{"type":"DOUBLE","properties":{},"array":false},"dataTypeStr":"DOUBLE","mapped":false,"dataFormat":null,"size":0,"precision":0,"userSelected":false,"nativeDataType":null,"children":null,"value":null,"sqlOutput":false,"exprsnOutput":false,"sqlId":"PROPSYND","expressionId":null,"exposedToTenant":false,"userExposedToTenant":0},{"description":null,"mandatory":true,"syndicate":false,"name":"evCoP","text":"evCoP","flatenedName":"PROPSYND/evCoP","sequence":0,"datatype":{"type":"DOUBLE","properties":{},"array":false},"dataTypeStr":"DOUBLE","mapped":false,"dataFormat":null,"size":0,"precision":0,"userSelected":false,"nativeDataType":null,"children":null,"value":null,"sqlOutput":false,"exprsnOutput":false,"sqlId":"PROPSYND","expressionId":null,"exposedToTenant":false,"userExposedToTenant":0},{"description":null,"mandatory":true,"syndicate":false,"name":"evED","text":"evED","flatenedName":"PROPSYND/evED","sequence":0,"datatype":{"type":"STRING","properties":{},"array":false},"dataTypeStr":"STRING","mapped":false,"dataFormat":null,"size":0,"precision":0,"userSelected":false,"nativeDataType":null,"children":null,"value":null,"sqlOutput":false,"exprsnOutput":false,"sqlId":"PROPSYND","expressionId":null,"exposedToTenant":false,"userExposedToTenant":0},{"description":null,"mandatory":true,"syndicate":false,"name":"evSD","text":"evSD","flatenedName":"PROPSYND/evSD","sequence":0,"datatype":{"type":"STRING","properties":{},"array":false},"dataTypeStr":"STRING","mapped":false,"dataFormat":null,"size":0,"precision":0,"userSelected":false,"nativeDataType":null,"children":null,"value":null,"sqlOutput":false,"exprsnOutput":false,"sqlId":"PROPSYND","expressionId":null,"exposedToTenant":false,"userExposedToTenant":0},{"description":null,"mandatory":true,"syndicate":false,"name":"fcED","text":"fcED","flatenedName":"PROPSYND/fcED","sequence":0,"datatype":{"type":"STRING","properties":{},"array":false},"dataTypeStr":"STRING","mapped":false,"dataFormat":null,"size":0,"precision":0,"userSelected":false,"nativeDataType":null,"children":null,"value":null,"sqlOutput":false,"exprsnOutput":false,"sqlId":"PROPSYND","expressionId":null,"exposedToTenant":false,"userExposedToTenant":0},{"description":null,"mandatory":true,"syndicate":false,"name":"listED","text":"listED","flatenedName":"PROPSYND/listED","sequence":0,"datatype":{"type":"STRING","properties":{},"array":false},"dataTypeStr":"STRING","mapped":false,"dataFormat":null,"size":0,"precision":0,"userSelected":false,"nativeDataType":null,"children":null,"value":null,"sqlOutput":false,"exprsnOutput":false,"sqlId":"PROPSYND","expressionId":null,"exposedToTenant":false,"userExposedToTenant":0},{"description":null,"mandatory":true,"syndicate":false,"name":"listSD","text":"listSD","flatenedName":"PROPSYND/listSD","sequence":0,"datatype":{"type":"STRING","properties":{},"array":false},"dataTypeStr":"STRING","mapped":false,"dataFormat":null,"size":0,"precision":0,"userSelected":false,"nativeDataType":null,"children":null,"value":null,"sqlOutput":false,"exprsnOutput":false,"sqlId":"PROPSYND","expressionId":null,"exposedToTenant":false,"userExposedToTenant":0},{"description":null,"mandatory":true,"syndicate":false,"name":"offerAD","text":"offerAD","flatenedName":"PROPSYND/offerAD","sequence":0,"datatype":{"type":"STRING","properties":{},"array":false},"dataTypeStr":"STRING","mapped":false,"dataFormat":null,"size":0,"precision":0,"userSelected":false,"nativeDataType":null,"children":null,"value":null,"sqlOutput":false,"exprsnOutput":false,"sqlId":"PROPSYND","expressionId":null,"exposedToTenant":false,"userExposedToTenant":0},{"description":null,"mandatory":true,"syndicate":false,"name":"preMED","text":"preMED","flatenedName":"PROPSYND/preMED","sequence":0,"datatype":{"type":"STRING","properties":{},"array":false},"dataTypeStr":"STRING","mapped":false,"dataFormat":null,"size":0,"precision":0,"userSelected":false,"nativeDataType":null,"children":null,"value":null,"sqlOutput":false,"exprsnOutput":false,"sqlId":"PROPSYND","expressionId":null,"exposedToTenant":false,"userExposedToTenant":0},{"description":null,"mandatory":true,"syndicate":false,"name":"premSD","text":"premSD","flatenedName":"PROPSYND/premSD","sequence":0,"datatype":{"type":"STRING","properties":{},"array":false},"dataTypeStr":"STRING","mapped":false,"dataFormat":null,"size":0,"precision":0,"userSelected":false,"nativeDataType":null,"children":null,"value":null,"sqlOutput":false,"exprsnOutput":false,"sqlId":"PROPSYND","expressionId":null,"exposedToTenant":false,"userExposedToTenant":0},{"description":null,"mandatory":true,"syndicate":false,"name":"redED","text":"redED","flatenedName":"PROPSYND/redED","sequence":0,"datatype":{"type":"STRING","properties":{},"array":false},"dataTypeStr":"STRING","mapped":false,"dataFormat":null,"size":0,"precision":0,"userSelected":false,"nativeDataType":null,"children":null,"value":null,"sqlOutput":false,"exprsnOutput":false,"sqlId":"PROPSYND","expressionId":null,"exposedToTenant":false,"userExposedToTenant":0},{"description":null,"mandatory":true,"syndicate":false,"name":"redSD","text":"redSD","flatenedName":"PROPSYND/redSD","sequence":0,"datatype":{"type":"STRING","properties":{},"array":false},"dataTypeStr":"STRING","mapped":false,"dataFormat":null,"size":0,"precision":0,"userSelected":false,"nativeDataType":null,"children":null,"value":null,"sqlOutput":false,"exprsnOutput":false,"sqlId":"PROPSYND","expressionId":null,"exposedToTenant":false,"userExposedToTenant":0},{"description":null,"mandatory":true,"syndicate":false,"name":"reoED","text":"reoED","flatenedName":"PROPSYND/reoED","sequence":0,"datatype":{"type":"STRING","properties":{},"array":false},"dataTypeStr":"STRING","mapped":false,"dataFormat":null,"size":0,"precision":0,"userSelected":false,"nativeDataType":null,"children":null,"value":null,"sqlOutput":false,"exprsnOutput":false,"sqlId":"PROPSYND","expressionId":null,"exposedToTenant":false,"userExposedToTenant":0},{"description":null,"mandatory":true,"syndicate":false,"name":"reoFD","text":"reoFD","flatenedName":"PROPSYND/reoFD","sequence":0,"datatype":{"type":"STRING","properties":{},"array":false},"dataTypeStr":"STRING","mapped":false,"dataFormat":null,"size":0,"precision":0,"userSelected":false,"nativeDataType":null,"children":null,"value":null,"sqlOutput":false,"exprsnOutput":false,"sqlId":"PROPSYND","expressionId":null,"exposedToTenant":false,"userExposedToTenant":0},{"description":null,"mandatory":true,"syndicate":false,"name":"reoSD","text":"reoSD","flatenedName":"PROPSYND/reoSD","sequence":0,"datatype":{"type":"STRING","properties":{},"array":false},"dataTypeStr":"STRING","mapped":false,"dataFormat":null,"size":0,"precision":0,"userSelected":false,"nativeDataType":null,"children":null,"value":null,"sqlOutput":false,"exprsnOutput":false,"sqlId":"PROPSYND","expressionId":null,"exposedToTenant":false,"userExposedToTenant":0},{"description":null,"mandatory":true,"syndicate":false,"name":"repCP","text":"repCP","flatenedName":"PROPSYND/repCP","sequence":0,"datatype":{"type":"DOUBLE","properties":{},"array":false},"dataTypeStr":"DOUBLE","mapped":false,"dataFormat":null,"size":0,"precision":0,"userSelected":false,"nativeDataType":null,"children":null,"value":null,"sqlOutput":false,"exprsnOutput":false,"sqlId":"PROPSYND","expressionId":null,"exposedToTenant":false,"userExposedToTenant":0},{"description":null,"mandatory":true,"syndicate":false,"name":"reqA","text":"reqA","flatenedName":"PROPSYND/reqA","sequence":0,"datatype":{"type":"DOUBLE","properties":{},"array":false},"dataTypeStr":"DOUBLE","mapped":false,"dataFormat":null,"size":0,"precision":0,"userSelected":false,"nativeDataType":null,"children":null,"value":null,"sqlOutput":false,"exprsnOutput":false,"sqlId":"PROPSYND","expressionId":null,"exposedToTenant":false,"userExposedToTenant":0}],"value":null,"sqlOutput":false,"exprsnOutput":false,"sqlId":"PROPSYND","expressionId":null,"exposedToTenant":false,"userExposedToTenant":0},{"description":null,"mandatory":false,"syndicate":false,"name":"HPAQuery","text":"HPAQuery","flatenedName":"HPAQuery","sequence":0,"datatype":{"type":"DOUBLE","properties":{"dimensions":[-1]},"array":true},"dataTypeStr":"DOUBLE|ARRAY|-1","mapped":false,"dataFormat":null,"size":0,"precision":0,"userSelected":false,"nativeDataType":null,"children":[{"description":null,"mandatory":false,"syndicate":false,"name":"HPA","text":"HPA","flatenedName":"HPAQuery/HPA","sequence":0,"datatype":{"type":"DOUBLE","properties":{},"array":false},"dataTypeStr":"DOUBLE","mapped":false,"dataFormat":null,"size":0,"precision":0,"userSelected":false,"nativeDataType":null,"children":null,"value":null,"sqlOutput":false,"exprsnOutput":false,"sqlId":"HPAQuery","expressionId":null,"exposedToTenant":false,"userExposedToTenant":0}],"value":null,"sqlOutput":false,"exprsnOutput":false,"sqlId":"HPAQuery","expressionId":null,"exposedToTenant":false,"userExposedToTenant":0},{"description":null,"mandatory":false,"syndicate":false,"name":"StigmaQuery","text":"StigmaQuery","flatenedName":"StigmaQuery","sequence":0,"datatype":{"type":"DOUBLE","properties":{"dimensions":[-1]},"array":true},"dataTypeStr":"DOUBLE|ARRAY|-1","mapped":false,"dataFormat":null,"size":0,"precision":0,"userSelected":false,"nativeDataType":null,"children":[{"description":null,"mandatory":false,"syndicate":false,"name":"B1","text":"B1","flatenedName":"StigmaQuery/B1","sequence":0,"datatype":{"type":"DOUBLE","properties":{},"array":false},"dataTypeStr":"DOUBLE","mapped":false,"dataFormat":null,"size":0,"precision":0,"userSelected":false,"nativeDataType":null,"children":null,"value":null,"sqlOutput":false,"exprsnOutput":false,"sqlId":"StigmaQuery","expressionId":null,"exposedToTenant":false,"userExposedToTenant":0},{"description":null,"mandatory":false,"syndicate":false,"name":"B2","text":"B2","flatenedName":"StigmaQuery/B2","sequence":0,"datatype":{"type":"DOUBLE","properties":{},"array":false},"dataTypeStr":"DOUBLE","mapped":false,"dataFormat":null,"size":0,"precision":0,"userSelected":false,"nativeDataType":null,"children":null,"value":null,"sqlOutput":false,"exprsnOutput":false,"sqlId":"StigmaQuery","expressionId":null,"exposedToTenant":false,"userExposedToTenant":0},{"description":null,"mandatory":false,"syndicate":false,"name":"B3","text":"B3","flatenedName":"StigmaQuery/B3","sequence":0,"datatype":{"type":"DOUBLE","properties":{},"array":false},"dataTypeStr":"DOUBLE","mapped":false,"dataFormat":null,"size":0,"precision":0,"userSelected":false,"nativeDataType":null,"children":null,"value":null,"sqlOutput":false,"exprsnOutput":false,"sqlId":"StigmaQuery","expressionId":null,"exposedToTenant":false,"userExposedToTenant":0},{"description":null,"mandatory":false,"syndicate":false,"name":"B4","text":"B4","flatenedName":"StigmaQuery/B4","sequence":0,"datatype":{"type":"DOUBLE","properties":{},"array":false},"dataTypeStr":"DOUBLE","mapped":false,"dataFormat":null,"size":0,"precision":0,"userSelected":false,"nativeDataType":null,"children":null,"value":null,"sqlOutput":false,"exprsnOutput":false,"sqlId":"StigmaQuery","expressionId":null,"exposedToTenant":false,"userExposedToTenant":0},{"description":null,"mandatory":false,"syndicate":false,"name":"B5","text":"B5","flatenedName":"StigmaQuery/B5","sequence":0,"datatype":{"type":"DOUBLE","properties":{},"array":false},"dataTypeStr":"DOUBLE","mapped":false,"dataFormat":null,"size":0,"precision":0,"userSelected":false,"nativeDataType":null,"children":null,"value":null,"sqlOutput":false,"exprsnOutput":false,"sqlId":"StigmaQuery","expressionId":null,"exposedToTenant":false,"userExposedToTenant":0},{"description":null,"mandatory":false,"syndicate":false,"name":"B6","text":"B6","flatenedName":"StigmaQuery/B6","sequence":0,"datatype":{"type":"DOUBLE","properties":{},"array":false},"dataTypeStr":"DOUBLE","mapped":false,"dataFormat":null,"size":0,"precision":0,"userSelected":false,"nativeDataType":null,"children":null,"value":null,"sqlOutput":false,"exprsnOutput":false,"sqlId":"StigmaQuery","expressionId":null,"exposedToTenant":false,"userExposedToTenant":0}],"value":null,"sqlOutput":false,"exprsnOutput":false,"sqlId":"StigmaQuery","expressionId":null,"exposedToTenant":false,"userExposedToTenant":0}]';
	/*"<div id='donate'><label class='default' style='width: 30px'><input type='radio' id='true' name='mandatory_{{row.branch.uid}}' value='true' ng-model='row.branch.mandatory'/><span><b>YES</b></span></label> <label class='default' style='width: 30px'><input type='radio' id='false' name='mandatory_{{row.branch.uid}}' value='false' ng-model='row.branch.mandatory'/> <span> <b>NO</b></span></label> </div>"*/
	
    $scope.testMapping='[{"mappingParam":"debugInfo","mappedTo":"debugInfo"},{"mappingParam":"modelCommonAssumptions","mappedTo":"modelCommonAssumptions"},{"mappingParam":"lpbInputs","mappedTo":"lpbInputs"},{"mappingParam":"PROPSYND/SOF","mappedTo":"testInt2"},{"mappingParam":"HPAQuery","mappedTo":"testInt1"}]';
    
	$scope.col_defs = [
	                   {
	                	   field: "name",
	                	   displayName: "Name",
	                	   sortable : true,
	                	   cellTemplate: "<input class='indented tree-label ng-binding' type='checkbox' tooltip='Skip in Tenant API' tooltip-placement='left' tooltip-trigger='mouseenter' ng-model='row.branch.exposedToTenant' ng-checked='{{row.branch.exposedToTenant}}'/> <span class='indented tree-label ng-binding'>{{row.branch.name}}{{row.branch.apiName}}</span>"
	                   },
	                   {	                	   
	                	   field: "mapping",
	                	   displayName: "Syndicate Query Mapping",
	                	   sortable : true,
	                	   cellTemplate: "<select name='mapping_{{row.branch.uid}}' ng-model='row.branch.mapping' ng-change=cellTemplateScope.updateMap(row.branch.mapping,row.branch.flatenedName) style='width: 450px'><option ng-repeat='option in row.versions' value='{{option}}' ng-selected=cellTemplateScope.checkMap(option,row.branch.flatenedName) >{{option}}</option></select>",
	                   	   cellTemplateScope: {
	                   		   checkMap: function (rowMappingParam,rowMappedTo){
	                   			   for (var i = 0; i < $scope.tidMidMappingData.length; i++) {
	                   				   var mappingParam = $scope.tidMidMappingData[i].mappingParam;
	                   				   var mappedTo = $scope.tidMidMappingData[i].mappedTo;
	                   				   if((rowMappingParam.localeCompare(mappingParam)==0) && (rowMappedTo.localeCompare(mappedTo)==0))
	                   				   {
	                   					   return true;
	                   				   }
	                   			   }
	                			   return false;
	                   		   },
	                   		   updateMap: function (rowMappingParam,rowMappedTo){
	                   			for (var i = 0; i < $scope.tidMidMappingData.length; i++) {
	                   				   var mappedTo = $scope.tidMidMappingData[i].mappedTo;
	                   				   var mappingParam = $scope.tidMidMappingData[i].mappingParam;
	                				   if(rowMappedTo.localeCompare(mappedTo)==0 && mappingParam.localeCompare(mappedTo)!=0)
	                   				   {
	                   					$scope.tidMidMappingData[i].mappingParam=rowMappingParam;
	                   					return;
	                   				   }
	                   			   }
	                			   if(rowMappingParam.localeCompare("                           ")!=0)
	                			   {
	                   			$scope.tidMidMappingData.push({mappingParam: rowMappingParam,mappedTo: rowMappedTo});  
	                			   }
	                   			return;
	                   		   } 
	                   	   }
	                   },
	                   {
	                	   field: "mandatory",
	                	   displayName: "Is Mandatory?",
	                	   sortable : true,
	                	   cellTemplate: "<div id='donate'><label class='default' style='width: 30px'><input type='radio' id='true' name='mandatory_{{row.branch.uid}}' ng-value='true' ng-model='row.branch.mandatory'/><span class='span_class'><b class='b_class'>YES</b></span></label> <label class='default' style='width: 30px'><input type='radio' id='false' name='mandatory_{{row.branch.uid}}' ng-value='false' ng-model='row.branch.mandatory'/> <span class='span_class'> <b class='b_class'>NO</b></span></label> </div> "
	                   },
	                   { 
	                	   field: "dataTypeStr",
	                	   displayName: "DataType",
	                	   sortable : true
	                   },
	                   { 
	                	   field: "syndicate",
	                	   displayName: "Is Syndicate?",
	                	   sortable : true,
	                   	   cellTemplate: "<div id='donate'><label class='default' style='width: 30px' ng-show='{{row.branch.syndicate}}'><input type='radio' id='true' name='syndicate_{{row.branch.uid}}' ng-value='true' ng-model='row.branch.syndicate' disabled/><span class='span_class'><b class='b_class'>YES</b></span></label> <label class='default' style='width: 30px'  ng-hide='{{row.branch.syndicate}}'><input type='radio' id='false' name='syndicate_{{row.branch.uid}}' ng-value='false' ng-model='row.branch.syndicate' disabled/> <span class='span_class'> <b class='b_class'>NO</b></span></label> </div>"
	                   }
	                   ];
	
	function customEditorUsingAngular(params) {
		params.$scope.setSelectionOptions = setSelectionOptions;

		var html = '<span ng-show="!editing" ng-click="startEditing()">{{data.'+params.colDef.field+'}}</span> ' +
		'<select ng-blur="editing=false" ng-change="editing=false" ng-show="editing" ng-options="item for item in setSelectionOptions" ng-model="data.'+params.colDef.field+'">';

		// we could return the html as a string, however we want to add a 'onfocus' listener, which is no possible in AngularJS
		var domElement = document.createElement("span");
		domElement.innerHTML = html;

		params.$scope.startEditing = function() {
			params.$scope.editing = true; // set to true, to show dropdown

			// put this into $timeout, so it happens AFTER the digest cycle,
			// otherwise the item we are trying to focus is not visible
			$timeout(function () {
				var select = domElement.querySelector('select');
				select.focus();
			}, 0);
		};

		return domElement;
	}

	var sideColumnDefs = [
	                      {headerName: "Query Name", field: "queryName",headerGroup: 'Query List',width:200,id:"sortable"},
	                      {headerName: "Sequence", field: "sequence",headerGroup: 'Query List',width:90}
	                      ];

	var sideRowData = [
	                   {queryName:"fgh",sequence:"1"},
	                   {queryName:"fgh",sequence:"2"},
	                   {queryName:"fgh",sequence:"3"},
	                   {queryName:"fgh",sequence:"4"},
	                   {queryName:"fgh",sequence:"5"},
	                   {queryName:"fgh",sequence:"6"},
	                   {queryName:"fgh",sequence:"7"},
	                   {queryName:"fgh",sequence:"8"},
	                   {queryName:"fgh",sequence:"9"},
	                   {queryName:"fgh",sequence:"10"},
	                   {queryName:"fgh",sequence:"11"},
	                   {queryName:"fgh",sequence:"12"},
	                   {queryName:"fgh",sequence:"13"},
	                   {queryName:"fgh",sequence:"14"},
	                   {queryName:"fgh",sequence:"15"},
	                   {queryName:"fgh",sequence:"16"},
	                   {queryName:"fgh",sequence:"17"},
	                   {queryName:"fgh",sequence:"18"},
	                   {queryName:"fgh",sequence:"19"},
	                   {queryName:"fgh",sequence:"20"},
	                   {queryName:"fgh",sequence:"21"},
	                   {queryName:"fgh",sequence:"22"},
	                   {queryName:"fgh",sequence:"23"},
	                   {queryName:"fgh",sequence:"24"},
	                   {queryName:"fgh",sequence:"25"},
	                   {queryName:"fgh",sequence:"26"},
	                   {queryName:"fgh",sequence:"27"},
	                   {queryName:"fgh",sequence:"28"},
	                   {queryName:"fgh",sequence:"29"},
	                   {queryName:"fgh",sequence:"30"}
	                   ];

	$scope.sideGridOptions = {
			columnDefs: sideColumnDefs,
			rowData:  sideRowData,
			enableColResize: true,
			enableSorting: true,
			groupHeaders: true,
			angularCompileRows: true,
			sortingOrder: ['desc','asc',null]
	};

	$scope.rowSelectFlag = false;
	$scope.modalDismiss= false;
	$scope.parentHierarchy="Base";
	$scope.addTid = {};
	$scope.tidProperties = {};
	$scope.modelList = [];
	$scope.derivedModelList = [];
	$scope.myData = [];
	$scope.sysParamUpdate = [];
	$scope.modelselect = false;
	$scope.derivedmodelselect = false;
	$scope.addTid.iomapping = 'INPUTMAPPING';
	$scope.derivedMdlFlg = true;
	$scope.infoField = false;
	$scope.isUpdate = false;
	$scope.ParamName = "";
	$scope.tidUpdate = [];
	$scope.enableParam = false;
	$scope.showDataTypeParam = false;
	$scope.showExpose = true;
	$scope.dataTypeString = "";
	var onlyNumbers = /(^-?\d\d*$)/;
	var doubleFormat = /^\d{1,9}\.\d{1,9}$/;
	$scope.tidName = "";
	$scope.ViewSystemVarInfoDiv = false;
	$scope.mappingGridHeight = "";
	$scope.SysParamSave = true;
	$scope.exposeTntParam = {};
	$scope.versionNo="";
	$scope.apiName="";
	$scope.versionId="";
	//added the below variables to have separate i/p and o/p js trees
	$scope.tidInputTreeJson = [];
	$scope.tidOutputTreeJson = [];
	
	//sujay
	$scope.tidSystemInput= {};
	$scope.tidMidMapping= {};
	$scope.addTid.derivedTidName={};
	$scope.tree_data = [];
	$scope.sysMapping= {};
	
	$scope.tidInputSystemTreeJson = [];
	$scope.midInputTreeJson = {};
	$scope.midOutputTreeJson = {};
	$scope.tidMidInputMappingDataJson = {};
	$scope.tidMidOutputMappingDataJson = {};
	//this is used to show/hide the *(star) for tid/mid nodes when switching between i/p and o/p mappings 
	var switchFlagForShowingStar = false;
	//this flag is set to true if version using this mapping is published/deactivated
	var versionMapped = false;
	//this flag is set to true if version using this mapping is saved/tested
	var saveWithoutValidn = false;
	//added below 2 variables for delete tid if not saved/finalized explicitly during tidcopy
	var tidCopySelected = false;	
	var markCancelled = false;
	//this flag identify if mapping is used any published or deactivated version 
	$scope.publishedOrDeactivated=false;
	//this flag was introduced for slow loading of i/p o/p mapping bug : UMG-1481  
	//this is used as one of the parameter to hide/show the save/finalize button 
	$scope.extracted = true;
	//added for not showing saveMapping confirmation pop-up on click of create/list query button 
	//when new mapping is created
	var modelNotExtracted = true;
	$scope.copiedTidName = "";
	$scope.oldSearchStr="";

	//sets the initial/blank values to parameters used for add-tid param
	var initTID = function() {
		$scope.addTID = {
				id : "",
				paramLabel : "",
				mandatory : false,
				exposedToTenant : false,
				userExposedToTenant : 0,
				dataFormat : "DD-MM-YYYY",
				size : "",
				minLength : "",
				maxLength : "",
				precision : "",
				pattern   : "",
				dataType : "INTEGER",
				defaultValue : "",
				description: "",
				syndicate: false
		};		
		$scope.parentHierarchy="Base";
		$scope.isDataFormat = true;
		$scope.showTIDMessage = false;
		$scope.showFormat = false;
		$scope.showSize = true;
		$scope.showPrecision = false;
		$scope.showStruct = false;
		$scope.showDataType = true;
		$scope.visibleDatatype = false;
		$scope.visibleDataFormat = false;
		$scope.visibleSize = false;		
		$scope.mandatoryDisabled = false;
		$scope.TIDSave = true;
		//added this -- as the new child tid parameter was created even if parent is not 
		//object type when clear button was clicked
		if ($scope.UpdateSave) {
			$scope.UpdateSave = true;
			$scope.TIDSave = false;
		} else {
			$scope.UpdateSave = false;
		}
		$scope.visibleDefaultValue = false;
		$scope.visibleTIDParam = false;
		$scope.visibleInfoTID = false;
		$scope.visibleParamTID = false;
		$scope.crtQueryDisable = false;
		$scope.lstQueryDisable = false;
		$scope.showSysParam = false;
		if ($scope.addTid.iomapping =='OUTPUTMAPPING') {
			$scope.crtQueryDisable = true;
			$scope.lstQueryDisable = true;
			$scope.showSysParam = true;
			$scope.tree_data=$scope.tidOutputTreeJson;
		}
		$scope.createQryRedirFlg = false;	
		$scope.saved = false;
		$scope.extracted = true;
		$scope.finalized = false;
		$scope.isArray = false;
		if (saveWithoutValidn == true) {
			$scope.saved = true;
		}
	};

	$scope.showErrorDialogBtn = false;

	$scope.validationErrors = {};

	$scope.dataTypes = [ "INTEGER", "BOOLEAN", "DOUBLE", "STRING", "DATE" ];

	$scope.dataFormat = [ "DD-MM-YYYY", "DD-MMM-YYYY", "MMM-DD-YYYY","MM-DD-YYYY","YYYY-MM-DD","YYYY-MMM-DD",
	                      "DD/MM/YYYY","DD/MMM/YYYY","MMM/DD/YYYY","MM/DD/YYYY","YYYY/MM/DD","YYYY/MMM/DD"];	




	//------------------ initial page load from tid-list/query page --- start ---

	// setting the button status and flags for published or deactivated version
	$scope.setBtnStatsForPubOrDeact = function() {		
		$scope.saved = true;
		$scope.finalized = true;
		versionMapped = true;
		$scope.crtQueryDisable = true;
		//$scope.lstQueryDisable = true;
		$scope.divgreyout = true;
		$scope.browseDisable = true;
		$scope.tcopyDisable = true;
	};

	//method to get the mapping for editing/viewing on page load from tid-list/query page
	//also set the flags for button hide/view if tid is mapped in a version 
	$scope.getTidMappingsOnTidName = function(tidName) {
		addTidService
		.getTidMidMapping(tidName)
		.then(
				function(responseData) {
					// $scope.showSuccessMessage = true;
					if (responseData.error) {
						console.log();
					} else {

						if (responseData.response
								&& responseData.response != null) {
							var mappingDescriptor = responseData.response;
							// $scope.setDefaultVeiw(mappingDescriptor);
							$scope.renderAll(mappingDescriptor);
							$scope.addTid.modelname = responseData.response.modelName;
							$scope.addTid.derivedmodelname = responseData.response.midName;
							$scope.addTid.tidName = tidName;
							$scope.addTid.apiName = $scope.apiName;
							$scope.addTid.versionNo=$scope.versionNo;
							$scope.addTid.versionId=$scope.versionId;
							$scope.addTid.derivedTidName=responseData.response.tidName;
							$scope.derivedMdlFlg = false;									
							$scope.showMessage = false;
							$scope.showSuccessMessage = false;
							$scope.modelselect = true;
							$scope.derivedmodelselect = true;
							var versionMap = sharedPropertiesService
							.get('VersionMapped');
							if (versionMap == true) {
								$scope.setBtnStatsForPubOrDeact();
							}
							var hideSaveButton = sharedPropertiesService.get('hideSaveButton');
							if (hideSaveButton == true) {
								saveWithoutValidn =  true;
								$scope.saved = true;
							}
						}
						sharedPropertiesService.remove('VersionMapped');
						sharedPropertiesService.remove('hideSaveButton');
					}

				}, function(responseData) {
					alert('Failed: ' + responseData);
				});
	};

	//Initial setup on page redirect from tid list
	$scope.initialSetup = function() {
		//UMG-2237 -- check for redirecting from list query page for published or deactivated
		if(sharedPropertiesService.get('publishedOrDeactivated')!=undefined){
			$scope.publishedOrDeactivated=sharedPropertiesService.get('publishedOrDeactivated');
			$scope.setBtnStatsForPubOrDeact();
			sharedPropertiesService.remove('publishedOrDeactivated');
		}
		if (sharedPropertiesService.contains('tidCallingType')
				&& sharedPropertiesService.get('tidCallingType') == 'edit') {
			var tidName = sharedPropertiesService.get('tidName');
			$scope.tidName = tidName;
			var versionNo = sharedPropertiesService.get('versionNo');
			$scope.versionNo = versionNo;
			var versionId = sharedPropertiesService.get('versionId');
			$scope.versionId = versionId;			
			var apiName = sharedPropertiesService.get('apiName');
			$scope.apiName = apiName;
			//method to get the mapping for editing/viewing 
			$scope.getTidMappingsOnTidName(tidName);
			sharedPropertiesService.remove('tidCallingType');
			sharedPropertiesService.remove('tidName');
			sharedPropertiesService.remove('versionNo');
			sharedPropertiesService.remove('versionId');
			sharedPropertiesService.remove("apiName");
			modelNotExtracted = false;
		}
		/*umgDialog.setupDialog("AddTID", 475, 360);
		umgDialog.destoryDialog();
		umgDialog.setupDialog("AddTID", 475, 360);
		umgDialog.setupDialog("ViewSystemVarInfo", 475, 300);
		umgDialog.destoryDialog();
		umgDialog.setupDialog("ViewSystemVarInfo", 475, 300);		

		umgDialog.setupDialog("ViewTidCopyInfo", 1050, 450);
		umgDialog.destoryDialog();

		umgDialog.setupDialog("ViewErrorInfo", 750, 250);
		umgDialog.destoryDialog();*/
		$scope.ViewErrorInfoDiv = false;
		$scope.copiedTidName="";
	};

	//first method called on page load
	$scope.initialSetup();	

	// -------------- clear and cancel button functions --- start ---

	//event for cancel button to redirect to tidlist screen
	$scope.cancel = function() {
		if($scope.addTid.versionId !=null){
			sharedPropertiesService.put("viewModelApi",$scope.versionId);			
			$location.path('modelApiView');

		}else{
			$location.path("version/umgVersionView");
		}
	};	

	//added for deleting the tid if not saved/finalized explicitly during tid copy
	$scope.deleteMappingIfNotSaved = function (tidNameBfrClear) {
		tidListDisplayService.deleteTidMapping(tidNameBfrClear).then(
				function(responseData) {
					if (responseData.error) {
						alert(" ErrorCode: " + responseData.errorCode + " \n Error while Deleting Tid  "
								+ responseData.message);
					} else {					
						if (markCancelled) {
							markCancelled = false;
							$location.path('tidList');
						}
					}
				});
	};


	//Clearing tid_tree, mid_tree and tidMidMapping
	$scope.clearMappingValues = function() {
		// clearing the tid tree
/*		$("#tid_tree").jstree("destroy");
		$('#tid_tree').jstree({
			'core' : {
				'data' : {}
			}
		});
		$("#tid_tree").jstree("destroy");
*/
		// clearing the mid tree
/*		$("#mid_tree").jstree("destroy");
		$('#mid_tree').jstree({
			'core' : {
				'data' : {}
			}
		});
		$("#mid_tree").jstree("destroy");
*/
		// clear the system param tree
/*		$("#sysPar_tree").jstree("destroy");
		$('#sysPar_tree').jstree({
			'core' : {
				'data' : {}
			}
		});
*///		$("#sysPar_tree").jstree("destroy");
		// clearing mapping data
		$scope.tidMidMappingData = {};
		$scope.tidMidGridDataMap = {};
		$scope.sqlInputMap = {};
	};

	// -------------- clear and cancel button functions --- ends  ---


	// ------------------ radio button switch functions ----- start ----

	// Event handler for input and output mapping radio button
	$scope.extractChangedMapping = function() {
		$log.warn("You confirmed switching input and output mappings ...");
		$scope.extracted = false;
		$scope.showMessage = false;
		$scope.showSuccessMessage = false;
		if ($scope.addTid.iomapping == 'OUTPUTMAPPING') {
			$scope.crtQueryDisable = true;
			$scope.lstQueryDisable = true;
			$scope.showSysParam = true;
			
			$scope.tree_data=$scope.tidOutputTreeJson;
			$scope.sysMapping=$scope.getNotApplicableJson();
		} else {
			$scope.crtQueryDisable = false;
			$scope.lstQueryDisable = false;
			$scope.showSysParam = false;
			
			$scope.tree_data=$scope.tidInputTreeJson;
			$scope.sysMapping=$scope.getQueryOutputAsJson($scope.tidInputSystemTreeJson);//(JSON.parse($scope.testSelect));
		}
		//$scope.saveCrntTreeAndLoadNewTree();
		$scope.cleared = false;
		$scope.setButtonStatus ();
	};

	// saving the current status of the tree and loading new tree
	/*$scope.saveCrntTreeAndLoadNewTree = function () {
		$scope.extracted = false;
		if ($scope.addTid.iomapping == 'INPUTMAPPING') {
			if ($("#tid_tree").jstree(true)._cnt > 0) {
				// save the existing output mapping trees
				var allTidNodes = $("#tid_tree").jstree(true).get_json();
				$scope.tidOutputTreeJson = $scope.getTreeDataAsJson(allTidNodes,'tid_tree');
				$scope.tidMidOutputMappingDataJson = $scope.tidMidMappingData;

				//clearing and generating new tree for input mapping
				$scope.clearMappingValues();
				$scope.renderTree('tid_tree', $scope.tidInputTreeJson);
				$scope.renderTree('sysPar_tree', $scope.tidInputSystemTreeJson);
				$scope.renderTree('mid_tree', $scope.midInputTreeJson);
				$scope.tidMidMappingData = $scope.tidMidInputMappingDataJson;
				$scope.buildTidMidMappingMap();
			}	
		} else {
			if ($("#tid_tree").jstree(true)._cnt > 0) {
				// save the existing input mapping trees
				var allTidNodes = $("#tid_tree").jstree(true).get_json();
				$scope.tidInputTreeJson = $scope.getTreeDataAsJson(allTidNodes,'tid_tree');
				$scope.tidMidInputMappingDataJson = $scope.tidMidMappingData;
				var sysTidNodes=null;
				try {
					sysTidNodes=$("#sysPar_tree").jstree(true).get_json();
				} catch (e) {}
				if(sysTidNodes!=null){
					//$scope.tidInputSystemTreeJson = sysTidNodes;
					$scope.tidInputSystemTreeJson = $scope.getTreeDataAsJson(sysTidNodes,'sysPar_tree');
				}	

				//clearing and generating new tree for ouptut mapping
				$scope.clearMappingValues();
				$scope.renderTree('tid_tree', $scope.tidOutputTreeJson);
				$scope.renderTree('mid_tree', $scope.midOutputTreeJson);
				$scope.tidMidMappingData = $scope.tidMidOutputMappingDataJson;
				$scope.buildTidMidMappingMap();
			}
		}
		switchFlagForShowingStar = true;
		$scope.extracted = true;
	};*/

	// ------------------ radio button switch functions ----- end ----
//	Need to remove below code . For reference not deleting this

	//event for extract button for extracting TID, MID tree and mapping for the selected model
	$scope.extract = function() {
		if ($scope.validate()) {
			var derivedModelName = $scope.addTid.derivedmodelname;			
			var tidNamePassed = null;
			
				tidNamePassed = $scope.selectedAPI[0].tidName;
			
			addTidService.extractTidParams(derivedModelName, tidNamePassed)
					.then(function(responseData) {
						if (responseData.error) {
							//throw e;
							$scope.showMessage = true;
							$scope.showSuccessMessage = false;
							$scope.message = responseData.message;
						} else {
							var mappingDescriptor = responseData.response;
							if (mappingDescriptor != null) {
								
									$scope.tidName = mappingDescriptor.tidName;
									tidCopySelected = true;
								
								$scope.renderAll(mappingDescriptor);
							}
						}
					});
			$scope.showMessage = false;
			$scope.showSuccessMessage = false;
			$scope.cleared = false;
			$scope.setButtonStatus();			
			switchFlagForShowingStar = false;
			modelNotExtracted = false;
		}
		$scope.validationErrors = {};
		$scope.showErrorDialogBtn = false;
	};

	//render tid tree and system paramater tree
	$scope.renderTID = function(tidTreeObj) {
		if (tidTreeObj && tidTreeObj != null) {
			$scope.tidInputTreeJson = tidTreeObj.tidInput;
			$scope.tidOutputTreeJson = tidTreeObj.tidOutput;


			$scope.tidInputSystemTreeJson = tidTreeObj.tidSystemInput;

			if ($scope.addTid.iomapping == 'INPUTMAPPING') {
				
				$scope.tree_data=$scope.tidInputTreeJson;
				$scope.sysMapping=$scope.getQueryOutputAsJson($scope.tidInputSystemTreeJson);//(JSON.parse($scope.testSelect));
				/*$scope.renderTree('tid_tree', $scope.tidInputTreeJson);*/
				/*$scope.renderTree('sysPar_tree', $scope.tidInputSystemTreeJson);*/
			} else {
				
				$scope.tree_data=$scope.tidOutputTreeJson;
				$scope.sysMapping=$scope.getNotApplicableJson();
				/*$scope.renderTree('tid_tree', $scope.tidOutputTreeJson);*/
			} 
		}
	};

	//render mid tree and system paramater tree
	$scope.renderMID = function(midTreeObj) {
		if (midTreeObj && midTreeObj != null) {
			$scope.midInputTreeJson = midTreeObj.midInput;
			$scope.midOutputTreeJson = midTreeObj.midOutput;

			if ($scope.addTid.iomapping == 'INPUTMAPPING') {
				/*$scope.renderTree('mid_tree', $scope.midInputTreeJson);*/
			} else {
				/*$scope.renderTree('mid_tree', $scope.midOutputTreeJson);*/
			}
		}
	};

	//render mappings grids
	$scope.renderTidMidMapping = function(tidMidMapping) {
		if (tidMidMapping && tidMidMapping != null) {
			$scope.tidMidInputMappingDataJson = tidMidMapping.inputMappingViews;
			$scope.tidMidOutputMappingDataJson = tidMidMapping.outputMappingViews;

			if ($scope.addTid.iomapping == 'INPUTMAPPING') {
				$scope.tidMidMappingData = $scope.tidMidInputMappingDataJson;
				//$scope.tidMidMappingData = JSON.parse($scope.testMapping);
			} else {
				$scope.tidMidMappingData = $scope.tidMidOutputMappingDataJson; 
			}
			$scope.buildTidMidMappingMap();
		}
	};

	// build tid-mid mapping data map
	$scope.buildTidMidMappingMap = function () {
		$scope.tidMidGridDataMap = {};
		for (var i = 0; i < $scope.tidMidMappingData.length; i++) {
			var mappingParam = $scope.tidMidMappingData[i].mappingParam;
			var mappedTo = $scope.tidMidMappingData[i].mappedTo;
			if ($scope.tidMidGridDataMap[mappedTo] != null) {
				$scope.tidMidGridDataMap[mappedTo].push(mappingParam);
			} else {
				var mappedTids = [];
				mappedTids.push(mappingParam);
				$scope.tidMidGridDataMap[mappedTo] = mappedTids;
			}
		}
	};
	
	/*$scope.renderTree = function(tree, treeJson) {
		// render tree
		$('#' + tree).on('loading.jstree', function(e, data) {
			$scope.manipulateNode(data, tree);
		}).jstree({
			'core' : {
				'data' : treeJson,
				'check_callback' : true,
				'multiple' : false
			}
		});

		// destroying the tree instance if there is no value
		if (treeJson == null) {
			$('#' + tree).jstree("destroy");
		}
		// bind on mouse hover function
		$('#' + tree).bind("hover_node.jstree", function(e, data) {
			// TODO display properties in a dialog
		});

		$('#' + tree).bind('dehover_node.jstree', function(e, data) {
			// TODO close properties dialog
		});
		var selectedNodeId = "";
		$('#' + tree).bind("select_node.jstree", function(e, data) {
			if (selectedNodeId == data.node.id) {
				data.instance.deselect_node(data.node.id);
				selectedNodeId = "";
			} else {
				selectedNodeId = data.node.id;
			}

		}).jstree(true);
	};*/

	//manipualtes the nodes on loading of the tree
	$scope.manipulateNode = function(data, tree) {
		var allParentNodes = data.instance.settings.core.data;
		$scope.manipulateChildNodes(allParentNodes, tree);
	};

	//adds the * or ^ after loading the tree to mark as mandatory or used as syndicate parameter
	$scope.manipulateChildNodes = function(nodes, tree) {
		if (nodes != null && nodes.length > 0 && 
				(!switchFlagForShowingStar || tree == 'mid_tree')) {

			for (var i = 0; i < nodes.length; i++) {
				var node = nodes[i];
				if (node.mandatory) {
					node.text = node.text
					+ '<span class="mandatoryParam">*</span>';
				}

				if (node.syndicate) {
					node.text = node.text
					+ '<span class="syndicateParam">^</span>';
				}

				var children = node.children;
				if (children != null && children.length > 0) {
					$scope.manipulateChildNodes(children, tree);
				}
			}
		}
	};

	//Returns tree data as json array
	$scope.getTreeDataAsJson = function(allNodes, treeDiv) {
		var nodeArray = [];
		if (allNodes != null && allNodes.length > 0) {
			for (var i = 0; i < allNodes.length; i++) {
				var node = $scope.getTreeNodeById(allNodes[i].id, treeDiv);
				var nodeObj = $scope.buildTreeJson(node);
				if (node.children != null && node.children.length > 0) {
					// build child nodes json
					nodeObj.children = $scope.getTreeDataAsJson($scope
							.prepareChildIds(node.children), treeDiv);
				}
				nodeArray.push(nodeObj);
			}
		}
		return nodeArray;
	};

	//Prepares array of child node ids
	$scope.prepareChildIds = function(node) {
		var childrenIds = [];
		if (node != null && node.length > 0) {
			for (var i = 0; i < node.length; i++) {
				var idObj = {};
				idObj.id = node[i];
				childrenIds.push(idObj);
			}
		}
		return childrenIds;
	};

	//builds data of one node
	$scope.buildTreeJson = function(node) {
		var nodeData = node.original;
		var nodeObj = {};
		nodeObj.description = nodeData.description;
		nodeObj.mandatory = nodeData.mandatory;
		nodeObj.exposedToTenant = nodeData.exposedToTenant;
		nodeObj.userExposedToTenant = nodeData.userExposedToTenant;
		nodeObj.syndicate = nodeData.syndicate;
		nodeObj.name = nodeData.name;
		nodeObj.apiName = nodeDate.apiName;
		nodeObj.text = nodeData.text;
		nodeObj.dataFormat = nodeData.dataFormat;
		nodeObj.size = nodeData.size;
		nodeObj.precision = nodeData.precision;
		nodeObj.userSelected = nodeData.userSelected;
		nodeObj.flatenedName = nodeData.flatenedName;
		nodeObj.sequence = nodeData.sequence;
		nodeObj.datatype = nodeData.datatype;
		nodeObj.value = nodeData.value;
		nodeObj.sqlId = nodeData.sqlId;
		nodeObj.expressionId = nodeData.expressionId;
		nodeObj.exprsnOutput = nodeData.exprsnOutput;
		nodeObj.sqlOutput = nodeData.sqlOutput;
		nodeObj.children = null;
		return nodeObj;
	};

	//returns node details of the given node id from given tree
	$scope.getTreeNodeById = function(nodeId, treeDiv) {
		var node = $("#" + treeDiv).jstree("get_node", nodeId);
		return node;
	};

	//mapping grid variables
	$scope.tidMidMappingData = {};
	$scope.selectedMappings = [];
	$scope.tidMidGridDataMap = {};
	$scope.sqlInputMap = {};
	$scope.mappingGrid = {
			data : 'tidMidMappingData',
			selectedItems : $scope.selectedMappings,
			columnDefs : [ {
				field : "mappingParam",
				displayName : "TID Param",
				cellClass : "mappingGridCell",
				headerClass : "mappingGridHeader"
			}, {
				field : "mappedTo",
				displayName : "MID Param",
				cellClass : "mappingGridCell",
				headerClass : "mappingGridHeader"
			} ],
			multiSelect : false,
			afterSelectionChange : function(data) {
				if ($scope.selectedMappings.length > 0) {
					$scope.markTidMidNode($scope.selectedMappings[0]);
				}
			}
	};

	//event for marking/selecting the nodes in trees
	$scope.markTidMidNode = function(mapping) {
		var mappingParam = mapping.mappingParam;
		var mappedTo = mapping.mappedTo;

		var selectedTidNodeId = $scope.getSelectedNodeIdFromTree('tid_tree');
		var selectedMidNodeId = $scope.getSelectedNodeIdFromTree('mid_tree');
		var selectedSqlNodeId = $scope.getSelectedNodeIdFromTree('sysPar_tree');

		var allTidNodes = $("#tid_tree").jstree(true).get_json('#', {
			'flat' : true
		});
		for (var i = 0; i < allTidNodes.length; i++) {
			var node = $scope.getTreeNodeById(allTidNodes[i].id, 'tid_tree');
			if (node.original.flatenedName == mappingParam) {
				if (node.id != selectedTidNodeId) {
					if (selectedTidNodeId != null
							&& selectedTidNodeId.trim().length > 0) {
						$("#tid_tree").jstree('deselect_node',
								selectedTidNodeId);
					}
					$("#sysPar_tree")
					.jstree('deselect_node', selectedSqlNodeId);
					$("#tid_tree").jstree("select_node", node.id);
				}
				break;
			}
		}

		var allSqlNodes;
		try {
			allSqlNodes = $("#sysPar_tree").jstree(true).get_json('#', {
				'flat' : true
			});
		} catch (e) {
			// ignore error sys_par tree may not have been initialized
			allSqlNodes = [];
		}
		for (var i = 0; i < allSqlNodes.length; i++) {
			var node = $scope.getTreeNodeById(allSqlNodes[i].id, 'sysPar_tree');
			if (node.original.flatenedName == mappingParam) {
				if (node.id != selectedSqlNodeId) {
					if (selectedSqlNodeId != null
							&& selectedSqlNodeId.trim().length > 0) {
						$("#sysPar_tree").jstree('deselect_node',
								selectedSqlNodeId);
					}
					$("#tid_tree").jstree('deselect_node', selectedTidNodeId);
					$("#sysPar_tree").jstree("select_node", node.id);
					// the selected node was clearing for system param tree,
					// hence to set the selected node
					$("#sysPar_tree").jstree("select_node", node.id);
				}
				break;
			}
		}

		var allMidNodes = $("#mid_tree").jstree(true).get_json('#', {
			'flat' : true
		});
		for (var i = 0; i < allMidNodes.length; i++) {
			var node = $scope.getTreeNodeById(allMidNodes[i].id, 'mid_tree');
			if (node.original.flatenedName == mappedTo) {
				if (node.id != selectedMidNodeId) {
					if (selectedMidNodeId != null
							&& selectedMidNodeId.trim().length > 0) {
						$("#mid_tree").jstree('deselect_node',
								selectedMidNodeId);
					}
					$("#mid_tree").jstree("select_node", node.id);
				}
				break;
			}
		}
	};

	//Returns the selected node id from the given tree
	$scope.getSelectedNodeIdFromTree = function(treeDiv) {
		var tree = $('#' + treeDiv).jstree(true);
		var node = '';
		var nodeId;
		try {
			node = tree.get_selected();
			if (node != null && node.length > 0) {
				nodeId = node[0];
			}
		} catch (e) {
			// ignore the error as jstree maynot have initialized
			node = "";
		}
		return nodeId;
	};

	//sujay experiment
	$scope.test= function(tree_data){
		console.log('Data retreived', tree_data);
	};

	//render all the trees
	$scope.renderAll = function(mappingDescriptor) {
		if (mappingDescriptor != null) {
			//sujay
			$scope.tidMidMapping= mappingDescriptor.tidMidMapping;
			$scope.tidMidOutputMappingViews= $scope.tidMidMapping.outputMappingViews;
			$scope.tidSystemInput=mappingDescriptor.tidTree.tidSystemInput;
			
			// clearing the js trees and mapping values
			$scope.clearMappingValues();
			$scope.extracted = false;
			var tidTreeObj = mappingDescriptor.tidTree;
			$scope.renderTID(tidTreeObj);
			var midTreeObj = mappingDescriptor.midTree;
			$scope.renderMID(midTreeObj);
			$scope.sqlInputMap = mappingDescriptor.queryInputs;
			//$scope.setHeightOfMappingGrid();
			var mappingObj = mappingDescriptor.tidMidMapping;
			$scope.renderTidMidMapping(mappingObj);
			if(mappingDescriptor.copiedTidName!=null){
				$scope.copiedTidName = mappingDescriptor.copiedTidName;
			}
			$scope.extracted = true;
			//sujay
			$scope.tidMidMapping= mappingDescriptor.tidMidMapping;
			$scope.tidSystemInput=mappingDescriptor.tidTree.tidSystemInput;
		}
	};

	//event for save/finalize tid-mid mapping
	$scope.saveTidMidMapping = function(tree_data,sqlOpenReq) {
		$scope.showMessage = false;
		$scope.showSuccessMessage = false;
		$scope.message = "";
		var type = $scope.addTid.iomapping;
		var tidJson; 
		var mapping;
		if (type == 'INPUTMAPPING') {
			tidJson = '"tidTree" : {"tidInput" : '
				+ JSON.stringify(tree_data)
				+ ', "tidOutput" : ' 
				+ JSON.stringify($scope.tidOutputTreeJson) +',"tidSystemInput":'+ JSON.stringify($scope.tidSystemInput) +"}";
		} else if (type ='OUTPUTMAPPING') {
			tidJson = '"tidTree" : {"tidInput" : '
				+ JSON.stringify($scope.tidInputTreeJson)
				+ ', "tidOutput" : ' 
				+ JSON.stringify(tree_data) +',"tidSystemInput":'+ JSON.stringify($scope.tidSystemInput) +"}";
		} else {
			alert('An error occurred while building tree data.');
			return false;
		}
		mapping = '"tidMidMapping": { "inputMappingViews": '+JSON.stringify($scope.tidMidMappingData)+',"outputMappingViews": '+JSON.stringify($scope.tidMidOutputMappingViews)+'}'; //sujay
		var mappingDescriptor = '{"tidName" : "' + $scope.addTid.derivedTidName						
		+ '", "midName" : "' + $scope.addTid.derivedmodelname
		+ '", "modelName" : "' + $scope.addTid.modelname
		+ '", "copiedTidName" : "' + $scope.copiedTidName + '",'
		+ tidJson + ',' + mapping + '}';
		//.saveMapping(mappingDescriptor, type, validate)
		addTidService
		.saveMapping(mappingDescriptor, true)
		.then(
				function(responseData) {
					if (responseData != null
							&& !responseData.error) {
						$scope.showMessage = false;
						$scope.showSuccessMessage = true;
						$scope.message = responseData.message;
						$scope.tidName = responseData.response.tidName;
						if (sqlOpenReq == true) {
							$scope.setParmsAndRedirectToQryPage();
						}
						$scope.showErrorDialogBtn = false;
						tidCopySelected = false;
					} else {
						$scope.showMessage = true;
						$scope.showSuccessMessage = false;
						$scope.message = responseData.message;
						$scope.validationErrors = responseData.response.validationErrors;
						$scope.showErrorDialogBtn = true;
					}
					$scope.cleared = true;
					$scope.saved = true;
				}, function(responseData) {
					alert('Failed: ' + responseData);
				});
	};

	// setting the button statuses for save/finalize and create/list query based on the status of mapping
	$scope.setButtonStatus = function() {
		if (versionMapped == true) {
			$scope.saved = true;
			$scope.finalized = true;
			$scope.crtQueryDisable = true;
			//$scope.lstQueryDisable = true;
		} else {
			$scope.finalized = false;
			$scope.saved = false;
			if (saveWithoutValidn == true) {
				$scope.saved = true;
			}
		}
	};

	/*
	 * ========================================== validating the form
	 */

	$scope.validate = function() {
		$scope.showMessage = true;
		if ($scope.AddTid.inoutmapping.$invalid) {
			$scope.message = "Please select Input or Output Mapping";
			return false;
		}

		if ($scope.AddTid.inoutmapping.$modelValue == ""
			|| $scope.AddTid.inoutmapping.$modelValue == undefined) {
			$scope.message = "Please select Input or Output Mapping";
			return false;
		}

		return true;
	};


	//--------------- add/update/delete/clear tid functions ----- start -----

	//Add Tid pop up screen
	$scope.addParamTID = function() {
		$scope.showMessage = false;
		$scope.showExpose = true;
		$scope.modalDismiss = "";
		$('#myModal').modal('show');
		mYinit();
		//do validation to check wether node is of object type then add the lines in else part
		
		if($scope.selectedRow.flatenedName && $scope.selectedRow.datatype.type.toUpperCase() == "OBJECT")
		{
			$scope.rowSelectFlag = true;
			$scope.showMessage = false;
			$scope.showAddDiv = true;
			$scope.isUpdate = false;
			
			$scope.showSize = true;
			$scope.TIDSave = true;
			$scope.UpdateSave = false;
		}
		else
		{
			$scope.rowSelectFlag = false;
			/*$scope.showMessage = true;
			$scope.showSuccessMessage = false;
			$scope.message = "Selected node is not Object Type to add TID Parameter";
			return false;*/
		}
	};

	//Update Tid pop up screen
	$scope.updateTID = function() {
		$scope.isUpdate = true;
		$scope.isArray = false;
		$scope.tidProperties = {};
		var property={};
		$scope.showDataType = true;
		$scope.showExpose = true;
		$scope.showMessage = false;
		$scope.showSuccessMessage = false;
		var selectedTID = $('#tid_tree').jstree(true);
		var selectedNode = selectedTID.get_selected();
		var structLabel = "";
		$scope.tidUpdate = selectedTID._model.data[selectedNode[0]];
		if (!selectedNode.length || selectedNode == null) {
			$scope.showMessage = true;
			$scope.showSuccessMessage = false;
			$scope.message = "Please select a Node to Edit";
			return false;
		} else if (selectedNode != null || selectedNode.length > 0) {
			$scope.showMessage = false;
			$scope.showStruct = false;
			/*umgDialog.setupDialog("AddTID", 475, 360);
			umgDialog.openDialog();*/
			var paramLabel = $scope.tidUpdate.text;
			var tidOriginal = $scope.tidUpdate.original;
			var dataTypeLabel = tidOriginal.datatype.type;
			var structName = tidOriginal.datatype.array;
			property = $scope.tidUpdate.original.datatype.properties;
			var selectedNodeFullName = tidOriginal.flatenedName;
			var editParamLabel = false;
			if ($scope.tidMidMappingData != null
					&& $scope.tidMidMappingData.length > 0) {
				for (var i = 0; i < $scope.tidMidMappingData.length; i++) {
					if ($scope.tidMidMappingData[i].mappingParam == selectedNodeFullName) {
						editParamLabel = true;
					}
				}
			}
			$scope.tidProperties = property;
			if (dataTypeLabel) {
				dataTypeLabel = dataTypeLabel.toUpperCase();
			}

			if (paramLabel.indexOf('<span') != -1) {
				paramLabel = paramLabel.substring(0, paramLabel
						.indexOf('<span'));
			}
			var selected = tidOriginal.userSelected;
			if (selected) {
				$scope.visibleInfoTID = false;
				if(editParamLabel){
					$scope.visibleParamTID = true;
				}else{
					$scope.visibleParamTID = false;
				}

				$scope.enableParam = true;
				$scope.TIDClear = true;
			} else if (selected == undefined || !selected) {
				$scope.visibleInfoTID = true;
				$scope.visibleParamTID = true;
				$scope.TIDClear = false;
				$scope.enableParam = false;
				if (dataTypeLabel == "OBJECT") {
					$scope.showStruct = true;
					$scope.showSize = false;
					$scope.showStringLength = false;
					$scope.showPrecision = false;
					structLabel = dataTypeLabel;
				}if (structName) {
					$scope.showStruct = true;
					$scope.showSize = false;
					$scope.showPrecision = false;
					$scope.showStringLength = false;
					structLabel = "Array";
					$scope.isArray = true;
				} if(dataTypeLabel == "OBJECT" && structName){
					$scope.showStruct = true;
					$scope.showSize = false;
					$scope.showPrecision = false;
					$scope.showStringLength = false;
					$scope.showDataType = false;
					structLabel = "Array";
				}
			}
			$scope.addTID = {
					paramLabel : paramLabel,
					mandatory : tidOriginal.mandatory,
					exposedToTenant : tidOriginal.exposedToTenant,
					userExposedToTenant : tidOriginal.userExposedToTenant,
					dataType : dataTypeLabel,
					structure : structLabel,
					dataFormat : property.pattern,
					size : property.totalDigits,
					minLength : property.minLength,
					maxLength : property.maxLength,
					precision : property.fractionDigits,
					pattern   : property.pattern,
					defaultValue : property.defaultValue
			};
			$scope.mandatoryDisabled = false;
			if (versionMapped == true) {
				$scope.TIDSave = false;
				$scope.UpdateSave = false;
			} else {
				$scope.TIDSave = false;
				$scope.UpdateSave = true;
			}
			$scope.showAddDiv = true;
		}
	};

	//Delete Tid pop up screen
	$scope.deleteTIDParam = function() {
		$scope.showSuccessMessage = false;
		var tidUpdate = $('#tid_tree').jstree(true);
		var selectedNode = tidUpdate.get_selected();
		if (!selectedNode.length) {
			$scope.showMessage = true;
			$scope.message = "Please select a Node to Delete";
			return false;
		} else {
			var selectedNodeFullName = tidUpdate._model.data[selectedNode[0]].original.flatenedName;
			if ($scope.tidMidMappingData != null
					&& $scope.tidMidMappingData.length > 0) {
				for (var i = 0; i < $scope.tidMidMappingData.length; i++) {
					if ($scope.tidMidMappingData[i].mappingParam == selectedNodeFullName) {
						$scope.message = "Unable to delete node as it participates in one or more mappings";
						$scope.showMessage = true;
						return false;
					}
				}
			}

			if ($scope.sqlInputMap != null) {
				var queryRef = $scope.sqlInputMap[selectedNodeFullName];
				if (queryRef != null) {
					$scope.message = "Parameter used in query " + queryRef;
					$scope.showMessage = true;
					return false;
				}
			}
		}
		var displayMsg = 'Are you sure you want to Delete TID Parameter';
		var dialogOptions = {
				closeButtonText : 'Cancel',
				actionButtonText : 'Delete',
				headerText : 'Delete TID Parameter?',
				bodyText : displayMsg,
				callback : function() {
					$log.warn("You confirmed TID Parameter Deletion ...");
					$scope.deleteTID();
				}
		};
		/*dialogService.showModalDialog({}, dialogOptions);*/
	};

	//deleting the tid node
	$scope.deleteTID = function() {
		var tidUpdate = $('#tid_tree').jstree(true);
		var selectedNode = tidUpdate.get_selected();
		if (!selectedNode.length) {
			$scope.showMessage = true;
			$scope.message = "Please select a Node to Delete";
			return false;
		} else {
			$scope.showMessage = false;
			tidUpdate.delete_node(selectedNode);
		}
		$scope.cleared = false;
		$scope.saved = false;
		if (saveWithoutValidn == true){
			$scope.saved = true;
		}
		$scope.finalized = false;
	};

	//event for save on add tid pop-up which Adds new node to the tid parameter
	
	var myJsonObj={};
	
	var mYinit=function(){
		
		initTID();
		
	}
	
	
	$scope.addNewTidParameter = function(addTIDParam) {
		var flag = $scope.validateTID(addTIDParam);
		/*alert(flag);*/
		var appendFlag = false;
		if (flag) {
			var properties = {};
			var ParamName = "";
			var flatened = "";
			var dataFormat="";
			$scope.modalDismiss = "modal";
			
			if($scope.parentHierarchy == "Base")
			{
				ParamName = addTIDParam.paramLabel;
				flatened = addTIDParam.paramLabel;
				appendFlag = false;
			}
			else
			{
				ParamName = $scope.selectedRow.flatenedName;
				flatened = ParamName + "/" + addTIDParam.paramLabel;
				appendFlag = true;
			}
			
			if (!((addTIDParam.dataType == "DOUBLE")
					|| (addTIDParam.dataType == "STRING") || (addTIDParam.dataType == "NUMERIC"))) {
				addTIDParam.size = "";
			}
			if (!((addTIDParam.dataType == "DOUBLE") || (addTIDParam.dataType == "NUMERIC"))) {
				addTIDParam.precision = "";
			}
			
			if (addTIDParam.mandatory) {
				addTIDParam.paramLabel = addTIDParam.paramLabel
				+ '<span class="mandatoryParam">*</span>';
			}
			var dataPatternStr = "";
			dataPatternStr=createPattern(addTIDParam);
			if (addTIDParam.dataType && addTIDParam.dataType == "DATE" && addTIDParam.dataFormat) {
				dataFormat = addTIDParam.dataFormat;
			}else {
				dataFormat = "";
			}
			
			myJsonObj["description"] = addTIDParam.description;
			myJsonObj["mandatory"] = addTIDParam.mandatory;
			myJsonObj["syndicate"] = addTIDParam.syndicate;
			myJsonObj["name"] = addTIDParam.paramLabel;
			myJsonObj["text"] = addTIDParam.paramLabel;
			myJsonObj["flatenedName"] = flatened;
			/*myJsonObj["sequence"] = addTIDParam.sequence;*/
			myJsonObj["datatype"] = {"type" : addTIDParam.dataType,
								 "properties" : $scope.createDatatypeProperties(addTIDParam),
								 "array" : false
								 };
			myJsonObj["dataTypeStr"] = dataPatternStr;
			/*myJsonObj["mapped"] = false;*/
			myJsonObj["dataFormat"] = dataFormat;
			myJsonObj["size"] = addTIDParam.size;
			myJsonObj["precision"] = addTIDParam.precision;	
			myJsonObj["userSelected"] = true;
			myJsonObj["nativeDataType"] = null;
			myJsonObj["children"] = null;
			myJsonObj["value"] = addTIDParam.defaultValue;
			myJsonObj["sqlOutput"] = false;
			myJsonObj["exprsnOutput"] = false;
			myJsonObj["sqlId"] = null;
			myJsonObj["expressionId"] = null;
			myJsonObj["exposedToTenant"] = addTIDParam.exposedToTenant;	
			myJsonObj["userExposedToTenant"] = addTIDParam.userExposedToTenant;
		
			//apending myJsonObj based on flag
			
			var JsonStr = JSON.stringify(myJsonObj);
			
			if(appendFlag)
			{
				
				$scope.selectedRow.children.push(myJsonObj);
			}
			else
			{
				$scope.tidInputTreeJson.push(myJsonObj);
				if ($scope.addTid.iomapping == 'INPUTMAPPING') {

					$scope.tree_data=$scope.tidInputTreeJson;
				}
			}
			
			/*saveTidMidMapping($scope.tree_data,false);*/
			/*alert(JSON.stringify($scope.tree_data));*/
			$scope.showTIDMessage = false;
			//umgDialog.closeDialog();
		}
		$scope.cleared = false;
		$scope.setButtonStatus();
	};

	//create properties for add new Tid param
	$scope.createDatatypeProperties = function(addTIDParam){
		var properties = {};
		if(addTIDParam.defaultValue){
			properties.defaultValue=addTIDParam.defaultValue;
		}
		if (addTIDParam.dataType == "DOUBLE") {
			if(addTIDParam.size)properties.totalDigits = addTIDParam.size;
			if(addTIDParam.precision)properties.fractionDigits = addTIDParam.precision;
		}
		else if(addTIDParam.dataType == "INTEGER"){
			if(addTIDParam.size)properties.totalDigits = addTIDParam.size;
		}
		else if(addTIDParam.dataType == "STRING"){
			if(addTIDParam.minLength)properties.minLength = addTIDParam.minLength;
			if(addTIDParam.maxLength)properties.maxLength = addTIDParam.maxLength;
			if(addTIDParam.pattern)properties.pattern = addTIDParam.pattern;
		}
		else if(addTIDParam.dataType == "DATE" || addTIDParam.dataType == "DATETIME"){
			if(addTIDParam.dataFormat){
				properties.pattern = addTIDParam.dataFormat;
			}
		}
		return properties;
	};

	//event for save on update tid pop-up which updates the tid node 
	$scope.updtTidParamWithTntExposAlrt = function(tidParam){
		$log.info("Showing Expose Alert");
		$scope.exposeTntParam = tidParam;

		var displayMsg = 'The concerned parameter is an object; hence all its child elements will inherit this property and skipped from Tenant API';
		var dialogOptions = {
				closeButtonText : 'Cancel',
				actionButtonText : 'Continue',
				headerText : 'Skip in Tenant API ?',
				bodyText : displayMsg,
				callback : function() {
					$scope.exposeTntParam.exposedToTenant = true;
					$scope.exposeTntParam.userExposedToTenant = 1;
					updateTidParameter($scope.exposeTntParam);
					$log.info("You agreed to Skip this Parameter in Tenant API.");
				},
				cancelFunctionFlag : true,
				close : function () {
					$scope.exposeTntParam.exposedToTenant = false;
					$scope.exposeTntParam.userExposedToTenant = 0;
					updateTidParameter($scope.exposeTntParam);
				}
		};

		if(tidParam.dataType == "OBJECT" && $scope.exposeTntParam.exposedToTenant){
			$scope.exposeTntParam.exposedToTenant = false;
			$log.info("This Parameter will be available in Tenant API.");
			/*dialogService.showModalDialog({}, dialogOptions);*/
		} else if ($scope.exposeTntParam.exposedToTenant) {
			$scope.exposeTntParam.userExposedToTenant = 1;
			updateTidParameter($scope.exposeTntParam);
		} else {
			$scope.exposeTntParam.exposedToTenant = false;
			$scope.exposeTntParam.userExposedToTenant = 2;
			updateTidParameter($scope.exposeTntParam);
		}
	};

	//updates the tid parameter
	var updateTidParameter = function(addTIDParam) {
		var ref = [];
		var sel = [];
		var selectedItem = "";
		var selectedNode = {};
		var selectedId = "";
		var parentNode = "";

		ref = $('#tid_tree').jstree(true);
		sel = ref.get_selected();	
		if (!sel.length || sel == null) {
			selectedItem = "#";
			selectedId = "#";
			parentNode = "#";
		} else if (sel != null || sel.length > 0) {
			selectedItem = sel[0];
			selectedId = sel[0];
			parentNode = ref._model.data[selectedItem].parent;
		}
		selectedNode = ref._model.data[parentNode];
		var levelNodes = selectedNode.children;
		var nameNode = "";
		var chileNode = {};
		if (levelNodes.length > 0) {
			for (var i = 0; i < levelNodes.length; i++) {
				chileNode = ref._model.data[levelNodes[i]];			
				if (!(selectedId == chileNode.id)) {
					nameNode = chileNode.original.name;
					if(!uniqueNameCheck(nameNode,addTIDParam.paramLabel)){
						return false;
					}
				}
			}
		}

		if ($scope.enableParam) {
			if (!$scope.validateTID(addTIDParam)) {
				return false;
			}
		}
		var updatedLabel = $scope.tidUpdate.text;
		var paramName = $scope.tidUpdate.text;

		if (paramName.indexOf('<span') != -1) {
			paramName = paramName.substring(0, paramName.indexOf('<span'));
		}
		updatedLabel = addTIDParam.paramLabel;
		if (addTIDParam.mandatory) {
			updatedLabel = updatedLabel
			+ '<span class="mandatoryParam">*</span>';
		} else {
			if (updatedLabel.indexOf('*') != -1) {
				updatedLabel = updatedLabel.substring(0, updatedLabel
						.indexOf('<span'));
			}
		}

		var orgFlatenedName = $scope.tidUpdate.original.flatenedName;

		if (orgFlatenedName.indexOf('/') != -1) {
			orgFlatenedName = orgFlatenedName.substring(0, orgFlatenedName
					.lastIndexOf("/"))
					+ '/' + paramName;
		} else {
			orgFlatenedName = paramName;
		}

		ref.rename_node(sel, updatedLabel, addTIDParam.paramLabel);
		$scope.tidUpdate.text = updatedLabel;
		$scope.tidUpdate.original = {};
		$scope.tidUpdate.original.mandatory = addTIDParam.mandatory;
		$scope.tidUpdate.original.exposedToTenant = addTIDParam.exposedToTenant;
		$scope.tidUpdate.original.userExposedToTenant = addTIDParam.userExposedToTenant;
		$scope.tidUpdate.original.text = updatedLabel;
		var datatype = {};
		datatype.type = addTIDParam.dataType;
		var properties = {};
		properties.defaultValue = addTIDParam.defaultValue;
		datatype.properties = properties;
		$scope.tidUpdate.original.name = paramName;
		$scope.tidUpdate.original.datatype = datatype;
		$scope.tidUpdate.original.userSelected = false;
		$scope.showTIDMessage = false;
		$scope.tidUpdate.original.flatenedName = orgFlatenedName;
		var arrayUpdate = $scope.isArray;
		var dataTypeProperty = $scope.tidProperties;
		var dataPatternStr = "";
		var tidDataType = addTIDParam.dataType;
		dataPatternStr=createPattern(addTIDParam);
		if (arrayUpdate) {
			$scope.tidUpdate.original.datatype.array = true;
		}	

		if ($scope.enableParam) {
			$scope.tidUpdate.original.userSelected = true;
			$scope.tidUpdate.original.dataFormat = addTIDParam.dataFormat;
			dataTypeProperty.defaultValue = addTIDParam.defaultValue;
			if(tidDataType){
				if(tidDataType == "INTEGER"){
					dataTypeProperty.totalDigits = addTIDParam.size;
					dataTypeProperty.fractionDigits = "";
				}else if(tidDataType == "DOUBLE"){
					dataTypeProperty.totalDigits = addTIDParam.size;		
					dataTypeProperty.fractionDigits = addTIDParam.precision;
					dataTypeProperty.minLength = "";
					dataTypeProperty.maxLength = "";
					dataTypeProperty.pattern = "";	
				}else if(tidDataType == "STRING"){
					dataTypeProperty.minLength = addTIDParam.minLength;
					dataTypeProperty.maxLength = addTIDParam.maxLength;
					dataTypeProperty.pattern = addTIDParam.pattern;	
					dataTypeProperty.totalDigits = "";		
					dataTypeProperty.fractionDigits = "";
				}else if(tidDataType == "BOOLEAN"){
					dataTypeProperty.minLength = "";
					dataTypeProperty.maxLength = "";
					dataTypeProperty.pattern = "";	
					dataTypeProperty.totalDigits = "";		
					dataTypeProperty.fractionDigits = "";
				}else if(tidDataType == "DATE"){
					dataTypeProperty.minLength = "";
					dataTypeProperty.maxLength = "";
					dataTypeProperty.totalDigits = "";		
					dataTypeProperty.fractionDigits = "";
					dataTypeProperty.pattern = "";	
					if(addTIDParam.dataFormat){
						dataTypeProperty.pattern= addTIDParam.dataFormat;
					}			
				}
			}
			$scope.tidUpdate.original.dataTypeStr = dataPatternStr;
		}
		$scope.tidUpdate.original.datatype.properties = dataTypeProperty;
		//umgDialog.closeDialog();
		$scope.cleared = false;
		$scope.setButtonStatus();
	};

	//Tid Unique name check for update tid
	var uniqueNameCheck=function(nameNode,paramLabel){
		if (nameNode == paramLabel) {
			$scope.showTIDMessage = true;
			$scope.tidMessage = "Parameter Label Name should be unique";
			return false;
		}else{
			return true;
		}
	}

	//Forming pattern based on datatype for update tid 
	var createPattern=function(addTIDParam){
		var dataPatternStr = "";
		var tidDataType = addTIDParam.dataType;
		if (tidDataType == "DATE") {
			dataPatternStr = "DATE" + "-" + addTIDParam.dataFormat;
		}else if(tidDataType == "INTEGER"){
			dataPatternStr = "INTEGER";
			if(addTIDParam.size)dataPatternStr = dataPatternStr+"-"+addTIDParam.size;
		}else if(tidDataType == "STRING"){
			dataPatternStr = "STRING";
			if(addTIDParam.pattern)dataPatternStr = dataPatternStr+"-"+addTIDParam.pattern;
			/*if(addTIDParam.minLength)dataPatternStr = dataPatternStr+"-"+addTIDParam.minLength;
			if(addTIDParam.maxLength)dataPatternStr = dataPatternStr+"-"+addTIDParam.maxLength;*/	
			if(addTIDParam.size)dataPatternStr = dataPatternStr+"-"+addTIDParam.size;
		}
		else if(tidDataType == "DOUBLE" && addTIDParam.size && addTIDParam.precision){
			dataPatternStr = "DOUBLE"+"-"+addTIDParam.size+"-"+addTIDParam.precision;

		} else {
			dataPatternStr = addTIDParam.dataType.toUpperCase();
		}
		return dataPatternStr;
	};

	//event for clearing the entered tid params in popup
	$scope.clearTID = function() {
		initTID();
		$scope.showSize = true;
		$scope.showAddDiv = true;
		$scope.showTIDMessage = false;
	};


	//--------------- add/update/delete/clear tid functions ----- end -----


	//--------------- edit/save system parameters ---- start -----

	//edit pop up for system parameters
	$scope.viewSystemParamNodeInfo = function() {
		var selectedMID = $('#sysPar_tree').jstree(true);
		var selectedNode = selectedMID.get_selected();
		$scope.sysParamUpdate = selectedMID._model.data[selectedNode[0]];
		if (!selectedNode.length) {
			$scope.showMessage = true;
			$scope.message = "Please select a Node to View";
			return false;
		} else {
			$scope.showMessage = false;
			$scope.showDataTypeParam = false;
			/*umgDialog.setupDialog("ViewSystemVarInfo", 475, 300);
			umgDialog.openDialog();*/
			var structureType = "";
			var sysParamOriginal=$scope.sysParamUpdate.original;
			var structureValue = sysParamOriginal.datatype.array;
			var paramLabel = $scope.sysParamUpdate.text;
			var dataTypeLabel = sysParamOriginal.datatype.type;
			if (dataTypeLabel) {
				dataTypeLabel = dataTypeLabel.toUpperCase();
			}
			if (structureValue) {
				$scope.showDataTypeParam = true;
				structureType = "Array";
			} else if (dataTypeLabel == "OBJECT") {
				$scope.showDataTypeParam = true;
				structureType = "OBJECT";
			} else {
				$scope.showDataTypeParam = false;
			}
			if (paramLabel.indexOf('<span') != -1) {
				paramLabel = paramLabel.substring(0, paramLabel
						.indexOf('<span'));
			}
			$scope.ViewSystemVarInfo = {
					paramLabel : paramLabel,
					mandatory : sysParamOriginal.mandatory,
					structure : structureType,
					dataType : dataTypeLabel,
			};

			$scope.ViewSystemVarInfoDiv = true;
			if (versionMapped == true) {
				$scope.SysParamSave = false;
			} else {
				$scope.SysParamSave = true;
			}
		}
	};

	//event for save on edit system parameter
	$scope.editSystemParameter = function(ViewSystemVarInfo) {
		var ref = [];
		var sel = [];
		ref = $('#sysPar_tree').jstree(true);
		sel = ref.get_selected();	
		var updateSysParam = ref._model.data[sel[0]];
		var updatedLabel = updateSysParam.text;	

		if(updatedLabel.indexOf('*') >=0){
			updatedLabel = updatedLabel.substring(0, updatedLabel
					.indexOf('<span class="mandatoryParam">'));
		}
		if (ViewSystemVarInfo.mandatory) {
			updatedLabel = updatedLabel
			+ '<span class="mandatoryParam">*</span>';
		} else {
			if (updatedLabel.indexOf('*') != -1) {
				updatedLabel = updatedLabel.substring(0, updatedLabel
						.indexOf('<span class="mandatoryParam">'));
			}
		}
		ref.rename_node(sel, updatedLabel, ViewSystemVarInfo.paramLabel);
		updateSysParam.original.mandatory = ViewSystemVarInfo.mandatory;
		updateSysParam.text = updatedLabel;
		updateSysParam.original.text = updatedLabel;
		$scope.sysParamUpdate = updateSysParam;
		/*umgDialog.closeDialog();*/
		$scope.cleared = false;
		$scope.setButtonStatus();
	};

	//--------------- edit/save system parameters ---- end -----


	// pop-up for mid node view
	$scope.viewMIDNodeInfo = function() {
		var property={};
		var selectedMID = $('#mid_tree').jstree(true);
		var selectedNode = selectedMID.get_selected();
		$scope.showMessage = false;
		$scope.showSuccessMessage = false;
		$scope.showDataType = true;
		$scope.showExpose = false;
		var structLabel = "";
		$scope.midUpdate = selectedMID._model.data[selectedNode[0]];
		if (!selectedNode.length) {
			$scope.showMessage = true;
			$scope.message = "Please select a Node to Edit";
			return false;
		} else {
			$scope.showMessage = false;
			$scope.showStruct = false;
			/*umgDialog.setupDialog("AddTID", 475, 360);
			umgDialog.openDialog();*/
			var paramLabel = $scope.midUpdate.text;
			var dataTypeLabel = $scope.midUpdate.original.datatype.type;
			var structName = $scope.midUpdate.original.datatype.array;
			property = $scope.midUpdate.original.datatype.properties;
			if (dataTypeLabel) {
				dataTypeLabel = dataTypeLabel.toUpperCase();
			}
			if (paramLabel.indexOf('<span') != -1) {
				paramLabel = paramLabel.substring(0, paramLabel
						.indexOf('<span'));
			}
			var selected = $scope.midUpdate.original.userSelected;
			if (selected) {
				$scope.visibleInfoTID = false;
				$scope.visibleParamTID = false;
				$scope.enableParam = true;

			} else if (selected == undefined || !selected) {
				$scope.visibleInfoTID = true;
				$scope.visibleParamTID = true;
				$scope.TIDClear = false;
				$scope.enableParam = false;
				if (dataTypeLabel == "OBJECT") {
					$scope.showStruct = true;
					$scope.showSize = false;
					$scope.showStringLength = false;
					$scope.showPrecision = false;
					structLabel = dataTypeLabel;
				} if (structName) {
					$scope.showStruct = true;
					$scope.showSize = false;
					$scope.showStringLength = false;
					$scope.showPrecision = false;
					structLabel = "Array";
				} if(dataTypeLabel == "OBJECT" && structName){
					$scope.showStruct = true;
					$scope.showSize = false;
					$scope.showPrecision = false;
					$scope.showStringLength = false;
					$scope.showDataType = false;
					structLabel = "Array";
				}
			}
			$scope.addTID = {
					paramLabel : paramLabel,
					mandatory : $scope.midUpdate.original.mandatory,
					dataType : dataTypeLabel,
					structure : structLabel,
					dataFormat : property.pattern,
					size : property.totalDigits,
					precision : property.fractionDigits,
					minLength : property.minLength,
					maxLength : property.maxLength,				
					pattern   : property.pattern,
					defaultValue : property.defaultValue
			};
			$scope.visibleInfoTID = true;
			$scope.visibleParamTID = true;
			$scope.TIDClear = false;
			$scope.TIDSave = false;
			$scope.UpdateSave = false;
			$scope.mandatoryDisabled = true;
			$scope.showAddDiv = true;
		}
	};


	//--------------- add/delete mapping ---- start -----

	//Add tid mid mapping
	$scope.addMapping = function() {
		var tidSeletedNodeId = $scope.getSelectedNodeIdFromTree('tid_tree');
		var midSeletedNodeId = $scope.getSelectedNodeIdFromTree('mid_tree');
		var sqlSeletedNodeId = $scope.getSelectedNodeIdFromTree('sysPar_tree');

		var tidParamSelected = true;
		var sqlParamSelected = true;

		if (midSeletedNodeId == null || midSeletedNodeId.length == 0) {
			$scope.message = 'Select a node from MID.';
			$scope.showSuccessMessage = false;
			$scope.showMessage = true;
			return false;
		}

		if (tidSeletedNodeId == null || tidSeletedNodeId.trim().length == 0) {
			tidParamSelected = false;
		}

		if (sqlSeletedNodeId == null || sqlSeletedNodeId.trim().length == 0) {
			sqlParamSelected = false;
		}

		if (!sqlParamSelected && !tidParamSelected) {
			$scope.message = 'Select a Input parameter.';
			$scope.showSuccessMessage = false;
			$scope.showMessage = true;
			return false;
		} else if (sqlParamSelected && tidParamSelected) {
			$scope.message = 'Select one input at a time for mapping.';
			$scope.showSuccessMessage = false;
			$scope.showMessage = true;
			return false;
		}

		// get node details of the selected node from tid tree
		var tidNode;
		if (tidParamSelected) {
			tidNode = $scope.getTreeNodeById(tidSeletedNodeId, 'tid_tree');
		} else {
			tidNode = $scope.getTreeNodeById(sqlSeletedNodeId, 'sysPar_tree');
		}

		// get node details of the selected node from mid tree
		var midNode = $scope.getTreeNodeById(midSeletedNodeId, 'mid_tree');

		// validate mapping
		var errMsg = '';
		if ($scope.addTid.iomapping == 'INPUTMAPPING') {
			errMsg = $scope.validateMapping(tidNode, midNode);
		}

		if (errMsg == null || errMsg.trim().length == 0) {
			// prepare new mapping row
			var mapping = {
					mappingParam : tidNode.original.flatenedName,
					mappedTo : midNode.original.flatenedName
			};

			// update the data map
			if ($scope.tidMidGridDataMap[midNode.original.flatenedName] != null) {
				$scope.tidMidGridDataMap[midNode.original.flatenedName]
				.push(tidNode.original.flatenedName);
			} else {
				var mappedTids = [];
				mappedTids.push(tidNode.original.flatenedName);
				$scope.tidMidGridDataMap[midNode.original.flatenedName] = mappedTids;
			}

			// add the mapping to the mapping grid
			$scope.tidMidMappingData.push(mapping);
			$scope.message = tidNode.original.flatenedName + ' mapped to '
			+ midNode.original.flatenedName + ' successfully.';
			$scope.showMessage = false;
			$scope.showSuccessMessage = true;
			$scope.cleared = false;
			$scope.saved = false;
			$scope.finalized = false;
			if (saveWithoutValidn == true){
				$scope.saved = true;
			}
		} else {
			$scope.message = errMsg;
			$scope.showMessage = true;
			$scope.showSuccessMessage = false;
		}
	};

	//delete tid mid mapping
	$scope.deleteMapping = function() {
		$scope.message = '';
		$scope.showMessage = false;
		if ($scope.selectedMappings != null
				&& $scope.selectedMappings.length == 1) {
			$scope.showMessage = false;
			// get the index of the selected row
			var index = $scope.tidMidMappingData
			.indexOf($scope.selectedMappings[0]);
			// remove the element from grid data
			$scope.tidMidMappingData.splice(index, 1);

			// remove data from mapping data map
			var existingMappings = $scope.tidMidGridDataMap[$scope.selectedMappings[0].mappedTo];
			existingMappings.splice(existingMappings
					.indexOf($scope.selectedMappings[0].mappingParam), 1);

			$scope.message = 'Mapping '
				+ $scope.selectedMappings[0].mappingParam + ' ~ '
				+ $scope.selectedMappings[0].mappedTo
				+ ' deleted successfully.';
			// clear selected row items
			$scope.selectedMappings.splice(0, $scope.selectedMappings.length);
			$scope.showSuccessMessage = true;
			$scope.cleared = false;
			$scope.saved = false;
			$scope.finalized = false;
			if (saveWithoutValidn == true){
				$scope.saved = true;
			}
		} else {
			$scope.showSuccessMessage = false;
			$scope.message = 'Select a maping to be deleted.';
			$scope.showMessage = true;
			return false;
		}
	};

	//--------------- add/delete mapping ---- end -----


	//---------------- validation methods for add/edit/delete Tid/mapping  --- start -------

	//Validate entered value in the Add TID form
	$scope.validateTID = function(addTIDParam) {
		$scope.showTIDMessage = false;
		$scope.tidMessage = "";
		var dataTypeTID = addTIDParam.dataType;
		var sizeTID = addTIDParam.size;
		var defaultParam = addTIDParam.defaultValue;
		/*var minLength = addTIDParam.minLength;
		var maxLength = addTIDParam.maxLength;*/
		var maxLength = addTIDParam.size; 
		var paramName = addTIDParam.paramLabel;

		if(angular.isUndefined(defaultParam)){
			defaultParam = '';
		}

		if (paramName != "") {
			if (paramName.length>50) {
				$scope.showTIDMessage = true;
				$scope.tidMessage = 'TextLabelMaxLengthErrorMsg';
				return false;
			} else if(paramName.length<3){
				$scope.showTIDMessage = true;
				$scope.tidMessage = 'TextLabelMinLengthErrorMsg';
				return false;
			}
		} else {
			$scope.showTIDMessage = true;
			$scope.tidMessage = "Please enter a parameter label";
			return false;
		}
		if (dataTypeTID) {
			if (dataTypeTID == "DOUBLE" || dataTypeTID == "NUMERIC") {
				if (sizeTID && addTIDParam.precision && defaultParam &&  defaultParam.length >= 1 && defaultParam.indexOf(".")!=-1) {
					var values = defaultParam.split(".");
					if(values){
						if ((sizeTID < values[0].length)
								|| (addTIDParam.precision < values[1].length)) {
							$scope.showTIDMessage = true;
							$scope.tidMessage = "Default value should not exceed size/precision";
							return false;
						}
					}
				}
				else if(dataTypeTID == "DOUBLE" && sizeTID && defaultParam.length >= 1 && defaultParam.length > sizeTID) {
					$scope.showTIDMessage = true;
					$scope.tidMessage = "Default value should not exceed size/precision";
					return false;
				}
				else if(dataTypeTID == "DOUBLE" && addTIDParam.precision && defaultParam.length >= 1 && defaultParam.length > addTIDParam.precision) {
					$scope.showTIDMessage = true;
					$scope.tidMessage = "Default value should not exceed size/precision";
					return false;
				}
			} 
			if (defaultParam && dataTypeTID){				
				if(dataTypeTID == "INTEGER" && sizeTID && defaultParam.length >= 1 && defaultParam.length > sizeTID) {
					$scope.showTIDMessage = true;
					$scope.tidMessage = "Default value length should not exceed size";
					return false;
				}
			}

		}
		if (defaultParam && defaultParam.length >= 1) {
			if (dataTypeTID == "BOOLEAN") {
				if (!(defaultParam == "true" || defaultParam == "false"
					|| defaultParam == "TRUE" || defaultParam == "FALSE"
						|| defaultParam == "True" || defaultParam == "False")) {
					$scope.showTIDMessage = true;
					$scope.tidMessage = 'AddTIDDefaultValueErrorMsg';
					return false;
				}
			} else if (dataTypeTID == "INTEGER"
				&& !(onlyNumbers.test(defaultParam))) {
				$scope.showTIDMessage = true;
				$scope.tidMessage = 'AddTIDDefaultPatternErrorMsg';
				return false;
			} else if ((dataTypeTID == "DOUBLE" || dataTypeTID == "NUMERIC")) {
				if (!(onlyNumbers.test(defaultParam) || (doubleFormat
						.test(defaultParam)))) {
					$scope.showTIDMessage = true;
					$scope.tidMessage = "Please enter valid number";
					return false;
				}
			}else if (dataTypeTID == "STRING") {
				/*if(minLength){ 
					if(minLength.length>=1 && defaultParam.length<minLength){
						$scope.showTIDMessage = true;
						$scope.tidMessage = "Default value length is less than Min Length";
						return false;
					}
				}*/
				if(maxLength){  
					if(maxLength>=1 && defaultParam.length>maxLength){
						$scope.showTIDMessage = true;
						$scope.tidMessage = "Default value length is greater than Max Length";
						alert($scope.tidMessage);
						return false;
					}
				}
			}
		}
		return true;
	};

	// mapping validation
	$scope.validateMapping = function(tidNode, midNode) {
		if ($scope.validateIfParentMapped(tidNode, midNode)) {
			return 'Duplcate mapping: Parent objects mapping already exists.';
		} else if ($scope.validateIfChildMapped(tidNode, midNode)) {
			return 'Child parameter mapping should be removed to map parent parameters.';
		} else if ($scope.validateDuplicateMapping(tidNode, midNode)) {
			return 'Mapping ' + tidNode.original.flatenedName + ' ~ '
			+ midNode.original.flatenedName + ' already exists.';
		} else {
			var datatypeErr = $scope.validateDatatype(tidNode, midNode);
			if (datatypeErr != null && datatypeErr.trim().length > 0) {
				return datatypeErr;
			}
			var maxMappingErr = $scope.validateMaxMapping(tidNode, midNode);
			if (maxMappingErr != null && maxMappingErr.trim().length > 0) {
				return maxMappingErr;
			}

			if (!$scope.validateMultipleMandatoryMappingToMid(tidNode, midNode)) {
				return 'Two Mandatory TID cannot be mapped to the same mandatory MID.';
			}

			if (!$scope.validateOptionalMid(tidNode, midNode)) {
				return 'Optional MID can have only one mapping from the TID';
			}
		}
		return "";
	};

	$scope.validateIfParentMapped = function(sourceNode, midNode) {
		var duplicate = false;
		var sourceFlatenedName = sourceNode.original.flatenedName;
		var midFlatenedName = midNode.original.flatenedName;
		var parentNodeName;
		var mappings;
		var midFlatenedNameArray;
		var flatName;
		if (sourceFlatenedName == midFlatenedName
				&& sourceFlatenedName.indexOf('/') != -1) {
			midFlatenedNameArray = midFlatenedName.split('/');
			for (var i = 0; i < midFlatenedNameArray.length; i++) {
				parentNodeName = midFlatenedNameArray[i];
				if (i > 0) {
					flatName = flatName + '/' + parentNodeName;
				} else {
					flatName = parentNodeName;
				}
				mappings = $scope.tidMidGridDataMap[flatName];
				if (mappings != null && mappings.length > 0) {
					for (var j = 0; j < mappings.length; j++) {
						if (flatName == mappings[j]) {
							duplicate = true;
							break;
						}
					}
				}
				if (duplicate) {
					break;
				}
			}
		}
		return duplicate;
	};

	$scope.validateIfChildMapped = function(sourceNode, midNode) {
		var childMappped = false;
		var sourceFlatenedName = sourceNode.original.flatenedName;
		var midFlatenedName = midNode.original.flatenedName;

		if (sourceFlatenedName == midFlatenedName) {
			for ( var mapping in $scope.tidMidGridDataMap) {
				if (mapping != sourceFlatenedName
						&& mapping.indexOf(sourceFlatenedName) != -1) {
					var mappingList = $scope.tidMidGridDataMap[mapping];
					if (mappingList != null && mappingList.length > 0) {
						for (var i = 0; i < mappingList.length; i++) {
							if (mapping == mappingList[i]) {
								childMappped = true;
								break;
							}
						}
					}
				}
				if (childMappped) {
					break;
				}
			}
		}

		return childMappped;
	};

	//Validates if the given tid-mid are already mapped or not.
	$scope.validateDuplicateMapping = function(tidNode, midNode) {
		var duplicate = false;
		var mappedTo = midNode.original.flatenedName;
		var mappedTids = $scope.tidMidGridDataMap[mappedTo];
		if (mappedTids != null && mappedTids.length > 0) {
			for (var i = 0; i < mappedTids.length; i++) {
				if (tidNode.original.flatenedName == mappedTids[i]) {
					duplicate = true;
					break;
				}
			}
		}
		return duplicate;
	};

	// validate datatype
	$scope.validateDatatype = function(tidNode, midNode) {
		var errorMessage = '';
		var tidDatatypeStr = tidNode.original.dataTypeStr;
		var midDatatypeStr = midNode.original.dataTypeStr;
		if (tidDatatypeStr && tidDatatypeStr.indexOf('|') != -1
				&& midDatatypeStr.indexOf('|') != -1) {
			var tidDatatype = $scope
			.getDimensionsOfArrayDatatype(tidDatatypeStr);
			var midDatatype = $scope
			.getDimensionsOfArrayDatatype(midDatatypeStr);
			if (tidDatatype.type == midDatatype.type) {
				if (tidDatatype.lhsDim == -1) {
					if (tidDatatype.rhsDim != midDatatype.rhsDim) {
						errorMessage = 'TID and MID array dimensions does not match.TID datatype : '+tidDatatypeStr + '~~ MID datatype : ' + midDatatypeStr;
					}
				} else if (tidDatatype.rhsDim == -1) {
					if (tidDatatype.lhsDim != midDatatype.lhsDim) {
						errorMessage = 'TID and MID array dimensions does not match. TID datatype : '+tidDatatypeStr + '~~ MID datatype : ' + midDatatypeStr;
					}
				} else if (midDatatype.rhsDim == -1) {
					if (tidDatatype.lhsDim != midDatatype.lhsDim) {
						errorMessage = 'TID and MID array dimensions does not match. TID datatype : '+tidDatatypeStr + '~~ MID datatype : ' + midDatatypeStr;
					}
				} else if (midDatatype.lhsDim == -1) {
					if (tidDatatype.rhsDim != midDatatype.rhsDim) {
						errorMessage = 'TID and MID array dimensions does not match.TID datatype : '+tidDatatypeStr + '~~ MID datatype : ' + midDatatypeStr;
					}
				} else if (tidDatatype.lhsDim != midDatatype.lhsDim
						|| tidDatatype.rhsDim != midDatatype.rhsDim) {
					errorMessage = 'TID and MID array dimensions does not match.TID datatype : '+tidDatatypeStr + '~~ MID datatype : ' + midDatatypeStr;
				}
			} else {
				errorMessage = 'TID and MID array dataype does not match.';
			}
		} else if (tidDatatypeStr && tidDatatypeStr != midDatatypeStr) {
			errorMessage = 'TID and MID datatypes should be same. TID datatype : '+tidDatatypeStr + '~~ MID datatype : ' + midDatatypeStr;
		}
		return errorMessage;
	};

	$scope.getDimensionsOfArrayDatatype = function(datatypeStr) {
		var datatype = {};
		if (datatypeStr != null && datatypeStr.length > 0) {
			var dimensions = datatypeStr.split('|');
			if (dimensions.length == 2) {
				datatype.type = dimensions[0];
				var dimStr = dimensions[1].split(',');
				if(dimStr.length > 0){
					datatype.lhsDim = dimStr[0];
					if (dimStr.length == 2) {
						datatype.rhsDim = dimStr[1];
					}
				}
			}
		}
		return datatype;
	};

	//Validates if the given mid has got more than 2 tid mapping or not.
	$scope.validateMaxMapping = function(tidNode, midNode) {
		var errorMessage = '';
		var mappedTo = midNode.original.flatenedName;
		var mappedTids = $scope.tidMidGridDataMap[mappedTo];
		var valid = (mappedTids == null || mappedTids.length < 2) ? true
				: false;
		if (!valid) {
			errorMessage = 'Inputs to MID parameter cannot exceed more than 2.';
		}
		return errorMessage;
	};

	// TODO move validation to different service
	$scope.validateMultipleMandatoryMappingToMid = function(tidNode, midNode) {
		// chek if tid and mid parameters are mandatory
		if (tidNode.original.mandatory && midNode.original.mandatory && !tidNode.original.exposedToTenant) {
			// fetch alrady existing mappings for mid
			var existingMappings = $scope.getMappingsForMid(midNode);

			// get all tid nodes
			var allTidNodes = $("#tid_tree").jstree(true).get_json();

			// get tid tree data as array
			var tidJsonArray = $scope
			.getTreeDataAsJson(allTidNodes, 'tid_tree');

			if (existingMappings != null && existingMappings.length > 0
					&& tidJsonArray != null) {
				for (var i = 0; i < tidJsonArray.length; i++) {
					if ((existingMappings[0].mappingParam == tidJsonArray[i].flatenedName)
							&& (tidJsonArray[i].mandatory)) {
						return false;
					}
				}
			}
		}
		return true;

	};

	$scope.validateOptionalMid = function(tidNode, midNode) {
		// chek if tid and mid parameters are mandatory
		if (!midNode.original.mandatory) {
			// fetch alrady existing mappings for mid
			var existingMappingsForMid = $scope.getMappingsForMid(midNode);
			if (existingMappingsForMid.length > 0) {
				return false;
			}
		}
		return true;
	};

	$scope.getMappingsForMid = function(midNode) {
		var existingMappings = [];
		if ($scope.tidMidMappingData != null
				&& $scope.tidMidMappingData.length > 0) {
			for (var i = 0; i < $scope.tidMidMappingData.length; i++) {
				if (midNode.original.flatenedName == $scope.tidMidMappingData[i].mappedTo) {
					existingMappings.push($scope.tidMidMappingData[i]);
				}
			}
		}
		return existingMappings;
	};

	//---------------- validation methods for add/edit/delete Tid/mapping  --- end -------


	// ------------------ query pages redirect functions ---- start ----------

	//event for list query page
	$scope.listQuery = function(treedata) {
		$scope.createQryRedirFlg = false;
	/*	$scope.getMappingStatus(treedata);
		
	*/	if (versionMapped == true || $scope.publishedOrDeactivated == true) {
			$scope.setParmsAndRedirectToQryPage();
		} 
		else if(modelNotExtracted == true) {
			$scope.getMappingStatus(treedata);
		} else {
			$dialogs.confirm('Please Confirm','<span class="confirm-body">Saving Mappings in the Background.</span>')
			.result.then(function(btn){
				$log.warn("You confirmed Saving the Mappings ...");
				$scope.getMappingStatus(treedata);
	        });
		}
		
		/*var displayMsg = 'Saving Tid Mid Mappings in the Background';
		var dialogOptions = {
				closeButtonText : 'Cancel',
				actionButtonText : 'Continue',
				headerText : 'Save Mappings',
				bodyText : displayMsg,
				callback : function() {
					$log.warn("You confirmed Saving the Tid Mid Mappings ...");
					$scope.createQryRedirFlg = false;
					$scope.getMappingStatus();
				}
		};
		if (versionMapped == true || $scope.publishedOrDeactivated == true) {
			$scope.setParmsAndRedirectToQryPage();
		} else if (modelNotExtracted == true){ 
			$scope.getMappingStatus();
		} else {
			dialogService.showModalDialog({}, dialogOptions);
		}*/
	};

	//event for create query page
	$scope.createQuery = function(treedata) {
		$scope.createQryRedirFlg = true;
		if (modelNotExtracted == true) {
			$scope.getMappingStatus(treedata);
		} else {
			$dialogs.confirm('Please Confirm','<span class="confirm-body">Saving Mappings in the Background.</span>')
			.result.then(function(btn){
				$log.warn("You confirmed Saving the Mappings ...");
				$scope.getMappingStatus(treedata);
	        });
		}
	/*	var displayMsg = 'Saving Tid Mid Mappings in the Background';
		var dialogOptions = {
				closeButtonText : 'Cancel',
				actionButtonText : 'Continue',
				headerText : 'Save Mappings',
				bodyText : displayMsg,
				callback : function() {
					$log.warn("You confirmed Saving the Tid Mid Mappings ...");
					$scope.createQryRedirFlg = true;
					$scope.getMappingStatus();
				}
		};
		if (modelNotExtracted == true) {
			$scope.getMappingStatus();
		} else {
			dialogService.showModalDialog({}, dialogOptions);
		}*/
	};
	
	/**
	 * audhyabh
	 */
	//saving the mapping as current status before redirection to create/list query page
	$scope.getMappingStatus = function(treedata) {
		var tidName = $scope.tidName;
		addTidService.getMappingStatus(tidName).then(
				function(responseData) {
					$scope.showSuccessMessage = true;
					if (responseData != null) {
						$scope.message = responseData.message;
						var response = responseData.response;
						/*if (response == "FINALIZED"){
							$scope.saveTidMidMapping(true,true);
						} else {*/
							$scope.saveTidMidMapping(treedata,true);
						/*}*/
					}
				}, function(responseData) {
					alert('Failed: ' + responseData);
				});
	};

	//setting parameters used in create/list query page and redirecting to those pages 
	$scope.setParmsAndRedirectToQryPage = function() {
		var type = $scope.addTid.iomapping;
		var tidName = $scope.tidName;
		addTidService.createInputMapForQuery(type, tidName).then(
				function(responseData) {
					//$scope.showSuccessMessage = true;
					if (responseData != null) {
						sharedPropertiesService.put("versionNo",$scope.addTid.versionNo);
						sharedPropertiesService.put("versionId",$scope.addTid.versionId);
						sharedPropertiesService.put("apiName",$scope.apiName);
						//$scope.message = responseData.message;
						var response = responseData.response;						
						if ($scope.createQryRedirFlg) {
							sharedPropertiesService.put("queryInputMap",
									response);
							//setting the save button status when moving to query page
							if (saveWithoutValidn == true){
								sharedPropertiesService.put('hideSaveButtonListPage',true);
							}
							$location.path('queryEditor');
						} else {
							sharedPropertiesService.put("queryLaunchInfo",
									response);							
							//setting the save button status when moving to query page
							if (saveWithoutValidn == true){
								sharedPropertiesService.put('hideSaveButtonListPage',true);
							}
							$location.path('queryView');
						}				
					}
				}, function(responseData) {
					alert('Failed: ' + responseData);
				});
	};

	// ------------------ query pages redirect functions ---- end ----------



	// ============================= tid copy functions ------ start ---

	$scope.pagedTidData = [];
	$scope.filteredTidData = [];
	$scope.selectedAPI = [];
	
	$scope.apiGridOptions = {
			data: 'pagedTidData',
			enableRowSelection: true,
			multiSelect: false,
			selectedItems: $scope.selectedAPI,
			columnDefs: [{field:'version', displayName:'API'},
			             {field:'versionNo', displayName:'Version #'}]
	};

	//event for getting the list of tids and populate in pop-up for tidcopy
	$scope.browseTidList = function() {
		$scope.selectedAPI.length=0;
		$scope.showTidCopyErrorMessage = false;
		$scope.showTidCopySuccessMessage = false;
		$scope.tidCopyMessage = "";
		$scope.searchString = "";
		$scope.searchMsg = "";
		addTidService
		.getTidListForCopy()
		.then(
				function(responseData) {
					if (responseData != null && !responseData.error) {
						if (responseData.response != null) {
							$scope.mappingInfos = responseData.response;
							$scope.setPagingData($scope.mappingInfos,
									$scope.pagingOptions.currentPage,
									$scope.pagingOptions.pageSize);
							$scope.ViewTidCopyInfoDiv = true;
						}
					} else {
						alert(" \n Error while getting tid list for copy "
								+ responseData.message);
					}
				}, function(responseData) {
					alert('Failed: ' + responseData);
				});
	};

	//event for onclick of cancel button in selectTid pop-up  
	$scope.cancelTidCopy = function() {
		$scope.pagedTidData = [];
		$scope.filteredTidData = [];
		$scope.showTidCopyErrorMessage = false;
		$scope.showTidCopySuccessMessage = false;
		$scope.tidCopyMessage = "";
	};

	//event for onclick of select button in selectTid pop-up 
	$scope.selectTidCopy = function() {
		var tidName = null;
		
			//tidName = $scope.tidcopy.select;
			tidName = $scope.selectedAPI[0].tidName;
		
			$scope.addTid.tcopy = tidName;
		
		if ($scope.validate()) {
			var derivedModelName = $scope.addTid.derivedmodelname;
			var derivedTidName = $scope.addTid.tidName;
			var tidNamePassed = null;
			
				tidNamePassed = $scope.selectedAPI[0].tidName;
			
			addTidService.extractTidParams(derivedModelName, derivedTidName, tidNamePassed)
			.then(function(responseData) {
				if (responseData.error) {
					//throw e;
					$scope.showMessage = true;
					$scope.showSuccessMessage = false;
					$scope.message = responseData.message;
				} else {
					var mappingDescriptor = responseData.response;
					if (mappingDescriptor != null) {
						
							$scope.tidName = mappingDescriptor.tidName;
							tidCopySelected = true;	
							mappingDescriptor.copiedTidName=tidNamePassed.replace("-MID-","-TID-")
						
						$scope.renderAll(mappingDescriptor);
					}
				}
			});
			$scope.showMessage = false;
			$scope.showSuccessMessage = false;
			$scope.cleared = false;
			$scope.setButtonStatus();			
			switchFlagForShowingStar = false;
			modelNotExtracted = false;
		}
		$scope.validationErrors = {};
		$scope.showErrorDialogBtn = false;

	};

	$scope.selectedTid = function() {
		$scope.showTidCopyErrorMessage = false;
		$scope.showTidCopySuccessMessage = false;
		$scope.tidCopyMessage = "";
	};

	// pagination for select tid copy pop-up

	$scope.pagingOptions = {
			pageSizes : [ 5, 10, 20 ],
			pageSize : 10,
			currentPage : 1			
	};

	/**
	 * This method will do sorting on all fields for Paged Data.
	 */
	$scope.sort = function(predicate, reverse) {
		$log.info("Sorting with " + predicate);
		$scope.predicate = predicate;
		$scope.reverse = reverse;
	};

	/**
	 * This method will set up paged data from filtered Server items.
	 */
	$scope.setFilteredPagingData = function(data, page, pageSize) {
		$scope.allfilteredTid = data;
		$scope.pagedTidData = data
		.slice((page - 1) * pageSize, page * pageSize);
		$scope.totalfilteredTid = data.length;
	};

	/**
	 * This method will set up paged data from total Server items.
	 */
	$scope.setPagingData = function(data, page, pageSize) {
		
		if (data != null) {
			$scope.allMappings = data;
			$scope.pagedTidData = data.slice((page - 1) * pageSize, page
					* pageSize);
			$scope.totalMappings = data.length;
		} else {
			$scope.allMappings = [];
			$scope.pagedTidData = [];
			$scope.totalMappings = 0;
		}
	};

	$scope.getPagedDataAsync = function(pageSize, page, searchText) {
		if (searchText) {
			var filtereddata = [];
			var ft = angular.lowercase(searchText);
			angular.forEach($scope.allMappings,
					function(data) {
				if (JSON.stringify(data.version).toLowerCase().indexOf(
						ft) != -1) {
					filtereddata.push(data);
				}
			});
			if (filtereddata.length == 0) {
				$scope.searchMsg = "No Model Found for this Search.";
				$scope.currentMaxPages=0;
			}	
			$scope.totalMappings=filtereddata.length;
			$scope.setFilteredPagingData(filtereddata, page, pageSize);
		} else {
			$scope.setPagingData($scope.mappingInfos, page, pageSize);
		}
	};

	$scope.$watch('pagingOptions', function(newVal, oldVal) {
		if (newVal != oldVal) {
			$scope.currentMaxPages = $scope.maxPages();
			$scope.expandAll = false;
			if (newVal.currentPage * newVal.pageSize > $scope.totalMappings) {
				$scope.pagingOptions.currentPage = $scope.currentMaxPages;
			}
			if (newVal.currentPage * newVal.pageSize < 0) {
				$scope.pagingOptions.currentPage = 1;
			}
			if($scope.searchString!='' && $scope.oldSearchStr==$scope.searchString){				
				$scope.searchMsg = "";
				$scope.getPagedDataAsync($scope.pagingOptions.pageSize,
						$scope.pagingOptions.currentPage, $scope.searchString);

			}else{
				$scope.getPagedDataAsync($scope.pagingOptions.pageSize,
						$scope.pagingOptions.currentPage);
			}
		}
	}, true);

	/**
	 * This method will always keep eye on Search request. And will set the
	 * resultant data in paged format.
	 */
	$scope.$watch('searchString', function(newVal, oldVal) {
		$scope.showTidCopyErrorMessage = false;
		$scope.showTidCopySuccessMessage = false;
		$scope.tidCopyMessage = "";
		if (newVal != oldVal) {
			$scope.oldSearchStr=newVal;
			$scope.pagingOptions.currentPage = 1;
			$scope.searchMsg = "";
			$scope.getPagedDataAsync($scope.pagingOptions.pageSize,
					$scope.pagingOptions.currentPage, $scope.searchString);
		}
	}, true);

	// --------------------- paging button methods

	$scope.maxRows = function() {
		var ret = Math.max($scope.totalMappings, $scope.pagedTidData.length);
		return ret;
	};

	$scope.$on('$destroy', $scope.$watch('totalMappings', function(n, o) {
		$scope.currentMaxPages = $scope.maxPages();
	}));

	$scope.maxPages = function() {
		if ($scope.maxRows() === 0) {
			return 1;
		}
		return Math.ceil($scope.maxRows() / $scope.pagingOptions.pageSize);
	};

	$scope.pageToFirst = function() {
		$scope.pagingOptions.currentPage = 1;
	};

	$scope.pageBackward = function() {
		var page = $scope.pagingOptions.currentPage;
		$scope.pagingOptions.currentPage = Math.max(page - 1, 1);
	};

	$scope.pageForward = function() {
		var page = $scope.pagingOptions.currentPage;
		if ($scope.totalMappings > 0) {
			$scope.pagingOptions.currentPage = Math.min(page + 1, $scope
					.maxPages());
		} else {
			$scope.pagingOptions.currentPage++;
		}
	};

	$scope.pageToLast = function() {
		var maxPages = $scope.maxPages();
		$scope.pagingOptions.currentPage = maxPages;
	};

	$scope.cantPageForward = function() {
		var curPage = $scope.pagingOptions.currentPage;
		var maxPages = $scope.maxPages();
		if ($scope.totalMappings > 0) {
			return curPage >= maxPages;
		} else {
			return $scope.pagedTidData.length < 1;
		}
	};

	$scope.cantPageToLast = function() {
		if ($scope.totalMappings > 0) {
			return $scope.cantPageForward();
		} else {
			return true;
		}
	};

	$scope.cantPageBackward = function() {
		var curPage = $scope.pagingOptions.currentPage;
		return curPage <= 1;
	};

	//============================= tid copy functions ------ ends ---

	//This method will open the error definition dialog box
	$scope.loadErrorDialogBox = function() {
		/*umgDialog.setupDialog("ViewErrorInfo", 750, 250);
		umgDialog.openDialog();*/
		$scope.ViewErrorInfoDiv = true;
	};	

	//Watch data types
	$scope.$watch('addTID', function(newVal, oldVal) {
		if ($scope.addTID) {
			if ($scope.addTID.dataType == 'DATE') {
				if (!$scope.isUpdate) {
					$scope.showSize = false;
					$scope.showFormat = true;
					$scope.dateTID = true;
					$scope.showPrecision = false;
					$scope.showStringLength = false;
					$scope.exactValue="dd-MM-yyyy";
				} else {
					$scope.showSize = false;
					$scope.showFormat = true;
					$scope.showStringLength = false;
					if ($scope.enableParam) {
						$scope.dateTID = true;
					} else {
						$scope.dateTID = false;
					}
					$scope.showPrecision = false;
				}
			} else if ($scope.addTID.dataType == 'BOOLEAN') {
				$scope.showSize = false;
				$scope.showFormat = false;
				$scope.dateTID = false;
				$scope.showPrecision = false;
				$scope.showStringLength = false;
			} else if ($scope.addTID.dataType == 'DOUBLE') {
				$scope.showSize = true;
				$scope.showPrecision = true;
				$scope.dateTID = false;
				$scope.showFormat = false;
				$scope.showStringLength = false;
			} else if ($scope.addTID.dataType == 'INTEGER') {
				$scope.showFormat = false;
				$scope.showSize = true;
				$scope.showPrecision = false;
				$scope.dateTID = false;
				$scope.showStringLength = false;
			} else if ($scope.addTID.dataType == 'STRING') {
				$scope.showFormat = false;
				$scope.showSize = true;
				$scope.showPrecision = false;
				$scope.dateTID = false;
				$scope.showStringLength = true;
			} else {
				$scope.showFormat = false;
				$scope.showSize = false;
				$scope.showPrecision = false;
				$scope.dateTID = false;
			}
		}
		$scope.showTIDMessage = false;
	}, true);

	//sets the height of mapping grid
	$scope.setHeightOfMappingGrid = function() {
		var midelement = document.getElementById('mid_tree');
		var syselement = document.getElementById('systemdiv');
		var divHeight = (midelement.offsetHeight + syselement.offsetHeight - 10);
		if (divHeight > 350) {
			$scope.mappingGridHeight = divHeight +"px";
			document.getElementById('mappingGridDiv').style.height = $scope.mappingGridHeight;			
		} else {
			document.getElementById('mappingGridDiv').style.height = '350px';
		}
	};
};
