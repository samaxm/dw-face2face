package online.decentworld.face2face.service.match;

public class MatchUserInfo {
	public MatchUserInfo() {
		super();
		// TODO Auto-generated constructor stub
	}

	private String dwID;
	
	private String name;
	
	private String icon;

	private String sign;


	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getDwID() {
		return dwID;
	}

	public void setDwID(String dwID) {
		this.dwID = dwID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public MatchUserInfo(String dwID, String name, String icon,String sign) {
		super();
		this.dwID = dwID;
		this.name = name;
		this.icon = icon;
		this.sign=sign;
	}

	public MatchUserInfo(String dwID, String name, String icon) {
		super();
		this.dwID = dwID;
		this.name = name;
		this.icon = icon;
	}
	
	
	
}
