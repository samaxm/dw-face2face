package online.decentworld.face2face.service.history;

/**
 * Created by Sammax on 2016/10/5.
 */
public interface IMessageHistroyService {

    public byte[] getChatRecords(String fromID,String toID,int page);

}
