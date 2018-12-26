package dominio.indicadores

import dominio.empresas.Empresa
import dominio.metodologias.Cuantificador
import dominio.parser.ParserIndicadores
import excepciones.NoExisteCuentaError
import excepciones.NoExisteElResultadoBuscadoError
import org.uqbarproject.jpa.java8.extras.WithGlobalEntityManager
import org.uqbarproject.jpa.java8.extras.transaction.TransactionalOps

import javax.persistence.*
import java.time.Year
import java.util.ArrayList

@Entity
class Indicador(val nombre: String) : Cuantificador(), WithGlobalEntityManager, TransactionalOps {

    var equivalencia: String? = null

    @OneToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL], orphanRemoval = true)
    private var resultados: MutableList<IndicadorPrecalculado> = ArrayList()

    @Transient
    var expresion: Expresion? = null

    override fun evaluarEn(empresa: Empresa, anio: Year): Int {
        try {
            return obtenerResultadoPara(empresa, anio)
        } catch (e: NoExisteElResultadoBuscadoError) {
            val indPrecalculado = precalcularIndicador(empresa, anio)
            guardarResultado(indPrecalculado)
            return indPrecalculado.valor
        }
    }

    fun esAplicableA(empresa: Empresa, anio: Year): Boolean {
        try {
            evaluarEn(empresa, anio)
            return true
        } catch (e: NoExisteCuentaError) {
            return false
        }
    }

    private fun precalcularIndicador(empresa: Empresa, anio: Year): IndicadorPrecalculado {
        if (expresion == null) {
            inicializarExpresion()
        }
        val resultado = expresion!!.evaluarEn(empresa, anio)
        return IndicadorPrecalculado(empresa, anio, resultado)
    }

    private fun obtenerResultadoPara(empresa: Empresa, anio: Year): Int {
        val indPrecalculado = resultados.stream()
                .filter { indPreCalc -> indPreCalc.esDe(empresa, anio) }
                .findFirst()
                .orElseThrow { NoExisteElResultadoBuscadoError("No pudo encontrarse el resultado para " + empresa.nombre + " en " + anio) }
        return indPrecalculado.valor
    }

    private fun guardarResultado(indPrecalculado: IndicadorPrecalculado) {
        resultados.add(indPrecalculado)
        withTransaction { setResultados(resultados) }
    }

    fun eliminarResultadosPrecalculados() {
        resultados.clear()
        withTransaction { setResultados(resultados) }
    }

    fun seLlama(nombre: String): Boolean {
        return this.nombre.equals(nombre, ignoreCase = true)
    }

    private fun inicializarExpresion() {
        val indicador = ParserIndicadores.parse(this.equivalencia)
        expresion = indicador.expresion
    }

    fun getResultados(): List<IndicadorPrecalculado>? {
        return resultados
    }

    private fun setResultados(resultados: MutableList<IndicadorPrecalculado>) {
        this.resultados = resultados
    }

    override fun equals(other: Any?): Boolean {
        return other is Indicador && this.seLlama(other.nombre)
    }

    override fun hashCode(): Int {
        return nombre.hashCode()
    }

    override fun toString(): String {
        return nombre
    }

}