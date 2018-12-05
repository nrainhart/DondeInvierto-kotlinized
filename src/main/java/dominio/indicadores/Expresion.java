package dominio.indicadores;

import dominio.empresas.Empresa;

import java.time.Year;

public interface Expresion {
	
	public abstract int evaluarEn(Empresa empresa, Year anio);

}
