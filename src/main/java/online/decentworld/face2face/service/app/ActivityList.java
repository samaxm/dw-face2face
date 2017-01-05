package online.decentworld.face2face.service.app;

import online.decentworld.rdb.entity.Activity;

import java.util.List;

/**
 * Created by Sammax on 2017/1/5.
 */
public class ActivityList {
    private long dateNum;
    private List<Activity> list;

    public long getDateNum() {
        return dateNum;
    }

    public void setDateNum(long dateNum) {
        this.dateNum = dateNum;
    }

    public List<Activity> getList() {
        return list;
    }

    public void setList(List<Activity> list) {
        this.list = list;
    }
}
