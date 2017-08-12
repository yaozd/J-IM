package org.tio.core.udp;

import org.tio.core.Node;
import org.tio.core.utils.SystemTimer;

/**
 * @author tanyaowu 
 * 2017年7月5日 下午4:46:24
 */
public class UdpPacket {

	/**
	 * 
	 * @author: tanyaowu
	 */
	public UdpPacket() {
	}

	public UdpPacket(byte[] data, Node remote) {
		super();
		this.data = data;
		this.remote = remote;
	}

	/**
	 * 
	 */
	private byte[] data;

	/**
	 * 对端Node
	 */
	private Node remote;

	/**
	 * 收到消息的时间
	 */
	private long time = SystemTimer.currentTimeMillis();

	/**
	 * @param args
	 * @author: tanyaowu
	 */
	public static void main(String[] args) {

	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public Node getRemote() {
		return remote;
	}

	public void setRemote(Node remote) {
		this.remote = remote;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}
}
