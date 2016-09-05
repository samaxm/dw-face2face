package online.decentworld.face2face.api.alibaba;

import online.decentworld.face2face.cache.SecurityCache;
import online.decentworld.face2face.common.PhoneCodeType;
import static online.decentworld.face2face.config.ConfigLoader.APIConfig.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;
import com.taobao.api.response.AlibabaAliqinFcSmsNumSendResponse;
/**
 * 阿里大鱼API
 * @author Sammax
 *
 */
@Component
public class AliSMSApiUtil {

	
	@Autowired
	private SecurityCache tokenCache;
	
	private static Log logger=LogFactory.getLog(AliSMSApiUtil.class);
	
	public SendResult sendCode(String phoneNum,PhoneCodeType type,String code) throws Exception{
		TaobaoClient client = new DefaultTaobaoClient(ALI_SMS_URL, ALI_SMS_APPKEY,ALI_SMS_APP_SECRET);
		AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();
		req.setSmsType("normal");
		req.setSmsFreeSignName(ALI_SMS_SIGN_TEMPLATE_DW);
		req.setSmsParamString("{\"code\":\""+code+"\"}");
		req.setRecNum(phoneNum);
		req.setSmsTemplateCode(ALI_SMS_REGISTER_NOTICE);
		AlibabaAliqinFcSmsNumSendResponse rsp = client.execute(req);
		if( rsp.getResult().getSuccess()){
			logger.info("[SEND_PHONE_CODE] phoneNum#"+phoneNum+" phoneCodeType#"+type);
			tokenCache.cachePhoneCode(phoneNum,code,type);
			return SendResult.SUCCESS;
		}else{
			return SendResult.FAIL;
		}
		
	}
	
	public enum SendResult{
		SUCCESS,FAIL;
	}

}
