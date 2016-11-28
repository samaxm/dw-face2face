package online.decentworld.face2face.tools;

import online.decentworld.face2face.test.HelloWorld;
import online.decentworld.face2face.test.HelloWorldHandler;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Sammax on 2016/9/26.
 */
public class Test {

    private static HelloWorld.Iface iface= new HelloWorldHandler();

    private static SimpleDateFormat format=new SimpleDateFormat("yyyyMMddHH");
    private static String ONLINE_NUM_TABLE="ONLINE_NUM_TABLE_2016";
    private static String COLUMN_FAMILY="ONLINENUM";


    public static void main(String[] args) throws Exception {
        Calendar calendar=Calendar.getInstance();
        String rowkey=format.format(calendar.getTime());
//        Result result=HbaseClient.instance().get(ONLINE_NUM_TABLE.getBytes(),rowkey.getBytes());
//        System.out.println("returned ----->");
//        NavigableMap<byte[], byte[]> familyMap =result.getFamilyMap(Bytes.toBytes(COLUMN_FAMILY));
//        int counter = 0;
//        String[] Quantifers = new String[familyMap.size()];
//        String[] value=new String[familyMap.size()];
//        for(byte[] bQunitifer : familyMap.keySet())
//        {
//            Quantifers[counter] = Bytes.toString(bQunitifer);
//            value[counter]=Bytes.toString(familyMap.get(bQunitifer));
//            counter++;
//        }
//
//        for(int i=0;i<familyMap.size();i++){
//            System.out.println(value[i]);
//            System.out.println(Quantifers[i]);
//        }
//
//        HbaseClient.instance().close();
        System.out.println(rowkey);
    }

    public static void simple(HelloWorld.Processor processor){
        try {
            TServerTransport serverTransport=new TServerSocket(9090);
            TServer server=new TSimpleServer(new TServer.Args(serverTransport).processor(processor));
            System.out.println("starting the simple server...");
            server.serve();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
//        Configuration configuration= HBaseConfiguration.create();
//        configuration.set("hbase.zookeeper.quorum","120.76.26.75:2080,112.74.29.115:2080,112.74.13.117:2080");
//        configuration.set("hbase.zookeeper.property.clientPort","2080");
//        Connection connection=null;
//        HTable table=null;
//        try {
//             connection= ConnectionFactory.createConnection(configuration);
//          table= (HTable) connection.getTable(TableName.valueOf("test2"));
//            byte[] rowkey="row1".getBytes();
////            //add rowkey put
////            Put put=new Put(rowkey);
////            //set cf and c v
////            put.addColumn("records".getBytes(),"col1".getBytes(),"value1".getBytes());
////            table.put(put);
//            Get get=new Get(rowkey);
//            get.addColumn("records".getBytes(),"col1".getBytes());
//
//            Get get2=new Get(rowkey);
//            get2.addColumn("records".getBytes(),"col1".getBytes());
//
//            Result result=table.get(get);
//
//            Result result2=table.get(get2);
//
//            CellScanner scanner=result.cellScanner();
//            while (scanner.advance()) {
//                Cell cell = scanner.current();
//                String s = new String(CellUtil.cloneValue(cell));
//                if (!s.startsWith(".") && !s.startsWith(",") && s.length() > 2) {
//                    System.out.println(s);
//                }
//            }
//            CellScanner scanner2=result2.cellScanner();
//
//            while (scanner2.advance()) {
//                Cell cell = scanner.current();
//                String s = new String(CellUtil.cloneValue(cell));
//                if (!s.startsWith(".") && !s.startsWith(",") && s.length() > 2) {
//                    System.out.println(s);
//                }
//            }
////            if (!admin.tableExists(TableName.valueOf("test"))) {
////                System.out.println("not found");
////            }else{
////                System.out.println("created");
////            }
//        }catch (Exception e){
//            e.printStackTrace();
//
//        }finally {
//            if(table!=null){
//                table.close();
//            }
//            if(connection!=null){
//                connection.close();
//            }
//        }
//        HTable t=new HTable(configuration,"test");
//        System.out.println(Bytes.toString(t.getName().getName()));
//        t.close();
//      Cipher cipher = Cipher.getInstance("RSA");
//      StringBuffer sb = new StringBuffer();
//      StringBuffer sb2 = new StringBuffer();
//      try {
//            File rsa_public=new File(ConfigLoader.AdminConfig.RSA_PUBLIC);
//            File rsa_private=new File(ConfigLoader.AdminConfig.RSA_PRIVATE);
//            InputStream in = new FileInputStream(rsa_public);
//            InputStream in2 =new FileInputStream(rsa_private);
//            int c;
//            while ((c = in.read()) != -1) {
//                sb.append((char) c);
//            }
//            while ((c = in2.read()) != -1) {
//                sb2.append((char) c);
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        String RSA_PUBLIC=sb.toString().replaceAll("(-+BEGIN PUBLIC KEY-+\\r?\\n?|-+END PUBLIC KEY-+\\r?\\n?)", "").replace("\n", "");
//        String RSA_PRIVATE=sb2.toString().replaceAll("(-+BEGIN PRIVATE KEY-+\\r?\\n?|-+END PRIVATE KEY-+\\r?\\n?)", "").replace("\n", "");
//        PublicKey key=RSA.getPublicKey(RSA_PUBLIC);
//        PrivateKey key2=RSA.getPrivateKey(RSA_PRIVATE);
//        cipher.init(Cipher.ENCRYPT_MODE,key);
//        String random="QJBSQHDKPYVXGRZF";
//        String data=Base64.encode(cipher.doFinal(random.getBytes()));
//        System.out.println(data);
//        byte[] data2=Base64.decode(data);
//        cipher.init(Cipher.DECRYPT_MODE, key2);
//        byte[] plainText = cipher.doFinal(data2);
//        System.out.println("plain : " + new String(plainText));
//
//        System.out.println(RSA.decrypt(data,RSA_PRIVATE,"utf-8"));
//        String text="text";
//        String url=FastDFSClient.upload(text.getBytes(),"txt",null);
//        System.out.println(url);
//    }

}
