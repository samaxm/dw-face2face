package online.decentworld.face2face.exception;

public class EasemobAPIException extends Exception{

	public static int PARAMS_ERROR=400;
	public static int IO_ERROR=-1;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5554294019427364381L;

	private int errcode;
	
	public EasemobAPIException(int errcode){
		this.setErrcode(errcode);
	}

	/**
	 * @return the errcode
	 */
	public int getErrcode() {
		return errcode;
	}

	/**
	 * @param errcode the errcode to set
	 */
	public void setErrcode(int errcode) {
		this.errcode = errcode;
	}
	
}
