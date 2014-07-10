angular.module('item', ['ngResource', 'itemService']);


function ItemController($scope, $rootScope, $timeout, $location, $routeParams, ItemService) {
	$rootScope.isDetail = true;
	$scope.itemId = $routeParams.itemId;
	$rootScope.loading = true;
	$scope.item = ItemService.getItem($scope.itemId);
	$rootScope.gotoInbox = function() {
		$location.hash($scope.itemId);
		$location.path("/inbox");
	};
	
}