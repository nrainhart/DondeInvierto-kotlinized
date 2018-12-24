package dominio.empresas

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

    fun esDeTipo(tipo: String): Boolean {
        return this.tipoCuenta.equals(tipo, ignoreCase = true)
    }

    fun esDeAnio(anio: Year): Boolean {
        return this.anio == anio
    }

    override fun equals(other: Any?): Boolean {
        return other is Cuenta &&
                anio == other.anio &&
                tipoCuenta == other.tipoCuenta
    }

    override fun hashCode(): Int {
        return Objects.hash(anio, tipoCuenta)
    }

}