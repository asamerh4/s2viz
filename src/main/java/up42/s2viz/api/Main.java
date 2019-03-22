package up42.s2viz.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import up42.s2viz.api.impl.LeanS2VizApi;


public class Main {
	public static final Logger logger = LoggerFactory.getLogger(Main.class);
	public static void main(String[] args) {
		
		/**
		 * OPERATOR REST API
		 * =================
		 * Started side by side with scheduler. API-Doc in resources/raml
		 */
		Boolean api = new LeanS2VizApi().startLeanS2VizApi();
		if (api) logger.info("Successfully started s2viz REST API"); 
		
	}

}
