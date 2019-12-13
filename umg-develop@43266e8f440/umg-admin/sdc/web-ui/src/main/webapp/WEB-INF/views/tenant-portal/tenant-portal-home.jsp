<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html>
<html data-ng-app="tntPortalModule" ng-cloak>
<head>
<meta charset="utf-8">
<title>Tenant Portal</title>
<!-- Generic page styles -->
<script src="<c:url value="/resources/lib/jquery/jquery-1.9.1.js" />"></script>	
<script src="<c:url value="/resources/lib/angularJS/angular.js" />"></script>
<link rel="stylesheet" type="text/css" media="screen" href="<c:url value="/resources/css/tenant-portal/tnt-portal-main.css" />" />
<link rel="stylesheet" type="text/css" media="screen" href="<c:url value="/resources/lib/jsonExplorer/gd-ui-jsonexplorer.css" />" />

<script type="text/javascript">
$(document).ready(function() {
	$("#splitterContainer").vsplitter({
		collapsed:false,
		leftPane:$('#leftPane'),
		rightPane:$('#rightPane'),
		container:$('#splitterContainer')
	}
			
	);
});

</script>
</head>
<body ng-controller="reportController">

<!-- EXPORT MODEL -->

<div class="modal fade" id="exportModal" tabindex="-1" role="dialog" aria-labelledby="exportModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" style="color: orange;" id="reportModalLabel">Export Transactions</h4>
      </div>
      <div class="modal-body">
        What transactions do you want to export ? <br>
        <input type="radio" id="all" name="transaction.type" ng-model="all.transactions" value="all" ng-click="disableAndEnableExportOptionsAll()"> All transactions ({{grid.pagingOptions.totalServerItems}})<br>
        <input type="radio" id="selected" name="transaction.type" ng-model="all.transactions" value="selected" ng-click="disableAndEnableExportOptionsSelected()"> Selected transactions ({{selectedTransactions.length}})<br>
        <br/>
        What is the purpose for export ? <br>
        <input type="radio" id="billing" name="export.option" ng-model="billing" value="billing"> Billing <br>
        <input type="radio" id="rerun" name="export.option" ng-model="re.run" value="rerun"> Re-run <br>
      </div>
      <div class="modal-footer">
        <div>
        	<span class="pull-left" style="color: red;" ng-show="exportMsg">{{exportMsg}}</span>
			<button  type="button" class="btn-defualt bg-dark-gray rpt-btn rd-corner5" id="cancel_export" data-dismiss="modal"> CANCEL </button>
			<button  type="button" class="btn-warning rpt-btn rd-corner5" id="ok" ng-click="exportBillingOrRerunReport()"> OK </button>
		</div>
      </div>
    </div>
  </div>
</div>

<!-- Modal -->
<div class="modal fade" id="reportModal" tabindex="-1" role="dialog" aria-labelledby="reportModalLabel" aria-hidden="true">
  <div class="modal-dialog" style="width: 90%;">
    <div class="modal-content">
      <div class="modal-header">
        <button id="cross" type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" style="color: orange;" id="reportModalLabel">Report-Transaction {{clientTxnId}}</h4>
      </div>
      <div class="modal-body">
      <div style="width: 100%; float: left;">
      	<span class="prenexttxn pull-left" ng-hide="currentIndex == 0"><a id="preTxn" style="cursor: pointer;" ng-click="setPreviousTxn(currentIndex)"><span class="glyphicon glyphicon-triangle-left cursor-hand"></span>Previous Transaction</a></span>
      	<span class="prenexttxn pull-right" ng-hide="currentIndex == grid.pagingOptions.totalServerItems-1"><a id="nextTxn" style="cursor: pointer;" ng-click="setNextTxn(currentIndex)">Next Transaction<span class="glyphicon glyphicon-triangle-right cursor-hand"></span></a></span>
      </div>
      <div style="height: 520px;">
		<div class="menu-container" style="width: 20%; float: left;">
			  <div class="menu">
			      <ul>
			      	<li id="tablur_input" ng-class="{'active-tab':isCurrentStep(0)}" ng-click="setCurrentStep(0)">Tabular Input<img class="pull-right" src="./resources/images/arrow.png" width="20px" height="20px" ng-show="isCurrentStep(0)"></li>
			      	<li id="tablur_output" ng-class="{'active-tab':isCurrentStep(1)}" ng-click="setCurrentStep(1)">Tabular Output<img class="pull-right" src="./resources/images/arrow.png" width="20px" height="20px" ng-show="isCurrentStep(1)"></li>
			        <li id="json_input_tnt" ng-class="{'active-tab':isCurrentStep(2)}" ng-click="setCurrentStep(2)">JSON Input (Tenant)<img class="pull-right" src="./resources/images/arrow.png" width="20px" height="20px" ng-show="isCurrentStep(2)"></li>
			        <li id="json_input_model" ng-class="{'active-tab':isCurrentStep(3)}" ng-click="setCurrentStep(3)">JSON Input (Model)<img class="pull-right" src="./resources/images/arrow.png" width="20px" height="20px" ng-show="isCurrentStep(3)"></li>
			        <li id="json_output_tnt" ng-class="{'active-tab':isCurrentStep(4)}" ng-click="setCurrentStep(4)">JSON Output (Tenant)<img class="pull-right" src="./resources/images/arrow.png" width="20px" height="20px" ng-show="isCurrentStep(4)"></li>
			        <li id="json_output_model" ng-class="{'active-tab':isCurrentStep(5)}" ng-click="setCurrentStep(5)">JSON Output (Model)<img class="pull-right" src="./resources/images/arrow.png" width="20px" height="20px" ng-show="isCurrentStep(5)"></li>
			      </ul>
			  </div>
		</div>
		<div class="viewport" id="viewport" ng-switch="getCurrentStep()">
			<div id="tenantInput" ng-switch-when="one">
			<section>
			<div class="popupTable" ng-show="tabularInputData">
			    <table class="tidmidTable">
			      <thead>
			        <tr class="header">
			          <th style="width: 40%;"><div>KEY</div></th>
			          <th style="width: 10%;"><div>TYPE</div></th>
			          <th style="width: 25%;"><div>TENANT VALUE</div></th>
			          <th style="width: 25%;"><div>MODEL VALUE</div></th>
			        </tr>
			      </thead>
			      <tbody>
			        <tr ng-repeat="tid in tabularInputData">
			          <td style="width: 40%; word-break: break-all; ">{{tid.key}}</td>
			          <td style="width: 10%;">{{tid.dataType}}</td>
			          <td style="width: 25%; word-break: break-word;">{{tid.tenantValue}}</td>
			          <td style="width: 25%; word-break: break-word;">{{tid.modelValue}}</td>
			        </tr>
			      </tbody>
			    </table>
			  </div>
			  <div ng-show="tabularInputData == null && loaded" class="popupTable && tabularInputDat.errorMessage">
			  		Input/Output data or definition is not available for the transaction. Unable to present data in tabular view.
			  </div>
			</section>
			</div>
       		<div id="modelInput" ng-switch-when="two">
       		<section>
			<div class="popupTable" ng-show="tabularOutputData">
			    <table class="tidmidTable">
			      <thead>
			        <tr class="header">
			          <th style="width: 40%;"><div>KEY</div></th>
			          <th style="width: 10%;"><div>TYPE</div></th>
			          <th style="width: 25%;"><div>TENANT VALUE</div></th>
			          <th style="width: 25%;"><div>MODEL VALUE</div></th>
			        </tr>
			      </thead>
			      <tbody>
			        <tr ng-repeat="tod in tabularOutputData">
			          <td style="width: 40%; word-break: break-all; ">{{tod.key}}</td>
			          <td style="width: 10%;">{{tod.dataType}}</td>
			          <td style="width: 25%; word-break: break-word;">{{tod.tenantValue}}</td>
			          <td style="width: 25%; word-break: break-word;">{{tod.modelValue}}</td>
			        </tr>
			      </tbody>
			    </table>
			  </div>		
			<div ng-show="tabularOutputData == null && loaded && errorMessage==null" class="popupTable">
			  		Input/Output data or definition is not available for the transaction. Unable to present data in tabular view.
			 </div>
			 <div ng-show="tabularOutputData == null && loaded && errorMessage" class="popupTable">
			  		Unable to present data in tabular view. Please refer to the JSON format to view the data.
			 </div>
			 </section>
       		</div>
       		
       		<div id="tenantInput" style="padding: 10px;" ng-switch-when="three"><json-explorer json-data="{{tenantInputJson}}"></json-explorer></div>
       		<div id="modelInput" style="padding: 10px;" ng-switch-when="four"><json-explorer json-data="{{modelInputJson}}"></json-explorer></div>
       		<div id="tenantOutput" style="padding: 10px;" ng-switch-when="five"><json-explorer json-data="{{tenantOutputJson}}"></json-explorer></div>
       		<div id="modelOutput" style="padding: 10px;" ng-switch-when="six"><json-explorer json-data="{{modelOutputJson}}"></json-explorer></div>
       	</div>
      
      </div>
       	
      </div>
      <div class="modal-footer">
      	<div class="rpt-btn-set ">
			<button  type="button" class="btn-defualt bg-dark-gray rpt-btn rd-corner5" id="export" ng-disabled="umgTxnId == ''" ng-click="exportReport()"> Export </button>
			<button  type="button" class="btn-warning rpt-btn rd-corner5" id="close" data-dismiss="modal"> Close </button>
		</div>
      </div>
    </div>
  </div>
</div>


<div class="rpt-container">

<div class="common-freeze" id="commonFreezeScreen" > commonFreezeScreen </div>
<div class="load-freeze" id="searchFreezeScreen" > 
    <div class="freeze-cancel-div"  > 
        <div class="load-freeze-header">loading...</div>
  	    <button type="button" class="freeze-cancel-btn btn-warning" ng-click="cancel()" ng-show="cancelAjax"> Cancel </button>
    </div>
</div>
	
<nav class="navbar navbar-inverse" style="height:35px;">
  <div class="container-fluid">
    <div class="navbar-header">
    	<span class="glyphicon glyphicon glyphicon-th-large"  style="color:white;"></span> &nbsp; &nbsp; &nbsp;
    	<span class="tnt-logo" ></span> 
    </div>

    <!-- Collect the nav links, forms, and other content for toggling -->
    <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
      <ul class="nav navbar-nav navbar-right">
        <li class="dropdown">
          <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">
          		Welcome, <sec:authentication property="principal.username" /> | ${TENANT_CODE} 
          		<span  class="caret"></span>
          	</a>
           <ul class="dropdown-menu" role="menu">
           <c:if test="${IS_ADMIN_ROLE}"> <li><a id="goto_admin" href="admin">Goto Admin</a></li> </c:if>
           <c:if test="${IS_DB_AUTHENTICATION}"> <li><a id="change_pass" href="changePassword">Change Password</a></li> </c:if>
            <li><a id="logout" href="#" onclick='location.href="<c:url value="j_spring_security_logout" />"'>logout</a></li>
          </ul>
        </li>
      </ul>
    </div><!-- /.navbar-collapse -->
  </div><!-- /.container-fluid -->
</nav>	
 <div class="rpt-header" > &nbsp; Reports </div>	

<div id="splitterContainer">
	<div id="leftPane">
		<ul class="nav nav-tabs" style="margin-left: -10px;">
		  <li id="filter" ng-class="{'active-filter':isCurrentSearchType(0)}" class="default-filter" ng-click="setCurrentSearchType(0)"><a ng-class="isCurrentSearchType(0) ? 'active-font' : 'default-font'">Filter</a></li>
		  <li id="srch" ng-class="{'active-filter':isCurrentSearchType(1)}" class="default-filter" ng-click="setCurrentSearchType(1)"><a ng-class="isCurrentSearchType(1) ? 'active-font' : 'default-font'">Search</a></li>
		</ul>

		 <br>
		 <div ng-switch="getCurrentSearchType()">
		 <div ng-switch-when="filter">
	 <form name="rpt-form" id="rpt-form">
		  <div class="label4">Report Type </div>
		  <select class="sel-200px " ng-model="searchOption.reportType" id="searchOption.reportTyp">
		    <option>Tenant Usage Report</option>
		 </select>
		 <br><br>
		 <div class="label4">Tenant</div>
		  <select class="sel-200px "  id="searchOption.tenant">
		    <option value="${TENANT_CODE}"  selected="selected">${TENANT_CODE}</option>
		 </select>
		 <br><br>
		  
		 <table>
		 <tr>
		 <td class="label4">Model</td>
		 <td align="left" class="label4"> Version </td>
		 </tr>
		 <tr>
		 <td style="padding-right:10px" >
		 <select class="sel-200px" ng-model="searchOption.model" ng-change="getVerionsforTheModel()" id="searchOption.model">
		    <option value="All"> All </option>
		    	<option ng-repeat="modelName in modelList" value="{{modelName}}"> {{modelName}} </option>
		    </select>
		 </select>
		 </td>
		 <td>
		 <select ng-model="searchOption.version" id="searchOption.version">
		    <option value="All"> All </option>
		    <option ng-repeat="version in versionList" value="{{version}}"> {{version}} </option>
		 </select>
		 </td>
		 </tr>
		 </table>
		 
		 <br>
		 <div>
		  <div class="label4"> Status </div>
		  <select class="sel-200px " ng-model="searchOption.status" id="searchOption.status">
		    <option value="All"> All </option>
		     <option value="success"> success </option>
		      <option value="failure"> failure </option>
		 </select>
		 </div>
		 <br>
		 <div class="label4"><input id="include_test" type="checkbox" ng-model="searchOption.includeTest"> Include Test Transactions </div>
		 <br>
<!-- Date field starts here  -->
		 <div class="label4"> Date </div>
		 <div id="single-date" ng-hide="searchOption.isCustomDate" >
		 <table >
		 <tr>
		 <td align="right"></td>
		 </tr>
		 <tr>
		 <td style="padding-right:10px">
		 <select class="sel-150px" ng-model="searchOption.month" id="searchOption.month">
		    <option value="" selected="selected">Select Month</option>
		    <option value="Jan-01-">January</option>
		    <option value="Feb-01-">February</option>
		    <option value="Mar-01-">March</option>
		    <option value="Apr-01-">April</option>
		    <option value="May-01-">May</option>
		    <option value="Jun-01-">June</option>
		    <option value="Jul-01-">July</option>
		    <option value="Aug-01-">August</option>
		    <option value="Sep-01-">September</option>
		    <option value="Oct-01-">October</option>
		    <option value="Nov-01-">November</option>
		    <option value="Dec-01-">December</option>
		 </select>
		 </td>
		 <td align="right">
		 <select class="sel-100px" ng-model="searchOption.year" id="searchOption.year">
		    <option value="" selected="selected">select year</option>
		    <option value="{{currentYear-num}}" ng-repeat="num in [0,1,2,3,4]">{{currentYear-num}}</option>
		   

		 </select>
		 </td>
		 </tr>
		 </table>
			<a id="custom" href="#" style="font-size: 12px;" ng-click="dateSettingChanges()">Select Custom Range</a> 
		 </div>
		 <div id="custom-date" ng-show="searchOption.isCustomDate" >
		 
		 <br>
		 <div class="dropdown" style="width: 130px; padding-left: 20px;">
		 <div class="label4"> Start Date </div>
						<a class="dropdown-toggle" id="dropdown1" role="button" data-toggle="dropdown" data-target="#" href="#">
							<div class="input-group">
								<input type="text"  data-ng-model="searchOption.startDate"  readonly name="searchOption.startDate" id="searchOption.startDate" > 
								<span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>
							</div>
						</a>
						<ul class="dropdown-menu" role="menu" aria-labelledby="dLabel">
							<datetimepicker data-ng-model="searchOption.startDate"	
							          data-datetimepicker-config="{ dropdownSelector: '#dropdown1', startView:'day', minView:'day' }" />
						</ul>
		  </div>
         
		 <div class="dropdown" style="width: 130px; padding-left: 20px;">
		 <div class="label4"> End Date </div>
						<a class="dropdown-toggle" id="dropdown2" role="button" data-toggle="dropdown" data-target="#" href="#">
							<div class="input-group">
								<input type="text"  data-ng-model="searchOption.endDate"  readonly name="searchOption.endDate" id="searchOption.endDate"  > 
								<span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>
							</div>
						</a>
						<ul class="dropdown-menu" role="menu" aria-labelledby="dLabel">
							<datetimepicker data-ng-model="searchOption.endDate"	
							          data-datetimepicker-config="{ dropdownSelector: '#dropdown2', startView:'day', minView:'day' }" />
						</ul>
			
		  <a id="default" href="#" style="font-size: 12px;" ng-click="dateSettingChanges()">Select Single Month </a> 
		  </div>
		 
		 </div>
<!-- Date field ends here  -->
<br>
<div class="rpt-btn-set ">
<button  type="button" class="btn-defualt bg-dark-gray rpt-btn rd-corner5" ng-click="clearSearchOption()" id="clearSearchOption-btn"> RESET </button>
<button  type="button" class="btn-warning rpt-btn rd-corner5" ng-click=" resetSearch(); search()" id="search-btn"> SEARCH </button>
</div>
</form>	 
		 </div>
		 
		 <div ng-switch-when="search">
		 <div class="label4"> Transaction ID [ Tenant / UMG ] </div>
		 <form name="srchForm">
		 	<textarea id="txnId" name="txnId" rows="6" ng-maxlength="255" ng-required="true" style="width: 95%;" data-ng-model="searchOption.searchString" placeholder="Comma seprated client or umg transaction ids"></textarea>
			<br>
			<div class="rpt-btn-set ">
			<button  type="button" class="btn-defualt bg-dark-gray rpt-btn rd-corner5" id="resetSearch" ng-click="searchOption.searchString = ''"> RESET </button>
			<button  type="button" class="btn-warning rpt-btn rd-corner5" id="search" ng-click="resetSearch(); searchByTxnId(searchOption.searchString)" ng-disabled="srchForm.$invalid"> SEARCH </button>
			</div>
			<div class="error-container ng-hide" ng-show="srchForm.txnId.$dirty &amp;&amp; srchForm.txnId.$invalid">
		 		<small class="error-msg" ng-show="srchForm.txnId.$error.required">Field is mandatory.</small>
				<small class="error-msg" ng-show="srchForm.txnId.$error.maxlength">Maximum 255 characters allowed.</small>
			</div>	
		 </form>
		 </div>
		 </div>
		 
		 
		
	</div><!-- left panel ends -->
	<div class="splitbarV">
		<div id="vbtnDiv" class="splitbuttonV"></div>
	</div>
	<!-- #leftPane -->
	
	
<div class="rpt-tool-bar">

<div class="rpt-title"> Tenant Usage Report </div>	

<div class="dropdown rpt-column-btn">
  <button id="dLabel" type="button" class="btn-defualt" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" ng-click="columnMsg=''">
    &nbsp; Columns &nbsp;
    <span class="caret"></span>
  </button>
  <ul class="dropdown-menu"  id="dropdown-menu" aria-labelledby="dLabel" onclick="event.stopPropagation();">
  		<li class=""><a tabindex="-1" href="#" ng-repeat="col in grid.columnDefs"><label><input type="checkbox" ng-model="col.visible" value="{{col.visible}}"/> {{col.displayName}} </label> </a></li>
  		<li style="text-align: right;padding-right: 20px;">
  		<button id="dLabel-close" type="button" class="btn-defualt" onclick="$(this).parent().dropdown('toggle');" > close </button> 
  		</li>
  		<li ng-show="columnMsg" style="padding: 4px;text-align:center;" class="error-msg"><div>{{columnMsg}} </div></li>
  </ul>
</div>
<div class="rpt-export-btn">
  <span> EXPORT : <span id="export_excel" ng-class="excelClass" class="grid-excel-icon-div cursor-hand" title="Export to excel" data-toggle="modal" data-target="#exportModal" ng-click="launchExportDailog()"></span> </span>
</div>
</div>		

	
		
		
			<div class="scroll-header">
				<div class='table-div'>
					<div class='header-div'>
						
						<!-- Select All -->
						<div class='cell' style="width: 30px;"><input id="select_All" type="checkbox" ng-model="selectAll" ng-change="selectAllTransactions()"></div>
						
						<div class='cell cursor-hand' ng-show="grid.columnDefs[0].visible" ng-click="sort(grid.columnDefs[0].displayName)">{{grid.columnDefs[0].displayName}}
							<span class="glyphicon glyphicon-triangle-top " ng-show="grid.columnDefs[0].order==1"></span>
							<span class="glyphicon glyphicon-triangle-bottom " ng-show="grid.columnDefs[0].order==2"></span>
						 </div>
						<div class='cell cursor-hand' ng-show="grid.columnDefs[1].visible" ng-click="sort(grid.columnDefs[1].displayName)">{{grid.columnDefs[1].displayName}}
							<span class="glyphicon glyphicon-triangle-top " ng-show="grid.columnDefs[1].order==1"></span>
							<span class="glyphicon glyphicon-triangle-bottom " ng-show="grid.columnDefs[1].order==2"></span>
						</div>
						<div class='cell cursor-hand' ng-show="grid.columnDefs[2].visible" ng-click="sort(grid.columnDefs[2].displayName)">{{grid.columnDefs[2].displayName}}
							<span class="glyphicon glyphicon-triangle-top " ng-show="grid.columnDefs[2].order==1"></span>
							<span class="glyphicon glyphicon-triangle-bottom " ng-show="grid.columnDefs[2].order==2"></span>
						</div>
						<div class='cell cursor-hand' ng-show="grid.columnDefs[3].visible" ng-click="sort(grid.columnDefs[3].displayName)">{{grid.columnDefs[3].displayName}}
							<span class="glyphicon glyphicon-triangle-top " ng-show="grid.columnDefs[3].order==1"></span>
							<span class="glyphicon glyphicon-triangle-bottom " ng-show="grid.columnDefs[3].order==2"></span>
						</div>
						<div class='cell cursor-hand' ng-show="grid.columnDefs[4].visible" ng-click="sort(grid.columnDefs[4].displayName)">{{grid.columnDefs[4].displayName}}
							<span class="glyphicon glyphicon-triangle-top " ng-show="grid.columnDefs[4].order==1"></span>
							<span class="glyphicon glyphicon-triangle-bottom " ng-show="grid.columnDefs[4].order==2"></span>
						</div>
						<div class='cell cursor-hand' ng-show="grid.columnDefs[5].visible" ng-click="sort(grid.columnDefs[5].displayName)">{{grid.columnDefs[5].displayName}}
							<span class="glyphicon glyphicon-triangle-top " ng-show="grid.columnDefs[5].order==1"></span>
							<span class="glyphicon glyphicon-triangle-bottom " ng-show="grid.columnDefs[5].order==2"></span>
						</div>
						<div class='cell cursor-hand' ng-show="grid.columnDefs[6].visible" ng-click="sort(grid.columnDefs[6].displayName)">{{grid.columnDefs[6].displayName}}
							<span class="glyphicon glyphicon-triangle-top " ng-show="grid.columnDefs[6].order==1"></span>
							<span class="glyphicon glyphicon-triangle-bottom " ng-show="grid.columnDefs[6].order==2"></span>
						</div>
						<div class='cell cursor-hand' ng-show="grid.columnDefs[7].visible" ng-click="sort(grid.columnDefs[7].displayName)">{{grid.columnDefs[7].displayName}}
							<span class="glyphicon glyphicon-triangle-top " ng-show="grid.columnDefs[7].order==1"></span>
							<span class="glyphicon glyphicon-triangle-bottom " ng-show="grid.columnDefs[7].order==2"></span>
						</div>
						<div class='cell cursor-hand' ng-show="grid.columnDefs[8].visible" ng-click="sort(grid.columnDefs[8].displayName)">{{grid.columnDefs[8].displayName}}
							<span class="glyphicon glyphicon-triangle-top " ng-show="grid.columnDefs[8].order==1"></span>
							<span class="glyphicon glyphicon-triangle-bottom " ng-show="grid.columnDefs[8].order==2"></span>
						</div>
						<div class='cell cursor-hand' ng-show="grid.columnDefs[9].visible" ng-click="sort(grid.columnDefs[9].displayName)">{{grid.columnDefs[9].displayName}}
							<span class="glyphicon glyphicon-triangle-top " ng-show="grid.columnDefs[9].order==1"></span>
							<span class="glyphicon glyphicon-triangle-bottom " ng-show="grid.columnDefs[9].order==2"></span>
						</div>
						<div class='cell cursor-hand' ng-show="grid.columnDefs[10].visible" ng-click="sort(grid.columnDefs[10].displayName)">{{grid.columnDefs[10].displayName}}
							<span class="glyphicon glyphicon-triangle-top " ng-show="grid.columnDefs[10].order==1"></span>
							<span class="glyphicon glyphicon-triangle-bottom " ng-show="grid.columnDefs[10].order==2"></span>
						</div>
						<div class='cell cursor-hand' ng-show="grid.columnDefs[11].visible" ng-click="sort(grid.columnDefs[11].displayName)">{{grid.columnDefs[11].displayName}}
							<span class="glyphicon glyphicon-triangle-top " ng-show="grid.columnDefs[11].order==1"></span>
							<span class="glyphicon glyphicon-triangle-bottom " ng-show="grid.columnDefs[11].order==2"></span>
						</div>
						
						
						<!-- action menu -->
						<div class='cell' style="width: 60px;"> ACTIONS </div>
					</div>
				</div>
			</div>

			<div id="data_grid" class="scroll-div ">
				<div class='table-div'>
						<div class='row-div' ng-repeat='data in grid.data'>
						<!-- Select All -->
						<div class='cell' style="width: 30px;"><input id="select_Txn" type="checkbox" ng-model="data.selected" ng-change="selectTransaction(data)"></div>
						<div class='cell' ng-show="grid.columnDefs[0].visible">{{data[grid.columnDefs[0].field]}}</div>
						<div class='cell' ng-show="grid.columnDefs[1].visible">{{data[grid.columnDefs[1].field]}}</div>
						<div class='cell' ng-show="grid.columnDefs[2].visible">{{data[grid.columnDefs[2].field]}}</div>
						<div class='cell' ng-show="grid.columnDefs[3].visible">{{data[grid.columnDefs[3].field]}}</div>
						<div class='cell' ng-show="grid.columnDefs[4].visible">{{data[grid.columnDefs[4].field]}}</div>
						<div class='cell' ng-show="grid.columnDefs[5].visible">{{data[grid.columnDefs[5].field]}}</div>
						<div class='cell' ng-show="grid.columnDefs[6].visible">{{data[grid.columnDefs[6].field]}}</div>
						<div class='cell' ng-show="grid.columnDefs[7].visible">{{data[grid.columnDefs[7].field]}}</div>
						<div class='cell' ng-show="grid.columnDefs[8].visible">{{data[grid.columnDefs[8].field]}}</div>
						<div class='cell' ng-show="grid.columnDefs[9].visible">{{data[grid.columnDefs[9].field]}}</div>
						<div class='cell' ng-show="grid.columnDefs[10].visible">{{data[grid.columnDefs[10].field]}}</div>
						<div class='cell' ng-show="grid.columnDefs[11].visible">{{data[grid.columnDefs[11].field]}}</div>
						
						<%-- action menu --%> 
						<div class='action-cell'  >
							<div class="collapse navbar-collapse"
								id="bs-example-navbar-collapse-1">
								<ul class="nav navbar-nav navbar-right">
									<li>
									<span id="action" style="cursor: pointer; padding: 10px;" data-toggle="dropdown" role="button" aria-expanded="false">
											<span class="glyphicon glyphicon-align-justify" style="top: 6px; margin-right: 30px;"></span>
									</span>
									<ul class="dropdown-menu" style="border: 1px solid orange;margin-top: -25px; margin-right: 40px;" role="menu">
										<li><a id="reports" style="cursor: pointer;" data-toggle="modal" data-target="#reportModal" ng-click="launchReportDialog(data, $index)">Reports</a></li>
									</ul>
									</li>
								</ul>
							</div>
						</div> 
						
						</div>
				</div>
			</div>


		</div>

			
	<!-- #rightPane -->
<!-- #splitterContainer -->
	<!-- ----------------------Headers buttons starts--------------------------- -->
	<br><br>
	<table class="rpt-paging-table">
	<tr>
	<td width="300px"></td>
	<td width="200px" ng-show="grid.pagingOptions.totalPages>0">Rows Per Page
		<select id="page_size" ng-change="pageSizeChanged()" 
			ng-model="grid.pagingOptions.pageSize" 
			ng-options="size as size for size in grid.pagingOptions.pageSizes"></select>
	</td>
	
	<td width="*" class="rpt-paging-table-nav">
		<span ng-show="grid.pagingOptions.totalPages>0">
		
		<span id="previous" class="glyphicon glyphicon-triangle-left cursor-hand" ng-show="grid.pagingOptions.totalPages>1" ng-click="setPrevious()"></span> &nbsp;
		
		<a id="first_page" href="#" 
		ng-class="{'rpt-selected-page':grid.pagingOptions.currentPage == 1}"
		ng-click="grid.pagingOptions.currentPage = 1; fetchPagedData();"> 1 </a>&nbsp;
		
		<a id="previous_pages" href="#" 
		ng-show="grid.pagingOptions.totalPages > 5" 
		ng-click="setPreviousPages()"> ... </a>&nbsp;
		
		<a id="page1" href="#" 
		ng-class="{'rpt-selected-page':grid.pagingOptions.currentPage == pages[0]}"
		ng-show="grid.pagingOptions.totalPages > pages[0] && pages[0] > 1" 
		ng-click="grid.pagingOptions.currentPage = pages[0]; fetchPagedData();"> {{pages[0]}} </a>&nbsp;
		
		<a id="page2" href="#" 
		ng-class="{'rpt-selected-page':grid.pagingOptions.currentPage == pages[1]}" 
		ng-show="grid.pagingOptions.totalPages > pages[1]  && pages[1] > 1" 
		ng-click="grid.pagingOptions.currentPage = pages[1]; fetchPagedData();"> {{pages[1]}} </a>&nbsp;
		
		<a id="page3" href="#" 
		ng-class="{'rpt-selected-page':grid.pagingOptions.currentPage == pages[2]}" 
		ng-show="grid.pagingOptions.totalPages > pages[2]  && pages[2] > 1" 
		ng-click="grid.pagingOptions.currentPage = pages[2]; fetchPagedData();"> {{pages[2]}} </a>&nbsp;
		
		<a id="next_pages" href="#" ng-show="grid.pagingOptions.totalPages > 5" 
		ng-click="setNextPages()" > ... </a>&nbsp;
		
		<a id="last_page" href="#" 
		ng-class="{'rpt-selected-page':grid.pagingOptions.currentPage == grid.pagingOptions.totalPages}"
		ng-show="grid.pagingOptions.totalPages > 1"
		ng-click="grid.pagingOptions.currentPage = grid.pagingOptions.totalPages; fetchPagedData();"> {{grid.pagingOptions.totalPages}} </a>&nbsp;
		
		<span id="next" class="glyphicon glyphicon-triangle-right cursor-hand" ng-show="grid.pagingOptions.totalPages>1" ng-click="setNext()"></span>
		
		</span>
	
	</td>
	
	
	
	</tr>
	<tr><td colspan="3" align="center"  ><div class="msg-box error-msg"  ng-bind-html="meassage" ></div> </td></tr>
	</table>		
	
</div>	
	<script src="<c:url value="/resources/lib/jquery/ui//jquery-ui-1.10.4.js" />"></script>
	<script src="<c:url value="/resources/lib/jquery/ui/jquery.ui.core.js" />"></script>
	<script src="<c:url value="/resources/lib/jquery/ui/jquery.ui.widget.js" />"></script>
	<script src="<c:url value="/resources/lib/jquery/ui/jquery.ui.mouse.js" />"></script>
	<script src="<c:url value="/resources/lib/jquery/ui/jquery.ui.slider.js" />"></script>
	<script src="<c:url value="/resources/lib/jquery/ui/jquery.ui.dialog.js" />"></script>
	<script src="<c:url value="/resources/lib/jquery/ui/jquery.ui.button.js" />"></script>
	<script src="<c:url value="/resources/lib/jquery/ui/jquery.ui.position.js" />"></script>
	<script src="<c:url value="/resources/lib/jquery/ui/jquery.ui.draggable.js" />"></script>
	<!-- splitter for the report screen -->
	<script src="<c:url value="/resources/lib/jquery/jquery.browser.min.js" />"></script>
	<script src="<c:url value="/resources/lib/splitter/splitter.js" />"></script>
	<script src="<c:url value="/resources/lib/angularJS/angular-idle.min.js" />"></script>
	<script src="<c:url value="/resources/lib/toastr/toastr.min.js" />"></script>
	
	

	<!-- bootsrtap JS -->
		<script src="<c:url value="/resources/lib/bootstrap/bootstrap-3.3.2-dist/js/bootstrap.min.js" />"></script>
	<!-- angularJS files -->
	
	<script src="<c:url value="/resources/lib/angularJS/angular-animate.js" />"></script>
	<script src="<c:url value="/resources/lib/angularJS/angular-ui-router.js" />"></script>
	<script src="<c:url value="/resources/lib/angularJS/angular-sanitize.js" />"></script>
	<script src="<c:url value="/resources/lib/ng-grid/ng-grid-2.0.7.debug.js" />"></script>
	<script src="<c:url value="/resources/lib/jsonExplorer/gd-ui-jsonexplorer.js" />"></script>

	<!-- DATE PICKER -->
	<script src="<c:url value="/resources/lib/momentjs/moment.js" />"></script>
	<script src="<c:url value="/resources/lib/datetimepicker-0.3.8-0/src/js/datetimepicker.js" />"></script>
	


	<!-- application related data starts -->
	<script src="<c:url value="/resources/js/tenant-portal/FreezeServices.js" />"></script>
	<script src="<c:url value="/resources/js/tenant-portal/HttpInterceptor.js" />"></script>
	<script src="<c:url value="/resources/js/dialog/umg-dialog.js" />"></script>
	<script src="<c:url value="/resources/js/dialog/dialogService.js" />"></script>
	
	
	<script src="<c:url value="/resources/js/tenant-portal/report/ReportService.js" />"></script>
	<script src="<c:url value="/resources/js/tenant-portal/report/ReportController.js" />"></script>
	
	<!-- application related data ends -->
	

	<!-- Angular config file, should be in the last, otherwise dependency problem may come -->
	<script src="<c:url value="/resources/js/tenant-portal/tnt-portal-app-service.js" />"></script>
	<script src="<c:url value="/resources/js/tenant-portal/tnt-portal-app.js" />"></script>
</body>
<!--------------------------------------------------------------------------------------------------->
</html>