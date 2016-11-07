package online.decentworld.face2face.service.security.token;

import online.decentworld.face2face.common.TokenType;
import online.decentworld.rpc.dto.api.ResultBean;

public interface ITokenCheckService {

	public boolean checkToken(String key,TokenType type,String token);

	public void cacheToken(String key, TokenType type,String token);

	public ResultBean checkPhoneCodeAndCreateToken(String checkKey,TokenType checkType,String checkToken,String cacheKey,TokenType cacheType);
}
