package online.decentworld.face2face.service.app;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Sammax on 2016/9/22.
 */
public class OnlineStatusDB {
    /**
     * only story 24h data
     */
    private static int STORE_LIMIT=24*60;

    private static LinkedList<OnlineStatus> onlineStatuses=new LinkedList<>();


    public static synchronized void storeCurrentOnlineNum(long onlineNum){
        if(onlineStatuses.size()==STORE_LIMIT){
            onlineStatuses.remove(0);
        }
        onlineStatuses.add(new OnlineStatus(onlineNum));
    }

    public static synchronized List<OnlineStatus> getOnlineStatuses(int count){
        if(count>onlineStatuses.size()){
            count=onlineStatuses.size();
        }
        ArrayList<OnlineStatus> copy=new ArrayList<>(count);
        onlineStatuses.subList(onlineStatuses.size()-count,onlineStatuses.size()).forEach((OnlineStatus status)->{
            copy.add(status.clone());
        });
        return copy;
    }

    public static void main(String[] args) {
        OnlineStatusDB.storeCurrentOnlineNum(2);
        OnlineStatusDB.storeCurrentOnlineNum(3);
        OnlineStatusDB.storeCurrentOnlineNum(4);
        OnlineStatusDB.storeCurrentOnlineNum(5);
        List<OnlineStatus> list=OnlineStatusDB.getOnlineStatuses(10);

        list.forEach((OnlineStatus status)->{
            System.out.println(status.getOnlineNum());
            status.setOnlineNum(0);
        });
        List<OnlineStatus> list2=OnlineStatusDB.getOnlineStatuses(2);
        list2.forEach((OnlineStatus status)->{
            System.out.println(status.getOnlineNum());
        });

    }
}
