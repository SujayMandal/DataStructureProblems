'use strict';

/* Services */

var services = angular.module('services', ['ngResource']);

services.factory('Phone', ['$resource',
  function($resource){
    return $resource('phones/:phoneId.json', {}, {
      query: {method:'GET', params:{phoneId:'phones'}, isArray:true}
    });
  }]);

services.factory('Model', ['$resource',
  function($resource){
    return $resource('api/runtime/:modelId', {}, {
      query: {method:'GET', params:{modelId:'flows'}, isArray:false}
    });
  }]);

services.factory('Home', ['$resource',
  function($resource){
   return $resource('api/home', {}, {
     query: {method:'GET', isArray:false}
   });
  }]);

services.factory('Dashboard', ['$resource',
  function($resource){
   return $resource('api/dashboard/statistics', {}, {
     query: {method:'GET', isArray:false}
   });
  }]);



services.factory('MetricsService', ['$resource',
    function ($resource) {
        return $resource('admin/metrics', {}, {
            'get': { method: 'GET'}
        });
    }]);

services.factory('ThreadDumpService', ['$http',
    function ($http) {
        return {
            dump: function() {
                var promise = $http.get('admin/threads').then(function(response){
                    return response.data;
                });
                return promise;
            }
        };
    }]);

services.factory('HealthCheckService', ['$rootScope', '$http',
    function ($rootScope, $http) {
        return {
            check: function() {
                var promise = $http.get('admin/healthcheck').then(function(response){
                    return response.data;
                });
                return promise;
            }
        };
    }]);

// Reddit constructor function to encapsulate HTTP and pagination logic
services.factory('Reddit', function($http) {
  var Reddit = function() {
    this.items = [];
    this.busy = false;
    this.after = '';
  };

  Reddit.prototype.nextPage = function() {
    if (this.busy) return;
    this.busy = true;

    var url = "http://api.reddit.com/hot?after=" + this.after + "&jsonp=JSON_CALLBACK";
    $http.jsonp(url).success(function(data) {
      var items = data.data.children;
      for (var i = 0; i < items.length; i++) {
        this.items.push(items[i].data);
      }
      this.after = "t3_" + this.items[this.items.length - 1].id;
      this.busy = false;
    }.bind(this));
  };

  return Reddit;
});