'use strict';
var HttpInterceptor = angular.module('tnt-portal.httpInterceptor', []);

HttpInterceptor.factory('tntPortalHttpInterceptor', function($q, $window, freezeServices) {
    return {
    	/**********************************
    	 * request interceptor
    	 *********************************/
        'request': function(req) {
          // do something on success
        	if(req.url.indexOf(".html")<0){
        		if(req.url!='tenantUsage/listAll' && req.url!='tenantUsage/createSearchRequest'){// avoid the search request because it is customized
        			try{
                		freezeServices.freezeScreen('commonFreezeScreen');	 
                	}catch(exception){
                		console.log("Error in interception :"+exception);
                	}
        		}
        	}
          return req;
        },
    	/**********************************
    	 * request error interceptor
    	 *********************************/
       'requestError': function(rejection) {
          // do something on error
          //if (canRecover(rejection)) {
          //  return responseOrNewPromise;
         // }
       	try{
       		freezeServices.unfreezeScreen('commonFreezeScreen');	 
    	}catch(exception){
    		console.log("Error in interception :"+exception);
    	}
          return $q.reject(rejection);
        },

    	/**********************************
    	 * response interceptor
    	 *********************************/

        'response': function(response) {
          // do something on success
        	
        	if(response.config.url.indexOf(".html")<0){
	        	try{
	        		freezeServices.unfreezeScreen('commonFreezeScreen');	 
	        	}catch(exception){
	        		console.log("Error in interception :"+exception);
	        	}
        	}
          return response;
        },
    	/**********************************
    	 * response error interceptor
    	 *********************************/
       'responseError': function(rejection) {
          // do something on error
         // if (canRecover(rejection)) { 
         //   return responseOrNewPromise;
         // }
          	try{
          		freezeServices.unfreezeScreen('commonFreezeScreen');	 
        	}catch(exception){
        		console.log("Error in interception :"+exception);
        	}
          return $q.reject(rejection);
        }
      };


    });