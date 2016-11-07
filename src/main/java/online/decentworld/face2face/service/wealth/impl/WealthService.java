package online.decentworld.face2face.service.wealth.impl;

import online.decentworld.charge.service.*;
import online.decentworld.face2face.service.wealth.IWealthService;
import online.decentworld.rdb.entity.TransferHistory;
import online.decentworld.rdb.entity.User;
import online.decentworld.rdb.entity.Wealth;
import online.decentworld.rdb.mapper.TransferHistoryMapper;
import online.decentworld.rdb.mapper.UserMapper;
import online.decentworld.rdb.mapper.WealthMapper;
import online.decentworld.rpc.dto.api.ObjectResultBean;
import online.decentworld.rpc.dto.api.ResultBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Sammax on 2016/10/7.
 */
@Component
public class WealthService implements IWealthService {
    @Autowired
    private IOrderService orderService;
    @Autowired
    private TransferHistoryMapper transferHistoryMapper;
    @Autowired
    private WealthMapper wealthMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public ResultBean getUserWealth(String dwID) {
        Wealth wealth=wealthMapper.selectByPrimaryKey(dwID);
        if(wealth!=null)
            return ObjectResultBean.SUCCESS(wealth.getWealth());
        else
            return ResultBean.FAIL("用户ID不存在");

    }

    @Override
    public ResultBean withdrawWealth(String dwID, String pay_password, int amount) {
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
                TransferHistory transferHistory=new TransferHistory(user.getId(),amount, TransferStatus.PROCESSING.name(),user.getAccount());
                transferHistoryMapper.insert(transferHistory);

            }

        }
        return null;
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
