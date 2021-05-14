define(['knockout', 'appController', 'ojs/ojmodule-element-utils', 'accUtils',
	'jquery'], function (ko, app, moduleUtils, accUtils, $) {

		class setNewPasswordViewModel {
			constructor() {
				var self = this;

				self.pwd1 = ko.observable();
				self.pwd2 = ko.observable();
				self.message = ko.observable();
				self.error = ko.observable();

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

			recover() {
				var self = this;
				var info = {
					pwd1: this.pwd1(),
					pwd2: this.pwd2()
				};
				var data = {
					data: JSON.stringify(info),
					url: "user/resetPwd",
					type: "post",
					contentType: 'application/json',
					success: function (response) {
						self.message("Contraseña cambiada");
					},
					error: function (response) {
						self.error(response.responseJSON.errorMessage);
					}
				};
				$.ajax(data);
			}

			connected() {
				document.title = "Recuperación contraseña";
			};

			disconnected() {
				// Implement if needed
			};

			transitionCompleted() {
				// Implement if needed
			};
		}

		return setNewPasswordViewModel;
	});
