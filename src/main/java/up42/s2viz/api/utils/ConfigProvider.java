package up42.s2viz.api.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigProvider {
	public static final Logger logger = LoggerFactory.getLogger(ConfigProvider.class);
	Properties prop = new Properties();
	InputStream inputStream;

	public Properties getPropValues() {

		try {
			
			String propFileName = "app.properties";

			inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

			if (inputStream != null) {
				prop.load(inputStream);
			} else {
				throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
			}

		} catch (IOException e) {
			logger.error(e.getMessage());
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error(e.getMessage());
			}
		}
		return prop;
	}
}
