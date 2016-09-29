package online.decentworld.face2face.service.user;

import online.decentworld.charge.service.PayChannel;
import online.decentworld.face2face.common.AccountType;
import online.decentworld.rpc.dto.api.ResultBean;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Sammax on 2016/9/23.
 */
public interface IUserActivityService {

    ResultBean login(String account,AccountType accountType,String password);
    ResultBean recharge(String dwID,PayChannel channel,int amount,String password,String ip);
    @Transactional
    ResultBean resetPassword(String phoneNum,String token,String password) throws Exception;
}
