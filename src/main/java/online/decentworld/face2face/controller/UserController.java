package online.decentworld.face2face.controller;

import online.decentworld.face2face.service.register.IRegisterService;
import online.decentworld.face2face.service.register.RegisterStrategyFactory;
import online.decentworld.face2face.service.user.IUserInfoService;
import online.decentworld.rpc.dto.api.ResultBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@RequestMapping("/user")
@Controller
public class UserController {

	@Autowired
	private RegisterStrategyFactory registerService;
	@Autowired
	private IUserInfoService userService; 
	@Autowired
	private CacheManager cacheManager;
	
	private static Logger logger=LoggerFactory.getLogger(UserController.class);
	
	@RequestMapping("/register")
	@ResponseBody
	public ResultBean register(@RequestParam String registerInfo,@RequestParam String registerType){
		
		IRegisterService service=registerService.getService(registerType.toUpperCase());
		ResultBean bean=service.register(registerInfo);
		
		return bean;
	}
	
	@RequestMapping("/info")
	@ResponseBody
	@Cacheable(value="userInfo",key="#dwID")
	public ResultBean getUserInfo(@RequestParam String dwID){
		logger.debug("[GET_USER_INFO] dwID#"+dwID);
		return userService.getUserInfo(dwID);
	}
	
	@RequestMapping("/bind/phone")
	@ResponseBody
	public ResultBean bindPhone(@RequestParam String dwID,@RequestParam String phoneNum,@RequestParam String code){
		
		return userService.bindUserPhoneNum(dwID, phoneNum, code);
	}
	
	@RequestMapping("/set/password")
	@ResponseBody
	public ResultBean setPassword(@RequestParam String dwID,@RequestParam String token,@RequestParam String password){
		
		return userService.setPassword(dwID, password,token);
	}
}
