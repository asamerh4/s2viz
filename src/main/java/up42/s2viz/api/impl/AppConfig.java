package up42.s2viz.api.impl;

import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

public class AppConfig extends ResourceConfig
{
	public static final Logger logger = LoggerFactory.getLogger(AppConfig.class);

	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_PURPLE = "\u001B[35m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_WHITE = "\u001B[37m";

	
	public static String splash1() {
		String msg =
				"\n\n"
						+ "=============="+ "\n"
						+ "s2viz REST API"+ "\n"
						+ "=============="
						+ "\n";

		return msg;
	}
	public AppConfig()
	{

		register(MultiPartFeature.class);
		register(V1S2VizResourceImpl.class);
		
		logger.info(splash1());
	}

} // class AppConfig
