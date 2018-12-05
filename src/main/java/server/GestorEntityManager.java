package server;

import org.uqbarproject.jpa.java8.extras.WithGlobalEntityManager;
import spark.Request;
import spark.Response;

public class GestorEntityManager implements WithGlobalEntityManager{
	public void cerrarEntityManager(Request req, Response res){
		entityManager().close();
	}
}
