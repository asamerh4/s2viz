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

	public static String search(String value, ArrayList<String> prodNames) {
	    for (String name : prodNames) {
	        if (name.contains(value)) {
	            return name;
	        }
	    }

	    return null;
	}
	public static BufferedImage getRGB(int utmzone, String latitudeband, String gridsquare, String date, channelMapTypes channelmap, ArrayList<String> targetProduct) {
		Properties conf = new ConfigProvider().getPropValues();
		try {
			logger.info(targetProduct.toString());

			String B01 = search("B01.tif", targetProduct);
			String B02 = search("B02.tif", targetProduct);
			String B03 = search("B03.tif", targetProduct);
			String B04 = search("B04.tif", targetProduct);
			String B05 = search("B05.tif", targetProduct);
			String B06 = search("B06.tif", targetProduct);
			String B07 = search("B07.tif", targetProduct);
			String B08 = search("B08.tif", targetProduct);
			String B08A = search("B08A.tif", targetProduct);
			String B09 = search("B09.tif", targetProduct);
			String B10 = search("B10.tif", targetProduct);
			String B11 = search("B11.tif", targetProduct);
			String B12 = search("B12.tif", targetProduct);
			String PVI = search("PVI.tif", targetProduct);
			String TCI = search("TCI.tif", targetProduct);
			
			//register GDAL Formats
			gdal.AllRegister();
			
			//prepare (open) rgb channels
			Dataset red = null;
			Dataset green = null;
			Dataset blue =null;
			String folder = conf.getProperty("granules_dir");

			targetProduct.contains("");
			//our 3 channelmaps
			switch (channelmap) {
			case visible: 
				logger.info("VISIBLE --> red: "+B04+" green: "+B03+" blue: "+B02 );
				red = gdal.Open(folder+"//"+B04);
				green = gdal.Open(folder+"//"+B03);
				blue = gdal.Open(folder+"//"+B02);
				break; 
			case vegetation: 
				logger.info("VEGETATION --> red: "+B05+" green: "+B06+" blue: "+B07 );
				red = gdal.Open(folder+"//"+B05);
				green = gdal.Open(folder+"//"+B06);
				blue = gdal.Open(folder+"//"+B07);
				break; 
			case waterVapor:
				logger.info("WATERVAPOR --> red: "+B01+" green: "+B10+" blue: "+B09 );
				red = gdal.Open(folder+"//"+B01);
				green = gdal.Open(folder+"//"+B10);
				blue = gdal.Open(folder+"//"+B09);
				break;
			case infrared:
				logger.info("INFRARED --> red: "+B08+" green: "+B04+" blue: "+B03 );
				red = gdal.Open(folder+"//"+B08);
				green = gdal.Open(folder+"//"+B04);
				blue = gdal.Open(folder+"//"+B03);
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
