package org.tio.core.maintain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.utils.lock.MapWithLock;
import org.tio.utils.lock.ObjWithLock;
import org.tio.utils.lock.SetWithLock;

/**
 * 
 * @author tanyaowu 
 * 2017年10月19日 上午9:40:40
 */
public class Users {
	private static Logger log = LoggerFactory.getLogger(Users.class);

	/**
	 * key: userid
	 * value: ChannelContext
	 */
	private MapWithLock<String, SetWithLock<ChannelContext>> mapWithLock = new MapWithLock<>(new HashMap<String, SetWithLock<ChannelContext>>());

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
		Lock lock = mapWithLock.writeLock();
		lock.lock();
		try {
			Map<String, SetWithLock<ChannelContext>> map = mapWithLock.getObj();
			SetWithLock<ChannelContext> setWithLock = map.get(key);
			if (setWithLock == null) {
				setWithLock = new SetWithLock<>(new HashSet<ChannelContext>());
				map.put(key, setWithLock);
			}
			setWithLock.add(channelContext);
			channelContext.setUserid(userid);
		} catch (Throwable e) {
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
	public SetWithLock<ChannelContext> find(GroupContext groupContext, String userid) {
		if (groupContext.isShortConnection()) {
			return null;
		}

		if (StringUtils.isBlank(userid)) {
			return null;
		}
		String key = userid;
		Lock lock = mapWithLock.readLock();
		lock.lock();
		try {
			Map<String, SetWithLock<ChannelContext>> m = mapWithLock.getObj();
			return m.get(key);
		} catch (Throwable e) {
			throw e;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * @return the mapWithLock
	 */
	public ObjWithLock<Map<String, SetWithLock<ChannelContext>>> getMap() {
		return mapWithLock;
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

		String userid = channelContext.getUserid();
		if (StringUtils.isBlank(userid)) {
			log.info("{}, {}, 并没有绑定用户", groupContext.getName(), channelContext.toString());
			return;
		}

		Lock lock = mapWithLock.writeLock();
		lock.lock();
		try {
			Map<String, SetWithLock<ChannelContext>> m = mapWithLock.getObj();
			SetWithLock<ChannelContext> setWithLock = m.get(userid);
			if (setWithLock == null) {
				log.info("{}, {}, userid:{}, 没有找到对应的SetWithLock", groupContext.getName(), channelContext.toString(), userid);
				return;
			}
			channelContext.setUserid(null);
			setWithLock.remove(channelContext);
			
			
			if(setWithLock.getObj().size() == 0) {
				m.remove(userid);
			}
			
		} catch (Throwable e) {
			throw e;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 解除groupContext范围内所有ChannelContext的 userid绑定
	 *
	 * @param userid the userid
	 * @author tanyaowu
	 */
	public void unbind(GroupContext groupContext, String userid) {
		if (groupContext.isShortConnection()) {
			return;
		}

		if (StringUtils.isBlank(userid)) {
			return;
		}

		Lock lock = mapWithLock.writeLock();
		lock.lock();
		try {
			Map<String, SetWithLock<ChannelContext>> m = mapWithLock.getObj();
			SetWithLock<ChannelContext> setWithLock = m.get(userid);
			if (setWithLock == null) {
				return;
			}

			WriteLock writeLock = setWithLock.writeLock();
			writeLock.lock();
			try {
				Set<ChannelContext> set = setWithLock.getObj();
				if (set.size() > 0) {
					for (ChannelContext channelContext : set) {
						channelContext.setUserid(null);
					}
					set.clear();
				}
				
				m.remove(userid);
			} catch (Throwable e) {
				log.error(e.getMessage(), e);
			} finally {
				writeLock.unlock();
			}

		} catch (Throwable e) {
			throw e;
		} finally {
			lock.unlock();
		}
	}
}
