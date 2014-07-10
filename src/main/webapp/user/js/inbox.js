angular.module('inbox', ['ngResource', 'itemService'])
.factory('Inbox', ['$resource', function($resource) {
    return $resource('/rest/user/inbox');
}]);

function InboxController($scope, $rootScope, $timeout, $location, $anchorScroll, $timeout, Inbox, ItemService) {
	var loader = {
		getItems: function() {
			return Inbox.query({});
		}	
	};
	ItemService.setLoader(loader);
	$rootScope.isDetail = false;
	$rootScope.loading = true;
	ItemService.loadItems();
	$scope.items = ItemService.getItems();
	
	$scope.showItem = function(itemId) {
		$location.path("/item/" + itemId);
	};
	
}