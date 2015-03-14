var app = angular.module('apt-controllers');

app.controller('AppointmentsController', ['$scope', '$http', function($scope, $http) {
	var loadAppointments = function() {
		sqliteService.getAppointments(null, function(appointments) {
			if (appointments.length != 0) {
				$scope.$apply(function() {
					$scope.appointments = appointments;
				})
			} else {
				console.log("error")
				$http.get(baseURL + "users/appointment").
				success(function(data) {
					sqliteService.addAppointments(data.data);
					setTimeout(loadAppointments, 1000);
				}).
				error(function(data) {
					alert('Can\'t load appointments');
					console.error(data);
				});
			}
		})
	}
	loadAppointments()
}])
.directive('userAppointment', ['$location',function($location) {
	return {
		scope: {
			apt: '=info'
		}, 
		restrict: 'E',
		templateUrl: 'app/directives/appointment.html',
		link: function($scope, iElm, iAttrs, controller) {
			$scope.edit = function(id) {
				$location.path('/appointment/' + new Date().getTime() + '/' + id)
			}
		}
	};
}]);