package online.decentworld.face2face.common;

public enum AppType {
	ANDROID,IPHONE;
	
	public  static boolean checkType(String typeName){
		for(AppType type:AppType.values()){
			if(type.toString().equals(typeName)){
				return true;
			}
		}
		return false;
	}
}
