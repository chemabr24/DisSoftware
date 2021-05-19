define([ 'knockout', 'appController', 'ojs/ojmodule-element-utils', 'accUtils',
		'jquery' ], function(ko, app, moduleUtils, accUtils, $) {

	class PaymentViewModel extends carritos_method{
		constructor() {
			super();
			var self = this;
			
			self.stripe = Stripe('pk_test_51IdbvhJ8BWeBYDgAXunkfkM7Ey76EcV3elIcca5Y8cTgkzZvVqrMRS755s5RKl7klgjqdWO5XKHD8tti3iR0Kdjk00hzYrTzqX');
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
		
		solicitarPreautorizacion() {
			let self = this;
			
			let data = {
				url : "payments/solicitarPreautorizacion",
				type : "post",
				contentType : 'application/json',
				success : function(response) {
					self.clientSecret = response;
					self.rellenarFormulario();
				},
				error : function(response) {
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
		    form.addEventListener("submit", function(event) {
		      event.preventDefault();
		      // Complete payment when the submit button is clicked
		      self.payWithCard(card);
		    });
		}
		
		payWithCard(card){
			let self = this;
			self.stripe.confirmCardPayment(self.clientSecret, {
				payment_method: {
					card: card
			    } 
			}).then(function(result) {
				if (result.error) {
					// Show error to your customer (e.g., insufficient funds)
					self.error(result.error.message);
				} else {
					if (result.paymentIntent.status === 'succeeded') {
						alert("Pago exitoso");
						let data = {
							url : "corder/removeCarrito",
							type : "get",
							contentType : 'application/json',
							error : function(response) {
								self.error(response.responseJSON.errorMessage);
							}
						};
						$.ajax(data);
					}
				}
			});			
		}

		connected() {
			accUtils.announce('Payment page loaded.');
			document.title = "Pago";
			super.getCarrito();
			this.solicitarPreautorizacion();			
		};

		disconnected() {
			// Implement if needed
		};

		transitionCompleted() {
			// Implement if needed
		};
	}

	return PaymentViewModel;
});
