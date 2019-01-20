package dondeInvierto.repo

import dondeInvierto.dominio.metodologias.Metodologia
import org.springframework.data.repository.CrudRepository

interface MetodologiaRepository: CrudRepository<Metodologia, Long>