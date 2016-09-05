package online.decentworld.face2face.cache;

import static online.decentworld.face2face.cache.ReturnResult.result;
import online.decentworld.face2face.service.match.MatchUserInfo;
import static online.decentworld.face2face.cache.ReturnResult.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

import redis.clients.jedis.Jedis;

/**
 * 匹配队列
 * @author Sammax
 *
 */
@Component
public class MatchQueueCache {
	
	private static Logger logger=LoggerFactory.getLogger(MatchQueueCache.class);
	@Autowired
	private RedisTemplate template;
	
//	public String getMatchUser(MatchUserInfo userInfo,int index){
//		Jedis jedis=null;
//		try{
//			jedis=RedisClient.getJedis();
//			String info=jedis.lpop(CacheKey.MATCH_QUEUE_KEY(index));
//			if(info!=null){
//				return info;
//			}else{
//				//需要将用户加入等待队列
//				jedis.rpush(CacheKey.MATCH_QUEUE_KEY(index), JSON.toJSONString(userInfo));
//				return null;
//			}
//		}catch(Exception ex){
//			logger.warn("[GET_MATCH_USER] userInfo#"+userInfo+" index#"+index,ex);
//			return null;
//		}finally{
//			if(jedis!=null)
//				jedis.close();
//		}
//	}
	
	/**
	 * 获得匹配的用户ID，若无等待用户，则返回null，并将本ID加入等待队列
	 * @param dwID
	 * @param index
	 * @return
	 */
	public String getMatchUser(MatchUserInfo userInfo,int index){
		logger.debug("[GET_MATCH]");
		ReturnResult result=template.cache((Jedis jedis)->{
			jedis=RedisClient.getJedis();
			String info=jedis.lpop(CacheKey.MATCH_QUEUE_KEY(index));
			if(info!=null){
				return result(info);
			}else{
				//需要将用户加入等待队列
				jedis.rpush(CacheKey.MATCH_QUEUE_KEY(index), JSON.toJSONString(userInfo));
				return SUCCESS;
			}
		});
		return (String)result.getResult();
	}
	
	
	
}
