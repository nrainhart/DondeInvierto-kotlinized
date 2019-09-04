package dondeInvierto.dominio.empresas

    import java.time.Year
    import java.util.*

    class Cuenta {

        val anio: Year
        val tipoCuenta: String
        var valor: Int
            private set

        constructor(anio: Year, tipoCuenta: String, valor: Int) {
            this.anio = anio
            this.tipoCuenta = tipoCuenta
            this.valor = valor
        }

        fun actualizar(valorActualizado: Int) {
            this.valor = valorActualizado
        }

        fun esDeTipoYAnio(tipoCuenta: String, anio: Year) = esDeTipo(tipoCuenta) && esDeAnio(anio)

        private fun esDeTipo(tipo: String) = this.tipoCuenta.equals(tipo, ignoreCase = true)

        private fun esDeAnio(anio: Year) = this.anio == anio

        override fun equals(other: Any?): Boolean {
            return other is Cuenta &&
                    anio == other.anio &&
                    tipoCuenta == other.tipoCuenta &&
                    valor == other.valor
        }

        override fun hashCode() = Objects.hash(anio, tipoCuenta, valor)

    }

