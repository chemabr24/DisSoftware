define([ 'knockout', 'appController', 'ojs/ojmodule-element-utils', 'accUtils',
		'jquery' ], function(ko, app, moduleUtils, accUtils, $) {

	class LoginViewModel {
		constructor() {
			var self = this;
			
			self.email = ko.observable();
			self.pwd = ko.observable();
			self.message = ko.observable();
			self.error = ko.observable();
			
			// Header Config
			self.headerConfig = ko.observable({
				'view' : [],
				'viewModel' : null
			});
			moduleUtils.createView({
				'viewPath' : 'views/header.html'
			}).then(function(view) {
				self.headerConfig({
					'view' : view,
					'viewModel' : app.getHeaderModel()
				})
			})
		}

		login() {
			var self = this;
			var info = {
				email : this.email(),
				pwd : this.pwd()
			};
			var data = {
				data : JSON.stringify(info),
				url : "user/login",
				type : "post",
				contentType : 'application/json',
				success : function() {
					app.router.go( { path : "catalogo"} );
				},
				error : function(response) {
					self.error(response.responseJSON.errorMessage);
				}
			};
			$.ajax(data);
		}
		
		recoverPwd() {
			var self = this;
			var data = {
				url : "user/recoverPwd?email=" + self.email(),
				type : "get",
				contentType : 'application/json',
				success : function(response) {
					self.message("Se ha enviado un correo para restablecer la contraseña")
				},
				error : function(response) {
					self.error(response.responseJSON.errorMessage);
				}
			};
			$.ajax(data);
		}

		register() {
			app.router.go( { path : "register" } );
		}

		connected() {
			accUtils.announce('Login page loaded.');
			document.title = "Login";
		}

		disconnected() {
			// Implement if needed
		}

		transitionCompleted() {
			// Implement if needed
		}
	}

	return LoginViewModel;
});
