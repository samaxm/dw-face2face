package online.decentworld.face2face.controller;

import online.decentworld.face2face.common.AppType;
import online.decentworld.face2face.common.StatusCode;
import online.decentworld.face2face.service.app.IAppService;
import online.decentworld.face2face.service.app.impl.IphoneOnlineStatusCommand;
import online.decentworld.rpc.dto.api.ResultBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@RequestMapping("/app")
@Controller
public class AppController {

	@Autowired
	private IAppService appService;

	private static Logger logger= LoggerFactory.getLogger(AppController.class);

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

	@RequestMapping("/online/users")
	@ResponseBody
	public ResultBean getOnlineUser(int page){
		return appService.getOnlineUsers(page);
	}


	@RequestMapping("/iphone/status")
	@ResponseBody
	public ResultBean isIphoneInChek(Integer versionNum){
		if(versionNum==null)
			versionNum=0;
		return appService.getiphoneStatus(versionNum);
	}



	@RequestMapping("/set/iphone/status")
	@ResponseBody
	public ResultBean setIphoneStatus(String token,Integer versionNum)
	{
		IphoneOnlineStatusCommand command=new IphoneOnlineStatusCommand();
		command.setToken(token);
		command.setVersionNum(versionNum);
		return appService.setiphoneStatus(command);
	}


	@RequestMapping("/onlineStatus")
	@ResponseBody
	public ResultBean getOnlineNum(@RequestParam String fromtime,@RequestParam String totime){
		HttpHeaders headers=new HttpHeaders();
		headers.set("Access-Control-Allow-Origin", "*");
		return appService.getOnlineStatus(fromtime,totime);
	}



	@RequestMapping("/activities")
	@ResponseBody
	public ResultBean getActivities(@RequestParam Long dateNum){
		return appService.getActivityList(dateNum);
	}


	@RequestMapping("/activity/answer")
	@ResponseBody
	public ResultBean checkActivityAnswer(@RequestParam Integer activityID,@RequestParam String dwID,@RequestParam String answer){
		return appService.checkActivityAnswer(activityID,answer,dwID);
	}


	@RequestMapping("/activity/address")
	@ResponseBody
	public ResultBean uploadAddress(@RequestParam Integer answerID,@RequestParam String dwID,@RequestParam String address){
		return appService.uploadAdress(dwID,answerID,address);
	}

}
