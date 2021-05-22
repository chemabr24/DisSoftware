			class carritos_method{
				constructor(){}

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
			}
