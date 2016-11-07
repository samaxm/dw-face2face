package online.decentworld.face2face.service.sms.impl;

import online.decentworld.face2face.api.alibaba.AliSMSApiUtil;
import online.decentworld.face2face.cache.SecurityCache;
import online.decentworld.face2face.common.TokenType;
import online.decentworld.face2face.service.sms.SMSService;
import online.decentworld.rpc.dto.api.ObjectResultBean;
import online.decentworld.rpc.dto.api.ResultBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class AliSMSService implements SMSService{

	@Autowired
	private SecurityCache securityCache;
	private static Logger logger=LoggerFactory.getLogger(AliSMSService.class);
	
	@Override
	public ResultBean sendPhoneCode(String phoneNum, TokenType type,
			String code) {
		try {
			AliSMSApiUtil.sendCode(phoneNum,code);
			securityCache.cacheToken(phoneNum, type, code);
			return ResultBean.SUCCESS;
		} catch (Exception e) {
			logger.warn("[SEND_SMS_FAILED] phoneNum#"+phoneNum+" type#"+type+" code#"+code);
			return ObjectResultBean.FAIL("发送验证码失败，请重试");
		}
	}

}
