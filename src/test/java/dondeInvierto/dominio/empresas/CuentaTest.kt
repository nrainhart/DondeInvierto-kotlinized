package dondeInvierto.dominio.empresas

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.time.Year

  @Suppress("NonAsciiCharacters")

  class CuentaTest {

    @Test
    fun `una cuenta es de un tipo y año determinados`() {
      val anio: Year = Year.of(2019)
      val tipoDeCuenta = "EBIT"
      val cuentaCreada = Cuenta(anio, tipoDeCuenta, 1000000)
      assertThat(cuentaCreada.esDeTipoYAnio(tipoDeCuenta, anio)).isTrue()
    }

  }

