angular.module('inbox', ['ngResource', 'itemService', 'mgcrea.pullToRefresh'])
.factory('Inbox', ['$resource', function($resource) {
    return $resource('/rest/user/inbox');
}]);

function InboxController($scope, $rootScope, $timeout, $location, $timeout, Inbox, ItemService) {
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
	
	$scope.items.$promise.then(function() {
		$timeout(function() {
			ItemService.restoreTopItem();
		});
	});
	
	$scope.showItem = function(itemId) {
		ItemService.rememberTopItem('scrollContainer');
		$location.path("/item/" + itemId);
	};
	
	$scope.refreshItems = function() {
		console.log("Pulled to refresh");
		$rootScope.toggle('myOverlay', 'on');
	}
	
}