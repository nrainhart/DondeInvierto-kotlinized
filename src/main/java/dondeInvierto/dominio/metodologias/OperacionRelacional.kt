package dondeInvierto.dominio.metodologias

enum class OperacionRelacional {

    Mayor {
        override fun aplicarA(num1: Int, num2: Int) = num1 > num2

        override fun toString() = ">"
    },

    Menor {
        override fun aplicarA(num1: Int, num2: Int) = num1 < num2

        override fun toString() = "<"
    },

    Igual {
        override fun aplicarA(num1: Int, num2: Int) = num1 == num2

        override fun toString() = "="
    };

    abstract fun aplicarA(num1: Int, num2: Int): Boolean
    abstract override fun toString(): String

}
