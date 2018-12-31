package controllers;

import dondeInvierto.dominio.indicadores.Indicador;
import repo.RepositorioUsuarios;
import dominio.usuarios.Usuario;
import excepciones.EntidadExistenteError;
import excepciones.ParserError;
import org.uqbarproject.jpa.java8.extras.WithGlobalEntityManager;
import org.uqbarproject.jpa.java8.extras.transaction.TransactionalOps;
import spark.ModelAndView;
import spark.Request;
import spark.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IndicadoresController implements WithGlobalEntityManager, TransactionalOps{
	public ModelAndView listar(Request req, Response res) {
		Map<String, List<Indicador>> model = new HashMap<>();
		Usuario usuarioActivo = new RepositorioUsuarios().obtenerPorId(Long.parseLong(req.cookie("idUsuario")));
		List<Indicador> indicadores = usuarioActivo.getIndicadores();
		model.put("indicadores", indicadores);
		return new ModelAndView(model, "indicadores/indicadores.hbs");
	}
	
	public ModelAndView newForm(Request req, Response res) {
		String mensajeError = req.cookie("mensajeError");
		String formulaIndicador = req.cookie("formulaIndicador");
		if (mensajeError == null){
			return new ModelAndView(null, "indicadores/crearIndicador.hbs");
		}
		Map<String, String> model = new HashMap<>();
		model.put("mensajeError", mensajeError);
		model.put("formulaIndicador", formulaIndicador);
		res.removeCookie("mensajeError");
		res.removeCookie("formulaIndicador");
		return new ModelAndView(model,"indicadores/crearIndicadorError.hbs");
	}

	public Void create(Request req, Response res) {
		long idUsuario = Long.parseLong(req.cookie("idUsuario"));
		Usuario usuarioActivo = new RepositorioUsuarios().obtenerPorId(idUsuario);
		String formulaIndicador = req.queryParams("indicador");
		try {
			withTransaction(()-> {
				usuarioActivo.crearIndicador(formulaIndicador);
			});
			res.redirect("/indicadores");
		} catch (EntidadExistenteError | ParserError e){
			res.cookie("mensajeError", e.getMessage());
			res.cookie("formulaIndicador", formulaIndicador);
			res.redirect("/indicadores/new");
		}
		return null;
	}
}