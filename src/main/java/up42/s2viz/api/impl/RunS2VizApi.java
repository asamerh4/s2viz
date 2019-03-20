package up42.s2viz.api.impl;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RunS2VizApi
{
	public static final Logger logger = LoggerFactory.getLogger(RunS2VizApi.class);
	
	public static void main(String[] args) {

		
		Server server = null;
		try {
			server = new Server(8585);
			// Handler for multiple web apps
			HandlerCollection handlers = new HandlerCollection();

			//Enable GZIP compression
			GzipHandler gzip = new GzipHandler();
			gzip.setIncludedMethods("GET","POST");
			gzip.setMinGzipSize(245);
			gzip.setIncludedMimeTypes("text/plain","text/css","text/html", "application/javascript", "application/json", "image/png", "image/jpeg");
			handlers.addHandler(gzip);

			WebAppContext webapp = new WebAppContext();

			//no registering of classes here! -> AppConfig.java is used
			webapp.setResourceBase("src/webapp/");
			webapp.setContextPath("/");

			//Disable file locking
			webapp.getInitParams().put("org.eclipse.jetty.servlet.Default.useFileMappedBuffer", "false");
			gzip.setHandler(webapp);
			handlers.addHandler(webapp);



			// Adding the handlers to the server
			server.setHandler(handlers);

			// Starting the Server
			server.start();
			logger.info("s2viz api started: goto `localhost:8585`");
			server.join();
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			try { if (server != null) server.destroy(); } catch(Exception e) { }
		}
	}
}