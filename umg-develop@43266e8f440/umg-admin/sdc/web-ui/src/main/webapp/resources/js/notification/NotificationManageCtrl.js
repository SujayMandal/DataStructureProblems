'use strict';

var NotificationManageCtrl = ['$state','$scope','$http','$log','$location','$dialogs','$window','notificationManageService','sharedPropertiesService',
                              function($state,$scope, $http, $log,$location,$dialogs,$window,notificationManageService,sharedPropertiesService) {
	
	$scope.eventListData = [];
	$scope.selectedRowData = {};
	$scope.responseMsg = '';
	$scope.responseMsgFlag = false;
	$scope.errorResponseBackground = 'background-color: #dd4b39; color:white;';
	$scope.successResponseBackground = 'background-color: #00a65a; color:white;';
	$scope.responseBackground = '';
	
	var columnDefs = [
	                  {field: "eventName", headerName: "Notification Name", unSortIcon: true},
	                  {field: "eventName", headerName: "Event Name", unSortIcon: true},
	                  {field: "lastUpdatedBy", headerName: "Last Updated By", unSortIcon: true},
	                  {field: "lastupdatedOnDate", headerName: "Last Updated On", unSortIcon: true, cellRenderer: renderNull}
	                  ];
	
	function rowSelectedFunc(event){
		$scope.selectedRowData = event;
	}
	
	function renderNull(params){
		if(params.value==0){
			params.value=null;
		}
		return params.value;
	}
	
	$scope.gridOptions ={
			rowHeight: 24,
			headerRowHeight:24,
	        rowData: 'eventListData',
	        rowSelection: 'single',
	        enableFilter: true,
	        enableColResize: true,
	        enableSorting: true,
	        rowSelected: rowSelectedFunc,
	       	columnDefs : columnDefs
		};
	
	//calling function on page load to get feature event list
	getNotificationEventList();
	
	var w = angular.element($window);
	$scope.$watch(function () {
		return {
			'h': w.height(), 
			'w': w.width()
		};
	}, function (newValue, oldValue) {
		$scope.gridOptions.api.sizeColumnsToFit();
	}, true);   
	
	function getNotificationEventList(){
		notificationManageService.getEventList().then(
				function(responseData){
					if(!responseData.error){
						$scope.eventListData = responseData.response;
						$scope.gridOptions.rowData = $scope.eventListData;
						$scope.gridOptions.api.onNewRows();
						$scope.gridOptions.api.selectIndex(0);
						$scope.gridOptions.api.refreshView();
						$scope.gridOptions.api.sizeColumnsToFit();
					}
					else{
						$log.error(responseData.message);
						$scope.responseMsg = responseData.message;
						$scope.responseMsgFlag = true;
						$scope.responseBackground = $scope.errorResponseBackground;
					}
				},
				function(errorData){
					$log.error(errorData + ": http failure");
					$scope.responseMsg = 'Connection Failure. Data could not be fetched. Try again in some time or contact RA support at RealAnalyticsSupport@altisource.com for assistance';
					$scope.responseMsgFlag = true;
					$scope.responseBackground = $scope.errorResponseBackground;
				});
	}
	
	$scope.editEvent = function(){
		$log.info("Redirecting to Edit notification screen.");
		sharedPropertiesService.put("editNotificationInfo",$scope.selectedRowData);
		//$location.path('notificationEdit');
		$state.go('notificationEdit');
	}
	
	$scope.deleteEvent = function(){
		$scope.responseMsg = '';
		$scope.responseMsgFlag = false;
		var dlg = $dialogs.confirm('Please Confirm','<span class="confirm-body">Are you sure to delete the ' + $scope.selectedRowData.eventName + ' notification for ' + $scope.selectedRowData.tenantCode + ' tenant? <br> Delete will take effect immediately.</span>');
		dlg.result.then(function(btn){
			notificationManageService.deleteEvent($scope.selectedRowData.id).then(
					function(responseData){
						if(!responseData.error){
							getNotificationEventList();
							$scope.responseMsg = 'Notification deleted successfully';
							$scope.responseMsgFlag = true;
							$scope.responseBackground = $scope.successResponseBackground;
						}
						else{
							$log.error("error in deleting event");
							$scope.responseMsg = responseData.message;
							$scope.responseMsgFlag = true;
							$scope.responseBackground = $scope.errorResponseBackground;
						}
					},
					function(errorData){
						$log.error(errorData + ": http failure");
						$scope.responseMsg = 'Delete failed. Try again in some time or contact RA support at RealAnalyticsSupport@altisource.com for assistance';
						$scope.responseMsgFlag = true;
						$scope.responseBackground = $scope.errorResponseBackground;
					});
		});
	}

}];