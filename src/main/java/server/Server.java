package server;

import spark.Spark;
import spark.debug.DebugScreen;

public class Server {

	public static void main(String[] args) {
		new Bootstrap().init();
		Spark.port(4567);
		DebugScreen.enableDebugScreen();
		Router.configure();
	}

}