package online.decentworld.face2face.service.security.authority;

import online.decentworld.rpc.dto.api.ObjectResultBean;
import online.decentworld.rpc.dto.api.ResultBean;

public interface IUserAuthorityService {
	public boolean checkPassword(String dwID,String password);
	public boolean preSetPayPassowrd(String dwID, String phoneNum);
	public boolean checkPayPassword(String dwID,String payPassword);
	public ObjectResultBean getRSAKey();
	public ResultBean uploadKey(String dwID,String password,String key);
	public String getUserKey(String dwID);
	public boolean checkToken(String dwID,String token);
}
