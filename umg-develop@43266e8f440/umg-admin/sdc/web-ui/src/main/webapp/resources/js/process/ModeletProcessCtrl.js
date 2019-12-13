'use strict';

var ModeletProcessCtrl = [ '$scope', '$log', 'modeletProcessService', function($scope, $log, modeletProcessService){

    $scope.modeletclientList = [];
    $scope.showSuccesssMessage = false;
    $scope.showFailureMessage = false;
    $scope.showMessage = '';
    $scope.processsList = [];
    $scope.showEditPopup = false;

    $scope.modeletTrackingId = function(modelet) {
        if(modelet != null)
            return modelet.host + ":" + modelet.port;
    };

    	$scope.getModeletClass = function(status){

    		var clazz = 'btn-default';

    		switch(status){
    			case 'Free' :
    			case 'Registered With System Default Pool' :
    				clazz = 'btn-success';
    				break;

                case 'Registration Inprogress' :
    				clazz = 'btn-info';
    				break;

    			case 'Failed' :
    			case 'Unavailable' :
    			case null :
    				clazz = 'btn-danger';
    				break;
    		}

    		return clazz;
    	};

    	$scope.getModeletDisbaledStatus = function(modelet){

            return modelet.executionLanguage.toUpperCase()=='MATLAB' || modelet.executionLanguage.toUpperCase()=='EXCEL' ||
            (modelet.executionLanguage.toUpperCase()=='R' && modelet.execEnvironment.toUpperCase()=='WINDOWS')
            || modelet.modeletStatus==null || modelet.modeletStatus.toUpperCase()=='FAILED' || modelet.modeletStatus.toUpperCase()=='UNAVAILABLE'
        };

    	$scope.showProcessList = function(modelet) {

            $scope.showSuccesssMessage = false;
            $scope.showFailureMessage = false;
            $scope.showMessage = '';
            $scope.processsList = [];
            $scope.showEditPopup = true;

    	    modeletProcessService.showProcessList(modelet).then(
				function(response){
					if(response.error == false) {
						$log.info('Request sent to show process of modelet: '+ modelet.host + ':'+ modelet.port);
						$scope.init();
						$scope.showSuccesssMessage = true;
						$scope.showMessage = "Process of modelet: "+ modelet.host + ":"+ modelet.port + " is successful. " ;
						angular.forEach(response.response, function(process){
                            if(process!=null) {
                                $scope.processsList.push(process);
                            }
                        });
					} else {
                        $log.info('Sending request failed for proces list of modelet: '+ modelet.host + ':'+ modelet.port);
                        $scope.showFailureMessage = true;
                        if (response.message.indexOf("[") != -1) {
                            $scope.showMessage = response.message.substring(response.message.indexOf("[")+1 , errorLength);
                        } else {
                            $scope.showMessage = response.message;
                        }
					}
				},
				function(error){
					$log.error('Error');
				}
    	    );

    	}

        $scope.closeNewModal= function() {
            $scope.showSuccesssMessage = false;
            $scope.showFailureMessage = false;
            $scope.showMessage = '';
            $scope.processsList = [];
            $scope.showEditPopup = false;
        };

    function setModeletClients(){
		modeletProcessService.getModeletClientDetails().then(
				function(responseData){
					$log.info('Received Modelet client Details.');

					if(!responseData.error) {
					    $scope.modeletclientList = [];
						angular.forEach(responseData.response, function(modeletClient){
						    if(modeletClient!=null) {
						        $scope.modeletclientList.push(modeletClient);
						    }
						});
					}
				},
				function(errorData){
					$log.error('Error while loading Modelet Pooling Details');
				}
		);
	};

	$scope.init = function(){
		setModeletClients();
	};

	$scope.init();



}];