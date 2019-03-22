package up42.s2viz.api.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Properties;
import javax.imageio.ImageIO;
import javax.ws.rs.core.StreamingOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import up42.s2viz.api.model.emptyResponse;
import up42.s2viz.api.model.errorResponse;
import up42.s2viz.api.model.generation_params;
import up42.s2viz.api.model.infoResponse;
import up42.s2viz.api.resource.V1S2vizResource;
import up42.s2viz.api.utils.ConfigProvider;
import up42.s2viz.api.utils.PixelOps;
import up42.s2viz.api.utils.S2Files;

public class V1S2VizResourceImpl implements V1S2vizResource{

	public static final Logger logger = LoggerFactory.getLogger(V1S2VizResourceImpl.class);
	@Override
	public PostV1S2vizGenerateImageResponse postV1S2vizGenerateImage(generation_params entity) throws Exception {
		Properties conf = new ConfigProvider().getPropValues();
		try {
			ArrayList<String> targetProduct = S2Files.listFilesForFolder(conf.getProperty("granules_dir"), entity.getUtmZone(), entity.getLatitudeBand(), entity.getGridSquare(), entity.getDate());
			if (targetProduct.size()>0) {
				BufferedImage image = PixelOps.getRGB(entity.getUtmZone(), entity.getLatitudeBand(), entity.getGridSquare(), entity.getDate(), entity.getChannelMap(), targetProduct);

				return PostV1S2vizGenerateImageResponse.withJpegCreated(new StreamingOutput() {
					@Override
					public void write(final OutputStream output) {
						try {
						ImageIO.write(image, "jpg", output);
						image.flush();
						output.close();
						} catch (IOException e) {
							logger.error(e.getMessage());
						}
					}
				});

			} else {
				emptyResponse empty = new emptyResponse();
				empty.setDescr("I'm sorry - there is no S2-Product matching: zone:"+entity.getUtmZone()+" latband:"+entity.getLatitudeBand()+" square:"+entity.getGridSquare()+" date:"+entity.getDate() );
				empty.setMsg("No S2-Product found");
				return PostV1S2vizGenerateImageResponse.withJsonNotFound(empty);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			errorResponse err = new errorResponse();
			err.setMsg("500 Internal Error --> Something went nuts...");
			return PostV1S2vizGenerateImageResponse.withJsonInternalServerError(err);
		}
	}

	@Override
	public PostV1S2vizHelpResponse postV1S2vizHelp() {
		try {
			infoResponse info = new infoResponse();
			info.setInfo("If images exist for the date and grid reference, then a standard JPEG image with " + 
					"the mime-type image/jpeg should be returned. " + 
					"When there are no images available, then a 404 with an informative error message " + 
					"as a JSON payload should be returned.");
			logger.info("pong");
			return PostV1S2vizHelpResponse.withJsonOK(info);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}

}
