package dominio.usuarios;

import dominio.AbstractRepository;
import excepciones.NoExisteUsuarioError;

public class RepositorioUsuarios extends AbstractRepository<Usuario>{

	public Long obtenerId(String email, String password){
		Usuario usuarioBuscado = obtenerTodos().stream().filter(usuario -> usuario.validar(email, password)).findFirst()
				.orElseThrow(() -> new NoExisteUsuarioError("Usuario o contrasena incorrectos."));
		return usuarioBuscado.getId();
	}
	
	public void eliminarIndicadoresPrecalculados(){
		obtenerTodos().forEach(usuario -> usuario.eliminarIndicadoresPrecalculados());
	}
	
	@Override
	protected Class<Usuario> tipoEntidad() {
		return Usuario.class;
	}

	@Override
	protected String mensajeEntidadExistenteError(Usuario elemento) {
		return "Ya existe un usuario con el email " + elemento.getEmail();
	}

}