package online.decentworld.face2face.controller;

import online.decentworld.charge.service.PayChannel;
import online.decentworld.face2face.common.TokenType;
import online.decentworld.face2face.service.register.IRegisterService;
import online.decentworld.face2face.service.register.IVipRegisterCheckService;
import online.decentworld.face2face.service.register.RegisterStrategyFactory;
import online.decentworld.face2face.service.sms.SMSService;
import online.decentworld.rpc.dto.api.ResultBean;
import online.decentworld.tools.IDUtil;
import online.decentworld.tools.IPHelper;
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
public class RegisterController {

    @Autowired
    private RegisterStrategyFactory registerService;
    @Autowired
    private SMSService SMSservice;
    @Autowired
    private IVipRegisterCheckService vipRegisterCheckService;


    private static Logger logger= LoggerFactory.getLogger(RegisterController.class);

    @RequestMapping("/")
    @ResponseBody
    public ResultBean register(@RequestParam String registerInfo,@RequestParam String registerType){

        IRegisterService service=registerService.getService(registerType.toUpperCase());
        ResultBean bean=service.register(registerInfo);

        return bean;
    }

    @RequestMapping("/v2")
    @ResponseBody
    public ResultBean registerV2( String registerInfo,@RequestParam String registerType,HttpServletRequest request) throws IOException, ServletException {
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
        return SMSservice.sendPhoneCode(phoneNum, TokenType.BIND_PHONE, IDUtil.createRandomCode());
    }

    @RequestMapping("/vip/code")
    @ResponseBody
    public ResultBean checkVIPCode(@RequestParam String vipCode){
        return vipRegisterCheckService.checkVipCode(vipCode);
    }


    @RequestMapping("/vip/pay")
    @ResponseBody
     public ResultBean pay4Vip(HttpServletRequest request,@RequestParam String dwID,@RequestParam String vipCode,@RequestParam int channel){
        try {
            PayChannel payChannel=PayChannel.getChannel(channel);
            String ip= IPHelper.getLocalIP(request);
            if(ip.equals("0:0:0:0:0:0:0:1")){
                ip="127.74.13.117";
            }
            return vipRegisterCheckService.createVipOrder(payChannel, dwID, ip, vipCode);
        }catch (IllegalArgumentException e){
            logger.debug("[ERROR_PAY_CHANNEL] #"+channel);
            return ResultBean.FAIL("不支持的支付方式");
        }
    }




}
