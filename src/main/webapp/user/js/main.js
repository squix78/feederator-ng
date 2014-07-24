angular.module('main', ['ngResource', 'angularLocalStorage', 'itemService', 'feed']);

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
	$scope.editFeed = function() {
		if (angular.isDefined($rootScope.feedId)) {
			$location.path("/feeds/" + $rootScope.feedId);
		}
	};
	$scope.newFeed = function() {
		$location.path("/feeds");
	};
	
}