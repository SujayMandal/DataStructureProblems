'use strict';
var AppDirectives = angular.module('umg-admin.directives', ['ngSanitize']);

AppDirectives.directive('fileModel', ['$parse', function ($parse) {
    return {
        restrict: 'A',
        link: function(scope, element, attrs) {
            var model = $parse(attrs.fileModel);
            var modelSetter = model.assign;
            
            element.bind('change', function(){
                scope.$apply(function(){
                    modelSetter(scope, element[0].files[0]);
                });
            });
        }
    };
}]).directive('autoSuggest',function($timeout){
	return function(scope, iElement, iAttrs) {
        iElement.autocomplete({
            source: scope[iAttrs.uiItems],
            select: function() {
                $timeout(function() {
                  iElement.trigger('input');
                }, 0);
            }
        });
	};
}).directive('positiveNumbers', function() {
    return function(scope, element, attrs) {
        var min = parseInt(attrs.min, 10) || 0,
            max = parseInt(attrs.max, 10) || 10000, 
            value = element.val();
        element.on('keyup', function(e) {
        		if(element.val()===''||element.val()==' ') {
        			 value ='';
            }else if (!between(element.val(), min, max)) {
               element.val(value);
            } 
            else {
                value = element.val();
            }
        });
            
        function between(n, min, max) { return n >= min && n <= max; };
    };
}).directive('dateTimePicker', function(){
	return {
		require: '?ngModel',
        restrict: 'AE',
        scope: {
            format: '=format'
          },
        link: function (scope, elem, attrs, ngModel){
       	 elem.datetimepicker({
             format: scope.format
         }).on('dp.change', function(e){
        	 ngModel.$setViewValue(e.target.value);
             scope.$apply();
         }).on('keyup', function(e){
        	 $(this).trigger('change');
        	 ngModel.$setViewValue(e.target.value);
             scope.$apply();
         }).on('focus', function(e){
        	 $(this).trigger('change');
        	 ngModel.$setViewValue(e.target.value);
             scope.$apply();
         });
     }
	};
}).directive('currentTime',['$interval','$filter', function($interval,$filter) {

    function link(scope, element, attrs) {
      var timeoutId;
      var TIMEZONE_OFFSET = '-4.00';
      function updateTime() {
    	  var d = new Date();
    	  var timeZone = 'America/New_York';
    	  /*var  utc = d.getTime() + (d.getTimezoneOffset() * 60000);
    	  var dateStr = $filter('date')(new Date(utc + (3600000*TIMEZONE_OFFSET)),"yyyy/MMM/dd   h:mm:ss a"); */
    	  // Comented above and added below code to fix PM-4017
    	  var dateStr = moment(d).tz(timeZone).format("YYYY/MMM/DD   h:mm:ss A");
    	  var abbr = ' EST';
    	  /*var abbr = moment.tz(d,timeZone).isDST() ? ' EDT' : ' EST'; can be enabled to show EDT or EST dynamically*/ 
    	  element.text(dateStr+abbr);
      }

      element.on('$destroy', function() {
        $interval.cancel(timeoutId);
      });

      timeoutId = $interval(function() {
        updateTime();
      }, 1000);
    }

    return {
      link: link
    };
  }]).directive("loader", ['$rootScope' ,function ($rootScope) {
	    return function ($scope, element, attrs) {
	        $scope.$on("loader_show", function () {
	            return element.show();
	        });
	        return $scope.$on("loader_hide", function () {
	            return element.hide();
	        });
	    };
  }]).directive("compile",['$compile', function($compile){
	  return function(scope, element, attrs) {
	      scope.$watch(
	        function(scope) {
	          return scope.$eval(attrs.compile);
	        },
	        function(value) {
	          element.html(value);
	          $compile(element.contents())(scope);
	        }
	      );
	   };
  }]);