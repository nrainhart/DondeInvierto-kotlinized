package dominio.metodologias;

import dominio.empresas.Empresa;

import javax.persistence.*;
import java.time.Year;

@Entity
@Table(name = "cuantificadores")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Cuantificador {

	@Id 
	@GeneratedValue
	protected Long id;
	
	public abstract int evaluarEn(Empresa empresa, Year anio);
	
}
