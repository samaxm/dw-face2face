package online.decentworld.face2face.cache;

import online.decentworld.cache.redis.RedisTemplate;
import online.decentworld.cache.redis.ReturnResult;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Created by Sammax on 2016/12/26.
 */
@Component
public class UserInfoCache extends RedisTemplate {

    public Set<String> getUserTags(){
        ReturnResult result=cache(jedis->{
            Set<String> tags=jedis.smembers(WebCacheKey.USER_TAG);
            return ReturnResult.result(tags);
        });
        if(result.isSuccess()){
            return (Set<String>) result.getResult();
        }else{
            return null;
        }
    }
}
