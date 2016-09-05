package online.decentworld.face2face.cache;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;


@Component
public class SpringRedisCacheManager implements CacheManager{

	@Autowired
	private RedisTemplate template;
	
	@Override
	public Cache getCache(String name) {
		return new SpringRedisCache(name);
	}

	@Override
	public Collection<String> getCacheNames() {
		return null;
	}

}
