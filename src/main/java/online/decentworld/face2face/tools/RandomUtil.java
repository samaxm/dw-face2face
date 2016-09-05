package online.decentworld.face2face.tools;

import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public class RandomUtil {
	private Random random=new Random(System.currentTimeMillis());
	
	public int getRandomNum(int roof){
		return random.nextInt(roof);
	}
}
