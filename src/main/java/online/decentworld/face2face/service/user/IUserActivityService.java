package online.decentworld.face2face.service.user;

import online.decentworld.face2face.common.AccountType;
import online.decentworld.rpc.dto.api.ResultBean;

/**
 * Created by Sammax on 2016/9/23.
 */
public interface IUserActivityService {

    ResultBean login(String account,AccountType accountType,String password);

    ResultBean loginWithVipInfo(String account,AccountType accountType,String password);
}
