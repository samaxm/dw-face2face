package online.decentworld.face2face.service.app;

import online.decentworld.face2face.service.app.impl.IphoneOnlineStatusCommand;
import online.decentworld.rdb.entity.Activity;
import online.decentworld.rpc.dto.api.MapResultBean;
import online.decentworld.rpc.dto.api.ObjectResultBean;
import online.decentworld.rpc.dto.api.ResultBean;

import java.util.List;

public interface IAppService {
	public ObjectResultBean checkVersion(String type);

	public ResultBean markOnline(String dwID);

	public MapResultBean getOnlineUsers(int page);

	public ResultBean getOnlineStatus(String fromtime,String totime);

	public ResultBean getiphoneStatus(Integer versionNum);

	public ResultBean setiphoneStatus(IphoneOnlineStatusCommand command);

	public ResultBean getActivityList(long dateNum);

}
