package dominio.empresas

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table
import java.time.Year
import java.util.Objects

@Entity
@Table(name = "cuentas")
class Cuenta {

    @Id
    @GeneratedValue
    private val id: Long? = null

    val anio: Year
    val tipoCuenta: String
    var valor: Int = 0
        private set

    constructor(anio: Year, tipoCuenta: String, valor: Int) {
        this.anio = anio
        this.tipoCuenta = tipoCuenta
        this.valor = valor
    }

    fun actualizar(cuentaConDatosNuevos: Cuenta, empresaQueContieneCuenta: Empresa) {
        valor = cuentaConDatosNuevos.valor
    }

    fun esDeTipo(tipo: String): Boolean {
        return this.tipoCuenta.equals(tipo, ignoreCase = true)
    }

    fun esDeAnio(anio: Year): Boolean {
        return this.anio == anio
    }

    override fun equals(obj: Any?): Boolean {
        return (obj is Cuenta && anio == obj.anio
                && tipoCuenta == obj.tipoCuenta)
    }

    override fun hashCode(): Int {
        return Objects.hash(anio, tipoCuenta)
    }

}
