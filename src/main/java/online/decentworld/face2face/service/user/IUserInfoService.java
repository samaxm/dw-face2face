package online.decentworld.face2face.service.user;

import online.decentworld.rdb.entity.User;
import online.decentworld.rpc.dto.api.ObjectResultBean;
import online.decentworld.rpc.dto.api.ResultBean;

public interface IUserInfoService {
	public ResultBean bindUserPhoneNum(String dwID,String phoneNum,String code);

	public ObjectResultBean getUserInfo(String dwID);
	
	public ResultBean updateUserInfo(User user);
	

}
