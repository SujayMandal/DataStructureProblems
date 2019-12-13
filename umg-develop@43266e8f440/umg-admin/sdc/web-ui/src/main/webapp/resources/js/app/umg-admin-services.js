'use strict';
var servicesModule = angular.module('umg-admin.services', []);

servicesModule.service('sharedPropertiesService', sharedPropertiesService);
servicesModule.service('umgVersionService', UmgVersionService);
servicesModule.service('tidListDisplayService', TidListDisplayService);
servicesModule.service('testBedService', TestBedService);
servicesModule.service('sysParamService', SystemParameterService);
servicesModule.service('queryEditorService', QueryEditorService);
servicesModule.service('pkgService', PackageService);
servicesModule.service('cacheDetailsService', CacheDetailsService);
servicesModule.service('syndicateDataService', SyndicateDataService);
servicesModule.service('tenantService', TenantService);
servicesModule.service('pluginService', PluginService);
servicesModule.service('addTidService', AddTidService);
servicesModule.service('batchDashBoardService', BatchDashBoardService);
servicesModule.service('dashboardService', DashBoardService);
servicesModule.service('raService', RAService);
servicesModule.service('modeletPoolingService', ModeletPoolingService);
servicesModule.service('modelPublishService', ModelPublishService);
servicesModule.service('mps', ModelPublishingService);
servicesModule.service('modelApprovalService', ModelApprovalService);
servicesModule.service('notificationAddEditService', NotificationAddEditService);
servicesModule.service('notificationManageService', NotificationManageService);
servicesModule.service('privilegeMappingService', PrivilegeMappingService);
servicesModule.service('modeletProfilingService', ModeletProfilingService);
servicesModule.service('assignModeletService', AssignModeletService);
servicesModule.service('modeletProcessService', ModeletProcessService);
