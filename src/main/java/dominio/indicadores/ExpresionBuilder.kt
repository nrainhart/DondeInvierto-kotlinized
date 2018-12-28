package dominio.indicadores

class ExpresionBuilder {

    private var expresion: Expresion? = null

    fun build(): Expresion = expresion!!

    fun agregarExpresion(expresion: Expresion) {
        this.expresion = expresion
    }

    fun agregarOperacion(operandoDer: Expresion, operadorBinario: String) {
        expresion = ExpresionOperacion(expresion!!, operandoDer, operadorBinario)
    }

}
