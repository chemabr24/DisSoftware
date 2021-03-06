package edu.uclm.esi.carreful.http;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import edu.uclm.esi.carreful.dao.TokenDao;
import edu.uclm.esi.carreful.dao.UserDao;
import edu.uclm.esi.carreful.exceptionhandling.GeneralException;
import edu.uclm.esi.carreful.model.User;
import edu.uclm.esi.carreful.tokens.Email;
import edu.uclm.esi.carreful.tokens.Token;

@RestController
@RequestMapping("user")
public class UserController extends CookiesController {
	@Autowired
	UserDao userDao;

	@Autowired
	TokenDao tokenDao;

	@GetMapping("/usarToken/{tokenId}")
	public void usarToken(HttpServletResponse response, HttpServletRequest request, @PathVariable String tokenId)
			throws IOException {
		Optional<Token> optToken = tokenDao.findById(tokenId);
		if (optToken.isPresent()) {
			Token token = optToken.get();
			if (token.isUsed())
				response.sendError(409, "El token ya se utilizó");
			else if (token.checkTime()) {
				response.sendError(409, "El token ha expirado");
			} else {
				request.getSession().setAttribute(Messages.getString("UserController.1"), tokenId);
				response.sendRedirect("http://localhost:8080?ojr=setNewPassword");
			}
		} else {
			response.sendError(404, "El token no existe");
		}
	}

	@PostMapping("/resetPwd")
	public void resetPwd(HttpServletRequest request, @RequestBody Map<String, Object> info)  {
		JSONObject jso = new JSONObject(info);
		String tokenId = (String) request.getSession().getAttribute("token");
		Optional<Token> token = tokenDao.findById(tokenId);
		if (token.isPresent()) {
			if (!token.get().isUsed() || token.get().checkTime()) {
				Token tkn = token.get();
				String email = tkn.getEmail();
				User user = userDao.findByEmail(email);
				final String pwd1 = jso.optString(Messages.getString("UserController.0"));
				final String pwd2 = jso.optString(Messages.getString("UserController.2"));
				String rqpwd = requisitosPwd(pwd1, pwd2);
				if (!rqpwd.equals("")) {
					throw new GeneralException(rqpwd);
				}
				user.setPwd(jso.getString("pwd1"));
				userDao.save(user);
				request.getSession().removeAttribute("token");
				tkn.setUsed(true);
				tokenDao.save(tkn);
			} else {
				throw new ResponseStatusException(HttpStatus.CONFLICT, "Token usado o expirado");
			}
		} else {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Token invalido");
		}
	}

	@GetMapping("/recoverPwd")
	public void recoverPwd(@RequestParam String email) {
		try {
			User user = userDao.findByEmail(email);
			if (user != null) {
				Token token = new Token(email);
				tokenDao.save(token);
				Email smtp = new Email();
				String texto = "Para recuperar tu contraseña, pulsa " + "<a href='http://localhost:8080/user/usarToken/"
						+ token.getId() + "'>aquí</a>";
				smtp.send(email, "Carreful: recuperacion de contraseña", texto);
			}else {
				throw new ResponseStatusException(HttpStatus.CONFLICT, "user not found");
			}
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
		}
	}

	@PostMapping("/login")
	public void login(HttpServletRequest request, @RequestBody Map<String, Object> info) {
		try {
			JSONObject jso = new JSONObject(info);
			String email = jso.getString("email");
			if (email.length() == 0)
				throw new GeneralException("Debes introducir un email");
			String pwd = jso.getString("pwd");
			User user = userDao.findByEmailAndPwd(email, DigestUtils.sha512Hex(pwd));
			if (user == null)
				throw new GeneralException("Credenciales invalidas");
			request.getSession().setAttribute("userEmail", email);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
		}

	}

	@GetMapping("/isLogin")
	public boolean isLogin(HttpServletRequest request) {
		try {
			String email = (String) request.getSession().getAttribute("userEmail");
			return email!=null;
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
		}

	}
	
	@PutMapping("/register")
	public void register(@RequestBody final Map<String, Object> info) {
		try {
			final JSONObject jso = new JSONObject(info);
			final String userName = jso.optString("userName");
			if (userName.length() == 0)
				throw new GeneralException("Debes introducir un nombre de usuario");
			final String email = jso.optString("email");
			if (email.length() == 0)
				throw new GeneralException("Debes introducir un correo valido");
			final String pwd1 = jso.optString("pwd1");
			final String pwd2 = jso.optString("pwd2");
			String rqpwd = requisitosPwd(pwd1, pwd2);
			if (!rqpwd.equals("")) {
				throw new GeneralException(rqpwd);
			}
			final User user = new User();
			user.setEmail(email);
			user.setPwd(pwd1);
			user.setFoto(jso.optString("picture"));
			userDao.save(user);
		} catch (final Exception e) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
		}
	}

	private String requisitosPwd(String pwd1, String pwd2) {
		if (pwd1.length() == 0)
			return "Debes introducir una contraseña";
		if (pwd2.length() == 0)
			return "Debes introducir la contraseña otra vez";
		if (!pwd1.equals(pwd2))
			return "Error: las contraseñas no coinciden";
		if (pwd1.length() < 4)
			return "Error: contraseña es demasiado corta, 4 caracteres minimo";
		return "";
	}

}
