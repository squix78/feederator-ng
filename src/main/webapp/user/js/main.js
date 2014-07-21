angular.module('main', ['ngResource', 'angularLocalStorage', 'itemService'])
.factory('Feeds', ['$resource', function($resource) {
    return $resource('/rest/user/feeds');
}]);

function MainController($scope, $rootScope, $timeout, $location, $routeParams, storage, Feeds) {
//	var lastRoute = storage.get("lastRoute");
//	if (lastRoute !== null && angular.isDefined(lastRoute)) {
//		$location.path(lastRoute);
//	}
	$scope.$on('$locationChangeStart', function(event, next, current) { 
		storage.set("lastRoute", next);
	});
	$scope.feeds = Feeds.query({}, function() {
		
	});
	
}