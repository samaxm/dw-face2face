//package online.decentworld.face2face.service.history.impl;
//
//import online.decentworld.face2face.service.history.IMessageHistroyService;
//import online.decentworld.rdb.entity.ChatIndex;
//import online.decentworld.rdb.entity.ChatRecord;
//import online.decentworld.rdb.mapper.ChatIndexMapper;
//import online.decentworld.rdb.mapper.ChatRecordMapper;
//import online.decentworld.rpc.codc.CodecHelper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by Sammax on 2016/10/5.
// */
//@Service
//public class MessageHistroyService implements IMessageHistroyService {
//
//    @Autowired
//    private ChatIndexMapper indexMapper;
//    @Autowired
//    private ChatRecordMapper recordMapper;
//
//
//    @Override
//    public byte[] getHistroyMessage(String dwID, String contactID) {
//        List<ChatIndex> indexes= indexMapper.getUserChats(dwID, contactID);
//        List<Long> ids=new ArrayList<>(indexes.size());
//        indexes.forEach((ChatIndex index)->{
//            ids.add(index.getChatRecordId());
//        });
//        List<ChatRecord> records=recordMapper.searchRecordsByID(ids);
//        List<byte[]> msgs=new ArrayList<>(ids.size());
//        records.forEach((ChatRecord record)->{
//            msgs.add(record.getData());
//        });
//
//        return CodecHelper.toByteArray(msgs,"SYSTEM",dwID);
//    }
//}
