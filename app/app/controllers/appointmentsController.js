var app = angular.module('apt-controllers', [])

app.controller('AppointmentsController', ['$scope', '$http', function($scope, $http) {

	$http.get(baseURL + "users/appointment").
		success(function(data) {
			$scope.appointments = data.data;
		}).
		error(function(data) {
			alert('Can\'t load appointments');
			console.error(data);
		});

}]);