package online.decentworld.face2face.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import static online.decentworld.face2face.cache.CacheKey.*;
import redis.clients.jedis.Jedis;

@Component
public class WealthCache {

	private static Logger logger=LoggerFactory.getLogger(WealthCache.class);
	@Autowired
	private RedisTemplate template;
	
	public int getWealth(String dwID){
		ReturnResult result=template.cache((Jedis jedis)->{
			String value=jedis.hget(WEALTH,dwID);
			if(value==null){
				return ReturnResult.FAIL;
			}else{
				return ReturnResult.result(Integer.parseInt(value));
			}
		});
		if(result.isSuccess()){
			return (Integer)result.getResult();
		}else{
			logger.debug("[GET_WEALTH_FAILED] dwID#"+dwID);
			return -1;
		}
	}
	
}
