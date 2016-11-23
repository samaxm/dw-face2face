package online.decentworld.face2face.service.history.impl;

import online.decentworld.face2face.service.history.IMessageHistroyService;
import online.decentworld.rdb.hbase.HTableHelper;
import online.decentworld.rdb.hbase.HbaseClient;
import online.decentworld.rpc.codc.Codec;
import online.decentworld.rpc.dto.message.MessageWrapper;
import online.decentworld.tools.HbaseRowKeyHelper;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Sammax on 2016/11/22.
 */
@Service
public class HbaseMessageHistoryService implements IMessageHistroyService {

    private static Logger logger= LoggerFactory.getLogger(HbaseMessageHistoryService.class);

    @Autowired
    private Codec codec;

    /**
     * 要根据page来进行记录选择
     * hbase每个存储为 1 mont per page
     * @param fromID
     * @param toID
     * @param page
     * @return
     */
    @Override
    public byte[] getChatRecords(String fromID, String toID, int page) {
        Calendar calendar=Calendar.getInstance();
        int month=calendar.get(Calendar.MONTH);
        int year=calendar.get(Calendar.YEAR);
        month=month-page;
        while(month<0){
            month=month+12;
            year=year-1;
        }
        byte[] rowkey= HbaseRowKeyHelper.getChatHistoryRowkey(fromID,toID,year+""+month);
        HbaseClient client=HbaseClient.instance();
        byte[] data=null;
        try {
            Result result=client.get((HTableHelper.CHAT_PREFIX + year + month).getBytes(), rowkey);
            List<byte[]> records=new LinkedList<>();
            for(Cell cell:result.rawCells()){
                byte[] value= CellUtil.cloneValue(cell);
                records.add(value);
            }
            for(byte[] chat:records){
                try {
                    MessageWrapper wrapper=codec.decode(chat);
                    System.out.println(wrapper.getType().name());
                }catch (Exception e){

                }

            }

//            data=CodecHelper.toByteArray(records,"","");
        } catch (Exception e) {
            logger.warn("",e);
        }
        return data;
    }
}
