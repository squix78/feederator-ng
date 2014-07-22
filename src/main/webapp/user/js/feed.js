angular.module('feed', ['ngResource'])
.factory('Feeds', ['$resource', function($resource) {
    return $resource('/rest/user/feeds');
}])
.factory('Feed', ['$resource', function($resource) {
    return $resource('/rest/user/feeds');
}]);
