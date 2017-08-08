package org.tio.http.common;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;

/**
 * 
 * @author tanyaowu 
 * 2017年8月4日 上午9:41:12
 */
public class HttpResponseEncoder
{
	private static Logger log = LoggerFactory.getLogger(HttpResponseEncoder.class);

	/**
	 * 
	 * 
	 * @author: tanyaowu
	 */
	public HttpResponseEncoder()
	{

	}

	public static final int MAX_HEADER_LENGTH = 20480;

	/**
	 * 
	 * @param httpResponse
	 * @param groupContext
	 * @param channelContext
	 * @return
	 * @author: tanyaowu
	 */
	public static ByteBuffer encode(HttpResponse httpResponse, GroupContext groupContext, ChannelContext channelContext)
	{
		int bodyLength = 0;
		byte[] body = httpResponse.getBody();
		if (body != null)
		{
			bodyLength = body.length;
		}

		StringBuilder sb = new StringBuilder(256);

		HttpResponseStatus httpResponseStatus = httpResponse.getStatus();
		//		httpResponseStatus.get
		sb.append("HTTP/1.1 ").append(httpResponseStatus.getStatus()).append(" ").append(httpResponseStatus.getDescription()).append("\r\n");

		Map<String, String> headers = httpResponse.getHeaders();
		if (headers != null && headers.size() > 0)
		{
			headers.put(HttpConst.ResponseHeaderKey.Content_Length, bodyLength + "");
			Set<Entry<String, String>> set = headers.entrySet();
			for (Entry<String, String> entry : set)
			{
				sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\r\n");
			}
		}
		
		//处理cookie
		List<Cookie> cookies = httpResponse.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				sb.append(HttpConst.ResponseHeaderKey.Set_Cookie).append(": ");
				sb.append(cookie.toString());
				sb.append("\r\n");
				if (log.isInfoEnabled()) {
					log.info("{}, 回应set-cookie:{}", channelContext, cookie.toString());
				}
			}
		}
		
		sb.append("\r\n");

		byte[] headerBytes = null;
		try
		{
			String headerString = sb.toString();
			httpResponse.setHeaderString(headerString);
			headerBytes = headerString.getBytes(httpResponse.getCharset());
		} catch (Exception e)
		{
			throw new RuntimeException(e);
		}

		ByteBuffer buffer = ByteBuffer.allocate(headerBytes.length + bodyLength);
		buffer.put(headerBytes);

		if (bodyLength > 0)
		{
			buffer.put(body);
		}
		return buffer;
	}

	/**
	 * 解析请求头的每一行
	 * @param line
	 * @return
	 *
	 * @author: tanyaowu
	 * 2017年2月23日 下午1:37:58
	 *
	 */
	public static KeyValue parseHeaderLine(String line)
	{
		KeyValue keyValue = new KeyValue();
		int p = line.indexOf(":");
		if (p == -1)
		{
			keyValue.setKey(line);
			return keyValue;
		}

		String name = line.substring(0, p).trim();
		String value = line.substring(p + 1).trim();

		keyValue.setKey(name);
		keyValue.setValue(value);

		return keyValue;
	}

	

	public static enum Step
	{
		firstline, header, body
	}

	/**
	 * @param args
	 *
	 * @author: tanyaowu
	 * 2017年2月22日 下午4:06:42
	 * 
	 */
	public static void main(String[] args)
	{

	}

}
