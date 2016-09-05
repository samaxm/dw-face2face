package online.decentworld.face2face.service.app.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import online.decentworld.face2face.common.StatusCode;
import online.decentworld.face2face.service.app.IAppService;
import online.decentworld.rdb.entity.AppVersion;
import online.decentworld.rdb.mapper.AppVersionMapper;
import online.decentworld.rpc.dto.api.ObjectResultBean;
@Service
public class AppService implements IAppService{

	@Autowired
	private AppVersionMapper versionMapper;
	
	@Override
	public ObjectResultBean checkVersion(String type) {
		ObjectResultBean bean=new ObjectResultBean();
		bean.setStatusCode(StatusCode.SUCCESS);
		AppVersion version=versionMapper.selectByAppType(type);
		bean.setData(version);
		return bean;
	}

}
