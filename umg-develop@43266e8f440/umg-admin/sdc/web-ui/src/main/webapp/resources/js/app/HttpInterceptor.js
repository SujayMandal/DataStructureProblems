'use strict';
var HttpInterceptor = angular.module('umg-admin.httpInterceptor', []);

HttpInterceptor.factory('umgHttpInterceptor', ['$q', '$location', '$log', '$rootScope', function($q, $location, $log, $rootScope) {
	var numLoadings = 0;
	return {
        'request': function(req) {
        	/*if(req.url.indexOf(".html")<0){*/
        	try{
        		 numLoadings++;
        		$rootScope.$broadcast("loader_show");	 
        	}catch(exception){
        		$log.error("Error in Request :"+exception);
        	}
        	/*	}*/
          return req;
        },
        'requestError': function(rejection) {
        	if (!(++numLoadings)) {
            	try{
            		$rootScope.$broadcast("loader_hide");	 
    	    	}catch(exception){
    	    		$log.error("Error in Request :"+exception);
    	    	}
        	}
	        return $q.reject(rejection);
        },
        'response': function(response) {
        	if ((--numLoadings) === 0) {
	        	try{
	        		$rootScope.$broadcast("loader_hide");	 
	        	}catch(exception){
	        		$log.error("Error in Response :"+exception);
	        	}
        	}
          return response;
        },
        'responseError': function(rejection) {
        	if (!(--numLoadings)) {
             	try{
             		$rootScope.$broadcast("loader_hide");
             		$log.error('Error came with status code : '+rejection.status);
                	if(rejection.status == 404 || rejection.status == 500 || rejection.status == 401)
                	$location.path('error-'+rejection.status);
            	}catch(exception){
            		$log.error("Error in Response :"+exception);
            	}
            }
          return $q.reject(rejection);
        }
      };

    }]);


	/**
	 * Intercept href in model-view.html page*/

	function accessPerm(id,event){ 
	    event.preventDefault();
	    var path = $("#"+id).attr('href');
	    $.ajax({
	       url: path
	       ,success: function(response) {
	    	   window.location = path;
	       }
	       ,error: function(response) {
	    	   if(response.status == "401"){
	    		   window.location = "#/error-401";
	    	   }else{
	    		   window.location = "#/error-500";
	    	   }
	       }
	    })
	    return false; //for good measure
	}