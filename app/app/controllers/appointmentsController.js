var app = angular.module('apt-controllers');

app.controller('AppointmentsController', ['$scope', '$http', function($scope, $http) {

	$http.get(baseURL + "users/appointment").
		success(function(data) {
			$scope.appointments = data.data;
		}).
		error(function(data) {
			alert('Can\'t load appointments');
			console.error(data);
		});

}])
.directive('userAppointment', [function(){
	
	return {
		// name: '',
		// priority: 1,
		// terminal: true,
		scope: {
			apt: '=info'
		}, // {} = isolate, true = child, false/undefined = no change
		// controller: function($scope, $element, $attrs, $transclude) {},
		// require: 'ngModel', // Array = multiple requires, ? = optional, ^ = check parent elements
		restrict: 'E', // E = Element, A = Attribute, C = Class, M = Comment
		// template: '',
		templateUrl: 'app/directives/appointment.html',
		// replace: true,
		// transclude: true,
		// compile: function(tElement, tAttrs, function transclude(function(scope, cloneLinkingFn){ return function linking(scope, elm, attrs){}})),
		link: function($scope, iElm, iAttrs, controller) {
			$scope.delete = function(id) {
				var realy = confirm("Are you sure?");
				if (realy === true) {
					console.log("Will delete " + id);
				}
			}
		}
	};
}]);