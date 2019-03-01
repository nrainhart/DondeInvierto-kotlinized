package dondeInvierto.dominio.repo

import dondeInvierto.dominio.metodologias.Metodologia
import javax.persistence.Entity

@Entity
class RepositorioMetodologias : AbstractLocalRepository<Metodologia>() {

    override fun mensajeEntidadExistenteError(elemento: Metodologia) = "Ya existe una metodología con el nombre " + elemento.nombre

    fun obtenerPorId(id: Long?): Metodologia = obtenerTodos().first { it.id == id }

}