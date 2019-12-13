<%@page import="org.springframework.security.core.userdetails.User"%>
<%@page import="org.springframework.security.core.context.SecurityContextHolder"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!doctype html>

<head>
<style type="text/css">
.error-style {
	border-radius: 5px; 
	-moz-border-radius: 5px; 
	-webkit-border-radius: 5px;	

	background: rgba(212,228,239,1);
	background: -moz-linear-gradient(left, rgba(212,228,239,1) 0%, rgba(212,228,239,1) 53%, rgba(134,174,204,1) 100%);
	background: -webkit-gradient(left top, right top, color-stop(0%, rgba(212,228,239,1)), color-stop(53%, rgba(212,228,239,1)), color-stop(100%, rgba(134,174,204,1)));
	background: -webkit-linear-gradient(left, rgba(212,228,239,1) 0%, rgba(212,228,239,1) 53%, rgba(134,174,204,1) 100%);
	background: -o-linear-gradient(left, rgba(212,228,239,1) 0%, rgba(212,228,239,1) 53%, rgba(134,174,204,1) 100%);
	background: -ms-linear-gradient(left, rgba(212,228,239,1) 0%, rgba(212,228,239,1) 53%, rgba(134,174,204,1) 100%);
	background: linear-gradient(to right, rgba(212,228,239,1) 0%, rgba(212,228,239,1) 53%, rgba(134,174,204,1) 100%);
	filter: progid:DXImageTransform.Microsoft.gradient( startColorstr='#d4e4ef', endColorstr='#86aecc', GradientType=1 );
	margin-left: auto;
    margin-right: auto;

	display:block;
	padding:40px;
	position:relative;
	top:200px;
	width :400px;	
}

.header {
	padding:4px;
	font-size: 20px;
	font-weight: bold;
	font-family: sans-serif ;		
}

.msg {
	padding:4px;
	font-size: 15px;
	font-weight: bold;
	font-family: sans-serif ;		
}

</style>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Universal Model Gateway</title>
</head>
<body>
	<div id="model-menu" class="box error-style center-div menu-container" > 
	
	<div class="header" >  Unauthorized access :  </div>	
	<div class="msg"> Current login user, do not have permission to access this page. Please Login as privileged user.  </div>
	<div class="msg"><a href="<c:url value="j_spring_security_logout" />" > logout </a>  </div>	
	</div>
</body>
</html>