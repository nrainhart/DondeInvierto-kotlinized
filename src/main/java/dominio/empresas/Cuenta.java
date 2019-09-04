package dominio.empresas;

	import java.time.Year;
	import java.util.Objects;

	public class Cuenta {

		private Year anio;
		private String tipoCuenta;
		private int valor;

		public Cuenta(Year anio, String tipoCuenta, int valor) {
			this.anio = anio;
			this.tipoCuenta = tipoCuenta;
			this.valor = valor;
		}

		public void actualizar(int valorActualizado) {
			this.valor = valorActualizado;
		}

		public boolean esDeTipoYAnio(String tipoCuenta, Year anio) {
			return esDeTipo(tipoCuenta) && esDeAnio(anio);
		}

		private boolean esDeTipo(String tipo) {
			return this.tipoCuenta.equalsIgnoreCase(tipo);
		}

		private boolean esDeAnio(Year anio) {
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

