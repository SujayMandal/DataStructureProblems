<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<title>Dynamic Pricing</title>
<link rel="icon" type="image/x-icon"
	href="resources/images/ra_icon_Mh6_icon.ico">
<style>
@import url(http://weloveiconfonts.com/api/?family=fontawesome);

[class*="fontawesome-"]:before {
	font-family: 'FontAwesome', sans-serif;
}

html {
	background: url(resources/images/bkg.jpg) no-repeat center center fixed;
	-webkit-background-size: cover;
	-moz-background-size: cover;
	-o-background-size: cover;
	background-size: cover;
}

body {
	background-color: transparent;
	color: #FFFFFF;
	font-size: 18px;
	padding-bottom: 30px;
	margin: 0px;
	font-family: sans-serif;
}

h1 {
	font-family: sans-serif;
	font-weight: 500;
}

navbar-text {
	float: left;
}

.container-fluid {
	padding-right: 15px;
	padding-left: 15px;
	margin-right: auto;
	margin-left: auto;
}

header.main-section .positioner {
	display: table;
	width: 100%;
}

header.main-section .logo {
	display: table-cell;
	vertical-align: middle;
	height: 42px;
	width: 20%;
	text-align: left;
}

header.main-section .logo img {
	width: auto;
	height: 42px;
}

header.main-section .title {
	display: table-cell;
	vertical-align: middle;
	height: 70px;
	width: 30%;
	text-align: center;
}

header.main-section .header-wrapper {
	background-color: #819E34;
	transform: skewX(-30deg);
	height: 100%;
	vertical-align: middle;
}

header.main-section h1 {
	display: block;
	width: 100%;
	line-height: 70px;
	text-align: center;
	font-size: 22px;
	color: #FFFFFF;
	transform: skewX(30deg);
	margin: 0;
	padding: 0;
	white-space: nowrap;
}

input {
	border: none;
	font-family: 'Open Sans', Arial, sans-serif;
	font-size: 16px;
	line-height: 1.5em;
	padding: 0;
	-webkit-appearance: none;
}

p {
	line-height: 1.5em;
}

after {
	clear: both;
}

#login {
	margin: 50px auto;
	width: 320px;
}

#login form {
	margin: auto;
	padding: 22px 22px 22px 22px;
	width: 100%;
	border-radius: 5px;
	background: #4A4A4A;
	border-top: 3px solid #434a52;
	border-bottom: 3px solid #434a52;
}

#login form span {
	background-color: #363b41;
	border-radius: 3px 0px 0px 3px;
	border-right: 3px solid #434a52;
	color: #606468;
	display: block;
	float: left;
	line-height: 35px;
	text-align: center;
	width: 50px;
	height: 35px;
}

#login form input[type="text"] {
	background-color: #3b4148;
	border-radius: 0px 3px 3px 0px;
	color: #a9a9a9;
	margin-bottom: 1em;
	padding: 0 16px;
	width: 267px;
	height: 35px;
}

#login form input[type="password"] {
	background-color: #3b4148;
	border-radius: 0px 3px 3px 0px;
	color: #a9a9a9;
	margin-bottom: 1em;
	padding: 0 16px;
	width: 267px;
	height: 35px;
}

#login form input[type="submit"] {
	background: #819e34;
	border: 0;
	width: 100%;
	height: 40px;
	border-radius: 3px;
	color: white;
	cursor: pointer;
	transition: background 0.3s ease-in-out;
}

#login form input[type="submit"]:hover {
	background: #95b825;
}

header.main-section .primary-nav {
    display: table-cell;
    vertical-align: middle;
    width: 50%;
    text-align: right;
    margin: 0;
    padding: 0; 
}

.navbar-collapse {
  padding-right: 15px;
  padding-left: 15px;
  overflow-x: visible;
  -webkit-overflow-scrolling: touch;
}

.navbar-collapse.collapse {
    display: block !important;
    height: auto !important;
    padding-bottom: 0;
    overflow: visible !important;
}

.col-sm-2, .col-sm-8 {
  position: relative;
  min-height: 1px;
  padding-right: 15px;
  padding-left: 15px;
}

.navbar-nav {
  margin: 7.5px -15px;
}

.nav {
  padding-left: 0;
  margin-bottom: 0;
  list-style: none;
}

.navbar-right {
    float: right !important;
    margin-right: -15px;
}

header.main-section {
	background-color: #505865;
	border-bottom: 3px solid #95B825;
	height: 67px;
}
</style>
<script type="text/javascript">
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
		} catch (err) {
		}
	}
	noiFrame();
</script>
</head>

<body>
	<link rel="stylesheet"
		href="http://fonts.googleapis.com/css?family=Open+Sans:400,700">
	<header class="main-section">
		<div class="container-fluid">
			<div class="positioner">
				<div class="logo">
					<a><img src="resources/images/logo-altisource.svg" /></a>
				</div>
				<div class="title">
					<div class="header-wrapper">
						<h1 class="navbar-text">Dynamic Pricing App</h1>
					</div>
				</div>
				<div class="primary-nav">
					<div id="nav-menu" class="collapse navbar-collapse">
						<div>
							<div class="col-sm-2"></div>
							<div class="col-sm-8">
								<ul class="nav navbar-nav">
								</ul>
							</div>
							<div class="col-sm-2"></div>
						</div>
						<ul class="nav navbar-nav navbar-right">
						</ul>
					</div>
				</div>
			</div>
		</div>
	</header>
	<div id="login">
		<form name='form-login' id='form-login'
			action="<c:url value='/loginPage' />" method='POST'>
			<span class="fontawesome-user"></span> <input id="user" type='text'
				name='username' placeholder="Username"/> <span
				class="fontawesome-lock"></span> <input id="pass" type='password'
				name='password' placeholder="Password"  autocomplete="off"/> <input name="submit"
				type="submit" value="Login" />
			<div
				style="text-align: center; color: rgba(209, 217, 222, 0.88); padding-top: 20px;"
				class="message">
				<c:if test="${not empty error}">
					<div style="font-weight: 700;">Login Failed :
						${sessionScope["SPRING_SECURITY_LAST_EXCEPTION"].message}</div>
				</c:if>
				<c:if test="${not empty errorDenied}">
					<div style="font-weight: 700;">Login Failed :
						${requestScope['SPRING_SECURITY_403_EXCEPTION'].message}</div>
				</c:if>
			</div>
		</form>
	</div>
</body>
</html>