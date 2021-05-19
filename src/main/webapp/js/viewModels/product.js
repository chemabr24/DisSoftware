define(['knockout', 'appController', 'ojs/ojmodule-element-utils', 'accUtils',
	'jquery'], function (ko, app, moduleUtils, accUtils, $) {

		class ProductViewModel {
			constructor() {

				let self = this;
				self.productos = ko.observableArray([]);
				self.carrito = ko.observableArray([]);
				self.selectedCategory = ko.observable();

				self.categorias = ko.observableArray(["Todos"]);
				self.selectionChanged = function () {
					this.getProductCategoria();
				}

				self.pag = function () {
					window.pagObj = $('#pagination').twbsPagination({
						totalPages: 35,
						visiblePages: 10,
						onPageClick: function (event, page) {
							console.info(page + ' (from options)');
						}
					}).on('page', function (event, page) {
						console.info(page + ' (from event listening)');
					});
				}

				self.message = ko.observable(null);
				self.error = ko.observable(null);

				self.pageNumber = ko.observable(0);
				self.nbPerPage = 16;
				self.totalPages = ko.computed(function () {
					let div = Math.floor(self.productos().length / self.nbPerPage);
					div += self.productos().length % self.nbPerPage > 0 ? 1 : 0;
					return div - 1;
				});

				this.paginated = ko.computed(function () {
					let first = self.pageNumber() * self.nbPerPage;
					return self.productos.slice(first, first + self.nbPerPage);
				});

				this.hasPrevious = ko.computed(function () {
					return self.pageNumber() !== 0;
				});

				this.hasNext = ko.computed(function () {
					return self.pageNumber() !== self.totalPages();
				});

				this.next = function () {
					if (self.pageNumber() < self.totalPages()) {
						window.scroll({ top: 0, left: 0, behavior: 'smooth' })
						self.pageNumber(self.pageNumber() + 1);
					}
				}

				this.previous = function () {
					if (self.pageNumber() != 0) {
						window.scroll({ top: 0, left: 0, behavior: 'smooth' })
						self.pageNumber(self.pageNumber() - 1);
					}
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

			add() {
				let self = this;
				let info = {
					nombre: this.nombre(),
					precio: this.precio()
				};
				let data = {
					data: JSON.stringify(info),
					url: "product/add",
					type: "post",
					contentType: 'application/json',
					success: function (response) {
						self.message("Producto guardado");
						self.getProductos();
					},
					error: function (response) {
						self.error(response.responseJSON.errorMessage);
					}
				};
				$.ajax(data);
			}

			getProductos() {
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

			getCategorias() {
				let self = this;
				let data = {
					url: "product/getCategorias",
					type: "get",
					contentType: 'application/json',
					success: function (response) {
						self.categorias(response);
					},
					error: function (response) {
						self.error(response.responseJSON.errorMessage);
					}
				};
				$.ajax(data);
			}

			getProductCategoria() {
				let self = this;
				let categoria = self.selectedCategory();
				let data = {
					url: "product/getProductos/" + categoria,
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

			eliminarProducto(nombre) {
				let self = this;
				let data = {
					url: "product/borrarProducto/" + nombre.replace( /\//g,"alt47"),
					type: "delete",
					contentType: 'application/json',
					success: function (response) {
						self.message("Producto eliminado");
						self.getProductos();
					},
					error: function (response) {
						self.error(response.responseJSON.errorMessage);
					}
				};
				$.ajax(data);
			}

			addAlCarrito(nombre) {
				let self = this;
				let data = {
					url: "corder/addAlCarrito/" + nombre.replace(/\//g, "alt47"),
					type: "post",
					contentType: 'application/json',
					success: function (response) {
						self.message("Producto añadido al carrito");
						self.carrito(response.products)
					},
					error: function (response) {
						self.error(response.responseJSON.errorMessage);
					}
				};
				$.ajax(data);
			}

			connected() {
				accUtils.announce('Login page loaded.');
				document.title = "Productos";
				this.getProductos();
				this.getCategorias();
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