//
// Angular module
// see https://code.angularjs.org/
//
// @param app name/alias
// @param [ ] extra module name dependencies
// ngMessages: prints messages to the document
// ngResource: provides http getter
var myApp = angular.module('myApp', ['ngMessages','ngResource']);

// Controller
// @param name for controller
// @param function/execution code or
//  array of dependencies followed by the function/execution
// the latter is preferred so that this file can be minified properly
// @param $scope injected by anular, global session object
// @param $log injected by angular, logging service
// @param $filter injected by angular, manipulator service
// @param $resource injected by angular, http data getter
// @param timeout injected by angular, timer capability
//
//
myApp.controller('mainController', ['$scope','$log','$filter','$resource','$http','$timeout',
    function($scope, $log, $filter,$resource,$http,$timeout) {
        $log.info("In mainController");
        //console.log("In mainController but using non-angular log!");
        $scope.g_projectName = $filter('uppercase')('Egor Project');
        
        $scope.g_author = 'EGOR';

        $scope.g_moveCmd = '';

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
                $scope.image_url = 'http://' + location.host + '/rap-web/img.jpg' + '?' + new Date().getTime();
                mytimeout = $timeout($scope.onTimeout,4000);
            }
            var mytimeout = $timeout($scope.onTimeout,4000);

            $scope.stop = function(){
                $timeout.cancel(mytimeout);
            }
        };

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
    }]);