angular.module('feedItems', ['ngResource', 'itemService', 'mgcrea.pullToRefresh'])
.factory('FeedItems', ['$resource', function($resource) {
    return $resource('/rest/user/feeds/:feedId/items');
}]);

function FeedItemsController($scope, $rootScope, $timeout, $location, $timeout, items, ItemService) {

	$rootScope.isDetail = false;
	$rootScope.loading = true;
	$scope.items = items;
	//ItemService.loadItems();
	//$scope.items = ItemService.getItems();
	
	$rootScope.$on('$routeChangeSuccess', function(newRoute, oldRoute) {
			ItemService.restoreTopItem();
		});

	
	$scope.showItem = function(itemId) {
		ItemService.rememberTopItem('scrollContainer');
		$location.path("/item/" + itemId);
	};
	
	$rootScope.refreshItems = function() {
		ItemService.clearItems();
		$scope.items = ItemService.loadItems();
	};	
};

FeedItemsController.resolve = {
    items: function($route, ItemService, FeedItems) {
    	var feedId = $route.current.params.feedId;
    	var loader = {
    			listTitle: 'Feed',
    			id: feedId,
    			lastUpdate: null,
    			getItems: function() {
    				return FeedItems.query({feedId: feedId});
    			}	
    		};
    	ItemService.setLoader(loader);
        return ItemService.loadItems().$promise;
    }
};
