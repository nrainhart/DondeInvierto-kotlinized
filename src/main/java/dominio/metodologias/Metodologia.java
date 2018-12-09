package dominio.metodologias;

import dominio.empresas.Empresa;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Entity
@Table(name = "metodologias")
public class Metodologia {
	@Id 
	@GeneratedValue
	private Long id;
	
	private String nombre = "";
	
	@OneToMany(cascade = CascadeType.PERSIST)
	@JoinColumn(name="metodologia_id")
	private List<CondicionTaxativa> condicionesTaxativas = new ArrayList<>();
	
	@OneToMany(cascade = CascadeType.PERSIST)
	@JoinColumn(name="metodologia_id")
	private List<CondicionPrioritaria> condicionesPrioritarias = new ArrayList<>();
	
	private Metodologia(){} //Necesario para persistir la clase
	
	public Metodologia(String nombre){
		this.nombre = nombre;
	}
	
	public List<Empresa> evaluarPara(List<Empresa> empresas, int anioActual){
		List<Empresa> empresasQueCumplenTaxativas = empresasQueCumplenTaxativas(empresasSinDatosFaltantes(empresas, anioActual), anioActual);
		return empresasQueCumplenTaxativas.stream()
				.sorted((emp1, emp2) -> puntaje(emp2, empresasQueCumplenTaxativas, anioActual).compareTo(
						puntaje(emp1, empresasQueCumplenTaxativas, anioActual)))
				.collect(Collectors.toList());
	}
	
	public List<Empresa> empresasQueNoCumplenTaxativas(List<Empresa> empresas, int anioActual){ //Devuelve sólo las que no cumplen (no las que faltan datos)
		Stream<Empresa> empresasQueNoCumplenTaxativas = empresasSinDatosFaltantes(empresas, anioActual).stream().filter(empresa -> !cumpleCondicionesTaxativas(empresa, anioActual));
		return empresasQueNoCumplenTaxativas.collect(Collectors.toList());
	}
	
	public List<Empresa> empresasConDatosFaltantes(List<Empresa> empresas, int anioActual){
		List<Condicion> condiciones = this.obtenerTodasLasCondiciones();
		return this.empresasConDatosInsuficientesParaLasCondiciones(empresas,condiciones, anioActual);
	}
	
	private List<Empresa> empresasSinDatosFaltantes(List<Empresa> empresas, int anioActual){
		Stream<Empresa> empresasSinDatosFaltantes = empresas.stream().filter(empresa -> !empresasConDatosFaltantes(empresas, anioActual).contains(empresa));
		return empresasSinDatosFaltantes.collect(Collectors.toList());
	}
	
	private List<Empresa> empresasQueCumplenTaxativas(List<Empresa> empresas, int anioActual){
		Stream<Empresa> empresasQueCumplenTaxativas = empresas.stream().filter(empresa -> !empresasQueNoCumplenTaxativas(empresas, anioActual).contains(empresa));
		return empresasQueCumplenTaxativas.collect(Collectors.toList());
	}
	
	private List<Condicion> obtenerTodasLasCondiciones(){
		List<Condicion> condiciones = new ArrayList<>();
		condiciones.addAll(condicionesTaxativas);
		condiciones.addAll(condicionesPrioritarias);
		return condiciones;
	}
	
	private List<Empresa> empresasConDatosInsuficientesParaLasCondiciones(List<Empresa> empresas, List<Condicion> condiciones, int anioActual){
		HashSet<Empresa> empresasConDatosFaltantes = new HashSet<>();
		condiciones.forEach(cond -> empresasConDatosFaltantes.addAll(empresasConDatosFaltantesParaEstaCondicion(empresas,cond, anioActual)));
		return new ArrayList<>(empresasConDatosFaltantes);
	}
	
	private List<Empresa> empresasConDatosFaltantesParaEstaCondicion(List<Empresa> empresas, Condicion condicion, int anioActual){
		return empresas.stream().filter(emp -> !condicion.getOperando().sePuedeEvaluarPara(emp, anioActual)).collect(Collectors.toList());
	}
	
	private boolean cumpleCondicionesTaxativas(Empresa empr, int anioActual){
		return condicionesTaxativas.stream().allMatch(cond -> cond.laCumple(empr, anioActual));
	}
	
	private Integer puntaje(Empresa empresa, List<Empresa> empresas, int anioActual){
		IntStream puntajesObtenidos = condicionesPrioritarias.stream().mapToInt(cond -> this.puntosObtenidosPara(empresa, cond, empresas, anioActual));
		return puntajesObtenidos.sum();
	}
	
	private int puntosObtenidosPara(Empresa empresa, CondicionPrioritaria condicion, List<Empresa> empresas, int anioActual){
		List<Empresa> empresasQueSonPeores = this.empresasQueSonPeoresSegunCondicion(empresa,condicion,empresas, anioActual);
		return empresasQueSonPeores.size();
	}
	
	private List<Empresa> empresasQueSonPeoresSegunCondicion(Empresa empresa, CondicionPrioritaria condicion, List<Empresa> empresas, int anioActual){
		Stream<Empresa> empresasQueSonPeores = empresas.stream().filter(otraEmpresa -> condicion.esMejorQue(empresa, otraEmpresa, anioActual));
		return empresasQueSonPeores.collect(Collectors.toList());
	}
	
	public void agregarCondicionTaxativa(CondicionTaxativa cond){
		condicionesTaxativas.add(cond);
	}
	
	public void agregarCondicionPrioritaria(CondicionPrioritaria cond){
		condicionesPrioritarias.add(cond);
	}
	
	public String getNombre() {
		return nombre;
	}
	
	public Long getId() {
		return id;
	}
	
	public boolean esMetodologiaValida(){
		return !nombre.isEmpty() && !condicionesTaxativas.isEmpty() && !condicionesPrioritarias.isEmpty();
	}
	
	public boolean equals(Object otroObjeto) {
	    return (otroObjeto instanceof Metodologia) && this.nombre.equals(((Metodologia) otroObjeto).getNombre());
	}
	
	public int hashCode() {
		return nombre.hashCode();
	}
	
	@Override
	public String toString(){
		return nombre;
	}
	
}