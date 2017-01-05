package online.decentworld.face2face.service.user.impl;

import online.decentworld.cache.redis.SessionCache;
import online.decentworld.face2face.common.AccountType;
import online.decentworld.face2face.service.user.IUserActivityService;
import online.decentworld.rdb.entity.BaseDisplayUserInfo;
import online.decentworld.rdb.entity.User;
import online.decentworld.rdb.entity.VipRecords;
import online.decentworld.rdb.mapper.UserMapper;
import online.decentworld.rdb.mapper.VipRecordsMapper;
import online.decentworld.rpc.dto.api.ObjectResultBean;
import online.decentworld.rpc.dto.api.ResultBean;
import online.decentworld.tools.AES;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Sammax on 2016/9/23.
 */
@Component
public class UserActivityService implements IUserActivityService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private VipRecordsMapper vipRecordsMapper;


    @Autowired
    private SessionCache sessionCache;

    @Override
    public ResultBean login(String account, AccountType accountType, String password) {
        User user=null;
        switch (accountType){
            case PHONENUM:
               user=userMapper.selectByPhoneNum(account);
                break;
            case THIRD_PARTY:
                user=userMapper.selectByUnionid(account);
                break;
            default:
                return ResultBean.FAIL("未支持该登录方式");
        }
        if(user==null){
            return ResultBean.FAIL("请注册后再登录！");
        }else if(user.getPassword().equals(AES.decode(password))){
            //cache user session info
            BaseDisplayUserInfo info=new BaseDisplayUserInfo(user);
            sessionCache.cacheSessionInfo(info);
            //protect password
            user.setPassword(null);
            user.setPayPassword(null);
            return ObjectResultBean.SUCCESS(user);
        }else{
            return ResultBean.FAIL("密码错误");
        }
    }

    @Override
    public ResultBean loginWithVipInfo(String account, AccountType accountType, String password) {
        User user=null;
        switch (accountType){
            case PHONENUM:
                user=userMapper.selectByPhoneNum(account);
                break;
            case THIRD_PARTY:
                user=userMapper.selectByUnionid(account);
                break;
            default:
                return ResultBean.FAIL("未支持该登录方式");
        }
        if(user==null){
            return ResultBean.FAIL("请注册后再登录！");
        }else if(user.getPassword().equals(AES.decode(password))){
            //cache user session info
            BaseDisplayUserInfo info=new BaseDisplayUserInfo(user);
            sessionCache.cacheSessionInfo(info);
            //protect password
            user.setPassword(null);
            user.setPayPassword(null);
            VipRecords records=vipRecordsMapper.selectByPrimaryKey(user.getId());
            UserInfoWithVipInfo infoWithVipInfo=new UserInfoWithVipInfo(user,records);
            return ObjectResultBean.SUCCESS(infoWithVipInfo);
        }else{
            return ResultBean.FAIL("密码错误");
        }
    }
}
