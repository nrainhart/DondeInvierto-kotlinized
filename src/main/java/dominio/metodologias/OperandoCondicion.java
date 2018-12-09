package dominio.metodologias;

import dominio.empresas.Empresa;
import excepciones.AntiguedadMenorACeroError;
import excepciones.NoExisteCuentaError;

import javax.persistence.*;
import java.time.Year;
import java.util.stream.IntStream;

@Entity
@Table(name = "operandos_condicion")
public class OperandoCondicion {
	
	@Id 
	@GeneratedValue
	private Long id;
	
	@Enumerated
	private OperacionAgregacion operacionAgregacion;
	@ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
	private Cuantificador indicadorOAntiguedad;
	private int aniosAEvaluar;
	
	private OperandoCondicion() {} //Necesario para persistir la clase
	
	public OperandoCondicion(OperacionAgregacion operacionAgregacion, Cuantificador indicadorOAntiguedad, int aniosAEvaluar) {
		this.operacionAgregacion = operacionAgregacion;
		this.indicadorOAntiguedad = indicadorOAntiguedad;
		this.aniosAEvaluar = aniosAEvaluar - 1; //Si aniosAEvaluar es 1, el intervalo tiene que ser (X,X), no (X-1,X)
		if (aniosAEvaluar < 0){
			throw new AniosAEvaluarMenorAUnoError("La condición se tiene que evaluar al menos en un año");
		}
	}
	
	public boolean sePuedeEvaluarPara(Empresa empresa, int anioActual){
		try{
			this.valorPara(empresa, anioActual);
			return true;
		} catch (NoExisteCuentaError | AntiguedadMenorACeroError e) {
			return false;
		}
	}
	
	public int valorPara(Empresa empresa, int anioActual){
		IntStream periodoAEvaluar = IntStream.rangeClosed(anioActual - aniosAEvaluar, anioActual);
		IntStream indicesEnPeriodo = periodoAEvaluar.map(anio -> indicadorOAntiguedad.evaluarEn(empresa, Year.of(anio)));
		return operacionAgregacion.aplicarA(indicesEnPeriodo);
	}
}

class AniosAEvaluarMenorAUnoError extends RuntimeException{AniosAEvaluarMenorAUnoError(String e){super(e);}}