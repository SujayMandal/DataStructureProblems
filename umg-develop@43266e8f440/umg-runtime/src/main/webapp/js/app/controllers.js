'use strict';

/* Controllers */

var controllers = angular.module('controllers', []);

controllers.controller('ModelListController', ['$scope', 'Model',
  function($scope, Model) {
    $scope.models = Model.query();
    //$scope.orderProp = 'fromDate';
  }]);

controllers.controller('ModelDetailController', ['$scope', '$routeParams', 'Model',
  function($scope, $routeParams, Model) {
    $scope.model = Model.get({modelId: $routeParams.modelId}, function(model) {
      //$scope.mainImageUrl = models.images[0];
    });

    $scope.setImage = function(imageUrl) {
      //$scope.mainImageUrl = imageUrl;
    }
  }]);

controllers.controller('HomeController', ['$scope', 'Home',
  function($scope, Home) {
    $scope.messages = {count:1,items:[{team:"Support Team",time:"5 mins", message:"Here is you message!!!"}]};
    $scope.notifications = {count:1,items:[{message:"5 new models deployed today"}]};
    $scope.tasks = {count:1,items:[{message:"Deploy new modelet.",completion:40}]};
    $scope.home = Home.query();
  }]);

controllers.controller('DashboardController', ['$scope', 'Dashboard',
	function($scope, Dashboard) {
	   $scope.dashboard = Dashboard.query();
  }]);

controllers.controller('TimelineController', ['$scope', 'Reddit',
  function($scope, Reddit) {
	  $scope.reddit = new Reddit();
	}]);

controllers.controller('MetricsController', ['$scope', 'MetricsService', 'HealthCheckService', 'ThreadDumpService',
function ($scope, MetricsService, HealthCheckService, ThreadDumpService) {

    $scope.refresh = function() {
        HealthCheckService.check().then(function(data) {
            $scope.healthCheck = data;
        });

        $scope.metrics = MetricsService.get();

        $scope.metrics.$get({}, function(items) {

            $scope.servicesStats = {};
            $scope.cachesStats = {};
            angular.forEach(items.timers, function(value, key) {
                if (key.indexOf("web.rest") != -1 || key.indexOf("service") != -1) {
                    $scope.servicesStats[key] = value;
                }

                if (key.indexOf("net.sf.ehcache.Cache") != -1) {
                    // remove gets or puts
                    var index = key.lastIndexOf(".");
                    var newKey = key.substr(0, index);

                    // Keep the name of the domain
                    index = newKey.lastIndexOf(".");
                    $scope.cachesStats[newKey] = {
                        'name': newKey.substr(index + 1),
                        'value': value
                    };
                }
            });
        });
    };

    $scope.refresh();

    $scope.threadDump = function() {
        ThreadDumpService.dump().then(function(data) {
            $scope.threadDump = data;

            $scope.threadDumpRunnable = 0;
            $scope.threadDumpWaiting = 0;
            $scope.threadDumpTimedWaiting = 0;
            $scope.threadDumpBlocked = 0;

            angular.forEach(data, function(value, key) {
                if (value.threadState == 'RUNNABLE') {
                    $scope.threadDumpRunnable += 1;
                } else if (value.threadState == 'WAITING') {
                    $scope.threadDumpWaiting += 1;
                } else if (value.threadState == 'TIMED_WAITING') {
                    $scope.threadDumpTimedWaiting += 1;
                } else if (value.threadState == 'BLOCKED') {
                    $scope.threadDumpBlocked += 1;
                }
            });

            $scope.threadDumpAll = $scope.threadDumpRunnable + $scope.threadDumpWaiting +
                $scope.threadDumpTimedWaiting + $scope.threadDumpBlocked;

        });
    };

    $scope.getLabelClass = function(threadState) {
        if (threadState == 'RUNNABLE') {
            return "label-success";
        } else if (threadState == 'WAITING') {
            return "label-info";
        } else if (threadState == 'TIMED_WAITING') {
            return "label-warning";
        } else if (threadState == 'BLOCKED') {
            return "label-danger";
        }
    };
    
    $(".connectedSortable").sortable({
        placeholder: "sort-highlight",
        connectWith: ".connectedSortable",
        handle: ".box-header, .nav-tabs",
        forcePlaceholderSize: true,
        zIndex: 999999
    }).disableSelection();
    $(".box-header, .nav-tabs").css("cursor","move");
    
  //Activate tooltips
    $("[data-toggle='tooltip']").tooltip();

    /*     
     * Add collapse and remove events to boxes
     */
    $("[data-widget='collapse']").click(function() {
        //Find the box parent        
        var box = $(this).parents(".box").first();
        //Find the body and the footer
        var bf = box.find(".box-body, .box-footer");
        if (!box.hasClass("collapsed-box")) {
            box.addClass("collapsed-box");
            bf.slideUp();
        } else {
            box.removeClass("collapsed-box");
            bf.slideDown();
        }
    });
}]);
