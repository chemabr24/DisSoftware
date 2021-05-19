class CatalogoMethod {
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
            },
            error: function (response) {
                self.error(response.responseJSON.errorMessage);
            }
        };
        $.ajax(data);
    }
}