package dondeInvierto.dominio.metodologias

import dondeInvierto.dominio.empresas.Empresa

import javax.persistence.*
import java.time.Year

@Entity
@Table(name = "cuantificadores")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
abstract class Cuantificador {

    @Id
    @GeneratedValue
    var id: Long? = null

    abstract fun evaluarEn(empresa: Empresa, anio: Year): Int

}
