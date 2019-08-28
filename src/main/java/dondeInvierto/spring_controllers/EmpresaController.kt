package dondeInvierto.spring_controllers

import dondeInvierto.dominio.empresas.Cuenta
import dondeInvierto.dominio.empresas.Empresa
import dondeInvierto.dominio.indicadores.Indicador
import dondeInvierto.repo.EmpresaRepository
import dondeInvierto.repo.IndicadorRepository
import excepciones.NoExisteElResultadoBuscadoError
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/empresas")
class EmpresaController(@Autowired val empresaRepository: EmpresaRepository,
                        @Autowired val indicadorRepository: IndicadorRepository
) {

  @GetMapping
  fun listar(): Iterable<Empresa> = empresaRepository.findAll()

  @GetMapping("/{id}")
  fun detalle(@PathVariable("id") idEmpresa: Long): List<Cuenta> {
    val empresa: Empresa = empresaRepository.findByIdOrNull(idEmpresa)
      ?: throw NoExisteElResultadoBuscadoError("No pudo encontrarse el resultado para Empresa[id=$idEmpresa]")
    val indicadoresAEvaluar: List<Indicador> = indicadorRepository.findAll().toList()
    val cuentasSeleccionadas = empresa.getCuentas() + empresa.resultadosParaEstosIndicadores(indicadoresAEvaluar)
    return cuentasSeleccionadas.sortedByDescending { it.anio }
  }
}