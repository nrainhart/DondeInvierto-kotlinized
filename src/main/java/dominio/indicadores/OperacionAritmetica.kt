package dominio.indicadores

import java.util.function.IntBinaryOperator

enum class OperacionAritmetica: IntBinaryOperator {

    Mas {
        override fun applyAsInt(operandoIzq: Int, operandoDer: Int): Int = operandoIzq + operandoDer
    },

    Menos {
        override fun applyAsInt(operandoIzq: Int, operandoDer: Int): Int = operandoIzq - operandoDer
    },

    Por {
        override fun applyAsInt(operandoIzq: Int, operandoDer: Int): Int = operandoIzq * operandoDer
    },

    Dividido {
        override fun applyAsInt(operandoIzq: Int, operandoDer: Int): Int = operandoIzq / operandoDer
    };

    abstract override fun applyAsInt(operandoIzq: Int, operandoDer: Int): Int

}
