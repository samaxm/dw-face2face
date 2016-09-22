package online.decentworld.face2face.service.app.impl;

import online.decentworld.face2face.cache.ApplicationInfoCache;
import online.decentworld.face2face.common.StatusCode;
import online.decentworld.face2face.service.app.IAppService;
import online.decentworld.face2face.service.app.OnlineStatusDB;
import online.decentworld.rdb.entity.AppVersion;
import online.decentworld.rdb.mapper.AppVersionMapper;
import online.decentworld.rpc.dto.api.MapResultBean;
import online.decentworld.rpc.dto.api.ObjectResultBean;
import online.decentworld.rpc.dto.api.ResultBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Timer;
import java.util.TimerTask;

@Service
public class AppService implements IAppService{

	@Autowired
	private AppVersionMapper versionMapper;
	@Autowired
	private ApplicationInfoCache applicationInfoCache;

	private static int onlineCheckPeriod=60000;
	public AppService(){
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				long currentOnline=applicationInfoCache.checkOnline();
				OnlineStatusDB.storeCurrentOnlineNum(currentOnline);
			}
		}, 1000, onlineCheckPeriod);
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
}
