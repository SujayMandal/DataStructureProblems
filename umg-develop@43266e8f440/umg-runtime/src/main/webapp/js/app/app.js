'use strict';

/* App Module */

var app = angular.module('app', [
  'ngRoute',
  'infinite-scroll',
  'animations',
  'controllers',
  'services',
  'filters'
]);

app.config(['$routeProvider',
  function($routeProvider) {
    $routeProvider.
      when('/dashboard',{
    	  templateUrl: 'partials/dashboard.html',
    	  controller: 'DashboardController'
      }).
      when('/tenants', {
          templateUrl: 'partials/tenant-list.html',
          controller: 'TenantListController'
      }).
      when('/models', {
        templateUrl: 'partials/model-list.html',
        controller: 'ModelListController'
      }).
      when('/models/:modelId', {
        templateUrl: 'partials/model-detail.html',
        controller: 'ModelDetailController'
      }).
      when('/timeline', {
          templateUrl: 'partials/timeline.html',
          controller: 'TimelineController'
        }).
      when('/metrics', {
          templateUrl: 'partials/metrics.html',
          controller: 'MetricsController'
        }).
      otherwise({
        redirectTo: '/dashboard'
      });
  }]);
