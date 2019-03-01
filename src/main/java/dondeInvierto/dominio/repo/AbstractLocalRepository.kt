package dondeInvierto.dominio.repo

import excepciones.EntidadExistenteError
import java.util.*
import javax.persistence.*

@MappedSuperclass
abstract class AbstractLocalRepository<T> {

    @Id
    @GeneratedValue
    private val id: Long? = null

    @OneToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "usuario_id")
    protected var elementos: MutableSet<T> = HashSet()

    fun obtenerTodos() = elementos.toList()

    fun agregar(elemento: T) {
        if (existe(elemento)) {
            throw EntidadExistenteError(mensajeEntidadExistenteError(elemento))
        }
        elementos.add(elemento)
    }

    protected fun existe(elemento: T) = obtenerTodos().contains(elemento)

    protected abstract fun mensajeEntidadExistenteError(elemento: T): String
}
