package online.decentworld.face2face.service.register;

import online.decentworld.charge.service.PayChannel;
import online.decentworld.rdb.entity.Order;
import online.decentworld.rpc.dto.api.ResultBean;

/**
 * Created by Sammax on 2016/11/28.
 */
public interface IVipRegisterCheckService
{

    public ResultBean checkVipCode(String code);
    public ResultBean createVipOrder(PayChannel payChannel,String dwID,String ip,String vipCode);
    public void getVipRegisterOrderResponse(Order order);
}
