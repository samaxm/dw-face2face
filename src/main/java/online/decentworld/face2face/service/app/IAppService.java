package online.decentworld.face2face.service.app;

import online.decentworld.face2face.service.app.impl.IphoneOnlineStatusCommand;
import online.decentworld.rpc.dto.api.MapResultBean;
import online.decentworld.rpc.dto.api.ObjectResultBean;
import online.decentworld.rpc.dto.api.ResultBean;

public interface IAppService {
	public ObjectResultBean checkVersion(String type);

	public ResultBean markOnline(String dwID);

	public MapResultBean getOnlineUsers(int page);

	public ResultBean getOnlineStatus(String fromtime,String totime);

	public ResultBean getiphoneStatus(Integer versionNum);

	public ResultBean setiphoneStatus(IphoneOnlineStatusCommand command);

	public ResultBean getActivityList(long dateNum);

	public ResultBean checkActivityAnswer(int activityID,String answer,String dwID);
	//回答对暗号后上传地址
	public ResultBean uploadAdress(String dwID,int answerID,String address);
}
