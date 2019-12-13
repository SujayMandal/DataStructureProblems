'use strict';

var adminApp = angular.module('umg-admin-app', ['umg-admin.services', 'umg-admin.directives', 'umg-admin.filters', 'umg-admin.httpInterceptor', 'ui.router', 'ui.bootstrap', 'dialogs', 'ngSanitize', 'angularCharts', 'googlechart', 'ngGrid', 'angularGrid', 'gd.ui.jsonexplorer', 'ngIdle', 'treeGrid', 'ngBootstrap', 'ngDragDrop', 'apicklist']);

var permission = $('#permission').val().trim();
permission = permission.substring(1);
var pages = permission.split(",");
var pageMap = {};
var buttons = {};
var staticPageList = {};
var staticActionList = {};
var sysAdmin = false;
var staticPermissionArray = {};
var currentStateObj = null;
var notificationCRUDFlag = $('#is_notification_enabled').val().trim();

if ($('#sysadmin').val() != "" && $('#sysadmin').val() != null && $('#sysadmin').val() != undefined) {
	sysAdmin = $('#sysadmin').val() == "true" ? true : false;
}
if ($('#pageaccesslist').val() != "" && $('#pageaccesslist').val() != null && $('#pageaccesslist').val() != undefined) {
	staticPageList = JSON.parse($('#pageaccesslist').val());
}
if ($('#actionacesslist').val() != "" && $('#actionacesslist').val() != null && $('#actionacesslist').val() != undefined) {
	staticActionList = JSON.parse($('#actionacesslist').val());
}
for (k in staticPageList) {
	pageMap[staticPageList[k].permission] = staticPageList[k].uiElementId;
	staticPermissionArray[staticPageList[k].permission] = staticPageList[k].uiElementId;
}
for (k in staticActionList) {
	buttons[staticActionList[k].permission] = staticActionList[k].uiElementId;
	staticPermissionArray[staticActionList[k].permission] = staticActionList[k].uiElementId;
}
var pageArray = $.map(pageMap, function (value, index) {
	return [value];
});
var buttonArray = $.map(buttons, function (value, index) {
	return [value];
});
pageArray.push("modeletPooling", "hazelCastStatus", "sysParam", "addTenant", "manageTenant", "privilegeMapping");
console.log("page array : ");
console.log(pageArray);
var finalPages = [];
var k = 0;
var j;

for (var i = 0; i < pages.length; i++) {
	if (pageMap[pages[i]] != undefined && pageMap[pages[i]] != null) {
		finalPages[k++] = pageMap[pages[i]];
	}
}

if (sysAdmin == true) {
	finalPages.push("modeletPooling", "hazelCastStatus", "sysParam", "addTenant", "manageTenant", "privilegeMapping");
}

console.log("FinalPages : ");
console.log(finalPages);

var states = [];
adminApp.config(['KeepaliveProvider', 'IdleProvider', function (KeepaliveProvider, IdleProvider) {
	IdleProvider.idle(1740);
	IdleProvider.timeout(60);
	KeepaliveProvider.interval(10);
}]);

adminApp.run(['$rootScope', 'Idle', '$log', 'Keepalive', '$window', '$document', function ($rootScope, Idle, $log, Keepalive, $window, $document) {
	Idle.watch();
	$log.debug('umg admin started at : ' + new Date());
	var timedOut = false;

	toastr.options = {
		"closeButton": false,
		"debug": false,
		"newestOnTop": true,
		"progressBar": true,
		"positionClass": "toast-bottom-full-width",
		"preventDuplicates": false,
		"onclick": function () {
			$window.location.href = 'j_spring_security_logout';
		},
		"showDuration": "300",
		"hideDuration": "1000",
		"timeOut": "0",
		"extendedTimeOut": "0",
		"showEasing": "swing",
		"hideEasing": "swing",
		"showMethod": "slideDown",
		"hideMethod": "slideUp"
	};

	$rootScope.$on('IdleStart', function () {
		$log.warn('Idle Timeout Started ...');
		toastr.info('Your session will expire in few second(s).', 'Session Expiring...', { timeOut: 60000 });
	});

	$rootScope.$on('IdleEnd', function () {
		$log.info('Resumed.');
		toastr.clear();
	});

	$rootScope.$on('IdleTimeout', function () {
		$log.error('Timed Out !');
		timedOut = true;
		toastr.options.progressBar = false;
		toastr.error('Click here to login again.', 'Session Timeout !');
	});

	$rootScope.$on("$stateChangeStart",
		function (event, toState, toParams,
			fromState, fromParams) {
			currentStateObj = toState;
			if ((toState.name == 'notificationAdd' || toState.name == 'notificationManage' || toState.name == 'notificationEdit') && notificationCRUDFlag == 'false') {
				$rootScope.error = "Access denied";
				toState.url = "/error-401";
				toState.views.mainContainer.controller = null;
				toState.views.mainContainer.templateUrl = "resources/partial/error/error-401.html"
			}
			if (finalPages.indexOf(toState.name) == -1 && pageArray.indexOf(toState.name) > -1) {
				$rootScope.error = "Access denied";
				toState.url = "/error-401";
				toState.views.mainContainer.controller = null;
				toState.views.mainContainer.templateUrl = "resources/partial/error/error-401.html"
			}
		});

	$document.on('click', function (event) {
		if (timedOut) {
			event.preventDefault();
			$window.location.href = 'j_spring_security_logout';
		}
	});

}]);

adminApp.config(['$httpProvider', function ($httpProvider) {
	$httpProvider.interceptors.push('umgHttpInterceptor');
}]);

adminApp.config(['$stateProvider', '$urlRouterProvider', function ($stateProvider, $urlRouterProvider) {

	$urlRouterProvider.otherwise('/home');

	$stateProvider.state('/home', {
		url: '/home',
		views: {
			'mainContainer': {
				templateUrl: 'resources/partial/admin/main.html',
				controller: RACtrl
			}
		}
	}).state('umgVersionView', {
		url: '/version/umgVersionView',
		pageTitle: 'Manage Models',
		views: {
			'mainContainer': {
				templateUrl: 'resources/partial/model/publishing/version-list.html',
				controller: VersionListCtrl
			}
		}
	}).state('testbed', {
		url: '/version/testbed',
		pageTitle: 'Testbed',
		views: {
			'mainContainer': {
				templateUrl: 'resources/partial/testbed/testbed.html',
				controller: TestBedControllerNew
			}
		}
	}).state('metrics', {
		url: '/metrics',
		pageTitle: 'Metrices',
		views: {
			'mainContainer': {
				templateUrl: 'resources/partial/model/management/version-metrics.html',
				controller: VersionMetricsCtrl
			}
		}
	}).state('smp', {
		url: '/smp',
		pageTitle: 'Model Publishing',
		views: {
			'mainContainer': {
				templateUrl: 'resources/partial/model/management/model-publish.html',
				controller: ModelPublishingCtrl
			}
		}
	}).state('modelPublish', {
		url: '/modelPublish',
		pageTitle: 'Create Model',
		views: {
			'mainContainer': {
				templateUrl: 'resources/partial/model/publish/add-model.html',
				controller: ModelPublishCtrl
			}
		}
	}).state('sysParam', {
		url: '/sysParam',
		pageTitle: 'Manage System Paramaters',
		views: {
			'mainContainer': {
				templateUrl: 'resources/partial/sys-param/sys-param-list.html',
				controller: SystemParameterCtrl
			}
		}
	}).state('queryEditor', {
		url: '/queryEditor',
		pageTitle: 'Query Editor',
		views: {
			'mainContainer': {
				templateUrl: 'resources/partial/query/query-editor.html',
				controller: QueryEditorCtrl
			}
		}
	}).state('queryView', {
		url: '/queryView',
		pageTitle: 'Queries',
		views: {
			'mainContainer': {
				templateUrl: 'resources/partial/query/query-list.html',
				controller: QueryViewController
			}
		}
	}).state('listPackages', {
		url: '/packages',
		pageTitle: 'Manage Supported Libraries',
		views: {
			'mainContainer': {
				templateUrl: 'resources/partial/package/package-list.html',
				controller: PackageViewCtrl
			}
		}
	}).state('addPackage', {
		url: '/addPackage',
		pageTitle: 'Add Supported Library',
		views: {
			'mainContainer': {
				templateUrl: 'resources/partial/package/package-upload.html',
				controller: PackageUploadCtrl
			}
		}
	}).state('syndicateDataCrud', {
		url: '/syndicateDataCrud',
		pageTitle: 'Add Look-up Data',
		views: {
			'mainContainer': {
				templateUrl: 'resources/partial/model/assumption/create-model-assumption.html',
				controller: SyndicateCRUDController
			}
		}
	}).state('addTenant', {
		url: '/addTenant',
		pageTitle: 'Add Tenant',
		views: {
			'mainContainer': {
				templateUrl: 'resources/partial/tenant/addTenant.html',
				controller: AddTenantController
			}
		}
	}).state('tenantConfig', {
		url: '/tenant',
		pageTitle: 'View / Update Tenant',
		views: {
			'mainContainer': {
				templateUrl: 'resources/partial/tenant/updateTenant.html',
				controller: TenantController
			}
		}
	}).state('manageTenant', {
		url: '/tenants-list',
		pageTitle: 'Manage Tenants',
		views: {
			'mainContainer': {
				templateUrl: 'resources/partial/tenant/tenants-list.html',
				controller: TenantsListCtrl
			}
		}
	}).state('manageAuthTokens', {
		url: '/manageAuthTokens',
		pageTitle: 'Manage Auth Codes',
		views: {
			'mainContainer': {
				templateUrl: 'resources/partial/tenant/manageAuthTokens.html',
				controller: AuthTokenCtrl
			}
		}
	}).state('modelAssumptionList', {
		url: '/model-assumption-list',
		pageTitle: 'Manage Look-up Data',
		views: {
			'mainContainer': {
				templateUrl: 'resources/partial/model/assumption/model-assumption-list.html',
				controller: ModelAssumptionListCtrl
			}
		}
	}).state('modelapiview', {
		url: '/modelApiView',
		pageTitle: 'View Model',
		views: {
			'mainContainer': {
				templateUrl: 'resources/partial/model/management/model-view.html',
				controller: ModelPublishingCtrl
			}
		}
	}).state('addTid', {
		url: '/addTid',
		pageTitle: 'Update Mapping',
		views: {
			'mainContainer': {
				templateUrl: 'resources/partial/mapping/addTid.html',
				controller: AddTidController
			}
		}
	}).state('dashboard', {
		url: '/dashboard',
		pageTitle: 'Transaction Dashboard',
		views: {
			'mainContainer': {
				templateUrl: 'resources/partial/dashboard/dashboard-template.html',
				controller: DashBoardController
			}
		}
	}).state('batchDashboard', {
		url: '/batchDashboard',
		pageTitle: 'Batch/Bulk Dashboard',
		views: {
			'mainContainer': {
				templateUrl: 'resources/partial/batch-dashboard/batch-dashboard-template.html',
				controller: BatchDashboardCtrl
			}
		}
	}).state('modeletPooling', {
		url: '/modeletPooling',
		pageTitle: 'Modelet Pooling',
		views: {
			'mainContainer': {
				templateUrl: 'resources/partial/pool/modelet-pooling.html',
				controller: ModeletPoolingCtrl
			}
		}
	}).state('manageProfile', {
		url: '/manageProfile',
		pageTitle: 'Manage Profile',
		views: {
			'mainContainer': {
				templateUrl: 'resources/partial/modelet-profiling/manage-profiling.html',
				controller: ModeletProfilingCtrl
			}
		}
	}).state('assignProfile', {
		url: '/assignProfile',
		pageTitle: 'Assign Modelet Startup Profiles',
		views: {
			'mainContainer': {
				templateUrl: 'resources/partial/modelet-profiling/assign-profile.html',
				controller: AssignModeletCtrl
			}
		}
	}).state('runningProcess', {
		url: '/runningProcess',
		pageTitle: 'Running Process in Modelet',
		views: {
			'mainContainer': {
				templateUrl: 'resources/partial/modelet-process/modelet-process.html',
				controller: ModeletProcessCtrl
			}
		}
	}).state('error-404', {
		url: '/error-404',
		pageTitle: 'Resource Not Found',
		views: {
			'mainContainer': {
				templateUrl: 'resources/partial/error/error-404.html'
			}
		}
	}).state('error-500', {
		url: '/error-500',
		pageTitle: 'Internal Server Error',
		views: {
			'mainContainer': {
				templateUrl: 'resources/partial/error/error-500.html'
			}
		}
	}).state('error-401', {
		url: '/error-401',
		pageTitle: 'Unauthorized access',
		views: {
			'mainContainer': {
				templateUrl: 'resources/partial/error/error-401.html'
			}
		}
	}).state('approval', {
		url: '/model/approval/*version',
		pageTitle: 'Approval Details',
		views: {
			'mainContainer': {
				templateUrl: 'resources/partial/model/publish/model-approval.html',
				controller: ModelApprovalCtrl
			}
		}
	}).state('notificationAdd', {
		url: '/notificationAdd',
		pageTitle: 'Add Notification',
		views: {
			'mainContainer': {
				templateUrl: 'resources/partial/notification/notificationAddEdit.html',
				controller: NotificationAddEditCtrl
			}
		}
	}).state('notificationEdit', {
		url: '/notificationEdit',
		pageTitle: 'Edit Notification',
		views: {
			'mainContainer': {
				templateUrl: 'resources/partial/notification/notificationAddEdit.html',
				controller: NotificationAddEditCtrl
			}
		}
	}).state('notificationManage', {
		url: '/notificationManage',
		pageTitle: 'Manage Notification',
		views: {
			'mainContainer': {
				templateUrl: 'resources/partial/notification/notificationManage.html',
				controller: NotificationManageCtrl
			}
		}
	}).state('hazelCastStatus', {
		url: '/hazelCastStatus',
		pageTitle: 'Cache Details',
		views: {
			'mainContainer': {
				templateUrl: 'resources/partial/hazelcast/hazelcast.html',
				controller: CacheDetailsCtrl
				/*['$scope','$http',function ($scope, $http) {
							$http.get('hazelCastStatus/listAll').success(function(result){
								$scope.output = result;
							}).error(function(error,status){
								$scope.errorData = error;
							});
						 }]*/
			}
		}
	}).state('privilegeMapping', {
		url: '/privilegeMapping',
		pageTitle: 'Edit Roles & Privileges Mapping',
		views: {
			'mainContainer': {
				templateUrl: 'resources/partial/privilege-mapping/privilegeMapping.html',
				controller: privilegeMappingManageCtrl
			}
		}
	});
}]);
adminApp.run(['$rootScope', '$state', '$stateParams',
	function ($rootScope, $state, $stateParams) {
		$rootScope.$state = $state;
		$rootScope.$stateParams = $stateParams;
	}
]);
adminApp.controller('AdminCtrl', AdminCtrl);
