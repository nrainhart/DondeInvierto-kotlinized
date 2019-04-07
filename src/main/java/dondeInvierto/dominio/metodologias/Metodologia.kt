package dondeInvierto.dominio.metodologias

import dondeInvierto.dominio.empresas.Empresa

import javax.persistence.*
import java.util.ArrayList

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

    fun resultadoPara(empresas: List<Empresa>, anioActual: Int): ResultadoEvaluacionDeMetodologias {
        val (empresasSinDatosFaltantes, empresasConDatosFaltantes) = empresas.partition { tieneDatosParaEvaluarTodasLasCondiciones(it, anioActual) }
        val (empresasQueCumplenTaxativas, empresasQueNoCumplenTaxativas) = empresasSinDatosFaltantes.partition { cumpleCondicionesTaxativas(it, anioActual) }
        val empresasOrdenadas = ordenarPorPrioridad(empresasQueCumplenTaxativas, anioActual)
        return ResultadoEvaluacionDeMetodologias(nombre, empresasOrdenadas, empresasQueNoCumplenTaxativas, empresasConDatosFaltantes);
    }

    fun evaluarPara(empresas: List<Empresa>, anioActual: Int) = resultadoPara(empresas, anioActual).empresasOrdenadas
    fun empresasQueNoCumplenTaxativas(empresas: List<Empresa>, anioActual: Int) = resultadoPara(empresas, anioActual).empresasQueNoCumplen
    fun empresasConDatosFaltantes(empresas: List<Empresa>, anioActual: Int) = resultadoPara(empresas, anioActual).empresasSinDatos

    private fun tieneDatosParaEvaluarTodasLasCondiciones(empresa: Empresa, anioActual: Int): Boolean {
        return todasLasCondiciones().all { it.sePuedeEvaluarPara(empresa, anioActual) }
    }

    private fun cumpleCondicionesTaxativas(empr: Empresa, anioActual: Int): Boolean {
        return condicionesTaxativas.all { it.laCumple(empr, anioActual) }
    }

    private fun ordenarPorPrioridad(empresasQueCumplenTaxativas: List<Empresa>, anioActual: Int): List<Empresa> {
        return empresasQueCumplenTaxativas.sortedWith(Comparator { emp1, emp2 ->
            puntaje(emp2, empresasQueCumplenTaxativas, anioActual).compareTo(
                    puntaje(emp1, empresasQueCumplenTaxativas, anioActual))
        })
    }

    private fun todasLasCondiciones() = condicionesTaxativas.plus(condicionesPrioritarias)

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

data class ResultadoEvaluacionDeMetodologias(val nombreMetodologia: String,
                                             val empresasOrdenadas: List<Empresa>,
                                             val empresasQueNoCumplen: List<Empresa>,
                                             val empresasSinDatos: List<Empresa>)