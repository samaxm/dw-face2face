package online.decentworld.face2face.service.user.impl;

import com.alibaba.fastjson.JSON;
import online.decentworld.face2face.common.PhoneCodeType;
import online.decentworld.face2face.common.StatusCode;
import online.decentworld.face2face.common.TokenType;
import online.decentworld.face2face.service.security.authority.impl.RDBUserAuthorityService;
import online.decentworld.face2face.service.security.token.ITokenCheckService;
import online.decentworld.face2face.service.user.IUserInfoService;
import online.decentworld.face2face.tools.FastDFSClient;
import online.decentworld.rdb.entity.PayPassword;
import online.decentworld.rdb.entity.User;
import online.decentworld.rdb.entity.UserInfo;
import online.decentworld.rdb.entity.Wealth;
import online.decentworld.rdb.mapper.PayPasswordMapper;
import online.decentworld.rdb.mapper.UserInfoMapper;
import online.decentworld.rdb.mapper.UserMapper;
import online.decentworld.rdb.mapper.WealthMapper;
import online.decentworld.rpc.dto.api.ObjectResultBean;
import online.decentworld.rpc.dto.api.ResultBean;
import online.decentworld.tools.AES;
import online.decentworld.tools.IDUtil;
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
	@Autowired
	private RDBUserAuthorityService authorityService;
	@Autowired
	private PayPasswordMapper payPasswordMapper;
	
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
		if(user.getName()!=null&&user.getName().length()>20){
			return ResultBean.FAIL("您的用戶名過長！最多十個字符");
		}else{
			try{
				String password=user.getPassword();
				User u=userMapper.selectByPrimaryKey(user.getId());
				if(password.equals(u.getPassword())){
					user=UserFieldFilter(user);
					userMapper.updateByPrimaryKeySelective(user);
					if(u.getIcon()!=null&&!u.getIcon().equals("")){
						FastDFSClient.deleteByFullName(u.getIcon());
					}
					return ObjectResultBean.SUCCESS(user);
				}else{
					return ResultBean.FAIL("身份校验错误");
				}
			}catch(DuplicateKeyException ex){
				logger.info("[REPEAT_NAME] user#"+JSON.toJSONString(user),ex);
				return ResultBean.FAIL("該用戶名已被使用！");
			}
		}
	}

	@Override
	public ResultBean preSetUserPayPassword(String dwID, String password) {
		if(authorityService.checkPayPassword(dwID,password)){
			String token=IDUtil.randomToken();
			tokenService.cacheToken(token,dwID, TokenType.SET_PAY_PASSWORD);
			return  ObjectResultBean.SUCCESS(token);
		}else{
			return ResultBean.FAIL("登录密码错误，请重试或通过手机号码找回密码");
		}
	}

	@Override
	public ResultBean setUserPayPassword(String dwID, String payPassword,String token) {
		if(tokenService.checkToken(dwID,TokenType.SET_PAY_PASSWORD,token)){
			payPassword= AES.decode(payPassword,authorityService.getUserKey(dwID));
			PayPassword payPasswordrecord=new PayPassword();
			payPasswordrecord.setDwid(dwID);
			payPasswordrecord.setPayPassword(payPassword);
			payPasswordMapper.insert(payPasswordrecord);
			return ResultBean.SUCCESS;
		}else{
			return ResultBean.FAIL("令牌错误，请验证登录密码后设置支付密码");
		}
	}

	@Override
	public ResultBean getUserWealth(String dwID) {
		String key=authorityService.getUserKey(dwID);
		dwID=AES.decode(dwID,key);
		if(dwID.length()==10){
			Wealth wealth=wealthMapper.selectByPrimaryKey(dwID);
			if(wealth!=null)
				return ObjectResultBean.SUCCESS(wealth.getWealth());
		}
		return ResultBean.FAIL("用户ID错误");
	}


	public static User UserFieldFilter(User user){
		user.setPassword(null);
		user.setOpenid(null);
		user.setUnionid(null);
		user.setType(null);
		user.setVersion(null);
		user.setWorth(null);
		return user;
	}
}
