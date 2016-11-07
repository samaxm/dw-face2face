package online.decentworld.face2face.service.user.impl;

import online.decentworld.cache.redis.SessionCache;
import online.decentworld.face2face.api.easemob.EasemobApiUtil;
import online.decentworld.face2face.common.AccountType;
import online.decentworld.face2face.service.security.token.ITokenCheckService;
import online.decentworld.face2face.service.user.IUserActivityService;
import online.decentworld.rdb.entity.BaseDisplayUserInfo;
import online.decentworld.rdb.entity.User;
import online.decentworld.rdb.mapper.UserMapper;
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
    private ITokenCheckService tokenCheckService;
    @Autowired
    private EasemobApiUtil easemobApiUtil;


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
            return ObjectResultBean.SUCCESS(user);
        }else{
            return ResultBean.FAIL("密码错误");
        }
    }
}
