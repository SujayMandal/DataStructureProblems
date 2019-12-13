'use strict';

var PackageViewCtrl = ['$scope', '$log', '$timeout', '$location', '$window', 'pkgService', 'sharedPropertiesService', function($scope, $log, $timeout, $location, $window, pkgService,sharedPropertiesService){
	
	$scope.envs = [];
	$scope.addons = [];
	$scope.pagedPackages = [];
	
	$scope.selectedEnv = '';
	$scope.selectedAddon = '';
	
	$scope.addonTotalRecords = 10;
	$scope.addonMaxPages = 1;
	
	$scope.message = [];
	$scope.clazz = [];
	
	$scope.addonPagingOptions = { pageSizes: [10, 20, 50], pageSize: 50, currentPage: 1};
	
	var listPackage = null;
	
	
	$scope.showMessage = function(content, cl) {
		$scope.message = content;
		$scope.clazz = cl;
		$timeout(function() {
			$scope.$apply('message = []');
			$scope.$apply('clazz = []');
		}, 10000);
	};
	
	$scope.pageInfo = {
			"searchString": '',
			"pageSize": $scope.addonPagingOptions.pageSize,
			"page": $scope.addonPagingOptions.currentPage,
			"sortColumn": 'createdDate',
			"descending": false
	};
	
	$scope.setEnvironments = function(){
		$log.info('Setting All Environments ...');	
		pkgService.getAllEnv().then(
				function(responseData){
					if(!responseData.error){
						$scope.envs = responseData.response;
						$log.info('Setting All Add-ons for Environment : '+ $scope.envs[0]);
						if($scope.selectedEnv == '') {
							$scope.selectedEnv = $scope.envs[0];
							$log.info('Setting All Add-ons for Environment : '+ $scope.selectedEnv);
							setAddons();
						}
					}
				},
				function(errorData){
					$scope.envs = [];
					$log.error('Error');				
				}
		);
	};
	
	
	var setAddons = function(){
		$log.info('Setting All Add-ons for Environment : '+ $scope.selectedEnv);
		pkgService.getAllAddons($scope.selectedEnv).then(
				function(responseData){
					if(!responseData.error){
						if(responseData.response.length > 0){
							var spn = [];
							angular.forEach(responseData.response, function(addon){
								spn.push(addon);
							});
							$scope.addons = spn;
							//commented this to remove null entry in array
							 if($scope.selectedAddon == '') 
								$scope.selectedAddon = 'ALL';
						}else{
							$log.error('No Package Folder Available');
							$scope.showMessage('No Package Folder Available','alert alert-error');
						}
					}else{
						$log.error(responseData.message);
						$scope.showMessage(responseData.message,'alert alert-error');
					}
				},
				function(errorData){
					$log.error("Error came with : "+errorData);
					$scope.showMessage(errorData,'alert alert-error');
				}
		);
	};
	
	$scope.setPackages = function(){
		$log.info('Setting Paged Packages for Environment : '+$scope.selectedEnv+' and Support Package : '+$scope.selectedAddon);
		if($scope.selectedEnv != '' && $scope.selectedAddon != ''){
		pkgService.getPagedPackages($scope.selectedEnv, $scope.selectedAddon, $scope.pageInfo).then(
				function(responseData){
					if(!responseData.error){
						var pagedPackages = [];
						angular.forEach(responseData.response.content, function(packageInfo){
							var pi = {
									id : packageInfo.id,
									packageName : packageInfo.packageName,
									environmentVersion : packageInfo.packageVersion,
									executionEnvironment : packageInfo.execEnv,
									createdDateTime : packageInfo.createdDateTime,
									createdBy : packageInfo.createdBy
							};
							pagedPackages.push(pi);
						});
						$scope.pagedPackages = pagedPackages;
						$scope.addonTotalRecords = responseData.response.totalElements;
						$scope.addonMaxPages = responseData.response.totalPages;
						setTimeout(function(){
				            $(window).resize();
				            $(window).resize();
				        }, 0);
					}else{
						$log.error('No Package Available for Environment : '+$scope.selectedEnv+' and Support Package : '+$scope.selectedAddon);
						$scope.showMessage('No Package Available for Environment : '+$scope.selectedEnv+' and Support Package : '+$scope.selectedAddon,'alert alert-error');
					}
				},
				function(errorData){
					$log.error("Error came with : "+errorData);
					$scope.showMessage(errorData,'alert alert-error');
				}
		);
		
		}
	};
	
	$scope.selectedPackages = [];
	
	$scope.gridOptions = {
	        data: 'pagedPackages',
	        multiSelect : true,
	        selectedItems: $scope.selectedPackages,
	        showSelectionCheckbox: true,
	        selectWithCheckboxOnly: true,
			enableColumnResize : true,
			headerRowHeight: 18,
	        rowHeight: 20,
	        columnDefs: [{field:'packageName', displayName:'Package',
	        			  cellTemplate : '<a ng-href="executionPackage/downloadSupportPackage/{{row.entity.id}}">{{row.entity.packageName}}</a>'}, 
	                     {field:'environmentVersion', displayName:'Package Version'},
	                     {field:'executionEnvironment', displayName:'Execution Environment'},
	                     {field:'createdDateTime', displayName:'Added On'},
	                     {field:'createdBy', displayName:'Added By'}
	                     ]
	    };
	
	$scope.sortPackages = function(sortColumn, sortDirection){
		$scope.pageInfo.sortColumn = sortColumn;
		$scope.pageInfo.descending = sortDirection;
		$scope.setPackages();
	};
	
	$scope.addPackage = function(){
		$log.info('Requested received to add another package under : '+$scope.selectedEnv+'/'+$scope.selectedAddon);
		var groupInfo = {environment: $scope.selectedEnv, groupName: $scope.selectedAddon};
		sharedPropertiesService.put("addPackage",groupInfo);
		$location.path('/addPackage');
	};
	
	$scope.downloadPackages = function(){
		$log.info('Request received to download '+$scope.selectedPackages.length+' packages.');
		var ids = '';
		angular.forEach($scope.selectedPackages, function(selectedPackage){
			ids += selectedPackage.id + ',';
		});
		$window.location.href = 'executionPackage/downloadSupportPackage/'+ids;
	};
	
	$scope.$watch('selectedPackages', function(n, o){
		if($scope.selectedPackages.length != $scope.pagedPackages.length) $scope.deselectCheckBox();
	}, true);
	
	$scope.deselectCheckBox = function(){
		 var selectAllHeader = angular.element(".ngSelectionHeader").scope();
		 if(selectAllHeader) selectAllHeader.allSelected = false;
	 };
	
	// ---------- Watch to monitor search string ---------------------
	
	$scope.$watch('pageInfo.searchString', function(n, o){
		if(n != o && n == '') $scope.setPackages();
	}, true);
	
	
	// ---------- Watch to monitor change in selected Add-on ---------
	
	$scope.$watch('selectedAddon', function(n, o){
		if(n != o) $scope.setPackages();
	}, true);
	
	
	
	// ---------------- Pagination related functions ------------------
	
	$scope.$watch('addonPagingOptions', function (newVal, oldVal) {
		if(newVal.currentPage * newVal.pageSize >= $scope.addonTotalRecords){
			$scope.addonPagingOptions.currentPage =  Math.ceil($scope.addonTotalRecords / $scope.addonPagingOptions.pageSize);
		}
		if(newVal.currentPage * newVal.pageSize <= 0){
			$scope.addonPagingOptions.currentPage = 1;
		}
    	if (newVal.currentPage !== oldVal.currentPage || newVal.pageSize !== oldVal.pageSize) {
    		$scope.pageInfo.pageSize = newVal.pageSize;
    		$scope.pageInfo.page = newVal.currentPage;
    		$scope.setPackages();
        }
    }, true);
	
	$scope.setNextPage = function(){
		var page = $scope.addonPagingOptions.currentPage;
        if ($scope.addonTotalRecords > 0) {
            $scope.addonPagingOptions.currentPage = Math.min(page + 1, $scope.addonMaxPages);
        } else {
            $scope.addonPagingOptions.currentPage++;
        }
	};
	
	$scope.setPreviousPage = function(){
		var page = $scope.addonPagingOptions.currentPage;
        $scope.addonPagingOptions.currentPage = Math.max(page - 1, 1);
	};
	
	//------ Method call on Page Load --------------
	
	listPackage = sharedPropertiesService.get("listPackage");
    if(listPackage != null){
    	if(listPackage.groupName != ''){
	    	$scope.selectedAddon = listPackage.groupName;
	    	$scope.selectedEnv = listPackage.environment;
	        sharedPropertiesService.remove("listPackage");
	        $scope.setPackages();
        }
    }
    
    $scope.$watch('selectedEnv', function(n, o){
		if(n != o) 
		setAddons();
		$scope.setPackages();
	}, true);
    
	$scope.setEnvironments();
}];