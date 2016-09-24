package online.decentworld.face2face.service.register.impl;

import online.decentworld.face2face.api.easemob.EasemobApiUtil;
import online.decentworld.face2face.api.wx.WXInfo;
import online.decentworld.face2face.api.wx.WeChatApiUtil;
import online.decentworld.face2face.common.CommonProperties;
import online.decentworld.face2face.common.StatusCode;
import online.decentworld.face2face.common.UserType;
import online.decentworld.face2face.exception.*;
import online.decentworld.face2face.service.register.IRegisterService;
import online.decentworld.rdb.entity.User;
import online.decentworld.rdb.entity.Wealth;
import online.decentworld.rdb.mapper.UserMapper;
import online.decentworld.rdb.mapper.WealthMapper;
import online.decentworld.rpc.dto.api.ObjectResultBean;
import online.decentworld.tools.IDUtil;
import online.decentworld.tools.MD5;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

/**
 * 通过微信获取用户信息并存储、注册环信账号
 * @author Sammax
 *
 */
@Service(value="wxRegisterService")
public class WXEasemobRegisterService implements IRegisterService{

	@Autowired
	private EasemobApiUtil easemobAPI;
	@Autowired
	private WeChatApiUtil wechatAPI;
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private WealthMapper wealthMapper;
	
	

	
	private static Logger logger=LoggerFactory.getLogger(WXEasemobRegisterService.class);

	@Override
	public ObjectResultBean register(String code) {
		ObjectResultBean bean=new ObjectResultBean();
		try{
			WXInfo info=wechatAPI.getWXInfo(code);
			User user=userMapper.selectByUnionid(info.getUnionid());
			if(user==null){
				String password= MD5.GetMD5Code(info.getUnionid());
				String id= IDUtil.getDWID();
				id=easemobAPI.registerEasemobUser(id,password);
				user=new User(id,info.getUnionid(),info.getOpenid(),info.getHeadimgurl(),info.getNickname(),
						password,info.getCity(),CommonProperties.DEFAULT_WORTH,null,info.getSex(),null,0,UserType.UNCERTAIN.toString());
				tryStoreUser(user);
				Wealth w=new Wealth();
				w.setDwid(id);
				w.setWealth(0);
				wealthMapper.insert(w);
			}
			bean.setStatusCode(StatusCode.SUCCESS);
			bean.setData(user); 
		}catch(GetWXAccessTokenError | GetWXInfoError e){
			bean.setStatusCode(StatusCode.FAILED);
			bean.setMsg("获取微信授权信息失败");
			logger.warn("[WX_REGISTER_RETRIVEINFO_FAILED]",e);
		}catch (EasemobAPIException |GetEasemobTokenException|RegisterFailException e) {
			bean.setStatusCode(StatusCode.FAILED);
			bean.setMsg("注册失败");
			logger.warn("[WX_REGISTER_EASEMOB_FAILED]",e);
		} 
		return bean;
	}
	
	private User trimName(User info){
		if(info.getName().length()>CommonProperties.MAX_NAME_LENGTH){
			info.setName(info.getName().substring(info.getName().length()-CommonProperties.MAX_NAME_LENGTH,info.getName().length()));
		}
		return info;
	}


	private void tryStoreUser(User user) throws RegisterFailException{
		int attemp=0;
		while(true){
			attemp++;
			trimName(user);
			try{
				userMapper.insert(user);
				break;
			}catch(DuplicateKeyException e){
				logger.info("[REPEAT_NAME]");
				user.setName(user.getName()+attemp);
			}
			//.....不會這麼倒霉有20個重名的註冊吧
			if(attemp>20){
				throw new RegisterFailException();
			}
		}
	}
}
