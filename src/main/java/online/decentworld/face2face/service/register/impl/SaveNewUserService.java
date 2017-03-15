package online.decentworld.face2face.service.register.impl;

import online.decentworld.charge.service.TransferAccountType;
import online.decentworld.face2face.api.easemob.EasemobApiUtil;
import online.decentworld.face2face.common.CommonProperties;
import online.decentworld.face2face.common.RegisterChannel;
import online.decentworld.face2face.config.ConfigLoader;
import online.decentworld.face2face.exception.RegisterFailException;
import online.decentworld.face2face.service.search.ISearchService;
import online.decentworld.rdb.entity.User;
import online.decentworld.rdb.entity.Wealth;
import online.decentworld.rdb.mapper.UserMapper;
import online.decentworld.rdb.mapper.WealthMapper;
import online.decentworld.tools.IDUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;

/**
 * Created by Sammax on 2016/11/25.
 */
public class SaveNewUserService {

    private static Logger logger= LoggerFactory.getLogger(SaveNewUserService.class);
    @Autowired
    private EasemobApiUtil easemobAPI;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WealthMapper wealthMapper;
    @Autowired
    private ISearchService searchService;

    /*
        check and save new user in db,search server
        save wealth
        save new user in easemob
        return user saved without password and secrete info
     */
    protected User saveUser(User user) throws Exception {
        user.setWorth(1);
        user.setCanwithdraw((byte)0);
        if(RegisterChannel.WEIXIN.name().equals(user.getRegisterChannel())){
            user.setAccount(user.getOpenid());
            user.setAccountType(TransferAccountType.WXPAY.name());
        }
        if(checkField(user)){
            tryStoreUser(user);
            Wealth w=new Wealth();
            w.setDwid(user.getId());
            w.setWealth(0);
            if (Boolean.valueOf(ConfigLoader.AdminConfig.NEED_EASEMON)) {
                easemobAPI.registerEasemobUser(user.getId(), user.getPassword());
            }
            wealthMapper.insert(w);
            //添加至索引
            searchService.saveOrUpdateIndex(user);
            return resetFields(user);
        }else{
            throw new RegisterFailException("信息不全");
        }
    }


    private boolean checkField(User user){
        if(user.getPassword()==null){
            logger.debug("[BAD_FIELD] getPassword#"+user.getPassword());
            return false;
        }else  if(user.getName()!=null&&user.getName().length()>20){
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
        }else if(user.getRegisterChannel()==null){
            logger.debug("[BAD_FIELD] registerChannel");
            return false;
        }else if(user.getCanwithdraw()==null){
            logger.debug("[BAD_FIELD] can_withdraw");
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
                if(e instanceof DuplicateKeyException){
                    logger.info("[REPEAT_NAME]");
                    user.setName(user.getName()+attemp);
                    user.setId(IDUtil.getDWID());
                }else{
                    logger.debug("[SAVE_USER_FAILED]",e);
                    break;
                }

            }
            //.....不會這麼倒霉有20個重名的註冊吧
            if(attemp>20){
                throw new RegisterFailException("名称已被使用");
            }
        }
    }

    private User trimName(User info){
        if(info.getName().length()> CommonProperties.MAX_NAME_LENGTH){
            info.setName(info.getName().substring(info.getName().length()-CommonProperties.MAX_NAME_LENGTH,info.getName().length()));
        }
        return info;
    }

    protected User resetFields(User user){
        user.setPassword(null);
        user.setPayPassword(null);
        return user;
    }


}
