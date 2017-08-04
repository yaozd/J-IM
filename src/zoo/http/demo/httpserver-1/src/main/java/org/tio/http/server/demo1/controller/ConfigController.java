package org.tio.http.server.demo1.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.http.common.HttpPacket;
import org.tio.http.common.HttpSession;
import org.tio.http.common.http.HttpRequestPacket;
import org.tio.http.common.http.HttpResponsePacket;
import org.tio.http.server.HttpServerConfig;
import org.tio.http.server.annotation.RequestPath;
import org.tio.http.server.util.Resps;

import com.jfinal.kit.PropKit;

/**
 * @author tanyaowu 
 * 2017年6月29日 下午7:53:59
 */
@RequestPath(value = "/config")
public class ConfigController {
	private static Logger log = LoggerFactory.getLogger(ConfigController.class);

	/**
	 * 
	 * @author: tanyaowu
	 */
	public ConfigController() {
	}

	@RequestPath(value = "/update")
	public HttpResponsePacket json(HttpRequestPacket httpRequestPacket, HttpServerConfig httpServerConfig, ChannelContext<HttpSession, HttpPacket, Object> channelContext)
			throws Exception {
//		AppStarter.conf = ConfigFactory.load("app.conf");
		PropKit.useless("app.properties");
		PropKit.use("app.properties", "utf-8");
		HttpResponsePacket ret = Resps.json(httpRequestPacket, "更新成功", httpServerConfig.getCharset());
		return ret;
	}

	/**
	 * @param args
	 * @author: tanyaowu
	 */
	public static void main(String[] args) {

	}
}
