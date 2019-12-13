<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8" />
<title>REALAnalytics</title>
<link rel="shortcut icon" type="image/x-icon" href="<c:url value = "/resources/images/ra_icon_Mh6_icon.ico" />" />
<link rel="stylesheet" type="text/css" media="screen" href="<c:url value="/resources/css/umg-admin-main.css" />" />
<link rel="stylesheet" type="text/css" media="screen" href="<c:url value="/resources/css/umg/NotificationApproval.css" />" />
<link href="<c:url value="https://maxcdn.bootstrapcdn.com/font-awesome/4.3.0/css/font-awesome.min.css"/>" rel="stylesheet" type="text/css" />
<link href="<c:url value="https://code.ionicframework.com/ionicons/2.0.1/css/ionicons.min.css"/>" rel="stylesheet" type="text/css" />
</head>
<body>
	<div class="col-sm-12">
		<div class="box box-primary noBorderTop noBoxShadow">
			<div class="col-sm-2 zeroPaddingRight">
				<div class="box-header box-header-div1"></div>
			</div>
			<div class="col-sm-2 zeroPaddingLeftRight">
				<div class="box-header box-header-div1 box-header-skewed"></div>
			</div>
			<div class="col-sm-8 zeroPaddingLeft">
				<div class="box-header box-header-div2">
					<h2 class="box-title headerTitle1">
						REAL<span class="fontWeight500">Analytics</span>
					</h2>
				</div>
			</div>
			<div class="col-sm-9 zeroPaddingLeft">
				<div id="container_demo" class="box-body">
					<div class="extraPaddingTopBottom">
						<b class="textMessage"> <c:out
								value="${notificationMessageInfo.notificationStatus}" escapeXml="false"></c:out>
						</b>
					</div>
					<div class="box box-primary noBorderTop">
						<div class="box-header box-header-div3">
							<i class="fa fa-info-circle"></i>
							<h3 class="box-title fontWeight600">Model Details</h3>
						</div>
						<div class="box-body boxBodyDiv">
							<div class="col-sm-12 zeroPaddingLeft borderBottom">
								<div class="col-sm-4 zeroPaddingLeft">
									<c:out value="Model Name"></c:out>
								</div>
								<div class="col-sm-8">
									<c:out value="${notificationMessageInfo.name}"></c:out>
								</div>
							</div>
							<div class="col-sm-12 zeroPaddingLeft borderBottom">
								<div class="col-sm-4 zeroPaddingLeft">
									<c:out value="Model Description"></c:out>
								</div>
								<div class="col-sm-8">
									<c:out value="${notificationMessageInfo.description}"></c:out>
								</div>
							</div>
							<div class="col-sm-12 zeroPaddingLeft borderBottom">
								<div class="col-sm-4 zeroPaddingLeft">
									<c:out value="Model Version"></c:out>
								</div>
								<div class="col-sm-8">
									<c:out value="${notificationMessageInfo.majorVersion}"></c:out>
									.
									<c:out value="${notificationMessageInfo.minorVersion}"></c:out>
								</div>
							</div>
							<div class="col-sm-12 zeroPaddingLeft borderBottom">
								<div class="col-sm-4 zeroPaddingLeft">
									<c:out value="Status"></c:out>
								</div>
								<div class="col-sm-8">
									<c:out value="${notificationMessageInfo.status}"></c:out>
								</div>
							</div>
							<div class="col-sm-12 zeroPaddingLeft borderBottom">
								<div class="col-sm-4 zeroPaddingLeft">
									<c:out value="Tenant"></c:out>
								</div>
								<div class="col-sm-8">
									<c:out value="${notificationMessageInfo.tenantName}"></c:out>
								</div>
							</div>
							<div class="col-sm-12 zeroPaddingLeft borderBottom">
								<div class="col-sm-4 zeroPaddingLeft">
									<c:out value="Created By"></c:out>
								</div>
								<div class="col-sm-8">
									<c:out value="${notificationMessageInfo.createdBy}"></c:out>
								</div>
							</div>
							<div class="col-sm-12 zeroPaddingLeft borderBottom">
								<div class="col-sm-4 zeroPaddingLeft">
									<c:out value="Created On"></c:out>
								</div>
								<div class="col-sm-8">
									<c:out value="${notificationMessageInfo.createdDateTime}"></c:out>
								</div>
							</div>
							<div class="col-sm-12 zeroPaddingLeft borderBottom">
								<div class="col-sm-4 zeroPaddingLeft">
									<c:out value="Approved By"></c:out>
								</div>
								<div class="col-sm-8">
									<c:out value="${notificationMessageInfo.publishedBy}"></c:out>
								</div>
							</div>
							<div class="col-sm-12 zeroPadding">
								<div class="col-sm-4 zeroPaddingLeft">
									<c:out value="Approved On"></c:out>
								</div>
								<div class="col-sm-8">
									<c:out value="${notificationMessageInfo.publishedDateTime}"></c:out>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>

	<script src="<c:url value = "/resources/lib/jquery/jQuery-2.1.4.min.js" />"></script>
	<script src="<c:url value = "/resources/lib/jquery/jquery-ui.min.js" />"></script>
	<script src="<c:url value = "/resources/lib/admin_lte/bootstrap/bootstrap.min.js" />"></script>
	<script src="<c:url value = "/resources/lib/admin_lte/app.min.js" />"></script>
</body>
</html>