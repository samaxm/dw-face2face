package online.decentworld.face2face.tools;

import online.decentworld.tools.MD5;

/**
 * Created by Sammax on 2016/9/26.
 */
public class Test {
    public static void main(String[] args) throws ClassNotFoundException {

        System.out.println(MD5.GetMD5Code("appid=wx8a6304437033f400&attach=1234567890&body=大腕用户充值&mch_id=1282167201&nonce_str=a0d1d2b717d0449197e4cedf33784cad&notify_url=http://120.76.26.75/DecentWorldServer/charge/getWXOrderResponse&out_trade_no=12345678901475912039587&spbill_create_ip=0:0:0:0:0:0:0:1&total_fee=100&trade_type=APP&key=0nhgdsoupnjdd49oeenskncfmdk9rvei").toUpperCase());

    }

}
