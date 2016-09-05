package online.decentworld.face2face.controller;

import online.decentworld.face2face.common.AppType;
import online.decentworld.face2face.common.StatusCode;
import online.decentworld.face2face.service.app.IAppService;
import online.decentworld.rpc.dto.api.ResultBean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/app")
@Controller
public class AppController {
	
	@Autowired
	private IAppService appService;
	
	
	@RequestMapping("/check/version")
	@ResponseBody
	public ResultBean sendRegisterPhoneCode(@RequestParam String appType){
		ResultBean bean=null;
		if(AppType.checkType(appType)){
			bean=appService.checkVersion(appType);
		}else{
			bean=new ResultBean();
			bean.setStatusCode(StatusCode.FAILED);
			bean.setMsg("类型错误");
		}
		return bean;
	}
}
