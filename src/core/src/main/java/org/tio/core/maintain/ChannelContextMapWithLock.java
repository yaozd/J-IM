package org.tio.core.maintain;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import org.tio.core.MapWithLock;
import org.tio.core.intf.Packet;

/**
 * @author tanyaowu
 */
public class ChannelContextMapWithLock {

	/** remoteAndChannelContext key: "ip:port" value: ChannelContext. */
	private MapWithLock<Integer, Packet> map = new MapWithLock<Integer, Packet>(new HashMap<Integer, Packet>());

	/**
	 * Adds the.
	 *
	 * @param channelContext the channel context
	 */
	public void put(Integer synSeq, Packet packet) {
		Lock lock = map.getLock().writeLock();
		try {
			lock.lock();
			Map<Integer, Packet> m = map.getObj();
			m.put(synSeq, packet);
		} catch (Exception e) {
			throw e;
		} finally {
			lock.unlock();
		}
	}

	public Packet remove(Integer synSeq) {
		Lock lock = map.getLock().writeLock();
		try {
			lock.lock();
			Map<Integer, Packet> m = map.getObj();
			Packet packet = m.remove(synSeq);
			return packet;
		} catch (Exception e) {
			throw e;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Gets the map.
	 *
	 * @return the map
	 */
	public MapWithLock<Integer, Packet> getMap() {
		return map;
	}

}
