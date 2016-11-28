package online.decentworld.face2face.service.register;

/**
 * Created by Sammax on 2016/9/21.
 */
public class PhoneCodeRegisterInfo {

    private String phoneNum;
    private String password;
    private String code;

    public PhoneCodeRegisterInfo(){}

    public PhoneCodeRegisterInfo(String phoneNum, String password, String code) {
        this.phoneNum = phoneNum;
        this.password = password;
        this.code = code;
    }

    public String getPhoneNum() {

        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
