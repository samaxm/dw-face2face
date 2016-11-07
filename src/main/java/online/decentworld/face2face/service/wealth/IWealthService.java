package online.decentworld.face2face.service.wealth;

import online.decentworld.charge.service.PayChannel;
import online.decentworld.rpc.dto.api.ResultBean;

/**
 * Created by Sammax on 2016/10/7.
 */
public interface IWealthService {

    ResultBean recharge(String dwID,PayChannel channel,int amount,String ip);

    ResultBean getUserWealth(String dwID);

    ResultBean withdrawWealth(String dwID,String pay_password,int amount);

}
