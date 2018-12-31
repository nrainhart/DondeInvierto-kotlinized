package dondeInvierto.dominio.indicadores

import dondeInvierto.dominio.empresas.Empresa

import java.time.Year

interface Expresion {

    fun evaluarEn(empresa: Empresa, anio: Year): Int

}
