package online.decentworld.face2face.controller;

import com.alibaba.fastjson.JSON;
import online.decentworld.charge.service.alipay.AlipayNotify;
import online.decentworld.charge.service.alipay.AlipayTradeStatus;
import online.decentworld.charge.service.wx.WXConfig;
import online.decentworld.charge.service.wx.WXPayResponseStatus;
import online.decentworld.charge.service.wx.WXSignUtil;
import online.decentworld.face2face.common.AppType;
import online.decentworld.face2face.common.StatusCode;
import online.decentworld.face2face.service.app.IAppService;
import online.decentworld.rpc.dto.api.ResultBean;
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


@RequestMapping("/app")
@Controller
public class AppController {

	@Autowired
	private IAppService appService;
	private static Logger logger= LoggerFactory.getLogger(AppController.class);

	@RequestMapping("/check/version")
	@ResponseBody
	public ResultBean sendRegisterPhoneCode(@RequestParam String appType){
		ResultBean bean=null;
		if(AppType.checkType(appType)){
			bean=appService.checkVersion(appType);
		}else{
			bean=new ResultBean();
			bean.setStatusCode(StatusCode.FAILED);
			bean.setMsg("类型错误");
		}
		return bean;
	}


	@RequestMapping("/onlineStatus")
	@ResponseBody
	public ResultBean getOnlineNum(int count){
		return appService.getOnlineStatus(count);
	}


	@RequestMapping("/response/wx")
	@ResponseBody
	public String getWXRechargeResponse(HttpServletRequest request,HttpServletResponse response){

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

		logger.info("getOrderResponse-------------->begin");
		try{
			Map<String,String> params=new HashMap<String, String>();
			Enumeration<String> parameterNames=request.getParameterNames();
			while(parameterNames.hasMoreElements()){
				String name=parameterNames.nextElement();
				String value=request.getParameter(name);
				params.put(name, value);
			}
			AlipayTradeStatus status=AlipayTradeStatus.valueOf(request.getParameter("trade_status"));
			logger.debug("【ORDER_RESPONSE_STATUS】  trade_status#"+status);
			if(status==AlipayTradeStatus.TRADE_SUCCESS){
				boolean verify= AlipayNotify.verify(params);
				if(verify){
					String orderNum=params.get("out_trade_no");
					String dwID=params.get("body");
					//转成分
					int amount= MoneyUnitConverter.fromYuantoFen(params.get("total_fee"));
					logger.debug(" orderNum#"+orderNum+" dwID#"+dwID+" amount#"+amount+" params#"+ LogUtil.toLogString(params));
					//				HashMap<String,String> extra=JSON.parseObject(attach, HashMap.class);
					try {
//						chargeService.checkAndUpdateOrder(orderNum, dwID, amount);
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
