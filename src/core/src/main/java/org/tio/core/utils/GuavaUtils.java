package org.tio.core.utils;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

/**
 * 
 * @author tanyaowu 
 * 2017年8月4日 下午5:42:37
 */
public class GuavaUtils
{
	private static Logger log = LoggerFactory.getLogger(GuavaUtils.class);

	/**
	 * 
	 */
	public GuavaUtils()
	{

	}

	/**
	 * 
	 * @param concurrencyLevel
	 * @param expireAfterWrite 设置写缓存后过期时间（单位：秒）
	 * @param expireAfterAccess 设置读缓存后过期时间（单位：秒）
	 * @param initialCapacity
	 * @param maximumSize
	 * @param recordStats
	 * @param removalListener
	 * @return
	 */
	public static <K, V> LoadingCache<K, V> createLoadingCache(Integer concurrencyLevel, Long expireAfterWrite,
			Long expireAfterAccess, Integer initialCapacity, Integer maximumSize, boolean recordStats,
			RemovalListener<K, V> removalListener)
	{

		if (removalListener == null)
		{
			removalListener = new RemovalListener<K, V>()
			{
				@Override
				public void onRemoval(RemovalNotification<K, V> notification)
				{
					log.info(notification.getKey() + " was removed");
				}
			};
		}

		CacheBuilder<K, V> cacheBuilder = (CacheBuilder<K, V>) CacheBuilder.newBuilder().removalListener(
				removalListener);

		//设置并发级别为8，并发级别是指可以同时写缓存的线程数
		cacheBuilder.concurrencyLevel(concurrencyLevel);
		if (expireAfterWrite != null && expireAfterWrite > 0)
		{
			//设置写缓存后8秒钟过期
			cacheBuilder.expireAfterWrite(expireAfterWrite, TimeUnit.SECONDS);
		}
		if (expireAfterAccess != null && expireAfterAccess > 0)
		{
			//设置访问缓存后8秒钟过期
			cacheBuilder.expireAfterAccess(expireAfterAccess, TimeUnit.SECONDS);
		}

		//设置缓存容器的初始容量为10
		cacheBuilder.initialCapacity(initialCapacity);
		//设置缓存最大容量为100，超过100之后就会按照LRU最近最少使用算法来移除缓存项
		cacheBuilder.maximumSize(maximumSize);

		if (recordStats)
		{
			//设置要统计缓存的命中率
			cacheBuilder.recordStats();
		}
		//build方法中可以指定CacheLoader，在缓存不存在时通过CacheLoader的实现自动加载缓存
		LoadingCache<K, V> loadingCache = cacheBuilder.build(new CacheLoader<K, V>()
		{
			@Override
			public V load(K key) throws Exception
			{
				return null;
			}
		});
		return loadingCache;

		//		for (int i = 0; i < 20; i++)
		//		{
		//			//从缓存中得到数据，由于我们没有设置过缓存，所以需要通过CacheLoader加载缓存数据
		//			Long student = studentCache.get("p");
		//			System.out.println(student);
		//			//休眠1秒
		//			TimeUnit.SECONDS.sleep(1);
		//		}

		//		System.out.println("cache stats:");
		//最后打印缓存的命中率等 情况
		//		System.out.println(studentCache.stats().toString());
	}

	/**
	 * 
	 * @param concurrencyLevel
	 * @param expireAfterWrite 设置写缓存后过期时间（单位：秒）
	 * @param expireAfterAccess 设置读缓存后过期时间（单位：秒）
	 * @param initialCapacity
	 * @param maximumSize
	 * @param recordStats
	 * @return
	 */
	public static <K, V> LoadingCache<K, V> createLoadingCache(Integer concurrencyLevel, Long expireAfterWrite,
			Long expireAfterAccess, Integer initialCapacity, Integer maximumSize, boolean recordStats)
	{
		return createLoadingCache(concurrencyLevel, expireAfterWrite, expireAfterAccess, initialCapacity, maximumSize,
				recordStats, null);
	}

	public static void main(String[] args) throws Exception
	{
		Integer concurrencyLevel = 8;
		Long expireAfterWrite = 1L;
		Long expireAfterAccess = null;
		Integer initialCapacity = 10;
		Integer maximumSize = 1000;
		boolean recordStats = false;
		LoadingCache<String, Object> loadingCache = GuavaUtils.createLoadingCache(concurrencyLevel, expireAfterWrite,
				expireAfterAccess, initialCapacity, maximumSize, recordStats);

		loadingCache.put("1", "1");
		Object o = loadingCache.get("1");
		//		loadingCache.invalidate("1");

		TimeUnit.SECONDS.sleep(3);
		loadingCache.put("2", "2");
		o = loadingCache.getIfPresent("1");
		System.out.println(o);
		o = loadingCache.getIfPresent("2");
		System.out.println(o);
	}
}
