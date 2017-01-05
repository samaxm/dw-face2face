package online.decentworld.face2face.service.app.impl;

import online.decentworld.face2face.service.app.ServiceCommand;

/**
 * Created by Sammax on 2016/12/6.
 */
public class IphoneOnlineStatusCommand implements ServiceCommand {

    private String token;
    private int versionNum;


    public int getVersionNum() {
        return versionNum;
    }

    public void setVersionNum(int versionNum) {
        this.versionNum = versionNum;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
