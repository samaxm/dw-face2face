package online.decentworld.face2face.test;

import org.apache.thrift.TException;

/**
 * Created by Sammax on 2016/11/24.
 */
public class HelloWorldHandler implements  HelloWorld.Iface {
    @Override
    public void hello(String word) throws TException {
        System.out.println(word);


    }
}
