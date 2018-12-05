package dominio.indicadores;

import dominio.empresas.Empresa;

import java.time.Year;

public class ExpresionOperacion implements Expresion{
	private Expresion operandoIzq;
	private Expresion operandoDer;
	private OperacionAritmetica operadorAritmetico;
	
	public ExpresionOperacion(Expresion operandoIzq, Expresion operandoDer, String operadorBinario){
		this.operandoIzq=operandoIzq;
		this.operandoDer=operandoDer;
		if(operadorBinario.equals("+")) this.operadorAritmetico = OperacionAritmetica.Mas;
		else if (operadorBinario.equals("-")) this.operadorAritmetico = OperacionAritmetica.Menos;
		else if (operadorBinario.equals("*")) this.operadorAritmetico = OperacionAritmetica.Por;
		else if (operadorBinario.equals("/")) this.operadorAritmetico = OperacionAritmetica.Dividido;
	}
	
	public int evaluarEn(Empresa empresa, Year anio) {
		return operadorAritmetico.applyAsInt(operandoIzq.evaluarEn(empresa, anio), operandoDer.evaluarEn(empresa, anio));
	}
	
}
