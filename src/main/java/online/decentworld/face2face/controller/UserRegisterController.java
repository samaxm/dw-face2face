package online.decentworld.face2face.controller;

import online.decentworld.face2face.common.PhoneCodeType;
import online.decentworld.face2face.service.register.IRegisterService;
import online.decentworld.face2face.service.register.RegisterStrategyFactory;
import online.decentworld.face2face.service.sms.SMSService;
import online.decentworld.rpc.dto.api.ResultBean;
import online.decentworld.tools.IDUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by Sammax on 2016/9/21.
 */
@RequestMapping("/user/register")
@Controller
public class UserRegisterController {

    @Autowired
    private RegisterStrategyFactory registerService;
    @Autowired
    private SMSService SMSservice;


    private static Logger logger= LoggerFactory.getLogger(UserRegisterController.class);


    @RequestMapping("/")
    @ResponseBody
    public ResultBean register(@RequestParam String registerInfo,@RequestParam String registerType){

        IRegisterService service=registerService.getService(registerType.toUpperCase());
        ResultBean bean=service.register(registerInfo);

        return bean;
    }

    @RequestMapping("/v2")
    @ResponseBody
    public ResultBean registerV2(@RequestParam String registerInfo,@RequestParam String registerType,HttpServletRequest request) throws IOException, ServletException {

        ResultBean bean=null;

        IRegisterService service=registerService.getService(registerType.toUpperCase());
        if(service==null){
            ResultBean.FAIL("注册类型错误");
        }else{
            bean=service.register(registerInfo);
        }
        return bean;
    }

    @RequestMapping("/send/code")
    @ResponseBody
    public ResultBean sendCode(@RequestParam String phoneNum){
        return SMSservice.sendPhoneCode(phoneNum, PhoneCodeType.BIND, IDUtil.createRandomCode());
    }

}
