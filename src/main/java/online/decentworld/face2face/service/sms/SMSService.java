package online.decentworld.face2face.service.sms;

import online.decentworld.face2face.common.TokenType;
import online.decentworld.rpc.dto.api.ResultBean;

public interface SMSService {
	ResultBean sendPhoneCode(String phoneNum,TokenType type,String code);
}
