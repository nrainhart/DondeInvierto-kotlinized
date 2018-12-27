package dominio.indicadores

import dominio.empresas.Empresa

import java.time.Year

interface Expresion {

    fun evaluarEn(empresa: Empresa, anio: Year): Int

}
