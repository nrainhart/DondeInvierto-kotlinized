package dominio.indicadores;

import dominio.AbstractLocalRepository;
import dominio.empresas.Empresa;
import dominio.parser.ParserIndicadores;

import javax.persistence.Entity;
import java.time.Year;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
public class RepositorioIndicadores extends AbstractLocalRepository<Indicador> {
	
	public void agregarMultiplesIndicadores(List<String> strIndicadores) {
		List<Indicador> indicadoresNuevos = obtenerIndicadoresParseados(strIndicadores);//NO ESTOY CATCHEANDO ParserError (!!!)
		indicadoresNuevos.forEach(ind -> agregar(ind));
	}
	
	public void eliminarIndicadores(){
		elementos.clear();
	}
	
	public void eliminarResultadosPrecalculados(){
		obtenerTodos().forEach(ind -> ind.eliminarResultadosPrecalculados());
	}

	@Override
	protected String mensajeEntidadExistenteError(Indicador elemento) {
		return "Ya existe un indicador con el nombre " + elemento.getNombre();
	}

	public List<Indicador> todosLosIndicadoresAplicablesA(Empresa empresa) {
		List<Indicador> indicadoresAplicables = new ArrayList<Indicador>();
		Set<Year> aniosDeCuentas = empresa.aniosDeLosQueTieneCuentas();
		aniosDeCuentas.forEach(anio -> indicadoresAplicables.addAll(this.indicadoresAplicablesA(empresa, anio)));
		return indicadoresAplicables;
	}

	public Set<Indicador> indicadoresAplicablesA(Empresa empresa, Year anio) {
		Set<Indicador> indicadoresAplicables = new HashSet<Indicador>();
		obtenerTodos().stream().filter(ind -> ind.esAplicableA(empresa, anio))
				.forEach(ind -> indicadoresAplicables.add(ind));
		return indicadoresAplicables;
	}

	public Indicador buscarIndicador(String nombreIndicador) {
		return obtenerTodos().stream().filter(ind -> ind.seLlama(nombreIndicador)).findFirst()
				.orElseThrow(() -> new NoExisteIndicadorError("No se pudo encontrar un indicador con ese nombre."));
	}
	
	private List<Indicador> obtenerIndicadoresParseados(List<String> strIndicadores){
		Stream<Indicador> indicadores = strIndicadores.stream().map(strInd -> ParserIndicadores.parse(strInd)).filter(ind -> !existe(ind));
		return indicadores.collect(Collectors.toList());
	}

}

class NoExisteIndicadorError extends RuntimeException {NoExisteIndicadorError(String e) {super(e);}}