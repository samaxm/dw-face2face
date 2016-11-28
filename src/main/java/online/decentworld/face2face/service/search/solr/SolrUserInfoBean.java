package online.decentworld.face2face.service.search.solr;

import online.decentworld.rdb.entity.BaseDisplayUserInfo;
import org.apache.solr.client.solrj.beans.Field;

/**
 * Created by Sammax on 2016/11/8.
 */
public class SolrUserInfoBean {
    @Field
    private String name;
    @Field
    private String area;
    @Field
    private String sex;
    @Field
    private String sign;
    @Field
    private String id;
    @Field
    private String worth;
    @Field
    private String icon;
    @Field
    private String type;

    public SolrUserInfoBean(){}

    public SolrUserInfoBean(String name, String area, String sex, String sign, String id, String worth, String icon, String type) {
        this.name = name;
        this.area = area;
        this.sex = sex;
        this.sign = sign;
        this.id = id;
        this.worth = worth;
        this.icon = icon;
        this.type = type;
    }

    public static SolrUserInfoBean convert(BaseDisplayUserInfo info){
        String sex;
        if(info.getSex()==null){
            sex="无";
        }else if(info.getSex()==1){
            sex="男";
        }else if(info.getSex()==2){
            sex="女";
        }else{
            sex="无";
        }
        return new SolrUserInfoBean(info.getName(),info.getArea(),sex,info.getSign(),info.getDwID(),String.valueOf(info.getWorth())
                ,info.getIcon(),info.getType());
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWorth() {
        return worth;
    }

    public void setWorth(String worth) {
        this.worth = worth;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
