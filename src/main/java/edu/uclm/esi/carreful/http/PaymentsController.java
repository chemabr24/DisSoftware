package edu.uclm.esi.carreful.http;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

import edu.uclm.esi.carreful.dao.EnvioDao;
import edu.uclm.esi.carreful.dao.ExpresDao;
import edu.uclm.esi.carreful.dao.ProductDao;
import edu.uclm.esi.carreful.dao.RecogidaDao;
import edu.uclm.esi.carreful.model.Carrito;
import edu.uclm.esi.carreful.model.Envio;
import edu.uclm.esi.carreful.model.Expres;
import edu.uclm.esi.carreful.model.OrderedProduct;
import edu.uclm.esi.carreful.model.Product;
import edu.uclm.esi.carreful.model.Recogida;
import edu.uclm.esi.carreful.tokens.Email;

@RestController
@RequestMapping("payments")
public class PaymentsController extends CookiesController {
	static {
		Stripe.apiKey = "sk_test_51IdbvhJ8BWeBYDgAUPUO8GiNVyrQ7T99gSZNGj64fbS4EdElVDLsq5DZ2wsUXGLntt5Zu53l3gAOedQMQCEsgFLw00zXVhM39x";  
	}

	@Autowired
	private ProductDao productDao;
	
	@Autowired
	private EnvioDao envioDao;
	
	@Autowired
	private ExpresDao expresDao;
	
	@Autowired
	private RecogidaDao recogidaDao;
	
	@PostMapping("/solicitarPreautorizacion")
	public String solicitarPreautorizacion(HttpServletRequest request) {
		try {
			Carrito carrito = (Carrito) request.getSession().getAttribute(Messages.getString("PaymentsController.1"));  
			if(carrito==null) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Messages.getString("PaymentsController.2"));  
			}
			double am = (carrito.getImporte()*100);
			long amount = (long) am;
			PaymentIntentCreateParams createParams = new PaymentIntentCreateParams.Builder()
					.setCurrency(Messages.getString("PaymentsController.4"))  
					.setAmount(amount)
					.build();
			PaymentIntent intent = PaymentIntent.create(createParams);
			JSONObject jso = new JSONObject(intent.toJson());
			return jso.getString(Messages.getString("PaymentsController.5"));  
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
	
	@GetMapping("/tieneCongelado")
	public boolean tieneCongelado(HttpServletRequest request) {
		Carrito carrito = (Carrito) request.getAttribute(Messages.getString("PaymentsController.6"));
		Collection<OrderedProduct> productos = carrito.getOproducts();
		for(OrderedProduct producto : productos) {
			Optional<Product> oProducto = productDao.findByNombre(producto.getName());
			if(oProducto.isPresent() && oProducto.get().isCongelado()) {
				return false;  
			}
		}
		return true;
	}
	
	@PostMapping("/compra")
	public void compra(HttpServletRequest request, @RequestBody Map<String, Object> info) {
		JSONObject jso = new JSONObject(info);
		Carrito carrito = (Carrito) request.getSession().getAttribute(Messages.getString("PaymentsController.6"));  
		Collection<OrderedProduct> productos = carrito.getOproducts();
		for(OrderedProduct producto : productos) {
			Optional<Product> oProducto = productDao.findByNombre(producto.getName());
			if(!oProducto.isPresent() || producto.getAmount() > oProducto.get().getStock()) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No hay suficientes productos en stock de "+producto.getName());  
			}
		}
		switch ((String)jso.get(Messages.getString("PaymentsController.7"))) {  
		case "Recogida": 
			Recogida corderR = new Recogida();
			corderR.setCorreo(jso.getString(Messages.getString("PaymentsController.9")));  
			corderR.setTienda(jso.getString(Messages.getString("PaymentsController.10")));  
			corderR.setPrecioTotal(carrito.getImporte());
			reducirStock(carrito);
			recogidaDao.save(corderR);
			sendMail(jso.getString(Messages.getString("PaymentsController.11")), corderR.getId());  
			break;
		case "Envio":
			Envio corderE = new Envio();
			corderE.setCalle(jso.getString(Messages.getString("PaymentsController.13")));  
			corderE.setCodigoPostal(Integer.parseInt(jso.getString(Messages.getString("PaymentsController.14"))));  
			corderE.setCorreo(jso.getString(Messages.getString("PaymentsController.15")));  
			corderE.setLocalidad(jso.getString(Messages.getString("PaymentsController.16")));  
			corderE.setPrecioTotal(carrito.getImporte());
			reducirStock(carrito);
			envioDao.save(corderE);
			sendMail(jso.getString(Messages.getString("PaymentsController.17")), corderE.getId());  
			break;
		case "Expres":
			Expres corderX = new Expres();
			corderX.setCalle(jso.getString(Messages.getString("PaymentsController.19")));  
			corderX.setCodigoPostal(Integer.parseInt(jso.getString(Messages.getString("PaymentsController.20"))));  
			corderX.setCorreo(jso.getString(Messages.getString("PaymentsController.21")));  
			corderX.setLocalidad(jso.getString(Messages.getString("PaymentsController.22")));  
			corderX.setPrecioTotal(carrito.getImporte());
			reducirStock(carrito);
			expresDao.save(corderX);
			sendMail(jso.getString(Messages.getString("PaymentsController.23")), corderX.getId());  
			break;
		default:
			break;
		}
		request.getSession().setAttribute("carrito", new Carrito());	
	}
	
	private void reducirStock(Carrito carrito) {
		try {
			Collection<OrderedProduct> productos = carrito.getOproducts();
			for(OrderedProduct producto : productos) {
				Optional<Product> prod = productDao.findByNombre(producto.getName());
				if(prod.isPresent()) {
					Product prodAct = prod.get();
					prodAct.subStock(producto.getAmount());
					productDao.save(prodAct);
				}else {
					throw new ResponseStatusException(HttpStatus.OK, "Error con el producto " + producto.getName());  
				}
			}
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.OK, "Error con los productos del carrito");  
		}	
	}

	private void sendMail(String email, String id) {
		Email smtp = new Email();
		String texto = "Para ver el estado del pedido use este identificador de pedido  " + id  
				+ ".<br> Tambien puede verlo pulsando el siguiente enlace "  
				+ "<a href='http://localhost:8080/?ojr=checkCorder/checkCorder/" + id + "'>aquí</a>";  
		smtp.send(email, "Carreful: recuperacion de contraseña", texto);  
	}

}
