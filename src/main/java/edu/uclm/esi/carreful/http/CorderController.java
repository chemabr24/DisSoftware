package edu.uclm.esi.carreful.http;

import java.util.Collection;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import edu.uclm.esi.carreful.dao.CorderDao;
import edu.uclm.esi.carreful.dao.ProductDao;
import edu.uclm.esi.carreful.model.Carrito;
import edu.uclm.esi.carreful.model.Corder;
import edu.uclm.esi.carreful.model.OrderedProduct;
import edu.uclm.esi.carreful.model.Product;

@RestController
@RequestMapping("corder")
public class CorderController extends CookiesController {

	@Autowired
	private ProductDao productDao;

	@Autowired
	private CorderDao corderDao;

	@GetMapping("/checkCorder/{orderid}")
	public String checkCorder(@PathVariable String orderid) {
		try {
			Optional<Corder> corder = corderDao.findById(orderid);
			if (corder.isPresent()) {
				return corder.get().getState();
			}
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}

		return "";

	}

	@PostMapping("/addAlCarrito/{nombre}")
	public Carrito addAlCarrito(HttpServletRequest request, @PathVariable String nombre) {
		Carrito carrito = (Carrito) request.getSession().getAttribute(Messages.getString("CorderController.1")); 
		if (carrito == null) {
			carrito = new Carrito();
			request.getSession().setAttribute(Messages.getString("CorderController.2"), carrito);
		}
		Collection<OrderedProduct> productos = carrito.getOproducts();
		
		OrderedProduct producto;
		Optional<Product> oProducto = productDao.findByNombre(nombre.replace(Messages.getString("CorderController.0"), "/")); 
		for(OrderedProduct prod : productos) {
		if(prod.getName().equals(nombre)) {
			 producto = prod;
			 if(!oProducto.isPresent() || producto.getAmount()+1 > oProducto.get().getStock()) {
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No hay suficientes productos en stock de "+producto.getName());  
				}
		}
		}
		
		
		
			carrito.add(oProducto.get(), 1);

		return carrito;
	}

	@PostMapping("/subAlCarrito/{nombre}")
	public Carrito minusAlCarrito(HttpServletRequest request, @PathVariable String nombre) {
		Carrito carrito = (Carrito) request.getSession().getAttribute(Messages.getString("CorderController.5"));
		if (carrito == null) {
			request.getSession().setAttribute(Messages.getString("CorderController.6"), new Carrito()); 
			return carrito;
		}
		Optional<Product> producto = productDao.findByNombre(nombre.replace(Messages.getString("CorderController.7"), "/")); 
		if (producto.isPresent())
			carrito.sub(producto.get(), 1);
		request.getSession().setAttribute(Messages.getString("CorderController.9"), carrito);
		return carrito;
	}

	@GetMapping("/getCarrito")
	public Carrito getCarrito(HttpServletRequest request) {
		return (Carrito) request.getSession().getAttribute(Messages.getString("CorderController.10")); 
	}
	
	@GetMapping("/changeEstado/{corderid}")
	public void changeEstado(@PathVariable String corderid) {
		try {
			Optional<Corder> corder = corderDao.findById(corderid);
			if (corder.isPresent()) {
				corder.get().nextState();
				corderDao.save(corder.get());
			}
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}
	
}
