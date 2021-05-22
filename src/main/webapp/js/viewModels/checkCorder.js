define([ 'knockout', 'appController', 'ojs/ojmodule-element-utils', 'accUtils',
		'jquery' ], function(ko, app, moduleUtils, accUtils, $) {

	class checkCorderViewModel {
		constructor() {
			var self = this;
			
			self.corderid = ko.observable();
			self.estado = ko.observable();
			self.shouldShow = ko.observable(false);
			self.admin = ko.observable(false);

			this.show = function () {
				self.shouldShow(true);
			}
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

		checkCorder() {
			var self = this;
			var data = {
				url : "corder/checkCorder/"+this.corderid(),
				type : "get",
				contentType : 'application/json',
				success : function(response) {
					if(response!=""){
						self.estado("El pedido " + self.corderid() + " se encuentra actualmente " + response);
					}else{
						self.estado("El id del pedido es incorrecto");
					}
					self.show();
				},
				error : function(response) {
					self.error(response.responseJSON.errorMessage);
				}
			};
			$.ajax(data);
			
		}

		cambiarEstado(){
			var self = this;
			var data = {
				url : "corder/changeEstado/"+this.corderid(),
				type : "get",
				contentType : 'application/json',
				success : function() {
					self.checkCorder();
				},
				error : function(response) {
					self.error(response.responseJSON.errorMessage);
				}
			};
			$.ajax(data);
		}

		isLogin(){
			var self = this;
			var data = {
				url : "user/isLogin",
				type : "get",
				contentType : 'application/json',
				success : function(response) {
					self.admin(!response);
				},
				error : function(response) {
					self.error(response.responseJSON.errorMessage);
				}
			};
			$.ajax(data);
		}

		connected() {
			document.title = "Comprobar pedido";
			this.isLogin();
		}

		disconnected() {
			// Implement if needed
		}

		transitionCompleted() {
			// Implement if needed
		}
	}

	return checkCorderViewModel;
});
