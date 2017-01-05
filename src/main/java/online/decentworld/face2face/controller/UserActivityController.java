package online.decentworld.face2face.controller;

import online.decentworld.face2face.annotation.Frequency;
import online.decentworld.face2face.common.AccountType;
import online.decentworld.face2face.service.app.IAppService;
import online.decentworld.face2face.service.user.IUserActivityService;
import online.decentworld.rpc.dto.api.ResultBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Sammax on 2016/9/21.
 */
@RequestMapping("/user/activity")
@Controller
public class UserActivityController {

    @Autowired
    private IAppService appService;
    @Autowired
    private IUserActivityService userActivityService;

    private static Logger logger= LoggerFactory.getLogger(UserActivityController.class);

    @RequestMapping("/login")
    @ResponseBody
    public ResultBean login(@RequestParam String account,@RequestParam String password,@RequestParam String accountType){
        try {
            AccountType type=AccountType.valueOf(accountType.toUpperCase());
            return userActivityService.login(account,type,password);
        }catch (IllegalArgumentException e){
            logger.debug("[ERROR_ACCOUNT_TYPE] #"+accountType);
            return ResultBean.FAIL("不支持的登录类型");
        }
    }

    @RequestMapping("/login/v2")
    @ResponseBody
    public ResultBean loginV2(@RequestParam String account,@RequestParam String password,@RequestParam String accountType){
        try {
            AccountType type=AccountType.valueOf(accountType.toUpperCase());
            return userActivityService.loginWithVipInfo(account,type,password);
        }catch (IllegalArgumentException e){
            logger.debug("[ERROR_ACCOUNT_TYPE] #"+accountType);
            return ResultBean.FAIL("不支持的登录类型");
        }
    }



    @RequestMapping("/logout")
    @ResponseBody
    public ResultBean logout(String user){
        return null;
    }

    @RequestMapping("/ping")
    @ResponseBody
    @Frequency(time=5*60*1000,limit =20)
    public ResultBean markOnline(String dwID,String info){
        return appService.markOnline(dwID);
    }
}
