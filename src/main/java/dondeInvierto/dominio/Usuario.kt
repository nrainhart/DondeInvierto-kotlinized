package dondeInvierto.dominio

import dondeInvierto.dominio.empresas.Empresa
import dondeInvierto.dominio.indicadores.Indicador
import dondeInvierto.dominio.repo.RepositorioIndicadores
import dondeInvierto.dominio.metodologias.Metodologia
import dondeInvierto.dominio.repo.RepositorioMetodologias
import dondeInvierto.dominio.parser.ParserIndicadores

import javax.persistence.*
import java.time.Year

@Entity
@Table(name = "usuarios")
class Usuario(val email: String, private val password: String) {
    @Id
    @GeneratedValue
    val id: Long? = null

    @OneToOne(cascade = [CascadeType.ALL], orphanRemoval = true)
    private val repositorioIndicadores: RepositorioIndicadores = RepositorioIndicadores()

    @OneToOne(cascade = [CascadeType.ALL], orphanRemoval = true)
    private val repositorioMetodologias: RepositorioMetodologias = RepositorioMetodologias()

    val indicadores: List<Indicador>
        get() = repositorioIndicadores.obtenerTodos()

    val metodologias: List<Metodologia>
        get() = repositorioMetodologias.obtenerTodos()

    fun validar(email: String, password: String): Boolean {
        return email == this.email && password == this.password
    }

    fun crearIndicador(formulaIndicador: String) {
        repositorioIndicadores.agregar(ParserIndicadores.parse(formulaIndicador))
    }

    fun buscarIndicador(nombreIndicador: String): Indicador {
        return repositorioIndicadores.buscarIndicador(nombreIndicador)
    }

    fun agregarIndicadores(indicadoresCreados: List<String>) {
        repositorioIndicadores.agregarMultiplesIndicadores(indicadoresCreados)
    }

    fun todosLosIndicadoresAplicablesA(empresa: Empresa): List<Indicador> {
        return repositorioIndicadores.todosLosIndicadoresAplicablesA(empresa)
    }

    fun indicadoresAplicablesA(empresa: Empresa, anio: Year): Set<Indicador> {
        return repositorioIndicadores.indicadoresAplicablesA(empresa, anio)
    }

    fun eliminarIndicadores() {
        repositorioIndicadores.eliminarIndicadores()
    }

    fun eliminarIndicadoresPrecalculados() {
        repositorioIndicadores.eliminarResultadosPrecalculados()
    }

    fun agregarMetodologia(metodologia: Metodologia) {
        repositorioMetodologias.agregar(metodologia)
    }

    fun obtenerMetodologiaPorId(id: Long?): Metodologia {
        return repositorioMetodologias.obtenerPorId(id)
    }

    override fun equals(other: Any?): Boolean = other is Usuario && this.email == other.email

    override fun hashCode(): Int = email.hashCode()

    override fun toString(): String = email

    companion object {
        var activo: Usuario? = null
    }

}