package online.decentworld.face2face.api.alibaba;

import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;
import com.taobao.api.response.AlibabaAliqinFcSmsNumSendResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static online.decentworld.face2face.config.ConfigLoader.APIConfig.*;
/**
 * 阿里大鱼API
 * @author Sammax
 *
 */
public class AliSMSApiUtil {

	private static Log logger=LogFactory.getLog(AliSMSApiUtil.class);
	
	public static SendResult sendCode(String phoneNum,String code) throws Exception{
		TaobaoClient client = new DefaultTaobaoClient(ALI_SMS_URL, ALI_SMS_APPKEY,ALI_SMS_APP_SECRET);
		AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();
		req.setSmsType("normal");
		req.setSmsFreeSignName(ALI_SMS_SIGN_TEMPLATE_DW);
		req.setSmsParamString("{\"code\":\""+code+"\"}");
		req.setRecNum(phoneNum);
		req.setSmsTemplateCode(ALI_SMS_REGISTER_NOTICE);
		AlibabaAliqinFcSmsNumSendResponse rsp = client.execute(req);
		if( rsp.getResult().getSuccess()){
			logger.debug("[SEND_PHONE_CODE] phoneNum#"+phoneNum+" code#"+code);
			return SendResult.SUCCESS;
		}else{
			return SendResult.FAIL;
		}
		
	}
	
	public enum SendResult{
		SUCCESS,FAIL;
		private int code;
		public void setCode(int code){
			this.code=code;
		}
		public int getCode(){
			return code;
		}
	}

}
