package controllers;

import repo.RepositorioUsuarios;
import excepciones.NoExisteUsuarioError;
import spark.ModelAndView;
import spark.Request;
import spark.Response;

import java.util.HashMap;
import java.util.Map;

public class LoginController{
	public ModelAndView login(Request req, Response res) {
		String mensajeError = req.cookie("mensajeError");
		if (mensajeError == null) return new ModelAndView(null, "login/login.hbs");
		Map<String, String> model = new HashMap<>();
		model.put("mensajeError", mensajeError);
		res.removeCookie("mensajeError");
		return new ModelAndView(model, "login/loginError.hbs");
	}

	public Void validate(Request req, Response res) {
		String email = req.queryParams("email");
		String password = req.queryParams("password");
		try{
			Long idUsuario = new RepositorioUsuarios().obtenerId(email, password);
			res.cookie("email", email);
			res.cookie("idUsuario", Long.toString(idUsuario));
			res.redirect("/home");
		} catch (NoExisteUsuarioError e){
			res.cookie("mensajeError", e.getMessage());
			res.redirect("/");
		}
		return null;
	}
}