package dondeInvierto.spring_controllers

import dondeInvierto.dominio.parser.ParserIndicadores
import dondeInvierto.repo.IndicadorRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/indicadores")
class IndicadorController(@Autowired val indicadorRepository: IndicadorRepository) {

    @GetMapping
    fun get() = indicadorRepository.findAll().map { it.equivalencia }

    @PostMapping
    fun create(@RequestBody creacionIndicadorTO: CreacionIndicadorTO) {
        val indicador = ParserIndicadores.parse(creacionIndicadorTO.formulaIndicador)
        indicadorRepository.save(indicador)
    }

}

data class CreacionIndicadorTO(val formulaIndicador: String)