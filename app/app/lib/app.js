var app = angular.module('apt-manage', [
	'ngRoute',
	'apt-controllers'
	]);

app.config(['$routeProvider', function($routeProvider) {
	$routeProvider.
	when('/', {
		templateUrl: 'app/views/login.html',
		controller: 'LoginController'
	}).
	when('/appointment', {
		templateUrl: 'app/views/appointment.html',
		controller: 'AppointmentController'
	}).
	when('/appointment/:start', {
		templateUrl: 'app/views/appointment.html',
		controller: 'AppointmentController'
	}).
	when('/appointment/:start/:apt', {
		templateUrl: 'app/views/appointment.html',
		controller: 'AppointmentController'
	}).
	when('/appointments', {
		templateUrl: 'app/views/appointments.html',
		controller: 'AppointmentsController'
	}).
	otherwise({
		redirectTo: '/'
	});
}]);