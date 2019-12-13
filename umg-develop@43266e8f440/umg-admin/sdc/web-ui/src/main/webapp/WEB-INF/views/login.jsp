<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
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
                <div id="container_demo" >
                	
                	<a class="hiddenanchor" id="toreset"></a>
                    <a class="hiddenanchor" id="tologin"></a>
                    
                    <div id="wrapper">
                    
                        <!-- LOGIN FORM -->
                        
                        <div id="login" class="animate form">
                            <form id="loginForm" name='loginForm' autocomplete="off" action="<c:url value='j_spring_security_check' />" method='POST'> 
                                <h1>Log in</h1> 
                                <p> 
                                    <label for="username" class="uname" data-icon="u" > Your user id </label>
                                    <input type="text" id="j_username" name="j_username" required="required" placeholder="myuserid"/>
                                </p>
                                <p> 
                                    <label for="password" class="youpasswd" data-icon="p"> Your password </label>
                                    <input type="password" id="j_password" name="j_password" required="required" placeholder="eg. P@ssword123" /> 
                                </p>
                                <c:if test="${param.error}">
										<p id="msg" class="uname" style="color:red;text-align: right;">
							                ${sessionScope["SPRING_SECURITY_LAST_EXCEPTION"].message} 
										</p>
								</c:if>		
								<c:if test="${param.passwordChanged}">
										<p id="success_msg" class="uname" style="color:green;text-align: right;">
							                Your password changed successfully. Please login
										</p>
								</c:if>
                                <p class="login button"> 
                                    <a><input type="submit" value="Login"/></a>
								</p>
								<div align="center">
								<a href="#toreset" class="to_reset">Forgot Password ?</a></div><br>
                                <p class="change_link">
                                <small>© 2009-2014 Altisource Portfolio Solutions. All Rights Reserved.<br>
                                	   Terms &amp; Conditions | Privacy Policy</small>
                                </p>
                            </form>
                        </div>
                        
                        <!-- RESET FORM -->
                    
                    	<div id="reset" class="animate form">
                            <form autocomplete="on"> 
                                <h1>RESET PASSWORD</h1> 
                                <p> 
                                    To reset your password, please raise a ticket on  <br>
									<a href="https://servicedesk.ascorp.com/CAisd/pdmweb.exe">https://servicedesk.ascorp.com/CAisd/pdmweb.exe</a> <br>
									For any high severity issues, please call <strong>298463</strong>
                                </p>
                                <p class="change_link">  
									<a href="#tologin" class="to_reset"> Go and log in </a>
								</p>
                            </form>
                        </div>
                        
                    </div>
                </div>  
            </section>

        </div>
        <script src="<c:url value = "/resources/lib/jquery/jQuery-2.1.4.min.js" />"></script>
		<script src="<c:url value = "/resources/lib/jquery/jquery-ui.min.js" />"></script>
        <script>
		        function noiFrame() {
		            try {
		                if (window.top !== window.self) {
		                    document.write = "";
		                    window.top.location = window.self.location;
		                    setTimeout(function() {
		                          document.body.innerHTML = '';
		                    }, 0);
		                    window.self.onload = function() {
		                          document.body.innerHTML = '';
		                    };
		                }
		            }catch (err) {
		            }
		       }
		    noiFrame();
       		 $(document).ready(function(){
        	  	$('#loginForm').submit(function() {
        	    	var el = $(this);
        	    	var hash = window.location.hash;
        	    	if (hash) el.prop('action', el.prop('action') + '#' + unescape(hash.substring(1)));
        	    	return true;
        	  	});
        	});
        </script>
        
    </body>
</html>