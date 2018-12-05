package dominio.empresas;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;

public abstract class ArchivoEmpresas{
	
	String ruta;
	List<Empresa> empresas;
	
	public ArchivoEmpresas(String ruta){
		this.ruta = ruta;
		empresas = new ArrayList<Empresa>();
	}
	
	public boolean puedeLeerArchivo(){
		String extension = "";
		int i = ruta.lastIndexOf('.');
		if (i > 0) {
		    extension = ruta.substring(i+1);
		}
		return extension.equals(this.extensionQueLee());
	}
	
	public void leerEmpresas(){
		this.abrirArchivo();
		this.leerRegistros();
		this.cerrarArchivo();
	}
	
	private void abrirArchivo(){
		this.prepararLector();
		empresas.clear();
	}
	
	public List<Empresa> getEmpresas() {
		return empresas;
	}
	
	protected Year obtenerAnio(String anio){
		return Year.parse(anio);
	}
	
	protected abstract void prepararLector();

	protected abstract String extensionQueLee();
	
	protected abstract void cerrarArchivo();
	
	protected abstract void leerRegistros();
}
