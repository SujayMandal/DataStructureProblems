'use strict';

var RACtrl = ['$scope', '$log', '$filter','$timeout','$dialogs', 'raService','dashboardService', function(scope, log, filter,timeout,dialogs, raService,dashboardService){
	scope.model_versioning_data=[];
	
	scope.modelEnvs = [];
	scope.selectedTenants = [];
	
	scope.ra_trend_format = "MMM dd";
	scope.model_trend_format = "MMM dd";
	scope.ra_trend_count = "-1"
	scope.model_trend_count = "-1";


	
	scope.filters = {
			runAsOfDateFromString : '',
			runAsOfDateToString : '',
			tenantNames:[],
			selectedTnt:[],
			selectionType:'DEFAULT'
	};
	
	scope.lookupStats;
	scope.chartObject2;
	scope.tenants;
	
	scope.transaction_count_data=[];
	scope.index=0;

	
	scope.$watch('index',function(){
		if(scope.index==2)
			scope.$broadcast("loaded_trans",scope.transaction_count_data);
	});
	
	scope.monthShortNames = ["Jan", "Feb", "Mar", "Apr", "May", "Jun",
		  "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
		];
	
	scope.config = {
		    tooltips: true,
		    labels: true,
		    mouseover: function() {},
		    mouseout: function() {},
		    click: function() {},
		    "legend": {
	 		    "display": true,
	 		    "position": "right"
	 		  },
		    colors: ['#4daea9','#d3c63a','#34508d','#0DF700','#E46B11'],
		    isAnimate: false
		  };
	
	/**dummy data*/
	scope.data = {
		    //series: ['HAMP', 'NonHamp', 'SS-DIL'],
			series: ['SaleAVM', 'RentAVM', 'DynamicPricing'],
		    data: [{
		      x: "Jan",
		      y: [100, 500, 0]
		    }, {
		      x: "Feb",
		      y: [300, 100, 100]
		    }, {
		      x: "Mar",
		      y: [351, 0, 10]
		    }, {
		      x: "Apr",
		      y: [154, 50, 879]
		    }, {
			  x: "May",
		      y: [54, 0, 879]
			}, {
			  x: "Jun",
			  y: [543, 330, 87]
			},{
			  x: "Jul",
			  y: [54, 200, 879]
			},{
			  x: "Aug",
			  y: [54, 450, 79]
			},{
			  x: "Sep",
			  y: [54, 0, 879]	
			},{
			  x: "Oct",
			  y: [351, 0, 330]
			},{
			  x: "Nov",
			  y: [543, 0, 0]
			},{
			  x: "Dec",
			  y: [54, 100, 8]
			}]
		  };
	
	/**creating models for data manupulation*/
    scope.models = [];
    angular.forEach(scope.data.series, function(modelName,key){
    	scope.models[key]=new Object();
    	scope.models[key].modelName = modelName;
    	scope.models[key].display = true;
    });
    
	
	/** 
	 * copying data for use*/
	scope.dataFromService = angular.copy(scope.data.data);
	
	/**to find sizeof array*/
	 var size = 0, k;
	    for (k in scope.dataFromService) {
	        if (scope.dataFromService.hasOwnProperty(k)) size++;
	    }
	    
	/**function to manipulate area graph*/
	scope.prepareTimeBasedChart = function(){
		
		angular.forEach(scope.models, function(model,key){
			if(model.display){
				scope.data.series.push(model.modelName);
				for(var i=0;i<size;i++){
					scope.data.data[i].y[key]=scope.dataFromService[i].y[key];
					
				}
			}
			else{
				angular.forEach(scope.data.data, function(data){
					data.y[key]=0;
				});
			}
		});
		
	};
	//endof funciton

		  
	
	
	/** pie chart manupulation */
	
	scope.pie_config = {
			"legend": {
	 		    "display": true,
	 		    "position": "right"
	 		  },
		    	  "labels": true,
		    	  "innerRadius": "42",
		    	  "lineLegend": "lineEnd",
		    	  "colors": ['#4daea9','#d3c63a','#34508d','#0DF700','#E46B11'],
		  };
	
	/**dummy data*/
	scope.pie_data = {
		    series: ['HAMP', 'NonHamp', 'SS-DIL'],
		    data: [{
		      x: "Hamp",
		      y: [61]
		    }, {
		      x: "Non Hamp",
		      y: [28]
		    }, {
		      x: "SS-DIL",
		      y: [41]
		    }]
		  };

    
	//scope.models = [{modelName : 'HAMP', display :  true},{modelName : 'NonHAMP', display :  true},{modelName : 'SS-DIL', display :  true}];
	
	/** 
	 * copying data for use*/
	scope.pieDataFromService = angular.copy(scope.pie_data.data);
	

	/**function to manupulate area graph*/
	scope.prepareTimeBasedPieChart = function(){
		
		angular.forEach(scope.models, function(model,key){
			if(model.display){
				scope.pie_data.series[key]=model.modelName;
				scope.pie_data.data[key].y[0]=scope.pieDataFromService[key].y[0];
			}
			else{
				scope.pie_data.series[key]="";
				scope.pie_data.data[key].y[0]=0;
			}
		});
		
	};
	//endof funciton
	/** end of pie chart manupulation */

	
	
	/** bar chart manupulation */
	
	scope.bar_config = {
	 	    tooltips: true,
	 	   "legend": {
	 		    "display": true,
	 		    "position": "right"
	 		  },
	 	    labels: false,
		    mouseover: function() {},
	   	    mouseout: function() {},
	   	    click: function() {},
	   	 colors: ['#4daea9','#ed6b00','#E46B11'],
		  };
	/**dummy data*/
	scope.bar_data = {
		    series: ['Success', 'Failure'],
		    data: [{
		      x: "Hamp",
		      y: [24,5]
		    }, {
		      x: "Non Hamp",
		      y: [13,1]
		    }, {
		      x: "SS-DIL",
		      y: [41,6]
		    }]
		  };

	
	//scope.models = [{modelName : 'HAMP', display :  true},{modelName : 'NonHAMP', display :  true},{modelName : 'SS-DIL', display :  true}];
	
	/** 
	 * copying data for use*/
	scope.barDataFromService = angular.copy(scope.bar_data.data);
	

	/**function to manupulate area graph*/
	scope.prepareTimeBasedBarChart = function(){
		
		angular.forEach(scope.models, function(model,key){
			if(model.display){
				/*scope.bar_data.series[key]=model.modelName;*/
				scope.bar_data.data[key].x=scope.barDataFromService[key].x;
				scope.bar_data.data[key].y=angular.copy(scope.barDataFromService[key].y);
			}
			else{
				/*scope.bar_data.series[key]="";*/
				scope.bar_data.data[key].x="";
				scope.bar_data.data[key].y[0]=0;
				scope.bar_data.data[key].y[1]=0;
			}
		});
		
	};
	
	/** end of bar chart manupulation */

	
   // Download IO Reports
	scope.downloadIO = function (entity){
		 var txnIds = entity.transactionId.trim()+":"+entity.transactionMode.trim()+":Error";
		dashboardService.downloadSelectedItems(txnIds).then(
		function(responseData) {
			if (responseData.error) {
				log.error(" \n Error in retrieving the details : " + responseData.message);
			} else {
				var result = responseData.response;
				saveAs(result.blob, result.fileName);

			}
		},
		function(responseData) {
			log.error("Connection to Server failed. Please try again later.");
		}
	);
	}

	
	 function createSuccessFailureChartBar(bar_data) {
		 var table = new google.visualization.DataTable();
	     table.addColumn("string", "Tenant");
	     table.addColumn("number", "Success"); 
	     table.addColumn("number", "I/P Validation Failures");
	     table.addColumn("number", "O/P Validation Failures");
	     table.addColumn("number", "Model Failures");
	     table.addColumn("number", "Tech Failures");

	     
	     $.each(bar_data, function(key,value){
	    	 var total = value["Success"]+value["inputValidationFailure"]+value["outputValidationFailure"]+value["modelFailures"]+value["otherFailures"];
	    	 table.addRow([key+"("+total+")",value["Success"],value["inputValidationFailure"],value["outputValidationFailure"],value["modelFailures"],value["otherFailures"]]);
			});
	  
		 scope.barObject = {
				  "type": "ColumnChart",
				  "displayed": false,
				  "data": table,
				  "options": {
				    "isStacked": "percent",
				    "fill": 20,
				    "height":173,
				    "colors": ['#72A10E','#FFC300','#FF5733','#C70039','#900C3F'],
				    "displayExactValues": true,
				    "backgroundColor": "transparent",
				    chartArea:{left:40,right:0,top:21},
				    "legend" : {position: 'top',textStyle: {fontSize: 9}},
				    "vAxis": {
				    	 viewWindow: {
				             min:0
				         },
				      "gridlines": {
				        "count": 5
				      }
				    },
				    "hAxis": {
				    }
				  },
				  "formatters": {}
				}
		
		
	};
	
	
	 function createChartPie(pie_data) {
		 pie_data = (pie_data===null | pie_data===undefined)?[]:pie_data;
		 var table_pie = new google.visualization.DataTable();
		 table_pie.addColumn("string", "Models");
		 table_pie.addColumn("number", "count"); 
		 $.each(pie_data, function(key,value){
	    	 table_pie.addRow([key,value]);
		 });
		 scope.pieObject = {
				  "type": "PieChart",
				  "displayed": false,
				  "data": table_pie,
				  "options": {
				    "fill": 20,
				    "height":173,
				    "displayExactValues": true,
				    "legend": { position: 'labeled' },
    			    chartArea:{bottom:0,top:0},
				    "sliceVisibilityThreshold":0.0000000001,
				    "backgroundColor": "transparent",
				    pieHole : 0.4,
				   
				  },
				  "formatters": {}
				}
		
		
	};
	
	
		scope.colSel = 0;
	
	/** column selection logic **/
	
	scope.columnSelect = function(){
		
		if(scope.colSel === 0)
		{
			scope.colSel = 1;
			document.getElementById('colDisp').style.display = 'block';
		}	
		else
		{
			scope.colSel = 0;
			document.getElementById('colDisp').style.display = 'none';
		}
		var currentTenant = document.getElementById('tenantCode_current').value
		if(scope.selectedTenants[0] === currentTenant){
			document.getElementById(document.getElementById('tenantCode_current').value).checked = true;
			}
		else{
			$.each( scope.selectedTenants, function(index,value) {
				document.getElementById(value).checked = true;					});
		}
	};  
		
	

	
	scope.change = function(tnt, active){
	    if (active)
	        scope.selectedTenants.push(tnt);
	    else
	        scope.selectedTenants.splice(scope.selectedTenants.indexOf(tnt), 1);
	};
	

	
// Setting STATUS Metrix
	
	function setUsageDynamics(tenant_range){
		raService.getUsageDynamics(scope.filters).then(
				function(responseData){
					if(responseData.error){
						scope.bar_data=[];
						createChartPie([]);
						createSuccessFailureChartBar([]);
						drawRaUsageTrendLine([]);
						drawModelUsageTrendLine([]);
						log.error('Error in fetching Usage Dynamics');
					}
					else{
					log.info('Received Usage Dynamics...');
					log.info(responseData);
					 var modelUsageTrendLine  ;
						var raUsageTrendLine ;
/*						var usageDynamics ;
*/						var usageMetrics ;
						var tenantTxnMetrics
					var dateFormat = responseData.response["date"] === undefined ?"MMM dd":responseData.response["date"];
					var gridCount = responseData.response["gridCount"] === undefined ?"-1":responseData.response["gridCount"];

					switch (tenant_range) {
				    case 'SELECTED':
	                        scope.model_trend_format = dateFormat;
	                        scope.model_trend_count = gridCount;
				    	 modelUsageTrendLine = responseData.response["modelUsageTrendLine"];
/*						 usageDynamics = responseData.response["usageDynamics"];
*/						 usageMetrics = responseData.response["usageMetrics"];
						drawModelUsageTrendLine(modelUsageTrendLine);
/*				    	scope.data_API = usageDynamics;
*/						scope.bar_data = usageMetrics;
						createChartPie(scope.bar_data);
				    	break; 
				    case 'ALL':
				    	 scope.ra_trend_format = dateFormat;
	                     scope.ra_trend_count = gridCount;
						 raUsageTrendLine = responseData.response["raUsageTrendLine"];
						drawRaUsageTrendLine(raUsageTrendLine);
						 tenantTxnMetrics = responseData.response["tenantTxnMetrics"];
				    	scope.bar_data = angular.copy(tenantTxnMetrics);
						createSuccessFailureChartBar(scope.bar_data);
				        break; 
				    default: 
				     modelUsageTrendLine = responseData.response["modelUsageTrendLine"];
					 raUsageTrendLine = responseData.response["raUsageTrendLine"];
/*					 usageDynamics = responseData.response["usageDynamics"];
*/					 usageMetrics = responseData.response["usageMetrics"];
					 tenantTxnMetrics = responseData.response["tenantTxnMetrics"];
				     scope.ra_trend_format = dateFormat;
                     scope.model_trend_format = dateFormat;
                     scope.model_trend_count = gridCount;
                     scope.ra_trend_count = gridCount;
						scope.bar_data = angular.copy(tenantTxnMetrics);
						createSuccessFailureChartBar(scope.bar_data);
						scope.bar_data = angular.copy(usageMetrics);
						createChartPie(scope.bar_data);
						drawRaUsageTrendLine(raUsageTrendLine);
						drawModelUsageTrendLine(modelUsageTrendLine);
				    }
					
				}},
				function(errorData){
					scope.bar_data=[];
					createChartPie([]);
					createSuccessFailureChartBar([]);
					drawRaUsageTrendLine([]);
					drawModelUsageTrendLine([]);
					log.error('Error in fetching Usage Dynamics');
				}
		);
	};
	
	function setFailTxn(){
		raService.getTopHundredFailTxn(scope.filters).then(
				function(responseData){
					log.info('Received Fail Txn List...');
					log.info(responseData);
					scope.fail_data = angular.copy(responseData.response);
				},
				function(errorData){
					scope.fail_data=[];
					log.error('Error in Fail Txn List');
				}
		);
	};
	function getUsageDynamicsGrid(){
		raService.getUsageDynamicsGrid(scope.filters).then(
				function(responseData){
					if(responseData.error){
						scope.data_API=[];
						log.error('Error in fetching Usage Dynamics');
					}else{
					log.info('Received Usage Dynamics Grid...');
					log.info(responseData);
					scope.data_API = angular.copy(responseData.response);
					}},
				function(errorData){
					scope.data_API=[];
					log.error('Error in fetching Usage Dynamics');
				}
		);
	};
	
  	
	scope.selctSrtDt;
	scope.selctEndDt;
	scope.allTntSrtDt;
	scope.allTntEndDt;
	
	$(function() {
		moment.tz.setDefault("America/New_York");
	    var start = moment().subtract(1, 'days');
		var end = moment();

	    function cb(start, end,num) {
	    	if(num === "Last 7 Days"){
    	        $('#reportrange span').html("Last 7 Days");
	    	}else if(num === "Last 30 Days"){
    	        $('#reportrange span').html("Last 30 Days");
	    	}else if(num === "Last 90 Days"){
    	        $('#reportrange span').html("Last 90 Days");
	    	}
            else if(num === "Last 180 Days"){
    	        $('#reportrange span').html("Last 180 Days");
	    	}
            else if(num === "Custom Range"){    	       
            	$('#reportrange span').html(start.format('YYYY-MMM-DD') + ' - ' + end.format('YYYY-MMM-DD'));
	    	}
            else{
             $('#reportrange span').html("Last 24 Hours");
	         start = moment().subtract(1, 'days').format("YYYY-MMM-DD HH:mm");
			 end = moment().format("YYYY-MMM-DD HH:mm");
			 num="Last 24 Hours"
			 }
	    	if(num === "Last 24 Hours"){
	    		scope.allTntSrtDt = start;
		        scope.allTntEndDt = end;
	    	}else{
	    		scope.allTntSrtDt = start.format("YYYY-MMM-DD HH:mm");
		        scope.allTntEndDt = end.format("YYYY-MMM-DD HH:mm");
	    	}
	    }

	    $('#reportrange').daterangepicker({
	    	allTntSrtDt: start,
	        allTntEndDt: end,
	        ranges: {
		           'Last 24 Hours': [moment().subtract(1, 'days'), moment(),"1"],
		           'Last 7 Days': [moment().subtract(6, 'days'), moment(),"2"],
		           'Last 30 Days': [moment().subtract(29, 'days'), moment(),"3"],
		           'Last 90 Days': [moment().subtract(89, 'days'), moment(),"4"],
	               'Last 180 Days': [moment().subtract(179, 'days'), moment(),"5"]
		        },
		        maxDate:end
	    }, cb);

	    cb(start, end);

		});
	
	//dateRange
	$(function() {
		moment.tz.setDefault("America/New_York");
	    var start = moment().subtract(1, 'days');
		var end = moment();

	    function cb(start, end ,num) {
	    	if(num === "Last 7 Days"){
    	        $('#slctreportrange span').html("Last 7 Days");
	    	}else if(num === "Last 30 Days"){
    	        $('#slctreportrange span').html("Last 30 Days");
	    	}else if(num === "Last 90 Days"){
    	        $('#slctreportrange span').html("Last 90 Days");
	    	}
            else if(num === "Last 180 Days"){
    	        $('#slctreportrange span').html("Last 180 Days");
	    	}
            else if(num === "Custom Range"){    	       
            	$('#slctreportrange span').html(start.format('YYYY-MMM-DD') + ' - ' + end.format('YYYY-MMM-DD'));
	    	}
            else{
             $('#slctreportrange span').html("Last 24 Hours");
	         start = moment().subtract(1, 'days').format("YYYY-MMM-DD HH:mm");
			 end = moment().format("YYYY-MMM-DD HH:mm");
			 num="Last 24 Hours"
			 }
	    	if(num === "Last 24 Hours"){
	    		scope.selctSrtDt = start;
		        scope.selctEndDt = end;
	    	}else{
	    		scope.selctSrtDt = start.format("YYYY-MMM-DD HH:mm");
		        scope.selctEndDt = end.format("YYYY-MMM-DD HH:mm");
	    	}
	    }

	    $('#slctreportrange').daterangepicker({
	    	selctSrtDt: start,
	    	selctEndDt: end,
	        ranges: {
		           'Last 24 Hours': [moment().subtract(1, 'days'), moment(),"1"],
		           'Last 7 Days': [moment().subtract(6, 'days'), moment(),"2"],
		           'Last 30 Days': [moment().subtract(29, 'days'), moment(),"3"],
		           'Last 90 Days': [moment().subtract(89, 'days'), moment(),"4"],
	               'Last 180 Days': [moment().subtract(179, 'days'), moment(),"5"]
		        },
        maxDate:end
	    }, cb);

	    cb(start, end);

		});
	
	
	//Line Graph 
	
	function drawModelUsageTrendLine(selected_tnt_data) {
	    var selectedLineChart;
	    var data = new google.visualization.DataTable();
	    data.addColumn('date', 'timeline');
	   
	    var columnArr = [];
	    //Added all the column
	    $.each(selected_tnt_data, function(key,value){
			    data.addColumn('number', key);
			    columnArr.push(key);
			});
	    
	      //Added value to each column 
	      var index = 0;
	       $.each(selected_tnt_data, function(key,value){
	    	var arrVal = value;
	    	$.each(arrVal, function(key,value){
	    		var arr = key.split("-");
	    		var val = value;
		    	var row ;
		    	 if(scope.model_trend_format === 'HH:mm'){
		 	    	scope.model_trend_count = -1;
		 	    	 row = [new Date(arr[0],arr[1]-1,arr[2],arr[3])];
		    	 }else{
		 	    	 row = [new Date(arr[0],arr[1]-1,arr[2])];
		    	 }
		    	
		    	
		    	 $.each(columnArr, function(key,value){
		    		 if(key === index){
		    		 row.push(val);
		    		 }else{
		    	     row.push(0);	 
		    		 }
		    	 });
		   	    data.addRow(row);
	    	});
	    	 index=index+1;
	    	});
	    if(columnArr.length === 0){
		    data.addColumn('number',"");
	    }
	  
	    switch (columnArr.length) {
	    case 1:
	    	  data = google.visualization.data.group(data,[0],
		    		  [{'column': 1, 'aggregation': google.visualization.data.sum, 'type': 'number'}]
		    		); 
	    	break; 
	    case 2:
	    	  data = google.visualization.data.group(data,[0],
		    		  [{'column': 1, 'aggregation': google.visualization.data.sum, 'type': 'number'},{'column': 2, 'aggregation': google.visualization.data.sum, 'type': 'number'}]
		    		);
	        break; 
        case 3:
        	  data = google.visualization.data.group(data,[0],
    	    		  [{'column': 1, 'aggregation': google.visualization.data.sum, 'type': 'number'},{'column': 2, 'aggregation': google.visualization.data.sum, 'type': 'number'},{'column': 3, 'aggregation': google.visualization.data.sum, 'type': 'number'}]
    	    		);
	        break; 
        case 4:
	           data = google.visualization.data.group(data,[0],
    		  [{'column': 1, 'aggregation': google.visualization.data.sum, 'type': 'number'},{'column': 2, 'aggregation': google.visualization.data.sum, 'type': 'number'},{'column': 3, 'aggregation': google.visualization.data.sum, 'type': 'number'},{'column': 4, 'aggregation': google.visualization.data.sum, 'type': 'number'}]
    		);
           break; 
        case 5:
	        data = google.visualization.data.group(data,[0],
    		  [{'column': 1, 'aggregation': google.visualization.data.sum, 'type': 'number'},{'column': 2, 'aggregation': google.visualization.data.sum, 'type': 'number'},{'column': 3, 'aggregation': google.visualization.data.sum, 'type': 'number'},{'column': 4, 'aggregation': google.visualization.data.sum, 'type': 'number'},{'column': 5, 'aggregation': google.visualization.data.sum, 'type': 'number'}]
    		);
           break; 
        case 6:
	         data = google.visualization.data.group(data,[0],
    		  [{'column': 1, 'aggregation': google.visualization.data.sum, 'type': 'number'},{'column': 2, 'aggregation': google.visualization.data.sum, 'type': 'number'},{'column': 3, 'aggregation': google.visualization.data.sum, 'type': 'number'},{'column': 4, 'aggregation': google.visualization.data.sum, 'type': 'number'},{'column': 5, 'aggregation': google.visualization.data.sum, 'type': 'number'},{'column': 6, 'aggregation': google.visualization.data.sum, 'type': 'number'}]
    		);
           break; 
        case 7:
	  data = google.visualization.data.group(data,[0],
    		  [{'column': 1, 'aggregation': google.visualization.data.sum, 'type': 'number'},{'column': 2, 'aggregation': google.visualization.data.sum, 'type': 'number'},{'column': 3, 'aggregation': google.visualization.data.sum, 'type': 'number'},{'column': 4, 'aggregation': google.visualization.data.sum, 'type': 'number'},{'column': 5, 'aggregation': google.visualization.data.sum, 'type': 'number'},{'column': 6, 'aggregation': google.visualization.data.sum, 'type': 'number'},{'column': 7, 'aggregation': google.visualization.data.sum, 'type': 'number'}]
    		);
     break; 
        case 8:
	  data = google.visualization.data.group(data,[0],
    		  [{'column': 1, 'aggregation': google.visualization.data.sum, 'type': 'number'},{'column': 2, 'aggregation': google.visualization.data.sum, 'type': 'number'},{'column': 3, 'aggregation': google.visualization.data.sum, 'type': 'number'},{'column': 4, 'aggregation': google.visualization.data.sum, 'type': 'number'},{'column': 5, 'aggregation': google.visualization.data.sum, 'type': 'number'},{'column': 6, 'aggregation': google.visualization.data.sum, 'type': 'number'},{'column': 7, 'aggregation': google.visualization.data.sum, 'type': 'number'},{'column': 8, 'aggregation': google.visualization.data.sum, 'type': 'number'}]
    		);
     break; 
        case 9:
	  data = google.visualization.data.group(data,[0],
    		  [{'column': 1, 'aggregation': google.visualization.data.sum, 'type': 'number'},{'column': 2, 'aggregation': google.visualization.data.sum, 'type': 'number'},{'column': 3, 'aggregation': google.visualization.data.sum, 'type': 'number'},{'column': 4, 'aggregation': google.visualization.data.sum, 'type': 'number'},{'column': 5, 'aggregation': google.visualization.data.sum, 'type': 'number'},{'column': 6, 'aggregation': google.visualization.data.sum, 'type': 'number'},{'column': 7, 'aggregation': google.visualization.data.sum, 'type': 'number'},{'column': 8, 'aggregation': google.visualization.data.sum, 'type': 'number'},{'column': 9, 'aggregation': google.visualization.data.sum, 'type': 'number'}]
    		);
     break; 
        case 10:
	  data = google.visualization.data.group(data,[0],
    		  [{'column': 1, 'aggregation': google.visualization.data.sum, 'type': 'number'},{'column': 2, 'aggregation': google.visualization.data.sum, 'type': 'number'},{'column': 3, 'aggregation': google.visualization.data.sum, 'type': 'number'},{'column': 4, 'aggregation': google.visualization.data.sum, 'type': 'number'},{'column': 5, 'aggregation': google.visualization.data.sum, 'type': 'number'},{'column': 6, 'aggregation': google.visualization.data.sum, 'type': 'number'},{'column': 7, 'aggregation': google.visualization.data.sum, 'type': 'number'},{'column': 8, 'aggregation': google.visualization.data.sum, 'type': 'number'},{'column': 9, 'aggregation': google.visualization.data.sum, 'type': 'number'},{'column': 10, 'aggregation': google.visualization.data.sum, 'type': 'number'}]
    		);
     break; 
	    default: 
	   
	    }
	    var startDateArr = scope.selctSrtDt.split('-');
	    var endDateArr = scope.selctEndDt.split('-');
	    var chartHeight = 173;
	    var chartOptions ;
	    if(scope.model_trend_format === 'HH:mm'){
	    	  chartOptions = {
	    		        isStacked: true,
	    		        "backgroundColor": "transparent",
	    		        height: chartHeight,
	    		        showInLegend: true, 
	    			    "legend" : {position: 'top'},
	    			    chartArea:{left:70,right:0,top:20},
	    		        pointSize: 5,
	    		        hAxis: {
	    		         viewWindow: {
	    		        	  min:new Date(startDateArr[0],scope.monthShortNames.indexOf(startDateArr[1]),startDateArr[2].substring(0,2),startDateArr[2].substring(3,5),'00'),
	    		  	      	  max:new Date(endDateArr[0],scope.monthShortNames.indexOf(endDateArr[1]),endDateArr[2].substring(0,2),endDateArr[2].substring(3,5),'59')
	    		            },
	    		            gridlines: {
	    		                count: -1,
	    		                units: {
	    		                  days: {format: ['MMM dd']},
	    		                }
	    		              },
	    		              minorGridlines: {
	    		                  units: {
		    		                  hours: {format: ['hh:mm a', 'ha']},
	    		                  }
	    		                }
	    		          },
	    		          "vAxis": {
	    		        	  viewWindow:{ min: 0 }
	    		          }
	    		    };	
	    }else{ 
	    	var month = scope.monthShortNames.indexOf(startDateArr[1]);
	    	var endMonth = scope.monthShortNames.indexOf(endDateArr[1]);
	    	var sday =(scope.model_trend_format === 'MMM yy')?1: startDateArr[2].substring(0,2);
	    	var lastDay = endDateArr[2].substring(0,2);
	    	var eday = (scope.model_trend_format === 'MMM yy' & lastDay < 15)?15:lastDay;
		    var grid_count = (scope.model_trend_count === 3)?4:scope.model_trend_count;

            chartOptions = {
		        isStacked: true,
		        "backgroundColor": "transparent",
		        height: chartHeight,
		        showInLegend: true, 
			    "legend" : {position: 'top'},
			    chartArea:{left:70,right:0,top:20},
		        pointSize: 5,
		        hAxis: {
		          viewWindow: {
		        	  min:new Date(startDateArr[0],month,sday,'00'),
		  	      	  max:new Date(endDateArr[0],endMonth,eday,'23')
		            },
		            gridlines: {
		                count:grid_count
		            },
		            format:scope.model_trend_format
	}
		    };
	    }
	    var formatter;
	    if(scope.model_trend_format === 'HH:mm'){
	    	   formatter = new google.visualization.DateFormat({pattern: 'MMM dd hh a'});
			   formatter.format(data, 0);
	    }
	    else if(scope.model_trend_format === 'dd MMM yy'){
			   formatter = new google.visualization.DateFormat({pattern: 'dd MMM yy'});
			   formatter.format(data, 0);
		   }else{
			   formatter = new google.visualization.DateFormat({pattern: 'MMM yy'});
			   formatter.format(data, 0);
	    }
	    selectedLineChart = new google.visualization.LineChart(document.getElementById('selectedTntChart'));
	    selectedLineChart.draw(data, chartOptions);
	}

    //Line Graph 
	
	function drawRaUsageTrendLine(all_tnt_data) {
	    var allTntChart;
	    var data = new google.visualization.DataTable();
	    data.addColumn('date', 'timeline');
	    data.addColumn('number', 'Success');
	    data.addColumn('number', 'Failure');
	   if(scope.ra_trend_format === 'HH:mm'){
	   $.each(all_tnt_data, function(key,value){
	    	var arr = key.split("-");
	    	scope.ra_trend_count = -1;
   	    data.addRow([new Date(arr[0],arr[1]-1,arr[2],arr[3]), value['Success'],value['failureCount']]);
			});
	   }else{
		   $.each(all_tnt_data, function(key,value){
		    	var arr = key.split("-");
		   	    data.addRow([new Date(arr[0],arr[1]-1,arr[2]), value['Success'],value['failureCount']]);
		   }); 
	   }
	   data = google.visualization.data.group(data,[0],
	    		  [{'column': 1, 'aggregation': google.visualization.data.sum, 'type': 'number'},{'column': 2, 'aggregation': google.visualization.data.sum, 'type': 'number'}]
	    		);
	   var startDateArr = scope.allTntSrtDt.split('-');
	    var endDateArr = scope.allTntEndDt.split('-');
	    var chartHeight = 173;
	    var grid_count = (scope.ra_trend_count === 3)?4:scope.ra_trend_count;
	    var chartOptions;
	    if(scope.ra_trend_format === 'HH:mm'){
	    	  chartOptions = {
	    		        isStacked: true,
	    		        "backgroundColor": "transparent",
	    		        height: chartHeight,
	    		        showInLegend: true, 
	    			    "legend" : {position: 'top'},
	    			    chartArea:{left:70,right:0,top:20},
	    			    colors:["#72A10E","#C70039"],
	    		        pointSize: 5,
	    		        hAxis: {
	    		         viewWindow: {
	    		        	  min:new Date(startDateArr[0],scope.monthShortNames.indexOf(startDateArr[1]),startDateArr[2].substring(0,2),startDateArr[2].substring(3,5),'00'),
	    		  	      	  max:new Date(endDateArr[0],scope.monthShortNames.indexOf(endDateArr[1]),endDateArr[2].substring(0,2),endDateArr[2].substring(3,5),'59')
	    		            },
	    		            gridlines: {
	    		                count: -1,
	    		                units: {
	    		                  days: {format: ['MMM dd']},
	    		                }
	    		              },
	    		              minorGridlines: {
	    		                  units: {
		    		                  hours: {format: ['hh:mm a', 'ha']},
	    		                  }
	    		                }
/*		    			   showTextEvery:scope.ra_trend_count,
*/	    		          },
	    		          vAxis: {
	    		        	  viewWindow:{ min: 0 }
	    		          }
	    		    };	
	    }else{
	    	var month = (scope.ra_trend_format === 'MMM dd') ? scope.monthShortNames.indexOf(startDateArr[1]):scope.monthShortNames.indexOf(startDateArr[1]);
	    	month = (scope.ra_trend_format === 'yyyy') ?0:month;
	    	var endMonth = scope.monthShortNames.indexOf(endDateArr[1]);
	    	var sday =(scope.ra_trend_format === 'MMM yy'|scope.ra_trend_format === 'yyyy')?1: startDateArr[2].substring(0,2);
	    	var lastDay = endDateArr[2].substring(0,2);
	    	var eday = (scope.ra_trend_format === 'MMM yy' & lastDay < 15)?15:lastDay;
/*            var count = (scope.ra_trend_format === 'yyyy')?(endDateArr[0]-startDateArr[0])>2?(endDateArr[0]-startDateArr[0])+1:(endDateArr[0]-startDateArr[0])+2:(rorsCont<5)?rorsCont+2:-1;
*/           /* if(scope.ra_trend_format === 'MMM yyyy'){
                count = (endMonth-month)<5?(endMonth-month)+2:-1;
            }
            if(scope.ra_trend_format === 'MMM dd'){
            	if(month === endMonth){
                    count = (eday-sday)<5?(eday-sday)+2:((eday-sday)>10?10:eday-sday);
            	}
            	else{
                    count = (eday+sday)<5?(eday+sday)+2:((eday+sday)>10?10:eday+sday);
            	}
            }*/
            chartOptions = {
	    		        isStacked: true,
	    		        "backgroundColor": "transparent",
	    		        height: chartHeight,
	    		        showInLegend: true, 
	    			    "legend" : {position: 'top'},
	    			    chartArea:{left:70,right:0,top:20},
	    			    colors:["#72A10E","#C70039"],
	    		        pointSize: 5,
	    		        hAxis: {
	    		         viewWindow: {
	    		        	  min:new Date(startDateArr[0],month,sday,'00'),
	    		  	      	  max:new Date(endDateArr[0],endMonth,eday,'23')
	    		            },
	    		            gridlines: {
        			        count: grid_count
	    				      },
/*	    			        showTextEvery:scope.ra_trend_count,
 * 
*/	    		            
/*	    		            ticks: rorsCont,
*/	    		            format:scope.ra_trend_format
	    		          },
	    		          "vAxis": {
	    		        	  viewWindow:{ min: 0 }
	    		          }
	    		    };
	    }
	    var formatter;
	    if(scope.ra_trend_format === 'HH:mm'){
	    	   formatter = new google.visualization.DateFormat({pattern: 'MMM dd hh a'});
			   formatter.format(data, 0);
	    }
	    else if(scope.ra_trend_format === 'dd MMM yy'){
			   formatter = new google.visualization.DateFormat({pattern: 'dd MMM yy'});
			   formatter.format(data, 0);
		   }else{
			   formatter = new google.visualization.DateFormat({pattern: 'MMM yy'});
			   formatter.format(data, 0);
	    }
	    allTntChart = new google.visualization.LineChart(document.getElementById('allTntChart'));
	    allTntChart.draw(data, chartOptions);
	}
	
// initialization of all Tenant data graph
	
	scope.initializeAllTenant = function(){
		scope.colSel = 0;
		document.getElementById('colDisp').style.display = 'none';
		var allTenants = document.getElementById("tenant").value;
		scope.tenants = allTenants.substring(1,allTenants.indexOf("]")).split(",");
		scope.filters.runAsOfDateFromString = scope.allTntSrtDt;
		scope.filters.runAsOfDateToString = scope.allTntEndDt;
		if(scope.selectedTenants.length === 0){
			scope.selectedTenants.push(document.getElementById('tenantCode_current').value);
		}
		scope.filters.tenantNames = scope.tenants;
		scope.filters.selectionType='ALL';
	    setUsageDynamics('ALL');
	};
	
// initialization of all graphs
	
	scope.initSelectedTenant = function(){
		scope.colSel = 0;
		document.getElementById('colDisp').style.display = 'none';
		var allTenants = document.getElementById("tenant").value;
		scope.tenants = allTenants.substring(1,allTenants.indexOf("]")).split(",");
		scope.filters.runAsOfDateFromString = scope.selctSrtDt;
		scope.filters.runAsOfDateToString = scope.selctEndDt;
		if(scope.selectedTenants.length === 0){
			scope.selectedTenants.push(document.getElementById('tenantCode_current').value);
		}
		scope.filters.tenantNames = scope.selectedTenants;
		scope.filters.selectedTnt=scope.filters.tenantNames;
		scope.filters.selectionType='SELECTED';
	    setUsageDynamics("SELECTED");
        setFailTxn();
        getUsageDynamicsGrid();
	};
	
	// initialization of all graphs
	
	scope.init = function(){
		scope.colSel = 0;
		document.getElementById('colDisp').style.display = 'none';
		var allTenants = document.getElementById("tenant").value;
		scope.tenants = allTenants.substring(1,allTenants.indexOf("]")).split(",");
		scope.filters.runAsOfDateFromString = scope.allTntSrtDt;
		scope.filters.runAsOfDateToString = scope.allTntEndDt;
		if(scope.selectedTenants.length === 0){
			scope.selectedTenants.push(document.getElementById('tenantCode_current').value);
		}
		scope.filters.tenantNames = scope.tenants;
		scope.filters.selectedTnt = [];
		scope.filters.selectedTnt.push(document.getElementById('tenantCode_current').value.trim());
		scope.filters.selectionType='DEFAULT';
	    setUsageDynamics('DEFAULT');
/*	    drawUsageTrend('DEFAULT');
*/        setFailTxn();
        getUsageDynamicsGrid();
	};
	
	scope.init();
	/**
	 * grid for displaying data
	 */
	 scope.tableGrid_API = {
				data: 'data_API',
				enableColumnResize: true,
				enableRowSelection: false,
				filterColumnLabel:'Model Name',
				multiSelect: false,
				columnDefs: [{field:'', displayName:'Model Name',cellTemplate:'<div class="cell_template"  title={{row.entity.modelName}}>{{row.entity.modelName}}</div>'}, 
					{field:'', displayName:'Model Version',cellTemplate:'<div class="cell_template"  title={{row.entity.modelVersion}}>{{row.entity.modelVersion}}</div>',headerClass:"listStatHeader", cellClass:"listStatHeader" , width: "60px"},
					{field:'', displayName:'Total Trans',cellTemplate:'<div class="cell_template"  title={{row.entity.total}}>{{row.entity.total}}</div>', headerClass:"listStatHeader", cellClass:"listStatHeader" , width: "90px"},
					{field:'', displayName:'Success',cellTemplate:'<div class="cell_template"  title={{row.entity.Success}}>{{row.entity.Success}}</div>', headerClass:"listStatHeader", cellClass:"listStatHeader" , width: "60px"},
					{field:'', displayName:'Model Failures',cellTemplate:'<div class="cell_template"  title={{row.entity.modelFailures}}>{{row.entity.modelFailures}}</div>', headerClass:"listStatHeader", cellClass:"listStatHeader" , width: "90px"},
					{field:'', displayName:'I/P Validation Failures',cellTemplate:'<div class="cell_template"  title={{row.entity.inputValidationFailure}}>{{row.entity.inputValidationFailure}}</div>', headerClass:"listStatHeader", cellClass:"listStatHeader" , width: "90px"},
					{field:'', displayName:'O/P Validation Failures',cellTemplate:'<div class="cell_template"  title={{row.entity.outputValidationFailure}}>{{row.entity.outputValidationFailure}}</div>', headerClass:"listStatHeader", cellClass:"listStatHeader" , width: "90px"},
					{field:'', displayName:'Tech Failures',cellTemplate:'<div class="cell_template"  title={{row.entity.otherFailures}}>{{row.entity.otherFailures}}</div>', headerClass:"listStatHeader", cellClass:"listStatHeader" , width: "90px"},
					{field:'', displayName:'Avg. Model Resp Time (sec)',cellTemplate:'<div class="cell_template"  title={{row.entity.modelResponseTime}}>{{row.entity.modelResponseTime}}</div>', headerClass:"listStatHeader", cellClass:"listStatHeader" , width: "90px"},
					{field:'', displayName:'Avg. E2E Resp Time (sec)', headerClass:"listStatHeader",cellTemplate:'<div class="cell_template"  title={{row.entity.endToEndTime}}>{{row.entity.endToEndTime}}</div>', cellClass:"listStatHeader" , width: "90px"},
					{field:'', displayName:'Modelet Utilization (sec)', headerClass:"listStatHeader",cellTemplate:'<div class="cell_template"  title={{row.entity.modelUtilization}}>{{row.entity.modelUtilization}}</div>', cellClass:"listStatHeader" , width: "90px"}
					],
                     sortInfo: {
							      fields: ['modelName'],
							      directions: ['asc']
							    }
		}
	

	 scope.tableGrid_TOP_HUNDRD_TXN = {
				data: 'fail_data',
				enableColumnResize: true,
				enableRowSelection: false,
				filterColumnLabel:'Model Name',
				tooltip: {isHtml: true},
				multiSelect: false,
				columnDefs: [{field:'', displayName:'Transaction Id',cellTemplate:'<div class="cell_template"  title={{row.entity.clientTransactionId}}>{{row.entity.clientTransactionId}}</div>'}, 
					{field:'modelName', displayName:'Model Name',cellTemplate:'<div class="cell_template"  title={{row.entity.modelName}}>{{row.entity.modelName}}</div>', headerClass:"listStatHeader", cellClass:"listStatHeader" , width: "90px"},
					{field:'modelVersion', displayName:'Model Version',cellTemplate:'<div class="cell_template"  title={{row.entity.modelVersion}}>{{row.entity.modelVersion}}</div>', headerClass:"listStatHeader", cellClass:"listStatHeader" , width: "60px"},
					{field:'runDate', displayName:'Run Date',cellTemplate:'<div class="cell_template"  title={{row.entity.runDate}}>{{row.entity.runDate}}</div>', headerClass:"listStatHeader", cellClass:"listStatHeader" , width: "90px"},
					{field:'errorCode', displayName:'Error Code', cellTemplate: '<div ng-click=showDescription(row.entity.errorDescription) class="error_code">{{row.entity.errorCode}}</div>',headerClass:"listStatHeader", cellClass:"listStatHeader" , width: "90px"},
					{field:'', displayName:'I/O', cellTemplate: '<div ng-click=downloadIO(row.entity) ><div class="download_img"><img src="./resources/images/download.png"/></div></div>',headerClass:"listStatHeader", cellClass:"listStatHeader" , width: "30px"},
					]/*,
                  sortInfo: {
							      fields: ['modelName'],
							      directions: ['asc']
							    }*/
		}
	 

		  scope.showDescription = function(desc) {
			  timeout(function () {
	                dialogs.notify('Error Description',desc);
	            }, 0);
				  };
		  
}];