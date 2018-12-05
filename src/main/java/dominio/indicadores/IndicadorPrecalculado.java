package dominio.indicadores;

import dominio.empresas.Empresa;

import javax.persistence.*;
import java.time.Year;

@Entity
@Table(name = "indicadoresPrecalculados")
public class IndicadorPrecalculado {
	//@OneToOne o @ManyToOne ???
	
	@Id
	@GeneratedValue
	private Long id;
	
	@OneToOne
	private Empresa empresa;
	private Year anio;
	private int valor;
	
	private IndicadorPrecalculado() {}
	
	public IndicadorPrecalculado(Empresa empresa, Year anio, int valor) {
		this.empresa = empresa;
		this.anio = anio;
		this.valor = valor;
	}
	
	public boolean esDe(Empresa empresa, Year anio) {
		return this.empresa.equals(empresa) && this.anio.equals(anio);
	}
	
	public int getValor() {
		return valor;
	}
	
	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	public Year getAnio() {
		return anio;
	}

	public void setAnio(Year anio) {
		this.anio = anio;
	}

	@Override
	public boolean equals(Object otroObjeto) {
	    return (otroObjeto instanceof IndicadorPrecalculado) && this.sonResultadosIguales((IndicadorPrecalculado) otroObjeto);
	}
	
	private boolean sonResultadosIguales(IndicadorPrecalculado otroResultado) {
		return otroResultado.getEmpresa().equals(empresa) && otroResultado.getAnio().equals(anio) && otroResultado.getValor() == valor;
	}
}
