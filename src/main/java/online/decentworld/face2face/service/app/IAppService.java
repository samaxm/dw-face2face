package online.decentworld.face2face.service.app;

import online.decentworld.charge.exception.IllegalChargeException;
import online.decentworld.charge.service.PayChannel;
import online.decentworld.rpc.dto.api.MapResultBean;
import online.decentworld.rpc.dto.api.ObjectResultBean;
import online.decentworld.rpc.dto.api.ResultBean;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface IAppService {
	public ObjectResultBean checkVersion(String type);

	public ResultBean markOnline(String dwID);

	public MapResultBean getOnlineUsers(int page);

	public ResultBean getOnlineStatus(int count);
	@Transactional(isolation = Isolation.SERIALIZABLE,propagation = Propagation.REQUIRED)
	public String getRechargeResponse(String orderNum,PayChannel channel,int amount) throws IllegalChargeException;

}
