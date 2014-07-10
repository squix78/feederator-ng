angular
    .module('Feederator', ['ngResource', 'ngTouch', 'ngRoute', 'ngSanitize', 
                           'mobile-angular-ui', 'inbox', 'fulltext', 'item'])
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
    });



