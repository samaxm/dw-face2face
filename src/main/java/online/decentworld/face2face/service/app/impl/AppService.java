package online.decentworld.face2face.service.app.impl;

import online.decentworld.cache.redis.ApplicationInfoCache;
import online.decentworld.cache.redis.SessionCache;
import online.decentworld.charge.exception.IllegalChargeException;
import online.decentworld.charge.service.PayChannel;
import online.decentworld.face2face.common.StatusCode;
import online.decentworld.face2face.service.app.IAppService;
import online.decentworld.face2face.service.app.OnlineStatusDB;
import online.decentworld.face2face.tools.Jpush;
import online.decentworld.rdb.entity.AppVersion;
import online.decentworld.rdb.entity.BaseDisplayUserInfo;
import online.decentworld.rdb.mapper.AppVersionMapper;
import online.decentworld.rpc.dto.api.MapResultBean;
import online.decentworld.rpc.dto.api.ObjectResultBean;
import online.decentworld.rpc.dto.api.ResultBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@Service
public class AppService implements IAppService{

	@Autowired
	private AppVersionMapper versionMapper;
	@Autowired
	private ApplicationInfoCache applicationInfoCache;
	@Autowired
	private SessionCache sessionCache;
	@Autowired
	private Jpush jpush;


	private static Logger logger= LoggerFactory.getLogger(AppService.class);
	private static int onlineCheckPeriod=60000;
	public AppService(){

	}

	@PostConstruct
	public void startTimer(){
		new Timer().schedule(new TimerTask() {

			private boolean isZero=false;

			@Override
			public void run() {
				try {
					long currentOnline=applicationInfoCache.checkOnline();
					if(currentOnline==0){
						isZero=true;
					}else{
						if(isZero==true){
							//new user coming
							jpush.pushOnlineNotice();
							isZero=false;
						}
					}
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
	public MapResultBean getOnlineUsers(int page) {
		long onlineNum=applicationInfoCache.getOnlineNum();
		List<BaseDisplayUserInfo> list=sessionCache.getSessionInfos(page);
		MapResultBean bean=new MapResultBean();
		bean.getData().put("onlineNum",onlineNum);
		if(list==null||list.isEmpty()){
			bean.getData().put("userInfos",null);
		}else {
			bean.getData().put("userInfos", list);
		}
		return bean;
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

		return null;
	}
}
