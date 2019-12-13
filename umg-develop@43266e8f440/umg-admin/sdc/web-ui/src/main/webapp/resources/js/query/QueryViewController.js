'use strict';
var QueryViewController = ['$scope', '$location', '$filter', 'queryEditorService', 'sharedPropertiesService',  function($scope, $location, $filter, queryEditorService, sharedPropertiesService) {
	//this flag identify the mapping is used any published or deactivated version 
	$scope.publishedOrDeactivated=false;
	$scope.meassage="";
	$scope.meassageClass="error-msg";
	$scope.grid={};
	$scope.grid.pagedData ={};
	$scope.grid.selectedItems=[];
	$scope.grid.totalServerItems=0;
	$scope.grid.columnDefs=[
	                        {field:"name", displayName:" Query Name ", width: "120px"},
	                        {field:"description", displayName:" Description "},
	                        {field:"execSequence", displayName:"Sequence", width: "120px"},
	                        {field:"createdBy", displayName:" Created By ", width: "160px"},
	                        {field:"createdOn", displayName:" Created Time ",  width: "150px"},
	                        {field:"lastModifiedBy", displayName:" Updated By ", width: "150px"},
	                        {field:"updatedOn", displayName:" Updated Time ", width: "120px"},
	                        {field:"col3", displayName:" ", width: "50px" ,cellClass: "cellCentralPos", cellTemplate: 'resources/partial/query/grid/cell-view-template.html'},
	                        {field:"col4", displayName:" ", width: "50px" ,cellClass: "cellCentralPos", cellTemplate: 'resources/partial/query/grid/cell-edit-template.html'}
	];
	$scope.grid.pagingOptions = {
            pageSizes: [10, 50, 100, 250],
            pageSize: 10,
            currentPage: 1,
            totalPages:1
			}; 	
	$scope.queryViewGridOptions = { 
			 	headerRowHeight: 18,
			 	rowHeight:20,
				data: 'grid.pagedData', 
				pagingOptions: $scope.grid.pagingOptions,
				totalServerItems: 'grid.totalServerItems',
				columnDefs: 'grid.columnDefs',
				multiSelect: true,
				selectedItems: $scope.grid.selectedItems,
				enablePaging: true,
				showFilter:true,
				footerTemplate:'resources/partial/query/query-list-footer.html'	
	}; 
	
	//get the save button status from tid mapping page
	var saveButtonListpageStatus = sharedPropertiesService.get('hideSaveButtonListPage');
	sharedPropertiesService.remove('hideSaveButtonListPage');
	
	$scope.queryDataList=[];
	$scope.query={};
	$scope.sortableArray=[];
	$scope.queryLaunchInfo={};
	$scope.versionId="";
	$scope.versionNo="";
	 /**==========================================
	  * watch for additional value from calling page
	  * */
	$scope.queryLaunchInfo=angular.copy(sharedPropertiesService.get("queryLaunchInfo"));

	
	 /**==========================================
	  * The orignal ng-grid has a problem, the grid will not search multiple pages
	  * The following watch will resolved this problem.
	  * but this watch will not work, if multiple ng-grids are there in the same page
	  * searching option for all the pages
	  */
	    $scope.$watch('queryViewGridOptions.$gridScope.filterText', function (newVal, oldVal) {
	     if ((oldVal!=undefined || oldVal=="") && newVal !== oldVal) {
	    	 //alert("newVal, oldval=" +newVal+","+ oldVal);
	    	 $scope.filteredQueryDataList  = $filter('filter')($scope.queryDataList, newVal);
	    	 $scope.totalServerItems = $scope.filteredQueryDataList.length;
	    	 $scope.grid.pagingOptions.totalPages=Math.ceil($scope.totalServerItems/$scope.grid.pagingOptions.pageSize);
			 $scope.setPagingData($scope.filteredQueryDataList, $scope.grid.pagingOptions.currentPage, $scope.grid.pagingOptions.pageSize); 
	     }
	     }, true);
	 
	
		 /**==========================================
		  * initial setup for default values
		  */
		 $scope.initSetup=function(){
			$scope.versionId = sharedPropertiesService.get("versionId");
			$scope.versionNo = sharedPropertiesService.get("versionNo");
			sharedPropertiesService.remove("versionId");
			sharedPropertiesService.remove("versionNo");
		 };	
	    /**==========================================
	     * Method to fetch all query details 
	     */
		 $scope.fetchQueryList = function(TIDName){	
			 if(TIDName!=undefined || TIDName!="" ){
			 queryEditorService.fetchQueryList(TIDName).then( function(responseData) {
					 if(responseData.error){
						 $scope.meassageClass="error-msg";
						 $scope.meassage=" ErrorCode: "+responseData.errorCode+" <BR> Error in loading data : " +responseData.message.replace("\n", "<BR>");
					 }else{
						$scope.queryDataList = responseData.response.allQueries;
						$scope.publishedOrDeactivated=responseData.response.publishedOrDeactivated;
						$scope.setPaging();
					 }			  				 
				 } );	
			 }else{
				 $scope.meassageClass="error-msg";
				 $scope.meassage=" TIDName is not received properly from the mapping service, can not load the data ";
			 }
			 $scope.setupVerIdandNo();
		 };
		 /**==========================================
		  *  Method to display the view query screen
		  */
		 $scope.view=function(row){
				var  queryObj={};
				queryObj["queryInfo"]=row.entity;
				queryObj["queryLaunchInfo"]=$scope.queryLaunchInfo;
	            sharedPropertiesService.put("viewQuery",queryObj);
	          //fixing for bug UMG-1447
	            if (saveButtonListpageStatus == true) {
					sharedPropertiesService.put('hideSaveButton',true);
				}
	            sharedPropertiesService.put("publishedOrDeactivated",$scope.publishedOrDeactivated);
	            $scope.setupVerIdandNo();
	            $location.path('queryEditor');
	            
		 };
		 /**==========================================
		  * Method to display the create query screen
		  */
		 $scope.create=function(){
		if($scope.publishedOrDeactivated==false){
			sharedPropertiesService.put("queryInputMap",$scope.queryLaunchInfo);
			//fixing for bug UMG-1447
			if (saveButtonListpageStatus == true) {
				sharedPropertiesService.put('hideSaveButton',true);
			}
			$scope.setupVerIdandNo();		
			$location.path("queryEditor");
		 }
			
		 };
		 /**==========================================
		  *  Method to display the edit query screen
		  */
		 $scope.edit=function(row){
			var queryObj={};
			queryObj["queryInfo"]=row.entity;
			queryObj["queryLaunchInfo"]=$scope.queryLaunchInfo;
            sharedPropertiesService.put("editQuery",queryObj);
            $scope.setupVerIdandNo();
            sharedPropertiesService.put("publishedOrDeactivated",$scope.publishedOrDeactivated);
            //fixing for bug UMG-1447 
            if (saveButtonListpageStatus == true) {
				sharedPropertiesService.put('hideSaveButton',true);
			}
            
            $location.path('queryEditor');
		 };
		 
		 /**==========================================
		  *           SORTABLE CODE START HERE
		  ========================================== */
		 
		 /**==========================================
		  * This method will open the sequencing dialog box 
		  */
		 $scope.sequencing=function(){
			 $scope.sortableArray=angular.copy($scope.queryDataList);
			 var height=800;
			 if($scope.queryDataList.length<10){
				 height=200+(45*$scope.queryDataList.length);
			 }
		 };
		 
		 $scope.showMapping = function(){
			 sharedPropertiesService.put("tidCallingType","edit");
			 sharedPropertiesService.put("tidName",$scope.queryLaunchInfo.tidName);
			 sharedPropertiesService.put("publishedOrDeactivated",$scope.publishedOrDeactivated);
			 //setting the save button status if it was set as true when this page was visited 
			 if (saveButtonListpageStatus == true) {
					sharedPropertiesService.put('hideSaveButton',true);
			}
			 $scope.setupVerIdandNo();
			 $location.path('addTid');
		 };
		 
		 $scope.dragStart = function(e, ui) {
		        ui.item.data('start', ui.item.index());
		    };
		    
		 $scope.dragEnd = function(e, ui) {
		        var start = ui.item.data('start'), end = ui.item.index();
		        $scope.sortableArray.splice(end, 0, $scope.sortableArray.splice(start, 1)[0]);
		        $scope.$apply();
		    };
		        
		 $('#sortable').sortable({
		        start: $scope.dragStart,
		        update: $scope.dragEnd
		    	});
		 
		 $scope.saveSequence = function(e, ui) {
			 var sequencedQueries=[];
			 var sequence=1;
			 for(var index in $scope.sortableArray){
				 var sQuery=$scope.sortableArray[index];
				 var obj={};
				 obj.id=sQuery.id;
				 obj.name=sQuery.name;
				 obj.execSequence=sequence;
				 sequencedQueries[sequence-1]=obj;
				 sequence++;
			 }
			 
			 queryEditorService.saveSequence(sequencedQueries).then( function(responseData) {
				 if(responseData.error){
					 $scope.meassageClass="error-msg";
					 $scope.meassage=" ErrorCode: "+responseData.errorCode+" <BR> Error in loading data : " +responseData.message.replace("\n", "<BR>");
				 }else{
					 $scope.meassageClass="success-msg";
					 $scope.meassage=" Sequence has been updated successfully...";
					 $scope.fetchQueryList($scope.queryLaunchInfo.tidName);
				 }
				 $scope.setupVerIdandNo();
			 } );
			 
		       
		    };

			 /**==========================================
			  *           SORTABLE CODE END HERE
		      ========================================== */
	 
	// PAGING RELATED
	    /**==========================================
	     * this method should be called for pagination
	     */
		 $scope.setPaging = function(){	
			 $scope.totalServerItems = $scope.queryDataList.length;
			 $scope.grid.pagingOptions.totalPages=Math.ceil($scope.totalServerItems/$scope.grid.pagingOptions.pageSize);
			 $scope.setPagingData($scope.queryDataList, $scope.grid.pagingOptions.currentPage, $scope.grid.pagingOptions.pageSize); 
		 };
		 /**==========================================
		  * page change handler
		  */
		 $scope.$watch('grid.pagingOptions', function(newVal, oldVal) {
		if (newVal !== oldVal) {
			// page change
			if (newVal.currentPage !== oldVal.currentPage) {
				if (newVal.currentPage <= 0) {
					newVal.currentPage = oldVal.currentPage;
				} else if (newVal.currentPage > newVal.totalPages) {
					newVal.currentPage = oldVal.currentPage;
				}
				$scope.setPaging();
			}
			
			}
		 }, true);
		 /**
			 * ========================================== triming the data based
			 * on the page number
			 */
		 $scope.setPagingData = function(data, page, pageSize){	
			 var myPagedData = data.slice((page - 1) * pageSize, page * pageSize);
		     $scope.grid.pagedData = myPagedData;
		    // $scope.grid.totalServerItems = data.length;
		     if (!$scope.$$phase) {
		         $scope.$apply();
		     }
		 };	 
		 /**==========================================
		  * first page
		  */
		 $scope.firstPage=function(){
			 $scope.grid.pagingOptions.currentPage=1;
		 };	
		 /**==========================================
		  * next page
		  */
		 $scope.nextPage=function(){
			 
			 if($scope.grid.pagingOptions.totalPages>$scope.grid.pagingOptions.currentPage){
				 $scope.grid.pagingOptions.currentPage=$scope.grid.pagingOptions.currentPage+1;
				 }
		 };	
		 /**==========================================
		  * previous page
		  */
		 $scope.previousPage=function(){
			 if($scope.grid.pagingOptions.currentPage>1){
			 $scope.grid.pagingOptions.currentPage=$scope.grid.pagingOptions.currentPage-1;
			 }
		 };	
		 /**==========================================
		  * last page
		  */
		 $scope.lastPage=function(){
			 $scope.grid.pagingOptions.currentPage=$scope.grid.pagingOptions.totalPages;
		 };	
		 /**==========================================
		  * page changed
		  */
		 $scope.pageSizeChanged=function(){
			 $scope.grid.pagingOptions.currentPage=1;
			 $scope.setPaging(); 
		 };		
		 
		 $scope.setupVerIdandNo=function(){
			 sharedPropertiesService.put("versionId",$scope.versionId);
			 sharedPropertiesService.put("versionNo",$scope.versionNo);				
		 };	
	 //------------------------------------------------
	 //Default execution methods while loading the controller
	 $scope.initSetup(); 
	 if($scope.queryLaunchInfo==undefined || $scope.queryLaunchInfo.tidName==undefined || $scope.queryLaunchInfo.tidName=="" ){
		 $scope.meassageClass="error-msg";
		 $scope.meassage=" TIDName is not received properly from the mapping service, can not load the data ";
	 }else{
		 $scope.fetchQueryList($scope.queryLaunchInfo.tidName); 
	 }
	 
}];