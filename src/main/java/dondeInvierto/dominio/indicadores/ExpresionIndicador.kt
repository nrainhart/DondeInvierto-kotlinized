package dondeInvierto.dominio.indicadores

import dondeInvierto.dominio.empresas.Empresa
import dominio.usuarios.Usuario

import java.time.Year

class ExpresionIndicador(private val nombreIndicador: String) : Expresion {

    override fun evaluarEn(empresa: Empresa, anio: Year): Int {
        val indicador = Usuario.activo!!.buscarIndicador(nombreIndicador)
        return indicador.evaluarEn(empresa, anio)
    }

}
