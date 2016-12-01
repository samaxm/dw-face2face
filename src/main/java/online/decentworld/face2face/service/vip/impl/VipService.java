package online.decentworld.face2face.service.vip.impl;

import com.alibaba.fastjson.JSON;
import online.decentworld.charge.service.IOrderService;
import online.decentworld.charge.service.OrderReceipt;
import online.decentworld.charge.service.OrderType;
import online.decentworld.charge.service.PayChannel;
import online.decentworld.face2face.common.UserType;
import online.decentworld.face2face.service.user.IUserInfoService;
import online.decentworld.face2face.service.vip.IVipService;
import online.decentworld.face2face.tools.Jpush;
import online.decentworld.rdb.entity.Order;
import online.decentworld.rdb.entity.VipRecords;
import online.decentworld.rdb.mapper.UserMapper;
import online.decentworld.rdb.mapper.VipRecordsMapper;
import online.decentworld.rpc.dto.api.ObjectResultBean;
import online.decentworld.rpc.dto.api.ResultBean;
import online.decentworld.rpc.dto.message.MessageWrapper;
import online.decentworld.rpc.dto.message.Notice_VipRenewMessageBody;
import online.decentworld.rpc.dto.message.types.MessageType;
import online.decentworld.tools.DateFormater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Sammax on 2016/12/1.
 */
@Service
public class VipService implements IVipService {

    @Autowired
    private IOrderService orderService;
    @Autowired
    private VipRecordsMapper recordsMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private IUserInfoService userInfoService;
    @Autowired
    private Jpush jpush;



    //temp amount
    private static int VIP_FEE=10000;

    private static String VIP_NOTICE="大我VIP续费";

    private static Logger logger= LoggerFactory.getLogger(VipService.class);

    @Override
    public ResultBean getVipRecords(String dwID) {
        try {
            VipRecords record=recordsMapper.selectByPrimaryKey(dwID);
            return ObjectResultBean.SUCCESS(record);
        }catch (Exception e){
            logger.warn("[获取用户VIP信息失败]",e);
            return ResultBean.FAIL("获取用户VIP信息失败");
        }
    }

    @Override
    public ResultBean createVIPOrder(PayChannel payChannel, String dwID, String ip) {
        try {
            OrderReceipt receipt=orderService.createOrder(payChannel, VIP_FEE, dwID, OrderType.VIP_FEE, DateFormater.formatWX(new Date()), ip, VIP_NOTICE);
            return ObjectResultBean.SUCCESS(receipt);
        } catch (Exception e) {
            logger.warn("",e);
            return ResultBean.FAIL("创建续费订单失败");
        }
    }

    @Override
    public void receiveVIPOrderResponse(Order order) {
        VipRecords record=recordsMapper.selectByPrimaryKey(order.getDwid());
        Calendar calendar=Calendar.getInstance();
        int month=calendar.get(Calendar.MONTH);
        month=month+1;
        if(month>11){
            month=month-12;
            calendar.set(Calendar.YEAR,calendar.get(Calendar.YEAR)+1);
        }
        calendar.set(Calendar.MONTH,month);
        record.setExpire(calendar.getTime());
        record.setExp(record.getExp()+VipRecords.ONE_MONTH_EXP);
        //update vip record
        recordsMapper.updateByPrimaryKey(record);
        //clean cache
        userInfoService.cleanUserInfoCache(order.getDwid());
        //change user info
        userMapper.setUserType(order.getDwid(), UserType.STAR.name());
        //send message notice
        MessageWrapper wrapper=MessageWrapper.createMessageWrapper("SYSTEM",order.getDwid(),new Notice_VipRenewMessageBody(order.getDwid(),new Date(),record.getExpire(),VIP_FEE),0);
        jpush.pushMessage(order.getDwid(), MessageType.NOTICE_VIP_RENEW, JSON.toJSONString(wrapper));
    }
}
