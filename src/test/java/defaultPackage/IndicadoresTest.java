package defaultPackage;

import dominio.empresas.ArchivoXLS;
import dominio.empresas.Empresa;
import dominio.indicadores.Indicador;
import dominio.usuarios.Usuario;
import excepciones.EntidadExistenteError;
import org.junit.Before;
import org.junit.Test;
import org.uqbarproject.jpa.java8.extras.WithGlobalEntityManager;
import org.uqbarproject.jpa.java8.extras.test.AbstractPersistenceTest;
import org.uqbarproject.jpa.java8.extras.transaction.TransactionalOps;

import java.time.Year;
import java.util.*;

import static org.junit.Assert.*;

public class IndicadoresTest extends AbstractPersistenceTest implements WithGlobalEntityManager, TransactionalOps{

	List<Indicador> indicadores = new ArrayList<Indicador>();
	List<Empresa> empresasParaIndicadores;
	Usuario usuario = new Usuario("admin","admin");
	
	@Before
	public void setUp() {
		withTransaction(() -> {
			usuario.agregarIndicadores(Arrays.asList(new String[] { 
					"INGRESONETO = netooperacionescontinuas + netooperacionesdiscontinuas",
					"INDICADORDOS = cuentarara + fds",
					"INDICADORTRES = INGRESONETO * 10 + ebitda",
					"A = 5 / 3", "PRUEBA = ebitda + 5" }));
		});
		Usuario.activo(usuario);
		indicadores = usuario.getIndicadores();
		ArchivoXLS archivoEjemploIndicadores = new ArchivoXLS("src/test/resources/EjemploIndicadores.xls");
		archivoEjemploIndicadores.leerEmpresas();		
		empresasParaIndicadores = archivoEjemploIndicadores.getEmpresas();
	}
	
	@Test
	public void elArchivoIndicadoresLeeCorrectamente() {
		Set<Indicador> indicadoresActuales = new HashSet<Indicador>(indicadores);
		Set<Indicador> indicadoresEsperados = new HashSet<Indicador>(Arrays.asList(new Indicador[] {
			new Indicador("INDICADORDOS"),
			new Indicador("A"),
			new Indicador("INGRESONETO"),
			new Indicador("PRUEBA"),
			new Indicador("INDICADORTRES")}));		
		boolean todosLosIndicadoresSonLosEsperados = indicadoresActuales.equals(indicadoresEsperados);
		assertTrue(todosLosIndicadoresSonLosEsperados);
	}

	@Test
	public void elArchivoIndicadoresLee5Renglones() {
		assertEquals(5, indicadores.size());
	}

	@Test
	public void elIndicadorIngresoNetoSeAplicaCorrectamenteALasEmpresas() {
		int resultadosEsperados[] = {7000, 3000, 11000};
		Indicador ingresoNeto = usuario.buscarIndicador("ingresoNeto");
		int resultados[] = this.resultadosLuegoDeAplicarIndicadorAEmpresas(ingresoNeto);
		assertTrue(Arrays.equals(resultadosEsperados, resultados));
	}
	
	@Test
	public void unIndicadorCompuestoPorIndicadorCuentaYNumeroSeAplicaCorrectamente(){
		Indicador indicadorTres = usuario.buscarIndicador("indicadorTres");
		int resultadosEsperados[] = {190000,330000,260000};
		int resultados[] = this.resultadosLuegoDeAplicarIndicadorAEmpresas(indicadorTres);
		assertTrue(Arrays.equals(resultadosEsperados, resultados));
	}
	
	@Test(expected = EntidadExistenteError.class)
	public void siGuardoDosVecesElMismoIndicadorFalla() {
		usuario.crearIndicador("INGRESONETO = ebitda + 2");
		usuario.crearIndicador("INGRESONETO = ebitda + 2");
	}
	
	@Test
	public void elIndicadorDosEsInaplicableAEmpresaLocaEn2016PorInexistenciaDeCuenta(){
		Empresa empresaLoca = empresasParaIndicadores.get(1);
		Indicador indicadorDos = usuario.buscarIndicador("indicadorDos");
		assertFalse(indicadorDos.esAplicableA(empresaLoca, obtenerAnio(2016)));
	}
	
	@Test
	public void soloDosIndicadoresSonAplicablesAEmpresaLocaEn2014(){
		Empresa empresaLoca = empresasParaIndicadores.get(1);
		int cantidadIndicadores = cantidadIndicadoresAplicablesSegunAnio(empresaLoca, obtenerAnio(2014));
		assertEquals(4,cantidadIndicadores);
	}
	
	@Test
	public void laCantidadDeIndicadoresAplicablesAEmpresaReLocaSonCinco(){
		Empresa empresaReLoca = empresasParaIndicadores.get(2);
		int cantidadIndicadores = cantidadIndicadoresAplicablesA(empresaReLoca);
		assertEquals(5,cantidadIndicadores);
	}
	
	@Test
	public void laCantidadDeIndicadoresAplicablesAEmpresaReLocaEn2016SonCuatro(){
		Empresa empresaReLoca = empresasParaIndicadores.get(2);
		empresaReLoca.resultadosParaEstosIndicadores(usuario.todosLosIndicadoresAplicablesA(empresaReLoca)).size();
		int cantidadIndicadores = cantidadIndicadoresAplicablesSegunAnio(empresaReLoca, obtenerAnio(2016));
		assertEquals(4,cantidadIndicadores);
	}

	/* ------------------------------- METODOS AUXILIARES  ------------------------------- */
	
	private int cantidadIndicadoresAplicablesA(Empresa empresa) {
		Set<Indicador> indicadAplicables = new HashSet<Indicador>(usuario.todosLosIndicadoresAplicablesA(empresa));
		return indicadAplicables.size();
	}
	
	private int cantidadIndicadoresAplicablesSegunAnio(Empresa empresa, Year anio) {
		return usuario.indicadoresAplicablesA(empresa, anio).size();
	}
	
	private int[] resultadosLuegoDeAplicarIndicadorAEmpresas(Indicador ind){
		int resultados[] = new int[3];
		Empresa miEmpresa = empresasParaIndicadores.get(0);
		Empresa EmpresaLoca = empresasParaIndicadores.get(1);
		Empresa EmpresaReLoca = empresasParaIndicadores.get(2);
		resultados[0] = ind.evaluarEn(miEmpresa, Year.of(2015));
		resultados[1] = ind.evaluarEn(EmpresaLoca, Year.of(2014));
		resultados[2] = ind.evaluarEn(EmpresaReLoca, Year.of(2016));
		return resultados;
	}
	
	private Year obtenerAnio(int anio) {
		return Year.of(anio);
	}

}

