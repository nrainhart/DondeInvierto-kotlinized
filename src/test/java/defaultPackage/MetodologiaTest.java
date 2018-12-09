package defaultPackage;

import dominio.empresas.ArchivoXLS;
import dominio.empresas.Empresa;
import dominio.indicadores.Indicador;
import dominio.metodologias.*;
import dominio.parser.ParserIndicadores;
import excepciones.NoExisteCuentaError;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class MetodologiaTest {

	private List<Empresa> empresasParaComparacionConMetodologias;
	private Empresa Sony;
	private Empresa Google;
	private Empresa Apple;
	private Empresa Deloitte;
	private Empresa IBM;
	private Empresa Falabella;
	private Indicador ingresoNeto;
	private Indicador saldoCrudo;
	private Metodologia metodologiaDeWarren;
	private Metodologia metodologiaDeMike;
	private Metodologia metodologiaDeSteve;
	
	@Before
	public void setUp() {
		ArchivoXLS archivoEjemploIndicadores = new ArchivoXLS("src/test/resources/EjemploIndicadores.xls");
		ArchivoXLS archivoEjemploMetodologias = new ArchivoXLS("src/test/resources/EjemploMetodologias.xls");
		archivoEjemploIndicadores.leerEmpresas();
		archivoEjemploMetodologias.leerEmpresas();
		List<Empresa> empresasParaIndicadores = archivoEjemploIndicadores.getEmpresas();
		empresasParaComparacionConMetodologias = archivoEjemploMetodologias.getEmpresas();
		Sony = empresasParaIndicadores.get(0);
		Google = empresasParaIndicadores.get(1);
		Apple = empresasParaIndicadores.get(2);
		Deloitte = empresasParaComparacionConMetodologias.get(0);
		IBM = empresasParaComparacionConMetodologias.get(1);
		Falabella = empresasParaComparacionConMetodologias.get(2);
		ingresoNeto = parsear("INGRESONETO = netooperacionescontinuas + netooperacionesdiscontinuas");
		saldoCrudo = parsear("SALDOCRUDO = cuentarara + fds");
		metodologiaDeWarren = new Metodologia("Warren");
		CondicionTaxativa condWarren = new CondicionTaxativa(new OperandoCondicion(OperacionAgregacion.Promedio,ingresoNeto,2), OperacionRelacional.Mayor, 10000);
		metodologiaDeWarren.agregarCondicionTaxativa(condWarren);
		metodologiaDeMike = new Metodologia("Mike"); // MetodologiaDeMike
		CondicionTaxativa condMike = new CondicionTaxativa(new OperandoCondicion(OperacionAgregacion.Sumatoria,ingresoNeto,3), OperacionRelacional.Mayor, 20000);
		metodologiaDeMike.agregarCondicionTaxativa(condMike);
		metodologiaDeSteve = new Metodologia("Steve"); // UnaMetodologiaConCondTaxINGRESONETOConPromedioMayorA10000YConCondPriorSaldoCrudoConSumatoriaAmbosEnUltimosDosAnios
		CondicionTaxativa condTaxSteve = new CondicionTaxativa(new OperandoCondicion(OperacionAgregacion.Promedio,ingresoNeto,2), OperacionRelacional.Mayor, 10000);
		CondicionPrioritaria condPriorSteve = new CondicionPrioritaria(new OperandoCondicion(OperacionAgregacion.Variacion,saldoCrudo,2), OperacionRelacional.Mayor);
		metodologiaDeSteve.agregarCondicionTaxativa(condTaxSteve);
		metodologiaDeSteve.agregarCondicionPrioritaria(condPriorSteve);
		//Ver formas de testear métodos que usan fecha actual (!!!)
	}

	private Indicador parsear(String expresion) {
		return ParserIndicadores.parse(expresion);
	}

	@Test
	public void SonyEsMasAntiguaQueApple() {
		assertTrue(new CondicionPrioritaria(new OperandoCondicion(OperacionAgregacion.Ultimo, new Antiguedad(), 1), OperacionRelacional.Mayor).esMejorQue(Sony, Apple));
	}
	
	@Test
	public void SonyCumpleCondTaxAntiguedadMenorA10() {
		assertTrue(new CondicionTaxativa(new OperandoCondicion(OperacionAgregacion.Ultimo, new Antiguedad(), 1), OperacionRelacional.Menor, 10).laCumple(Sony));
	}
	
	@Test
	public void AppleNoCumpleCondTaxAntiguedadMayorA3() {
		assertFalse(new CondicionTaxativa(new OperandoCondicion(OperacionAgregacion.Ultimo, new Antiguedad(), 1), OperacionRelacional.Mayor, 3).laCumple(Apple));
	}
	
	@Test
	public void AppleCumpleCondTaxAntiguedadIgualA1(){
		assertTrue(new CondicionTaxativa(new OperandoCondicion(OperacionAgregacion.Ultimo, new Antiguedad(), 1), OperacionRelacional.Igual, 1).laCumple(Apple));
	}
	
	@Test
	public void AppleNOCumpleCondTaxIndicadorSaldoCrudoMayorA360000() {
		OperandoCondicion operando = new OperandoCondicion(OperacionAgregacion.Ultimo,saldoCrudo,1);
		CondicionTaxativa cond = new CondicionTaxativa(operando, OperacionRelacional.Mayor, 360000);
		assertFalse(cond.laCumple(Apple));
	}
	
	@Test
	public void AppleNOCumpleCondTaxIndicadorSaldoCrudoMenorA200000(){
		OperandoCondicion operando = new OperandoCondicion(OperacionAgregacion.Ultimo,saldoCrudo,1);
		CondicionTaxativa cond = new CondicionTaxativa(operando, OperacionRelacional.Menor, 200000);
		assertFalse(cond.laCumple(Apple));
	}
	
	@Test
	public void AppleCumpleCondTaxIndicadorSaldoCrudoIgualA30000(){
		OperandoCondicion operando = new OperandoCondicion(OperacionAgregacion.Ultimo,saldoCrudo,1);
		CondicionTaxativa cond = new CondicionTaxativa(operando, OperacionRelacional.Igual, 300000);
		assertTrue(cond.laCumple(Apple));
	}
	
	@Test
	public void SonyNOCumpleCondTaxIndicadorINGRESONETOMayorA20000EnUltimoAnio() {
		CondicionTaxativa cond = new CondicionTaxativa(new OperandoCondicion(OperacionAgregacion.Ultimo,ingresoNeto,1), OperacionRelacional.Mayor, 20000);
		assertFalse(cond.laCumple(Sony));
	}
	
	@Test
	public void SonyNOCumpleCondTaxIndicadorINGRESONETOMenorA8000EnUltimoAnio(){
		CondicionTaxativa cond = new CondicionTaxativa(new OperandoCondicion(OperacionAgregacion.Ultimo,ingresoNeto,1), OperacionRelacional.Menor, 8000);
		assertFalse(cond.laCumple(Sony));
	}
	
	@Test
	public void SonyCumpleCondTaxIndicadorINGRESONETOIgualA17000EnUltimoAnio(){
		CondicionTaxativa cond = new CondicionTaxativa(new OperandoCondicion(OperacionAgregacion.Ultimo,ingresoNeto,1), OperacionRelacional.Igual, 17000);
		assertTrue(cond.laCumple(Sony));
	}
	
	@Test
	public void SonyNOCumpleCondTaxIndicadorINGRESONETOConSumatoriaMayorA30000EnUltimosDosAnios() {
		CondicionTaxativa cond = new CondicionTaxativa(new OperandoCondicion(OperacionAgregacion.Sumatoria,ingresoNeto,2), OperacionRelacional.Mayor, 30000);
		assertFalse(cond.laCumple(Sony));
	}
	
	@Test
	public void SonyNOCumpleCondTaxIndicadorINGRESONETOConSumatoriaMenorA15000EnUltimosDosAnios(){
		CondicionTaxativa cond = new CondicionTaxativa(new OperandoCondicion(OperacionAgregacion.Sumatoria,ingresoNeto,2), OperacionRelacional.Menor, 15000);
		assertFalse(cond.laCumple(Sony));
	}
	
	@Test
	public void SonyCumpleCondTaxIndicadorINGRESONETOConSumatoriaIgualA28000EnUltimosDosAnios(){
		CondicionTaxativa cond = new CondicionTaxativa(new OperandoCondicion(OperacionAgregacion.Sumatoria,ingresoNeto,2), OperacionRelacional.Igual, 28000);
		assertTrue(cond.laCumple(Sony));
	}
	
	@Test
	public void SonyNOCumpleCondTaxIndicadorINGRESONETOConPromedioMayorA15000EnUltimosDosAnios() {
		CondicionTaxativa cond = new CondicionTaxativa(new OperandoCondicion(OperacionAgregacion.Promedio,ingresoNeto,2), OperacionRelacional.Mayor, 15000);
		assertFalse(cond.laCumple(Sony));
	}
	
	@Test
	public void SonyNOCumpleCondTaxIndicadorINGRESONETOConPromedioMenorA10000EnUltimosDosAnios(){
		CondicionTaxativa cond = new CondicionTaxativa(new OperandoCondicion(OperacionAgregacion.Promedio,ingresoNeto,2), OperacionRelacional.Menor, 10000);
		assertFalse(cond.laCumple(Sony));
	}
	
	@Test
	public void SonyCumpleCondTaxIndicadorINGRESONETOConPromedioIgualA14000EnUltimosDosAnios(){
		CondicionTaxativa cond = new CondicionTaxativa(new OperandoCondicion(OperacionAgregacion.Promedio,ingresoNeto,2), OperacionRelacional.Igual, 14000);
		assertTrue(cond.laCumple(Sony));
	}
	
	@Test
	public void SonyNOCumpleCondTaxIndicadorINGRESONETOConPromedioMayorA10000EnUltimosCuatroAnios() {
		CondicionTaxativa cond = new CondicionTaxativa(new OperandoCondicion(OperacionAgregacion.Promedio,ingresoNeto,4), OperacionRelacional.Mayor, 10000);
		assertFalse(cond.laCumple(Sony));
	}
	
	@Test
	public void SonyNOCumpleCondTaxIndicadorINGRESONETOConPromedioMenorA8000EnUltimosCuatroAnios(){
		CondicionTaxativa cond = new CondicionTaxativa(new OperandoCondicion(OperacionAgregacion.Promedio,ingresoNeto,4), OperacionRelacional.Menor, 8000);
		assertFalse(cond.laCumple(Sony));
	}
	
	@Test
	public void SonyCumpleCondTaxIndicadorINGRESONETOConPromedioIgualA9500EnUltimosCuatroAnios(){
		CondicionTaxativa cond = new CondicionTaxativa(new OperandoCondicion(OperacionAgregacion.Promedio,ingresoNeto,4), OperacionRelacional.Igual, 9500);
		assertTrue(cond.laCumple(Sony));
	}
	
	@Test
	public void SonyNOCumpleCondTaxIndicadorINGRESONETOConMedianaMayorA15000EnUltimosTresAnios() { // Mediana con n impar
		CondicionTaxativa cond = new CondicionTaxativa(new OperandoCondicion(OperacionAgregacion.Mediana,ingresoNeto,3), OperacionRelacional.Mayor, 15000);
		assertFalse(cond.laCumple(Sony));
	}
	
	@Test
	public void SonyNOCumpleCondTaxIndicadorINGRESONETOConMedianaMenorA10000EnUltimosTresAnios(){
		CondicionTaxativa cond = new CondicionTaxativa(new OperandoCondicion(OperacionAgregacion.Mediana,ingresoNeto,3), OperacionRelacional.Menor, 10000);
		assertFalse(cond.laCumple(Sony));
	}
	
	@Test
	public void SonyCumpleCondTaxIndicadorINGRESONETOConMedianaIgualA11000EnUltimosTresAnios(){
		CondicionTaxativa cond = new CondicionTaxativa(new OperandoCondicion(OperacionAgregacion.Mediana,ingresoNeto,3), OperacionRelacional.Igual, 11000);
		assertTrue(cond.laCumple(Sony));
	}
	
	@Test
	public void SonyNOCumpleCondTaxIndicadorINGRESONETOConMedianaMayorA10000EnUltimosCuatroAnios() { // Mediana con n impar
		CondicionTaxativa cond = new CondicionTaxativa(new OperandoCondicion(OperacionAgregacion.Mediana,ingresoNeto,4), OperacionRelacional.Mayor, 10000);
		assertFalse(cond.laCumple(Sony));
	}
	
	@Test
	public void SonyNOCumpleCondTaxIndicadorINGRESONETOConMedianaMenorA7000EnUltimosCuatroAnios(){
		CondicionTaxativa cond = new CondicionTaxativa(new OperandoCondicion(OperacionAgregacion.Mediana,ingresoNeto,4), OperacionRelacional.Menor, 7000);
		assertFalse(cond.laCumple(Sony));
	}
	
	@Test
	public void SonyCumpleCondTaxIndicadorINGRESONETOConMedianaIgualA9000EnUltimosCuatroAnios(){
		CondicionTaxativa cond = new CondicionTaxativa(new OperandoCondicion(OperacionAgregacion.Mediana,ingresoNeto,4), OperacionRelacional.Igual, 9000);
		assertTrue(cond.laCumple(Sony));
	}
	
	@Test
	public void SonyNOCumpleCondTaxIndicadorINGRESONETOConVariacionMayorA15000EnUltimosCuatroAnios() { // Mediana con n impar
		CondicionTaxativa cond = new CondicionTaxativa(new OperandoCondicion(OperacionAgregacion.Variacion,ingresoNeto,4), OperacionRelacional.Mayor, 15000);
		assertFalse(cond.laCumple(Sony));
	}
	
	@Test
	public void SonyNOCumpleCondTaxIndicadorINGRESONETOConVariacionMenorA13000EnUltimosCuatroAnios(){
		CondicionTaxativa cond = new CondicionTaxativa(new OperandoCondicion(OperacionAgregacion.Variacion,ingresoNeto,4), OperacionRelacional.Menor, 13000);
		assertFalse(cond.laCumple(Sony));
	}
	
	@Test
	public void SonyCumpleCondTaxIndicadorINGRESONETOConVariacionIgualA14000EnUltimosCuatroAnios(){
		CondicionTaxativa cond = new CondicionTaxativa(new OperandoCondicion(OperacionAgregacion.Variacion,ingresoNeto,4), OperacionRelacional.Igual, 14000);
		assertTrue(cond.laCumple(Sony));
	}
	
	@Test(expected = NoExisteCuentaError.class)
	public void AppleLanzaErrorAlQuererCumplirCondTaxIndicadorINGRESONETOConVariacionIgualA14000EnUltimosTresAniosPorFaltaDeCuentas(){
		CondicionTaxativa cond = new CondicionTaxativa(new OperandoCondicion(OperacionAgregacion.Variacion,ingresoNeto,3), OperacionRelacional.Igual, 14000);
		cond.laCumple(Apple);
	}
	
	@Test(expected = NoExisteCuentaError.class)
	public void SonyLanzaErrorAlQuererCumplirCondTaxIndicadorINGRESONETOConSumatoriaIgualA14000EnUltimosCincoAniosPorFaltaDeCuentas(){
		CondicionTaxativa cond = new CondicionTaxativa(new OperandoCondicion(OperacionAgregacion.Variacion,ingresoNeto,5), OperacionRelacional.Igual, 50000);
		cond.laCumple(Sony);
	}
	
	@Test
	public void SonyEsMejorQueAppleConINGRESONETOEnUltimoAnio(){
		CondicionPrioritaria cond = new CondicionPrioritaria(new OperandoCondicion(OperacionAgregacion.Ultimo,ingresoNeto,1), OperacionRelacional.Mayor);
		assertTrue(cond.esMejorQue(Sony, Apple));
	}
	
	@Test
	public void FalabellaEsMejorQueDeloitteConSaldoCrudoConSumatoriaEnUltimosDosAnios(){
		CondicionPrioritaria cond = new CondicionPrioritaria(new OperandoCondicion(OperacionAgregacion.Sumatoria,saldoCrudo,1), OperacionRelacional.Mayor);
		assertTrue(cond.esMejorQue(Falabella, Deloitte));
	}
	
	@Test
	public void FalabellaEsMejorQueDeloitteConSaldoCrudoConPromedioEnUltimosDosAnios(){
		CondicionPrioritaria cond = new CondicionPrioritaria(new OperandoCondicion(OperacionAgregacion.Promedio,saldoCrudo,1), OperacionRelacional.Mayor);
		assertTrue(cond.esMejorQue(Falabella, Deloitte));
	}
	
	@Test
	public void FalabellaEsMejorQueDelloiteYDelloiteEsMejorQueIBMConSaldoCrudoConPromedioEnUltimosDosAnios(){
		CondicionPrioritaria cond = new CondicionPrioritaria(new OperandoCondicion(OperacionAgregacion.Promedio,saldoCrudo,1), OperacionRelacional.Mayor);
		assertTrue(cond.esMejorQue(Falabella, Deloitte) && cond.esMejorQue(Deloitte, IBM));
	}
	
	@Test
	public void DeloitteEsMejorQueFalabellaYFalabellaEsMejorQueIBMConINGRESONETOConSumatoriaEnUltimosDosAnios(){
		CondicionPrioritaria cond = new CondicionPrioritaria(new OperandoCondicion(OperacionAgregacion.Sumatoria,ingresoNeto,1), OperacionRelacional.Mayor);
		assertTrue(cond.esMejorQue(Deloitte, Falabella) && cond.esMejorQue(Falabella, IBM));
	}
	
	@Test
	public void DeloitteEsMejorQueFalabellaYFalabellaEsMejorQueIBMConINGRESONETOConVariacionEnUltimosDosAnios(){
		CondicionPrioritaria cond = new CondicionPrioritaria(new OperandoCondicion(OperacionAgregacion.Variacion,ingresoNeto,2), OperacionRelacional.Mayor);
		assertTrue(cond.esMejorQue(Deloitte, Falabella) && cond.esMejorQue(Falabella, IBM));
	}
	
	@Test
	public void DeloitteEsMejorQueFalabellaYFalabellaEsMejorQueIBMConINGRESONETOConVariacionConsiderandoLaMenorEnUltimosDosAnios(){
		CondicionPrioritaria cond = new CondicionPrioritaria(new OperandoCondicion(OperacionAgregacion.Variacion,ingresoNeto,2), OperacionRelacional.Menor);
		assertTrue(cond.esMejorQue(IBM, Falabella) && cond.esMejorQue(Falabella, Deloitte));
	}
	
	@Test(expected = NoExisteCuentaError.class)
	public void hayErrorAlQuererCompararSonyConGooglePorINGRESONETOConSumatoriaEnUltimosTresAniosPorFaltaDeCuentas(){
		CondicionPrioritaria cond = new CondicionPrioritaria(new OperandoCondicion(OperacionAgregacion.Sumatoria,ingresoNeto,3), OperacionRelacional.Mayor);
		cond.esMejorQue(Sony, Google);
	}
	

	@Test
	public void soloDosEmpresasCumplenMetodologiaDeWarren(){
		assertEquals(2,metodologiaDeWarren.evaluarPara(empresasParaComparacionConMetodologias).size());
	}
	
	@Test
	public void soloDeloitteYFalabellaCumplenMetodologiaDeWarren(){
		List<Empresa> empresas = new ArrayList<Empresa>();
		empresas.add(Deloitte);
		empresas.add(Falabella);
		assertTrue(metodologiaDeWarren.evaluarPara(empresasParaComparacionConMetodologias).containsAll(empresas));
	}
	
	@Test
	public void lasEmpresasQueCumplenMetodologiaDeWarrenSonOrdenadasCorrectamente(){
		assertTrue(metodologiaDeWarren.evaluarPara(empresasParaComparacionConMetodologias).get(0)==Deloitte && metodologiaDeWarren.evaluarPara(empresasParaComparacionConMetodologias).get(1)==Falabella);
	}
	
	@Test
	public void soloUnaEmpresaNoCumpleMetodologiaDeWarren(){
		assertEquals(1,metodologiaDeWarren.empresasQueNoCumplenTaxativas(empresasParaComparacionConMetodologias).size());
	}
	
	@Test
	public void soloIBMNOCumpleMetodologiaDeWarren(){
		assertTrue(metodologiaDeWarren.empresasQueNoCumplenTaxativas(empresasParaComparacionConMetodologias).get(0)==IBM);
	}
	
	@Test
	public void soloUnaEmpresaCumpleMetodologiaDeMike(){
		assertEquals(1,metodologiaDeMike.evaluarPara(empresasParaComparacionConMetodologias).size());
	}
	
	@Test
	public void soloDeloitteCumpleMetodologiaDeMike(){
		assertTrue(metodologiaDeMike.evaluarPara(empresasParaComparacionConMetodologias).stream().anyMatch(emp -> emp == Deloitte));
	}
	
	@Test
	public void ningunaEmpresaNOCumpleMetodologiaDeMike(){
		assertEquals(0,metodologiaDeMike.empresasQueNoCumplenTaxativas(empresasParaComparacionConMetodologias).size());
	}
	
	@Test
	public void soloDosEmpresasNOtienenDatosSuficientesParaMetodologiaDeMike(){
		assertEquals(2,metodologiaDeMike.empresasConDatosFaltantes(empresasParaComparacionConMetodologias).size());
	}
	
	@Test
	public void soloIBMYFalabellaNOtienenDatosSuficientesParaMetodologiaDeMike(){
		List<Empresa> empresas = new ArrayList<Empresa>();
		empresas.add(IBM);
		empresas.add(Falabella);
		assertTrue(metodologiaDeMike.empresasConDatosFaltantes(empresasParaComparacionConMetodologias).containsAll(empresas));
	}
	
	@Test
	public void soloDosEmpresasSeAplicaCorrectamenteMetodologiaDeSteve(){
		assertEquals(2,metodologiaDeSteve.evaluarPara(empresasParaComparacionConMetodologias).size());
	}
	
	@Test
	public void seAplicaCorrectamenteMetodologiaDeSteveDevolviendoEnCorrectoOrdenADeloitteYFalabella(){
		assertTrue(metodologiaDeSteve.evaluarPara(empresasParaComparacionConMetodologias).get(0)==Falabella && metodologiaDeSteve.evaluarPara(empresasParaComparacionConMetodologias).get(1)==Deloitte);
	}
	
	@Test
	public void seAplicaCorrectamenteUnaMetodologiaConCondTaxPRUEBAConUltimoMayorA0YConCondPriorPRUEBAConUltimoAmbosEnUltimoAnioDevolviendoEnCorrectoOrdenAFalabellaYDeloitte(){
		Indicador prueba = parsear("PRUEBA = ebitda + 5");
		Metodologia metodologia = new Metodologia("Una Metodologia");
		CondicionTaxativa condTax = new CondicionTaxativa(new OperandoCondicion(OperacionAgregacion.Ultimo,prueba,1), OperacionRelacional.Mayor, 0);
		CondicionPrioritaria condPrior = new CondicionPrioritaria(new OperandoCondicion(OperacionAgregacion.Ultimo,prueba,1), OperacionRelacional.Mayor);
		metodologia.agregarCondicionTaxativa(condTax);
		metodologia.agregarCondicionPrioritaria(condPrior);
		assertTrue(metodologia.evaluarPara(empresasParaComparacionConMetodologias).get(0)==Falabella && metodologia.evaluarPara(empresasParaComparacionConMetodologias).get(1)==Deloitte);
	}

	
}
