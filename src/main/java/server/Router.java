package server;

import controllers.*;
import spark.Spark;
import spark.template.handlebars.HandlebarsTemplateEngine;
import spark.utils.BooleanHelper;
import spark.utils.HandlebarsTemplateEngineBuilder;

public class Router {

	public static void configure() {
		HandlebarsTemplateEngine engine = HandlebarsTemplateEngineBuilder.create().withDefaultHelpers()
				.withHelper("isTrue", BooleanHelper.isTrue).build();

		Spark.staticFiles.location("/public");

		Spark.before(FiltroAutenticacion::validarLogueo);
		Spark.after(new GestorEntityManager()::cerrarEntityManager);
		Spark.get("/", new LoginController()::login, engine);
		Spark.post("/", new LoginController()::validate);
		Spark.get("/home", HomeController::home, engine);
		Spark.get("/empresas", EmpresasController::listar, engine);
		Spark.get("/empresas/:id", EmpresasController::mostrar, engine);
		Spark.get("/indicadores", new IndicadoresController()::listar, engine);
		Spark.get("/indicadores/new", new IndicadoresController()::newForm, engine);
		Spark.post("/indicadores/new", new IndicadoresController()::create);
		Spark.get("/metodologias", MetodologiasController::listar, engine);
		Spark.get("/metodologias/:id", MetodologiasController::mostrar, engine);
		Spark.get("/*", InvalidURLController::mostrar,engine);
	}

}