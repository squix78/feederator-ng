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
    	return $resource(
                "http://fulltext-squix.rhcloud.com/full-text-rss/extract.php",
                {
                    callback: "JSON_CALLBACK"
                },
                {
                    getFullText: {
                        method: "JSONP",
                        isArray: false
                    }
                });
    }])
	.factory('Item', ['$resource', function($resource) {
		return $resource('/rest/user/item/:itemId');
	}]);

function InboxController($scope, $rootScope, $timeout, $location, $anchorScroll, $timeout, Inbox) {
	$rootScope.isDetail = false;
	$rootScope.loading = true;
	$scope.items = Inbox.query({}, function() {
		$rootScope.loading = false;
		$timeout(function(){
			$anchorScroll();
        });

	});
	$scope.showItem = function(itemId) {
		$location.path("/item/" + itemId);
	};
	
}
function ItemController($scope, $rootScope, $timeout, $location, $routeParams, Item, Fulltext) {
	$rootScope.isDetail = true;
	$scope.itemId = $routeParams.itemId;
	$rootScope.loading = true;
	$scope.item = Item.get({itemId: $scope.itemId}, function() {
		$scope.article = Fulltext.getFullText({url: $scope.item.link}, function() {
			$rootScope.loading = false;
		});
	});
	$rootScope.gotoInbox = function() {
		$location.hash($scope.itemId);
		$location.path("/inbox");
	};
	
}

