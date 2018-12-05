package controllers;

import spark.ModelAndView;
import spark.Request;
import spark.Response;

import java.util.Map;

public class HomeController {
	public static ModelAndView home(Request req, Response res) {
		Map<String, String> model = req.cookies();
		return new ModelAndView(model, "home/home.hbs");
	}

}