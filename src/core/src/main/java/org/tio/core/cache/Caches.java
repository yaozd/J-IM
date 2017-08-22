package org.tio.core.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.utils.cache.ICache;
import org.tio.utils.cache.guava.GuavaCache;

/**
 * @author tanyaowu
 * 2017年8月16日 上午11:35:49
 */
public class Caches {
	private static Logger log = LoggerFactory.getLogger(Caches.class);

	static {
		init();
	}

	public static ICache getCache(CacheConfig cacheConfig) {
		String cacheName = cacheConfig.getCacheName();
		//		CacheType cacheType = cacheConfig.getCacheType();

		ICache ret = null;
		//		if (cacheType == CacheType.REDIS) {
		//			ret = RedisCache.getCache(cacheName);
		//		} else if (cacheType == CacheType.GUAVA_REDIS) {
		//			ret = GuavaRedisCache.getCache(cacheName);
		//		} else if (cacheType == CacheType.GUAVA) {
		ret = GuavaCache.getCache(cacheName);
		//		}
		if (ret == null) {
			log.error("cacheName[{}]还没注册", cacheName);
		}
		return ret;
	}

	private static void init() {
		CacheConfig[] values = CacheConfig.values();
		for (CacheConfig cacheConfig : values) {
			String cacheName = cacheConfig.getCacheName();
			Long timeToLiveSeconds = cacheConfig.getTimeToLiveSeconds();
			Long timeToIdleSeconds = cacheConfig.getTimeToIdleSeconds();
			//			CacheType cacheType = cacheConfig.getCacheType();

			//			if (cacheType == CacheType.REDIS) {
			//				RedisCache.register(RedisUtils.get(), cacheName, timeToLiveSeconds, timeToIdleSeconds);
			//			} else if (cacheType == CacheType.GUAVA_REDIS) {
			//				GuavaRedisCache.register(RedisUtils.get(), cacheName, timeToLiveSeconds, timeToIdleSeconds);
			//			} else if (cacheType == CacheType.GUAVA) {
			GuavaCache.register(cacheName, timeToLiveSeconds, timeToIdleSeconds);
			//			}
		}

	}

	/**
	 * @param args
	 * @author tanyaowu
	 */
	public static void main(String[] args) {

	}

	/**
	 *
	 * @author tanyaowu
	 */
	public Caches() {
	}
}
