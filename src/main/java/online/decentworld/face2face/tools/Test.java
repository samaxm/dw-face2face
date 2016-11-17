package online.decentworld.face2face.tools;

/**
 * Created by Sammax on 2016/9/26.
 */
public class Test {

    private static volatile boolean bChanged;

    public static void main(String[] args) throws Exception {


//        Configuration configuration= HBaseConfiguration.create();
//        configuration.set("hbase.zookeeper.quorum","120.76.26.75:2080,112.74.29.115:2080,112.74.13.117:2080");
//        configuration.set("hbase.zookeeper.property.clientPort","2080");
//        Connection connection=null;
//        try {
//             connection= ConnectionFactory.createConnection(configuration);
//            HBaseAdmin admin = (HBaseAdmin) connection.getAdmin();
//            HTableDescriptor tableDescriptor = new HTableDescriptor(TableName.valueOf("test"));
////            tableDescriptor.addFamily(new HColumnDescriptor("records"));
//            if (!admin.tableExists(TableName.valueOf("test"))) {
////                admin.createTable(tableDescriptor);
//                System.out.println("not found");
//            }else{
//                System.out.println("created");
//            }
//        }catch (Exception e){
//            e.printStackTrace();
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
    }

}
