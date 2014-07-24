angular
    .module('Feederator', ['ngResource', 'ngTouch', 'ngRoute', 'ngSanitize', 
                           'mobile-angular-ui', 'angular-carousel', 'inbox', 'main', 'fulltext', 'item', 'feedItems'])
    .config(function($routeProvider) {
      $routeProvider.when('/inbox', {
    	  controller : InboxController,
    	  templateUrl : 'partials/inbox.html',
    	  resolve: InboxController.resolve
      })
      .when('/feeds/:feedId/items', {
    	  controller : FeedItemsController,
    	  templateUrl : 'partials/inbox.html',
    	  resolve: FeedItemsController.resolve
      })
      .when('/item/:itemId', {
    	  controller : 'ItemController',
    	  templateUrl : 'partials/item.html'
      })
      .when('/feeds/:feedId', {
    	  controller : 'FeedController',
    	  templateUrl : 'partials/feed.html'
      })
      .when('/feeds', {
    	  controller : 'NewFeedController',
    	  templateUrl : 'partials/feed.html'
      })
      .otherwise({
        controller : 'InboxController',
        templateUrl : 'partials/inbox.html',
  	    resolve: InboxController.resolve
      });
    });



