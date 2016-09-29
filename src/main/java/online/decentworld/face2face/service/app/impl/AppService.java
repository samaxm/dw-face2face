package online.decentworld.face2face.service.app.impl;

import online.decentworld.charge.ChargeService;
import online.decentworld.charge.event.RechargeEvent;
import online.decentworld.charge.exception.IllegalChargeException;
import online.decentworld.charge.service.PayChannel;
import online.decentworld.face2face.cache.ApplicationInfoCache;
import online.decentworld.face2face.common.StatusCode;
import online.decentworld.face2face.service.app.IAppService;
import online.decentworld.face2face.service.app.OnlineStatusDB;
import online.decentworld.rdb.entity.AppVersion;
import online.decentworld.rdb.entity.Order;
import online.decentworld.rdb.mapper.AppVersionMapper;
import online.decentworld.rdb.mapper.OrderMapper;
import online.decentworld.rpc.dto.api.MapResultBean;
import online.decentworld.rpc.dto.api.ObjectResultBean;
import online.decentworld.rpc.dto.api.ResultBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Timer;
import java.util.TimerTask;

@Service
public class AppService implements IAppService{

	@Autowired
	private AppVersionMapper versionMapper;
	@Autowired
	private ApplicationInfoCache applicationInfoCache;
	@Autowired
	private OrderMapper orderMapper;
	@Autowired
	private ChargeService chargeService;


	private static Logger logger= LoggerFactory.getLogger(AppService.class);
	private static int onlineCheckPeriod=60000;
	public AppService(){

	}

	@PostConstruct
	public void startTimer(){
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					long currentOnline=applicationInfoCache.checkOnline();
					OnlineStatusDB.storeCurrentOnlineNum(currentOnline);
				}catch (Exception e){
					logger.debug("",e);
				}

			}
		}, 10000, onlineCheckPeriod);
	}


	@Override
	public ObjectResultBean checkVersion(String type) {
		ObjectResultBean bean=new ObjectResultBean();
		bean.setStatusCode(StatusCode.SUCCESS);
		AppVersion version=versionMapper.selectByAppType(type);
		bean.setData(version);
		return bean;
	}


	public ResultBean markOnline(String dwID){
		applicationInfoCache.markOnline(dwID);
		return ResultBean.SUCCESS;
	}

	@Override
	public ResultBean getOnlineStatus(int count) {
		MapResultBean bean=new MapResultBean<>();
		bean.getData().put("maxOnline",applicationInfoCache.getMAX_ONLINE());
		bean.getData().put("statusList",OnlineStatusDB.getOnlineStatuses(count));
		bean.setStatusCode(StatusCode.SUCCESS);
		return  bean;
	}

	@Override
	public String getRechargeResponse(String orderNum, PayChannel channel, int amount) throws IllegalChargeException {
		if(channel==PayChannel.WX){
			Order order=orderMapper.selectByPrimaryKey(orderNum);
			if(order.getAmount()!=amount){
				logger.warn("[DIFFERENT_AMOUNT] orderNum#"+orderNum);
				return null;
			}
			chargeService.charge(new RechargeEvent(order));
			orderMapper.updateStatus(orderNum,true);
		}else if(channel==PayChannel.ALIPAY){

		}
		return null;
	}
}
