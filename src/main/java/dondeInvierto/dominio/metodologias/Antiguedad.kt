package dondeInvierto.dominio.metodologias

import dondeInvierto.dominio.empresas.Empresa
import excepciones.AntiguedadMenorACeroError

import javax.persistence.Entity
import java.time.Year

@Entity
class Antiguedad : Cuantificador() { // TODO tener una única instancia de Antiguedad en la BD

    override fun evaluarEn(empresa: Empresa, anio: Year): Int {
        val antiguedad = anio.value - empresa.anioDeCreacion().value
        if (antiguedad < 0) {
            throw AntiguedadMenorACeroError("La empresa " + empresa.nombre + " no existía en el año " + anio)
        }
        return antiguedad
    }
}