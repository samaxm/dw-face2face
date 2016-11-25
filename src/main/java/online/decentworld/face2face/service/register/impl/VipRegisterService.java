package online.decentworld.face2face.service.register.impl;

import com.alibaba.fastjson.JSON;
import online.decentworld.charge.service.IOrderService;
import online.decentworld.charge.service.OrderReceipt;
import online.decentworld.charge.service.OrderType;
import online.decentworld.charge.service.PayChannel;
import online.decentworld.face2face.exception.RegisterFailException;
import online.decentworld.face2face.service.register.IRegisterService;
import online.decentworld.face2face.tools.Jpush;
import online.decentworld.rdb.entity.Order;
import online.decentworld.rdb.entity.PartnerCode;
import online.decentworld.rdb.entity.User;
import online.decentworld.rdb.entity.VipRecords;
import online.decentworld.rdb.mapper.PartnerCodeMapper;
import online.decentworld.rdb.mapper.VipRecordsMapper;
import online.decentworld.rpc.dto.api.ObjectResultBean;
import online.decentworld.rpc.dto.api.ResultBean;
import online.decentworld.rpc.dto.message.MessageWrapper;
import online.decentworld.rpc.dto.message.Notice_VipRegisterMessageBody;
import online.decentworld.rpc.dto.message.types.MessageType;
import online.decentworld.tools.AES;
import online.decentworld.tools.IDUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Sammax on 2016/11/25.
 */
@Service(value = "vipRegisterService")
public class VipRegisterService extends SaveNewUserService implements IRegisterService {

    private static Logger logger= LoggerFactory.getLogger(VipRegisterService.class);
    @Autowired
    private PartnerCodeMapper partnerCodeMapper;
    @Autowired
    private IOrderService orderService;
    @Autowired
    private VipRecordsMapper vipRecordsMapper;
    @Autowired
    private Jpush jpush;

    //10yuan
    final private static int VIP_REGISTER_FEE=1000;
    final private static String REGISTER_NOTICE="大我VIP注册";

    @Override
    public ResultBean register(String info) {
        try {
            info=AES.decode(info);
        }catch (Exception e){
            logger.warn("",e);
            return ResultBean.FAIL("invalid key");
        }
        VipRegisterInfo vipRegisterInfo=JSON.parseObject(info,VipRegisterInfo.class);
        String code=vipRegisterInfo.getCode();
        if(code==null){
            return ResultBean.FAIL("无效的邀请码");
        }else{
            PartnerCode partnerCode=partnerCodeMapper.isCodeExist(code);
            if(partnerCode!=null&&partnerCode.getDwid()!=null&&!partnerCode.getStatus().equals(PartnerCode.Status.PAID.name())){
                String dwID= partnerCode.getDwid();
                User user=vipRegisterInfo.getUser();
                if(user.getId().equals(dwID)){
                    try {
                        User saveUser=saveUser(user);
                        if(!saveUser.getId().equals(user.getId())){
                            //ID change so change the related records;
                            partnerCode.setDwid(saveUser.getId());
                            partnerCodeMapper.updateByPrimaryKeySelective(partnerCode);
                            VipRecords records=vipRecordsMapper.selectByPrimaryKey(user.getId());
                            records.setDwid(saveUser.getId());
                            vipRecordsMapper.updateByPrimaryKeySelective(records);
                        }
                        return ObjectResultBean.SUCCESS(saveUser);
                    }catch (RegisterFailException e){
                        return ResultBean.FAIL(e.getMessage());
                    }catch (Exception ex){
                        return ResultBean.FAIL("注册失败请稍后再试");
                    }
                }else{
                    return ResultBean.FAIL("用户信息错误");
                }
            }else{
                return ResultBean.FAIL("无效的邀请码");
            }
        }
    }

    @Transactional
    public ResultBean checkVipCode(String code){
        try {
            code=AES.decode(code);
        }catch (Exception e){
            logger.warn("",e);
            return ResultBean.FAIL("invalid key");
        }
        PartnerCode partnerCode=partnerCodeMapper.isCodeExist(code);
        if(partnerCode!=null&&partnerCode.getDwid()==null&&!partnerCode.getStatus().equals(PartnerCode.Status.UNUSED.name())){
            String dwID= IDUtil.getDWID();
            partnerCode.setDwid(dwID);
            partnerCode.setStatus(PartnerCode.Status.CHECKED.name());
            partnerCodeMapper.updateByPrimaryKeySelective(partnerCode);
            return ObjectResultBean.SUCCESS(dwID);
        }else{
            return ResultBean.FAIL("无效的邀请码");
        }
    }

    public ResultBean createVipOrder(PayChannel payChannel,String dwID,String ip,String vipCode){
        try {
            PartnerCode code=partnerCodeMapper.isCodeExist(vipCode);
            if(code!=null&&code.getDwid().equals(dwID)&&code.getStatus().equals(PartnerCode.Status.CHECKED.name())){
                OrderReceipt receipt=orderService.createOrder(payChannel, VIP_REGISTER_FEE, dwID, OrderType.VIP_REGISTER, vipCode, ip, REGISTER_NOTICE);
                return ObjectResultBean.SUCCESS(receipt.getRequestData());
            }else{
                return ResultBean.FAIL("无效的邀请码");
            }
        }catch (Exception e){
            logger.warn("[VIP_ORDER_CREATE_FAIL] dwID#"+dwID,e);
            return ResultBean.FAIL("创建订单失败，请稍后再试");
        }
    }

    @Transactional
    public void getVipRegisterOrderResponse(Order order){
        if(order.getType()!=OrderType.VIP_REGISTER.getValue()){
            return;
        }

        PartnerCode code=partnerCodeMapper.isCodeExist(order.getExtra());
        if(!code.getDwid().equals(order.getDwid())||!code.getStatus().equals(PartnerCode.Status.CHECKED.name())){
            logger.warn("[Strange State] order#"+order.toString());
        }else{
            code.setStatus(PartnerCode.Status.PAID.name());
            //update code status
            partnerCodeMapper.updateByPrimaryKeySelective(code);
            VipRecords records=vipRecordsMapper.selectByPrimaryKey(code.getDwid());
            String recordKey=code.getDwid();
            if(records!=null){
                logger.warn("[Strange State] vipRecords#dwID"+code.getDwid());
                recordKey=code.getCode();
            }
            records=new VipRecords();
            records.setDwid(recordKey);
            Calendar calendar=Calendar.getInstance();
            int month=calendar.get(Calendar.MONTH)+1;
            month=month%11;
            calendar.set(Calendar.MONTH,month);
            records.setExpire(calendar.getTime());
            records.setExp(30*100);
            //create user vip record
            vipRecordsMapper.insert(records);
            MessageWrapper messageWrapper=MessageWrapper.createMessageWrapper("SYSTEM",order.getDwid(),new Notice_VipRegisterMessageBody(order.getDwid(),new Date()),0);
            jpush.pushMessage(order.getDwid(), MessageType.NOTICE_VIP_REGISTER,JSON.toJSONString(messageWrapper) );
        }
        //change order status
        orderService.receiverOrderSuccessResponse(order.getOrdernumer());
    }
}
