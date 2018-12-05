package server;

import spark.Spark;
import spark.debug.DebugScreen;

public class Server {

	public static void main(String[] args) {
		new Bootstrap().init();
		ProcessBuilder process = new ProcessBuilder();
		Integer port;
		if (process.environment().get("PORT") != null) {
			port = Integer.parseInt(process.environment().get("PORT"));
		} else {
			port = 4567;
		}
		Spark.port(port);
		//Spark.port(8080);
		DebugScreen.enableDebugScreen();
		Router.configure();
	}

}