package online.decentworld.face2face.controller;

import online.decentworld.face2face.annotation.Frequency;
import online.decentworld.face2face.common.PhoneCodeType;
import online.decentworld.face2face.service.security.advice.IAdviceService;
import online.decentworld.face2face.service.security.authority.IUserAuthorityService;
import online.decentworld.face2face.service.security.report.IReportService;
import online.decentworld.face2face.service.security.token.ITokenCheckService;
import online.decentworld.face2face.service.sms.SMSService;
import online.decentworld.rdb.entity.CustomAdvice.AdviceType;
import online.decentworld.rpc.dto.api.ObjectResultBean;
import online.decentworld.rpc.dto.api.ResultBean;
import online.decentworld.tools.IDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static online.decentworld.face2face.common.StatusCode.FAILED;

@RequestMapping("/security")
@Controller
public class SecurityController {

	@Autowired
	private IReportService reportService;
	@Autowired
	private IAdviceService adviceService;
	@Autowired
	private IUserAuthorityService authorityService;
	@Autowired
	private SMSService SMSservice;
	@Autowired
	private ITokenCheckService tokenService;
	
	@RequestMapping("/report")
	@ResponseBody
	@Frequency(limit=2,time=60000)
	public ResultBean reportUser(@RequestParam String reporterID,@RequestParam String reportedID,@RequestParam String reason){
		ResultBean bean=null;
		if(reason!=null&&reason.length()>100){
			bean=new ResultBean();
			bean.setMsg("举报理由过长，请重新编辑");
			bean.setStatusCode(FAILED);
		}else{
			bean=reportService.reportUser(reporterID, reportedID, reason);
		}
		return bean;
	}
	
	@RequestMapping(value="/advise", method = RequestMethod.POST)
	@ResponseBody
	@Frequency(limit=1,time=60000)
	public ResultBean advise(MultipartFile voice,HttpServletRequest request) throws IOException{
		ResultBean bean;
		String dwID=request.getParameter("dwID");
		String type=request.getParameter("type");
		if(dwID!=null&&type!=null&&AdviceType.valueOf(type.toUpperCase())!=null){
			byte[] data=voice.getBytes();
			bean=adviceService.recordAdivce(dwID, data, AdviceType.valueOf(type.toUpperCase()));
		}else{
			bean=new ResultBean();
			bean.setStatusCode(FAILED);
			bean.setMsg("参数错误");
		}
		return bean;
	}

	@RequestMapping(value="/key/get")
	@ResponseBody
	public ResultBean getRSAKey(@RequestParam String dwID) throws IOException{
		return authorityService.getRSAKey();
	}


	@RequestMapping(value="/key/upload")
	@ResponseBody
	public ResultBean uploadAES(@RequestParam String dwID,@RequestParam String password,@RequestParam String key) throws IOException{
		return authorityService.uploadKey(dwID,password,key);
	}

	@RequestMapping("/forget/password")
	@ResponseBody
	@Frequency(limit = 3,time = 6000)
	public ResultBean resetPassword(@RequestParam String phoneNum){
		if(phoneNum.length()!=11){
			return ResultBean.FAIL("请输入11位手机号码");
		}
		return SMSservice.sendPhoneCode(phoneNum, PhoneCodeType.SETPWD, IDUtil.createRandomCode());
	}


	@RequestMapping("/forget/password/check")
	@ResponseBody
	public ObjectResultBean checkPhoneCode(@RequestParam String phoneCode,@RequestParam String phoneNum){
		return tokenService.checkPhoneCodeAndCreateToken(phoneNum, phoneCode, PhoneCodeType.SETPWD);
	}

}
