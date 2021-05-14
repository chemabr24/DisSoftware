define([ 'knockout', 'appController', 'ojs/ojmodule-element-utils', 'accUtils',
		'jquery' ], function(ko, app, moduleUtils, accUtils, $) {

	class checkCorderViewModel {
		constructor() {
			var self = this;
			
			self.carrito = ko.observableArray([]);
			self.importe = ko.observable();
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

		getCarrito() {
			var self = this;
			var data = {
				url : "corder/getCarrito",
				type : "get",
				contentType : 'application/json',
				success : function(response) {
					self.carrito(response.oproducts)
					console.log(self.carrito)
					self.importe(response.importe)
					console.log(self.importe);
				},
				error : function(response) {
					self.error(response.responseJSON.errorMessage);
				}
			};
			$.ajax(data);
			
		}

		sumar(nombre){
			let self = this;
			let data = {
				url : "corder/addAlCarrito/" + nombre.replace(/\//g, 'alt47'),
				type : "post",
				contentType : 'application/json',
				success : function(response) {
					self.message("Producto añadido al carrito");
					self.carrito(response.oproducts)
					console.log(self.carrito)
					self.importe(response.importe)
					console.log(self.importe);
				},
				error : function(response) {
					self.error(response.responseJSON.errorMessage);
				}
			};
			$.ajax(data);
		}

		restar(nombre){
			let self = this;
			let data = {
				url : "corder/subAlCarrito/" + nombre.replace(/\//g, 'alt47'),
				type : "post",
				contentType : 'application/json',
				success : function(response) {
					self.message("Producto reducido al carrito");
					self.carrito(response.oproducts)
					console.log(self.carrito)
					self.importe(response.importe)
					console.log(self.importe);
				},
				error : function(response) {
					self.error(response.responseJSON.errorMessage);
				}
			};
			$.ajax(data);
		}

		pagar(){
			//posible redirect
		}

		connected() {
			document.title = "Carrito";
			this.getCarrito();
		};

		disconnected() {
			// Implement if needed
		};

		transitionCompleted() {
			// Implement if needed
		};
	}

	return checkCorderViewModel;
});
