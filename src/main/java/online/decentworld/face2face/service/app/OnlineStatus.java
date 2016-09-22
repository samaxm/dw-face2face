package online.decentworld.face2face.service.app;

/**
 * Created by Sammax on 2016/9/22.
 */
public class OnlineStatus implements  Cloneable{
    /**
     * current online number
     */
    private long onlineNum;
    /**
     * history max online number
     */
    private long time;

    public OnlineStatus(long onlineNum) {
        this.onlineNum = onlineNum;
        this.time=System.currentTimeMillis();
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getOnlineNum() {
        return onlineNum;
    }

    public void setOnlineNum(long onlineNum) {
        this.onlineNum = onlineNum;
    }

    public OnlineStatus(long onlineNum, long time) {
        this.onlineNum = onlineNum;
        this.time = time;
    }

    public OnlineStatus clone(){
        return new OnlineStatus(this.getOnlineNum(),this.getTime());
    }
}
