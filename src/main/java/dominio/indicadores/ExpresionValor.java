package dominio.indicadores;

import dominio.empresas.Empresa;

import java.time.Year;

public class ExpresionValor implements Expresion{
	
	private int valor;
	
	public ExpresionValor(int valor) {
		this.valor = valor;
	}

	public int evaluarEn(Empresa empresa, Year anio) {
		return valor;
	}
	
}
