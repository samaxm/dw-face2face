package online.decentworld.face2face.cache;

import online.decentworld.cache.redis.RedisTemplate;
import online.decentworld.cache.redis.ReturnResult;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Sammax on 2016/12/26.
 */
@Component
public class UserInfoCache extends RedisTemplate {

    public  List<String> getUserTags(){
        ReturnResult result=cache(jedis->{
            List<String> tags=jedis.lrange(WebCacheKey.USER_TAG, 0, -1);
            return ReturnResult.result(tags);
        });
        if(result.isSuccess()){
            return ( List<String> ) result.getResult();
        }else{
            return null;
        }
    }

    public void setTags(List<String> list){
        cache(jedis->{
            jedis.del(WebCacheKey.USER_TAG);
            jedis.rpush(WebCacheKey.USER_TAG, list.toArray(new String[list.size()]));
            return ReturnResult.SUCCESS;
        });
    }
}
