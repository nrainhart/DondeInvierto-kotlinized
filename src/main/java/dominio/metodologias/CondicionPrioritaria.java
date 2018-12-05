package dominio.metodologias;

import dominio.empresas.Empresa;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "condiciones_prioritarias")
public class CondicionPrioritaria extends Condicion{
	
	private CondicionPrioritaria() {} //Necesario para persistir la clase
	
	public CondicionPrioritaria(OperandoCondicion operando, OperacionRelacional operacionRelacional) {
		this.operando = operando;
		this.operacionRelacional = operacionRelacional;
	}
	
	public boolean esMejorQue(Empresa empresa1, Empresa empresa2){
		return operacionRelacional.aplicarA(operando.valorPara(empresa1), operando.valorPara(empresa2));
	}
	
}
