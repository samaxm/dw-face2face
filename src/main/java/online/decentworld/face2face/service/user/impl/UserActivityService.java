package online.decentworld.face2face.service.user.impl;

import online.decentworld.charge.service.IOrderService;
import online.decentworld.charge.service.OrderReceipt;
import online.decentworld.charge.service.OrderType;
import online.decentworld.charge.service.PayChannel;
import online.decentworld.face2face.api.easemob.EasemobApiUtil;
import online.decentworld.face2face.common.AccountType;
import online.decentworld.face2face.common.StatusCode;
import online.decentworld.face2face.common.TokenType;
import online.decentworld.face2face.service.security.authority.IUserAuthorityService;
import online.decentworld.face2face.service.security.token.ITokenCheckService;
import online.decentworld.face2face.service.user.IUserActivityService;
import online.decentworld.rdb.entity.User;
import online.decentworld.rdb.mapper.UserMapper;
import online.decentworld.rpc.dto.api.ObjectResultBean;
import online.decentworld.rpc.dto.api.ResultBean;
import online.decentworld.tools.AES;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Sammax on 2016/9/23.
 */
@Component
public class UserActivityService implements IUserActivityService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ITokenCheckService tokenCheckService;
    @Autowired
    private EasemobApiUtil easemobApiUtil;
    @Autowired
    private IOrderService orderService;
    @Autowired
    private IUserAuthorityService authorityService;


    @Override
    public ResultBean login(String account, AccountType accountType, String password) {
        switch (accountType){
            case PHONENUM:
                User user=userMapper.selectByPhoneNum(account);
                if(user==null){
                    return ResultBean.FAIL("请注册后再登录！");
                }else if(user.getPassword().equals(AES.decode(password))){
                    return ObjectResultBean.SUCCESS(UserInfoService.UserFieldFilter(user));
                }else{
                    return ResultBean.FAIL("密码错误");
                }
        }
        return ResultBean.FAIL("未支持该登录方式");
    }

    @Override
    public ResultBean recharge(String dwID, PayChannel channel, int amount, String password,String ip) {
        if(authorityService.checkPayPassword(dwID,password)){
            try {
                OrderReceipt receipt=orderService.createOrder(channel, amount, dwID, OrderType.RECHARGE, null, ip);
                return ObjectResultBean.SUCCESS(receipt.getRequestData());
            } catch (Exception e) {
                return ResultBean.FAIL("创建订单失败，请重试!");
            }
        }else{
            return ResultBean.FAIL("支付密码错误!");
        }

    }



    @Override
    public ResultBean resetPassword(String phoneNum,String token, String password) throws Exception {
        ResultBean bean=new ResultBean();
        password= AES.decode(password);
        if(tokenCheckService.checkToken(phoneNum, TokenType.CHANGEPWD, token)){
            userMapper.resetPassword(phoneNum,password);
            User user=userMapper.selectByPhoneNum(phoneNum);
            easemobApiUtil.resetPassword(user.getId(),password);
            bean.setStatusCode(StatusCode.SUCCESS);
        }else{
            bean.setStatusCode(StatusCode.FAILED);
            bean.setMsg("驗證碼錯誤,请重新获取验证码");
        }
        return bean;
    }
}
