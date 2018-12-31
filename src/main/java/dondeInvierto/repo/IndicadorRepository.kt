package dondeInvierto.repo

import dondeInvierto.dominio.indicadores.Indicador
import org.springframework.data.repository.CrudRepository

interface IndicadorRepository: CrudRepository<Indicador, Long>