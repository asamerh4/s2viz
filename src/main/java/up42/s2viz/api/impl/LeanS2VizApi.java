package up42.s2viz.api.impl;

import java.util.Properties;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import up42.s2viz.api.utils.ConfigProvider;

public class LeanS2VizApi {
	public static final Logger logger = LoggerFactory.getLogger(LeanS2VizApi.class);

	public Boolean startLeanS2VizApi() {

		Server server = null;
		try {
			Properties conf = new ConfigProvider().getPropValues();
			server = new Server(Integer.parseInt(conf.getProperty("rest_api_port")));
			HandlerCollection handlers = new HandlerCollection();

			// Enable GZIP compression
			GzipHandler gzip = new GzipHandler();
			gzip.setIncludedMethods("GET", "POST");
			gzip.setMinGzipSize(245);
			gzip.setIncludedMimeTypes("text/plain", "text/css", "text/html",
					"application/javascript", "application/json", "image/png",
					"image/jpeg");
			handlers.addHandler(gzip);

			WebAppContext webapp = new WebAppContext();

			// no registering of classes here! -> AppConfig.java is used
			webapp.setResourceBase("src/webapp/");
			webapp.setContextPath("/");

			// Disable file locking
			webapp.getInitParams().put(
					"org.eclipse.jetty.servlet.Default.useFileMappedBuffer",
					"false");
			gzip.setHandler(webapp);
			handlers.addHandler(webapp);

			// Adding the handlers to the server
			server.setHandler(handlers);

			// Starting the Server
			server.start();
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		} finally {
			try {
				if (server != null)
					server.destroy();
			} catch (Exception e) {
			}
		}
	}
}