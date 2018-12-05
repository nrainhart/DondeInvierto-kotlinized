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
	private List<CondicionTaxativa> condicionesTaxativas = new ArrayList<CondicionTaxativa>();
	
	@OneToMany(cascade = CascadeType.PERSIST)
	@JoinColumn(name="metodologia_id")
	private List<CondicionPrioritaria> condicionesPrioritarias = new ArrayList<CondicionPrioritaria>();
	
	private Metodologia(){} //Necesario para persistir la clase
	
	public Metodologia(String nombre){
		this.nombre = nombre;
	}
	
	public List<Empresa> evaluarPara(List<Empresa> empresas){
		List<Empresa> empresasSinDatosFaltantes = this.empresasSinDatosFaltantes(empresas);
		List<Empresa> empresasQueCumplenTaxativas = this.empresasQueCumplenTaxativas(empresasSinDatosFaltantes);
		empresasQueCumplenTaxativas.sort((emp1, emp2)-> this.puntaje(emp2, empresasQueCumplenTaxativas).compareTo(this.puntaje(emp1, empresasQueCumplenTaxativas)));//Sort modifica la misma lista
		return empresasQueCumplenTaxativas;
	}
	
	public List<Empresa> empresasQueNoCumplenTaxativas(List<Empresa> empresas){ //Devuelve sólo las que no cumplen (no las que faltan datos)
		List<Empresa> empresasSinDatosFaltantes = this.empresasSinDatosFaltantes(empresas);
		Stream<Empresa> empresasQueNoCumplenTaxativas = empresasSinDatosFaltantes.stream().filter(empresa -> !this.cumpleCondicionesTaxativas(empresa));
		return empresasQueNoCumplenTaxativas.collect(Collectors.toList());
	}
	
	public List<Empresa> empresasConDatosFaltantes(List<Empresa> empresas){	
		List<Condicion> condiciones = this.obtenerTodasLasCondiciones();
		return this.empresasConDatosInsuficientesParaLasCondiciones(empresas,condiciones);
	}
	
	private List<Empresa> empresasSinDatosFaltantes(List<Empresa> empresas){
		List<Empresa> empresasConDatosFaltantes = this.empresasConDatosFaltantes(empresas);
		Stream<Empresa> empresasSinDatosFaltantes = empresas.stream().filter(empresa -> !empresasConDatosFaltantes.contains(empresa));
		return empresasSinDatosFaltantes.collect(Collectors.toList());
	}
	
	private List<Empresa> empresasQueCumplenTaxativas(List<Empresa> empresas){
		List<Empresa> empresasQueNoCumplenTaxativas = this.empresasQueNoCumplenTaxativas(empresas);
		Stream<Empresa> empresasQueCumplenTaxativas = empresas.stream().filter(empresa -> !empresasQueNoCumplenTaxativas.contains(empresa));
		return empresasQueCumplenTaxativas.collect(Collectors.toList());
	}
	
	private List<Condicion> obtenerTodasLasCondiciones(){
		List<Condicion> condiciones = new ArrayList<Condicion>();
		condiciones.addAll(condicionesTaxativas);
		condiciones.addAll(condicionesPrioritarias);
		return condiciones;
	}
	
	private List<Empresa> empresasConDatosInsuficientesParaLasCondiciones(List<Empresa> empresas, List<Condicion> condiciones){
		HashSet<Empresa> empresasConDatosFaltantes = new HashSet<Empresa>();
		condiciones.forEach(cond -> empresasConDatosFaltantes.addAll(this.empresasConDatosFaltantesParaEstaCondicion(empresas,cond)));
		return new ArrayList<Empresa>(empresasConDatosFaltantes);
	}
	
	private List<Empresa> empresasConDatosFaltantesParaEstaCondicion(List<Empresa> empresas, Condicion condicion){
		return empresas.stream().filter(emp -> !condicion.getOperando().sePuedeEvaluarPara(emp)).collect(Collectors.toList());
	}
	
	private boolean cumpleCondicionesTaxativas(Empresa empr){
		return condicionesTaxativas.stream().allMatch(cond -> cond.laCumple(empr));
	}
	
	private Integer puntaje(Empresa empresa, List<Empresa> empresas){
		IntStream puntajesObtenidos = condicionesPrioritarias.stream().mapToInt(cond -> this.puntosObtenidosPara(empresa, cond, empresas));
		return puntajesObtenidos.sum();
	}
	
	private int puntosObtenidosPara(Empresa empresa, CondicionPrioritaria condicion, List<Empresa> empresas){
		List<Empresa> empresasQueSonPeores = this.empresasQueSonPeoresSegunCondicion(empresa,condicion,empresas);
		return empresasQueSonPeores.size();
	}
	
	private List<Empresa> empresasQueSonPeoresSegunCondicion(Empresa empresa, CondicionPrioritaria condicion, List<Empresa>empresas){
		Stream<Empresa> empresasQueSonPeores = empresas.stream().filter(otraEmpresa -> condicion.esMejorQue(empresa, otraEmpresa));
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