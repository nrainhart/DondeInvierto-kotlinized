package dondeInvierto.dominio.repo

import dondeInvierto.dominio.empresas.Empresa
import dondeInvierto.dominio.indicadores.Indicador
import dondeInvierto.dominio.parser.ParserIndicadores
import java.time.Year
import java.util.*
import javax.persistence.Entity
import kotlin.streams.toList

@Entity
class RepositorioIndicadores : AbstractLocalRepository<Indicador>() {

    fun agregarMultiplesIndicadores(strIndicadores: List<String>) {
        val indicadoresNuevos = obtenerIndicadoresParseados(strIndicadores)//NO ESTOY CATCHEANDO ParserError (!!!)
        indicadoresNuevos.forEach { ind -> agregar(ind) }
    }

    fun eliminarIndicadores() {
        elementos.clear()
    }

    fun eliminarResultadosPrecalculados() {
        obtenerTodos().forEach { ind -> ind.eliminarResultadosPrecalculados() }
    }

    override fun mensajeEntidadExistenteError(elemento: Indicador) = "Ya existe un indicador con el nombre " + elemento.nombre

    fun todosLosIndicadoresAplicablesA(empresa: Empresa): List<Indicador> {
        val indicadoresAplicables = ArrayList<Indicador>()
        val aniosDeCuentas = empresa.aniosDeLosQueTieneCuentas()
        aniosDeCuentas.forEach { anio -> indicadoresAplicables.addAll(this.indicadoresAplicablesA(empresa, anio)) }
        return indicadoresAplicables
    }

    fun indicadoresAplicablesA(empresa: Empresa, anio: Year): Set<Indicador> {
        val indicadoresAplicables = HashSet<Indicador>()
        obtenerTodos().stream().filter { ind -> ind.esAplicableA(empresa, anio) }
                .forEach { ind -> indicadoresAplicables.add(ind) }
        return indicadoresAplicables
    }

    fun buscarIndicador(nombreIndicador: String): Indicador {
        return obtenerTodos().stream().filter { ind -> ind.seLlama(nombreIndicador) }.findFirst()
                .orElseThrow { NoExisteIndicadorError("No se pudo encontrar un indicador con ese nombre.") }
    }

    private fun obtenerIndicadoresParseados(strIndicadores: List<String>): List<Indicador> {
        val indicadores = strIndicadores.stream().map { strInd -> ParserIndicadores.parse(strInd) }.filter { ind -> !existe(ind) }
        return indicadores.toList()
    }

}

internal class NoExisteIndicadorError(e: String) : RuntimeException(e)