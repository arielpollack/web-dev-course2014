var app = angular.module('apt-controllers', [])

app.controller('LoginController', ['$scope', '$http', '$location', function($scope, $http, $location) {
	$scope.login = function() {
		var data = {
			id_number: $scope.idNumber,
			password: $scope.password
		};

		$http.post(baseURL + "users/login", data).
			success(function(data) {
				alert('Success!');
				console.log(data);
				$location.path('/appointments');
			}).
			error(function(error) {
				alert('Failed!');
				console.error(error);
			});
	}
}]);