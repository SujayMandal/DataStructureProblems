'use strict';
var tntServicesModule = angular.module('tenant-portal.services', []);
tntServicesModule.value('version', '0.1');
tntServicesModule.service('freezeServices', FreezeServices);
tntServicesModule.service('reportService', ReportService);



