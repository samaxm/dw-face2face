package online.decentworld.face2face.service.wealth.impl;

import com.alibaba.fastjson.JSON;
import online.decentworld.charge.ChargeResultCode;
import online.decentworld.charge.ChargeService;
import online.decentworld.charge.event.RechargeEvent;
import online.decentworld.charge.event.TipEvent;
import online.decentworld.charge.event.TransferEvent;
import online.decentworld.charge.receipt.ChargeReceipt;
import online.decentworld.charge.receipt.PlainChargeReceipt;
import online.decentworld.charge.service.*;
import online.decentworld.face2face.service.security.authority.IUserAuthorityService;
import online.decentworld.face2face.service.user.IUserInfoService;
import online.decentworld.face2face.service.wealth.IWealthService;
import online.decentworld.face2face.tools.Jpush;
import online.decentworld.face2face.tools.UserInforTransfer;
import online.decentworld.rdb.entity.BaseDisplayUserInfo;
import online.decentworld.rdb.entity.User;
import online.decentworld.rdb.entity.Wealth;
import online.decentworld.rdb.mapper.UserMapper;
import online.decentworld.rdb.mapper.WealthMapper;
import online.decentworld.rpc.dto.api.MapResultBean;
import online.decentworld.rpc.dto.api.ObjectResultBean;
import online.decentworld.rpc.dto.api.ResultBean;
import online.decentworld.rpc.dto.api.StatusCode;
import online.decentworld.rpc.dto.message.MessageWrapper;
import online.decentworld.rpc.dto.message.Notice_RechargeMessageBody;
import online.decentworld.rpc.dto.message.Notice_TipMessageBody;
import online.decentworld.rpc.dto.message.types.MessageType;
import online.decentworld.tools.AES;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by Sammax on 2016/10/7.
 */
@Component
public class WealthService implements IWealthService {
    @Autowired
    private IOrderService orderService;
    @Autowired
    private WealthMapper wealthMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ChargeService chargeService;
    @Autowired
    private IUserAuthorityService userAuthorityService;
    @Autowired
    private Jpush jpush;
    @Autowired
    private IUserInfoService userInfoService;

    private static Logger logger= LoggerFactory.getLogger(WealthService.class);

    @Override
    public ResultBean getUserWealth(String dwID) {
        Wealth wealth=wealthMapper.selectByPrimaryKey(dwID);
        if(wealth!=null)
            return ObjectResultBean.SUCCESS(wealth.getWealth());
        else
            return ResultBean.FAIL("用户ID不存在");
    }


    @Override
    public ResultBean withdrawWealth(String dwID, String pay_password, int amount,String ip) {
        if(amount<=0){
            return ObjectResultBean.FAIL("请提取正整数金额");
        }else{
            User user=userMapper.selectByPrimaryKey(dwID);
            if(user==null){
                return ObjectResultBean.FAIL("用户不存在");
            }else if(user.getAccount()==null||user.getAccountType()==null){
                return ObjectResultBean.FAIL("为了您的账户安全,请先绑定收款账户");
            }else if(user.getPhone()==null||user.getPhone().length()==0){
                return ObjectResultBean.FAIL("为了您的账户安全,请先绑定手机号码");
            }else if(user.getPayPassword()==null){
                return ObjectResultBean.FAIL("为了您的账户安全,请先设置支付密码");
            }else{
                try {
                    pay_password = AES.decode(pay_password, userAuthorityService.getUserKey(dwID));
                }catch (Exception e){
                    return ResultBean.AUTH_FAIL;
                }
                if(!user.getPayPassword().equals(pay_password)){
                    return ObjectResultBean.FAIL("支付密码错误");
                }
                TransferAccountType type;
                try {
                    type=TransferAccountType.valueOf(user.getAccountType());
                }catch (Exception e){
                    return ObjectResultBean.FAIL("不支持的账户类型，请重新绑定");
                }
                try {
                    ChargeReceipt receipt=chargeService.charge(new TransferEvent(dwID, amount, user.getAccount(), type, ip));
                    if(receipt.getChargeResult().getStatusCode()== ChargeResultCode.SUCCESS){
                        return ResultBean.SUCCESS;
                    }else if(receipt.getChargeResult().getStatusCode()== ChargeResultCode.WEALTH_LACK){
                        return ObjectResultBean.FAIL("提现失败,余额不足");
                    }else{
                        return ObjectResultBean.FAIL("提现失败,请稍后再试");
                    }
                } catch (Exception e) {
                    logger.warn("",e);
                    return ObjectResultBean.FAIL("提款失败"+e.getMessage());
                }
            }

        }
    }

    @Override
    public ResultBean tip(String dwID, String pay_password,String tipedID, int amount) {
        TipEvent tipEvent=new TipEvent(dwID,tipedID,amount);
        try{
            if(userAuthorityService.checkPayPassword(dwID,pay_password)){
                PlainChargeReceipt receipt= (PlainChargeReceipt) chargeService.charge(tipEvent);
                int payerWealth=receipt.getChargeResult().getPayerWealth();
                try {
                    //push message
                    BaseDisplayUserInfo info=userInfoService.getUserInfo(dwID);
                    Notice_TipMessageBody tipMessageBody=new Notice_TipMessageBody(UserInforTransfer.transfer(info),amount,new Date());
                    MessageWrapper messageWrapper=MessageWrapper.createMessageWrapper(dwID,tipedID,tipMessageBody,0);
                    jpush.pushMessage(tipedID, MessageType.NOTICE_TIP, JSON.toJSONString(messageWrapper));
                }catch (Exception e){
                    logger.warn("[PUSH_TIP_NOTICE_FAIL] dwID#"+dwID+" tipedID#"+tipedID+" amount#"+amount,e);
                }
                MapResultBean<String,Integer> resultBean=new MapResultBean<>();
                resultBean.getData().put("wealth",payerWealth);
                resultBean.setStatusCode(StatusCode.SUCCESS);
                return resultBean;
            }else{
                return ResultBean.FAIL("支付密码错误");
            }

        }catch (Exception e){
            logger.warn("[TIP_FAIL] dwID#"+dwID+" tipedID#"+tipedID+" amount#"+amount,e);
            return ObjectResultBean.FAIL("打赏失败，请稍后再试");
        }
    }

    @Override
    public ResultBean getRechargeResponse(String dwID, String orderNum, int amount) throws Exception {

        chargeService.charge(new RechargeEvent(dwID,amount,orderNum));
        try {
            //push message
            Notice_RechargeMessageBody rechargeMessageBody=new Notice_RechargeMessageBody(dwID,amount);
            MessageWrapper messageWrapper=MessageWrapper.createMessageWrapper("",dwID,rechargeMessageBody,0);
            jpush.pushMessage(dwID, MessageType.NOTICE_RECHARGE, JSON.toJSONString(messageWrapper));
        }catch (Exception e){
            logger.warn("[PUSH_RECHARGE_NOTICE_FAIL] dwID#"+dwID+" orderNum#"+orderNum+" amount#"+amount,e);
        }
        return ResultBean.SUCCESS;
    }


    @Override
    public ResultBean recharge(String dwID, PayChannel channel, int amount,String ip) {
        try {
            OrderReceipt receipt=orderService.createOrder(channel, amount, dwID, OrderType.RECHARGE, null, ip);
            return ObjectResultBean.SUCCESS(receipt.getRequestData());
        } catch (Exception e) {
            return ResultBean.FAIL("创建订单失败，请重试!");
        }
    }
}
