angular.module('main', ['ngResource', 'itemService'])
.factory('Feeds', ['$resource', function($resource) {
    return $resource('/rest/user/feeds');
}]);

function MainController($scope, $rootScope, $timeout, $location, $routeParams, Feeds) {
	$scope.feeds = Feeds.query({}, function() {
		
	});
	
}