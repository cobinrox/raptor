//
// Angular module
// see https://code.angularjs.org/
//
// @param app name/alias aka our scope to help distinguish our app from other apps provided by other third parties
// @param [ ] extra AJS module name dependencies; note that the AJS files that contain these dependencies must
//            also be defined in the corresponding HTML file using this JS module
// ngMessages: prints messages to the document
// ngResource: provides http getter
var egorApp = angular.module('egorApp', ['ngMessages','ngResource']);

// Controller
// @param name for controller
// @param function/execution code or
//  array of dependencies followed by the function/execution
// the latter signature is preferred so that this file can be minified properly
// @param $scope injected by anular, global session object
// @param $log injected by angular, logging service
// @param $filter injected by angular, string manipulator service
// @param $resource injected by angular, http data getter
// @param timeout injected by angular, timer capability
//
//
egorApp.controller('egorController', ['$scope','$log','$filter','$resource','$http','$timeout',
    function($scope, $log, $filter,$resource,$http,$timeout) {
        $log.info("In egorController!");
        //console.log("In mainController but using non-angular log!");
        $scope.g_projectName = $filter('uppercase')('Egor Project');
        
        $scope.g_author = 'EGOR';

        $scope.g_moveCmd = '';

        $scope.g_volts = 512;
        $scope.g_ramp  = 512;
        $scope.g_vr_min = 0;
        $scope.g_vr_max = 2047;
        $scope.g_vr_step = 512/4;

        $scope.g_audioFileName = '';
        $scope.g_moveResponse = '';
        $scope.g_httpResponseData = '';
        $scope.g_httpStatusCode = '';
        $scope.g_error='';
        $scope.f_lowerCaseIt = function(){
           return $filter('lowercase')($scope.g_author);
        };


        $scope.f_adhocClick = function()
        {
            $log.info("IN adhocClick: " + $scope.g_moveCmd);
            if($scope.g_moveCmd == 'hi' ) $log.info("HI HIHIHIHIHIHIHIHIHIHIHIHIHI");
            f_sendMoveCmd($scope.g_moveCmd);
        };
        //
        // Handle one of the MOVE buttons click
        // @param l_moveCmd String F,B,FL,FR,BL,BR,BRAKE
        //
        $scope.f_moveClick = function(l_moveCmd){
           $log.info("in doCmd and l_moveCmd is [" + l_moveCmd+ "]");
            $scope.g_moveCmd = l_moveCmd;
            if(l_moveCmd == "F" ){
                $log.info("FORWARD!!!!");
            }
            if(l_moveCmd == "FL" ){
                $log.info("FORWARD LEFT!!!!");

            }
            if(l_moveCmd == "FR" ){
                $log.info("FORWARD RIGHT!!!!");

            }
            if(l_moveCmd == "B" ){
                $log.info("BACK!!!!");

            }
            if(l_moveCmd == "BL" ){
                $log.info("BACK LEFT!!!!");
            }
            if(l_moveCmd == "BR" ){
                $log.info("BACK RIGHT!!!!");
            }
            if(l_moveCmd == "BRAKE" ){
                $log.info("BRAKE!!!!");
            }
           f_sendMoveCmd(l_moveCmd);
        };

        //
        // Handle one of the AUDIO button clicks
        // @param l_say String from html page based on button clicked, value will be something
        // like 'hi', 'bye', 'move over', etc.
        //
        $scope.f_audioClick = function(l_audioCmd){
            $log.info("in the method f_audioClick and l_audioCmd is [" + l_audioCmd+ "]");
            $scope.g_audioFileName = l_audioCmd;
            f_sendPlayFileCmd(l_audioCmd);

        };

        //
        // Handle one of the spin button clilcks
        //
        $scope.f_changeValClick = function(l_val_direction)
        {
            $log.info(l_val_direction.valueOf());
            if (l_val_direction.valueOf() == 'D_AVG_UP')
            {
                if( $scope.g_volts == $scope.g_vr_max) return;
                if(! $scope.g_volts < $scope.g_vr_max &&
                     $scope.g_volts+$scope.g_vr_step <= $scope.g_vr_max) {
                    $scope.g_volts += $scope.g_vr_step;

                }
                else
                {
                    $scope.g_volts = $scope.g_vr_max;
                }
                f_sendMoveCmd('D_AVG_' + $scope.g_volts);
            }
            else if (l_val_direction.valueOf() == 'D_AVG_DOWN')
            {
                if($scope.g_volts == $scope_g_vr_min) return;
                if(! $scope.g_volts > $scope.g_vr_min &&
                    $scope.g_volts-$scope.g_vr_step >= $scope.g_vr_min) {
                    $scope.g_volts -= $scope.g_vr_step;

                }
                else
                {
                    $scope.g_volts = $scope.g_vr_min;
                }
                f_sendMoveCmd('D_AVG_' + $scope.g_volts);
            }
            if (l_val_direction.valueOf() == 'D_RAMP_UP')
            {
                if( $scope.g_ramp == $scope.g_vr_max) return;
                if(! $scope.g_ramp < $scope.g_vr_max &&
                    $scope.g_ramp+$scope.g_vr_step <= $scope.g_vr_max) {
                    $scope.g_ramp += $scope.g_vr_step;

                }
                else
                {
                    $scope.g_ramp = $scope.g_vr_max;
                }
                f_sendMoveCmd('D_RAMP_' + $scope.g_ramp);
            }
            else if (l_val_direction.valueOf() == 'D_RAMP_DOWN')
            {
                if( $scope.g_ramp == $scope.g_vr_min) return;
                if(! $scope.g_ramp > $scope.g_vr_min &&
                    $scope.g_ramp-$scope.g_vr_step >= $scope.g_vr_min) {
                    $scope.g_ramp -= $scope.g_vr_step;
                }
                else
                {
                    $scope.g_ramp = $scope.g_vr_min;
                }
                f_sendMoveCmd('D_RAMP_' + $scope.g_ramp);
            }
        }
        //
        // low-level function to contact back-end motor command servlet
        //
        f_sendMoveCmd = function(l_moveCmd)
        {
            var urlStr = 'servlets/EgorServlet';
            $log.info("sending GET to [" + urlStr + "]/with param [" + l_moveCmd + "]");
            $http({
                method : 'GET',
                url : urlStr,
                params: {leCmd: l_moveCmd}
            }).success(function(data, status, headers, config) {
                $log.info("http call successful status/data: [" + status + "]/[" + data + "]");
                $scope.g_moveResponse =  data.doCmdResult;

                $scope.g_httpResponseData = JSON.stringify(data);
                $scope.g_httpStatusCode = status;
            }).error(function(data, status, headers, config) {
                // called asynchronously if an error occurs
                // or server returns response with an error status.

                $scope.g_error="http call UNSUCCESSFUL status/data: [" + status + "]/[" + data + "]";
                $log.error($scope.g_error);
                $scope.g_httpResponseData = data;
                $scope.g_httpStatusCode = status;

            });
        };

        //
        // low level function to contact back end AUDIO servlet
        //
        f_sendPlayFileCmd = function(l_audioCmd)
        {

            var urlStr = 'servlets/AudioServlet';
            $log.info("sending GET to [" + urlStr + "]/with param [" + l_audioCmd + "]");
            $http({
                method : 'GET',
                url : urlStr,
                params: {leCmd: l_audioCmd}
            }).success(function(data, status, headers, config) {
                $log.info("http call successful status/data: [" + status + "]/[" + data + "]");
                $scope.g_audioResponse =  data.result;

                $scope.g_httpResponseData = JSON.stringify(data);
                $scope.g_httpStatusCode = status;
            }).error(function(data, status, headers, config) {
                // called asynchronously if an error occurs
                // or server returns response with an error status.

                $scope.g_error="http call UNSUCCESSFUL status/data: [" + status + "]/[" + data + "]";
                $log.error($scope.g_error);
                $scope.g_httpResponseData = data;
                $scope.g_httpStatusCode = status;

            });
        };
        //
        // Timer to refresh image file every xx seconds
        //
        $scope.image_reloader = function(timer,url){
            $log.info("IMAGE RELOADER");
            $scope.counter = 0;
            $scope.onTimeout = function(){
                $scope.counter++;
                $log.info($scope.counter + " " + "timer" + location.host);
                // add a hash tag to fake out new url to force refresh of a file with the same name,
                // but which may have changed content back on the server
                // well hash tag does not work but question mark does seem to work
                //$scope.image_url = 'http://' + location.host + '/rap-web/img.jpg' + '?' + new Date().getTime();
                var mywebctx = location.pathname;
                var webIdx = mywebctx.indexOf('rap-web');
                mywebctx = location.pathname.substr(0,webIdx+'rap-web'.length);
                //console.log("mywebct: " + mywebctx);
                $scope.my_image_url = 'http://' + location.host + mywebctx + '/img.jpg' + '#' + new Date().getTime();

                //var lastSlash = location.pathname.lastIndexOf("/");
                //var webCtx = location.pathname.substr(0,lastSlash);
                //$log.info("my webcontext: " + webCtx);
                //$scope.my_image_url = 'http://' + location.host + webCtx + '/img.jpg' + '#' + new Date().getTime();
                mytimeout = $timeout($scope.onTimeout,2000);
            }
            var mytimeout = $timeout($scope.onTimeout,2000);

            $scope.stop = function(){
                $timeout.cancel(mytimeout);
            }
        };

    }]);