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
							self.importe(response.importe)
						},
						error : function(response) {
							self.error(response.responseJSON.errorMessage);
						}
					};
					$.ajax(data);
					
				}
			}
