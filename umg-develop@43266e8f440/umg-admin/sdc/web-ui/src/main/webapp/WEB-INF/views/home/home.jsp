<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html>
<html>
<head>
<style>
[ng\:cloak],[ng-cloak],.ng-cloak{display:none !important}

.main-header>.navbar {
margin-left:0px !important;
}
</style>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>REALAnalytics</title>
<link rel="shortcut icon" type="image/x-icon" href="resources/images/ra_icon_Mh6_icon.ico" />
<link rel="stylesheet" type="text/css" media="screen" href="<c:url value="/resources/css/umg-admin-main.css" />" />
<link href="<c:url value="/resources/css/admin_lte/font-awesome/css/font-awesome.min.css"/>" rel="stylesheet" type="text/css" />
<style>
    .v-center{display:table!important}
</style>
</head>
<body id="body" class="skin-blue sidebar-mini">
    <div data-ng-app="umg-admin-app" ng-controller="AdminCtrl" class="wrapper">
       <%--  <sec:authorize ifAllGranted="RA.sysParam.show,RA.trandashboard.show" var="isAdmin"></sec:authorize>
        <sec:authorize ifAllGranted="RA.trandashboard.show" var="isUser"></sec:authorize>
       <c:if test="${isAdmin}">
         <c:set var="role" value='{ "pages" : ["dashboard"]}' />
       </c:if>
        <c:if test="${isUser}">
         <c:set var="role" value='{ "pages" : ["dashboard","batchDashboard"]}' />
       </c:if>
       <input type="hidden" id="request" value='${role}' /> --%>
       
        <sec:authentication property="principal.authorities" var="authorities" />
        <c:set var="permission" value="" scope="application" />
        <c:forEach items="${authorities}" var="authority" varStatus="vs">
            <c:set var="permission">${permission},${authority.authority}</c:set>
        </c:forEach>
       <input type="hidden" id="permission" value='${permission}' />
       <%-- <input type="hidden" id="previleges" value='${PREVILEGES}' /> --%>
       <input type="hidden" id="actionacesslist" value='${ACTIONACESSLIST}' />
       <input type="hidden" id="pageaccesslist" value='${PAGEACCESSLIST}' />
       <input type="hidden" id="sysadmin" value='${IS_USER_SYSADMIN}' />
       <input type="hidden" id="tenantCode_current" value='${TENANT_CODE}' />
       <input type="hidden" id="is_notification_enabled" value='${IS_NOTIFICATION_ENABLED}' />
       <input type="hidden" id="tenant" value='${TENANT_LIST}' />
       
        <header class="main-header">
            <!-- <a  class="logo">
              <span class="logo-mini"><b> <img src="resources/images/logo/ocwen.jpg" class="tenant-logo-collapse" alt="User Image"></b></span>
              <span class="logo-lg">
              <div class="pull-left">
                <img src="resources/images/logo/ocwen.jpg" class="tenant-logo" alt="User Image">
              </div>
              <div class="pull-left" style="padding-left:16%">OCWEN</div>
                
                <img src="resources/images/logo/ocwen.jpg" class="img-circle" alt="User Image">
               </span>
            </a> -->
            
            <nav class="navbar navbar-static-top" id="nav_id" role="navigation" style="height:42px;">
                <a href="#" id="chart_home" class="sidebar-toggle" onclick="hide_div();" data-toggle="offcanvas" role="button">
                   <span class="sr-only">Toggle navigation</span>
                </a>
                <span style="padding-left:45%;color:white;font-weight: 550;font-family:monospace ;font-size: 30px;letter-spacing: 2px;">${fn:toUpperCase(TENANT_CODE)}</span>
                <div class="navbar-custom-menu">
                <ul class="nav navbar-nav">
                <li>
                    <li class="dropdown notifications-menu">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                          <i class="glyphicon glyphicon-user"></i>
                          <span class="label label-warning">${fn:length(TENANT_LIST)}</span>
                        </a>
                        <ul class="dropdown-menu">
                        <!--  <li class="header">You have 2 tenants</li>-->
                        <li>
                            <!-- inner menu: contains the actual data -->
                            <ul class="menu">
                              <c:forEach items="${TENANT_LIST}" var="tenant" varStatus="vs">
                                <li>
                                    <a ng-click="switchTenant('${tenant}','${TENANT_CODE}')">
                                    <c:if test="${tenant == TENANT_CODE}">
                                       <i class="fa fa-user text-green"></i>  ${fn:toUpperCase(tenant)}
                                    </c:if>
                                    <c:if test="${tenant != TENANT_CODE}">
                                       <i class="fa fa-user text-grey"></i>  ${fn:toUpperCase(tenant)}
                                    </c:if>
                                    </a>
                                </li>
                              </c:forEach>
                              
                             <%--  <li>
                                <a href="switchTenant/switch/ocwen">
                                  <i class="fa fa-user text-green"></i>  ${TENANT_CODE}
                                </a>
                              </li> --%>
                            </ul>
                        </li>
                        <!-- <li class="footer"><a href="#">View all</a></li> -->
                        </ul>
                    </li>
       
                     <!-- User Account: style can be found in dropdown.less -->
                    <li class="dropdown user user-menu">
                      <a href="#" class="dropdown-toggle" id="Logged_user_name" data-toggle="dropdown">
                          <img src="<c:url value="/resources/images/dp.jpg"/>" class="user-image" alt="User Image" />
                          <span class="hidden-xs"><sec:authentication property="principal.username" /></span>
                      </a>
                      <ul class="dropdown-menu">
                      <!-- User image -->
                      <li class="user-header">
                        <img src="<c:url value="/resources/images/dp.jpg"/>" class="img-circle" alt="User Image" />
                        <p><p>User : <sec:authentication property="principal.username" /></p></p>
                      </li>
                      <!-- Menu Footer-->
                      <li class="user-footer">
                        <div class="pull-left">
                          <c:if test="${IS_DB_AUTHENTICATION}"><a id="change_pass" href="changePassword" class="btn btn-primary btn-flat">Change Password</a></c:if>
                        </div>
                        <div class="pull-right">
                          <a href="#" id="logout" href="#" ng-click="change_clear('logout')" onclick='location.href="<c:url value="j_spring_security_logout" />"' class="btn btn-primary btn-flat">Sign out</a>
                        </div>
                      </li>
                </ul>
              </li>
              <!-- Control Sidebar Toggle Button -->
              <li>
                <a id="hm_cllpse_sdbar" ng-click="collapse_sidebar()" href="#" data-toggle="control-sidebar"><i class="glyphicon glyphicon-list"></i></a>
              </li>
            </ul>
          </div>
          </nav>
        </header>
        <aside class="main-sidebar"> <!-- style="min-height:50% !important;"> -->
        <!-- sidebar: style can be found in sidebar.less -->
        <section class="sidebar sidebar-scroll">
          <!-- Sidebar user panel -->
          <!-- sidebar menu: : style can be found in sidebar.less -->
          <!-- left modification-->
          <ul id="hm_lft_sdbar_menu" class="sidebar-menu">
            <li class="treeview">
               <a id="Home" ui-sref="/home" ng-click="change_clear('Home')">
                <!--   <i class="glyphicon glyphicon-stats"></i>-->
                 <img src="resources/images/summary-icon.png"/>
                <span>Summary</span> <!-- <i class="fa fa-angle-left pull-right"></i> -->
              </a>
            </li>
            <li class="treeview">
              <a href="#" id="hm_lft_sdbar_Models">
                <!--<i class="fa fa-cog"></i> -->
                 <img src="resources/images/models-icon.png"/>
                <span id="hm_lft_sdbar_span_Models">Models</span>
                <span class="fa fa-angle-left pull-right"></span>
              </a>
              <ul class="treeview-menu submenuPadding">
                <li id="modelPublish"><a id="hm_lp_smp" ui-sref="modelPublish" ng-click="clicked_tree('hm_lp_smp')"><i class=""></i> Add</a></li>
                <!-- <li><a id="hm_lp_impver" ui-sref="importVersion" ng-click="clicked_tree('hm_lp_impver')"><i class=""></i> Import</a></li> -->
                <li id="umgVersionView"><a id="hm_lp_vview" ui-sref="umgVersionView" ng-click="clicked_tree('hm_lp_vview');"><i class=""></i> Manage</a></li>
                <!-- <li><a id="hm_lp_rlibUpLd" ui-sref="rlibUpload" ng-click="clicked_tree('hm_lp_rlibUpLd');"><i class=""></i>Upload R Model</a></li> -->
              </ul>
            </li>
             <li class="treeview">
              <a href="#" id="hm_lft_sdbar_LookupData">
                <!--  i class="glyphicon glyphicon-book"></i>-->
                <img src="resources/images/lookup-icon.png"/>
                <span id="hm_lft_sdbar_span_LookupData">Lookup Data</span>
                <span class="fa fa-angle-left pull-right"></span>
              </a>
              <ul class="treeview-menu submenuPadding">
                <li id="syndicateDataCrud"><a id="hm_lp_synDC" ui-sref="syndicateDataCrud" ng-click="clicked_tree('hm_lp_synDC');"><i class=""></i> Add</a></li>
                <li id="modelAssumptionList"><a id="hm_lp_mdAsLst" ui-sref="modelAssumptionList" ng-click="clicked_tree('hm_lp_mdAsLst');"><i class=""></i> Manage</a></li>
              </ul>
            </li>
            <!-- <li class="treeview">
               <a href="" id="hm_lp_TD"  ui-sref="dashboard" ng-click="clicked('hm_lp_TD')">
                <i class="fa fa-dashboard"></i><span>Transaction Dashboard</span> <i class="fa fa-angle-left pull-right"></i>
              </a>
            </li> -->
            
            <!-- <li class="treeview">
              <a href="#">
                <i class="glyphicon glyphicon-th-list"></i>
                <span>Batch Execution</span>
                <span class="fa fa-angle-left pull-right"></span>
              </a>
              <ul class="treeview-menu">
                <li><a id="hm_lp_FUpld" href="#" ng-click="clicked_tree('hm_lp_FUpld');"><i class="fa fa-circle-o"></i> File upload</a></li>
                <li><a id="hm_lp_Dbrd" href="#" ng-click="clicked_tree('hm_lp_Dbrd');"><i class="fa fa-circle-o"></i> Dashboard</a></li>
             </ul>
            </li> -->
            <!-- <li class="treeview">
              <a id="hm_lp_HDbrd" ui-sref="batchDashboard" ng-click="clicked('hm_lp_HDbrd');">
                <i class="fa fa-dashboard"></i><span>Batch Dashboard</span> <i class="fa fa-angle-left pull-right"></i>
              </a>
            </li> -->
            
            <li class="treeview">
              <a href="#">
              <img id="hm_lft_sdbar_icon_Dashboard" src="resources/images/dashboard-icon.png"/>
                <span id="hm_lft_sdbar_span_Dashboard">Dashboard</span>
                <span class="fa fa-angle-left pull-right"></span>
              </a>
              <ul class="treeview-menu submenuPadding">
                <li id="dashboard"> <!-- ng-if='(requestData.pages.indexOf("dashboard")==-1)' --><a href="" id="hm_lp_TD" ui-sref="dashboard" ui-sref-opts="{reload:true}" ng-click="clicked_tree('hm_lp_TD')"><i class=""></i><span id="hm_lft_sdbar_span_Transaction">Transaction</span></a></li>
                <li id="batchDashboard"><!--  ng-if='(requestData.pages.indexOf("batchDashboard")==-1)'> --><a id="hm_lp_HDbrd" ui-sref="batchDashboard" ui-sref-opts="{reload:true}" ng-click="clicked_tree('hm_lp_HDbrd');"><i class=""></i><span id="hm_lft_sdbar_span_Batch">Batch/Bulk</span></a></li>
              </ul>
            </li>
            
            
            <!-- <li class="treeview">
              <a id="hm_db_dashboard" ui-sref="dashboard" ng-click="clicked('hm_db_dashboard')">
                <i class="fa fa-dashboard"></i><span>Dashboard</span>
              </a>
            </li> -->
            
            
            <li class="treeview">
              <a href="#">
                  <!--   <i class="glyphicon glyphicon-hdd"></i>-->
                <img src="resources/images/support_libraries-icon.png"/>
                <span id="hm_lft_sdbar_span_SupportLibraries">Support Libraries</span>
                <span class="fa fa-angle-left pull-right"></span>
              </a>
              <ul class="treeview-menu submenuPadding">
                <li id="addPackage"><a id="hm_lp_addPckg" ui-sref="addPackage" ng-click="clicked_tree('hm_lp_addPckg');"><i class=""></i>Add</a></li>
                <li id="listPackages"><a id="hm_lp_lstPckg" ui-sref="listPackages" ng-click="clicked_tree('hm_lp_lstPckg');"><i class=""></i>Manage</a></li>
              </ul>
            </li>
            
            <li class="treeview">
              <a href="#">
                 <i class="fa fa-info-circle"></i>
                <span id="hm_lft_sdbar_span_Notification">Notification</span>
                <span class="fa fa-angle-left pull-right"></span>
              </a>
              <ul class="treeview-menu submenuPadding">
                <li id="notificationAdd" ng-class="${IS_NOTIFICATION_ENABLED}==true ? '':'disable-html'"><a id="hm_lp_notificationAdd" ui-sref="notificationAdd" ng-click="clicked_tree('hm_lp_notificationAdd');"><i class=""></i>Add</a></li>
                <li id="notificationManage" ng-class="${IS_NOTIFICATION_ENABLED}==true ? '':'disable-html'"><a id="hm_lp_notificationManage" ui-sref="notificationManage" ng-click="clicked_tree('hm_lp_notificationManage');"><i class=""></i>Manage</a></li>
              </ul>
            </li>
            
             <li class="treeview">
              <a id="hm_lp_Reports" ng-click="clicked('')"> <!-- Removed ui-sref because it contains no refrnce and was giving error in console -->
                <i class="glyphicon glyphicon-th-list"></i><span id="hm_lft_sdbar_span_Reports">Reports</span> <!-- <i class="fa fa-angle-left pull-right"></i> -->
              </a>
            </li>
            <li id="superAdminPanel_Tenant" class="treeview">
              <a href="#" id="hm_lp_TConfig" >
                <i class="glyphicon glyphicon-user"></i><span id="hm_lft_sdbar_span_Tenant_mgmt">Tenant </span><span class="fa fa-angle-left pull-right"></span>
              </a>
               <ul class="treeview-menu submenuPadding">
                <li id="addTenant"><a id="hm_lp_tenantAdd" ui-sref="addTenant" ng-click="clicked_tree('hm_lp_tenantAdd')" ><i class=""></i> Add</a></li>
                <li id="manageTenant"><a id="hm_lp_tenantManage" ui-sref="manageTenant" ng-click="clicked_tree('hm_lp_tenantManage');"><i class=""></i> Manage</a></li>
              </ul>
            </li>
            <li id="superAdminPanel_roles_privileges" class="treeview">
              <a href="#">
                 <i class="fa fa-shield"></i>
                <span id="hm_lft_sdbar_span_Roles_Privileges">Roles & Privileges</span>
                <span class="fa fa-angle-left pull-right"></span>
              </a>
              <ul class="treeview-menu submenuPadding">
                <!-- <li id="addRolesPrivileges"><a id="hm_lp_addRolesPrivileges" ui-sref="#" ng-click="clicked_tree('hm_lp_addRolesPrivileges');"><i class=""></i>Add</a></li> -->
                <li id="editRolesPrivileges"><a id="hm_lp_editRolesPrivileges" ui-sref="privilegeMapping" ng-click="clicked_tree('hm_lp_editRolesPrivileges');"><i class=""></i>Edit Mapping</a></li>
              </ul>
            </li>
             <li id="superAdminPanel_ModeletPooling" class="treeview">
              <a id="hm_lp_ModeletPooling" ng-click="clicked('hm_lp_ModeletPooling')" ui-sref="modeletPooling"> <!-- Removed ui-sref because it contains no refrnce and was giving error in console -->
                <i class="glyphicon glyphicon-random"></i><span id="hm_lft_sdbar_span_ModeletPooling">Modelet Pooling</span> <!-- <i class="fa fa-angle-left pull-right"></i> -->
              </a>
            </li>
            <li id="superAdminPanel_ModeletProcessing" class="treeview">
              <a id="hm_lp_ModeletProcessing" ng-click="clicked('hm_lp_ModeletProcessing')" ui-sref="runningProcess"> <!-- Removed ui-sref because it contains no refrnce and was giving error in console -->
                <i class="glyphicon glyphicon-paperclip"></i><span id="hm_lft_sdbar_span_ModeletProcessing">Modelet Process</span> <!-- <i class="fa fa-angle-left pull-right"></i> -->
              </a>
            </li>
             <li  id="superAdminPanel_ModeletProfiling" class="treeview">
              <a href="#">
                <img src="resources/images/support_libraries-icon.png"/>
                <span id="hm_lft_sdbar_span_Modelet_Profiling">Modelet Profiling</span>
                <span class="fa fa-angle-left pull-right"></span>
              </a>               
                <ul class="treeview-menu submenuPadding">
                <li id="manageProfile"><a id="hm_lp_manageProfile" ui-sref="manageProfile" ng-click="clicked_tree('hm_lp_manageProfile');"><i class=""></i>Manage</a></li>
                <li id="assignProfile"><a id="hm_lp_assignProfile" ui-sref="assignProfile" ng-click="clicked_tree('hm_lp_assignProfile');"><i class=""></i>Assign Profile</a></li>
               <!--  <li id="editRolesPrivileges"><a id="hm_lp_editRolesPrivileges" ui-sref="privilegeMapping" ng-click="clicked_tree('hm_lp_editRolesPrivileges');"><i class=""></i>Edit Mapping</a></li> -->
              </ul>
            </li>
             <li id="superAdminPanel_Sysparam" class="treeview">
              <a id="hm_lp_SystemParameters" ng-click="clicked('hm_lp_SystemParameters')" ui-sref="sysParam"> <!-- Removed ui-sref because it contains no refrnce and was giving error in console -->
                <i class="glyphicon glyphicon-wrench"></i><span id="hm_lft_sdbar_span_SystemParameters">System Parameters</span> <!-- <i class="fa fa-angle-left pull-right"></i> -->
              </a>
            </li>
             <li id="superAdminPanel_HazelCast" class="treeview">
              <a id="hm_lp_HazelCastStatus" ng-click="clicked('hm_lp_HazelCastStatus')" ui-sref="hazelCastStatus"> <!-- Removed ui-sref because it contains no refrnce and was giving error in console -->
                <i class="glyphicon glyphicon-signal"></i><span id="hm_lft_sdbar_span_HazelCastStatus">Cache Details</span> <!-- <i class="fa fa-angle-left pull-right"></i> -->
              </a>
            </li>
            <!-- just check for scroll -->
            <!-- end of test -->
            
          </ul>
        </section>
        <!-- /.sidebar -->
        <div id="left-footer" style="border-left: 30px solid transparent;position: absolute; bottom: 0;display:none;"><img src="resources/images/PoweredBy.png"/><!-- <span style="color: #b8c7ce;font-size:14px">Powered by Altisource</span> --></div>
       </aside>
        <!-- Content Wrapper. Contains page content -->
      <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header"  ng-cloak>
          <h1>{{$state.current.pageTitle}}</h1>
          <ol class="breadcrumb">
            <!-- <li class="active">Home</li> -->
          </ol>
        </section>
        
        <!-- Main content -->
        <section class="content">
                <div class="row">
                    <!-- Partial Page Viewer -->
                
                    <div class="col-lg-12" ui-view="mainContainer" style="min-height: 502px;" ng-cloak></div>
                    
                    <!-- Help Desk -->
                    
                    <div id="video_draggable" class="box box-primary"
                        style="border-right: solid 4px white; border-left: solid 4px white; padding: auto; display: none; position: fixed; right: 0px; bottom: 0px; width: 25%;">
                        <div class="box-header with-border">
                            <h3 class="box-title">Help Viewer</h3>
                            <div class="box-tools pull-right">
                                <!-- <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button> -->
                                <button id="hm_rst" ng-click="reset_video()" class="btn btn-box-tool"
                                    >
                                    <i class="fa fa-times"></i>
                                </button>
                            </div>
                        </div>
                        <!-- /.box-header -->
                        <div class="box-body no-padding">
                            <div class="embed-responsive embed-responsive-16by9">
                                <iframe id="help_video" class="embed-responsive-item" src=""
                                    frameborder="0" allowfullscreen=""></iframe>
                            </div>
                        </div>
                        <!-- /.box-body -->
                        <div class="box-footer text-center">
                            <p id="video_details" class="form contol"></p>
                        </div>
                        <!-- /.box-footer -->
                    </div>
                </div>
                
                <!-- Freez Screen -->
                
                <div id="loaderDiv" loader>
                    <img src="resources/images/loader.gif" class="ajax-loader" />
                </div>
            <br>
        </section><!-- /.content -->
      </div><!-- /.content-wrapper -->
      
      <!-- Sticky Footer -->
      
      <footer class="main-footer" style="margin-right:0px !important;">
        <div class="col-lg-12">
        <div class="col-lg-4">
        <!-- <strong>Powered by </strong> <img width="42" height="42" src="resources/images/logo/Altisource_Logo.png"/> -->
        <span style="font-style: inherit;font-weight: 600;color: #B8C7CE;font-size: 14px;" class="pull-left">RA-<c:out value="${sysEnv['umg-env']}" default="Dev" />,v<c:out value="${sysEnv['umg-ver']}" default="1.0"/></span>
        </div>
        <div class="col-lg-4"><%-- <strong>RA-<c:out value="${sysEnv['umg-env']}" default="Dev" />,v<c:out value="${sysEnv['umg-ver']}" default="1.0"/></strong> --%></div>
        <div class="col-lg-3"><span style="font-family: inherit;font-weight: 600;padding-right:12%;color: #B8C7CE;font-size: 14px;" class="pull-right" current-time></span></div>
        <!-- <div class="col-lg-1" style="padding-right: 6px !important;"><span class="pull-right" style="color: #222D32;"><strong>REAL</strong><span style="font-weight:500">Analytics</span><sup><small>TM</small></sup></span></div> -->
        </div>
      </footer>
      
      <!-- Video Player -->
      
      <aside id="right-sidebar" class="control-sidebar control-sidebar-dark">
        <!-- Create the tabs -->
        <!-- Tab panes -->
        <div class="tab-content">
        <div class="tab-pane active" id="control-sidebar-home-tab">
            <h3 class="control-sidebar-heading">Video List</h3>
            <ul class="control-sidebar-menu">
              <li>
                <a id="hm_ch_vdo1" href="" ng-click="change_video('https://www.youtube.com/embed/fEH63nzsHZY','video1')">
                  <i class="menu-icon fa fa-video-camera bg-red"></i>
                  <div class="menu-info">
                    <h4 class="control-sidebar-subheading">REALAnalytics</h4>
                    <p>Login Process</p>
                  </div>
                </a>
              </li>
              <li>
                <a id="hm_ch_vdo2" href="" ng-click="change_video('https://www.youtube.com/embed/m1_EjLkgu_U','video2')">
                  <i class="menu-icon fa fa-video-camera bg-yellow"></i>
                  <div class="menu-info">
                    <h4 class="control-sidebar-subheading">REALAnalytics</h4>
                    <p>Landing Page</p>
                  </div>
                </a>
              </li>
              <li>
                <a id="hm_ch_vdo3" href="" ng-click="change_video('https://www.youtube.com/embed/R-2kkqENnuk','video3')">
                  <i class="menu-icon fa fa-video-camera bg-light-blue"></i>
                  <div class="menu-info">
                    <h4 class="control-sidebar-subheading">Model Management</h4>
                    <p>Create</p>
                  </div>
                </a>
              </li>
              <li>
                <a id="hm_ch_vdo4" href="" ng-click="change_video('https://www.youtube.com/embed/L9GidY6bwQY','video4')">
                  <i class="menu-icon fa fa-video-camera bg-green"></i>
                  <div class="menu-info">
                    <h4 class="control-sidebar-subheading">Model Management </h4>
                    <p>Import</p>
                  </div>
                </a>
              </li>
                <li>
                <a id="hm_ch_vdo5" href="" ng-click="change_video('https://www.youtube.com/embed/L9GidY6bwQY','video4')">
                  <i class="menu-icon fa fa-video-camera bg-green"></i>
                  <div class="menu-info">
                    <h4 class="control-sidebar-subheading">Model Management </h4>
                    <p>Listing</p>
                  </div>
                </a>
              </li>
               <li>
                <a id="hm_ch_vdo6" href="" ng-click="change_video('https://www.youtube.com/embed/L9GidY6bwQY','video4')">
                  <i class="menu-icon fa fa-video-camera bg-green"></i>
                  <div class="menu-info">
                    <h4 class="control-sidebar-subheading">Model Management </h4>
                    <p>Uploading Of R Models</p>
                  </div>
                </a>
              </li>
              <li>
                <a id="hm_ch_vdo7" href="" ng-click="change_video('https://www.youtube.com/embed/L9GidY6bwQY','video4')">
                  <i class="menu-icon fa fa-video-camera bg-green"></i>
                  <div class="menu-info">
                    <h4 class="control-sidebar-subheading">Model Assumptions</h4>
                    <p>Create</p>
                  </div>
                </a>
              </li>
                <li>
                <a id="hm_ch_vdo8" href="" ng-click="change_video('https://www.youtube.com/embed/L9GidY6bwQY','video4')">
                  <i class="menu-icon fa fa-video-camera bg-green"></i>
                  <div class="menu-info">
                    <h4 class="control-sidebar-subheading">Model Assumptions</h4>
                    <p>List</p>
                  </div>
                </a>
              </li>
               <!-- <li>
                <a id="hm_ch_vdo9" href="" ng-click="change_video('https://www.youtube.com/embed/L9GidY6bwQY','video4')">
                  <i class="menu-icon fa fa-video-camera bg-green"></i>
                  <div class="menu-info">
                    <h4 class="control-sidebar-subheading">Tenant Management</h4>
                    <p></p>
                  </div>
                </a>
              </li>
                 <li>
                <a id="hm_ch_vdo10" href="" ng-click="change_video('https://www.youtube.com/embed/L9GidY6bwQY','video4')">
                  <i class="menu-icon fa fa-video-camera bg-green"></i>
                  <div class="menu-info">
                    <h4 class="control-sidebar-subheading">Transaction Dashboard</h4>
                    <p></p>
                  </div>
                </a>
              </li>
               <li>
                <a id="hm_ch_vdo11" href="" ng-click="change_video('https://www.youtube.com/embed/L9GidY6bwQY','video4')">
                  <i class="menu-icon fa fa-video-camera bg-green"></i>
                  <div class="menu-info">
                    <h4 class="control-sidebar-subheading">Batch Dashboard</h4>
                    <p></p>
                  </div>
                </a>
              </li>
                <li>
                <a id="hm_ch_vdo12" href="" ng-click="change_video('https://www.youtube.com/embed/L9GidY6bwQY','video4')">
                  <i class="menu-icon fa fa-video-camera bg-green"></i>
                  <div class="menu-info">
                    <h4 class="control-sidebar-subheading">Listing Model Support Packages</h4>
                    <p></p>
                  </div>
                </a>
              </li>
               <li>
                <a id="hm_ch_vdo13" href="" ng-click="change_video('https://www.youtube.com/embed/L9GidY6bwQY','video4')">
                  <i class="menu-icon fa fa-video-camera bg-green"></i>
                  <div class="menu-info">
                    <h4 class="control-sidebar-subheading">Adding Model Support Package</h4>
                    <p></p>
                  </div>
                </a>
              </li> -->
            </ul><!-- /.control-sidebar-menu -->
          </div>
        </div>
      </aside>
      <div class="control-sidebar-bg" style="position: fixed; height: auto;"></div>
        
    </div>
    
    <!-- Other Libraries -->
    
    <script src="resources/lib/jquery/jQuery-2.1.4.min.js"></script>
    <script src="resources/lib/jquery/jquery-ui.min.js"></script>
    <script src="resources/lib/admin_lte/bootstrap/bootstrap.min.js"></script>
 <!--    <script src="resources/lib/admin_lte/plugins/slimScroll/jquery.slimscroll.min.js"></script> -->
    <script src="resources/lib/admin_lte/plugins/fastclick/fastclick.min.js"></script>
    <script type="text/javascript" src="resources/lib/charts/jsapi.js"></script>
    <script type="text/javascript">google.load('visualization', '1.0', {'packages':['corechart']});</script>
    
    <!-- Admin LTE Main JS -->
    
    <script src="resources/lib/admin_lte/app.min.js"></script>
    
    <!-- Angular Libraries -->
    
    <script src="resources/lib/angularJS/angular.min.js"></script>
    <script src="resources/lib/angularJS/angular-ui-router.js"> </script>
    <script src="resources/lib/angularJS/angular-sanitize.js"></script>
    <script src="resources/lib/angularJS/angular-idle.min.js"></script>
    <script src="resources/lib/ng-grid/ng-grid.debug-2.0.11.js"></script>
    <script src="resources/lib/tree-grid/tree-grid-directive.js"></script>
    <script src="resources/lib/charts/d3.min.js"></script>
    <script src="resources/lib/charts/angular-charts.min.js"></script>
    <script src="resources/lib/charts/ng-google-chart.min.js"></script>
    <script src="resources/lib/angularGrid/angular-grid.min.js"></script>
    <script src="resources/lib/angularJS/angular-dragdrop.min.js"></script>
    <script src="resources/lib/angularJS/FileSaver.min.js"></script>
    
    <script src="resources/lib/admin_lte/bootstrap/ui-bootstrap-tpls-0.6.0.js"></script>
    
    <script src="resources/lib/momentjs/moment-with-locales.js"></script>
    <script src="resources/lib/momentjs/moment-timezone.js"></script>
    <script src="resources/lib/admin_lte/bootstrap/bootstrap-datetimepicker.js"></script>
    <script src="resources/lib/daterangepicker/daterangepicker.js"></script>
    <script src="resources/lib/daterangepicker/ng-bs-daterangepicker.js"></script>
        
    <script src="resources/lib/pickList/lodash.legacy.min.js"></script>
    <script src="resources/lib/pickList/angular-picklist.min.js"></script>
    
    <!-- UMG Admin related JS -->
    <script src="resources/lib/dialog/dialogs.min.js"></script>
    <script src="resources/lib/toastr/toastr.min.js"></script>
    <script src="resources/lib/pickList/component.js"></script>
    <script src="resources/js/util/SharedPropertiesService.js"></script>
    <script src="resources/lib/jsonExplorer/gd-ui-jsonexplorer.js"></script>
    
    <script src="resources/js/umg-version/UmgVersionService.js"></script>
    <script src="resources/js/mapping/TidListDisplayService.js"></script>
    <script src="resources/js/testbed/TestBedService.js"></script>
    <script src="resources/js/smp/ModelPublishingService.js"></script>
    <script src="resources/js/model-publish/ModelPublishService.js"></script>
    <script src="resources/js/sys-param/SystemParameterService.js"></script>
    <script src="resources/js/query/QueryEditorService.js"></script>
    <script src="resources/js/package/PackageService.js"></script>
    <script src="resources/js/model/SyndicateDataService.js"></script>
    <script src="resources/js/tenant/TenantService.js"></script>
    <script src="resources/js/plugins/PluginService.js"></script>
    <script src="resources/js/mapping/AddTidService.js"></script>
    <script src="resources/js/batch-dashboard/BatchDashBoardService.js"></script>
    <script src="resources/js/dashboard/DashBoardService.js"></script>
    <script src="resources/js/admin/RAService.js"></script>
    <script src="resources/js/pool/ModeletPoolingService.js"></script>
    <script src="resources/js/notification/ModelApprovalService.js"></script>
    <script src="resources/js/notification/NotificationAddEditService.js"></script>
    <script src="resources/js/notification/NotificationManageService.js"></script>
    <script src="resources/js/privilegeMapping/privilegeMappingService.js"></script>
    
    <script src="resources/js/admin/AdminCtrl.js"></script>
    <script src="resources/js/admin/RACtrl.js"></script>
    <script src="resources/js/umg-version/VersionListCtrl.js"></script>
    <script src="resources/js/umg-version/VersionImportWizardCtrl.js"></script>
    <script src="resources/js/testbed/TestBedControllerNew.js"></script>
    <script src="resources/js/smp/ModelPublishingCtrl.js"></script>
    <script src="resources/js/model-publish/ModelPublishCtrl.js"></script>
    <script src="resources/js/sys-param/SystemParameterCtrl.js"></script>
    <script src="resources/js/query/QueryViewController.js"></script>
    <script src="resources/js/package/PackageUploadCtrl.js"></script>
    <script src="resources/js/package/PackageViewCtrl.js"></script>
    <script src="resources/js/model/SyndicateCRUDController.js"></script>
    <script src="resources/js/tenant/TenantController.js"></script>
    <script src="resources/js/tenant/AddTenantController.js"></script>
    <script src="resources/js/tenant/TenantsListCtrl.js"></script>
    <script src="resources/js/query/QueryEditorCtrl.js"></script>
    <script src="resources/js/model/ModelAssumptionListCtrl.js"></script>
    <script src="resources/js/plugins/PluginController.js"></script>
    <script src="resources/js/mapping/AddTidController.js"></script>
    <script src="resources/js/umg-version/VersionMetricsCtrl.js"></script>
    <script src="resources/js/batch-dashboard/BatchDashboardCtrl.js"></script>
    <script src="resources/js/dashboard/DashBoardController.js"></script>
    <script src="resources/js/pool/ModeletPoolingCtrl.js"></script>
    <script src="resources/js/notification/ModelApprovalCtrl.js"></script>
    <script src="resources/js/notification/NotificationAddEditCtrl.js"></script>
    <script src="resources/js/notification/NotificationManageCtrl.js"></script>
    <script src="resources/js/tenant/AuthTokenCtrl.js"></script>
    <script src="resources/js/privilegeMapping/privilegeMappingManageCtrl.js"></script>
    
    <script src="resources/js/cacheDetails/CacheDetailsService.js"></script>
    <script src="resources/js/cacheDetails/CacheDetailsCtrl.js"></script> 
    
    <script src="resources/js/modelet-profiling/ModeletProfilingCtrl.js"></script>
    <script src="resources/js/modelet-profiling/ModeletProfilingService.js"></script>
    
    <script src="resources/js/modelet-assign/AssignModeletCtrl.js"></script>
    <script src="resources/js/modelet-assign/AssignModeletService.js"></script>
    <script src="resources/js/process/ModeletProcessService.js"></script>
    <script src="resources/js/process/ModeletProcessCtrl.js"></script>
    
    <script src="resources/js/app/umg-admin-services.js"></script>
    <script src="resources/js/app/umg-admin-directives.js"></script>
    <script src="resources/js/app/umg-admin-filters.js"></script>
    <script src="resources/js/app/HttpInterceptor.js"></script>
    <script src="resources/js/app/umg-admin-app.js"></script>
    <script src="resources/js/app/permissions.js"></script>
    
  
    <!-- home page related js -->
     <script>
	     function noiFrame() {
	    	 try {
		    	 if (window.top !== window.self) {
			    	 document.write = "";
			    	 window.top.location = window.self.location;
			    	 setTimeout(function() {
			    		   document.body.innerHTML = '';
			    	 }, 0);
			    	 window.self.onload = function() {
			    		   document.body.innerHTML = '';
			    	 };
		    	 }
	    	 }catch (err) {
	    	 }
	    }
	 noiFrame();
     /**diasbale backspace for page traversal**/
     $(document).unbind('keydown').bind('keydown', function (event) {
         var doPrevent = false;
         if (event.keyCode === 8) {
             var d = event.srcElement || event.target;
             if ((d.tagName.toUpperCase() === 'INPUT' && 
                  (
                      d.type.toUpperCase() === 'TEXT' ||
                      d.type.toUpperCase() === 'PASSWORD' || 
                      d.type.toUpperCase() === 'FILE' || 
                      d.type.toUpperCase() === 'EMAIL' || 
                      d.type.toUpperCase() === 'SEARCH' || 
                      d.type.toUpperCase() === 'DATE' )
                  ) || 
                  d.tagName.toUpperCase() === 'TEXTAREA') {
                 doPrevent = d.readOnly || d.disabled;
             }
             else {
                 doPrevent = true;
             }
         }

         if (doPrevent) {
             event.preventDefault();
         }
     });
     function hide_div()
     {
         if($("#body").hasClass('sidebar-collapse'))
             document.getElementById("left-footer").style.display = "block";             
        else
            document.getElementById("left-footer").style.display = "none";
     };
     //disable refresh
     function disableF5(e) { if ((e.which || e.keyCode) == 116) e.preventDefault(); };
     $(document).on("keydown", disableF5);
     
     //onload actions
     $(window).load(function(){
      localStorage.setItem('tenant', $('#tenantCode_current').val());
      var val=localStorage.getItem('breadcrumb');
      $("#body").addClass('skin-blue fixed sidebar-mini');
      if($("#body").hasClass('skin-blue sidebar-mini fixed'))
        document.getElementById("left-footer").style.display = "none";
        //alert("hi");
      
      if($(location).attr('href').indexOf("#/home") > -1) {
                $('.breadcrumb').html("");
                localStorage.setItem('breadcrumb', $('.breadcrumb').html());
         }else if(val!=null){ 
                $('.breadcrumb').html("");
                $('.breadcrumb').append(localStorage.getItem('breadcrumb'));
         }
      
     permissionMappingTab();
    });
    /*  window.addEventListener('focus', function() {
           window.location.reload(true);
        }); */
        
        $(window).bind('storage', function (e) {
            if($('#tenantCode_current').val() != e.originalEvent.newValue && e.originalEvent.key == 'tenant'){
                window.location.href="";
            }
         });
     $.widget.bridge('uibutton', $.ui.button);
     </script>
</body>
</html>
