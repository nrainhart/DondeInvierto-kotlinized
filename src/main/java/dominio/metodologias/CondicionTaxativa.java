package dominio.metodologias;

import dominio.empresas.Empresa;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "condiciones_taxativas")
public class CondicionTaxativa extends Condicion{
	
	private int valor;
	
	private CondicionTaxativa() {} //Necesario para persistir la clase
	
	public CondicionTaxativa(OperandoCondicion operando, OperacionRelacional operacionRelacional, int valor) {
		this.operando = operando;
		this.operacionRelacional = operacionRelacional;
		this.valor = valor;
	}
	
	public boolean laCumple(Empresa empresa, int anioActual){
		return operacionRelacional.aplicarA(operando.valorPara(empresa, anioActual), valor);
	}

}
