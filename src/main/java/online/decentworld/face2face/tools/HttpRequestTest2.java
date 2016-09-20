package online.decentworld.face2face.tools;

import okhttp3.*;
import online.decentworld.face2face.common.CommonProperties;
import online.decentworld.rpc.codc.protos.ReflectBodyConverterFactory;
import online.decentworld.rpc.codc.protos.SimpleProtosCodec;
import online.decentworld.rpc.dto.message.*;
import online.decentworld.rpc.dto.message.types.ChatMessageType;
import online.decentworld.rpc.dto.message.types.MessageType;
import online.decentworld.tools.AES;
import online.decentworld.tools.MD5;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
* Created by Sammax on 2016/9/6.
*/
public class HttpRequestTest2 {
    public static void main(String[] args) {
        OkHttpClient c= null;
        SimpleProtosCodec codec=new SimpleProtosCodec();
        codec.setConverterFactory(new ReflectBodyConverterFactory());
        c = new OkHttpClient.Builder().connectTimeout(0, TimeUnit.MILLISECONDS).writeTimeout(0, TimeUnit.MILLISECONDS).readTimeout(0, TimeUnit.MILLISECONDS).build();
        TextChatMessage msg=TextChatMessage.createTextMessage("123","456","test");
//        ProtosVedioLikeMessage msg=ProtosVedioLikeMessage.create(new ProtosVedioLikeMessageBody("123", "sam", "123", "123", "1", new Date()));
        byte[] data=codec.encode(MessageWrapper.wrapChatMessage(msg));
        RequestBody body= RequestBody.create(MediaType.parse(CommonProperties.MEDIA_MARKDOWN), data);
        MultipartBody.Part part=MultipartBody.Part.createFormData("MSG", "msg", body);
        String token=AES.encode(MD5.GetMD5Code(data));
        MultipartBody mb=new MultipartBody.Builder().addFormDataPart("TOKEN",token).addFormDataPart("TEMP_ID", "123").addFormDataPart("USER_ID", "123").addPart(part).build();
        int i=c.connectTimeoutMillis();
        System.out.println(i);
        Request request=new Request.Builder().url("http://localhost:8080/message/send").post(mb).build();
        RequestBody body1=new FormBody.Builder().add("syncNum","19").add("dwID","456").build();
        Request sync=new Request.Builder().url("http://localhost:8080/message/sync").post(body1).build();
        try {
            Response response=c.newCall(sync).execute();
            if(response.isSuccessful()){
                System.out.println("success receive");
                MessageWrapper message=codec.decode(IOUtils.toByteArray(response.body().byteStream()));
                System.out.println(message.getType());
                if(message.getType()== MessageType.WEALTH_ACK){
                    WealthAckMessage wealth=(WealthAckMessage)message.getBody();
                    System.out.println("wealth#" + wealth.getWealth() + " status#" + wealth.getStatus() + " relation#" + wealth.getRelation());
                }else if(message.getType()==MessageType.LIST){
                    MessageContainer container=(MessageContainer)message.getBody();
                    List<MessageWrapper> msgs=container.getMessages();
                    for(MessageWrapper m:msgs){
                        if(m.getType()==MessageType.CHAT){
                            ChatMessage cm=(ChatMessage)m.getBody();
                            System.out.println(cm.getType());
                            if(cm.getChatType()== ChatMessageType.TXT){
                                TextChatMessage tcm=(TextChatMessage)cm;
                                System.out.println("word#"+tcm.getText()+" id#"+tcm.getFromID());
                            }
                        }
                    }
                }
            }
//            System.out.println(r.message());
//            System.out.println(r.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
