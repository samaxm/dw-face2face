package online.decentworld.face2face.service.user.impl;

import com.alibaba.fastjson.JSON;
import online.decentworld.cache.redis.SessionCache;
import online.decentworld.charge.ChargeService;
import online.decentworld.charge.event.ChangeWorthEvent;
import online.decentworld.charge.receipt.ChargeReceipt;
import online.decentworld.charge.service.TransferAccountType;
import online.decentworld.face2face.api.easemob.EasemobApiUtil;
import online.decentworld.face2face.common.TokenType;
import online.decentworld.face2face.common.UserType;
import online.decentworld.face2face.service.search.ISearchService;
import online.decentworld.face2face.service.security.authority.IUserAuthorityService;
import online.decentworld.face2face.service.security.token.ITokenCheckService;
import online.decentworld.face2face.service.user.IUserInfoService;
import online.decentworld.face2face.tools.FastDFSClient;
import online.decentworld.rdb.entity.BaseDisplayUserInfo;
import online.decentworld.rdb.entity.User;
import online.decentworld.rdb.mapper.UserMapper;
import online.decentworld.rpc.dto.api.MapResultBean;
import online.decentworld.rpc.dto.api.ObjectResultBean;
import online.decentworld.rpc.dto.api.ResultBean;
import online.decentworld.rpc.dto.api.StatusCode;
import online.decentworld.tools.IDUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
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
	@Autowired
	private ISearchService searchService;
	@Autowired
	private SessionCache sessionCache;
	@Autowired
	private IUserAuthorityService authorityService;
	@Autowired
	private ChargeService chargeService;


	private static Logger logger=LoggerFactory.getLogger(UserInfoService.class);

	@Override
	public ResultBean bindUserPhoneNum(String dwID, String phoneNum,String code) {
		if(!tokenService.checkToken(phoneNum,TokenType.BIND_PHONE,code)){
			return ObjectResultBean.FAIL("验证码错误");
		}else{
			try {
				userMapper.bindPhoneNum(phoneNum, dwID);
				/**
				 * 绑定成功后直接返回一个可以设置密码的token
				 */
			    String token= IDUtil.randomToken();
				tokenService.cacheToken(phoneNum,TokenType.CHANGE_PWD,token);
				return ObjectResultBean.SUCCESS(token);
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
	@CacheEvict(value="redis_online.decentworld.rdb.entity.BaseDisplayUserInfo" ,key="#user.id")
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

					searchService.saveOrUpdateIndex(user);
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
		MapResultBean<String,String> bean=new MapResultBean();
		bean.getData().put("account",account);
		bean.getData().put("accountType",accountType.name());
		bean.setStatusCode(StatusCode.SUCCESS);
		return bean;
	}

	@Override
	public ResultBean bindJPush(String dwID, String jpushID) {
		sessionCache.cacheOfflinePushChannel(dwID,jpushID);
		return ResultBean.SUCCESS;
	}

	@Override
	public String getJPush(String dwID) {
		return sessionCache.getOfflinePushChannle(dwID);
	}

	@Override
	public void setUserPayPassword(String dwID, String payPassword) {
		userMapper.setPayPassword(dwID,payPassword);
	}

	/**
	 * filter all invalid field
	 * @param user
	 * @return
	 */
	public static User UserFieldFilter(User user){
		user.setPassword(null);
		user.setPayPassword(null);
		user.setOpenid(null);
		user.setUnionid(null);
		user.setType(null);
		user.setVersion(null);
		user.setWorth(null);
		user.setTag(null);
		return user;
	}

	@Override
	public void setPassword(String phoneNum, String password) throws Exception {
		User user=userMapper.selectByPhoneNum(phoneNum);
		if(user!=null){
			userMapper.resetPassword(phoneNum,password);
			easemobApiUtil.resetPassword(user.getId(),password);
		}

	}

	@Override
	public ResultBean setWorth(String dwID, String paypassword, int worth) {
		String type=userMapper.getUserType(dwID);
		if(!UserType.STAR.getName().equals(type)){
			return ResultBean.FAIL("目前该功能仅对VIP用户开放");
		}
		if(authorityService.checkPayPassword(dwID,paypassword)){
			try {
				ChargeReceipt receipt=chargeService.charge(new ChangeWorthEvent(worth, dwID));
				return ObjectResultBean.SUCCESS(receipt.getChargeResult().getPayerWealth());
			} catch (Exception e) {
				return ResultBean.FAIL("修改身价失败！");
			}
		}else{
			return ResultBean.FAIL("支付密码错误");
		}
	}

}
