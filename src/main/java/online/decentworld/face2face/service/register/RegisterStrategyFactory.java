package online.decentworld.face2face.service.register;

import javax.annotation.Resource;

import online.decentworld.face2face.common.RegisterType;

import org.springframework.stereotype.Service;

/**
 * 註冊服務的簡單工廠
 * @author Sammax
 *
 */
@Service
public class RegisterStrategyFactory {
	@Resource(name="wxRegisterService")
	private IRegisterService wxRegisterService;
	@Resource(name = "userInfoEasemobRegisterService")
	private IRegisterService userInfoEasemobRegisterService;

	public IRegisterService getService(String registerType){
		RegisterType type=RegisterType.valueOf(registerType);
		if(type==RegisterType.WX){
			return wxRegisterService;
		}else if(type==RegisterType.INFO){
			return userInfoEasemobRegisterService;
		}else {
			return null;
		}
	}
}
