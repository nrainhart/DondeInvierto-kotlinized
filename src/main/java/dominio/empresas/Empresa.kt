package dominio.empresas

import dominio.indicadores.Indicador
import excepciones.NoExisteCuentaError
import java.time.Year
import javax.persistence.*

@Entity
@Table(name = "empresas")
class Empresa(val nombre: String,
              @OneToMany(cascade = [CascadeType.PERSIST])
              @JoinColumn(name = "empresa_id")
              private val cuentas: MutableList<Cuenta>
) {

    @Id
    @GeneratedValue
    val id: Long? = null

    fun anioDeCreacion(): Year {
        return cuentas.map { it.anio }
                .min()
                ?: throw NoExisteCuentaError("La empresa no tiene ninguna cuenta, por lo que no se puede calcular el año de creación.")
    }

    fun cantidadDeCuentas(): Int = cuentas.size

    fun seLlama(nombre: String): Boolean = this.nombre == nombre

    fun registrarCuenta(cuenta: Cuenta) {
        cuentas.add(cuenta)
    }

    fun actualizar(empresaConDatosNuevos: Empresa) {
        for (cuenta in empresaConDatosNuevos.getCuentas()) {
            if (!cuentas.contains(cuenta))
                registrarCuenta(cuenta)
            else {
                val cuentaAActualizar = cuentas[cuentas.indexOf(cuenta)]
                cuentaAActualizar.actualizar(cuenta)
            }
        }
    }

    fun aniosDeLosQueTieneCuentas(): Set<Year> {
        return cuentas.map { it.anio }
                .toSet()
    }

    fun resultadosParaEstosIndicadores(indicadores: List<Indicador>): List<Cuenta> {
        return aniosDeLosQueTieneCuentas().flatMap { anio -> resultadosParaEstosIndicadoresSegunAnio(indicadores, anio) }
    }

    private fun resultadosParaEstosIndicadoresSegunAnio(indicadores: List<Indicador>, anio: Year): List<Cuenta> {
        return indicadores.filter { it.esAplicableA(this, anio) }
                .map { indicador -> Cuenta(anio, indicador.nombre, indicador.evaluarEn(this, anio)) }
        // TODO no se debería representar el resultado de evaluar un Indicador con una Cuenta
    }

    fun getCuentas(): List<Cuenta> = cuentas

    fun getValorCuenta(tipoCuenta: String, anio: Year): Int {
        return cuentas.firstOrNull { cuenta -> cuenta.esDeTipo(tipoCuenta) && cuenta.esDeAnio(anio) }
                //El first podría enmascarar el caso erróneo en el que haya dos cuentas del mismo tipo con valores distintos en el mismo año
                ?.valor
                ?: throw NoExisteCuentaError("No se pudo encontrar la cuenta " + tipoCuenta + " en el año " + anio + " para la empresa " + this.nombre + ".")
    }

    override fun equals(other: Any?): Boolean = other is Empresa && this.seLlama(other.nombre)

    override fun hashCode(): Int = nombre.hashCode()

    override fun toString(): String = nombre //Es necesario para que el Selector muestre solo el nombre de la empresa

}