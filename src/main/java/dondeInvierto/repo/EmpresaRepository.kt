package dondeInvierto.repo

import dondeInvierto.dominio.empresas.Empresa
import org.springframework.data.repository.CrudRepository

interface EmpresaRepository: CrudRepository<Empresa, Long>