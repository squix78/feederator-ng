angular.module('itemService', ['ngResource', 'angularLocalStorage', 'fulltext'])
.factory('Item', ['$resource', function($resource) {
	return $resource('/rest/user/item/:itemId');
}])
.factory('ItemService', ['$rootScope', '$anchorScroll', '$location', '$q', 'storage', 'Item', 'Fulltext', function($rootScope, $anchorScroll, $location, $q, storage, Item, Fulltext) {
	var loader = {};
	var items = [];
	var itemMap = {};
	var topItemId = null;
	var service = {
		rememberTopItem: function(scrollContainerName) {
			var scrollPosition = document.getElementById(scrollContainerName).scrollTop;
			service.topItemId = null;
			var minDistance = 999;
			angular.forEach(items, function(value, key) {
				var element = document.getElementById(value.id);
				if (angular.isDefined(element)) {
					var distance = Math.abs(element.offsetTop - scrollPosition);
					if (distance < minDistance) {
						service.topItemId = value.id;
						minDistance = distance;
					}
				}

			});
		},
		restoreTopItem: function() {
			if (angular.isDefined(service.topItemId)) {
				$location.hash(service.topItemId);
				$anchorScroll();
			}
		},
		setLoader: function(newLoader) {
			loader = newLoader;
			$rootScope.listTitle = loader.listTitle;
			$rootScope.lastUpdated = loader.lastUpdated;
		},
		loadItems: function() {
			if (items.length === 0) {
				var storageItems = storage.get("items");
				if (storageItems !== null) {
					var defer = $q.defer();
					defer.resolve(storageItems);
					storageItems.$promise = defer.promise; 
					items = storageItems;
					service.fillItemMap();
					return items;
				}
				items = loader.getItems();
				items.$promise.then(function(loadedItems) {
					angular.forEach(loadedItems, function(value, key) {
						itemMap[value.id] = value;
					});
					storage.set("items", items);
					loader.lastUpdated = new Date();
				});
			}
			return items;
		},
		fillItemMap: function() {
			angular.forEach(items, function(value, key) {
				itemMap[value.id] = value;
			});
		},
		getItems: function() {
			return items;
		},
		getItem: function(itemId) {
			var item = itemMap[itemId];
			if (angular.isDefined(item)) {
				service.loadFullText(itemId);
				return item;
			}
			return Item.get({itemId: itemId}, function(item) {
				itemMap[item.id] = item;
				service.loadFullText(itemId);
			});
		},
		loadFullText: function(itemId) {
			var item = itemMap[itemId];
			if (angular.isDefined(item) && (!angular.isDefined(item.article) || item.article === null)) {
				item.fulltext = Fulltext.getFullText({url: item.link}, function(fulltext) {
					console.log("Received fulltext");
				});				
			}
		},
		clearItems: function() {
			items = [];
			storage.clearAll();
		}

	};
	return service;
}]);