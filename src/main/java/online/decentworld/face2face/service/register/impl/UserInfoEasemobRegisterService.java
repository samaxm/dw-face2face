package online.decentworld.face2face.service.register.impl;

import com.alibaba.fastjson.JSON;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import online.decentworld.charge.service.TransferAccountType;
import online.decentworld.face2face.api.easemob.EasemobApiUtil;
import online.decentworld.face2face.common.CommonProperties;
import online.decentworld.face2face.common.RegisterChannel;
import online.decentworld.face2face.common.UserType;
import online.decentworld.face2face.config.ConfigLoader;
import online.decentworld.face2face.exception.RegisterFailException;
import online.decentworld.face2face.service.register.IRegisterService;
import online.decentworld.rdb.entity.User;
import online.decentworld.rdb.entity.Wealth;
import online.decentworld.rdb.mapper.UserMapper;
import online.decentworld.rdb.mapper.WealthMapper;
import online.decentworld.rpc.dto.api.ObjectResultBean;
import online.decentworld.rpc.dto.api.ResultBean;
import online.decentworld.tools.IDUtil;
import online.decentworld.tools.MD5;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

/**
 * Created by Sammax on 2016/9/9.
 */
@Service(value = "userInfoEasemobRegisterService")
public class UserInfoEasemobRegisterService implements IRegisterService {

    private static Logger logger= LoggerFactory.getLogger(UserInfoEasemobRegisterService.class);
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private EasemobApiUtil easemobAPI;
    @Autowired
    private WealthMapper wealthMapper;




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
                if(RegisterChannel.WEIXIN.name().equals(user.getRegisterChannel())){
                    user.setAccount(user.getOpenid());
                    user.setAccountType(TransferAccountType.WXPAY.name());
                }
                Wealth w=new Wealth();
                w.setDwid(user.getId());
                w.setWealth(0);
                if(checkField(user)) {
                    tryStoreUser(user);
                    if (Boolean.valueOf(ConfigLoader.AdminConfig.NEED_EASEMON)) {
                        easemobAPI.registerEasemobUser(user.getId(), user.getPassword());
                    }
                    wealthMapper.insert(w);

                    return ObjectResultBean.SUCCESS(resetFields(user));
                }else{
                    return ObjectResultBean.FAIL("注册信息缺失");
                }
            }
        }catch (Exception e){
            logger.debug("[PARSE_USER_INFO]",e);
            return ObjectResultBean.FAIL("用户信息格式错误");
        }
    }


    private boolean checkField(User user){
        if(user.getPassword()==null){
            logger.debug("[BAD_FIELD] getPassword#"+user.getPassword());
            return false;
        }else  if(user.getName()==null||user.getName().length()>20){
            logger.debug("[BAD_FIELD] getName#"+user.getName());
            return false;

        }else if(user.getWorth()==null){
            logger.debug("[BAD_FIELD] getWorth#"+user.getWorth());
            return false;
        }else if(user.getId()==null){
            logger.debug("[BAD_FIELD] getId#"+user.getId());
            return false;
        }else if(user.getType()==null){
            logger.debug("[BAD_FIELD] getType#"+user.getType());
            return false;
        }
        return true;
    }

    private void tryStoreUser(User user) throws RegisterFailException {
        int attemp=0;
        while(true){
            attemp++;
            user=trimName(user);
            try{
                userMapper.insert(user);
                break;
            }catch(Exception e){
                if(e instanceof MySQLIntegrityConstraintViolationException||
                        e instanceof DuplicateKeyException){
                    logger.info("[REPEAT_NAME]");
                    user.setName(user.getName()+attemp);
                    user.setId(IDUtil.getDWID());
                }

            }
            //.....不會這麼倒霉有20個重名的註冊吧
            if(attemp>20){
                throw new RegisterFailException();
            }
        }
    }

    private User trimName(User info){
        if(info.getName().length()>CommonProperties.MAX_NAME_LENGTH){
            info.setName(info.getName().substring(info.getName().length()-CommonProperties.MAX_NAME_LENGTH,info.getName().length()));
        }
        return info;
    }

    private User resetFields(User user){
        user.setPassword(null);
        return user;
    }

    public static void main(String[] args) {
        UserInfoEasemobRegisterService info=new UserInfoEasemobRegisterService();
        User user=new User();
        user.setName("1234567890asdfgvbasd1234567890");
        info.trimName(user);
        System.out.println(user.getName());
    }
}
