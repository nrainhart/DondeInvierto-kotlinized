package dominio.empresas;

import excepciones.NoExisteCuentaError;

import java.time.Year;
import java.util.Comparator;
import java.util.List;

	@SuppressWarnings("Convert2MethodRef")

	public class Empresa {

		private String nombre;
		private List<Cuenta> cuentas;

		public Empresa(String nombre, List<Cuenta> cuentas){
			this.nombre = nombre;
			this.cuentas = cuentas;
		}

		public void registrarCuenta(Cuenta cuenta){
			cuentas.add(cuenta);
		}

		public Year anioDeCreacion(){
			return cuentas.stream()
				.map(cuenta-> cuenta.getAnio())
				.min(Comparator.naturalOrder())
				.orElseThrow(() -> new NoExisteCuentaError("La empresa no tiene ninguna cuenta, por lo que no se puede calcular el año de creación."));
		}

		public int getValorCuenta(String tipoCuenta, Year anio){
			return cuentas.stream()
				.filter(cuenta -> cuenta.esDeTipoYAnio(tipoCuenta, anio))
				.findFirst()
				.map(cuenta -> cuenta.getValor())
				.orElseThrow(() -> new NoExisteCuentaError("No se pudo encontrar la cuenta " + tipoCuenta + " en el año " + anio +
					" para la empresa " + this.getNombre() + "."));
		}

		public String getNombre(){
			return nombre;
		}

	}

