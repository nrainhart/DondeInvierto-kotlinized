package dominio.empresas

import dominio.indicadores.Indicador
import excepciones.NoExisteCuentaError

import javax.persistence.*
import java.time.Year
import java.util.ArrayList
import java.util.HashSet

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

    fun anioDeCreacion(): Year = cuentas.map { it.anio }
                .min()
                ?: throw NoExisteCuentaError("La empresa no tiene ninguna cuenta, por lo que no se puede calcular el año de creación.")

    fun cantidadDeCuentas(): Int {
        return cuentas.size
    }

    fun seLlama(nombre: String): Boolean {
        return this.nombre == nombre
    }

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
        val anios = HashSet<Year>()
        cuentas.forEach { cuenta -> anios.add(cuenta.anio) }
        return anios
    }

    fun resultadosParaEstosIndicadores(indicadores: List<Indicador>): List<Cuenta> {
        val anios = this.aniosDeLosQueTieneCuentas()
        val resultadosTotales = ArrayList<Cuenta>()
        anios.forEach { anio -> resultadosTotales.addAll(this.resultadosParaEstosIndicadoresSegunAnio(indicadores, anio)) }
        return resultadosTotales
    }

    private fun resultadosParaEstosIndicadoresSegunAnio(indicadores: List<Indicador>, anio: Year): List<Cuenta> {
        val resultados = ArrayList<Cuenta>()
        val indicadoresAplicables = indicadores.filter { ind -> ind.esAplicableA(this, anio) }
        indicadoresAplicables.forEach { ind -> resultados.add(
                Cuenta(anio, ind.nombre, ind.evaluarEn(this, anio))
        ) }
        return resultados
    }

    fun getCuentas(): List<Cuenta> {
        return cuentas
    }

    fun getValorCuenta(tipoCuenta: String, anio: Year): Int {
        val cuentaBuscada = cuentas.stream()
                .filter { cuenta -> cuenta.esDeTipo(tipoCuenta) && cuenta.esDeAnio(anio) }
                .findFirst()
                .orElseThrow { NoExisteCuentaError("No se pudo encontrar la cuenta " + tipoCuenta + " en el año " + anio + " para la empresa " + this.nombre + ".") }
        //El findFirst podría enmascarar el caso erróneo en el que haya dos cuentas del mismo tipo con valores distintos en el mismo año
        return cuentaBuscada.valor
    }

    override fun equals(other: Any?): Boolean {
        return other is Empresa && this.seLlama(other.nombre)
    }

    override fun hashCode(): Int {
        return nombre.hashCode()
    }

    override fun toString(): String { //Es necesario para que el Selector muestre solo el nombre de la empresa
        return nombre
    }

}