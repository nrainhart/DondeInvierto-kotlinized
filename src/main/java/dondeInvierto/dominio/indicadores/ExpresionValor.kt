package dondeInvierto.dominio.indicadores

import dondeInvierto.dominio.empresas.Empresa

import java.time.Year

class ExpresionValor(private val valor: Int) : Expresion {

    override fun evaluarEn(empresa: Empresa, anio: Year): Int = valor

}
