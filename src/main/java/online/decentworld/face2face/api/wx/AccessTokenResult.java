package online.decentworld.face2face.api.wx;
/**
 * 通過授權code獲取access_token
 * @author Sammax
 *
 */
public class AccessTokenResult {
	private String unionid;
	private String openid;
	private String access_token;
	public String getUnionid() {
		return unionid;
	}
	public void setUnionid(String unionid) {
		this.unionid = unionid;
	}
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public String getAccess_token() {
		return access_token;
	}
	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}
	
	
	
}
