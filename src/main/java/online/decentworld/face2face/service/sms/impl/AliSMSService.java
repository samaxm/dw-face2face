package online.decentworld.face2face.service.sms.impl;

import static online.decentworld.face2face.common.StatusCode.FAILED;
import static online.decentworld.face2face.common.StatusCode.SUCCESS;
import online.decentworld.face2face.api.alibaba.AliSMSApiUtil;
import online.decentworld.face2face.cache.SecurityCache;
import online.decentworld.face2face.common.PhoneCodeType;
import online.decentworld.face2face.service.sms.SMSService;
import online.decentworld.rpc.dto.api.ResultBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class AliSMSService implements SMSService{

	@Autowired
	private AliSMSApiUtil aliSMSUtil;
	@Autowired
	private SecurityCache securityCache;
	private static Logger logger=LoggerFactory.getLogger(AliSMSService.class);
	
	@Override
	public ResultBean sendPhoneCode(String phoneNum, PhoneCodeType type,
			String code) {
		ResultBean bean=new ResultBean();
		try {
			aliSMSUtil.sendCode(phoneNum, type,code);
			securityCache.cachePhoneCode(phoneNum, code, type);
			bean.setStatusCode(SUCCESS);
			
		} catch (Exception e) {
			bean.setStatusCode(FAILED);
			bean.setMsg("发送验证码失败，请重试");
			logger.warn("[SEND_SMS_FAILED] phoneNum#"+phoneNum+" type#"+type+" code#"+code);
		}
		return bean;
	}

}
