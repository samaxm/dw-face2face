package online.decentworld.face2face.service.app;

import online.decentworld.rpc.dto.api.ObjectResultBean;
import online.decentworld.rpc.dto.api.ResultBean;

public interface IAppService {
	public ObjectResultBean checkVersion(String type);

	public ResultBean markOnline(String dwID);

	public ResultBean getOnlineStatus(int count);

}
