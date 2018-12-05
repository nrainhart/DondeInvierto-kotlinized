package dominio.metodologias;

import dominio.AbstractLocalRepository;

import javax.persistence.Entity;
import java.util.List;

@Entity
public class RepositorioMetodologias extends AbstractLocalRepository<Metodologia> {

	public void agregarMetodologias(List<Metodologia> metodologias) {
		metodologias.forEach(metodologia -> agregar(metodologia));
	}
	
	@Override
	protected String mensajeEntidadExistenteError(Metodologia elemento) {
		return "Ya existe una metodología con el nombre " + elemento.getNombre();
	}

	public Metodologia obtenerPorId(Long id) {
		return obtenerTodos().stream().filter(metodologia -> metodologia.getId().equals(id)).findFirst()
				.orElseThrow(null);//Estoy seguro que la metodologia va a existir
	}

}