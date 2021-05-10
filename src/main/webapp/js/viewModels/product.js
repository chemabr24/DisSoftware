define([ 'knockout', 'appController', 'ojs/ojmodule-element-utils', 'accUtils',
		'jquery', 'twbsPagination' ], function(ko, app, moduleUtils, accUtils, $) {

	class ProductViewModel {
		constructor() {
			var self = this;
			self.productos = ko.observableArray([]);
			self.carrito = ko.observableArray([]);

			self.message = ko.observable(null);
			self.error = ko.observable(null);
			
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

		paginacion() {
			$('#pagination').twbsPagination({
				currentPage: page.currentPage,
				totalPages: Math.ceil(page.totalCount/page.pageSize),
				startPage: 1,
				visiblePages: 7,
				first: "Home",
						 last: "No pages",
						 prev: 'Previous',
						 next: 'Next',
				initiateStartPageClick: false,
				onPageClick: function (event, page) {
					$('#page-content').text('Page ' + page);
					User.getList(page,User.param);
				}
			});
			 
			}

		add() {
			var self = this;
			var info = {
				nombre : this.nombre(),
				precio : this.precio()
			};
			let data = {
				data : JSON.stringify(info),
				url : "product/add",
				type : "post",
				contentType : 'application/json',
				success : function(response) {
					self.message("Producto guardado");
					self.getProductos();
				},
				error : function(response) {
					self.error(response.responseJSON.errorMessage);
				}
			};
			$.ajax(data);
		}

		getProductos(){
			let self = this;
			let data = {
				url: "product/getTodos",
				type: "get",
				contentType: 'application/json',
				success: function (response) {
					self.productos(response);					
				},
				error: function (response) {
					self.error(response.responseJSON.errorMessage);
				}
			};
			$.ajax(data);
		}
		
		eliminarProducto(nombre){
			let self = this;
			let data = {
				url : "product/borrarProducto/" + nombre,
				type : "delete",
				contentType : 'application/json',
				success : function(response) {
					self.message("Producto eliminado");
					self.getProductos();
				},
				error : function(response) {
					self.error(response.responseJSON.errorMessage);
				}
			};
			$.ajax(data);
		}
		
		addAlCarrito(nombre){
			let self = this;
			let data = {
				url : "product/addAlCarrito/" + nombre,
				type : "post",
				contentType : 'application/json',
				success : function(response) {
					self.message("Producto añadido al carrito");
					self.carrito(response.products)
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
			this.getProductos();
			this.paginacion();

		};

		disconnected() {
			// Implement if needed
		};

		transitionCompleted() {
			// Implement if needed
		};
	}

	return ProductViewModel;
});
