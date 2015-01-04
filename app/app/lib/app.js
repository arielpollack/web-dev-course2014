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
	when('/appointment/:aptId', {
		templateUrl: 'app/views/appointment.html',
		controller: 'AppointmentController'
	}).
	when('/appointments', {
		templateUrl: 'app/views/appointments.html',
		controller: 'AppointmentsController'
	}).
	when('/profile', {
		templateUrl: 'app/views/editDetails.html',
		controller: 'UserController'
	}).
	otherwise({
		redirectTo: '/'
	});
}]);