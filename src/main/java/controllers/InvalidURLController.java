package controllers;

import org.uqbarproject.jpa.java8.extras.WithGlobalEntityManager;
import org.uqbarproject.jpa.java8.extras.transaction.TransactionalOps;
import spark.ModelAndView;
import spark.Request;
import spark.Response;

public class InvalidURLController implements WithGlobalEntityManager, TransactionalOps{
	public static ModelAndView mostrar(Request req, Response res) {
		return new ModelAndView(null, "invalidURL/invalidURL.hbs");
	}
}