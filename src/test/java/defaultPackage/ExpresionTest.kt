package defaultPackage

import dominio.indicadores.ExpresionOperacion
import dominio.indicadores.ExpresionValor
import org.junit.Test
import java.lang.IllegalStateException
import kotlin.test.assertFailsWith

class ExpresionTest {

    @Test
    fun cuandoSeCreaUnaExpresionOperacion_ConUnOperadorInvalido_seLanzaUnaExcepcion() {
        assertFailsWith(IllegalStateException::class, "Las únicas operaciones aceptadas son: [+,-,*,/]") {
            val operadorInvalido = "Operador Invalido"
            ExpresionOperacion(ExpresionValor(5), ExpresionValor(2), operadorInvalido)
        }
    }
}
