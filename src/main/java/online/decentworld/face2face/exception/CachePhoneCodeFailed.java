package online.decentworld.face2face.exception;

public class CachePhoneCodeFailed extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7918973510041474705L;
	private String phoneNum;

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public CachePhoneCodeFailed(String phoneNum) {
		super();
		this.phoneNum = phoneNum;
	}
	
	
	
}
