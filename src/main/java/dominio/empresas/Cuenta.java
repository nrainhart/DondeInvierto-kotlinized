package dominio.empresas;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Year;
import java.util.Objects;

@Entity
@Table(name = "cuentas")
public class Cuenta {

	@Id
	@GeneratedValue
	private Long id;

	private Year anio;
	private String tipoCuenta;
	private int valor;

	private Cuenta() {} // Necesario para persistir la clase

	public Cuenta(Year anio, String tipoCuenta, int valor) {
		this.anio = anio;
		this.tipoCuenta = tipoCuenta;
		this.valor = valor;
	}
	
	public void actualizar(Cuenta cuentaConDatosNuevos, Empresa empresaQueContieneCuenta) {
		valor = cuentaConDatosNuevos.getValor();
	}

	public boolean esDeTipo(String tipo) {
		return this.tipoCuenta.equalsIgnoreCase(tipo);
	}

	public boolean esDeAnio(Year anio) {
		return this.anio.equals(anio);
	}

	public Year getAnio() {
		return anio;
	}

	public String getTipoCuenta() {
		return tipoCuenta;
	}

	public int getValor() {
		return valor;
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof Cuenta) && Objects.equals(anio, ((Cuenta) obj).getAnio())
				&& Objects.equals(tipoCuenta, ((Cuenta) obj).getTipoCuenta());
	}

	@Override
	public int hashCode() {
		return Objects.hash(anio, tipoCuenta);
	}

}
