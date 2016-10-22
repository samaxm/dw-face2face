package online.decentworld.face2face.service.user;

import online.decentworld.rdb.entity.BaseDisplayUserInfo;
import online.decentworld.rdb.entity.User;
import online.decentworld.rpc.dto.api.ResultBean;
public interface IUserInfoService {
	public ResultBean bindUserPhoneNum(String dwID,String phoneNum,String code);

	public BaseDisplayUserInfo getUserInfo(String dwID);

	public ResultBean updateUserInfo(User user);

	/**
	 * 设置支付密码前先通过登录密码获取令牌
	 * @param dwID
	 * @param password
	 * @return
	 */
	public ResultBean preSetUserPayPassword(String dwID,String password);
	/**
	 *
	 * @param dwID
	 * @param payPassword
	 * @return
	 */
	public ResultBean setUserPayPassword(String dwID,String payPassword,String token);


}
