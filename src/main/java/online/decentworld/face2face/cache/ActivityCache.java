package online.decentworld.face2face.cache;

import online.decentworld.cache.redis.RedisTemplate;
import online.decentworld.cache.redis.ReturnResult;
import online.decentworld.rdb.entity.ActivityAnswer;
import online.decentworld.rdb.mapper.ActivityAnswerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Sammax on 2017/1/6.
 */
@Component
public class ActivityCache extends RedisTemplate{

    @Autowired
    private ActivityAnswerMapper activityAnswerMapper;


    public Integer isAnswerRight(int activityID,String answer){
        ReturnResult result=cache(jedis->{
            Long rem=jedis.srem(WebCacheKey.ACTIVITY_ANSWER_SET(activityID),answer);
            if(rem!=null&&rem==1){
                String idString=jedis.hget(WebCacheKey.ACTIVITY_ANSWER_INDEX(activityID),answer);
                if(idString!=null){
                    return ReturnResult.result(Integer.parseInt(idString));
                }
            }
            return ReturnResult.FAIL;
        });
        return (Integer) result.getResult();
    }


    //由于可能存在并发问题导致缓存第一次写入时多个客户端重复写入，数据覆盖，想到最简单的方式就是运维提前将活动答案写入
    public boolean writeAnswerCache(int activityID){
        List<ActivityAnswer> answers=activityAnswerMapper.getActivitieAnswers(activityID);
        if(answers!=null){
            ReturnResult result=cache(Jedis -> {
                        answers.forEach(answer -> {
                            if (!answer.getChecked()) {
                                int id = answer.getId();
                                String data = answer.getAnswer();
                                Jedis.sadd(WebCacheKey.ACTIVITY_ANSWER_SET(activityID), data);
                                Jedis.hset(WebCacheKey.ACTIVITY_ANSWER_INDEX(activityID),data,String.valueOf(id));
                            }
                        });
                        return ReturnResult.SUCCESS;
                    }
            );
            return  result.isSuccess();
        }
        return true;
    }
}
