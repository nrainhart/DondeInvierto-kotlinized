package server;

import spark.Request;
import spark.Response;

public class FiltroAutenticacion {
	public static void validarLogueo(Request req, Response res){
		if(!esPantallaDeLogin(req) && !hayUsuarioLogueado(req)){
			res.redirect("/");
		}
	}

	private static boolean hayUsuarioLogueado(Request req) {
		return req.cookie("idUsuario") != null;
	}

	private static boolean esPantallaDeLogin(Request req) {
		return req.pathInfo().equals("/");
	}
}
