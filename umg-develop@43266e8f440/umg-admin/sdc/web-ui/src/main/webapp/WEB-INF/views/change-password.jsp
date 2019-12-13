<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8" />
		<title>REALAnalytics</title>
		<meta name="viewport" content="width=device-width, initial-scale=1.0"> 
	    <meta name="description" content="Login and Registration Form with HTML5 and CSS3" />
	    <meta name="keywords" content="html5, css3, form, switch, animation, :target, pseudo-class" />
	    <meta name="author" content="Codrops" />
	    <link rel="shortcut icon" href="../favicon.ico"> 
	    <link rel="stylesheet" type="text/css" href="resources/css/login/demo.css" />
	    <link rel="stylesheet" type="text/css" href="resources/css/login/style.css" />
		<link rel="stylesheet" type="text/css" href="resources/css/login/animate-custom.css" />
	</head>
	<body>
        <div class="container">
            <header>
                <h1>REAL<span>Analytics</span><sup><small>TM</small></sup></h1>
            </header>
            <section>				
                <div id="container_demo" ng-app="cpApp" ng-controller='ChangePasswordCtrl'>
                    <div id="wrapper">
                        <div id="login" class="animate form">
                            <form name="cpForm" autocomplete="off"> 
                                <h1> Change Password </h1>
                                <input id="username" name="username" type="hidden" value="<sec:authentication property='principal.username'/>"/> 
                                <p> 
                                    <label for="passwordsignup" class="youpasswd" data-icon="p">Your current password </label>
                                    <input id="curr_pass" name="curr_pass" type="password" ng-model="formData.currentPassword" placeholder="eg. P@ssword123" ng-blur="validateCurrentPassword()"/>
                                </p>
                                <p> 
                                    <label for="passwordsignup" class="youpasswd" data-icon="p">Your new password </label>
                                    <input id="new_pass" name="new_pass" type="password" ng-model="formData.newPassword" placeholder="eg. P@ssword123" ng-blur="validateNewPassword()"/>
                                </p>
                                <p> 
                                    <label for="passwordsignup_confirm" class="youpasswd" data-icon="p">Please confirm your new password </label>
                                    <input id="confirm_pass" name="confirm_pass" ng-model="formData.confirmPassword" type="password" placeholder="eg. P@ssword123" ng-blur="validateConfirmPassword()"/>
                                </p>
                                <p id="message" class="uname" style="color:red;text-align: right;" ng-show="showMsg">
							        {{msg}}
								</p>
                                <p class="signin button">
									<a ><input type="button" value="Submit" ng-click="changePassword()"/></a>
								</p>
                                <p class="change_link">
                                <small>© 2009-2014 Altisource Portfolio Solutions. All Rights Reserved.<br>
                                	   Terms &amp; Conditions | Privacy Policy</small>
                                </p>
                            </form>
                        </div>
                        
                    </div>
                </div>
            </section>
        </div>
        
        
        
        <script src="<c:url value="/resources/lib/angularJS/angular.js" />"></script>
        
        <script type='text/javascript'>//<![CDATA[ 
                                                  
        function ChangePasswordCtrl($scope, $log, $http, $window) {
        	
        	$scope.formData = {
        			userName : '',
        			currentPassword : '',
        			newPassword : '',
        			confirmPassword : ''
        	};
        	
        	$scope.formData.userName = document.getElementById('username').value;
        	
        	$scope.msg = '';
        	$scope.showMsg = false;

        	$scope.changePassword = function(){
        		$scope.msg = '';
        		$scope.showMsg = false;
        		$log.info('Request received to change password ...');
        		
        		if($scope.validateCurrentPassword() && $scope.validateConfirmPassword()){
        		
	        		$http.post('dbAuth/changePassword', $scope.formData).
	        		  success(function(data, status, headers, config) {
	        			  if(data.error){
	        				  $log.error(data.message);
		        			  $scope.msg = data.message;
		        			  $scope.showMsg = true;
	        			  }else{
		        			  $window.location.href= 'login?passwordChanged=true';
		        			  $log.info(data.message);
	        			  }
	        		  }).
	        		  error(function(data, status, headers, config) {
	        			  $log.error(data.message);
	        			  $scope.msg = data.message;
	        			  $scope.showMsg = true;
	        		  });
        		}
        	};
        	
        	$scope.validateCurrentPassword = function(){
        		$scope.msg = '';
        		$scope.showMsg = false;
        		var current_password = $scope.formData.currentPassword;
        		
        		$log.info('Validating Current Password...');
        		
        		if(current_password == '' || angular.isUndefined(current_password)){
        			$log.error('Current Password is Mandatory.');
        			$scope.msg = 'Please enter current password.';
        			$scope.showMsg = true;
        			return false;
        		}
        		
        		$log.info('Validated Successfully.');
        		return true;
        		
        	};
        	
        	
        	
        	$scope.validateNewPassword = function(){
        		$scope.msg = '';
        		$scope.showMsg = false;
        		var new_password = $scope.formData.newPassword;
        		
        		$log.info('Validating New Password...');
        		
        		var password_pattern = /((?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*():?<>]))/;
        		
        		if(new_password == '' || angular.isUndefined(new_password)){
        			$log.error('New Password is Mandatory.');
        			$scope.msg = 'Please enter new password.';
        			$scope.showMsg = true;
        			return false;
        		}
        		
        		if(new_password.length < 8){
        			$log.error('Password is Too Short.');
        			$scope.msg = 'Password is too short. Need atleast 8 characters.';
        			$scope.showMsg = true;
        			return false;
        		}
        		
        		if(!new_password.match(password_pattern)){
        			$log.error('Password should have atleast 1 small/capital character, 1 number, 1 Special character.');
        			$scope.msg = 'Password is not strong, Password should have atleast 1 small/capital character, 1 number, 1 Special character.';
        			$scope.showMsg = true;
        			return false;
        		}
        		
        		if(new_password.length > 32){
        			$log.error('Password is Too Long.');
        			$scope.msg = 'Password is too long. Should have maximum 32 characters.';
        			$scope.showMsg = true;
        			return false;
        		}
        		
        		$log.info('Validated Successfully.');
        		return true;
        		
        	};
        	
        	
        	$scope.validateConfirmPassword = function(){
        		$scope.msg = '';
        		$scope.showMsg = false;
        		var new_password  = $scope.formData.newPassword;
        		var confirm_password = $scope.formData.confirmPassword;
        		
        		$log.info('Validating Password Confirmation...');
        		
        		if($scope.validateNewPassword()){
        			
        			if(confirm_password == '' || angular.isUndefined(confirm_password)){
            			$log.error('Password Confirmation is Mandatory.');
            			$scope.msg = 'Please enter password to confirm';
            			$scope.showMsg = true;
            			return false;
            		}
        			
        			if(confirm_password !== new_password){
        				$log.error('Password doesnt match. ');
        				$scope.msg = 'Password does not match';
        				$scope.showMsg = true;
        				return false;
        			}
        			
        			$log.info('Validated Successfully.');
        		}
        		else{
        			$log.info('Validated Failed.');
        			return false;
        		}

        		return true;
        		
        	};
        }                                        
		
		angular.module('cpApp', []);
		//]]>  
		
		</script>
    </body>
</html>