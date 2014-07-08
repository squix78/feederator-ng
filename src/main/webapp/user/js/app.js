angular
    .module('Feederator', ['ngResource', 'ngTouch', 'ngRoute', 'ngSanitize', 'mobile-angular-ui'])
    .config(function($routeProvider) {
      $routeProvider.when('/inbox', {
    	  controller : 'InboxController',
    	  templateUrl : 'partials/inbox.html'
      })
      .when('/item/:itemId', {
    	  controller : 'ItemController',
    	  templateUrl : 'partials/item.html'
      })
      .otherwise({
        controller : 'InboxController',
        templateUrl : 'partials/inbox.html'
      });
    })
    .factory('Inbox', ['$resource', function($resource) {
      return $resource('/rest/user/inbox');
    }])
    .factory('Fulltext', ['$resource', function($resource) {
    	return $resource('/rest/user/fulltext/:url');
    }])
	.factory('Item', ['$resource', function($resource) {
		return $resource('/rest/user/item/:itemId');
	}]);

function InboxController($scope, $timeout, $location, Inbox) {
	$scope.items = Inbox.query({}, function() {
	});
	
}
function ItemController($scope, $timeout, $location, $routeParams, Item, Fulltext) {
	$scope.itemId = $routeParams.itemId;
	$scope.item = Item.get({itemId: $scope.itemId}, function() {
		$scope.article = Fulltext.get({url: $scope.item.link}, function() {
			
		});
	});
	
}

