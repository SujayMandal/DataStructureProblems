'use strict';

var AssignModeletCtrl = ['$scope', '$log', 'assignModeletService', function ($scope, $log, assignModeletService) {

    $scope.showMessage = function (content, cl, responseMessage) {
        $scope.message = (responseMessage != null && responseMessage != '') ? responseMessage : content;
        $scope.clazz = cl;
    };

    $scope.getModeletClass = function (status) {

        var clazz = 'text-default';

        switch (status) {
            case 'Free':
            case 'Registered With System Default Pool':
                clazz = 'text-success';
                break;

            case 'Registration Inprogress':
                clazz = 'text-info';
                break;

            case 'Failed':
            case 'Unavailable':
            case null:
                clazz = 'text-danger';
                break;
        }

        return clazz;
    };

    $scope.modeletRestartPopUp = false;
    $scope.showEditAssignPopup = false;

    $scope.processAssignment = function () {
        console.log(" params :" + $scope.editModeletData);
        $scope.showMessage('', '', null);

        assignModeletService.updateModeletProfilerLink($scope.editModeletData.hostName,
            $scope.editModeletData.port, $scope.editModeletData.modeletProfiler.id).then(
                function (responseData) {
                    $scope.init();
                    if (!responseData.error) {
                        console.log(responseData);
                        console.log("profile unlinked");
                        $scope.showMessage('Modelet profile linked Successfully.', 'text-left text-success', responseData.response);
                    } else {
                        $scope.showMessage('Modelet profile linking failed!', 'text-left text-danger', responseData.message);
                    }
                }, function (errorData) {
                    $log.error(errorData);
                    $scope.showMessage(errorData, 'text-left text-danger', null);
                }
            );
    };

    $scope.restartModeletPopUp = function () {
        $scope.showMessage('', '', null);
        $scope.modeletRestartPopUp = true;
    }

    $scope.downloadModeletLog = function (modelet) {
        $log.info("Request received to download Modelet logs.");
        assignModeletService.downloadModeletLogs({ host: modelet.hostName, port: modelet.port, rServePort: modelet.rServePort }).then(
            function (responseData) {
                $scope.init();
                if (!responseData.error) {
                    console.log(responseData);
                    $scope.showMessage('Logs downloaded succesfully.', 'text-left text-success', responseData.response);
                } else {
                    $scope.showMessage('Logs download failed!.', 'text-left text-danger', responseData.message);
                }
            }, function (errorData) {
                $scope.init();
                $log.error(errorData);
                $scope.showMessage(errorData, 'text-left text-danger', null);
            }
        );
    }

    $scope.restartModelet = function () {
        $log.info("Request received to restart Modelet.");

        let modeletReq = [];
        angular.forEach($scope.modeletParams, function (mdlt) {
            var key = mdlt.hostName + "-" + mdlt.port;
            if ($scope.modelets[key]) {
                mdlt.host = mdlt.hostName;
                mdlt.execEnvironment = mdlt.executionEnvironment;
                modeletReq.push(mdlt);
            }
        });

        assignModeletService.restartModelets(modeletReq).then(
            function (responseData) {
                $scope.init();
                if (!responseData.error) {
                    console.log(responseData);
                    $scope.showMessage('Modelet restarted succesfully.', 'text-left text-success', responseData.response);
                } else {
                    $scope.showMessage('Modelet restart failed!.', 'text-left text-danger', responseData.message);
                }
            }, function (errorData) {
                $scope.init();
                $log.error(errorData);
                $scope.showMessage(errorData, 'text-left text-danger', null);
            }
        );
        $log.info($scope.modelets);
    }

    $scope.modeletRestartActive = function () {
        $log.info("Modelet restart button check.");
        let restartModeletButton = true;
        angular.element(document.getElementById("restartModelBtn"))[0].disabled = false;
        for (let o of Object.values($scope.modelets)) {
            if (o) {
                restartModeletButton = false;
                break;
            }
        }
        angular.element(document.getElementById("restartModelBtn"))[0].disabled = restartModeletButton;
    }

    $scope.showEditAssignModelet = function (modelet) {
        $log.info("Request received to change assign Modelet.");
        $scope.showMessage('', '', null);
        $scope.editModeletData = modelet;
        $scope.showEditAssignPopup = true;
        $scope.modeletNames = $scope.modeletParams
    };

    $scope.init = function () {
        $scope.showMessage('', '', null);
        assignModeletService.getAllModelets().then(
            function (responseData) {
                if (!responseData.error) {
                    console.log(responseData);
                    $scope.modeletList = '';
                    $scope.dataParams = '';
                    $scope.modeletList = responseData;
                    $scope.modeletParams = $scope.modeletList.response;
                    console.log("modeletParams" + $scope.modeletParams);
                    $scope.modelets = [];
                    angular.forEach($scope.modeletParams, function (tmn) {
                        var key = tmn.hostName + "-" + tmn.port;
                        $scope.modelets[key] = false;
                    });
                }
            }, function (errorData) {
                $log.error(errorData);
            }

        );

        assignModeletService.getModeletProfilers().then(
            function (responseData) {
                if (!responseData.error) {
                    console.log(responseData);
                    $scope.profilerList = '';
                    $scope.profilerList = responseData.response;
                    console.log("modelet profilers : " + $scope.profilerList);
                }
            }, function (errorData) {
                $log.error(errorData);
            }

        );
        let restartButtonElementDoc = angular.element(document.getElementById("restartModelBtn"));
        if(restartButtonElementDoc != undefined && restartButtonElementDoc.length > 0) {
            restartButtonElementDoc[0].disabled = true;
        } 

    }

    $scope.init();

}];
