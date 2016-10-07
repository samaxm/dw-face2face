package online.decentworld.face2face.controller;

import online.decentworld.charge.service.PayChannel;
import online.decentworld.face2face.service.wealth.IWealthService;
import online.decentworld.rpc.dto.api.ResultBean;
import online.decentworld.tools.IPHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Sammax on 2016/10/7.
 */
@RequestMapping("/wealth")
@Controller
public class WealthController {

    private static Logger logger= LoggerFactory.getLogger(WealthController.class);
    @Autowired
    private IWealthService wealthService;


    @RequestMapping("/recharge")
    @ResponseBody
    public ResultBean recharge(HttpServletRequest request,@RequestParam String dwID,@RequestParam String channel,@RequestParam int amount){
        try {
            PayChannel payChannel=PayChannel.valueOf(channel);
            String ip= IPHelper.getLocalIP(request);
            return wealthService.recharge(dwID,payChannel,amount,ip);
        }catch (IllegalArgumentException e){
            logger.debug("[ERROR_PAY_CHANNEL] #"+channel);
            return ResultBean.FAIL("不支持的支付方式");
        }
    }

    @RequestMapping("")
    @ResponseBody
    public ResultBean getUserWealth(HttpServletRequest request){
        String dwID= (String) request.getAttribute("dwID");
        if(dwID==null){
            return ResultBean.FAIL("非法请求");
        }
        logger.debug("[GET_USER_WEALTH] dwID#"+dwID);
        return wealthService.getUserWealth(dwID);
    }
}
