package defaultPackage

import dondeInvierto.dominio.empresas.Cuenta
import dondeInvierto.dominio.empresas.Empresa
import repo.RepositorioEmpresas
import dondeInvierto.dominio.indicadores.Indicador
import repo.RepositorioIndicadores
import dondeInvierto.dominio.metodologias.*
import dondeInvierto.dominio.parser.ParserIndicadores
import excepciones.EntidadExistenteError
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.uqbarproject.jpa.java8.extras.WithGlobalEntityManager
import org.uqbarproject.jpa.java8.extras.test.AbstractPersistenceTest
import org.uqbarproject.jpa.java8.extras.transaction.TransactionalOps
import repo.RepositorioMetodologias
import java.time.Year
import kotlin.test.assertFailsWith

class PersistenciaTest : AbstractPersistenceTest(), WithGlobalEntityManager, TransactionalOps {

    private val repoEmpresas = RepositorioEmpresas()
    private val repoIndicadores = RepositorioIndicadores()
    private val repoMetodologias = RepositorioMetodologias()
    private val listaCuentas1: MutableList<Cuenta>
    private val listaCuentas2: MutableList<Cuenta>
    private val listaEmpresas: MutableList<Empresa>
    private val listaIndicadores: MutableList<String>
    private val listaMetodologias: MutableList<Metodologia>

    init {
        withTransaction {
            repoIndicadores.agregarMultiplesIndicadores(listOf(
                    "INGRESONETO = netooperacionescontinuas + netooperacionesdiscontinuas",
                    "INDICADORDOS = cuentarara + fds",
                    "INDICADORTRES = INGRESONETO * 10 + ebitda",
                    "A = 5 / 3",
                    "PRUEBA = ebitda + 5"))
        }

        listaCuentas1 = mutableListOf(
                Cuenta(Year.of(2015), "EBITDA", 2000),
                Cuenta(Year.of(2014), "FDS", 3000)
        )
        listaCuentas2 = mutableListOf(
                Cuenta(Year.of(2013), "EBITDA", 6000),
                Cuenta(Year.of(2010), "FDS", 8000),
                Cuenta(Year.of(2014), "EBITDA", 8000)
        )
        listaEmpresas = mutableListOf(
                Empresa("empresa1", listaCuentas1),
                Empresa("empresa2", listaCuentas2)
        )
        listaIndicadores = mutableListOf(
                "UNINDICADOR = ebitda + fds - 2",
                "OTROINDICADOR = ebitda * 2 + fds - 2500"
        )
        listaMetodologias = mutableListOf(
                obtenerMetodologia1("Metodologia1"),
                obtenerMetodologia2("Metodologia2")
        )
    }

    @Test
    fun alAgregarDosEmpresasAlRepositorioEmpresasEstasSePersistenCorrectamente() {
        withTransaction { repoEmpresas.agregarMultiplesEmpresas(listaEmpresas) }
        assertTrue(repoEmpresas.obtenerTodos().containsAll(listaEmpresas))
    }

    @Test
    fun alPersistirDosEmpresasConElMismoNombreSeActualizaLaMisma() {
        val empresaCopia = Empresa("empresa1", listaCuentas2)
        withTransaction {
            listaEmpresas.add(empresaCopia)
            repoEmpresas.agregarMultiplesEmpresas(listaEmpresas)
        }
        assertEquals(listaEmpresas.size - 1, repoEmpresas.obtenerTodos().size)
    }

    @Test
    fun alAgregarDosIndicadoresAlRepositorioIndicadoresEstosSePersistenEsaCantidad() {
        val cantidadAntesDeAgregar = repoIndicadores.obtenerTodos().size
        withTransaction { repoIndicadores.agregarMultiplesIndicadores(listaIndicadores) }
        assertEquals(cantidadAntesDeAgregar + 2, repoIndicadores.obtenerTodos().size)
    }

    @Test
    fun alAgregarDosIndicadoresAlRepositorioIndicadoresEstosSePersistenCorrectamente() {
        val indicadores = crearIndicadoresAPartirDeSusExpresiones(listaIndicadores)
        repoIndicadores.agregarMultiplesIndicadores(listaIndicadores)
        assertTrue(repoIndicadores.obtenerTodos().containsAll(indicadores))
    }

    @Test
    fun noSePersistenDosIndicadoresConElMismoNombre() {
        val indicadorRepetido = "UNINDICADOR = 2 * 3 * 6 * ebitda + fds"
        listaIndicadores.add(indicadorRepetido)
        assertFailsWith(EntidadExistenteError::class) { repoIndicadores.agregarMultiplesIndicadores(listaIndicadores) }
    }

    @Test
    fun alAgregarDosMetodologiasAlRepositorioMetodologiasSePersisteEsaCantidad() {
        val cantidadAntesDeAgregar = repoMetodologias.obtenerTodos().size
        withTransaction { listaMetodologias.forEach { repoMetodologias.agregar(it) } }
        assertEquals(cantidadAntesDeAgregar + 2, repoMetodologias.obtenerTodos().size)
    }

    @Test
    fun alAgregarDosMetodologiasAlRepositorioMetodologiasEstosSePersistenCorrectamente() {
        withTransaction { listaMetodologias.forEach { repoMetodologias.agregar(it) } }
        assertTrue(repoMetodologias.obtenerTodos().containsAll(listaMetodologias))
    }

    @Test
    fun siPersistoDosVecesLaMismaMetodologiaFalla() {
        repoMetodologias.agregar(obtenerMetodologia1("Metodologia1"))
        assertFailsWith(EntidadExistenteError::class) { repoMetodologias.agregar(obtenerMetodologia1("Metodologia1")) }
    }


    // ************ METODOS AUXILIARES************//
    private fun crearIndicadoresAPartirDeSusExpresiones(expresiones: List<String>): List<Indicador> {
        return expresiones.map { exp -> ParserIndicadores.parse(exp) }
    }

    private fun obtenerMetodologia1(nombreMetodologia: String): Metodologia {
        val ingresoNeto = repoIndicadores.buscarIndicador("ingresoNeto")
        val indicadorDos = repoIndicadores.buscarIndicador("indicadorDos")
        val metodologia = Metodologia(nombreMetodologia)
        val condTax = CondicionTaxativa(
                OperandoCondicion(OperacionAgregacion.Promedio, ingresoNeto, 2), OperacionRelacional.Mayor, 10000)
        val condPrior = CondicionPrioritaria(
                OperandoCondicion(OperacionAgregacion.Sumatoria, indicadorDos, 2), OperacionRelacional.Mayor)
        agregarCondiciones(metodologia, condTax, condPrior)
        return metodologia
    }

    private fun obtenerMetodologia2(nombreMetodologia: String): Metodologia {
        val prueba = repoIndicadores.buscarIndicador("prueba")
        val metodologia = Metodologia(nombreMetodologia)
        val condTax = CondicionTaxativa(OperandoCondicion(OperacionAgregacion.Ultimo, prueba, 1),
                OperacionRelacional.Mayor, 0)
        val condPrior = CondicionPrioritaria(
                OperandoCondicion(OperacionAgregacion.Ultimo, prueba, 1), OperacionRelacional.Mayor)
        agregarCondiciones(metodologia, condTax, condPrior)
        return metodologia
    }

    private fun agregarCondiciones(metodologia: Metodologia, condTax: CondicionTaxativa, condPrior: CondicionPrioritaria) {
        metodologia.agregarCondicionTaxativa(condTax)
        metodologia.agregarCondicionPrioritaria(condPrior)
    }
}
