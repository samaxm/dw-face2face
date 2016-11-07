package online.decentworld.face2face.controller;

import com.alibaba.fastjson.JSON;
import online.decentworld.charge.ChargeService;
import online.decentworld.charge.event.RechargeEvent;
import online.decentworld.charge.service.PayChannel;
import online.decentworld.charge.service.alipay.AlipayNotify;
import online.decentworld.charge.service.alipay.AlipayTradeStatus;
import online.decentworld.charge.service.wx.WXConfig;
import online.decentworld.charge.service.wx.WXPayResponseStatus;
import online.decentworld.charge.service.wx.WXSignUtil;
import online.decentworld.face2face.common.AttributeKey;
import online.decentworld.face2face.service.wealth.IWealthService;
import online.decentworld.rpc.dto.api.ObjectResultBean;
import online.decentworld.rpc.dto.api.ResultBean;
import online.decentworld.tools.IPHelper;
import online.decentworld.tools.LogUtil;
import online.decentworld.tools.MoneyUnitConverter;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Sammax on 2016/10/7.
 */
@RequestMapping("/wealth")
@Controller
public class WealthController {

    private static Logger logger= LoggerFactory.getLogger(WealthController.class);
    @Autowired
    private IWealthService wealthService;
    @Autowired
    private ChargeService chargeService;

    @RequestMapping("/recharge")
    @ResponseBody
    public ResultBean recharge(HttpServletRequest request,@RequestParam String dwID,@RequestParam int channel,@RequestParam int amount){
        try {
            PayChannel payChannel=PayChannel.getChannel(channel);
            String ip= IPHelper.getLocalIP(request);
            if(ip.equals("0:0:0:0:0:0:0:1")){
                ip="127.74.13.117";
            }
            return wealthService.recharge(dwID,payChannel,amount,ip);
        }catch (IllegalArgumentException e){
            logger.debug("[ERROR_PAY_CHANNEL] #"+channel);
            return ResultBean.FAIL("不支持的支付方式");
        }
    }

    @RequestMapping("/withdraw")
    @ResponseBody
    public ResultBean withdrawMoney(HttpServletRequest request,@RequestParam String dwID,@RequestParam String pay_password,@RequestParam int amount){
//        Boolean isValidate= (Boolean) request.getAttribute(AttributeKey.isValidate);
//        if(isValidate==null||!isValidate){
//            return ObjectResultBean.FAIL("invalidate key");
//        }

        logger.debug("[GET_USER_WEALTH] dwID#"+dwID);

        return wealthService.withdrawWealth(dwID,pay_password,amount);

    }


    @RequestMapping("")
    @ResponseBody
    public ResultBean getUserWealth(HttpServletRequest request){

        Boolean isValidate= (Boolean) request.getAttribute(AttributeKey.isValidate);
        if(isValidate==null||!isValidate){
            return ObjectResultBean.FAIL("invalidate key");
        }
        String dwID= (String) request.getAttribute("dwID");
        logger.debug("[GET_USER_WEALTH] dwID#"+dwID);
        return wealthService.getUserWealth(dwID);
    }



    @RequestMapping("/response/wx")
    @ResponseBody
    public String getWXRechargeResponse(HttpServletRequest request,HttpServletResponse response){
        logger.info("[GET_WX_RESPONSE]");
        Map<String, String> map = new HashMap<>();
        InputStream inputStream = null;
        try {
            inputStream = request.getInputStream();
            SAXReader reader = new SAXReader();
            Document document = reader.read(inputStream);
            Element root = document.getRootElement();
            List<Element> elementList = root.elements();
            for (Element e : elementList){
                map.put(e.getName(), e.getText());
            }
            logger.info(JSON.toJSONString(map));
            WXPayResponseStatus status=WXPayResponseStatus.valueOf(map.get("return_code"));
            if(status==WXPayResponseStatus.SUCCESS){
                String sign= WXSignUtil.sign(map);
                logger.info(sign);
                if(sign.equals(map.get("sign"))){
                    String orderNum=map.get("out_trade_no");
                    int amount=Integer.parseInt(map.get("total_fee"));
                    //微信单位为分
                    String dwID= map.get("attach");
                    chargeService.charge(new RechargeEvent(dwID, amount, orderNum));
                    return WXConfig.SUCCESS;
                }else{
                    logger.error("签名错误！ return_msg:"+map.get("return_msg"));
                }
            }else{
                logger.error("return_msg:"+map.get("return_msg"));
            }
            return null;
        } catch (Exception e) {
            logger.error("",e);
            return null;
        }finally{
            if(inputStream!=null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    logger.error("",e);
                }
            }
        }
    }

    @RequestMapping("/response/alipay")
    @ResponseBody
    public String getOrderResponse(HttpServletRequest request,HttpServletResponse response){

        logger.info("[GET_ALIPAY_RESPONSE]");
        try{
            Map<String,String> params=new HashMap<String, String>();
            Enumeration<String> parameterNames=request.getParameterNames();
            while(parameterNames.hasMoreElements()){
                String name=parameterNames.nextElement();
                String value=request.getParameter(name);
                params.put(name, value);
            }
            AlipayTradeStatus status=AlipayTradeStatus.valueOf(request.getParameter("trade_status"));
            logger.debug("[ORDER_RESPONSE_STATUS]  trade_status#"+status);
            if(status==AlipayTradeStatus.TRADE_SUCCESS){
                boolean verify= AlipayNotify.verify(params);
                if(verify){
                    String orderNum=params.get("out_trade_no");
                    String dwID=params.get("body");
                    //转成分
                    int amount= MoneyUnitConverter.fromYuantoFen(params.get("total_fee"));
                    logger.debug(" orderNum#"+orderNum+" dwID#"+dwID+" amount#"+amount+" params#"+ LogUtil.toLogString(params));
                    //HashMap<String,String> extra=JSON.parseObject(attach, HashMap.class);
                    try {
                        chargeService.charge(new RechargeEvent(dwID,amount,orderNum));
                    } catch (Exception e) {
                        logger.error("",e);
                        return "fail";
                    }
                    logger.info("getOrderResponse-------------->end--success");
                    return "success";
                }
            }
        }catch(Exception ex){
            logger.warn("",ex);
        }
        return null;
    }




}
