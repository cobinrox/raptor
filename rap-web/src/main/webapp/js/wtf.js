//
// Angular module
// see https://code.angularjs.org/
//
// @param app name/alias
// @param [ ] extra module name dependencies
// ngMessages: prints messages to the document
// ngResource: provides http getter
var myWtf = angular.module('myWtf', []);

// Controller
// @param name for controller
// @param function/execution code or
//  array of dependencies followed by the function/execution
// the latter is preferred so that this file can be minified properly
// @param $scope injected by anular, global session object
// @param $log injected by angular, logging service
// @param $filter injected by angular, manipulator service
// @param $resource injected by angular, http data getter
//myApp.controller('mainController', function($scope, $log, $filter,$resource) {
myWtf.controller('wtfController', ['$scope','$log','$filter',
    function($scope, $log, $filter) {
        $log.info("In mainController");
        //console.log("In mainController but using non-angular log!");
        $scope.g_projectName = $filter('uppercase')('Egor Project');

        $scope.g_author = 'COBINROX';



    }]);