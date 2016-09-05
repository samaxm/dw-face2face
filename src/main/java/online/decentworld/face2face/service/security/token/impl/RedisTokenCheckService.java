package online.decentworld.face2face.service.security.token.impl;

import static online.decentworld.face2face.common.StatusCode.FAILED;
import static online.decentworld.face2face.common.StatusCode.SUCCESS;
import online.decentworld.face2face.cache.SecurityCache;
import online.decentworld.face2face.common.PhoneCodeType;
import online.decentworld.face2face.common.TokenType;
import online.decentworld.face2face.service.security.token.ITokenCheckService;
import online.decentworld.face2face.tools.IDUtil;
import online.decentworld.rpc.dto.api.ObjectResultBean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RedisTokenCheckService implements ITokenCheckService{

	@Autowired
	private SecurityCache tokenCache;
	
	@Override
	public boolean checkPhoneCode(String phoneNum,PhoneCodeType type,String code) {
		String cacheCode=tokenCache.getPhoneCodeCache(phoneNum, type);
		if(cacheCode!=null&&cacheCode.equals(code)){
			return true;
		}else{
			return false;	
		}
	}

	@Override
	public ObjectResultBean checkPhoneCodeAndCreateToken(String dwID,
			String phoneNum, String code, PhoneCodeType type) {
		String cacheCode=tokenCache.getPhoneCodeCache(phoneNum, type);
		ObjectResultBean bean=new ObjectResultBean();
		if(cacheCode!=null&&cacheCode.equals(code)){
			String token=IDUtil.randomToken();
			tokenCache.cacheToken(dwID, TokenType.CHANGEPWD, token);
			bean.setStatusCode(SUCCESS);
			bean.setData(token);
		}else{
			bean.setStatusCode(FAILED);
			bean.setMsg("驗證碼錯誤");
		}
		return bean;
	}

	@Override
	public boolean checkToken(String dwID, TokenType type, String token) {
		String cacheToken=tokenCache.getToken(dwID, type);
		if(cacheToken!=null&&cacheToken.equals(token)){
			return true;
		}else{
			return false;	
		}
	}

}
