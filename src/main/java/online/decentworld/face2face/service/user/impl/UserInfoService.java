package online.decentworld.face2face.service.user.impl;

import com.alibaba.fastjson.JSON;
import online.decentworld.face2face.common.PhoneCodeType;
import online.decentworld.face2face.common.TokenType;
import online.decentworld.face2face.service.security.authority.IUserAuthorityService;
import online.decentworld.face2face.service.security.token.ITokenCheckService;
import online.decentworld.face2face.service.user.IUserInfoService;
import online.decentworld.face2face.tools.FastDFSClient;
import online.decentworld.rdb.entity.BaseDisplayUserInfo;
import online.decentworld.rdb.entity.PayPassword;
import online.decentworld.rdb.entity.User;
import online.decentworld.rdb.entity.UserInfo;
import online.decentworld.rdb.mapper.PayPasswordMapper;
import online.decentworld.rdb.mapper.UserMapper;
import online.decentworld.rpc.dto.api.ObjectResultBean;
import online.decentworld.rpc.dto.api.ResultBean;
import online.decentworld.tools.AES;
import online.decentworld.tools.IDUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
@Service
@CacheConfig(cacheResolver = "default_cache_resolver")
public class UserInfoService implements IUserInfoService{

	@Autowired
	private UserMapper userMapper;
	@Autowired
	private ITokenCheckService tokenService;
	@Autowired
	private IUserAuthorityService authorityService;
	@Autowired
	private PayPasswordMapper payPasswordMapper;
	
	private static Logger logger=LoggerFactory.getLogger(UserInfoService.class);

	@Override
	public ResultBean bindUserPhoneNum(String dwID, String phoneNum,String code) {

		if(!tokenService.checkPhoneCode(phoneNum, PhoneCodeType.BIND, code)){
			return ObjectResultBean.FAIL("验证码错误");
		}else{
			UserInfo info=new UserInfo();
			info.setDwid(dwID);
			info.setPhonenum(phoneNum);
			try {
				userMapper.bindPhoneNum(phoneNum, dwID);
			}catch (DuplicateKeyException e){
				return ObjectResultBean.FAIL("该手机号码已被绑定");
			}
			String token=IDUtil.randomToken();
			tokenService.cacheToken(token,phoneNum,TokenType.CHANGEPWD);
			return ObjectResultBean.SUCCESS(token);
		}
	}

	@Override
	@Cacheable(value="redis_online.decentworld.rdb.entity.BaseDisplayUserInfo" ,key="#dwID")
	public BaseDisplayUserInfo getUserInfo(String dwID) {
		System.out.println("invoke");
		User user=userMapper.selectByPrimaryKey(dwID);
		if(user==null){
			return null;
		}else{
			return new BaseDisplayUserInfo(user);
		}
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




	public static User UserFieldFilter(User user){
		user.setPassword(null);
		user.setOpenid(null);
		user.setUnionid(null);
		user.setType(null);
		user.setVersion(null);
		return user;
	}
}
