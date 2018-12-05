package dominio.metodologias;

import excepciones.MetodologiaInvalidaError;

public class MetodologiaBuilder {
	private Metodologia metodologia;
	
	public MetodologiaBuilder crearMetodologia(String nombre){
		metodologia = new Metodologia(nombre);
		return this;
	}
	
	public Metodologia buildMetodologia(){
		if (!this.contieneMetodologiaValida()){
			throw new MetodologiaInvalidaError("Metodología inválida (falta nombre o al menos 1 condición taxativa y 1 prioritaria)");
		}
		return metodologia;
	}
	
	public MetodologiaBuilder agregarCondicionTaxativa(OperacionAgregacion opAgregacion, Cuantificador indicadorOAntiguedad, 
			int aniosAEvaluar, OperacionRelacional opRelacional, int valor){
		metodologia.agregarCondicionTaxativa(new CondicionTaxativa(this.crearOperandoCondicion(opAgregacion, indicadorOAntiguedad, aniosAEvaluar),
				opRelacional, valor));
		return this;
	}
	
	public MetodologiaBuilder agregarCondicionPrioritaria(OperacionAgregacion opAgregacion, Cuantificador indicadorOAntiguedad, 
			int aniosAEvaluar, OperacionRelacional opRelacional){
		metodologia.agregarCondicionPrioritaria(new CondicionPrioritaria(this.crearOperandoCondicion(opAgregacion, indicadorOAntiguedad, aniosAEvaluar),
				opRelacional));
		return this;
	}
	
	private OperandoCondicion crearOperandoCondicion(OperacionAgregacion opAgregacion, Cuantificador indicadorOAntiguedad, int aniosAEvaluar){
		return new OperandoCondicion(opAgregacion, indicadorOAntiguedad, aniosAEvaluar);
	}
	
	public boolean contieneMetodologiaValida(){
		return metodologia.esMetodologiaValida();
	}
}