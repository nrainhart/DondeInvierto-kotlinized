package defaultPackage

import dondeInvierto.dominio.empresas.ArchivoXLS
import dondeInvierto.dominio.empresas.Empresa
import dondeInvierto.dominio.indicadores.Indicador
import dominio.usuarios.Usuario
import excepciones.EntidadExistenteError
import org.junit.Assert.*
import org.junit.Test
import org.uqbarproject.jpa.java8.extras.WithGlobalEntityManager
import org.uqbarproject.jpa.java8.extras.test.AbstractPersistenceTest
import org.uqbarproject.jpa.java8.extras.transaction.TransactionalOps
import java.time.Year
import java.util.*
import kotlin.test.assertFailsWith

class IndicadoresTest : AbstractPersistenceTest(), WithGlobalEntityManager, TransactionalOps {

    private val indicadores: List<Indicador>
    private val empresasParaIndicadores: List<Empresa>
    private val usuario = Usuario("admin", "admin")

    init {
        withTransaction {
            usuario.agregarIndicadores(listOf(
                    "INGRESONETO = netooperacionescontinuas + netooperacionesdiscontinuas",
                    "INDICADORDOS = cuentarara + fds",
                    "INDICADORTRES = INGRESONETO * 10 + ebitda",
                    "A = 5 / 3", "PRUEBA = ebitda + 5"))
        }
        Usuario.activo = usuario
        indicadores = usuario.indicadores
        val archivoEjemploIndicadores = ArchivoXLS("src/test/resources/EjemploIndicadores.xls")
        archivoEjemploIndicadores.leerEmpresas()
        empresasParaIndicadores = archivoEjemploIndicadores.empresas
    }

    @Test
    fun elArchivoIndicadoresLeeCorrectamente() {
        val indicadoresActuales = indicadores.toSet()
        val indicadoresEsperados = setOf(
                Indicador("INDICADORDOS"),
                Indicador("A"),
                Indicador("INGRESONETO"),
                Indicador("PRUEBA"),
                Indicador("INDICADORTRES")
        )
        assertEquals(indicadoresActuales, indicadoresEsperados)
    }

    @Test
    fun elArchivoIndicadoresLee5Renglones() {
        assertEquals(5, indicadores.size)
    }

    @Test
    fun elIndicadorIngresoNetoSeAplicaCorrectamenteALasEmpresas() {
        val resultadosEsperados = intArrayOf(7000, 3000, 11000)
        val ingresoNeto = usuario.buscarIndicador("ingresoNeto")
        val resultados = resultadosLuegoDeAplicarIndicadorAEmpresas(ingresoNeto)
        assertArrayEquals(resultadosEsperados, resultados)
    }

    @Test
    fun unIndicadorCompuestoPorIndicadorCuentaYNumeroSeAplicaCorrectamente() {
        val indicadorTres = usuario.buscarIndicador("indicadorTres")
        val resultadosEsperados = intArrayOf(190000, 330000, 260000)
        val resultados = resultadosLuegoDeAplicarIndicadorAEmpresas(indicadorTres)
        assertArrayEquals(resultadosEsperados, resultados)
    }

    @Test
    fun siGuardoDosVecesElMismoIndicadorFalla() {
        val indicadorExistente = "INGRESONETO = ebitda + 2"
        assertFailsWith(EntidadExistenteError::class) { usuario.crearIndicador(indicadorExistente) }
    }

    @Test
    fun elIndicadorDosEsInaplicableAEmpresaLocaEn2016PorInexistenciaDeCuenta() {
        val empresaLoca = empresasParaIndicadores[1]
        val indicadorDos = usuario.buscarIndicador("indicadorDos")
        assertFalse(indicadorDos.esAplicableA(empresaLoca, obtenerAnio(2016)))
    }

    @Test
    fun soloDosIndicadoresSonAplicablesAEmpresaLocaEn2014() {
        val empresaLoca = empresasParaIndicadores[1]
        val cantidadIndicadores = cantidadIndicadoresAplicablesSegunAnio(empresaLoca, obtenerAnio(2014))
        assertEquals(4, cantidadIndicadores)
    }

    @Test
    fun laCantidadDeIndicadoresAplicablesAEmpresaReLocaSonCinco() {
        val empresaReLoca = empresasParaIndicadores[2]
        val cantidadIndicadores = cantidadIndicadoresAplicablesA(empresaReLoca)
        assertEquals(5, cantidadIndicadores)
    }

    @Test
    fun laCantidadDeIndicadoresAplicablesAEmpresaReLocaEn2016SonCuatro() {
        val empresaReLoca = empresasParaIndicadores[2]
        empresaReLoca.resultadosParaEstosIndicadores(usuario.todosLosIndicadoresAplicablesA(empresaReLoca)).size
        val cantidadIndicadores = cantidadIndicadoresAplicablesSegunAnio(empresaReLoca, obtenerAnio(2016))
        assertEquals(4, cantidadIndicadores)
    }

    /* ------------------------------- METODOS AUXILIARES  ------------------------------- */

    private fun cantidadIndicadoresAplicablesA(empresa: Empresa): Int {
        val indicadAplicables = HashSet(usuario.todosLosIndicadoresAplicablesA(empresa))
        return indicadAplicables.size
    }

    private fun cantidadIndicadoresAplicablesSegunAnio(empresa: Empresa, anio: Year): Int {
        return usuario.indicadoresAplicablesA(empresa, anio).size
    }

    private fun resultadosLuegoDeAplicarIndicadorAEmpresas(ind: Indicador): IntArray {
        val miEmpresa = empresasParaIndicadores[0]
        val empresaLoca = empresasParaIndicadores[1]
        val empresaReLoca = empresasParaIndicadores[2]
        return intArrayOf(
                ind.evaluarEn(miEmpresa, Year.of(2015)),
                ind.evaluarEn(empresaLoca, Year.of(2014)),
                ind.evaluarEn(empresaReLoca, Year.of(2016))
        )
    }

    private fun obtenerAnio(anio: Int): Year {
        return Year.of(anio)
    }

}