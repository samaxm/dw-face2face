package online.decentworld.face2face.common;
/**
 * 註冊方式
 * @author Sammax
 *
 */
public enum RegisterType {
	/**
	 * 微信註冊
	 */
	WX,INFO;

	public static void main(String[] args) {
		System.out.println(RegisterType.valueOf("INFO"));
	}
}
