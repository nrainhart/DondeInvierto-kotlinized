package defaultPackage

import dominio.empresas.ArchivoXLS
import dominio.empresas.Empresa
import dominio.indicadores.Indicador
import dominio.metodologias.*
import dominio.parser.ParserIndicadores
import excepciones.NoExisteCuentaError
import org.junit.Before
import org.junit.Test

import java.util.ArrayList

import org.junit.Assert.*
import kotlin.test.assertFailsWith

class MetodologiaTest {

    private val empresasParaComparacionConMetodologias: List<Empresa>
    private val Sony: Empresa
    private val Google: Empresa
    private val Apple: Empresa
    private val Deloitte: Empresa
    private val IBM: Empresa
    private val Falabella: Empresa
    private val ingresoNeto: Indicador
    private val saldoCrudo: Indicador
    private val metodologiaDeWarren: Metodologia
    private val metodologiaDeMike: Metodologia
    private val metodologiaDeSteve: Metodologia
    private val anioActual = 2017

    init {
        val archivoEjemploIndicadores = ArchivoXLS("src/test/resources/EjemploIndicadores.xls")
        val archivoEjemploMetodologias = ArchivoXLS("src/test/resources/EjemploMetodologias.xls")
        archivoEjemploIndicadores.leerEmpresas()
        archivoEjemploMetodologias.leerEmpresas()
        val empresasParaIndicadores = archivoEjemploIndicadores.empresas
        empresasParaComparacionConMetodologias = archivoEjemploMetodologias.empresas
        Sony = empresasParaIndicadores[0]
        Google = empresasParaIndicadores[1]
        Apple = empresasParaIndicadores[2]
        Deloitte = empresasParaComparacionConMetodologias[0]
        IBM = empresasParaComparacionConMetodologias[1]
        Falabella = empresasParaComparacionConMetodologias[2]
        ingresoNeto = parsear("INGRESONETO = netooperacionescontinuas + netooperacionesdiscontinuas")
        saldoCrudo = parsear("SALDOCRUDO = cuentarara + fds")
        metodologiaDeWarren = Metodologia("Warren")
        val condWarren = CondicionTaxativa(OperandoCondicion(OperacionAgregacion.Promedio, ingresoNeto, 2), OperacionRelacional.Mayor, 10000)
        metodologiaDeWarren.agregarCondicionTaxativa(condWarren)
        metodologiaDeMike = Metodologia("Mike") // MetodologiaDeMike
        val condMike = CondicionTaxativa(OperandoCondicion(OperacionAgregacion.Sumatoria, ingresoNeto, 3), OperacionRelacional.Mayor, 20000)
        metodologiaDeMike.agregarCondicionTaxativa(condMike)
        metodologiaDeSteve = Metodologia("Steve") // UnaMetodologiaConCondTaxINGRESONETOConPromedioMayorA10000YConCondPriorSaldoCrudoConSumatoriaAmbosEnUltimosDosAnios
        val condTaxSteve = CondicionTaxativa(OperandoCondicion(OperacionAgregacion.Promedio, ingresoNeto, 2), OperacionRelacional.Mayor, 10000)
        val condPriorSteve = CondicionPrioritaria(OperandoCondicion(OperacionAgregacion.Variacion, saldoCrudo, 2), OperacionRelacional.Mayor)
        metodologiaDeSteve.agregarCondicionTaxativa(condTaxSteve)
        metodologiaDeSteve.agregarCondicionPrioritaria(condPriorSteve)
    }

    private fun parsear(expresion: String): Indicador {
        return ParserIndicadores.parse(expresion)
    }

    @Test
    fun SonyEsMasAntiguaQueApple() {
        assertTrue(CondicionPrioritaria(OperandoCondicion(OperacionAgregacion.Ultimo, Antiguedad(), 1), OperacionRelacional.Mayor).esMejorQue(Sony, Apple, anioActual))
    }

    @Test
    fun SonyCumpleCondTaxAntiguedadMenorA10() {
        assertTrue(CondicionTaxativa(OperandoCondicion(OperacionAgregacion.Ultimo, Antiguedad(), 1), OperacionRelacional.Menor, 10).laCumple(Sony, anioActual))
    }

    @Test
    fun AppleNoCumpleCondTaxAntiguedadMayorA3() {
        assertFalse(CondicionTaxativa(OperandoCondicion(OperacionAgregacion.Ultimo, Antiguedad(), 1), OperacionRelacional.Mayor, 3).laCumple(Apple, anioActual))
    }

    @Test
    fun AppleCumpleCondTaxAntiguedadIgualA1() {
        assertTrue(CondicionTaxativa(OperandoCondicion(OperacionAgregacion.Ultimo, Antiguedad(), 1), OperacionRelacional.Igual, 1).laCumple(Apple, anioActual))
    }

    @Test
    fun AppleNOCumpleCondTaxIndicadorSaldoCrudoMayorA360000() {
        val operando = OperandoCondicion(OperacionAgregacion.Ultimo, saldoCrudo, 1)
        val cond = CondicionTaxativa(operando, OperacionRelacional.Mayor, 360000)
        assertFalse(cond.laCumple(Apple, anioActual))
    }

    @Test
    fun AppleNOCumpleCondTaxIndicadorSaldoCrudoMenorA200000() {
        val operando = OperandoCondicion(OperacionAgregacion.Ultimo, saldoCrudo, 1)
        val cond = CondicionTaxativa(operando, OperacionRelacional.Menor, 200000)
        assertFalse(cond.laCumple(Apple, anioActual))
    }

    @Test
    fun AppleCumpleCondTaxIndicadorSaldoCrudoIgualA30000() {
        val operando = OperandoCondicion(OperacionAgregacion.Ultimo, saldoCrudo, 1)
        val cond = CondicionTaxativa(operando, OperacionRelacional.Igual, 300000)
        assertTrue(cond.laCumple(Apple, anioActual))
    }

    @Test
    fun SonyNOCumpleCondTaxIndicadorINGRESONETOMayorA20000EnUltimoAnio() {
        val cond = CondicionTaxativa(OperandoCondicion(OperacionAgregacion.Ultimo, ingresoNeto, 1), OperacionRelacional.Mayor, 20000)
        assertFalse(cond.laCumple(Sony, anioActual))
    }

    @Test
    fun SonyNOCumpleCondTaxIndicadorINGRESONETOMenorA8000EnUltimoAnio() {
        val cond = CondicionTaxativa(OperandoCondicion(OperacionAgregacion.Ultimo, ingresoNeto, 1), OperacionRelacional.Menor, 8000)
        assertFalse(cond.laCumple(Sony, anioActual))
    }

    @Test
    fun SonyCumpleCondTaxIndicadorINGRESONETOIgualA17000EnUltimoAnio() {
        val cond = CondicionTaxativa(OperandoCondicion(OperacionAgregacion.Ultimo, ingresoNeto, 1), OperacionRelacional.Igual, 17000)
        assertTrue(cond.laCumple(Sony, anioActual))
    }

    @Test
    fun SonyNOCumpleCondTaxIndicadorINGRESONETOConSumatoriaMayorA30000EnUltimosDosAnios() {
        val cond = CondicionTaxativa(OperandoCondicion(OperacionAgregacion.Sumatoria, ingresoNeto, 2), OperacionRelacional.Mayor, 30000)
        assertFalse(cond.laCumple(Sony, anioActual))
    }

    @Test
    fun SonyNOCumpleCondTaxIndicadorINGRESONETOConSumatoriaMenorA15000EnUltimosDosAnios() {
        val cond = CondicionTaxativa(OperandoCondicion(OperacionAgregacion.Sumatoria, ingresoNeto, 2), OperacionRelacional.Menor, 15000)
        assertFalse(cond.laCumple(Sony, anioActual))
    }

    @Test
    fun SonyCumpleCondTaxIndicadorINGRESONETOConSumatoriaIgualA28000EnUltimosDosAnios() {
        val cond = CondicionTaxativa(OperandoCondicion(OperacionAgregacion.Sumatoria, ingresoNeto, 2), OperacionRelacional.Igual, 28000)
        assertTrue(cond.laCumple(Sony, anioActual))
    }

    @Test
    fun SonyNOCumpleCondTaxIndicadorINGRESONETOConPromedioMayorA15000EnUltimosDosAnios() {
        val cond = CondicionTaxativa(OperandoCondicion(OperacionAgregacion.Promedio, ingresoNeto, 2), OperacionRelacional.Mayor, 15000)
        assertFalse(cond.laCumple(Sony, anioActual))
    }

    @Test
    fun SonyNOCumpleCondTaxIndicadorINGRESONETOConPromedioMenorA10000EnUltimosDosAnios() {
        val cond = CondicionTaxativa(OperandoCondicion(OperacionAgregacion.Promedio, ingresoNeto, 2), OperacionRelacional.Menor, 10000)
        assertFalse(cond.laCumple(Sony, anioActual))
    }

    @Test
    fun SonyCumpleCondTaxIndicadorINGRESONETOConPromedioIgualA14000EnUltimosDosAnios() {
        val cond = CondicionTaxativa(OperandoCondicion(OperacionAgregacion.Promedio, ingresoNeto, 2), OperacionRelacional.Igual, 14000)
        assertTrue(cond.laCumple(Sony, anioActual))
    }

    @Test
    fun SonyNOCumpleCondTaxIndicadorINGRESONETOConPromedioMayorA10000EnUltimosCuatroAnios() {
        val cond = CondicionTaxativa(OperandoCondicion(OperacionAgregacion.Promedio, ingresoNeto, 4), OperacionRelacional.Mayor, 10000)
        assertFalse(cond.laCumple(Sony, anioActual))
    }

    @Test
    fun SonyNOCumpleCondTaxIndicadorINGRESONETOConPromedioMenorA8000EnUltimosCuatroAnios() {
        val cond = CondicionTaxativa(OperandoCondicion(OperacionAgregacion.Promedio, ingresoNeto, 4), OperacionRelacional.Menor, 8000)
        assertFalse(cond.laCumple(Sony, anioActual))
    }

    @Test
    fun SonyCumpleCondTaxIndicadorINGRESONETOConPromedioIgualA9500EnUltimosCuatroAnios() {
        val cond = CondicionTaxativa(OperandoCondicion(OperacionAgregacion.Promedio, ingresoNeto, 4), OperacionRelacional.Igual, 9500)
        assertTrue(cond.laCumple(Sony, anioActual))
    }

    @Test
    fun SonyNOCumpleCondTaxIndicadorINGRESONETOConMedianaMayorA15000EnUltimosTresAnios() { // Mediana con n impar
        val cond = CondicionTaxativa(OperandoCondicion(OperacionAgregacion.Mediana, ingresoNeto, 3), OperacionRelacional.Mayor, 15000)
        assertFalse(cond.laCumple(Sony, anioActual))
    }

    @Test
    fun SonyNOCumpleCondTaxIndicadorINGRESONETOConMedianaMenorA10000EnUltimosTresAnios() {
        val cond = CondicionTaxativa(OperandoCondicion(OperacionAgregacion.Mediana, ingresoNeto, 3), OperacionRelacional.Menor, 10000)
        assertFalse(cond.laCumple(Sony, anioActual))
    }

    @Test
    fun SonyCumpleCondTaxIndicadorINGRESONETOConMedianaIgualA11000EnUltimosTresAnios() {
        val cond = CondicionTaxativa(OperandoCondicion(OperacionAgregacion.Mediana, ingresoNeto, 3), OperacionRelacional.Igual, 11000)
        assertTrue(cond.laCumple(Sony, anioActual))
    }

    @Test
    fun SonyNOCumpleCondTaxIndicadorINGRESONETOConMedianaMayorA10000EnUltimosCuatroAnios() { // Mediana con n impar
        val cond = CondicionTaxativa(OperandoCondicion(OperacionAgregacion.Mediana, ingresoNeto, 4), OperacionRelacional.Mayor, 10000)
        assertFalse(cond.laCumple(Sony, anioActual))
    }

    @Test
    fun SonyNOCumpleCondTaxIndicadorINGRESONETOConMedianaMenorA7000EnUltimosCuatroAnios() {
        val cond = CondicionTaxativa(OperandoCondicion(OperacionAgregacion.Mediana, ingresoNeto, 4), OperacionRelacional.Menor, 7000)
        assertFalse(cond.laCumple(Sony, anioActual))
    }

    @Test
    fun SonyCumpleCondTaxIndicadorINGRESONETOConMedianaIgualA9000EnUltimosCuatroAnios() {
        val cond = CondicionTaxativa(OperandoCondicion(OperacionAgregacion.Mediana, ingresoNeto, 4), OperacionRelacional.Igual, 9000)
        assertTrue(cond.laCumple(Sony, anioActual))
    }

    @Test
    fun SonyNOCumpleCondTaxIndicadorINGRESONETOConVariacionMayorA15000EnUltimosCuatroAnios() { // Mediana con n impar
        val cond = CondicionTaxativa(OperandoCondicion(OperacionAgregacion.Variacion, ingresoNeto, 4), OperacionRelacional.Mayor, 15000)
        assertFalse(cond.laCumple(Sony, anioActual))
    }

    @Test
    fun SonyNOCumpleCondTaxIndicadorINGRESONETOConVariacionMenorA13000EnUltimosCuatroAnios() {
        val cond = CondicionTaxativa(OperandoCondicion(OperacionAgregacion.Variacion, ingresoNeto, 4), OperacionRelacional.Menor, 13000)
        assertFalse(cond.laCumple(Sony, anioActual))
    }

    @Test
    fun SonyCumpleCondTaxIndicadorINGRESONETOConVariacionIgualA14000EnUltimosCuatroAnios() {
        val cond = CondicionTaxativa(OperandoCondicion(OperacionAgregacion.Variacion, ingresoNeto, 4), OperacionRelacional.Igual, 14000)
        assertTrue(cond.laCumple(Sony, anioActual))
    }

    @Test
    fun AppleLanzaErrorAlQuererCumplirCondTaxIndicadorINGRESONETOConVariacionIgualA14000EnUltimosTresAniosPorFaltaDeCuentas() {
        val cond = CondicionTaxativa(OperandoCondicion(OperacionAgregacion.Variacion, ingresoNeto, 3), OperacionRelacional.Igual, 14000)
        assertFailsWith(NoExisteCuentaError::class) { cond.laCumple(Apple, anioActual) }
    }

    @Test
    fun SonyLanzaErrorAlQuererCumplirCondTaxIndicadorINGRESONETOConSumatoriaIgualA14000EnUltimosCincoAniosPorFaltaDeCuentas() {
        val cond = CondicionTaxativa(OperandoCondicion(OperacionAgregacion.Variacion, ingresoNeto, 5), OperacionRelacional.Igual, 50000)
        assertFailsWith(NoExisteCuentaError::class) { cond.laCumple(Sony, anioActual) }
    }

    @Test
    fun SonyEsMejorQueAppleConINGRESONETOEnUltimoAnio() {
        val cond = CondicionPrioritaria(OperandoCondicion(OperacionAgregacion.Ultimo, ingresoNeto, 1), OperacionRelacional.Mayor)
        assertTrue(cond.esMejorQue(Sony, Apple, anioActual))
    }

    @Test
    fun FalabellaEsMejorQueDeloitteConSaldoCrudoConSumatoriaEnUltimosDosAnios() {
        val cond = CondicionPrioritaria(OperandoCondicion(OperacionAgregacion.Sumatoria, saldoCrudo, 1), OperacionRelacional.Mayor)
        assertTrue(cond.esMejorQue(Falabella, Deloitte, anioActual))
    }

    @Test
    fun FalabellaEsMejorQueDeloitteConSaldoCrudoConPromedioEnUltimosDosAnios() {
        val cond = CondicionPrioritaria(OperandoCondicion(OperacionAgregacion.Promedio, saldoCrudo, 1), OperacionRelacional.Mayor)
        assertTrue(cond.esMejorQue(Falabella, Deloitte, anioActual))
    }

    @Test
    fun FalabellaEsMejorQueDelloiteYDelloiteEsMejorQueIBMConSaldoCrudoConPromedioEnUltimosDosAnios() {
        val cond = CondicionPrioritaria(OperandoCondicion(OperacionAgregacion.Promedio, saldoCrudo, 1), OperacionRelacional.Mayor)
        assertTrue(cond.esMejorQue(Falabella, Deloitte, anioActual) && cond.esMejorQue(Deloitte, IBM, anioActual))
    }

    @Test
    fun DeloitteEsMejorQueFalabellaYFalabellaEsMejorQueIBMConINGRESONETOConSumatoriaEnUltimosDosAnios() {
        val cond = CondicionPrioritaria(OperandoCondicion(OperacionAgregacion.Sumatoria, ingresoNeto, 1), OperacionRelacional.Mayor)
        assertTrue(cond.esMejorQue(Deloitte, Falabella, anioActual) && cond.esMejorQue(Falabella, IBM, anioActual))
    }

    @Test
    fun DeloitteEsMejorQueFalabellaYFalabellaEsMejorQueIBMConINGRESONETOConVariacionEnUltimosDosAnios() {
        val cond = CondicionPrioritaria(OperandoCondicion(OperacionAgregacion.Variacion, ingresoNeto, 2), OperacionRelacional.Mayor)
        assertTrue(cond.esMejorQue(Deloitte, Falabella, anioActual) && cond.esMejorQue(Falabella, IBM, anioActual))
    }

    @Test
    fun DeloitteEsMejorQueFalabellaYFalabellaEsMejorQueIBMConINGRESONETOConVariacionConsiderandoLaMenorEnUltimosDosAnios() {
        val cond = CondicionPrioritaria(OperandoCondicion(OperacionAgregacion.Variacion, ingresoNeto, 2), OperacionRelacional.Menor)
        assertTrue(cond.esMejorQue(IBM, Falabella, anioActual) && cond.esMejorQue(Falabella, Deloitte, anioActual))
    }

    @Test
    fun hayErrorAlQuererCompararSonyConGooglePorINGRESONETOConSumatoriaEnUltimosTresAniosPorFaltaDeCuentas() {
        val cond = CondicionPrioritaria(OperandoCondicion(OperacionAgregacion.Sumatoria, ingresoNeto, 3), OperacionRelacional.Mayor)
        assertFailsWith(NoExisteCuentaError::class) { cond.esMejorQue(Sony, Google, anioActual) }
    }

    @Test
    fun soloDosEmpresasCumplenMetodologiaDeWarren() {
        assertEquals(2, metodologiaDeWarren.evaluarPara(empresasParaComparacionConMetodologias, anioActual).size)
    }

    @Test
    fun soloDeloitteYFalabellaCumplenMetodologiaDeWarren() {
        val empresas = listOf(Deloitte, Falabella)
        assertTrue(metodologiaDeWarren.evaluarPara(empresasParaComparacionConMetodologias, anioActual).containsAll(empresas))
    }

    @Test
    fun lasEmpresasQueCumplenMetodologiaDeWarrenSonOrdenadasCorrectamente() {
        val empresasOrdenadas = metodologiaDeWarren.evaluarPara(empresasParaComparacionConMetodologias, anioActual)
        val mejorEmpresa = empresasOrdenadas[0]
        val peorEmpresa = empresasOrdenadas[1]
        assertTrue(mejorEmpresa === Deloitte && peorEmpresa === Falabella)
    }

    @Test
    fun soloUnaEmpresaNoCumpleMetodologiaDeWarren() {
        assertEquals(1, metodologiaDeWarren.empresasQueNoCumplenTaxativas(empresasParaComparacionConMetodologias, anioActual).size)
    }

    @Test
    fun soloIBMNOCumpleMetodologiaDeWarren() {
        val empresaQueNoCumpleTaxativas = metodologiaDeWarren.empresasQueNoCumplenTaxativas(empresasParaComparacionConMetodologias, anioActual)[0]
        assertSame(empresaQueNoCumpleTaxativas, IBM)
    }

    @Test
    fun soloUnaEmpresaCumpleMetodologiaDeMike() {
        assertEquals(1, metodologiaDeMike.evaluarPara(empresasParaComparacionConMetodologias, anioActual).size)
    }

    @Test
    fun soloDeloitteCumpleMetodologiaDeMike() {
        assertSame(metodologiaDeMike.evaluarPara(empresasParaComparacionConMetodologias, anioActual)[0], Deloitte )
    }

    @Test
    fun ningunaEmpresaNOCumpleMetodologiaDeMike() {
        assertEquals(0, metodologiaDeMike.empresasQueNoCumplenTaxativas(empresasParaComparacionConMetodologias, anioActual).size)
    }

    @Test
    fun soloDosEmpresasNOtienenDatosSuficientesParaMetodologiaDeMike() {
        assertEquals(2, metodologiaDeMike.empresasConDatosFaltantes(empresasParaComparacionConMetodologias, anioActual).size)
    }

    @Test
    fun soloIBMYFalabellaNOtienenDatosSuficientesParaMetodologiaDeMike() {
        val empresas = listOf(IBM, Falabella)
        assertTrue(metodologiaDeMike.empresasConDatosFaltantes(empresasParaComparacionConMetodologias, anioActual).containsAll(empresas))
    }

    @Test
    fun soloDosEmpresasSeAplicaCorrectamenteMetodologiaDeSteve() {
        assertEquals(2, metodologiaDeSteve.evaluarPara(empresasParaComparacionConMetodologias, anioActual).size)
    }

    @Test
    fun seAplicaCorrectamenteMetodologiaDeSteveDevolviendoEnCorrectoOrdenADeloitteYFalabella() {
        val empresasOrdenadas = metodologiaDeSteve.evaluarPara(empresasParaComparacionConMetodologias, anioActual)
        val mejorEmpresa = empresasOrdenadas[0]
        val peorEmpresa = empresasOrdenadas[1]
        assertTrue(mejorEmpresa === Falabella && peorEmpresa === Deloitte)
    }

    @Test
    fun seAplicaCorrectamenteUnaMetodologiaConCondTaxPRUEBAConUltimoMayorA0YConCondPriorPRUEBAConUltimoAmbosEnUltimoAnioDevolviendoEnCorrectoOrdenAFalabellaYDeloitte() {
        val prueba = parsear("PRUEBA = ebitda + 5")
        val metodologia = Metodologia("Una Metodologia")
        val condTax = CondicionTaxativa(OperandoCondicion(OperacionAgregacion.Ultimo, prueba, 1), OperacionRelacional.Mayor, 0)
        val condPrior = CondicionPrioritaria(OperandoCondicion(OperacionAgregacion.Ultimo, prueba, 1), OperacionRelacional.Mayor)
        metodologia.agregarCondicionTaxativa(condTax)
        metodologia.agregarCondicionPrioritaria(condPrior)
        val empresasOrdenadas = metodologia.evaluarPara(empresasParaComparacionConMetodologias, anioActual)
        val mejorEmpresa = empresasOrdenadas[0]
        val peorEmpresa = empresasOrdenadas[1]
        assertTrue(mejorEmpresa === Falabella && peorEmpresa === Deloitte)
    }

}