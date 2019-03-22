package up42.s2viz.api.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Vector;
import javax.imageio.ImageIO;
import org.gdal.gdal.gdal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import up42.s2viz.api.model.channelMapTypes;
import org.gdal.gdal.BuildVRTOptions;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.TranslateOptions;

public class PixelOps {

	public static final Logger logger = LoggerFactory.getLogger(PixelOps.class);

	public static BufferedImage getRGB(int utmzone, String latitudeband, String gridsquare, String date, channelMapTypes channelmap, ArrayList<String> targetProduct) {
		Properties conf = new ConfigProvider().getPropValues();
		try {
			logger.info(targetProduct.toString());

			//register GDAL Formats
			gdal.AllRegister();
			
			//prepare (open) rgb channels
			Dataset red = null;
			Dataset green = null;
			Dataset blue =null;
			String folder = conf.getProperty("granules_dir");

			//our 3 channelmaps
			switch (channelmap) {
			case visible: 
				logger.info("VISIBLE --> red: "+targetProduct.get(3)+" green: "+targetProduct.get(2)+" blue: "+targetProduct.get(1) );
				red = gdal.Open(folder+"//"+targetProduct.get(3));
				green = gdal.Open(folder+"//"+targetProduct.get(2));
				blue = gdal.Open(folder+"//"+targetProduct.get(1));
				break; 
			case vegetation: 
				logger.info("VEGETATION --> red: "+targetProduct.get(5)+" green: "+targetProduct.get(6)+" blue: "+targetProduct.get(7) );
				red = gdal.Open(folder+"//"+targetProduct.get(4));
				green = gdal.Open(folder+"//"+targetProduct.get(5));
				blue = gdal.Open(folder+"//"+targetProduct.get(6));
				break; 
			case waterVapor:
				logger.info("WATERVAPOR --> red: "+targetProduct.get(0)+" green: "+targetProduct.get(10)+" blue: "+targetProduct.get(8) );
				red = gdal.Open(folder+"//"+targetProduct.get(0));
				green = gdal.Open(folder+"//"+targetProduct.get(9));
				blue = gdal.Open(folder+"//"+targetProduct.get(8));
				break;
			case infrared:
				logger.info("INFRARED --> red: "+targetProduct.get(7)+" green: "+targetProduct.get(3)+" blue: "+targetProduct.get(2) );
				red = gdal.Open(folder+"//"+targetProduct.get(7));
				green = gdal.Open(folder+"//"+targetProduct.get(3));
				blue = gdal.Open(folder+"//"+targetProduct.get(2));
				break;
			}

			Vector<String> vec = new Vector<String>(6);

			vec.add("-of");
			vec.add("VRT");
			vec.add("-scale");
			vec.add("0 32000 0 255");
			vec.add("-ot");
			vec.add("Byte");
			TranslateOptions to = new TranslateOptions(vec);

			//create VRT's of each band
			final Dataset test_red = gdal.Translate("", red, to);
			final Dataset test_green = gdal.Translate("", green, to);
			final Dataset test_blue = gdal.Translate("", blue, to);

			//create Dataset-Array
			Dataset[] rgb = new Dataset[3];
			rgb[0]=test_red;
			rgb[1]=test_green;
			rgb[2]=test_blue;

			Vector<String> vec1 = new Vector<String>(4);

			//Use gdal_buildvrt to create rgb composite (using -separate switch)
			vec1.add("-separate");
			vec1.add("-overwrite");
			BuildVRTOptions vrtops = new BuildVRTOptions(vec1);
			final Dataset test = gdal.BuildVRT("", rgb, vrtops, null);

			//now translate VRT-composite to JPEG temp file
			//TODO: avoid writing files->use direct conversion to Bufferedimage
			Vector<String> vec2 = new Vector<String>(4);
			vec2.add("-of");
			vec2.add("JPEG");
			vec2.add("-co");
			vec2.add("QUALITY=100");

			TranslateOptions to1 = new TranslateOptions(vec2);
			long timest = System.currentTimeMillis();
			final Dataset out = gdal.Translate(conf.getProperty("work_dir")+"//"+timest+"temp.jpg", test, to1);
			out.delete();
			
			//Use ImageIO to transform into BufferedImage
			File jpegFile = new File(conf.getProperty("work_dir")+"//"+timest+"temp.jpg");
			BufferedImage image = ImageIO.read(jpegFile);
			jpegFile.delete();

			return image;

		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}
}
