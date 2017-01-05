package online.decentworld.face2face.service.user.impl;

import online.decentworld.rdb.entity.User;
import online.decentworld.rdb.entity.VipRecords;

/**
 * Created by Sammax on 2016/12/2.
 */
public class UserInfoWithVipInfo {

    private User userInfo;

    private VipRecords vipInfo;

    public UserInfoWithVipInfo() {}

    public UserInfoWithVipInfo(User userInfo, VipRecords vipInfo) {
        this.userInfo = userInfo;
        this.vipInfo = vipInfo;
    }

    public User getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(User userInfo) {
        this.userInfo = userInfo;
    }

    public VipRecords getVipInfo() {
        return vipInfo;
    }

    public void setVipInfo(VipRecords vipInfo) {
        this.vipInfo = vipInfo;
    }
}
