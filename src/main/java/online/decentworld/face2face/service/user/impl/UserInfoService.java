package online.decentworld.face2face.service.user.impl;

import com.alibaba.fastjson.JSON;
import online.decentworld.charge.service.TransferAccountType;
import online.decentworld.face2face.api.easemob.EasemobApiUtil;
import online.decentworld.face2face.common.TokenType;
import online.decentworld.face2face.service.security.token.ITokenCheckService;
import online.decentworld.face2face.service.user.IUserInfoService;
import online.decentworld.face2face.tools.FastDFSClient;
import online.decentworld.rdb.entity.BaseDisplayUserInfo;
import online.decentworld.rdb.entity.User;
import online.decentworld.rdb.mapper.UserMapper;
import online.decentworld.rpc.dto.api.ObjectResultBean;
import online.decentworld.rpc.dto.api.ResultBean;
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
	private EasemobApiUtil easemobApiUtil;


	
	private static Logger logger=LoggerFactory.getLogger(UserInfoService.class);

	@Override
	public ResultBean bindUserPhoneNum(String dwID, String phoneNum,String code) {
		if(!tokenService.checkToken(phoneNum,TokenType.BIND_PHONE,code)){
			return ObjectResultBean.FAIL("验证码错误");
		}else{
			try {
				userMapper.bindPhoneNum(phoneNum, dwID);
				return ResultBean.SUCCESS;
			}catch (DuplicateKeyException e){
				return ObjectResultBean.FAIL("该手机号码已被绑定");
			}
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
	public ResultBean bindAccount(String dwID,TransferAccountType accountType, String account) {
		userMapper.bindAccount(account,accountType.name(),dwID);
		return ResultBean.SUCCESS;
	}

	@Override
	public void setUserPayPassword(String dwID, String payPassword) {
		userMapper.setPayPassword(dwID,payPassword);
	}

	public static User UserFieldFilter(User user){
		user.setPassword(null);
		user.setOpenid(null);
		user.setUnionid(null);
		user.setType(null);
		user.setVersion(null);
		return user;
	}

	@Override
	public void setPassword(String dwID, String password) throws Exception {
		User user=new User();
		user.setId(dwID);
		user.setPassword(password);
		userMapper.updateByPrimaryKeySelective(user);
		easemobApiUtil.resetPassword(dwID,password);
	}

}
