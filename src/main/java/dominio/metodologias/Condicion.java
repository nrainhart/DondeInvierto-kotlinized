package dominio.metodologias;

import javax.persistence.*;

@MappedSuperclass
public abstract class Condicion {
	@Id 
	@GeneratedValue
	private Long id;
	
	@OneToOne(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
	protected OperandoCondicion operando;
	@Enumerated
	protected OperacionRelacional operacionRelacional;
	
	public OperandoCondicion getOperando() {
		return operando;
	}
}
