class Catalogo_method{
    constructor(){

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
                self.pageNumber(0);
            },
            error: function (response) {
                self.error(response.responseJSON.errorMessage);
            }
        };
        $.ajax(data);
    }


    getNumeroProductos(categoria){
        let self = this;
        
        self.numero_de_productos("");
        self.error("");
        let data = {
            url: "product/numeroProductos/" + categoria ,
            type: "get",
            contentType: 'application/json',
            success: function (response) {
                self.numero_de_productos(response);
            },
            error: function (response) {
                self.error(response.responseJSON.errorMessage);
            }
        };
        $.ajax(data);
    }


}