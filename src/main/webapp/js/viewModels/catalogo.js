define(['knockout', 'appController', 'ojs/ojmodule-element-utils', 'accUtils',
	'jquery'], function (ko, app, moduleUtils, accUtils, $) {

		class CatalogoViewModel extends Catalogo_method {
			constructor() {
				super();
				let self = this;
				self.productos = ko.observableArray([]);
				self.carrito = ko.observableArray([]);
				self.selectedCategory = ko.observable();
				self.logged = ko.observable(false);
				self.nologged = ko.observable(true);
				self.numero_de_productos = ko.observable();
				
				self.idproducto = ko.observable();
				self.nombreproducto = ko.observable();
				self.precioproducto = ko.observable();
				self.imagenproducto = ko.observable();
				self.stockproducto = ko.observable();
				self.congeladoproducto = ko.observable(false);
				self.categoriaproducto = ko.observable();

				self.categorias = ko.observableArray(["Todos"]);
				self.selectionChanged = function () {
					this.getProductCategoria();
					this.getNumeroProductos(self.selectedCategory());
				}

				self.setimagenproducto = function (widget, event) {
					var file = event.target.files[0];
					var reader = new FileReader();
					reader.onload = function () {
						self.imagenproducto("data:image/png;base64," + btoa(reader.result));
					}
					reader.readAsBinaryString(file);
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

			isLogged(){
				let self = this;
				let data = {
					url: "user/isLogin",
					type: "get",
					contentType: 'application/json',
					success: function (response) {
						self.logged(response);
						self.nologged(!response);
					},
					error: function (response) {
						self.error(response.responseJSON.errorMessage);
					}
				};
				$.ajax(data);
			}

			
			vaciarmodel(){
				let self = this;
				self.idproducto("");
				self.nombreproducto("");
				self.precioproducto("");
				self.stockproducto("");
				self.categoriaproducto("");
				self.congeladoproducto(false);
				self.imagenproducto("");
			}

			addProduct() {
				let self = this;
				self.message("");
				self.error("");
				let info = {
					id: self.idproducto(),
					nombre: self.nombreproducto(),
					precio: self.precioproducto(),
					stock: self.stockproducto(),
					congelado: self.congeladoproducto(),
					foto: self.imagenproducto(),
					categoria: self.categoriaproducto()
				};
				let data = {
					data: JSON.stringify(info),
					url: "product/add",
					type: "post",
					contentType: 'application/json',
					success: function (response) {
						self.getProductCategoria();
						self.message("Producto guardado")
						self.getNumeroProductos(self.selectedCategory());
					},
					error: function (response) {
						self.error(response.responseJSON.errorMessage);
					}
				};
				$.ajax(data);
			}

			cargarProducto(id,nombre,precio,stock,categoria,congelado,imagen){
				let self = this;
				self.idproducto(id);
				self.nombreproducto(nombre);
				self.precioproducto(precio);
				self.stockproducto(stock);
				self.categoriaproducto(categoria.nombre);
				self.congeladoproducto(congelado);
				self.imagenproducto(imagen);
			}

			eliminarProducto(id) {
				let self = this;
				self.message("");
				self.error("");
				let data = {
					url: "product/borrarProducto/" + id,
					type: "delete",
					contentType: 'application/json',
					success: function (response) {
						self.getProductCategoria();
						self.message("Producto eliminado")
						self.getNumeroProductos(self.selectedCategory());
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
				super.getProductos();
				super.getCategorias();
				this.isLogged();
				super.getNumeroProductos("Todos");
			};

			}


			disconnected() {
				// Implement if needed
			}

			transitionCompleted() {
				// Implement if needed
			}
		}

		return CatalogoViewModel;
	});