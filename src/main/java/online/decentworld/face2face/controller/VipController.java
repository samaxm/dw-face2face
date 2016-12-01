package online.decentworld.face2face.controller;

import online.decentworld.charge.service.PayChannel;
import online.decentworld.face2face.service.vip.IVipService;
import online.decentworld.rpc.dto.api.ResultBean;
import online.decentworld.tools.IPHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Sammax on 2016/12/1.
 */
@RequestMapping("/vip")
@Controller
public class VipController {

    @Autowired
    private IVipService vipService;


    @RequestMapping("/status")
    @ResponseBody
    public ResultBean getVipStatus(@RequestParam String dwID,String token){
        return vipService.getVipRecords(dwID);
    }


    @RequestMapping("/renew")
    @ResponseBody
    public ResultBean renewVip(@RequestParam Integer channel,String dwID,HttpServletRequest request){
        PayChannel payChannel=PayChannel.getChannel(channel);
        String ip= IPHelper.getLocalIP(request);
        if(ip.equals("0:0:0:0:0:0:0:1")){
            ip="127.74.13.117";
        }
        return vipService.createVIPOrder(payChannel,dwID,ip);
    }
}
