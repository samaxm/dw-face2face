package online.decentworld.face2face.cache;

import com.alibaba.fastjson.JSON;
import online.decentworld.cache.redis.RedisTemplate;
import online.decentworld.cache.redis.ReturnResult;
import online.decentworld.face2face.service.match.MatchUserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import static online.decentworld.cache.redis.ReturnResult.result;

;

/**
 * 匹配队列
 * @author Sammax
 *
 */
@Component
public class MatchQueueCache extends RedisTemplate {
	private static int EXPIRE=5*60;
	private static Logger logger=LoggerFactory.getLogger(MatchQueueCache.class);
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
	 * @param index
	 * @return
	 */
	public String getMatchUser(MatchUserInfo userInfo,int index){

		String myinfo=JSON.toJSONString(userInfo);
		ReturnResult result=cache((Jedis jedis)->{
			jedis.srem(WebCacheKey.MATCH_SET,userInfo.getDwID());
			String matchID=jedis.spop(WebCacheKey.MATCH_SET);

			if(matchID!=null){
				String info=jedis.get(WebCacheKey.MATCH_INFO(matchID));
				logger.debug("[GET_MATCH] dwID#"+userInfo.getDwID()+" index#"+index+" matchID#"+matchID+" matchInfo#"+info);
				if(info!=null){
					return result(info);
				}else{
					//需要将用户加入等待队列
					jedis.setex(WebCacheKey.MATCH_INFO(userInfo.getDwID()),EXPIRE,myinfo);
					jedis.sadd(WebCacheKey.MATCH_SET, userInfo.getDwID());
					return ReturnResult.SUCCESS;
				}
			}else{
				//需要将用户加入等待队列
				jedis.setex(WebCacheKey.MATCH_INFO(userInfo.getDwID()),EXPIRE,myinfo);
				jedis.sadd(WebCacheKey.MATCH_SET, userInfo.getDwID());
				return ReturnResult.SUCCESS;
			}
		});
		return (String)result.getResult();
	}


	public String getVIPMatchUser(MatchUserInfo userInfo){
		logger.debug("[GET_MATCH] dwID#"+userInfo.getDwID());
		String myinfo=JSON.toJSONString(userInfo);
		ReturnResult result=cache((Jedis jedis)->{
			jedis.srem(WebCacheKey.VIP_MATCH_SET,myinfo);
			String info=jedis.spop(WebCacheKey.VIP_MATCH_SET);
//			String info=jedis.lpop(WebCacheKey.MATCH_QUEUE_KEY(index));
			if(info!=null){
				return result(info);
			}else{
				//需要将用户加入等待队列
//				jedis.rpush(WebCacheKey.MATCH_QUEUE_KEY(index), JSON.toJSONString(userInfo));
				jedis.sadd(WebCacheKey.VIP_MATCH_SET,myinfo);
				return ReturnResult.SUCCESS;
			}
		});
		return (String)result.getResult();
	}

	public void removeVIPMatchUser(MatchUserInfo userInfo){
		logger.debug("[REMOVE_MATCH] dwID#"+userInfo.getDwID());
		cache((Jedis jedis)->{
			jedis.srem(WebCacheKey.VIP_MATCH_SET,JSON.toJSONString(userInfo));
			return ReturnResult.SUCCESS;
		});
	}


	public void removeMatchUser(MatchUserInfo userInfo){
		logger.debug("[REMOVE_MATCH] dwID#"+userInfo.getDwID());
		cache((Jedis jedis)->{
			jedis.srem(WebCacheKey.MATCH_SET,userInfo.getDwID());
			return ReturnResult.SUCCESS;
		});
	}


}
