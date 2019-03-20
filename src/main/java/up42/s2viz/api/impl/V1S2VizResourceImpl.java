package up42.s2viz.api.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import up42.s2viz.api.model.generation_params;
import up42.s2viz.api.model.infoResponse;
import up42.s2viz.api.resource.V1S2vizResource;

public class V1S2VizResourceImpl implements V1S2vizResource{

	public static final Logger logger = LoggerFactory.getLogger(V1S2VizResourceImpl.class);
	@Override
	public PostV1S2vizGenerateImageResponse postV1S2vizGenerateImage(
			generation_params entity) throws Exception {
		// TODO Auto-generated method stub
		//PostV1S2vizGenerateImageResponse.withJpegCreated(entity)
		return null;
	}

	@Override
	public GetV1S2vizHelpResponse getV1S2vizHelp() throws Exception {
		try {
		infoResponse info = new infoResponse();
		info.setInfo("some text here...");
		logger.info("pong");
		return GetV1S2vizHelpResponse.withJsonOK(info);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}

}
