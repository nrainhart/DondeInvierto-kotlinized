package dominio.metodologias

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
}