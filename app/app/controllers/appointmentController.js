var app = angular.module('apt-controllers');

app.controller('AppointmentController', ['$scope', '$http', '$routeParams', function($scope, $http, $routeParams) {
	if (!$routeParams.start) {
		$routeParams.start = new Date().getTime();
	}

	var date = new Date(parseInt($routeParams.start));
	var tomorrow = new Date(parseInt($routeParams.start));
	tomorrow.setDate(date.getDate() + 1);
	tomorrow = simplifyDate(tomorrow);

	$scope.nextDay = tomorrow.getTime();
	var yesterday = new Date($scope.nextDay);
	yesterday.setDate(yesterday.getDate() - 2);
	var current = simplifyDate(new Date()).getTime();
	$scope.prevDay = yesterday.getTime() < current ? current : yesterday.getTime();

	console.log(date.getTime(), tomorrow.getTime());
	$http({
		url: baseURL + 'users/appointment/available',
		method: 'GET',
		params: {start: date.getTime(), end: tomorrow.getTime()},
		}).
		success(function(data) {
			$scope.blocks = data.data;
		}).
		error(function(data) {
			alert(data.error);
		});
}]);