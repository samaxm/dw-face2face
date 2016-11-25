package online.decentworld.face2face.service.register;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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
	@Resource(name="phoneCodeRegisterService")
	private IRegisterService phoneCodeRegisterService;
	@Resource(name="vipRegisterService")
	private IRegisterService vipRegisterService;


	public IRegisterService getService(String registerType){
		RegisterType type=RegisterType.valueOf(registerType);
		if(type==RegisterType.WX){
			return wxRegisterService;
		}else if(type==RegisterType.INFO){
			return userInfoEasemobRegisterService;
		}else if(type==RegisterType.PHONECODE) {
			return phoneCodeRegisterService;
		}else if(type==RegisterType.VIP){
			return vipRegisterService;
		}else{
			return null;
		}
	}
}
