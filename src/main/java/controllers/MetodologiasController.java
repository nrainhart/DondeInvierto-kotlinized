package controllers;

import dominio.empresas.Empresa;
import dominio.empresas.RepositorioEmpresas;
import dominio.metodologias.Metodologia;
import dominio.usuarios.RepositorioUsuarios;
import dominio.usuarios.Usuario;
import spark.ModelAndView;
import spark.Request;
import spark.Response;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MetodologiasController{
	public static ModelAndView listar (Request req, Response res) {
		Map<String, List<Metodologia>> model = new HashMap<>();
		Usuario usuarioActivo = new RepositorioUsuarios().obtenerPorId(Long.parseLong(req.cookie("idUsuario")));
		List<Metodologia> metodologias = usuarioActivo.getMetodologias();
		model.put("metodologias", metodologias);
		return new ModelAndView(model, "metodologias/metodologias.hbs");
	}
	
	public static ModelAndView mostrar (Request req, Response res) {
		Map<String, Object> model = new HashMap<>();
		Usuario usuarioActivo = new RepositorioUsuarios().obtenerPorId(Long.parseLong(req.cookie("idUsuario")));
		Metodologia metodologiaAEvaluar = usuarioActivo.obtenerMetodologiaPorId(Long.parseLong(req.params("id")));
		List<Empresa> empresas = new RepositorioEmpresas().obtenerTodos();
		model.put("empresasOrdenadas",nombresDeEmpresas(metodologiaAEvaluar.evaluarPara(empresas)));
		model.put("empresasQueNoCumplen",nombresDeEmpresas(metodologiaAEvaluar.empresasQueNoCumplenTaxativas(empresas)));
		model.put("empresasSinDatos",nombresDeEmpresas(metodologiaAEvaluar.empresasConDatosFaltantes(empresas)));
		model.put("nombreMetodologia", metodologiaAEvaluar.getNombre());
		return new ModelAndView(model, "metodologias/metodologiaEvaluada.hbs");
	}

	private static List<String> nombresDeEmpresas(List<Empresa> listaEmpresas) {
		if(listaEmpresas.isEmpty()){
			return Arrays.asList(new String[] {"No hay empresas en esta categoria"});
		}
		return listaEmpresas.stream().map(empresa -> empresa.getNombre()).collect(Collectors.toList());
	}

}
