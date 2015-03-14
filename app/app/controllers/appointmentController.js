var app = angular.module('apt-controllers');

app.controller('AppointmentController', ['$scope', '$filter', '$location', '$http', '$routeParams', function($scope, $filter, $location, $http, $routeParams) {
	if (!$routeParams.start) {
		$routeParams.start = new Date().getTime();
	}

	$scope.updatingAppointment = $routeParams.apt

	var date = new Date(parseInt($routeParams.start));
	var tomorrow = new Date(parseInt($routeParams.start));
	tomorrow.setDate(date.getDate() + 1);
	tomorrow = simplifyDate(tomorrow);

	$scope.formattedDay = $filter('date')(date, "EEE, dd/MM")

	$scope.nextDay = tomorrow.getTime();
	var yesterday = new Date($scope.nextDay);
	yesterday.setDate(yesterday.getDate() - 2);
	var current = simplifyDate(new Date()).getTime();
	$scope.prevDay = yesterday.getTime() < current ? current : yesterday.getTime();

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

	$scope.selectBlock = function(date) {
		console.log(date)
		if ($scope.updatingAppointment) {
			$http({
				url: baseURL + 'users/appointment',
				method: 'PUT',
				data: {
					id: parseInt($scope.updatingAppointment),
					date: date
				},
			}).
			success(function(data) {
				sqliteService.updateAppointment($scope.updatingAppointment, date);
				$location.path('/appointments')
			}).
			error(function(data) {
				alert(data.error);
			});
		} else {
			$http({
				url: baseURL + 'users/appointment',
				method: 'POST',
				data: {date: date},
			}).
			success(function(data) {
				sqliteService.addAppointments([data.data])
				$location.path('/appointments')
			}).
			error(function(data) {
				alert(data.error);
			});
		}
	}
}])