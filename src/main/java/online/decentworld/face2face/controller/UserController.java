package online.decentworld.face2face.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import online.decentworld.face2face.common.CommonProperties;
import online.decentworld.face2face.common.FileSubfix;
import online.decentworld.face2face.common.StatusCode;
import online.decentworld.face2face.service.register.IRegisterService;
import online.decentworld.face2face.service.register.RegisterStrategyFactory;
import online.decentworld.face2face.service.user.IUserInfoService;
import online.decentworld.face2face.tools.FastDFSClient;
import online.decentworld.rdb.entity.User;
import online.decentworld.rpc.dto.api.ResultBean;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import sun.nio.ch.IOUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.IOException;
import java.util.Collection;

import static online.decentworld.face2face.common.CommonProperties.HTTP;
import static online.decentworld.face2face.config.ConfigLoader.DomainConfig.FDFS_DOMAIN;


@RequestMapping("/user")
@Controller
public class UserController {

	@Autowired
	private RegisterStrategyFactory registerService;
	@Autowired
	private IUserInfoService userService; 



	private static Logger logger=LoggerFactory.getLogger(UserController.class);


	@RequestMapping("/register")
	@ResponseBody
	public ResultBean register(@RequestParam String registerInfo,@RequestParam String registerType){

		IRegisterService service=registerService.getService(registerType.toUpperCase());
		ResultBean bean=service.register(registerInfo);

		return bean;
	}


	@RequestMapping("/register/v2")
	@ResponseBody
	public ResultBean registerV2(@RequestParam String registerInfo,@RequestParam String registerType,HttpServletRequest request) throws IOException, ServletException {

		ResultBean bean=null;
		if(ServletFileUpload.isMultipartContent(request)){
			Collection<Part> parts=request.getParts();
			for(Part part:parts){
				if("file".equals(part.getName())){
					try {
						String url=FastDFSClient.upload(IOUtils.toByteArray(part.getInputStream()), FileSubfix.JPG, null);
						User user=JSON.parseObject(registerInfo, User.class);
						user.setIcon(FastDFSClient.getFullURL(url));
						registerInfo=JSON.toJSONString(user);
					} catch (Exception e) {
						logger.info("[UPLOAD_ICON_FAILED]",e);
						bean=new ResultBean();
						bean.setStatusCode(StatusCode.FAILED);
						bean.setMsg("上传头像失败");
						return bean;
					}
				}
			}
		}
		IRegisterService service=registerService.getService(registerType.toUpperCase());
		if(service==null){
			ResultBean.FAIL("注册类型错误");
		}else{
			bean=service.register(registerInfo);
		}

		
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


	@RequestMapping("/set/info")
	@ResponseBody
	public ResultBean setUserInfo(@RequestParam String dwID,@RequestParam String info){
		User user;
		try{
			user=JSON.parseObject(info,User.class);
			user.setId(dwID);
		}catch (Exception e){
			ResultBean bean=new ResultBean();
			logger.debug("[ERROR_JSON] info#"+info);
			bean.setStatusCode(StatusCode.FAILED);
			bean.setMsg("格式错误");
			return  bean;
		}
		return userService.updateUserInfo(user);
	}

	@RequestMapping("/set/icon")
	@ResponseBody
	public ResultBean setUserInfo(MultipartFile file,@RequestParam String dwID){
		ResultBean bean=new ResultBean();
		if(file==null||file.isEmpty()){
			bean.setStatusCode(StatusCode.FAILED);
			bean.setMsg("请选中头像");
		}else{
			try {
				String url=FastDFSClient.upload(file.getBytes(), FileSubfix.JPG,null);
				User user=new User();
				user.setId(dwID);
				user.setIcon(HTTP+FDFS_DOMAIN+"/"+url);
				bean=userService.updateUserInfo(user);
			} catch (Exception e) {
				bean.setStatusCode(StatusCode.FAILED);
				bean.setMsg("上传失败");
			}
		}
		return bean;
	}

}
