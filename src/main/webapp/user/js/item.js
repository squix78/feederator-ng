angular.module('item', ['ngResource', 'itemService'])
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
	      }

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
	            $rootScope.carouselNext(carouselId);
	          } else if (endAction == "next") {
	            $rootScope.carouselPrev(carouselId);
	          }
	          scope.$apply();
	          translateAndRotate(0, 0, 0, 0);
	          e.stopPropagation();
	        },

	        move: function(coords) {
	          if( startX != null) {
	            var deltaX = coords.x - startX;
	            var deltaXRatio = deltaX / element[0].clientWidth;
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
	  if (element.hasClass('active')) {
	    return item.article.content;
	  } else {
	    return "Empty content";
	  }
	};
	
	//$scope.item = ItemService.getItem($scope.itemId);
	$rootScope.gotoInbox = function() {
		$location.hash($scope.itemId);
		$location.path("/inbox");
	};
	
}