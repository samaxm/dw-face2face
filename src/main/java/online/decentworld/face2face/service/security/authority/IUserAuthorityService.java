package online.decentworld.face2face.service.security.authority;

import online.decentworld.rpc.dto.api.ObjectResultBean;
import online.decentworld.rpc.dto.api.ResultBean;

public interface IUserAuthorityService {
	public boolean checkPassword(String dwID,String password);
	public ObjectResultBean getRSAKey();
	public ResultBean uploadKey(String dwID,String password,String key);
}
