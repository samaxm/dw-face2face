package online.decentworld.face2face.service.register;
/**
 * 註冊方式
 * @author Sammax
 *
 */
public enum RegisterType {
	/**
	 * 微信註冊
	 */
	WX,INFO,PHONECODE;

	public static void main(String[] args) {
		System.out.println(RegisterType.valueOf("INFO"));
	}
}
