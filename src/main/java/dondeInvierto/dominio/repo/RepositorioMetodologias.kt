package dondeInvierto.dominio.repo

import dondeInvierto.dominio.metodologias.Metodologia
import javax.persistence.Entity

@Entity
class RepositorioMetodologias : AbstractLocalRepository<Metodologia>() {

    override fun mensajeEntidadExistenteError(elemento: Metodologia): String {
        return "Ya existe una metodología con el nombre " + elemento.nombre
    }

    fun obtenerPorId(id: Long?): Metodologia {
        return obtenerTodos().stream().filter { metodologia -> metodologia.id == id }.findFirst()
                .orElseThrow<RuntimeException>(null)//Estoy seguro que la metodologia va a existir
    }

}