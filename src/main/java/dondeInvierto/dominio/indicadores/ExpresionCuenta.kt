package dondeInvierto.dominio.indicadores

import dondeInvierto.dominio.empresas.Empresa

import java.time.Year

class ExpresionCuenta(private val cuenta: String) : Expresion {

    override fun evaluarEn(empresa: Empresa, anio: Year): Int = empresa.getValorCuenta(cuenta, anio)

}
