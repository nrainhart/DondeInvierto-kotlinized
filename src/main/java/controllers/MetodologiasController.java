package controllers;

import dondeInvierto.dominio.empresas.Empresa;
import dondeInvierto.dominio.metodologias.ResultadoEvaluacionDeMetodologias;
import repo.RepositorioEmpresas;
import dondeInvierto.dominio.metodologias.Metodologia;
import repo.RepositorioUsuarios;
import dondeInvierto.dominio.Usuario;
import spark.ModelAndView;
import spark.Request;
import spark.Response;

import java.time.LocalDate;
import java.util.*;
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
		int anioActual = LocalDate.now().getYear();
		ResultadoEvaluacionDeMetodologias resultadoEvaluacionDeMetodologias = metodologiaAEvaluar.resultadoPara(empresas, anioActual);
		model.put("empresasOrdenadas",nombresDeEmpresas(resultadoEvaluacionDeMetodologias.getEmpresasOrdenadas()));
		model.put("empresasQueNoCumplen",nombresDeEmpresas(resultadoEvaluacionDeMetodologias.getEmpresasQueNoCumplen()));
		model.put("empresasSinDatos",nombresDeEmpresas(resultadoEvaluacionDeMetodologias.getEmpresasSinDatos()));
		model.put("nombreMetodologia", metodologiaAEvaluar.getNombre());
		return new ModelAndView(model, "metodologias/metodologiaEvaluada.hbs");
	}

	private static List<String> nombresDeEmpresas(List<Empresa> listaEmpresas) {
		if(listaEmpresas.isEmpty()){
			return Collections.singletonList("No hay empresas en esta categoria");
		}
		return listaEmpresas.stream().map(empresa -> empresa.getNombre()).collect(Collectors.toList());
	}

}
