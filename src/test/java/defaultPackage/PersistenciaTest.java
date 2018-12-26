package defaultPackage;

import dominio.empresas.Cuenta;
import dominio.empresas.Empresa;
import dominio.empresas.RepositorioEmpresas;
import dominio.indicadores.Indicador;
import dominio.indicadores.RepositorioIndicadores;
import dominio.metodologias.*;
import dominio.parser.ParserIndicadores;
import excepciones.EntidadExistenteError;
import org.junit.Before;
import org.junit.Test;
import org.uqbarproject.jpa.java8.extras.WithGlobalEntityManager;
import org.uqbarproject.jpa.java8.extras.test.AbstractPersistenceTest;
import org.uqbarproject.jpa.java8.extras.transaction.TransactionalOps;

import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PersistenciaTest extends AbstractPersistenceTest implements WithGlobalEntityManager,TransactionalOps {

	private RepositorioEmpresas repoEmpresas;
	private RepositorioIndicadores repoIndicadores;
	private RepositorioMetodologias repoMetodologias;
	private List<Cuenta> listaCuentas1;
	private List<Cuenta> listaCuentas2;
	private List<Empresa> listaEmpresas;
	private List<String> listaIndicadores;
	private List<Metodologia> listaMetodologias;

	@Before
	public void setUp() {
		repoIndicadores = new RepositorioIndicadores();
		withTransaction(() -> repoIndicadores.agregarMultiplesIndicadores(Arrays.asList(
				"INGRESONETO = netooperacionescontinuas + netooperacionesdiscontinuas",
				"INDICADORDOS = cuentarara + fds",
				"INDICADORTRES = INGRESONETO * 10 + ebitda",
				"A = 5 / 3",
				"PRUEBA = ebitda + 5")));
		repoEmpresas = new RepositorioEmpresas();
		repoMetodologias = new RepositorioMetodologias();
		listaEmpresas = new ArrayList<>();
		listaCuentas1 = new ArrayList<>();
		listaCuentas2 = new ArrayList<>();
		listaIndicadores = new ArrayList<>();
		listaMetodologias = new ArrayList<>();

		listaCuentas1.add(new Cuenta(Year.of(2015), "EBITDA", 2000));
		listaCuentas1.add(new Cuenta(Year.of(2014), "FDS", 3000));
		listaCuentas2.add(new Cuenta(Year.of(2013), "EBITDA", 6000));
		listaCuentas2.add(new Cuenta(Year.of(2010), "FDS", 8000));
		listaCuentas2.add(new Cuenta(Year.of(2014), "EBITDA", 8000));
		listaEmpresas.add(new Empresa("empresa1", listaCuentas1));
		listaEmpresas.add(new Empresa("empresa2", listaCuentas2));
		listaIndicadores.add("UNINDICADOR = ebitda + fds - 2");
		listaIndicadores.add("OTROINDICADOR = ebitda * 2 + fds - 2500");
		listaMetodologias.add(obtenerMetodologia1("Metodologia1"));
		listaMetodologias.add(obtenerMetodologia2("Metodologia2"));
	}

	@Test
	public void alAgregarDosEmpresasAlRepositorioEmpresasEstasSePersistenCorrectamente() {
		withTransaction(() -> repoEmpresas.agregarMultiplesEmpresas(listaEmpresas));
		assertTrue(repoEmpresas.obtenerTodos().containsAll(listaEmpresas));
	}
	
	@Test
	public void alPersistirDosEmpresasConElMismoNombreSeActualizaLaMisma() {
		Empresa empresaCopia = new Empresa("empresa1", listaCuentas2);
		withTransaction(() -> {
			listaEmpresas.add(empresaCopia);
			repoEmpresas.agregarMultiplesEmpresas(listaEmpresas);
		});
		assertEquals(listaEmpresas.size() - 1, repoEmpresas.obtenerTodos().size());
	}

	@Test
	public void alAgregarDosIndicadoresAlRepositorioIndicadoresEstosSePersistenEsaCantidad() {
		int cantidadAntesDeAgregar = repoIndicadores.obtenerTodos().size();
		withTransaction(() -> repoIndicadores.agregarMultiplesIndicadores(listaIndicadores));
		assertEquals(cantidadAntesDeAgregar + 2, repoIndicadores.obtenerTodos().size());
	}

	@Test
	public void alAgregarDosIndicadoresAlRepositorioIndicadoresEstosSePersistenCorrectamente() {
		List<Indicador> indicadores = crearIndicadoresAPartirDeSusExpresiones(listaIndicadores);
		repoIndicadores.agregarMultiplesIndicadores(listaIndicadores);
		assertTrue(repoIndicadores.obtenerTodos().containsAll(indicadores));
	}

	@Test(expected = EntidadExistenteError.class)
	public void noSePersistenDosIndicadoresConElMismoNombre() {
		listaIndicadores.add("UNINDICADOR = 2 * 3 * 6 * ebitda + fds");
		repoIndicadores.agregarMultiplesIndicadores(listaIndicadores);
	}

	@Test
	public void alAgregarDosMetodologiasAlRepositorioMetodologiasEstosSePersistenEsaCantidad() {
		int cantidadAntesDeAgregar = repoMetodologias.obtenerTodos().size();
		withTransaction(() -> listaMetodologias.forEach(metodologia -> repoMetodologias.agregar(metodologia)));
		assertEquals(cantidadAntesDeAgregar + 2, repoMetodologias.obtenerTodos().size());
	}

	@Test
	public void alAgregarDosMetodologiasAlRepositorioMetodologiasEstosSePersistenCorrectamente() {
		withTransaction(() -> listaMetodologias.forEach(metodologia -> repoMetodologias.agregar(metodologia)));
		assertTrue(repoMetodologias.obtenerTodos().containsAll(listaMetodologias));
	}

	@Test(expected = EntidadExistenteError.class)
	public void siPersistoDosVecesLaMismaMetodologiaFalla() {
		repoMetodologias.agregar(obtenerMetodologia1("Metodologia1"));
		repoMetodologias.agregar(obtenerMetodologia1("Metodologia1"));
	}


	// ************ METODOS AUXILIARES************//
	private List<Indicador> crearIndicadoresAPartirDeSusExpresiones(List<String> expresiones) {
		List<Indicador> indicadores = new ArrayList<>();
		expresiones.forEach(exp -> indicadores.add(ParserIndicadores.parse(exp)));
		return indicadores;
	}

	private Metodologia obtenerMetodologia1(String nombreMetodologia) {
		Indicador ingresoNeto = repoIndicadores.buscarIndicador("ingresoNeto");
		Indicador indicadorDos = repoIndicadores.buscarIndicador("indicadorDos");
		Metodologia metodologia = new Metodologia(nombreMetodologia);
		CondicionTaxativa condTax = new CondicionTaxativa(
				new OperandoCondicion(OperacionAgregacion.Promedio, ingresoNeto, 2), OperacionRelacional.Mayor, 10000);
		CondicionPrioritaria condPrior = new CondicionPrioritaria(
				new OperandoCondicion(OperacionAgregacion.Sumatoria, indicadorDos, 2), OperacionRelacional.Mayor);
		agregarCondiciones(metodologia, condTax, condPrior);
		return metodologia;
	}

	private Metodologia obtenerMetodologia2(String nombreMetodologia) {
		Indicador prueba = repoIndicadores.buscarIndicador("prueba");
		Metodologia metodologia = new Metodologia(nombreMetodologia);
		CondicionTaxativa condTax = new CondicionTaxativa(new OperandoCondicion(OperacionAgregacion.Ultimo, prueba, 1),
				OperacionRelacional.Mayor, 0);
		CondicionPrioritaria condPrior = new CondicionPrioritaria(
				new OperandoCondicion(OperacionAgregacion.Ultimo, prueba, 1), OperacionRelacional.Mayor);
		agregarCondiciones(metodologia, condTax, condPrior);
		return metodologia;
	}
	
	private void agregarCondiciones(Metodologia metodologia, CondicionTaxativa condTax, CondicionPrioritaria condPrior){
		metodologia.agregarCondicionTaxativa(condTax);
		metodologia.agregarCondicionPrioritaria(condPrior);
	}
}
