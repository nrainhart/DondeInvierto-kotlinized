package dominio.metodologias

import dominio.empresas.Empresa

import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "condiciones_taxativas")
class CondicionTaxativa(operando: OperandoCondicion, operacionRelacional: OperacionRelacional, private val valor: Int) : Condicion(operando, operacionRelacional) {

    fun laCumple(empresa: Empresa, anioActual: Int): Boolean {
        return operacionRelacional.aplicarA(operando.valorPara(empresa, anioActual), valor)
    }

}
