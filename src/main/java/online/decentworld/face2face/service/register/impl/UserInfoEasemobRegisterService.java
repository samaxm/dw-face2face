package online.decentworld.face2face.service.register.impl;

import com.alibaba.fastjson.JSON;
import online.decentworld.face2face.common.CommonProperties;
import online.decentworld.face2face.common.UserType;
import online.decentworld.face2face.exception.RegisterFailException;
import online.decentworld.face2face.service.register.IRegisterService;
import online.decentworld.rdb.entity.User;
import online.decentworld.rdb.mapper.UserMapper;
import online.decentworld.rpc.dto.api.ObjectResultBean;
import online.decentworld.rpc.dto.api.ResultBean;
import online.decentworld.tools.IDUtil;
import online.decentworld.tools.MD5;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Sammax on 2016/9/9.
 */
@Service(value = "userInfoEasemobRegisterService")
public class UserInfoEasemobRegisterService extends SaveNewUserService implements IRegisterService {

    private static Logger logger= LoggerFactory.getLogger(UserInfoEasemobRegisterService.class);
    @Autowired
    private UserMapper userMapper;




    @Override
    public ResultBean register(String info) {
        try {
            User user = JSON.parseObject(info, User.class);
            String unionid=user.getUnionid();
            if(unionid==null){
                return ObjectResultBean.FAIL("信息不完整");
            }
            User isuser=userMapper.selectByUnionid(unionid);
            if(isuser!=null){
                return ObjectResultBean.SUCCESS(resetFields(isuser));
            }else{
                String password= MD5.GetMD5Code(unionid);
                user.setPassword(password);
                user.setId(IDUtil.getDWID());
                user.setType(UserType.UNCERTAIN.toString());
                user.setVersion(0);
                user.setWorth(CommonProperties.DEFAULT_WORTH);
                user.setInit(false);

                try {
                    return ObjectResultBean.SUCCESS(saveUser(user));
                }catch (RegisterFailException e){
                    return ObjectResultBean.FAIL(e.getMessage());
                }

            }
        }catch (Exception e){
            logger.debug("[PARSE_USER_INFO]",e);
            return ObjectResultBean.FAIL("用户信息格式错误");
        }
    }

}
