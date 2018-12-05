package dominio.empresas;

import excepciones.NoSePudoLeerEseTipoDeArchivoError;

import java.util.ArrayList;
import java.util.List;

public class LectorArchivos {
	private List<ArchivoEmpresas> lectores = new ArrayList<>();//Se inicializa en el constructor
	
	public LectorArchivos(String ruta){
		lectores.add(new ArchivoXLS(ruta));
		lectores.add(new ArchivoCSV(ruta));
	}
	
	public List<Empresa> getEmpresas(){
		ArchivoEmpresas archivo = obtenerLectorApropiado();
		archivo.leerEmpresas();
		return archivo.getEmpresas();
	}
	
	private ArchivoEmpresas obtenerLectorApropiado(){
		return lectores.stream().filter(lector -> lector.puedeLeerArchivo()).findFirst().orElseThrow(() -> new NoSePudoLeerEseTipoDeArchivoError("No se pudo leer el archivo."));
	}

}