package online.decentworld.face2face.service.user.impl;

import com.alibaba.fastjson.JSON;
import online.decentworld.face2face.common.PhoneCodeType;
import online.decentworld.face2face.common.StatusCode;
import online.decentworld.face2face.common.TokenType;
import online.decentworld.face2face.service.security.token.ITokenCheckService;
import online.decentworld.face2face.service.user.IUserInfoService;
import online.decentworld.rdb.entity.User;
import online.decentworld.rdb.entity.UserInfo;
import online.decentworld.rdb.entity.Wealth;
import online.decentworld.rdb.mapper.UserInfoMapper;
import online.decentworld.rdb.mapper.UserMapper;
import online.decentworld.rdb.mapper.WealthMapper;
import online.decentworld.rpc.dto.api.ObjectResultBean;
import online.decentworld.rpc.dto.api.ResultBean;
import online.decentworld.tools.MD5;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import static online.decentworld.face2face.common.StatusCode.FAILED;
import static online.decentworld.face2face.common.StatusCode.SUCCESS;
@Service
public class UserInfoService implements IUserInfoService{
	@Autowired
	private UserInfoMapper userInfoMapper;
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private ITokenCheckService tokenService;

	@Autowired
	private WealthMapper wealthMapper;
	
	private static Logger logger=LoggerFactory.getLogger(UserInfoService.class);

	@Override
	public ResultBean bindUserPhoneNum(String dwID, String phoneNum,String code) {
		ResultBean bean=new ResultBean();
		if(!tokenService.checkPhoneCode(phoneNum, PhoneCodeType.BIND, code)){
			bean.setStatusCode(StatusCode.FAILED);
			bean.setMsg("驗證碼錯誤！");
		}else{
			UserInfo info=new UserInfo();
			info.setDwid(dwID);
			info.setPhonenum(phoneNum);
			userInfoMapper.updateByPrimaryKeySelective(info);
			bean.setStatusCode(SUCCESS);
		}
		return bean;

	}

	@Override
	public ObjectResultBean getUserInfo(String dwID) {
		ObjectResultBean bean=new ObjectResultBean();
		User user=userMapper.selectByPrimaryKey(dwID);
		if(user==null){
			bean.setStatusCode(FAILED);
			bean.setMsg("用户不存在");
		}else{
			Wealth w=wealthMapper.selectByPrimaryKey(dwID);
			BaseDisplayUserInfo info=new BaseDisplayUserInfo(user,w.getWealth());
			bean.setStatusCode(SUCCESS);
			bean.setData(info);
		}
		return bean;
	}

	@Override
	public ResultBean updateUserInfo(User user) {
		if(user.getName().length()>20){
			return ResultBean.FAIL("您的用戶名過長！最多十個字符");
		}else{
			try{
				String password=user.getPassword();
				if(password.equals(userMapper.getUserPassword(user.getId()))){
					user=UserFieldFilter(user);
					userMapper.updateByPrimaryKeySelective(user);
					return ResultBean.SUCCESS;
				}else{
					return ResultBean.FAIL("身份校验错误");
				}
			}catch(DuplicateKeyException ex){
				logger.info("[REPEAT_NAME] user#"+JSON.toJSONString(user),ex);
				return ResultBean.FAIL("該用戶名已被使用！");
			}
		}
	}
	
	
	private User UserFieldFilter(User user){
		user.setPassword(null);
		user.setOpenid(null);
		user.setUnionid(null);
		user.setType(null);
		user.setVersion(null);
		user.setWorth(null);
		return user;
	}

	@Override
	public ResultBean setPassword(String dwID, String password,String token) {
		ResultBean bean=new ResultBean();
		password= MD5.GetMD5Code(password);
		if(tokenService.checkToken(dwID, TokenType.CHANGEPWD, token)){
			User user=new User();
			user.setId(dwID);user.setPassword(password);
			userMapper.updateByPrimaryKeySelective(user);
			bean.setStatusCode(StatusCode.SUCCESS);
		}else{
			bean.setStatusCode(StatusCode.FAILED);
			bean.setMsg("驗證碼錯誤");
		}
		return bean;
	}






}
