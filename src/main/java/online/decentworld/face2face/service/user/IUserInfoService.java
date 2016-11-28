package online.decentworld.face2face.service.user;

import online.decentworld.charge.service.TransferAccountType;
import online.decentworld.rdb.entity.BaseDisplayUserInfo;
import online.decentworld.rdb.entity.User;
import online.decentworld.rpc.dto.api.ResultBean;
import org.springframework.transaction.annotation.Transactional;

public interface IUserInfoService {
	/**
	 * 绑定用户手机号码
	 * @param dwID
	 * @param phoneNum
	 * @param code
	 * @return
	 */
	public ResultBean bindUserPhoneNum(String dwID,String phoneNum,String code);

	/**
	 * 获取用户信息
	 * @param dwID
	 * @return
	 */
	public BaseDisplayUserInfo getUserInfo(String dwID);
	/**
	 * 设置用户信息
	 * @param user
	 * @return
	 */
	public ResultBean updateUserInfo(User user);

	/**
	 * 绑定用户转账账户
	 * @param dwID
	 * @param account
	 * @return
	 */
	public ResultBean bindAccount(String dwID,TransferAccountType accountType,String account);

	/**
	 * 绑定极光推送ID
	 * @param dwID
	 * @param jpushID
	 */
	public ResultBean bindJPush(String dwID, String jpushID);



	public String getJPush(String dwID);

	/**
	 *
	 * @param dwID
	 * @param payPassword
	 * @return
	 */
	public void setUserPayPassword(String dwID,String payPassword);

	@Transactional
	public void setPassword(String phoneNum,String password) throws Exception;

	public ResultBean setWorth(String dwID, String paypassword, int worth);


}
