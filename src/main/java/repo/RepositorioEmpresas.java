package repo;

import dondeInvierto.dominio.empresas.Empresa;
import repo.AbstractRepository;

import java.util.List;

public class RepositorioEmpresas extends AbstractRepository<Empresa> {

	public boolean hayEmpresas() {
		return obtenerTodos().size() > 0;
	}

	public void agregarMultiplesEmpresas(List<Empresa> empresas) {
		for (Empresa empresa: empresas){
			if (!existe(empresa)) agregar(empresa);
			else {
				List<Empresa> empresasExistentes = obtenerTodos();
				Empresa empresaAActualizar = empresasExistentes.get(empresasExistentes.indexOf(empresa));
				empresaAActualizar.actualizar(empresa);
			}
		}
	}

	@Override
	protected Class<Empresa> tipoEntidad() {
		return Empresa.class;
	}

	@Override
	protected String mensajeEntidadExistenteError(Empresa elemento) {
		return "Ya existe una empresa con el nombre " + elemento.getNombre();
	}
	
}