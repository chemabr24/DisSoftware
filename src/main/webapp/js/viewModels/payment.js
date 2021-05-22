define(['knockout', 'appController', 'ojs/ojmodule-element-utils', 'accUtils',
	'jquery'], function (ko, app, moduleUtils, accUtils, $) {

		class PaymentViewModel extends carritos_method {
			constructor() {
				super();
				var self = this;

				self.stripe = Stripe('pk_test_51IdbvhJ8BWeBYDgAXunkfkM7Ey76EcV3elIcca5Y8cTgkzZvVqrMRS755s5RKl7klgjqdWO5XKHD8tti3iR0Kdjk00hzYrTzqX');

				self.carrito = ko.observableArray([]);
				self.importe = ko.observable();

				self.aviableTiendas = ko.observableArray(['Ciudad Real', 'Toledo', 'Albacete', 'Cuenca', 'Guadalajara']);
				self.selectedTienda = ko.observable();
				self.selectedTipoEnvio = ko.observable("Recogida");
				self.congelado = ko.observable(false);

				self.bEnvio = ko.observable(false);
				self.bRecogida = ko.observable(false);
				self.bPagar = ko.observable(false);
				self.pagando = ko.observable(true);

				self.correo = ko.observable();
				self.localidad = ko.observable();
				self.calle = ko.observable();
				self.codigoPostal = ko.observable();

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

				self.Shipping = function () {
					this.tipo = "";
				};

				self.Shipping.prototype = {
					setStrategy: function (tipo) {
						this.tipo = tipo;
					},

					habilitarPagar: function(){
						self.bPagar(true);
						self.pagando(false);
					},
					habilitardatos: function(){
						self.pagando(true);
						self.bPagar(false);
					},
					pagar: function(){
						this.tipo.comprar()
					}
				};

				self.Envio = function () {
					this.comprar = function () {
						let info = {
							correo : self.correo(),
							localidad : self.localidad(),
							calle : self.calle(),
							codigoPostal : self.codigoPostal(),
							tipo : self.selectedTipoEnvio()
						};
						self.compra(info)
					}
				};

				self.Recogida = function () {
					this.comprar = function () {
						let info = {
							correo : self.correo(),
							tienda : self.selectedTienda(),
							tipo : self.selectedTipoEnvio()
						};
						self.compra(info);
					}
				};

				self.shipping = new self.Shipping();
			}

			strategy(){
				let self = this;
				let stra;
				if(self.selectedTipoEnvio()=="Recogida"){
					self.bEnvio(false);
					self.bRecogida(true);
					stra = new self.Recogida();
				}else{
					self.bEnvio(true);
					self.bRecogida(false);
					stra = new self.Envio();
				}
				self.shipping.setStrategy(stra);
			}

			habilitarPagar(){
				let self = this;
				self.shipping.habilitarPagar()
			}

			habilitardatos(){
				let self = this;
				self.shipping.habilitardatos()
			}

			compra(info){
				let self = this;
				let data = {
					data: JSON.stringify(info),
					url: "payments/compra",
					type: "post",
					contentType: 'application/json',
					success: function (response) {
						alert("Pago realizado\n Se ha enviado un correo con la informacion del pedido")
					},
					error: function (response) {
						self.error(response.responseJSON.errorMessage);
					}
				};
				$.ajax(data);
			}

			solicitarPreautorizacion() {
				let self = this;

				let data = {
					url: "payments/solicitarPreautorizacion",
					type: "post",
					contentType: 'application/json',
					success: function (response) {
						self.clientSecret = response;
						self.rellenarFormulario();
					},
					error: function (response) {
						self.error(response.responseJSON.errorMessage);
					}
				};
				$.ajax(data);
			}

			rellenarFormulario() {
				let self = this;
				var elements = self.stripe.elements();
				var style = {
					base: {
						color: "#32325d",
						fontFamily: 'Arial, sans-serif',
						fontSmoothing: "antialiased",
						fontSize: "16px",
						"::placeholder": {
							color: "#32325d"
						}
					},
					invalid: {
						fontFamily: 'Arial, sans-serif',
						color: "#fa755a",
						iconColor: "#fa755a"
					}
				};

				var card = elements.create("card", { style: style });
				// Stripe injects an iframe into the DOM
				card.mount("#card-element");
				card.on("change", function (event) {
					// Disable the Pay button if there are no card details in the Element
					document.querySelector("button").disabled = event.empty;
					document.querySelector("#card-error").textContent = event.error ? event.error.message : "";
				});

				var form = document.getElementById("payment-form");
				form.addEventListener("submit", function (event) {
					event.preventDefault();
					// Complete payment when the submit button is clicked
					self.payWithCard(card);
				});
			}

			payWithCard(card) {
				let self = this;
				self.stripe.confirmCardPayment(self.clientSecret, {
					payment_method: {
						card: card
					}
				}).then(function (result) {
					if (result.error) {
						// Show error to your customer (e.g., insufficient funds)
						self.error(result.error.message);
					} else {
						if (result.paymentIntent.status === 'succeeded') {
							self.shipping.pagar();
						}
					}
				});
			}
			tieneCongelado(){
				let self = this;
				let data = {
					url: "corder/tieneCongelado",
					type: "get",
					contentType: 'application/json',
					success: function(response){
						self.congelado(response);
					},
					error: function (response) {
						self.error(response.responseJSON.errorMessage);
					}
				};
				$.ajax(data);
			}
			connected() {
				accUtils.announce('Payment page loaded.');
				document.title = "Pago";
				super.getCarrito();
				this.tieneCongelado();
				this.solicitarPreautorizacion();
				
			}

			disconnected() {
				// Implement if needed
			}

			transitionCompleted() {
				// Implement if needed
			}
		}

		return PaymentViewModel;
	});
