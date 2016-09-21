package online.decentworld.face2face.controller;

import online.decentworld.face2face.annotation.Frequency;
import online.decentworld.face2face.service.security.StatisticsService;
import online.decentworld.rpc.dto.api.ResultBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Sammax on 2016/9/21.
 */
@RequestMapping("/user/activity")
@Controller
public class UserActivityController {


    @Autowired
    private StatisticsService statisticsService;


    @RequestMapping("/login")
    @ResponseBody
    public ResultBean login(String dwID,String password){
        return null;
    }


    @RequestMapping("/logout")
    @ResponseBody
    public ResultBean logout(String user){
        return null;
    }


    @RequestMapping("/ping")
    @ResponseBody
    @Frequency(time=5*60*1000,limit =2)
    public ResultBean markOnline(String dwID,String info){
        statisticsService.markOnline(dwID);
        return ResultBean.SUCCESS;
    }
}
