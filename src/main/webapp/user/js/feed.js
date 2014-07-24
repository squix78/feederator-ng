var app = angular.module('feed', ['ngResource'])
.factory('Feeds', ['$resource', function($resource) {
    return $resource('/rest/user/feeds');
}])
.factory('Feed', ['$resource', function($resource) {
    return $resource('/rest/user/feeds/:feedId', {feedId:'@id'});
}]);

app.controller('FeedController', ['$scope', '$rootScope', '$routeParams', 'Feed', function($scope, $rootScope, $routeParams, Feed) {
	$scope.feedId = $routeParams.feedId;
	$rootScope.feedId = $routeParams.feedId;
	$scope.feed = Feed.get({feedId: $scope.feedId}, function() {
		
	});
	
	$scope.save = function() {
		$scope.feed.$save();
	};
	
}]);
app.controller('NewFeedController', ['$scope', '$rootScope', '$routeParams', 'Feeds', function($scope, $rootScope, $routeParams, Feeds) {

	$scope.save = function() {
		$scope.feed = new Feeds($scope.feed);
		$scope.feed.$save();
	};
	
}]);
