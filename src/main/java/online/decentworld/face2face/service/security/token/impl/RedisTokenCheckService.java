package online.decentworld.face2face.service.security.token.impl;

import online.decentworld.face2face.cache.SecurityCache;
import online.decentworld.face2face.common.TokenType;
import online.decentworld.face2face.service.security.token.ITokenCheckService;
import online.decentworld.rpc.dto.api.ObjectResultBean;
import online.decentworld.rpc.dto.api.ResultBean;
import online.decentworld.tools.IDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RedisTokenCheckService implements ITokenCheckService{

	@Autowired
	private SecurityCache tokenCache;


	@Override
	public ResultBean checkPhoneCodeAndCreateToken(String checkKey,TokenType checkType,String checkToken,String cacheKey,TokenType cacheType) {

		if(checkToken(checkKey,checkType,checkToken)){
			String token= IDUtil.randomToken();
			tokenCache.cacheToken(cacheKey, cacheType, token);
			return ObjectResultBean.SUCCESS(token);
		}else{
			return ObjectResultBean.FAIL("驗證碼錯誤");
		}
	}

	@Override
	public boolean checkToken(String key, TokenType type, String token) {
		String cacheToken=tokenCache.getToken(key, type);
		if(cacheToken!=null&&cacheToken.equals(token)){
			return true;
		}else{
			return false;	
		}
	}

	@Override
	public void cacheToken( String key, TokenType type,String token) {
		tokenCache.cacheToken(key,type,token);
	}

}
