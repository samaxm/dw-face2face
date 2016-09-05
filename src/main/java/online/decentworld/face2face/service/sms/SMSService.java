package online.decentworld.face2face.service.sms;

import online.decentworld.face2face.common.PhoneCodeType;
import online.decentworld.rpc.dto.api.ResultBean;

public interface SMSService {
	ResultBean sendPhoneCode(String phoneNum,PhoneCodeType type,String code);
	
}
