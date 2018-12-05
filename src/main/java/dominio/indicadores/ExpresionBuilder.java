package dominio.indicadores;

public class ExpresionBuilder {
	
	private Expresion expresion;
	
	public Expresion build(){
		//Se podrían agregar validaciones
		return expresion;
	}
	
	public void agregarExpresion(Expresion expresion){
		this.expresion = expresion;
	}
	
	public void agregarOperacion(Expresion operandoDer, String operadorBinario){
		expresion = new ExpresionOperacion(expresion, operandoDer, operadorBinario);
	}
	
}
