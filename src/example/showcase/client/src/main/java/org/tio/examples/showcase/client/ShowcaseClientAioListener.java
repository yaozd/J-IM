package org.tio.examples.showcase.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.client.intf.ClientAioListener;
import org.tio.core.ChannelContext;
import org.tio.core.intf.Packet;
import org.tio.examples.showcase.common.ShowcaseSessionContext;
import org.tio.json.Json;

/**
 * @author tanyaowu 
 * 2017年3月26日 下午8:20:51
 */
public class ShowcaseClientAioListener implements ClientAioListener
{
	private static Logger log = LoggerFactory.getLogger(ShowcaseClientAioListener.class);

	/**
	 * 
	 * @author: tanyaowu
	 */
	public ShowcaseClientAioListener()
	{
	}

	/**
	 * @param args
	 * @author: tanyaowu
	 */
	public static void main(String[] args)
	{

	}

	/** 
	 * @param channelContext
	 * @param isConnected
	 * @param isReconnect
	 * @throws Exception
	 * @author: tanyaowu
	 */
	@Override
	public void onAfterConnected(ChannelContext channelContext, boolean isConnected, boolean isReconnect) throws Exception
	{
		log.info("onAfterConnected channelContext:{}, isConnected:{}, isReconnect:{}", channelContext, isConnected, isReconnect);

		//连接后，需要把连接会话对象设置给channelContext
		channelContext.setAttribute(new ShowcaseSessionContext());

	}

	/** 
	 * @param channelContext
	 * @param packet
	 * @param isSentSuccess
	 * @throws Exception
	 * @author: tanyaowu
	 */
	@Override
	public void onAfterSent(ChannelContext channelContext, Packet packet, boolean isSentSuccess) throws Exception
	{
		log.info("onAfterSent channelContext:{}, packet:{}, isSentSuccess:{}", channelContext, Json.toJson(packet), isSentSuccess);
	}

	/** 
	 * @param channelContext
	 * @param packet
	 * @param packetSize
	 * @throws Exception
	 * @author: tanyaowu
	 */
	@Override
	public void onAfterReceived(ChannelContext channelContext, Packet packet, int packetSize) throws Exception
	{
		log.info("onAfterReceived channelContext:{}, packet:{}, packetSize:{}", channelContext, Json.toJson(packet), packetSize);
	}

	/** 
	 * @param channelContext
	 * @param throwable
	 * @param remark
	 * @param isRemove
	 * @throws Exception
	 * @author: tanyaowu
	 */
	@Override
	public void onAfterClose(ChannelContext channelContext, Throwable throwable, String remark, boolean isRemove) throws Exception
	{
		log.info("onAfterClose channelContext:{}, throwable:{}, remark:{}, isRemove:{}", channelContext, throwable, remark, isRemove);
	}

	@Override
	public void onBeforeClose(ChannelContext channelContext, Throwable throwable, String remark, boolean isRemove) {
	}

}
