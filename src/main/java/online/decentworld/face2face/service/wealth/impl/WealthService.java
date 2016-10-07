package online.decentworld.face2face.service.wealth.impl;

import online.decentworld.charge.service.IOrderService;
import online.decentworld.charge.service.OrderReceipt;
import online.decentworld.charge.service.OrderType;
import online.decentworld.charge.service.PayChannel;
import online.decentworld.face2face.service.security.authority.IUserAuthorityService;
import online.decentworld.face2face.service.wealth.IWealthService;
import online.decentworld.rdb.entity.Wealth;
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
    private IUserAuthorityService authorityService;
    @Autowired
    private WealthMapper wealthMapper;

    @Override
    public ResultBean getUserWealth(String dwID,String token) {
        if(authorityService.checkToken(dwID,token)){
            Wealth wealth=wealthMapper.selectByPrimaryKey(dwID);
            if(wealth!=null)
                return ObjectResultBean.SUCCESS(wealth.getWealth());
            else
                return ResultBean.FAIL("用户ID不存在");
        }else {
            return ResultBean.FAIL("非法请求");
        }

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
