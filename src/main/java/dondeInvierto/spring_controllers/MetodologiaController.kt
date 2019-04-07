package dondeInvierto.spring_controllers

import dondeInvierto.dominio.empresas.Empresa
import dondeInvierto.dominio.metodologias.Metodologia
import dondeInvierto.dominio.metodologias.ResultadoEvaluacionDeMetodologias
import dondeInvierto.repo.EmpresaRepository
import dondeInvierto.repo.MetodologiaRepository
import excepciones.NoExisteElResultadoBuscadoError
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping("/api/metodologias")
class MetodologiaController(@Autowired val metodologiaRepository: MetodologiaRepository,
                            @Autowired val empresaRepository: EmpresaRepository
) {

    @GetMapping
    fun listar() = metodologiaRepository.findAll().toList()

    @GetMapping("/{id}")
    fun evaluar(@PathVariable("id") idMetodologia: Long): ResultadoEvaluacionDeMetodologiasTO {
        val metodologiaAEvaluar: Metodologia = metodologiaRepository.findById(idMetodologia)
                .orElseThrow { NoExisteElResultadoBuscadoError("No pudo encontrarse el resultado para Metodologia[id=$idMetodologia]") }
        val empresas: List<Empresa> = empresaRepository.findAll().toList()
        val anioActual = LocalDate.now().year
        val resultadoEvaluacionDeMetodologias = metodologiaAEvaluar.resultadoPara(empresas, anioActual)
        return convertirATO(resultadoEvaluacionDeMetodologias)
    }

    private fun convertirATO(resultadoEvaluacionDeMetodologias: ResultadoEvaluacionDeMetodologias): ResultadoEvaluacionDeMetodologiasTO {
        return ResultadoEvaluacionDeMetodologiasTO(
                resultadoEvaluacionDeMetodologias.nombreMetodologia,
                nombresDeEmpresas(resultadoEvaluacionDeMetodologias.empresasOrdenadas),
                nombresDeEmpresas(resultadoEvaluacionDeMetodologias.empresasQueNoCumplen),
                nombresDeEmpresas(resultadoEvaluacionDeMetodologias.empresasSinDatos)
        )
    }

    private fun nombresDeEmpresas(empresas: List<Empresa>) = empresas.map { it.nombre }

}

data class ResultadoEvaluacionDeMetodologiasTO(val nombreMetodologia: String,
                                               val empresasOrdenadas: List<String>,
                                               val empresasQueNoCumplen: List<String>,
                                               val empresasSinDatos: List<String>)