package dondeInvierto.dominio.metodologias

import dondeInvierto.dominio.empresas.Empresa

import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "condiciones_prioritarias")
class CondicionPrioritaria(operando: OperandoCondicion, operacionRelacional: OperacionRelacional) : Condicion(operando, operacionRelacional) {

    fun esMejorQue(empresa1: Empresa, empresa2: Empresa, anioActual: Int): Boolean {
        return operacionRelacional.aplicarA(operando.valorPara(empresa1, anioActual), operando.valorPara(empresa2, anioActual))
    }

}
