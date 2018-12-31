package defaultPackage

import dondeInvierto.dominio.empresas.ArchivoXLS
import dondeInvierto.dominio.empresas.Empresa
import dondeInvierto.dominio.indicadores.Indicador
import dondeInvierto.dominio.indicadores.IndicadorPrecalculado
import dominio.usuarios.Usuario
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.uqbarproject.jpa.java8.extras.WithGlobalEntityManager
import org.uqbarproject.jpa.java8.extras.test.AbstractPersistenceTest
import org.uqbarproject.jpa.java8.extras.transaction.TransactionalOps
import java.time.Year
import java.util.*

class CacheTest : AbstractPersistenceTest(), WithGlobalEntityManager, TransactionalOps {

    private val usuario = Usuario("admin", "admin")
    private val ingresoNeto: Indicador
    private val sony: Empresa

    init {
        Usuario.activo = usuario
        withTransaction {
            usuario.agregarIndicadores(Arrays.asList(
                    "INGRESONETO = netooperacionescontinuas + netooperacionesdiscontinuas",
                    "SALDOCRUDO = cuentarara + fds"))
        }
        val indicadores = usuario.indicadores
        val archivoEjemploIndicadores = ArchivoXLS("src/test/resources/EjemploIndicadores.xls")
        archivoEjemploIndicadores.leerEmpresas()
        val empresasParaIndicadores = archivoEjemploIndicadores.empresas
        ingresoNeto = indicadores[0]
        sony = empresasParaIndicadores[0]
    }

    @Test
    fun elIndicadorCacheaElResultadoLuegoDeCalcularlo() {
        val resultado = IndicadorPrecalculado(sony, Year.of(2017), 17000)
        ingresoNeto.evaluarEn(sony, Year.of(2017))
        assertTrue(ingresoNeto.getResultados().contains(resultado))
    }

    @Test
    fun elIndicadorNoCacheaDosVecesElResultadoLuegoDeCalcularlo() {
        ingresoNeto.evaluarEn(sony, Year.of(2017))
        ingresoNeto.evaluarEn(sony, Year.of(2017))
        assertEquals(ingresoNeto.getResultados().size, 1)
    }

    @Test
    fun elIndicadorDevuelveElResultadoCorrectoSiEstaCacheado() {
        ingresoNeto.evaluarEn(sony, Year.of(2017))
        val resultadoCacheado = ingresoNeto.getResultados()[0]
        assertEquals(resultadoCacheado.valor, ingresoNeto.evaluarEn(sony, Year.of(2017)))
    }
}
