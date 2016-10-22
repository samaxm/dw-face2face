//import com.google.protobuf.ByteString;
//import online.decentworld.rpc.codc.protos.ReflectBodyConverterFactory;
//import online.decentworld.rpc.codc.protos.SimpleProtosCodec;
//import online.decentworld.rpc.dto.message.MessageWrapperFactory;
//import online.decentworld.rpc.dto.message.protos.MessageProtos;
//
//import java.io.OutputStream;
//import java.net.Socket;
//
///**
// * Created by Sammax on 2016/10/15.
// */
//public class NettyTest {
//
//    public static void main(String[] args) throws Exception {
//        Socket socket=new Socket("192.168.1.198",8888);
//        OutputStream out=socket.getOutputStream();
//        SimpleProtosCodec codec=new SimpleProtosCodec();
////        codec.setConverterFactory(new ReflectBodyConverterFactory());
//        MessageProtos.Message m=MessageProtos.Message.newBuilder().setMid(1).setTime(1).setFrom("1").setTo("2").setType(MessageProtos.Message.MessageType.CHAT).setData(ByteString.copyFrom("aaa".getBytes())).build();
//        byte[] data=codec.encode(MessageWrapperFactory.createLikeMessage("123","123","123","123","123"));
//        System.out.println(online.decentworld.tools.Base64.encode(data));
//        System.out.println(online.decentworld.tools.Base64.encode(m.toByteArray()));
////        data=ByteBuffer.allocate(4).putInt(123).array();
//        out.write(m.toByteArray());
//        out.flush();
//        socket.close();
//    }
//}
