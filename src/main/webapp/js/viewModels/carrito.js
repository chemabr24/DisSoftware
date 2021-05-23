define([ 'knockout', 'appController', 'ojs/ojmodule-element-utils', 'accUtils',
		'jquery' ], function(ko, app, moduleUtils, accUtils, $) {

	class checkCorderViewModel extends carritos_method{
		constructor() {
			super();
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

		sumar(nombre){
			let self = this;
			self.message("");
			self.error("");
			let data = {
				url : "corder/addAlCarrito/" + nombre.replace(/\//g, 'alt47'),
				type : "post",
				contentType : 'application/json',
				success : function(response) {
					self.message("Producto añadido al carrito");
					self.carrito(response.oproducts)
					self.importe(response.importe)
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
					self.importe(response.importe)
				},
				error : function(response) {
					self.error(response.responseJSON.errorMessage);
				}
			};
			$.ajax(data);
		}

		pagar() {
			app.router.go( { path : "payment" } );
		}

		connected() {
			document.title = "Carrito";
			super.getCarrito();
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
