var showImgApp = angular.module('showImgApp', ['ngMessages']);


showImgApp.controller('showImgController', ['$scope','$log','$timeout','$http',
    function($scope, $log,$timeout,$http) {
        $log.info("In showImgController!");

        //$scope.my_image_url = 'http://' + location.host + '/rap-web/img.jpg' + '?' + new Date().getTime();
        $scope.my_image_url = 'img.jpg';
        $scope.my_image_reloader = function(timer,url){
            $log.info("IMAGE RELOADER");
            $scope.counter = 0;
            $scope.onTimeout = function(){
                $scope.counter++;
                $log.info($scope.counter + " " + "timer" + location.host);
                // add a hash tag to fake out new url to force refresh of a file with the same name,
                // but which may have changed content back on the server
                // well hash tag does not work but question mark does seem to work
                //$scope.my_image_url = 'http://' + location.host + '/rap-web/img.jpg' + '?' + new Date().getTime();
                //$scope.my_image_url = 'http://' + location.host + '/raptor/rap-web/img.jpg' + '#' + new Date().getTime();
                //var lastSlash = location.pathname.lastIndexOf("/");
                //var webCtx = location.pathname.substr(0,lastSlash);
                //$log.info("my context: " + webCtx);
                var mywebctx = location.pathname;
                var webIdx = mywebctx.indexOf('rap-web');
                mywebctx = location.pathname.substr(0,webIdx+'rap-web'.length);
                console.log("mywebct: " + mywebctx);
                $scope.my_image_url = 'http://' + location.host + mywebctx + '/img.jpg' + '#' + new Date().getTime();
                //$scope.my_image_url = 'http://' + location.host + webCtx + '/img.jpg' + '#' + new Date().getTime();
                mytimeout = $timeout($scope.onTimeout,4000);
            }
            var mytimeout = $timeout($scope.onTimeout,4000);

            $scope.stop = function(){
                $timeout.cancel(mytimeout);
            }
        };

    }]);