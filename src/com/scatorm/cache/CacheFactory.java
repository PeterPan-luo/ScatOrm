package com.scatorm.cache;
/**
 * 添加缓存，通过缓存获取对象
 * @author Administrator
 *
 */
public class CacheFactory {

	public void addCache(String name, Object object, long time){
		Cache cache = new Cache();
		cache.setName(name);
		cache.setObject(object);
		long startTime = System.currentTimeMillis();
		cache.setStartTime(startTime);
		cache.setEndTime(startTime + time);
		CacheMap.CACHEMAP.put(name, cache);	
	}
	
	public Object getFromCache(String name){
		Object object = null;
		Cache cache = CacheMap.CACHEMAP.get(name);
		if (cache != null) {
			object = cache.getObject();
		}
		return object;
	}
}
