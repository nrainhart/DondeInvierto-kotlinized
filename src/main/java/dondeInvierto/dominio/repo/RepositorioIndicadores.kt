package dondeInvierto.dominio.repo

import dondeInvierto.dominio.empresas.Empresa
import dondeInvierto.dominio.indicadores.Indicador
import dondeInvierto.dominio.parser.ParserIndicadores
import java.time.Year
import javax.persistence.Entity

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
        return empresa.aniosDeLosQueTieneCuentas()
                .flatMap { anio -> indicadoresAplicablesA(empresa, anio) }
    }

    fun indicadoresAplicablesA(empresa: Empresa, anio: Year): Set<Indicador> {
        return obtenerTodos().filter { it.esAplicableA(empresa, anio) }
                .toSet()
    }

    fun buscarIndicador(nombreIndicador: String): Indicador {
        return obtenerTodos().firstOrNull { it.seLlama(nombreIndicador) }
                ?: throw NoExisteIndicadorError("No se pudo encontrar un indicador con ese nombre.")
    }

    private fun obtenerIndicadoresParseados(strIndicadores: List<String>): List<Indicador> {
        return strIndicadores.map { ParserIndicadores.parse(it) }
                .filter { ind -> !existe(ind) }
    }

}

internal class NoExisteIndicadorError(e: String) : RuntimeException(e)