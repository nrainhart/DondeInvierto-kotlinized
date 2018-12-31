package dondeInvierto.dominio.indicadores

import dondeInvierto.dominio.empresas.Empresa

import javax.persistence.*
import java.time.Year
import java.util.*

@Entity
@Table(name = "indicadoresPrecalculados")
class IndicadorPrecalculado(@ManyToOne val empresa: Empresa, val anio: Year, val valor: Int) {
    @Id
    @GeneratedValue
    private val id: Long? = null

    fun esDe(empresa: Empresa, anio: Year): Boolean {
        return this.empresa == empresa && this.anio == anio
    }

    override fun equals(other: Any?): Boolean {
        return other is IndicadorPrecalculado && other.empresa == empresa && other.anio == anio && other.valor == valor
    }

    override fun hashCode(): Int = Objects.hash(empresa, anio, valor)
}
