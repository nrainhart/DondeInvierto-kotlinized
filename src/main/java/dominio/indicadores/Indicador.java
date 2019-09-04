package dominio.indicadores;

import dominio.empresas.Empresa;
import dominio.metodologias.Cuantificador;
import dominio.parser.ParserIndicadores;
import excepciones.NoExisteCuentaError;
import excepciones.NoExisteElResultadoBuscadoError;
import org.uqbarproject.jpa.java8.extras.WithGlobalEntityManager;
import org.uqbarproject.jpa.java8.extras.transaction.TransactionalOps;

import javax.persistence.*;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Indicador extends Cuantificador implements WithGlobalEntityManager, TransactionalOps{
	
	private String nombre;
	
	private String equivalencia;
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<IndicadorPrecalculado> resultados;
	
	@Transient
	private Expresion expresion;

	private Indicador(){}
	
	public Indicador(String nombre){
		this.nombre = nombre;
		resultados = new ArrayList<IndicadorPrecalculado>();
	}
	
	public int evaluarEn(Empresa empresa, Year anio){
		try {
			return this.obtenerResultadoPara(empresa,anio);
		}catch(NoExisteElResultadoBuscadoError e) {
			IndicadorPrecalculado indPrecalculado = this.precalcularIndicador(empresa, anio);
			this.guardarResultado(indPrecalculado);
			return indPrecalculado.getValor();
		}
	}
	
	public boolean esAplicableA(Empresa empresa, Year anio){
		int a = empresa.anioDeCreacion();
		try{
			this.evaluarEn(empresa, anio);
			return true;
		} catch (NoExisteCuentaError e){
			return false;
		}
	}
	
	private IndicadorPrecalculado precalcularIndicador(Empresa empresa, Year anio){
		if(expresion == null){
			this.inicializarExpresion();
		}
		int resultado = expresion.evaluarEn(empresa,anio);
		IndicadorPrecalculado ind = new IndicadorPrecalculado(empresa, anio, resultado);
		return ind; 
	}
	
	private int obtenerResultadoPara(Empresa empresa, Year anio) {
		IndicadorPrecalculado indPrecalculado = resultados.stream().filter(indPreCalc -> indPreCalc.esDe(empresa, anio))
				.findFirst().orElseThrow(() -> new NoExisteElResultadoBuscadoError("No pudo encontrarse el resultado para " + empresa.getNombre() + " en " + anio));
		return indPrecalculado.getValor();
	}
	
	private void guardarResultado(IndicadorPrecalculado indPrecalculado) {
		resultados.add(indPrecalculado);
		withTransaction(() -> {
			this.setResultados(resultados);
		});	
	}
	
	public void eliminarResultadosPrecalculados(){
		resultados.clear();
		withTransaction(() -> {
			this.setResultados(resultados);
		});
	}
	
	public boolean seLlama(String nombre){
		return this.nombre.equalsIgnoreCase(nombre);
	}
	
	private void inicializarExpresion(){
		Indicador indicador = ParserIndicadores.parse(this.getEquivalencia());
		expresion = indicador.getExpresion();
	}
	
	private Expresion getExpresion() {
		return expresion;
	}

	public void setEquivalencia(String equivalencia){
		this.equivalencia = equivalencia;
	}
	
	public String getEquivalencia() {
		return equivalencia;
	}
	
	public String getNombre() {
		return nombre;
	}
	
	public void setExpresion(Expresion expresion) {
		this.expresion = expresion;
	}
	
	public boolean equals(Object otroObjeto) {
	    return (otroObjeto instanceof Indicador) && this.seLlama(((Indicador) otroObjeto).getNombre());
	}
	
	public int hashCode() {
		return nombre.hashCode();
	}
	
	public List<IndicadorPrecalculado> getResultados() {
		return resultados;
	}

	public void setResultados(List<IndicadorPrecalculado> resultados) {
		this.resultados = resultados;
	}
	
	@Override
	public String toString() {
		return nombre;
	}
	
}
