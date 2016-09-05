package online.decentworld.face2face.cache;

import redis.clients.jedis.Jedis;

public interface RedisOperation {

	
	public ReturnResult execute(Jedis jedis);
}
