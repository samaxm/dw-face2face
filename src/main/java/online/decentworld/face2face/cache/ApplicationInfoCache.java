package online.decentworld.face2face.cache;

import online.decentworld.cache.redis.RedisTemplate;
import online.decentworld.cache.redis.ReturnResult;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

/**
 * Created by Sammax on 2016/9/21.
 */
@Component
public class ApplicationInfoCache extends RedisTemplate {

    private long MAX_ONLINE=0;
    private long MAX_IDLE_TIME=10*60*1000;
    public void markOnline(String dwID){
        ReturnResult result=cache((Jedis jedis) -> {
            jedis.zadd(WebCacheKey.ONLINE_NUM, System.currentTimeMillis(), dwID);
            return ReturnResult.result(jedis.zcount(WebCacheKey.ONLINE_NUM, 0, -1));
        });
        long size=(long)result.getResult();
        if(size>MAX_ONLINE){
            cache((Jedis jedis) -> {
                String max=jedis.get(WebCacheKey.MAX_ONLINE);
                if(size>Long.parseLong(max)){
                    jedis.set(WebCacheKey.MAX_ONLINE,String.valueOf(size));
                }else{
                    MAX_ONLINE=Long.parseLong(max);
                }
                return ReturnResult.SUCCESS;
            });
        }
    }


    public void checkOnline(){
        cache((Jedis jedis)->{
            jedis.zremrangeByScore(WebCacheKey.ONLINE_NUM,0,System.currentTimeMillis()-MAX_IDLE_TIME);
            return ReturnResult.SUCCESS;
        });
    }
}
