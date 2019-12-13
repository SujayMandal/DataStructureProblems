/**
 * Batch Transaction dashBoard controller 
 */
'use strict';
var PluginController = ['$scope', '$filter', 'pluginService', function($scope, $filter, pluginService) {
	$scope.showMessage="";
	$scope.message="";
	/**
	 * this map contains the plugin details of the tenant
	 */ 
	$scope.pluginMap={};
	/**
	 * oneAtATime value decides all the tabs can be open at a time or not 
	 */  
	$scope.oneAtATime = false;
	/**
	 * initial value to be set here if required
	 */  
	$scope.intialSetup=function(){
		};

	/**
	 * this method will loading all plugins for a tenant
	 */  
	  $scope.getAllPlugins =function(){    	
			pluginService.getAllPlugins().then( function(responseData) {
					 if(responseData.error){
						 $scope.showMessage=true;
						 $scope.showMessage=responseData.message.replace("\n", "<BR>");
					 }else{
						 $scope.pluginMap=responseData.response;
						 $scope.showMessage=false;
					 }
			 }, 
			 function(responseData) {
				 alert('System Failed: ' + responseData);
			 });	
	    };
	
	/**
	 * The default methods invoked on load
	 */    
	$scope.intialSetup();
	$scope.getAllPlugins();
	
	/** Below code will support R model upload process */
	
	$scope.rModel = {tar : '', version : '', checksum : ''};
	var BYTESIZE = 1024;
	$scope.progressVisible = false;
	
	function setRVersions(){
		pluginService.getRVersions().then(
		function(responseData){
			if(!responseData.error){
				$scope.versions = responseData.response;
				$scope.rModel.version = $scope.versions[0];
			}
			else{
				$scope.err = true;
				$scope.msg = 'Unable to find R versions.';
			}
		}, 
		function(errorData){
			$scope.err = true;
			$scope.msg = 'Unable to find R versions.';
		});
	};
	
	function validateClientSide(rModel){
		if(angular.isUndefined(rModel.tar) || rModel.tar == ''){
			$scope.err = true;
			$scope.msg = 'Please upload valid tar.gz';
			return false;
		}
		if(!(rModel.tar.type == 'application/x-gzip' || rModel.tar.type == 'application/gzip')){
			$scope.err = true;
			$scope.msg = 'Invalid file type. Only tar.gz supported.';
			return false;
		}
		if(angular.isUndefined(rModel.checksum) || rModel.checksum == ''){
			$scope.err = true;
			$scope.msg = 'Please provide checksum for tar';
			return false;
		}
		return true;
	};
	
	$scope.validateServerSide = function(){

		if(validateClientSide($scope.rModel)){
			var metadata = new FormData();
			metadata.append('tarName',$scope.rModel.tar.name);
			metadata.append('checksum',$scope.rModel.checksum);
			pluginService.doServerSideValidation(metadata).then(
				function(responseData){
					$scope.err = responseData.error;
					$scope.msg = responseData.message;
					if(!$scope.err){
						uploadRModel();
					}
				},
				function(errorData){
					$scope.err = responseData.error;
					$scope.msg = responseData.message;
				});
		}
		
	};
	
	function uploadRModel(){
		
		$scope.err = false;
		$scope.msg = '';
		$scope.progressVisible = false;

				var fd = new FormData();
				fd.append('tar', $scope.rModel.tar);
				fd.append('version', $scope.rModel.version);
				fd.append('checksum', $scope.rModel.checksum);
		        var xhr = new XMLHttpRequest();
		        xhr.upload.addEventListener("progress", uploadProgress, false);
		        xhr.addEventListener("load", uploadComplete, false);
		        xhr.addEventListener("error", uploadFailed, false);
		        xhr.addEventListener("abort", uploadCanceled, false);
		        xhr.open("POST", "fileUpload/rUpload");
		        $scope.progressVisible = true;
		        xhr.send(fd);
	};
	
	$scope.resetRUploadForm = function(){
		$scope.rModel = {tar : '', version : '', checksum : ''};
		$scope.msg = '';
		$scope.progressVisible = false;
		setRVersions();
	};
	
	function uploadProgress(evt) {
        $scope.$apply(function(){
            if (evt.lengthComputable) {
                $scope.loaded = getUserReadableSize(evt.loaded);
                $scope.total = getUserReadableSize(evt.total);
                $scope.progress = Math.round(evt.loaded * 100 / evt.total);
            } else {
                $scope.progress = 'unable to compute';
            }
        });
    };
    
    function getUserReadableSize(fileSizeInBytes){
    	var sizeWithUnit;
    	if(fileSizeInBytes / BYTESIZE > BYTESIZE){
        	if((fileSizeInBytes / BYTESIZE) % BYTESIZE > BYTESIZE){
        		sizeWithUnit = $filter('number')(fileSizeInBytes / Math.pow(BYTESIZE, 3), 2) + ' GB';
        	}else{
        		sizeWithUnit = $filter('number')(fileSizeInBytes / Math.pow(BYTESIZE, 2), 2)+ ' MB';
        	}
        }else{
        	sizeWithUnit = $filter('number')(fileSizeInBytes / BYTESIZE, 2) + ' KB';
        }
    	return sizeWithUnit;
    };
    
    function uploadComplete(evt) {
    	evt.target.status == 500 ? $scope.err = true : $scope.err = false;
    	$scope.msg = evt.target.responseText;
    };
    
    function uploadFailed(evt) {
    	$scope.progressVisible = false;
    	$scope.err = true;
		$scope.msg = 'Error in loading. Please try again ... ';
    };
    
    function uploadCanceled(evt) {
        $scope.$apply(function(){
            $scope.progressVisible = false;
        });
        $scope.err = true;
		$scope.msg = evt.target.responseText;
    };
    
    $scope.$watch('rModel', function (n, o) {
    	if(n != o){
    		 $scope.progressVisible = false;
    		 $scope.err = false;
    		 $scope.msg = '';
    	}
    }, true);
    
    setRVersions();
}];
