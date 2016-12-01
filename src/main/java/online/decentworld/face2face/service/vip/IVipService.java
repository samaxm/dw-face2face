package online.decentworld.face2face.service.vip;

import online.decentworld.charge.service.PayChannel;
import online.decentworld.rdb.entity.Order;
import online.decentworld.rpc.dto.api.ResultBean;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Sammax on 2016/12/1.
 */
public interface IVipService {

    public ResultBean getVipRecords(String dwID);

    public ResultBean createVIPOrder(PayChannel payChannel,String dwID,String ip);
    @Transactional
    public void receiveVIPOrderResponse(Order order);
}
