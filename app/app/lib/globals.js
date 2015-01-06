var baseURL = "http://localhost:8080/"; //"http://webedu17.mtacloud.co.il:8080/APTManage/";
angular.module('apt-controllers', []);

var simplifyDate = function(date) {
	date.setHours(0);
	date.setMinutes(0);
	date.setSeconds(0);
	date.setMilliseconds(0);
	return date;
}