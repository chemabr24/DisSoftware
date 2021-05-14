package edu.uclm.esi.carreful.http;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import edu.uclm.esi.carreful.dao.CategoriaDao;
import edu.uclm.esi.carreful.dao.ProductDao;
import edu.uclm.esi.carreful.model.Product;

@RestController
@RequestMapping("product")
public class ProductController extends CookiesController {

	@Autowired
	private ProductDao productDao;
	@Autowired
	private CategoriaDao categoriaDao;

	@PostMapping("/add")
	public void add(@RequestBody Product product) {
		try {
			productDao.save(product);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
		}
	}

	@GetMapping("/getTodos")
	public List<Product> get() {
		try {
			System.out.println("Ha estado aqui");
			List<Product> productos = productDao.findAll();
			System.out.println("El producto primero es: " + productos.get(0));
			return productos;
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	@GetMapping("/getCategorias")
	public List<String> getCategorias() {
		try {
			List<String> categorias = categoriaDao.findAllNames();
			categorias.add(0, "Todos");
			return categorias;
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	@GetMapping("/getProductos/{categoria}")
	public List<Product> getProductos(@PathVariable String categoria) {
		try {
			System.out.println("Ha estado aqui");
			if (categoria.equals("Todos")) {
				return productDao.findAll();
			} else {
				System.out.println("La categoria es: " + categoria);
				System.out.println("y el primer producto: " + productDao.findProduct(categoria).get(0));
				return productDao.findProduct(categoria);
			}
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	@GetMapping("/getPrecio/{nombre}")

	public double getPrecio(@PathVariable String nombre) {
		try {
			Optional<Product> optProduct = productDao.findById(nombre);
			if (optProduct.isPresent())
				return optProduct.get().getPrecio();
			throw new Exception("El producto no existe");
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	@DeleteMapping("/borrarProducto/{nombre}")
	public void borrarProducto(@PathVariable String nombre) {
		try {
			Optional<Product> optProduct = productDao.findById(nombre);
			if (optProduct.isPresent())
				productDao.deleteById(nombre);
			else
				throw new Exception("El producto no existe");
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

}
