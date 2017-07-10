/**
 * 
 */
package org.tio.http.server;

import org.tio.core.ChannelContext;
import org.tio.http.common.HttpPacket;
import org.tio.http.common.HttpSessionContext;
import org.tio.http.common.http.HttpRequestPacket;
import org.tio.http.common.http.HttpResponsePacket;
import org.tio.http.common.http.RequestLine;
import org.tio.http.server.handler.AbstractHttpServerAioHandler;

/**
 * 版本: [1.0]
 * 功能说明: 
 * 作者: WChao 创建时间: 2017年7月10日 下午4:23:11
 */
public class HttpServerAioHandler extends AbstractHttpServerAioHandler {

	public HttpServerAioHandler(HttpServerConfig httpServerConfig) {
		super(httpServerConfig);
	}
	
	@Override
	public HttpResponsePacket handler(HttpRequestPacket packet, RequestLine requestLine,
			ChannelContext<HttpSessionContext, HttpPacket, Object> channelContext) throws Exception {
		
		return null;
	}

}
