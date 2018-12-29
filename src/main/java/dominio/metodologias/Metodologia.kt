package dominio.metodologias

import dominio.empresas.Empresa

import javax.persistence.*
import java.util.ArrayList
import java.util.HashSet

@Entity
@Table(name = "metodologias")
class Metodologia(val nombre: String) {
    @Id
    @GeneratedValue
    val id: Long? = null

    @OneToMany(cascade = [CascadeType.PERSIST])
    @JoinColumn(name = "metodologia_id")
    private val condicionesTaxativas: MutableList<CondicionTaxativa> = ArrayList()

    @OneToMany(cascade = [CascadeType.PERSIST])
    @JoinColumn(name = "metodologia_id")
    private val condicionesPrioritarias: MutableList<CondicionPrioritaria> = ArrayList()

    fun evaluarPara(empresas: List<Empresa>, anioActual: Int): List<Empresa> {
        val empresasQueCumplenTaxativas = empresasQueCumplenTaxativas(empresasSinDatosFaltantes(empresas, anioActual), anioActual)
        return empresasQueCumplenTaxativas.sortedWith(Comparator{ emp1, emp2 ->
                    puntaje(emp2, empresasQueCumplenTaxativas, anioActual).compareTo(
                            puntaje(emp1, empresasQueCumplenTaxativas, anioActual))
                })
    }

    fun empresasQueNoCumplenTaxativas(empresas: List<Empresa>, anioActual: Int): List<Empresa> { //Devuelve sólo las que no cumplen (no las que faltan datos)
        return empresasSinDatosFaltantes(empresas, anioActual).filter { empresa -> !cumpleCondicionesTaxativas(empresa, anioActual) }
    }

    fun empresasConDatosFaltantes(empresas: List<Empresa>, anioActual: Int): List<Empresa> {
        val condiciones = obtenerTodasLasCondiciones()
        return this.empresasConDatosInsuficientesParaLasCondiciones(empresas, condiciones, anioActual)
    }

    private fun empresasSinDatosFaltantes(empresas: List<Empresa>, anioActual: Int): List<Empresa> {
        return empresas.filter { empresa -> !empresasConDatosFaltantes(empresas, anioActual).contains(empresa) }
    }

    private fun empresasQueCumplenTaxativas(empresas: List<Empresa>, anioActual: Int): List<Empresa> {
        return empresas.filter { empresa -> !empresasQueNoCumplenTaxativas(empresas, anioActual).contains(empresa) }
    }

    private fun obtenerTodasLasCondiciones(): List<Condicion> = condicionesTaxativas.plus(condicionesPrioritarias)

    private fun empresasConDatosInsuficientesParaLasCondiciones(empresas: List<Empresa>, condiciones: List<Condicion>, anioActual: Int): List<Empresa> {
        val empresasConDatosFaltantes = HashSet<Empresa>()
        condiciones.forEach { cond -> empresasConDatosFaltantes.addAll(empresasConDatosFaltantesParaEstaCondicion(empresas, cond, anioActual)) }
        return ArrayList(empresasConDatosFaltantes)
    }

    private fun empresasConDatosFaltantesParaEstaCondicion(empresas: List<Empresa>, condicion: Condicion, anioActual: Int): List<Empresa> {
        return empresas.filter { emp -> !condicion.getOperando().sePuedeEvaluarPara(emp, anioActual) }
    }

    private fun cumpleCondicionesTaxativas(empr: Empresa, anioActual: Int): Boolean {
        return condicionesTaxativas.all { it.laCumple(empr, anioActual) }
    }

    private fun puntaje(empresa: Empresa, empresas: List<Empresa>, anioActual: Int): Int {
        return condicionesPrioritarias.map { cond -> puntosObtenidosPara(empresa, cond, empresas, anioActual) }
                .sum()
    }

    private fun puntosObtenidosPara(empresa: Empresa, condicion: CondicionPrioritaria, empresas: List<Empresa>, anioActual: Int): Int {
        val empresasQueSonPeores = empresasQueSonPeoresSegunCondicion(empresa, condicion, empresas, anioActual)
        return empresasQueSonPeores.size
    }

    private fun empresasQueSonPeoresSegunCondicion(empresa: Empresa, condicion: CondicionPrioritaria, empresas: List<Empresa>, anioActual: Int): List<Empresa> {
        return empresas.filter { otraEmpresa -> condicion.esMejorQue(empresa, otraEmpresa, anioActual) }
    }

    fun agregarCondicionTaxativa(cond: CondicionTaxativa) {
        condicionesTaxativas.add(cond)
    }

    fun agregarCondicionPrioritaria(cond: CondicionPrioritaria) {
        condicionesPrioritarias.add(cond)
    }

    fun esMetodologiaValida(): Boolean {
        return !nombre.isEmpty() && !condicionesTaxativas.isEmpty() && !condicionesPrioritarias.isEmpty()
    }

    override fun equals(other: Any?): Boolean = other is Metodologia && this.nombre == other.nombre

    override fun hashCode(): Int = nombre.hashCode()

    override fun toString(): String = nombre

}