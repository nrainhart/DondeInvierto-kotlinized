package dondeInvierto.dominio.empresas

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table
import java.time.Year
import java.util.Objects

@Entity
@Table(name = "cuentas")
class Cuenta(val anio: Year, val tipoCuenta: String, valor: Int) {

    @Id
    @GeneratedValue
    private val id: Long? = null

    var valor: Int = valor
        private set

    fun actualizar(cuentaConDatosNuevos: Cuenta) {
        valor = cuentaConDatosNuevos.valor
    }

    fun esDeTipoYAnio(tipoCuenta: String, anio: Year) =
      esDeTipo(tipoCuenta) && esDeAnio(anio)

    private fun esDeTipo(tipo: String): Boolean = this.tipoCuenta.equals(tipo, ignoreCase = true)

    private fun esDeAnio(anio: Year): Boolean = this.anio == anio

    override fun equals(other: Any?): Boolean {
        return other is Cuenta &&
                anio == other.anio &&
                tipoCuenta == other.tipoCuenta
    }

    override fun hashCode(): Int = Objects.hash(anio, tipoCuenta)

}