'use strict';

var tntPortalModule = angular.module('tntPortalModule', ['ui.router', 'ngGrid', 'ngAnimate', 'ngSanitize', 'tenant-portal.services','tnt-portal.httpInterceptor', 
                                         'ui.bootstrap.datetimepicker','gd.ui.jsonexplorer', 'ngIdle']);

tntPortalModule.config(['KeepaliveProvider', 'IdleProvider', function(KeepaliveProvider, IdleProvider) {
	  IdleProvider.idle(1740);
	  IdleProvider.timeout(60);
	  KeepaliveProvider.interval(10);
	}]);

tntPortalModule.run(['$rootScope', 'Idle', '$log', 'Keepalive', '$window', '$document', function($rootScope, Idle, $log, Keepalive, $window, $document){
	  Idle.watch();
	  $log.debug('tenant portal started at : '+new Date());
	  var timedOut = false;
	  
	  toastr.options = {
			  "closeButton": false,
			  "debug": false,
			  "newestOnTop": true,
			  "progressBar": true,
			  "positionClass": "toast-bottom-full-width",
			  "preventDuplicates": false,
			  "onclick": function(){
				  $window.location.href='j_spring_security_logout';
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
	  
	  $rootScope.$on('IdleStart', function() {
		$log.warn('Idle Timeout Started ...');
		toastr.warning('Your session will expire in few second(s).','Session Expiring...', {timeOut: 60000});
	  });

	  $rootScope.$on('IdleEnd', function() {
		  $log.info('Resumed.');
		  toastr.clear();
	  });

	  $rootScope.$on('IdleTimeout', function() {
		  $log.error('Timed Out !');
		  timedOut = true;
		  toastr.options.progressBar = false;
		  toastr.error('Click here to login again.','Session Timeout !');
	  });
	  
	  $document.on('click',function(event){
		  if(timedOut){
			  event.preventDefault();
			  $window.location.href='j_spring_security_logout';
		  }
	  });
	    
	}]);

tntPortalModule.config(function($httpProvider){
    $httpProvider.interceptors.push('tntPortalHttpInterceptor');
});


tntPortalModule.constant('AppName', 'TenantPortal');
tntPortalModule.value('version', '1.0');
//controllers
tntPortalModule.controller('reportController', ReportController);

