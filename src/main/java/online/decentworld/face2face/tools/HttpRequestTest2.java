//package online.decentworld.face2face.tools;
//
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.Response;
//import online.decentworld.rpc.codc.protos.ReflectBodyConverterFactory;
//import online.decentworld.rpc.codc.protos.SimpleProtosCodec;
//
//import java.io.IOException;
//import java.util.concurrent.TimeUnit;
//
///**
//* Created by Sammax on 2016/9/6.
//*/
//public class HttpRequestTest2 {
//    public static void main(String[] args) {
//        OkHttpClient c= null;
//        SimpleProtosCodec codec=new SimpleProtosCodec();
//        codec.setConverterFactory(new ReflectBodyConverterFactory());
//        c = new OkHttpClient.Builder().connectTimeout(0, TimeUnit.MILLISECONDS).writeTimeout(0, TimeUnit.MILLISECONDS).readTimeout(0, TimeUnit.MILLISECONDS).build();
//        Request request=new Request.Builder().url("http://localhost:8080/face2face/user/register/v2?registerInfo={%22code%22:%221083%22,%22password%22:%22806A8C066B0E48F7375DC77CAF688DF33BAF313BB3C4DFDB14CC47825D9A6ECEEDBB210C3554185ECBEFB5661EF9EE85%22,%22phoneNum%22:%2215013445816%22}&registerType=PHONECODE").get().build();
////        TextChatMessage msg=TextChatMessage.createTextMessage("123","456","test");
//////        ProtosVedioLikeMessage msg=ProtosVedioLikeMessage.create(new ProtosVedioLikeMessageBody("123", "sam", "123", "123", "1", new Date()));
////        byte[] data=codec.encode(MessageWrapper.wrapChatMessage(msg));
////        RequestBody body= RequestBody.create(MediaType.parse(CommonProperties.MEDIA_MARKDOWN), data);
////        MultipartBody.Part part=MultipartBody.Part.createFormData("MSG", "msg", body);
////        String token=AES.encode(MD5.GetMD5Code(data));
////        MultipartBody mb=new MultipartBody.Builder().addFormDataPart("TOKEN",token).addFormDataPart("TEMP_ID", "123").addFormDataPart("USER_ID", "123").addPart(part).build();
////        int i=c.connectTimeoutMillis();
////        System.out.println(i);
////        Request request=new Request.Builder().url("http://localhost:8080/message/send").post(mb).build();
////        RequestBody body1=new FormBody.Builder().add("syncNum","19").add("dwID","456").build();
////        Request sync=new Request.Builder().url("http://localhost:8080/message/sync").post(body1).build();
//        try {
//            Response response=c.newCall(request).execute();
//            if(response.isSuccessful()){
//                System.out.println("success receive");
//                System.out.println(response.body().string());
////                MessageWrapper message=codec.decode(IOUtils.toByteArray(response.body().byteStream()));
////                System.out.println(message.getType());
////                if(message.getType()== MessageType.WEALTH_ACK){
////                    WealthAckMessage wealth=(WealthAckMessage)message.getBody();
////                    System.out.println("wealth#" + wealth.getWealth() + " status#" + wealth.getStatus() + " relation#" + wealth.getRelation());
////                }else if(message.getType()==MessageType.LIST){
////                    MessageContainer container=(MessageContainer)message.getBody();
////                    List<MessageWrapper> msgs=container.getMessages();
////                    for(MessageWrapper m:msgs){
////                        if(m.getType()==MessageType.CHAT){
////                            ChatMessage cm=(ChatMessage)m.getBody();
////                            System.out.println(cm.getType());
////                            if(cm.getChatType()== ChatMessageType.TXT){
////                                TextChatMessage tcm=(TextChatMessage)cm;
////                                System.out.println("word#"+tcm.getText()+" id#"+tcm.getFromID());
////                            }
////                        }
////                    }
////                }
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            Thread.sleep(Integer.MAX_VALUE);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//}
