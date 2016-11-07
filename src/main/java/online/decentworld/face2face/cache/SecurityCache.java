package online.decentworld.face2face.cache;

import online.decentworld.cache.redis.CacheKey;
import online.decentworld.cache.redis.RedisTemplate;
import online.decentworld.cache.redis.ReturnResult;
import online.decentworld.face2face.common.TokenType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import static online.decentworld.cache.redis.ReturnResult.SUCCESS;
import static online.decentworld.cache.redis.ReturnResult.result;
import static online.decentworld.face2face.config.ConfigLoader.SecurityConfig.TOKEN_EXPIRE;

/**
 * 驗證碼相關緩存
 * @author Sammax
 *
 */
@Component
public class SecurityCache extends RedisTemplate{
	
	private static Logger logger=LoggerFactory.getLogger(SecurityCache.class);
	

//	/**
//	 * 緩存用戶驗證碼
//	 * @throws CachePhoneCodeFailed
//	 */
//	public void cachePhoneCode(String phoneNum,String code,PhoneCodeType type) throws CachePhoneCodeFailed{
//		ReturnResult result=cache((Jedis jedis)->{
//			jedis.setex(WebCacheKey.PHONECODE_KEY(phoneNum,type), REGISTER_CODE_EXPIRE, code);
//			return SUCCESS;
//		});
//		if(!result.isSuccess()){
//			throw new CachePhoneCodeFailed(phoneNum);
//		}
//	}
//
//	/**
//	 * 獲取用戶驗證碼
//	 */
//	public String getPhoneCodeCache(String phoneNum,PhoneCodeType type){
//		ReturnResult result=cache((Jedis jedis)->{
//			String key=WebCacheKey.PHONECODE_KEY(phoneNum,type);
//			String code=jedis.get(key);
//			return result(code);
//		});
//		return (String)result.getResult();
//	}
	
	
	/**
	 * 增加舉報次數
	 * @param reportedID
	 * @return
	 */
	public int incrReportNum(String reportedID){
		ReturnResult result=cache((Jedis jedis)->{
			return result(jedis.hincrBy(CacheKey.REPORT_TABLE,reportedID,1).intValue());
		});
		if(result.isSuccess()){
			return (Integer)result.getResult();
		}else{
			logger.warn("[INCR_REPORT_FAILED] reportedID#"+reportedID);
			return -1;
		}
	}
	
	
	public void cacheToken(String key,TokenType type,String token){
		cache((Jedis jedis)->{
			jedis.setex(WebCacheKey.TOKEN_KEY(key,type),TOKEN_EXPIRE,token);
			return SUCCESS;
		});
	}
	
	public String getToken(String key,TokenType type){
		ReturnResult result=cache((Jedis jedis)->{
			String redisKey=WebCacheKey.TOKEN_KEY(key, type);
			String code=jedis.get(redisKey);
			return result(code);
		});
		return (String)result.getResult();
	}

	public void cacheAES(String dwID,String aes) throws Exception {
		ReturnResult result=cache((Jedis jedis)->{
			jedis.set(CacheKey.AESKey(dwID),aes);
			return ReturnResult.SUCCESS;
		});
		if(!result.isSuccess()){
			throw new Exception();
		}
	}

	public String getAES(String dwID){
		ReturnResult result=cache((Jedis jedis)->{
			String key=jedis.get(CacheKey.AESKey(dwID));
			return ReturnResult.result(key);
		});
		if(result.isSuccess()){
			return (String)result.getResult();
		}else{
			return null;
		}
	}
}
