angular.module('itemService', ['ngResource', 'fulltext'])
.factory('Item', ['$resource', function($resource) {
	return $resource('/rest/user/item/:itemId');
}])
.factory('ItemService', ['Item', 'Fulltext', function(Item, Fulltext) {
	var loader = {};
	var items = [];
	var itemMap = {};
	var service = {
		setLoader: function(newLoader) {
			loader = newLoader;
		},
		loadItems: function() {
			if (items.length === 0) {
				items = loader.getItems();
				items.$promise.then(function(loadedItems) {
					angular.forEach(loadedItems, function(value, key) {
						itemMap[value.id] = value;
					});
				});
			}
			return items;
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
			if (angular.isDefined(item) && !angular.isDefined(item.fulltext)) {
				item.fulltext = Fulltext.getFullText({url: item.link}, function(fulltext) {
					console.log("Received fulltext");
				});				
			}
		}

	};
	return service;
}]);