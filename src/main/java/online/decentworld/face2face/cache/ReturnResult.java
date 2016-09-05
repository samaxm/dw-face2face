package online.decentworld.face2face.cache;

public class ReturnResult{
	private Object result;
	private boolean success;
	
	public final static ReturnResult SUCCESS=new ReturnResult(true);
	public final static ReturnResult FAIL=new ReturnResult(false);

	public Object getResult() {
		return result;
	}



	public void setResult(Object result) {
		this.result = result;
	}



	


	public boolean isSuccess() {
		return success;
	}



	public void setSuccess(boolean success) {
		this.success = success;
	}



	public ReturnResult(boolean status) {
		super();
		this.success = status;
	}

	
	
	public ReturnResult(Object result, boolean status) {
		super();
		this.result = result;
		this.success = status;
	}



	public static ReturnResult result(Object object){
		return new ReturnResult(object,true);
	}

}
