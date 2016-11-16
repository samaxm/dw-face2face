package online.decentworld.face2face.tools;

import online.decentworld.rdb.entity.BaseDisplayUserInfo;
import online.decentworld.rpc.dto.message.UserInfo;

/**
 * Created by Sammax on 2016/11/16.
 */
public class UserInforTransfer {

    public static UserInfo transfer(BaseDisplayUserInfo info){
        return new UserInfo(info.getDwID(),info.getSex(),info.getName(),info.getIcon(),info.getArea(),info.getSign(),info.getWorth(),info.getType());
    }
}
