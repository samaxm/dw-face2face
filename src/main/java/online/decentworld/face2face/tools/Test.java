package online.decentworld.face2face.tools;

import online.decentworld.cache.redis.RedisClient;
import redis.clients.jedis.Jedis;

/**
 * Created by Sammax on 2016/9/26.
 */
public class Test {
    public static void main(String[] args) throws ClassNotFoundException {
        Jedis jedis= RedisClient.getJedis();
        jedis.del("test");
    }

}
