angular.module('item', ['ngResource', 'angularLocalStorage', 'itemService'])
.run(["$rootScope", "storage", function($rootScope, storage) {
    
    $rootScope.itemCarouselPrev = function(id) {
      $rootScope.$emit("item.carousel.prev", id);
    };
    
    $rootScope.itemCarouselNext = function(id) {
      $rootScope.$emit("item.carousel.next", id);
    };
       
    var carouselItems = function(id) {
      var elem = angular.element(document.getElementById(id));
      var res = angular.element(elem.children()[0]).children();
      elem = null;
      return res;
    };

    var findActiveItemIndex = function(items) {
      var idx = -1;
      var found = false;

      for (var _i = 0; _i < items.length; _i++) {
        item = items[_i];
        idx += 1;
        if (angular.element(item).hasClass('active')) {
          found = true;
          break;
        }
      }

      if (found) {
        return idx;
      } else {
        return -1;
      }

    };
    // length: 5
    // input  -2  -1   0   1  2  3  4  5  6
    // output  3   4   0   1  2  3  4  0  1
    var getIndex = function(length, index) {
    	return (index + length) % length;
    };
    
    var moveActiveItem = function(id, direction) {
        var items = carouselItems(id);
        var idx = findActiveItemIndex(items);

        var newIdx = getIndex(items.length, idx + direction);
        var leftNeighbour = getIndex(items.length, newIdx - 1);
        var rightNeighbour = getIndex(items.length, newIdx + 1);


        removeActiveItems(items);


        angular.element(items[newIdx]).addClass("active");
        angular.element(items[leftNeighbour]).addClass("left-active");
        angular.element(items[rightNeighbour]).addClass("right-active");

        var itemId = angular.element(items[newIdx]).attr("id")
        storage.set("lastRoute", "/item/" + itemId);

        items = null;
        idx = null;
        lastIdx = null;    	
    };
    
    var removeActiveItems = function(items) {
    	angular.forEach(items, function(item, key) {
    		angular.element(item).removeClass("active");
    		angular.element(item).removeClass("left-active");
    		angular.element(item).removeClass("right-active");
    	});
    };

    $rootScope.$on("item.carousel.prev", function(e, id) {
    	moveActiveItem(id, -1);
    });

    $rootScope.$on("item.carousel.next", function(e, id) {
    	moveActiveItem(id, 1);
    });
  }
])
.directive('itemContent', function () {
	  return function(scope, elm, attrs) {
		  scope.$watch(function() { 
			  return (' ' + elm[0].className + ' ').indexOf('active') > -1;
		  }, function(val) {
			  if (val) {
				  console.log(elm[0].id);
			  }
		  });
	  }
	})
.directive( "carouselItem", function($rootScope, $swipe){
	  return function(scope, element, attrs){
	      var startX = null;
	      var startY = null;
	      var endAction = "cancel";
	      var carouselId = element.parent().parent().attr("id");

	      var translateAndRotate = function(x, y, z, deg){
	    		  
			        element[0].style["-webkit-transform"] =
			           "translate3d("+x+"px,"+ y +"px," + z + "px) rotate("+ deg +"deg)";
			        element[0].style["-moz-transform"] =
			           "translate3d("+x+"px," + y +"px," + z + "px) rotate("+ deg +"deg)";
			        element[0].style["-ms-transform"] =
			           "translate3d("+x+"px," + y + "px," + z + "px) rotate("+ deg +"deg)";
			        element[0].style["-o-transform"] =
			           "translate3d("+x+"px," + y  + "px," + z + "px) rotate("+ deg +"deg)";
			        element[0].style["transform"] =
			           "translate3d("+x+"px," + y + "px," + z + "px) rotate("+ deg +"deg)";

	      };

	      $swipe.bind(element, {
	        start: function(coords) {
	          startX = coords.x;
	          startY = coords.y;
	        },

	        cancel: function(e) {
	          translateAndRotate(0, 0, 0, 0);
	          e.stopPropagation();
	        },

	        end: function(coords, e) {
	          if (endAction == "prev") {
	            $rootScope.itemCarouselNext(carouselId);
	          } else if (endAction == "next") {
	            $rootScope.itemCarouselPrev(carouselId);
	          }
	          scope.$apply();
	          translateAndRotate(0, 0, 0, 0);
	          e.stopPropagation();
	          endAction == "";
	        },

	        move: function(coords) {
	          if( startX != null) {
	            var deltaX = coords.x - startX;
	            var clientWidth = element[0].clientWidth;
	            var deltaXRatio = deltaX / clientWidth;
	            if (deltaXRatio > 0.3) {
	              endAction = "next";
	            } else if (deltaXRatio < -0.3){
	              endAction = "prev";
	            } 
	            translateAndRotate(deltaXRatio * 200, 0, 0, deltaXRatio * 15);
	          }
	        }
	      });
	    }
	});

function ItemController($scope, $rootScope, $timeout, $location, $routeParams, ItemService) {
	$rootScope.isDetail = true;
	$scope.itemId = $routeParams.itemId;
	$rootScope.loading = true;
	
	ItemService.loadItems();
	$scope.items = ItemService.getItems();
	
	$scope.getContent = function(item) {
	  var element = angular.element(document.getElementById(item.id));
	  var content = item.description;
	  if (angular.isDefined(item.article) && item.article.content !== null) {
		  content = item.article.content;
	  }
	  if (element.hasClass('active') || element.hasClass('left-active') || element.hasClass('right-active')) {
	    return content;
	  } else {
	    return "Loading content...";
	  }
	};
	
	//$scope.item = ItemService.getItem($scope.itemId);
	$rootScope.gotoInbox = function() {
		$location.hash($scope.itemId);
		$location.path("/inbox");
	};
	
}