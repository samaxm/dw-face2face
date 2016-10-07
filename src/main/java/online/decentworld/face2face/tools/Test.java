package online.decentworld.face2face.tools;

/**
 * Created by Sammax on 2016/9/26.
 */
public class Test {
    public static void main(String[] args) throws ClassNotFoundException {

        String test="localhost:8080/face2face/aaaaaa";
        System.out.println(test.substring(test.indexOf("face2face")+"face2face".length()+1));


    }

}
