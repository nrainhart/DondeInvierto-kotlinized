package dondeInvierto.dominio.metodologias

import dondeInvierto.dominio.empresas.Empresa
import javax.persistence.*

@MappedSuperclass
abstract class Condicion(@OneToOne(cascade = [CascadeType.PERSIST], fetch = FetchType.EAGER)
                         val operando: OperandoCondicion,
                         @Enumerated
                         protected val operacionRelacional: OperacionRelacional
) {
    @Id
    @GeneratedValue
    private val id: Long? = null

    fun sePuedeEvaluarPara(empresa: Empresa, anioActual: Int) = operando.sePuedeEvaluarPara(empresa, anioActual)
}