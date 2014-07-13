angular
    .module('Feederator', ['ngResource', 'ngTouch', 'ngRoute', 'ngSanitize', 
                           'mobile-angular-ui', 'angular-carousel', 'inbox', 'fulltext', 'item'])
    .config(function($routeProvider) {
      $routeProvider.when('/inbox', {
    	  controller : InboxController,
    	  templateUrl : 'partials/inbox.html',
    	  resolve: InboxController.resolve
      })
      .when('/item/:itemId', {
    	  controller : 'ItemController',
    	  templateUrl : 'partials/item.html'
      })
      .otherwise({
        controller : 'InboxController',
        templateUrl : 'partials/inbox.html',
  	    resolve: InboxController.resolve
      });
    });



