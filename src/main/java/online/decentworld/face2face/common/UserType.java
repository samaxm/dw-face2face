package online.decentworld.face2face.common;


public enum UserType {
	
	UNCERTAIN(0,"疑"),CHECKED(1,"真"),STAR(2,"腕"),DWCS(3,"客服");
	
	private int code;
	
	private String name;
	
	private UserType(int code,String name){
		this.code=code;
		this.name=name;
	}
	
	public int getTypeCode(){
		return code;
	}

	public String getName() {
		return name;
	}

	public static UserType getUserType(int code){
		for(UserType type:UserType.values()){
			if(type.getTypeCode()==code){
				return type;
			}
		}
		return null;
	}


	@Override
	public String toString() {
		return this.name;
	}

}
