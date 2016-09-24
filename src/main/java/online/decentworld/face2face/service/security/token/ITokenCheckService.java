package online.decentworld.face2face.service.security.token;

import online.decentworld.face2face.common.PhoneCodeType;
import online.decentworld.face2face.common.TokenType;
import online.decentworld.rpc.dto.api.ObjectResultBean;

public interface ITokenCheckService {
	
	public boolean checkPhoneCode(String phoneNum,PhoneCodeType type,String code);
	
	public boolean checkToken(String key,TokenType type,String token);
	
	public ObjectResultBean checkPhoneCodeAndCreateToken(String phoneNum,String code,PhoneCodeType type);
}
