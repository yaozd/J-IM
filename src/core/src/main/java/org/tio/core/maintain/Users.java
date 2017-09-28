package org.tio.core.maintain;

import java.util.concurrent.locks.Lock;

import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.apache.commons.lang3.StringUtils;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.utils.lock.ObjWithLock;

/**
 * The Class Users.
 *
 * @param <Ext> the generic type
 * @param  the generic type
 * @param <R> the generic type
 */
public class Users {

	/**
	 * key: userid
	 * value: ChannelContext
	 */
	private ObjWithLock<DualHashBidiMap<String, ChannelContext>> map = new ObjWithLock<>(new DualHashBidiMap<String, ChannelContext>());

	/**
	 * 绑定userid.
	 *
	 * @param userid the userid
	 * @param channelContext the channel context
	 * @author tanyaowu
	 */
	public void bind(String userid, ChannelContext channelContext) {
		GroupContext groupContext = channelContext.getGroupContext();
		if (groupContext.isShortConnection()) {
			return;
		}

		if (StringUtils.isBlank(userid)) {
			return;
		}
		String key = userid;
		Lock lock = map.getLock().writeLock();
		DualHashBidiMap<String, ChannelContext> m = map.getObj();

		try {
			lock.lock();
			m.put(key, channelContext);
			channelContext.setUserid(userid);
		} catch (Exception e) {
			throw e;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Find.
	 *
	 * @param userid the userid
	 * @return the channel context
	 */
	public ChannelContext find(GroupContext groupContext, String userid) {
		if (groupContext.isShortConnection()) {
			return null;
		}

		if (StringUtils.isBlank(userid)) {
			return null;
		}
		String key = userid;
		Lock lock = map.getLock().readLock();
		DualHashBidiMap<String, ChannelContext> m = map.getObj();

		try {
			lock.lock();
			return m.get(key);
		} catch (Exception e) {
			throw e;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * @return the map
	 */
	public ObjWithLock<DualHashBidiMap<String, ChannelContext>> getMap() {
		return map;
	}

	/**
	 * 解除channelContext绑定的userid
	 *
	 * @param channelContext the channel context
	 */
	public void unbind(ChannelContext channelContext) {
		GroupContext groupContext = channelContext.getGroupContext();
		if (groupContext.isShortConnection()) {
			return;
		}

		Lock lock = map.getLock().writeLock();
		DualHashBidiMap<String, ChannelContext> m = map.getObj();
		try {
			lock.lock();
			m.removeValue(channelContext);
		} catch (Exception e) {
			throw e;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 解除userid的绑定。一般用于多地登录，踢掉前面登录的场景
	 *
	 * @param userid the userid
	 * @author tanyaowu
	 */
	public ChannelContext unbind(GroupContext groupContext, String userid) {
		if (groupContext.isShortConnection()) {
			return null;
		}

		if (StringUtils.isBlank(userid)) {
			return null;
		}

		Lock lock = map.getLock().writeLock();
		DualHashBidiMap<String, ChannelContext> m = map.getObj();
		try {
			lock.lock();
			return m.remove(userid);
		} catch (Exception e) {
			throw e;
		} finally {
			lock.unlock();
		}
	}
}
