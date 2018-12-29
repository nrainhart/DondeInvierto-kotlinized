package defaultPackage;

import dominio.empresas.ArchivoXLS;
import dominio.empresas.Empresa;
import dominio.indicadores.Indicador;
import dominio.indicadores.IndicadorPrecalculado;
import dominio.usuarios.Usuario;
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

public class CacheTest extends AbstractPersistenceTest implements WithGlobalEntityManager,TransactionalOps {

	private Usuario usuario = new Usuario("admin","admin");
	private List<Indicador> indicadores = new ArrayList<>();
	private List<Empresa> empresasParaIndicadores;
	private Indicador ingresoNeto;
	private Empresa sony;
	private Empresa google;
	private Empresa apple;
	
	@Before
	public void setUp() {
		Usuario.Companion.setActivo(usuario);
		withTransaction(() -> usuario.agregarIndicadores(Arrays.asList(
				"INGRESONETO = netooperacionescontinuas + netooperacionesdiscontinuas",
				"SALDOCRUDO = cuentarara + fds")));
		indicadores = usuario.getIndicadores();
		ArchivoXLS archivoEjemploIndicadores = new ArchivoXLS("src/test/resources/EjemploIndicadores.xls");
		archivoEjemploIndicadores.leerEmpresas();		
		empresasParaIndicadores = archivoEjemploIndicadores.getEmpresas();
		ingresoNeto = indicadores.get(0);
		sony = empresasParaIndicadores.get(0);
		google = empresasParaIndicadores.get(1);
		apple = empresasParaIndicadores.get(2);
		
	}

	@Test
	public void elIndicadorCacheaElResultadoLuegoDeCalcularlo() {
		IndicadorPrecalculado resultado = new IndicadorPrecalculado(sony,Year.of(2017),17000);
		ingresoNeto.evaluarEn(sony, Year.of(2017));
		assertTrue(ingresoNeto.getResultados().contains(resultado));
	}
	
	@Test
	public void elIndicadorNoCacheaDosVecesElResultadoLuegoDeCalcularlo() {
		ingresoNeto.evaluarEn(sony, Year.of(2017));
		ingresoNeto.evaluarEn(sony, Year.of(2017));
		assertEquals(ingresoNeto.getResultados().size(),1);
	}
	
	@Test
	public void elIndicadorDevuelveElResultadoCorrectoSiEstaCacheado() {
		ingresoNeto.evaluarEn(sony, Year.of(2017));
		IndicadorPrecalculado resultadoCacheado = ingresoNeto.getResultados().get(0);
		assertEquals(resultadoCacheado.getValor(),ingresoNeto.evaluarEn(sony, Year.of(2017)));
	}
}
