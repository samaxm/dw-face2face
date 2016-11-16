package online.decentworld.face2face.controller;

import online.decentworld.face2face.annotation.Frequency;
import online.decentworld.face2face.common.TokenType;
import online.decentworld.face2face.service.security.advice.IAdviceService;
import online.decentworld.face2face.service.security.authority.IUserAuthorityService;
import online.decentworld.face2face.service.security.report.IReportService;
import online.decentworld.face2face.service.security.token.ITokenCheckService;
import online.decentworld.face2face.service.sms.SMSService;
import online.decentworld.face2face.service.user.IUserInfoService;
import online.decentworld.rdb.entity.CustomAdvice.AdviceType;
import online.decentworld.rpc.dto.api.ObjectResultBean;
import online.decentworld.rpc.dto.api.ResultBean;
import online.decentworld.tools.AES;
import online.decentworld.tools.IDUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	private SMSService sMSservice;
	@Autowired
	private ITokenCheckService tokenService;
	@Autowired
	private IUserInfoService userInfoService;

	private static Logger logger= LoggerFactory.getLogger(SecurityController.class);
	
	@RequestMapping("/report")
	@ResponseBody
	@Frequency(limit=20,time=60000)
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
	@Frequency(limit=10,time=60000)
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

	@RequestMapping("/password/token")
	@ResponseBody
	@Frequency(limit = 30,time = 6000)
	public ResultBean resetPassword(@RequestParam String phoneNum){
		if(phoneNum.length()!=11){
			return ResultBean.FAIL("请输入11位手机号码");
		}
		return sMSservice.sendPhoneCode(phoneNum, TokenType.PRE_CHANGE_PWD, IDUtil.createRandomCode());
	}

	@RequestMapping("/password/token/check")
	@ResponseBody
	public ResultBean checkPhoneCode(@RequestParam String phoneCode,@RequestParam String phoneNum){
		if(phoneNum==null||phoneNum.length()!=11){
			return ObjectResultBean.FAIL("请绑定正确的手机号码");
		}else{
			return tokenService.checkPhoneCodeAndCreateToken(phoneNum,TokenType.PRE_CHANGE_PWD,phoneCode,phoneNum,TokenType.CHANGE_PWD);
		}
	}
	@RequestMapping("/password")
	@ResponseBody
	public ResultBean resetPassword(@RequestParam String phoneNum,@RequestParam String password,@RequestParam String token){
		if(tokenService.checkToken(phoneNum,TokenType.CHANGE_PWD,token)){
			password= AES.decode(password);
			try {
				userInfoService.setPassword(phoneNum, password);
				return ResultBean.SUCCESS;
			}catch (Exception e){
				logger.warn("",e);
				return ResultBean.FAIL("设置密码出错，请重试");
			}
		}else{
			return ObjectResultBean.FAIL("令牌错误");
		}
	}

	@RequestMapping("/pay_password/token")
	@ResponseBody
	public ResultBean sendPayPasswordPhoneCode(@RequestParam String dwID,@RequestParam String phoneNum){
		if(phoneNum==null||phoneNum.length()!=11){
			return ObjectResultBean.FAIL("请绑定正确的手机号码");
		}else{
			if(authorityService.preSetPayPassowrd(dwID, phoneNum)){
				sMSservice.sendPhoneCode(phoneNum,TokenType.PRE_SET_PAY_PASSWORD,IDUtil.createRandomCode());
				return ObjectResultBean.SUCCESS;
			}else{
				return ObjectResultBean.FAIL("为了您的账户安全，请绑定手机号并设置收款账户后再进行支付密码设置");
			}
		}
	}

	@RequestMapping("/pay_password/token/check")
	@ResponseBody
	public ResultBean checkPayPasswordPhoneCode(@RequestParam String dwID,@RequestParam String phoneNum,@RequestParam String code){
		if(phoneNum==null||phoneNum.length()!=11){
			return ObjectResultBean.FAIL("请绑定正确的手机号码");
		}else{
			if(authorityService.preSetPayPassowrd(dwID, phoneNum)){
				return tokenService.checkPhoneCodeAndCreateToken(phoneNum,TokenType.PRE_SET_PAY_PASSWORD,code,dwID,TokenType.SET_PAY_PASSWORD);
			}else{
				return ObjectResultBean.FAIL("绑定的手机号有误！");
			}
		}
	}

	@RequestMapping("/pay_password")
	@ResponseBody
	public ResultBean setPayPassword(@RequestParam String dwID,@RequestParam String password,@RequestParam String token){
		if(tokenService.checkToken(dwID,TokenType.SET_PAY_PASSWORD,token)){
			password=AES.decode(password, authorityService.getUserKey(dwID));
			userInfoService.setUserPayPassword(dwID,password);
			return ResultBean.SUCCESS;
		}else{
			return ObjectResultBean.FAIL("令牌错误");
		}
	}
}
