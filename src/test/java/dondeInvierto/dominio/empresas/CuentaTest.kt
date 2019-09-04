package dondeInvierto.dominio.empresas

import org.junit.Test
import java.time.Year

  @Suppress("NonAsciiCharacters")

  class CuentaTest {

    @Test
    fun `se puede crear una cuenta a partir de un tipo, un año y un valor`() {
      val anio: Year = Year.of(2019)
      Cuenta(anio, "EBIT", 1000000)
    }

  }

