package com.scatorm.cache;

import java.util.Map.Entry;

public class CacheThread implements Runnable{

	public void run() {
		// TODO Auto-generated method stub
		while(true){
			for(Entry<String, Cache> entry : CacheMap.CACHEMAP.entrySet()){
				Cache cache = entry.getValue();
				long now = System.currentTimeMillis();
				long endtime = cache.getEndTime();
				if (now >= endtime) {
					//³¬Ê±£¬´ÓmapÖÐÒÆ³ý
					CacheMap.CACHEMAP.remove(entry.getKey());
				}
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
