angular.module('inbox', ['ngResource', 'itemService', 'mgcrea.pullToRefresh'])
.factory('Inbox', ['$resource', function($resource) {
    return $resource('/rest/user/inbox');
}]);

function InboxController($scope, $rootScope, $timeout, $location, $timeout, items, ItemService) {

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

InboxController.resolve = {
    items: function($rootScope, ItemService, Inbox) {
    	var loader = {
    			listTitle: 'Inbox',
    			id: 'Inbox',
    			lastUpdate: null,
    			getItems: function() {
    				$rootScope.loading = true;
    				return Inbox.query({}, function() {
    					$rootScope.loading = true;
    				});
    			}	
    		};
    	ItemService.setLoader(loader);
        return ItemService.loadItems().$promise;
    }
};
