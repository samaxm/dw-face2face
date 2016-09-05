package online.decentworld.face2face.cache;

import io.codis.jodis.JedisResourcePool;
import io.codis.jodis.RoundRobinJedisPool;

import java.util.ArrayList;
import java.util.List;

import static online.decentworld.face2face.config.ConfigLoader.CodisConfig.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;



public class RedisClient {
	
	
	private static JedisResourcePool jedisPool;
//    private static JedisPool jedisPool;//非切片连接池
    private static ShardedJedisPool shardedJedisPool;//切片连接池
    static{
    	initialPool(); 
        initialShardedPool();  
    }

    public static Jedis getJedis(){
    	return jedisPool.getResource();
    }
    
    public static ShardedJedis getShardedJedis(){
    	ShardedJedis shardedJedis=shardedJedisPool.getResource();
    	return shardedJedis;
    }
    
  
    /**
     * 初始化非切片池
     */
    private static void initialPool() 
    { 
    	
        JedisPoolConfig config = new JedisPoolConfig(); 
        config.setMaxTotal(-1); 
        config.setMaxIdle(100); 
        config.setMaxWaitMillis(5000); 
        config.setTestOnBorrow(true); 
    	jedisPool=RoundRobinJedisPool.create().curatorClient(CODIS_ZK_CONNECTSTR, 30000).zkProxyDir(CODIS_PROXY_NAMESPACE).poolConfig(config).build();        
    }
            
    /** 
     * 初始化切片池 
     */ 
    private static void initialShardedPool() 
    { 
        // 池基本配置 
        JedisPoolConfig config = new JedisPoolConfig(); 
        config.setMaxTotal(20); 
        config.setMaxIdle(5); 
        config.setMaxWaitMillis(1000l); 
        config.setTestOnBorrow(false); 
        // slave链接 
        List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>(); 
        shards.add(new JedisShardInfo("112.74.13.117", 6379, "master")); 

        // 构造池 
        shardedJedisPool = new ShardedJedisPool(config, shards); 
    } 

    
    public static void main(String[] args) {
    	Jedis jedis=getJedis();
    	System.out.println(jedis.get("aaa"));
    	jedis.set("bbb","test");
    	System.out.println(jedis.get("bbb"));
    	
    }
}