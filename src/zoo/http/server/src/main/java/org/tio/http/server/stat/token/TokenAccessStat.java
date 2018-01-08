package org.tio.http.server.stat.token;

import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.tio.utils.SystemTimer;
import org.tio.utils.lock.MapWithLock;

import com.xiaoleilu.hutool.date.BetweenFormater;
import com.xiaoleilu.hutool.date.BetweenFormater.Level;

/**
 * token访问统计
 * @author tanyaowu 
 * 2017年10月27日 下午1:53:03
 */
public class TokenAccessStat implements Serializable {
	private static final long serialVersionUID = 5314797979230623121L;

	/**
	 * key:   path, 形如："/user/login"
	 * value: TokenPathAccessStat
	 */
	private MapWithLock<String, TokenPathAccessStat> tokenPathAccessStatMap = new MapWithLock<>(new HashMap<>());
	
	private Long durationType;
		
	/**
	 * 当前统计了多久，单位：毫秒
	 */
	private long duration;

	public long getDuration() {
		duration = SystemTimer.currentTimeMillis() - this.firstAccessTime;
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	/**
	 * token
	 */
	private String token;



	/**
	 * 第一次访问时间， 单位：毫秒
	 */
	private long firstAccessTime = SystemTimer.currentTimeMillis();

	/**
	 * 最近一次访问时间， 单位：毫秒
	 */
	private long lastAccessTime = SystemTimer.currentTimeMillis();

	/**
	 * 这个token访问的次数
	 */
	public final AtomicInteger count = new AtomicInteger();
	
	/**
	 * 这个token访问给服务器带来的时间消耗，单位：毫秒
	 */
	public final AtomicLong timeCost = new AtomicLong();
	
	/**
	 * 根据token获取TokenAccesspathStat，如果缓存中不存在，则创建
	 * @param tokenAccessStat
	 * @param path
	 * @return
	 * @author tanyaowu
	 */
	public TokenPathAccessStat get(String path) {
		return get(path, true);
	}
	
	/**
	 * 根据tokenAccessStat获取TokenAccesspathStat，如果缓存中不存在，则根据forceCreate的值决定是否创建
	 * @param tokenAccessStat
	 * @param path
	 * @param forceCreate
	 * @return
	 * @author tanyaowu
	 */
	public TokenPathAccessStat get(String path, boolean forceCreate) {
		if (path == null) {
			return null;
		}
		
		TokenPathAccessStat tokenPathAccessStat = tokenPathAccessStatMap.get(path);
		if (tokenPathAccessStat == null && forceCreate) {
			tokenPathAccessStat = tokenPathAccessStatMap.putIfAbsent(path, new TokenPathAccessStat(durationType, token, path));
		}
		
		return tokenPathAccessStat;
	}

	/**
	 * 
	 * @param durationType
	 * @param token
	 * @author tanyaowu
	 */
	public TokenAccessStat(Long durationType, String token) {
		this.durationType = durationType;
		this.token = token;
	}

	public MapWithLock<String, TokenPathAccessStat> getTokenPathAccessStatMap() {
		return tokenPathAccessStatMap;
	}

	public void setTokenPathAccessStatMap(MapWithLock<String, TokenPathAccessStat> tokenPathAccessStatMap) {
		this.tokenPathAccessStatMap = tokenPathAccessStatMap;
	}
	
	/**
	 * @return the duration
	 */
	public String getFormatedDuration() {
		duration = SystemTimer.currentTimeMillis() - this.firstAccessTime;
		BetweenFormater betweenFormater = new BetweenFormater(duration, Level.MILLSECOND);
		return betweenFormater.format();
	}
	
	public double getPerSecond() {
		int count = this.count.get();
		long duration = getDuration();
		double perSecond = (double)((double)count / (double)duration) * (double)1000;
		return perSecond;
	}

	public Long getDurationType() {
		return durationType;
	}

	public void setDurationType(Long durationType) {
		this.durationType = durationType;
	}

	public String getIp() {
		return token;
	}

	public void setIp(String token) {
		this.token = token;
	}

	public long getFirstAccessTime() {
		return firstAccessTime;
	}

	public void setFirstAccessTime(long firstAccessTime) {
		this.firstAccessTime = firstAccessTime;
	}

	public long getLastAccessTime() {
		return lastAccessTime;
	}

	public void setLastAccessTime(long lastAccessTime) {
		this.lastAccessTime = lastAccessTime;
	}
}