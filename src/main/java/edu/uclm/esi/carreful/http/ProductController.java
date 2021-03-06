package edu.uclm.esi.carreful.http;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.json.JSONObject;
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
import edu.uclm.esi.carreful.exceptionhandling.GeneralException;
import edu.uclm.esi.carreful.model.Categoria;
import edu.uclm.esi.carreful.model.Product;

@RestController
@RequestMapping("product")
public class ProductController extends CookiesController {

	@Autowired
	private ProductDao productDao;
	@Autowired
	private CategoriaDao categoriaDao;

	@PostMapping("/add")
	public void add(@RequestBody Map<String, Object> info) {
		try {
			JSONObject jso = new JSONObject(info);
			Optional<Product> producto = productDao.findById(jso.getString("id"));
			Optional<Categoria> categoria = categoriaDao.findByNombre(jso.getString("categoria")); 
			Categoria categorianueva;
			if (categoria.isPresent()) {
				categorianueva = categoria.get();
			} else {
				categorianueva = new Categoria();
				categorianueva.setNombre(jso.getString("categoria")); 
				categoriaDao.save(categorianueva);
			}
			Product productonuevo;
			if (producto.isPresent()) {
				productonuevo = producto.get();
			} else {
				categorianueva.addProd();

				productonuevo = new Product();
			}

			productonuevo.setCategoria(categorianueva);
			productonuevo.setCongelado(jso.getBoolean("congelado")); 
			productonuevo.setFoto(jso.getString("foto")); 
			productonuevo.setNombre(jso.getString("nombre")); 
			productonuevo.setPrecio(jso.getDouble("precio"));
			productonuevo.setStock(jso.getInt("stock")); 
			productDao.save(productonuevo);
			categoriaDao.save(categorianueva);

		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
		}
	}

	@GetMapping("/getTodos")
	public List<Product> get() {
		try {
			return productDao.findAll();
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	@GetMapping("/getCategorias")
	public List<String> getCategorias() {
		try {
			List<String> categorias = categoriaDao.findAllNames();
			categorias.add(0, Messages.getString("ProductController.8")); 
			return categorias;
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	@GetMapping("/getProductos/{categoria}")
	public List<Product> getProductos(@PathVariable String categoria) {
		try {
			if (categoria.equals(Messages.getString("ProductController.7"))) { 
				return productDao.findAll();
			} else {
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
			throw new GeneralException(Messages.getString("ProductController.0"));
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	@DeleteMapping("/borrarProducto/{id}")
	public void borrarProducto(@PathVariable String id) {
		try {
			Optional<Product> optProduct = productDao.findById(id);
			if (optProduct.isPresent()) {
				Categoria categoria = optProduct.get().getCategoria();
				categoria.subproduct();
				productDao.deleteById(id);
				categoriaDao.save(categoria);
			}else {
				throw new GeneralException(Messages.getString("ProductController.1")); 
			}
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}
	@GetMapping("/numeroProductos/{name}")
	public int getNumeroProductos(@PathVariable String name) {
		int nproductos=0;
		Optional<Categoria> categoria;
		if(name.equals(Messages.getString("ProductController.12"))) { 
			List<String> categorias = categoriaDao.findAllNames();
			for(int i=0;i<categorias.size();i++) {
				 categoria = categoriaDao.findByNombre(categorias.get(i));
				 if(categoria.isPresent()) {
				nproductos = nproductos + categoria.get().getNumeroDeProductos();
			}else {
				throw new GeneralException(Messages.getString("ProductController.2")); 
			}
			}
		}else {
			
		categoria = categoriaDao.findByNombre(name);
		if(categoria.isPresent()) {
		nproductos = categoria.get().getNumeroDeProductos();
		}else {
			throw new GeneralException(Messages.getString("ProductController.3")); 
		}
		}
		return nproductos;
	}

}
