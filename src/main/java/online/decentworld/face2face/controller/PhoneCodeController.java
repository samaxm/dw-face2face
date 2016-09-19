package online.decentworld.face2face.controller;

import online.decentworld.face2face.common.PhoneCodeType;
import online.decentworld.face2face.common.StatusCode;
import online.decentworld.face2face.service.security.token.ITokenCheckService;
import online.decentworld.face2face.service.sms.SMSService;
import online.decentworld.rpc.dto.api.ObjectResultBean;
import online.decentworld.rpc.dto.api.ResultBean;
import online.decentworld.tools.IDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * 發送驗證碼接口
 * @author Sammax
 *
 */
@RequestMapping("/phonecode")
@Controller
public class PhoneCodeController {

	@Autowired
	private SMSService SMSservice;
	@Autowired
	private ITokenCheckService tokenService;
	
	
	@RequestMapping("/send")
	@ResponseBody
	public ResultBean sendRegisterPhoneCode(@RequestParam String phoneNum,@RequestParam String type){
		
		if(type==null||PhoneCodeType.valueOf(type)==null){
			ResultBean bean=new ResultBean();
			bean.setMsg("異常驗證碼類型");
			bean.setStatusCode(StatusCode.FAILED);
			return bean;
		}else{
			return SMSservice.sendPhoneCode(phoneNum, PhoneCodeType.valueOf(type), IDUtil.createRandomCode());
		}
	}
	
	@RequestMapping("/check")
	@ResponseBody
	public ObjectResultBean checkPhoneCode(@RequestParam String dwID,@RequestParam String phoneCode,@RequestParam String phoneNum,@RequestParam String type){
		
		if(type==null||PhoneCodeType.valueOf(type)==null){
			ObjectResultBean bean=new ObjectResultBean();
			bean.setMsg("異常驗證碼類型");
			bean.setStatusCode(StatusCode.FAILED);
			return bean;
		}else{
			return tokenService.checkPhoneCodeAndCreateToken(dwID, phoneNum, phoneCode, PhoneCodeType.valueOf(type));
		}
	}

}
