package dominio.empresas;

import dominio.indicadores.Indicador;
import excepciones.NoExisteCuentaError;

import javax.persistence.*;
import java.time.Year;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "empresas")
public class Empresa {
	
	@Id 
	@GeneratedValue
	private Long id;
	
	private String nombre;
	
	@OneToMany(cascade = CascadeType.PERSIST)
	@JoinColumn(name="empresa_id")
	private List<Cuenta> cuentas;//Hibernate requiere que las colecciones a persistir estén declarados como una interfaz (no una clase concreta).
	
	private Empresa(){} //Necesario para persistir la clase
	
	public Empresa(String nombre, List<Cuenta> cuentas){
		this.nombre = nombre;
		this.cuentas = cuentas;
	}
	
	public int cantidadDeCuentas(){
		return cuentas.size();
	}
	
	public boolean seLlama(String nombre){
		return this.nombre.equals(nombre);
	}
	
	public void registrarCuenta(Cuenta cuenta){
		cuentas.add(cuenta);
	}
	
	public void actualizar(Empresa empresaConDatosNuevos) {
		for(Cuenta cuenta: empresaConDatosNuevos.getCuentas()){
			if(!cuentas.contains(cuenta)) registrarCuenta(cuenta);
			else {
				Cuenta cuentaAActualizar = cuentas.get(cuentas.indexOf(cuenta));
				cuentaAActualizar.actualizar(cuenta, this);
			}
		}
	}
	
	public Set<Year> aniosDeLosQueTieneCuentas(){
		Set<Year> anios = new HashSet<Year>();
		cuentas.forEach(cuenta -> anios.add(cuenta.getAnio()));
		return anios;
	}
	
	public List<Cuenta> resultadosParaEstosIndicadores(List<Indicador> indicadores){
		Set<Year> anios = this.aniosDeLosQueTieneCuentas();
		List<Cuenta> resultadosTotales = new ArrayList<Cuenta>();
		anios.forEach(anio -> resultadosTotales.addAll(this.resultadosParaEstosIndicadoresSegunAnio(indicadores,anio)));
		return resultadosTotales;
	}
	
	private List<Cuenta> resultadosParaEstosIndicadoresSegunAnio(List<Indicador> indicadores, Year anio){
		List<Cuenta> resultados = new ArrayList<Cuenta>();
		List<Indicador> indicadoresAplicables = new ArrayList<Indicador>();
		indicadoresAplicables = indicadores.stream().filter(ind -> ind.esAplicableA(this, anio)).collect(Collectors.toList());
		indicadoresAplicables.forEach(ind -> resultados.add(new Cuenta(anio,ind.getNombre(),ind.evaluarEn(this, anio))));
		return resultados;
	}
	
	public int getAnioDeCreacion(){//El anio de creación se obtiene a partir de la cuenta más antigua
		return cuentas.stream().mapToInt(cuenta-> Integer.parseInt(cuenta.getAnio().toString())).min()
				.orElseThrow(() -> new NoExisteCuentaError("La empresa no tiene ninguna cuenta, por lo que no se puede calcular el año de creación."));
	}
	
	public Long getId() {
		return id;
	}
	
	public String getNombre(){
		return nombre;
	}

	public List<Cuenta> getCuentas() {
		return cuentas;
	}
	
	public int getValorCuenta(String tipoCuenta, Year anio){
		Cuenta cuentaBuscada = cuentas.stream().filter(cuenta -> cuenta.esDeTipo(tipoCuenta) && cuenta.esDeAnio(anio)).findFirst().orElseThrow(() -> new NoExisteCuentaError("No se pudo encontrar la cuenta " + tipoCuenta + " en el año " + anio + " para la empresa " + this.getNombre() + "."));
		//El findFirst podría enmascarar el caso erróneo en el que haya dos cuentas del mismo tipo con valores distintos en el mismo año
		return cuentaBuscada.getValor();
	}
	
	@Override
	public boolean equals(Object otroObjeto) {
	    return (otroObjeto instanceof Empresa) && this.seLlama(((Empresa) otroObjeto).getNombre());
	}
	
	@Override
	public int hashCode() {
		return nombre.hashCode();
	}
	
	@Override
	public String toString(){ //Es necesario para que el Selector muestre solo el nombre de la empresa
		return nombre;
	}
	
}