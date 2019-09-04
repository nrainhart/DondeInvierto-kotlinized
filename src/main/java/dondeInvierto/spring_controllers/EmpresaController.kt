package dondeInvierto.spring_controllers

import dondeInvierto.dominio.empresas.Empresa
import dondeInvierto.repo.EmpresaRepository
import dondeInvierto.repo.IndicadorRepository
import excepciones.NoExisteElResultadoBuscadoError
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.web.bind.annotation.*

interface EmpresaRepository: CrudRepository<Empresa, Long>

@RestController
@RequestMapping("/api/empresas")
class EmpresaController(@Autowired val empresaRepository: EmpresaRepository) {

  @GetMapping
  fun listar(): List<Empresa> = empresaRepository.findAll().toList()

  @GetMapping("/{id}")
  fun detalle(@PathVariable("id") idEmpresa: Long): Empresa {
    return empresaRepository.findByIdOrNull(idEmpresa)
      ?: throw NoExisteElResultadoBuscadoError("No pudo encontrarse el resultado para Empresa[id=$idEmpresa]")
  }

}

