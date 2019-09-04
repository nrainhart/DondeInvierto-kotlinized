package dondeInvierto.dominio.empresas

import excepciones.NoExisteCuentaError
import java.time.Year

    class Empresa(val nombre: String, private val cuentas: MutableList<Cuenta>) {

        fun registrarCuenta(cuenta: Cuenta) {
            cuenta.valor
            cuentas.add(cuenta)
        }

        fun anioDeCreacion(): Year {
            return cuentas.map { it.anio }
                    .min()
                    ?: throw NoExisteCuentaError("La empresa no tiene ninguna cuenta, por lo que no se puede calcular el año de creación.")
        }

        fun getValorCuenta(tipoCuenta: String, anio: Year): Int {
            return cuentas.firstOrNull { cuenta -> cuenta.esDeTipoYAnio(tipoCuenta, anio) }
                    ?.valor
                    ?: throw NoExisteCuentaError("No se pudo encontrar la cuenta $tipoCuenta en el año $anio para la empresa ${this.nombre}.")
        }

    }

