package dondeInvierto.dominio.empresas

import java.time.Year

    data class Cuenta(val anio: Year, val tipoCuenta: String, val valor: Int) {

        fun esDeTipoYAnio(tipoCuenta: String, anio: Year) = esDeTipo(tipoCuenta) && esDeAnio(anio)

        private fun esDeTipo(tipo: String) = this.tipoCuenta.equals(tipo, ignoreCase = true)

        private fun esDeAnio(anio: Year) = this.anio == anio

    }

