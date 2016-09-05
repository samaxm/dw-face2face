package online.decentworld.face2face.cache;

import static online.decentworld.face2face.cache.ReturnResult.SUCCESS;
import static online.decentworld.face2face.cache.ReturnResult.result;
import static online.decentworld.face2face.config.ConfigLoader.SecurityConfig.*;
import online.decentworld.face2face.common.PhoneCodeType;
import online.decentworld.face2face.common.TokenType;
import online.decentworld.face2face.exception.CachePhoneCodeFailed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;

/**
 * 驗證碼相關緩存
 * @author Sammax
 *
 */
@Component
public class SecurityCache {
	
	private static Logger logger=LoggerFactory.getLogger(SecurityCache.class);
	
	@Autowired
	private RedisTemplate template;
	
	/**
	 * 緩存用戶驗證碼
	 * @throws CachePhoneCodeFailed 
	 */
	public void cachePhoneCode(String phoneNum,String code,PhoneCodeType type) throws CachePhoneCodeFailed{
		ReturnResult result=template.cache((Jedis jedis)->{
			jedis=RedisClient.getJedis();
			jedis.setex(CacheKey.PHONECODE_KEY(phoneNum,type), REGISTER_CODE_EXPIRE, code);
			return SUCCESS;
		});
		if(!result.isSuccess()){
			throw new CachePhoneCodeFailed(phoneNum);
		}
	} 
	
	/**
	 * 獲取用戶驗證碼
	 */
	public String getPhoneCodeCache(String phoneNum,PhoneCodeType type){
		ReturnResult result=template.cache((Jedis jedis)->{
			jedis=RedisClient.getJedis();
			String key=CacheKey.PHONECODE_KEY(phoneNum,type);
			String code=jedis.get(key);
			return result(code);
		});
		return (String)result.getResult();
	} 
	
	
	/**
	 * 增加舉報次數
	 * @param reportedID
	 * @return
	 */
	public int incrReportNum(String reportedID){
		ReturnResult result=template.cache((Jedis jedis)->{
			jedis=RedisClient.getJedis();
			return result(jedis.hincrBy(CacheKey.REPORT_TABLE,reportedID,1).intValue());
		});
		if(result.isSuccess()){
			return (Integer)result.getResult();
		}else{
			logger.warn("[INCR_REPORT_FAILED] reportedID#"+reportedID);
			return -1;
		}
	}
	
	
	public void cacheToken(String dwID,TokenType type,String token){
		template.cache((Jedis jedis)->{
			jedis=RedisClient.getJedis();
			jedis.setex(CacheKey.TOKEN_KEY(dwID,type),TOKEN_EXPIRE,token);
			return SUCCESS;
		});
	}
	
	public String getToken(String dwID,TokenType type){
		ReturnResult result=template.cache((Jedis jedis)->{
			jedis=RedisClient.getJedis();
			String key=CacheKey.TOKEN_KEY(dwID, type);
			String code=jedis.get(key);
			return result(code);
		});
		return (String)result.getResult();
	}
	
}
