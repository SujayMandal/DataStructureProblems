'use strict';

var CacheDetailsCtrl = [ '$scope', '$log', 'cacheDetailsService', function($scope, $log, cacheDetailsService){

	
$scope.init = function(){
	cacheDetailsService.getAllSystemParameters().then(
				function(responseData){
					if(!responseData.error){
						console.log(responseData);
						$scope.output=responseData;
						}
				},function(errorData){
					$log.error(errorData);
				}		
		
		);
	cacheDetailsService.getIndexes().then(
			function(responseData){
				if(!responseData.error){
					console.log(responseData);
					$scope.indexData=responseData;
					$scope.tenant_codes =Object.keys($scope.indexData.response); 
					$scope.index_values =Object.values($scope.indexData.response)[0]; 
					$scope.index_keys = Object.keys($scope.index_values);
					
					}
			},function(errorData){
				$log.error(errorData);
			}		
	
	);
		
	};
	
	$scope.init();
}];