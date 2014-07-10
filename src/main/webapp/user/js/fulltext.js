angular.module('fulltext', ['ngResource'])
.factory('Fulltext', ['$resource', function($resource) {
    	return $resource(
                "http://fulltext-squix.rhcloud.com/full-text-rss/extract.php",
                {
                    callback: "JSON_CALLBACK"
                },
                {
                    getFullText: {
                        method: "JSONP",
                        isArray: false
                    }
                });
    }]);