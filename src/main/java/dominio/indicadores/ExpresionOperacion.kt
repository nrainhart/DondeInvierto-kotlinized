package dominio.indicadores

import dominio.empresas.Empresa
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

import java.time.Year

class ExpresionOperacion(private val operandoIzq: Expresion,
                         private val operandoDer: Expresion,
                         operadorBinario: String) : Expresion {

    private val operadorAritmetico: OperacionAritmetica = when (operadorBinario) {
        "+" -> OperacionAritmetica.Mas
        "-" -> OperacionAritmetica.Menos
        "*" -> OperacionAritmetica.Por
        "/" -> OperacionAritmetica.Dividido
        else -> throw IllegalArgumentException("Las únicas operaciones aceptadas son: [+,-,*,/]")
    }

    override fun evaluarEn(empresa: Empresa, anio: Year): Int {
        return operadorAritmetico.applyAsInt(operandoIzq.evaluarEn(empresa, anio), operandoDer.evaluarEn(empresa, anio))
    }

}
