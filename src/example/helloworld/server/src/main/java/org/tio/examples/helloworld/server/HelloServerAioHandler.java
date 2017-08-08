package org.tio.examples.helloworld.server;

import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.core.intf.Packet;
import org.tio.examples.helloworld.common.HelloAbsAioHandler;
import org.tio.examples.helloworld.common.HelloPacket;
import org.tio.server.intf.ServerAioHandler;

/**
 * 
 * @author tanyaowu 
 *
 */
public class HelloServerAioHandler extends HelloAbsAioHandler implements ServerAioHandler {
	/** 
	 * 处理消息
	 */
	@Override
	public void handler(Packet packet, ChannelContext channelContext) throws Exception {
		HelloPacket helloPacket = (HelloPacket) packet;
		byte[] body = helloPacket.getBody();
		if (body != null) {
			String str = new String(body, HelloPacket.CHARSET);
			System.out.println("收到消息：" + str);

			HelloPacket resppacket = new HelloPacket();
			resppacket.setBody(("收到了你的消息，你的消息是:" + str).getBytes(HelloPacket.CHARSET));
			Aio.send(channelContext, resppacket);
		}
		return;
	}
}
