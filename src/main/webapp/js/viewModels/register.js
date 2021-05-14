define(['knockout', 'appController', 'ojs/ojmodule-element-utils', 'accUtils',
	'jquery'], function (ko, app, moduleUtils, accUtils, $) {

		class RegisterViewModel {
			constructor() {
				let self = this;

				self.userName = ko.observable("");
				self.email = ko.observable("");
				self.pwd1 = ko.observable("");
				self.pwd2 = ko.observable("");
				self.picture = ko.observable();

				self.message = ko.observable();
				self.error = ko.observable();

				self.setPicture = function (widget, event) {
					var file = event.target.files[0];
					var reader = new FileReader();
					reader.onload = function () {
						self.picture("data:image/png;base64," + btoa(reader.result));
					}
					reader.readAsBinaryString(file);
				}

				self.register = function () {
					var info = {
						userName: self.userName(),
						email: self.email(),
						pwd1: self.pwd1(),
						pwd2: self.pwd2(),
						picture: self.picture()
					};
					var data = {
						data: JSON.stringify(info),
						url: "user/register",
						type: "put",
						contentType: 'application/json',
						success: function (response) {
							self.error("");
							self.message("Te hemos enviado un correo para confirmar tu registro");
						},
						error: function (response) {
							self.message("");
							self.error(response.responseJSON.errorMessage);
						}
					};
					$.ajax(data);
				}

				// Header Config
				self.headerConfig = ko.observable({
					'view': [],
					'viewModel': null
				});
				moduleUtils.createView({
					'viewPath': 'views/header.html'
				}).then(function (view) {
					self.headerConfig({
						'view': view,
						'viewModel': app.getHeaderModel()
					})
				})
			}
			connected = function () {
				accUtils.announce('Register page loaded.');
				document.title = "Registro";
				// Implement further logic if needed
			};

			disconnected = function () {
				// Implement if needed
			};

			transitionCompleted = function () {
				// Implement if needed
			};
		}

		return RegisterViewModel;
	});