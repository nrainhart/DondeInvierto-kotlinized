package dondeInvierto.dominio.metodologias

import dondeInvierto.dominio.empresas.Empresa
import excepciones.AntiguedadMenorACeroError
import excepciones.NoExisteCuentaError

import javax.persistence.*
import java.time.Year
import java.util.stream.IntStream

@Entity
@Table(name = "operandos_condicion")
class OperandoCondicion(@Enumerated
                        private val operacionAgregacion: OperacionAgregacion,
                        @ManyToOne(cascade = [CascadeType.PERSIST], fetch = FetchType.EAGER)
                        private val indicadorOAntiguedad: Cuantificador,
                        aniosAEvaluar: Int) {

    @Id
    @GeneratedValue
    private val id: Long? = null

    private val aniosAEvaluar: Int = aniosAEvaluar - 1 //Si aniosAEvaluar es 1, el intervalo tiene que ser (X,X), no (X-1,X)

    init {
        if (aniosAEvaluar < 0) {
            throw AniosAEvaluarMenorAUnoError("La condición se tiene que evaluar al menos en un año")
        }
    }

    fun sePuedeEvaluarPara(empresa: Empresa, anioActual: Int): Boolean {
        return try {
            this.valorPara(empresa, anioActual)
            true
        } catch (e: NoExisteCuentaError) {
            false
        } catch (e: AntiguedadMenorACeroError) {
            false
        }

    }

    fun valorPara(empresa: Empresa, anioActual: Int): Int {
        val periodoAEvaluar = IntStream.rangeClosed(anioActual - aniosAEvaluar, anioActual)
        val indicesEnPeriodo = periodoAEvaluar.map { anio -> indicadorOAntiguedad.evaluarEn(empresa, Year.of(anio)) }
        return operacionAgregacion.aplicarA(indicesEnPeriodo)
    }
}

internal class AniosAEvaluarMenorAUnoError(e: String) : RuntimeException(e)