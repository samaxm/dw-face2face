package online.decentworld.face2face.service.user.impl;


import online.decentworld.rdb.entity.User;
import online.decentworld.tools.MoneyUnitConverter;

/**
 * 用户对外基本信息
 * @author Sammax
 *
 */
public class BaseDisplayUserInfo {

	private String dwID;
	
	private String sex;
	
	private String name;
	
	private String icon;

	private String area;
	
	private String sign;
	
	private String worth;
	
	private String wealth;
	
	
	
	
	public BaseDisplayUserInfo() {
		super();
	}

	public BaseDisplayUserInfo(User user,int wealth) {
		this.dwID=user.getId();
		this.icon=user.getIcon();
		this.area=user.getArea();
		this.name=user.getName();
		this.sex=user.getSex()==1?"男":"女";
		this.sign=user.getSign();
		this.worth= MoneyUnitConverter.fromFenToYuanStr(user.getWorth());
		this.wealth=MoneyUnitConverter.fromFenToYuanStr(wealth);
				
	}
	
	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getWorth() {
		return worth;
	}

	public void setWorth(String worth) {
		this.worth = worth;
	}

	public String getWealth() {
		return wealth;
	}

	public void setWealth(String wealth) {
		this.wealth = wealth;
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
	
	
	
}
