'use strict';

/**
 * Concept taken by http://jsfiddle.net/vishalvasani/4hqVu/
 * */


var PackageUploadCtrl = [ '$scope', '$log', 'pkgService', 'sharedPropertiesService', function($scope, $log, pkgService,sharedPropertiesService){
	
	$scope.filequeue = [];
	$scope.cancelRest = false;
	$scope.showSuccess = false;
	$scope.showError = false;
	$scope.message = '';
	$scope.resLargeFiles='';
	$scope.showLargeTab = false;
	
	
	var dropbox = document.getElementById("dropbox");
	$scope.dropText = 'Drop files here...';
	
	// init event handlers
	function dragEnterLeave(evt) {
	    evt.stopPropagation();
	    evt.preventDefault();
	    $scope.$apply(function(){
	    	$scope.dropText = 'Drop files here...';
	        $scope.dropClass = '';
	    });
	 }
	
	 dropbox.addEventListener("dragenter", dragEnterLeave, false);
	 dropbox.addEventListener("dragleave", dragEnterLeave, false);
	 
	 dropbox.addEventListener("dragover", function(evt) {
	        evt.stopPropagation();
	        evt.preventDefault();
	        var ok = evt.dataTransfer && evt.dataTransfer.types && evt.dataTransfer.types.indexOf('Files') >= 0;
	        $scope.$apply(function(){
	            $scope.dropText = ok ? 'Drop files here...' : 'Only files are allowed!';
	            $scope.dropClass = ok ? 'over' : 'not-available';
	        });
	    }, false);
	 
	 dropbox.addEventListener("drop", function(evt) {
	        $log.info('drop evt:', JSON.parse(JSON.stringify(evt.dataTransfer)));
	        evt.stopPropagation();
	        evt.preventDefault();
	        $scope.$apply(function(){
	            $scope.dropText = 'Drop files here...';
	            $scope.dropClass = '';
	        });
	        var files = evt.dataTransfer.files;
	        if (files.length > 0) {
	            $scope.$apply(function(){
	            	angular.forEach(files, function(elementfile){
	    	    		var fileDetail = {
	    		        		file :  elementfile,
	    		        		isValid : true,
	    		        		isUploading : false,
	    		        		isSaved : false,
	    		        		isFailed : false,
	    		        		status : ''
	    		         };
	    	    		validateFile(fileDetail);
	    	    		$scope.filequeue.push(fileDetail);
	    	    	});
	            });
	        }
	    }, false);
	
	/** Method to set files in queue */
	
	$scope.setFiles = function(element) {
	    $scope.$apply(function($scope) {
	    	angular.forEach(element.files, function(elementfile){
	    		var fileDetail = {
		        		file :  elementfile,
		        		isValid : true,
		        		isUploading : false,
		        		isSaved : false,
		        		isFailed : false,
		        		status : ''
		         };
	    		validateFile(fileDetail);
	    		$scope.filequeue.push(fileDetail);
	    	});
	      });
	    };
	    
	
			    
	/** Method to validate file */
	    
	function validateFile(fileDetail){
		$log.info('Validating File : '+fileDetail.file.name);
		if(!(fileDetail.file.type == 'application/x-gzip' || fileDetail.file.type == 'application/gzip' || fileDetail.file.type == 'application/x-zip-compressed')){
			fileDetail.isValid = false;
			fileDetail.status = 'Invalid File Type. Only tar.gz & zip Supported.';
			$log.error('Invalid File Type.');
		}
	};
	
	
	/** Method to upload all valid file */
	
	$scope.uploadAll = function(){
		$log.info('Validating File : '+fileDetail.file.name);
		angular.forEach($scope.filequeue, function(fileDetail){
			if(!$scope.cancelRest){
				$scope.upload(fileDetail);
			 }
		});
	};
	
		
	/** Method call for button */
	
	$scope.uploadLargeFiles = function(){
		$log.info('Upload uploadLargeFiles: ');	
		$scope.showLargeTab = true;
		pkgService.getLargePackage().then(
				function(responseData){
					if(!responseData.error){
						console.log(responseData.response);
						
					}else{
						console.log("failed in save");
					}
					$scope.resLargeFiles=responseData.response;
					pkgService.getLargeFileCount().then(
							function(responseData){
								if(!responseData.error){
									console.log("Count Call Success")
								}else{
									console.log("Count Call failed");
								}
								$scope.largeFileCount=responseData.response;
							},function(errorData){
								$log.error(errorData);
							}		
					
					);
				},function(errorData){
					$log.error(errorData);
				});
		
		
	};
	
/** Method to upload  all pending valid file */
	
	$scope.uploadPendingAll = function(){
		angular.forEach($scope.filequeue, function(fileDetail){
			if(!$scope.cancelRest){
				$scope.uploadPendingStart(fileDetail);
			 }
		});
	};
	/** Method to upload file */
	
	$scope.upload = function(fileDetail){
		if(fileDetail.isValid && !(fileDetail.isSaved || fileDetail.isFailed)){
			$log.info('Uploading : '+fileDetail.file.name);
			fileDetail.isUploading = true;
			$scope.wip = true;
			pkgService.addSupportPackage(fileDetail).then(
			function(responseData){
				if(!responseData.error){
					fileDetail.isSaved = true;
				}else{
					fileDetail.isFailed = true;
				}
				fileDetail.isUploading = false;
				$scope.wip = false;
				fileDetail.status = responseData.message;
			},function(errorData){
				$log.error(errorData);
				$scope.wip = false;
				fileDetail.isUploading = false;
			});
		}
	};

	/** Method to remove file from queue */
	
	$scope.remove = function(fileDetail){
		$log.warn('Removing : '+fileDetail.file.name);
		$scope.filequeue = $scope.filequeue.filter(function (el) {
            return el.file.name !== fileDetail.file.name;
         });
	};
	
	/** Method to reset queue and properties */
	
	$scope.reset = function(){
		$scope.filequeue = [];
		$scope.cancelRest = false;
		$scope.showSuccess = false;
		$scope.showError = false;
		$scope.message = '';
	};
	
	/** Method to check whether all file uploaded */
	
	$scope.isAllUploaded = function(){
		var i = 0;
		while(i < $scope.filequeue.length){
            if($scope.filequeue[i].isValid && !($scope.filequeue[i].isSaved || $scope.filequeue[i].isFailed)){
            	break;
            }
            i++;
        }
		if($scope.filequeue.length!=0 && i == $scope.filequeue.length){
			return true;
		}
		return false;
	};
	
/** Method to get large file count */
	
	$scope.init = function(){
		
		pkgService.getLargeFileCount().then(
				function(responseData){
					if(!responseData.error){
						console.log(responseData.response);
						$scope.largeFileCount=responseData.response;
						console.log("Count Call Success")
					}else{
						console.log("Count Call failed");
					}
				},function(errorData){
					$log.error(errorData);
				}		
		
		);
	};
	
	$scope.init();
	/** Method to get failed file count */
	
	$scope.getFailCount = function(){
		var failCount = 0;
		angular.forEach($scope.filequeue, function(fileDetail){
			if(!fileDetail.isValid || fileDetail.isFailed){
				failCount += 1;
			 }
		});
		return failCount;
	};
	
	/** Method to get saved file count */
	
	$scope.getSavedCount = function(){
		var savedCount = 0;
		angular.forEach($scope.filequeue, function(fileDetail){
			if(fileDetail.isSaved){
				savedCount += 1;
			 }
		});
		return savedCount;
	};
	
	$scope.$watch('filequeue.length', function(n, o){
    	if(n > o){
    		$scope.cancelRest = false;
    		$scope.showSuccess = false;
    		$scope.showError = false;
    		$scope.message = '';
    	}
    }, true);
}];