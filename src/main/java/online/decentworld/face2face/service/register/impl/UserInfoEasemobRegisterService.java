package online.decentworld.face2face.service.register.impl;

import com.alibaba.fastjson.JSON;
import online.decentworld.face2face.api.easemob.EasemobApiUtil;
import online.decentworld.face2face.common.CommonProperties;
import online.decentworld.face2face.common.StatusCode;
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
import online.decentworld.tools.AES;
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
        ObjectResultBean bean=new ObjectResultBean();
        try {
            User user = JSON.parseObject(info, User.class);
            String password = null;
            if(user.getPassword()==null){
                //若用户未设置密码，为第三方登录，使用第三方openid作为临时密码
                String unionid=user.getUnionid();
                if(unionid!=null){
                    //先校验用户是否已经注册
                    User isuser=userMapper.selectByUnionid(unionid);
                    if(isuser!=null){
                        bean.setStatusCode(StatusCode.SUCCESS);
                        bean.setData(resetFields(isuser));
                        return bean;
                    }
                    password= MD5.GetMD5Code(unionid);
                }
            }else{
                password=MD5.GetMD5Code(AES.decode(user.getPassword()));
            }
            user.setPassword(password);
            user.setId(IDUtil.getDWID());
            user.setType(UserType.UNCERTAIN.toString());
            user.setVersion(0);
            user.setWorth(CommonProperties.DEFAULT_WORTH);

            Wealth w=new Wealth();
            w.setDwid(user.getId());
            w.setWealth(0);
            wealthMapper.insert(w);

            if(checkField(user)){
                tryStoreUser(user);
                if(Boolean.valueOf(ConfigLoader.AdminConfig.NEED_EASEMON)){
                    easemobAPI.registerEasemobUser(user.getId(),user.getPassword());
                }
                bean.setStatusCode(StatusCode.SUCCESS);
                bean.setData(resetFields(user));
            }else{
                bean.setStatusCode(StatusCode.FAILED);
                bean.setMsg("注册信息缺失");
            }
        }catch (Exception e){
            logger.debug("[PARSE_USER_INFO]",e);
            bean.setStatusCode(StatusCode.FAILED);
            bean.setMsg("用户信息格式错误");
        }
        return bean;
    }


    private boolean checkField(User user){
        if(user.getPassword()==null){
            logger.debug("[BAD_FIELD] getPassword#"+user.getPassword());
            return false;
        }else  if(user.getName()==null||user.getName().length()>20){
            logger.debug("[BAD_FIELD] getName#"+user.getName());
            return false;

        }else if(user.getSex()==null||(user.getSex()!=1&&user.getSex()!=2)){
            logger.debug("[BAD_FIELD] getSex#"+user.getSex());
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
            }catch(DuplicateKeyException e){
                logger.info("[REPEAT_NAME]");
                user.setName(user.getName()+attemp);
                user.setId(IDUtil.getDWID());
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
