package online.decentworld.face2face.service.security;

import online.decentworld.face2face.cache.ApplicationInfoCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Sammax on 2016/9/21.
 */
@Service
public class StatisticsService {

    @Autowired
    private ApplicationInfoCache applicationInfoCache;

    public StatisticsService(){
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                applicationInfoCache.checkOnline();
            }
        }, 60000, 60000);
    }

    public void markOnline(String dwID){
        applicationInfoCache.markOnline(dwID);
    }




}
