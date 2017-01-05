package online.decentworld.face2face.controller;

import com.alibaba.fastjson.JSON;
import online.decentworld.charge.service.TransferAccountType;
import online.decentworld.face2face.common.FileSubfix;
import online.decentworld.face2face.service.user.IUserInfoService;
import online.decentworld.face2face.tools.FastDFSClient;
import online.decentworld.rdb.entity.BaseDisplayUserInfo;
import online.decentworld.rdb.entity.User;
import online.decentworld.rpc.dto.api.ObjectResultBean;
import online.decentworld.rpc.dto.api.ResultBean;
import online.decentworld.tools.AES;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.util.Collection;


@RequestMapping("/user")
@Controller
public class UserInfoController {

	@Autowired
	private IUserInfoService userService;
	private static Logger logger=LoggerFactory.getLogger(UserInfoController.class);

	@RequestMapping("/info")
	@ResponseBody
	public ResultBean getUserInfo(@RequestParam String dwID){
		BaseDisplayUserInfo info= userService.getUserInfo(dwID);
		if(info==null){
			return ResultBean.FAIL("用户不存在");
		}else{
			return ObjectResultBean.SUCCESS(info);
		}
	}

	@RequestMapping("/tags")
	@ResponseBody
	public ResultBean getUserTags(){
		return userService.getUserTags();
	}



	@RequestMapping("/bind/phone")
	@ResponseBody
	public ResultBean bindPhone(@RequestParam String dwID,@RequestParam String phoneNum,@RequestParam String code){
		return userService.bindUserPhoneNum(dwID, phoneNum, code);
	}

	@RequestMapping("/bind/account")
	@ResponseBody
	public ResultBean bindAccount(@RequestParam String dwID,@RequestParam String account,@RequestParam String accountType){
		try{
			TransferAccountType type=TransferAccountType.valueOf(accountType);
			return userService.bindAccount(dwID,type,account);
		}catch (Exception e){
			logger.warn("[BIND_ACCOUNT_FAILED] dwID#"+dwID+" account#"+account+" accountType#"+accountType,e);
			return ObjectResultBean.FAIL("绑定账户失败");
		}
	}

	@RequestMapping("/bind/jpush")
	@ResponseBody
	public ResultBean bindJpush(@RequestParam String dwID,@RequestParam String JpushID){
		try{
			return userService.bindJPush(dwID,JpushID);
		}catch (Exception e){
			return ObjectResultBean.FAIL("绑定账户失败");
		}
	}

	@RequestMapping("/set/worth")
	@ResponseBody
	public ResultBean setWorth(@RequestParam String dwID,@RequestParam String paypassword,@RequestParam int worth){
		if(worth<1){
			return ObjectResultBean.FAIL("身价必须大于1");
		}
		try{
			return userService.setWorth(dwID,paypassword,worth);
		}catch (Exception e){
			logger.warn("",e);
			return ObjectResultBean.FAIL("设置身价失败");
		}
	}


	@RequestMapping("/set/info")
	@ResponseBody
	public ResultBean setUserInfo(@RequestParam String password,@RequestParam String dwID,String info,HttpServletRequest request){

		ResultBean bean;
		User user;
		try{
			if(ServletFileUpload.isMultipartContent(request)){
				Collection<Part> parts=request.getParts();
				for(Part part:parts){
					if("file".equals(part.getName())){
						try {
							String url= FastDFSClient.upload(IOUtils.toByteArray(part.getInputStream()), FileSubfix.JPG, null);
							if(info!=null){
								user= JSON.parseObject(info, User.class);
							}else{
								user=new User();
							}
							user.setIcon(FastDFSClient.getFullURL(url));
							info=JSON.toJSONString(user);
						} catch (Exception e) {
							logger.info("[UPLOAD_ICON_FAILED]",e);
							bean=ResultBean.FAIL("上传头像失败");
							return bean;
						}
					}
				}
			}
			if(info!=null){
				user=JSON.parseObject(info,User.class);
				user.setId(dwID);
				user.setPassword(AES.decode(password));
				return userService.updateUserInfo(user);
			}else{
				return ResultBean.FAIL("未有修改信息");
			}

		}catch (Exception e){
			bean=ResultBean.FAIL("格式错误");
			logger.debug("[ERROR_JSON] info#"+info,e);
			return  bean;
		}
	}

//	@RequestMapping("/set/icon")
//	@ResponseBody
//	public ResultBean setUserInfo(MultipartFile file,@RequestParam String dwID){
//		ResultBean bean=new ResultBean();
//		if(file==null||file.isEmpty()){
//			bean.setStatusCode(StatusCode.FAILED);
//			bean.setMsg("请选中头像");
//		}else{
//			try {
//				String url=FastDFSClient.upload(file.getBytes(), FileSubfix.JPG,null);
//				User user=new User();
//				user.setId(dwID);
//				user.setIcon(HTTP+FDFS_DOMAIN+"/"+url);
//				bean=userService.updateUserInfo(user);
//			} catch (Exception e) {
//				bean.setStatusCode(StatusCode.FAILED);
//				bean.setMsg("上传失败");
//			}
//		}
//		return bean;
//	}

}
