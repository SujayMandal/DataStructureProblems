'use strict';

var VersionMetricsCtrl = ['$scope','sharedPropertiesService','umgVersionService','$log', function(scope,sharedPropertiesService,umgVersionService,log){

	/**
	 * steps
	 */
	scope.steps = ['one', 'two'];
	scope.step = 0;
	scope.isCurrentStep = function(step) {
		return scope.step === step;
	};

	scope.versionMetricRequestInfo = {
			versionName : '',
			majorVersion : '',
			minorVersion : '',
			fromDate : '',
			toDate : '',
			isTest: 0
	};
	scope.versionMetricResponse;

	var data_parsed_API;
	var sum_min_API;
	var sum_max_API;
	var sum_mean_API;
	var sum_percentile_API;

	scope.setCurrentStep = function(step) {
		scope.step = step;
		scope.versionMetric();
	};
	scope.getCurrentStep = function() {
		return scope.steps[scope.step];
	};

	scope.onPageload = function(){
		log.info(sharedPropertiesService.get("MetricData"));
		scope.data_API=sharedPropertiesService.get("MetricData").api.metricsInfo;
		data_parsed_API=sharedPropertiesService.get("MetricData").api;
		scope.transaction_API=data_parsed_API.totalTransactions;
		scope.selected_name=sharedPropertiesService.get("VersionData").modelName;
		scope.selected_version=sharedPropertiesService.get("VersionData").version;

		/**
		 * calculating data for stacked parcentage in API
		 */
		sum_min_API=data_parsed_API.metricsInfo[0].minTime+data_parsed_API.metricsInfo[1].minTime+data_parsed_API.metricsInfo[2].minTime+data_parsed_API.metricsInfo[3].minTime;
		sum_max_API=data_parsed_API.metricsInfo[0].maxTime+data_parsed_API.metricsInfo[1].maxTime+data_parsed_API.metricsInfo[2].maxTime+data_parsed_API.metricsInfo[3].maxTime;
		sum_mean_API=data_parsed_API.metricsInfo[0].meanTime+data_parsed_API.metricsInfo[1].meanTime+data_parsed_API.metricsInfo[2].meanTime+data_parsed_API.metricsInfo[3].meanTime;
		sum_percentile_API=data_parsed_API.metricsInfo[0].percentileTransaction+data_parsed_API.metricsInfo[1].percentileTransaction+data_parsed_API.metricsInfo[2].percentileTransaction+data_parsed_API.metricsInfo[3].percentileTransaction;

	};

	scope.onPageload();
	createGoogleCharts();

	/**
	 * function for version metric
	 */
	scope.versionMetric = function() {
		log.info("version metric : "+scope.selected_name);
		scope.versionMetricRequestInfo.versionName = scope.selected_name;
		scope.versionMetricRequestInfo.majorVersion = scope.selected_version.split(".")[0];
		scope.versionMetricRequestInfo.minorVersion = scope.selected_version.split(".")[1];
		scope.versionMetricRequestInfo.isTest = scope.step;
		umgVersionService.fetchVersionMetric(scope.versionMetricRequestInfo).then(
				function(responseData){
					if(!responseData.error){
						scope.versionMetricResponse = responseData.response;
						sharedPropertiesService.put('MetricData',scope.versionMetricResponse);
						scope.onPageload();
						createGoogleCharts();
					}else{
						scope.allModels = [];
						log.error("Error came with : "+responseData.message);
						scope.msg(responseData.message,'no data found');
					}
				},
				function(errorData){
					log.error("Error came with : "+errorData);
					scope.msg(errorData,'no data found');
				}
		);
	};

	/**
	 * grid for displaying data
	 */
	scope.tableGrid_API = {
			data: 'data_API',
			enableRowSelection: false,
			multiSelect: false,
			columnDefs: [{field:'stage', displayName:'Processing Stage'}, 
				{field:'meanTime', displayName:'Mean Time', headerClass:"listStatHeader", cellClass:"listStatHeader" , width: "100px"},
				{field:'percentileTransaction', displayName:'90% Percentile', headerClass:"listStatHeader", cellClass:"listStatHeader" , width: "120px"},
				{field:'minTime', displayName:'Min Time', headerClass:"listStatHeader", cellClass:"listStatHeader" , width: "90px"},
				{field:'maxTime', displayName:'Max Time', width: "90px", headerClass:"listStatHeader" , cellClass:"listStatHeader"}
				]
	};



	/**
	 * pie for displaying data
	 */
	function createGoogleCharts(){
		scope.chartObjectPie = {
				"type": "PieChart",
				"displayed": true,
				"backgroundColor": "transparent",
				"data" :{
					"cols": [{
						id: "Name",
						label: "Name",
						type: "string"
					},{
						id: "Value",
						label: "Value",
						type: "number"
					}],

					"rows": [{
						"c": [
							{
								"v": "Runtime"
							},
							{
								"v": data_parsed_API.metricsInfo[0].percentileTransaction
							},
							]},
							{
								"c": [
									{
										"v": "Modelet Wait Time"
									},
									{
										"v": data_parsed_API.metricsInfo[1].percentileTransaction
									},
									]},
									{"c": [
										{
											"v": "Modelet Exec Time"
										},
										{
											"v": data_parsed_API.metricsInfo[2].percentileTransaction
										},
										]},
										{"c": [
											{
												"v": "Model Exec Time"
											},
											{
												"v": data_parsed_API.metricsInfo[3].percentileTransaction
											},
											],  			    

										}]

				},

				"options": {
					"title": "Pie Chart for 90 percentile",
					"isStacked": "true",
					"fill": 20,
					"displayExactValues": true,
					"backgroundColor": "transparent",
					"vAxis": {
						"title": "Sales unit",
						"gridlines": {
							"count": 10
						}
					},
					"hAxis": {
						"title": "Date"
					}
				},
				"formatters": {}
		};
		
		scope.chartObjectColoumnChart = {
				"type": "ColumnChart",
				"displayed": true,
				"backgroundColor": "transparent",
				"data": {
					"cols": [
						{
							"id": "Metrices",
							"label": "Metrices",
							"type": "string",
							"p": {}
						},
						{
							"id": "Runtime",
							"label": "Runtime",
							"type": "number"
						},
						{
							"id": "Modlet Wait Time",
							"label": "Modlet Wait Time",
							"type": "number",
							"p": {}
						},
						{
							"id": "Modlet Exec Time",
							"label": "Modlet Exec Time",
							"type": "number",
							"p": {}
						},
						{
							"id": "Model Exec Time",
							"label": "Model Exec Time",
							"type": "number",
							"p": {}
						}
						],
						"rows": [
							{
								"c": [
									{
										"v": "Min"
									},
									{
										"v": (data_parsed_API.metricsInfo[0].minTime*100/sum_min_API)
									},
									{
										"v": (data_parsed_API.metricsInfo[1].minTime*100/sum_min_API)
									},
									{
										"v": (data_parsed_API.metricsInfo[2].minTime*100/sum_min_API)
									},
									{
										"v": (data_parsed_API.metricsInfo[3].minTime*100/sum_min_API)
									}
									]
							},
							{
								"c": [
									{
										"v": "Mean"
									},
									{
										"v": (data_parsed_API.metricsInfo[0].meanTime*100/sum_mean_API)
									},
									{
										"v": (data_parsed_API.metricsInfo[1].meanTime*100/sum_mean_API)
									},
									{
										"v": (data_parsed_API.metricsInfo[2].meanTime*100/sum_mean_API)
									},
									{
										"v": (data_parsed_API.metricsInfo[3].meanTime*100/sum_mean_API)
									}
									]
							},
							{
								"c": [
									{
										"v": "90 Percentile"
									},
									{
										"v": (data_parsed_API.metricsInfo[0].percentileTransaction*100/sum_percentile_API)
									},
									{
										"v": (data_parsed_API.metricsInfo[1].percentileTransaction*100/sum_percentile_API)
									},
									{
										"v": (data_parsed_API.metricsInfo[2].percentileTransaction*100/sum_percentile_API)
									},
									{
										"v": (data_parsed_API.metricsInfo[3].percentileTransaction*100/sum_percentile_API)
									}
									]
							},
							{
								"c": [
									{
										"v": "Max"
									},
									{
										"v": (data_parsed_API.metricsInfo[0].maxTime*100/sum_max_API)
									},
									{
										"v": (data_parsed_API.metricsInfo[1].maxTime*100/sum_max_API)
									},
									{
										"v": (data_parsed_API.metricsInfo[2].maxTime*100/sum_max_API)
									},
									{
										"v": (data_parsed_API.metricsInfo[3].maxTime*100/sum_max_API)
									}
									]
							}  
							]
				},
				"options": {
					"title": "Stacked percentage",
					"isStacked": "true",
					'legend':'bottom',
					"fill": 20,
					"displayExactValues": true,
					"backgroundColor": "transparent",
					"vAxis": {
						"title": "Processing Stages",
						"minValue": "0", 
						"maxValue": "100",
						"format": " #\'%\'",
						"gridlines": {
							"count": 5
						}
					},
					"hAxis": {
						"title": "Metrices"
					}
				},
				"formatters": {}
		};

		scope.chartObjectAreaChart = {
				"type": "SteppedAreaChart",
				"backgroundColor": "transparent",
				"displayed": true,
				"data": {
					"cols": [
						{
							"id": "Metrices",
							"label": "Metrices",
							"type": "string",
							"p": {}
						},
						{
							"id": "Model Exec Time",
							"label": "Model Exec Time",
							"type": "number",
							"p": {}
						},
						{
							"id": "Modlet Exec Time",
							"label": "Modlet Exec Time",
							"type": "number",
							"p": {}
						}
						],
						"rows": [
							{
								"c": [
									{
										"v": "Mean"
									},
									{
										"v": (data_parsed_API.metricsInfo[3].meanTime)
									},
									{
										"v": (data_parsed_API.metricsInfo[2].meanTime)
									}
									]
							},
							{
								"c": [
									{
										"v": "90 Percentile"
									},
									{
										"v": (data_parsed_API.metricsInfo[3].percentileTransaction)
									},
									{
										"v": (data_parsed_API.metricsInfo[2].percentileTransaction)
									}
									]
							},
							]
				},
				"options": {
					"title": "Stacked steps",
					"backgroundColor": "transparent",
					"isStacked": "true",
					'legend':'bottom',
					"fill": 20,
					"displayExactValues": true,
					"vAxis": {
						"title": "Stages",
						"gridlines": {
							"count": 6
						}
					},
					"hAxis": {
						"title": "Metrices"
					}
				},
				"formatters": {}
		};
	};
}];