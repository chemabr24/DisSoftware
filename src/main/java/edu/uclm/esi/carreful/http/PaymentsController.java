package edu.uclm.esi.carreful.http;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

import edu.uclm.esi.carreful.model.Carrito;

@RestController
@RequestMapping("payments")
public class PaymentsController extends CookiesController {
	static {
		Stripe.apiKey = "sk_test_51IdbvhJ8BWeBYDgAUPUO8GiNVyrQ7T99gSZNGj64fbS4EdElVDLsq5DZ2wsUXGLntt5Zu53l3gAOedQMQCEsgFLw00zXVhM39x";
	}
	
	@PostMapping("/solicitarPreautorizacion")
	public String solicitarPreautorizacion(HttpServletRequest request) {
		try {
			Carrito carrito = (Carrito) request.getSession().getAttribute("carrito");
			if(carrito==null) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No hay carrito");
			}
			double am = (carrito.getImporte()*100);
			long amount = (long) am;
			PaymentIntentCreateParams createParams = new PaymentIntentCreateParams.Builder()
					.setCurrency("eur")
					.setAmount(amount)
					.build();
			// Create a PaymentIntent with the order amount and currency
			PaymentIntent intent = PaymentIntent.create(createParams);
			JSONObject jso = new JSONObject(intent.toJson());
			return jso.getString("client_secret");
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
}
