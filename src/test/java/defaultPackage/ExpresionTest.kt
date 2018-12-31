package defaultPackage

import dondeInvierto.dominio.indicadores.ExpresionOperacion
import dondeInvierto.dominio.indicadores.ExpresionValor
import org.junit.Test
import java.lang.IllegalArgumentException
import kotlin.test.assertFailsWith

class ExpresionTest {

    @Test
    fun cuandoSeCreaUnaExpresionOperacion_ConUnOperadorInvalido_seLanzaUnaExcepcion() {
        assertFailsWith(IllegalArgumentException::class, "Las únicas operaciones aceptadas son: [+,-,*,/]") {
            val operadorInvalido = "Operador Invalido"
            ExpresionOperacion(ExpresionValor(5), ExpresionValor(2), operadorInvalido)
        }
    }
}
