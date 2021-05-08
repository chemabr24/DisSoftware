package edu.uclm.esi.carreful.http;

import java.util.List;
import java.util.Optional;

import javax.print.attribute.SetOfIntegerSyntax;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.PUT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import edu.uclm.esi.carreful.dao.CorderDao;
import edu.uclm.esi.carreful.dao.ProductDao;
import edu.uclm.esi.carreful.model.Carrito;
import edu.uclm.esi.carreful.model.Corder;
import edu.uclm.esi.carreful.model.Product;

@RestController
@RequestMapping("product")
public class ProductController extends CookiesController {
	
	@Autowired
	private ProductDao productDao;
	
	@Autowired
	private CorderDao corderDao;
	
	@PostMapping("/add")
	public void add(@RequestBody Product product) {
		try {
			productDao.save(product);
		} catch(Exception e) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
		}
	}
	
	@GetMapping("/getTodos")
	public List<Product> get() {
		try {
			return productDao.findAll();
		} catch(Exception e) {
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
		} catch(Exception e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}
	
	@PostMapping("/addAlCarrito/{nombre}")
	public Carrito addAlCarrito(HttpServletRequest request, @PathVariable String nombre) {
		Carrito carrito= (Carrito) request.getSession().getAttribute("carrito");
		if (carrito==null) {
			carrito = new Carrito();
			request.getSession().setAttribute("carrito", carrito);
		}
		Product producto =  productDao.findById(nombre).get();
		carrito.add(producto,1);
		return carrito;
	}
	
	@DeleteMapping("/borrarProducto/{nombre}")
	public void borrarProducto(@PathVariable String nombre) {
		try {
			Optional<Product> optProduct = productDao.findById(nombre);
			if (optProduct.isPresent())
				productDao.deleteById(nombre);
			else
				throw new Exception("El producto no existe");
		} catch(Exception e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}
	
	@GetMapping("/checkCorder/{orderid}")
	public String checkCorder(@PathVariable String orderid) {
		try {
//			Corder c = new Corder();
//			c.setState("Preparado");
//			corderDao.save(c);
			Optional<Corder> corder = corderDao.findById(orderid);
			if(corder.isPresent()) {
				return corder.get().getState();
			}
			
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
		return "";
	}
}
