package com.tumao.sync.bean;

import com.tumao.sync.util.HttpConnectionUtil;

/**
 * @author 王冰
 * @date 2018/5/9
 */
public class UserInfo {

    /**
     * id : 564477
     * user_id : 429752
     * shop_id : 0
     * user_name : 80286575
     * real_name : 余生情诗
     * openid : ohSPnjhb5ygkFuPBGXXyCW-GddZs
     * phone_num : 18395614373
     * app_openid :
     * passwd : e10adc3949ba59abbe56e057f20f883e
     * privilege :
     * sex : 1
     * language : zh_CN
     * city : Langfang
     * province : Hebei
     * country : CN
     * headimgurl : http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTJO0zYq01WYDpmdrcJISjwItMjg0SpeRxia72NxC35YkYUMujkiaBW5Picd5rpVKjU4cibfbq6EXdJj0w/132
     * create_time : 2017-08-11 10:26:48
     * is_pay : n
     * is_rater : n
     * is_expert : n
     * is_blacklist : n
     * lng : null
     * lat : null
     * is_frozen : n
     * appid : null
     * money : null
     * is_auth : n
     * is_regphone : y
     * is_lead : n
     * remark : null
     * subscribe : null
     */

    private String id;
    private String user_id;
    private String shop_id;
    private String user_name;
    private String real_name;
    private String openid;
    private String phone_num;
    private String app_openid;
    private String passwd;
    private String privilege;
    private int sex;
    private String language;
    private String city;
    private String province;
    private String country;
    private String headimgurl;
    private String create_time;
    private String is_pay;
    private String is_rater;
    private String is_expert;
    private String is_blacklist;
    private String is_frozen;
    private String is_auth;
    private String is_regphone;
    private String is_lead;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getShop_id() {
        return shop_id;
    }

    public void setShop_id(String shop_id) {
        this.shop_id = shop_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getReal_name() {
        return real_name;
    }

    public void setReal_name(String real_name) {
        this.real_name = real_name;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getPhone_num() {
        return phone_num;
    }

    public void setPhone_num(String phone_num) {
        this.phone_num = phone_num;
    }

    public String getApp_openid() {
        return app_openid;
    }

    public void setApp_openid(String app_openid) {
        this.app_openid = app_openid;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getPrivilege() {
        return privilege;
    }

    public void setPrivilege(String privilege) {
        this.privilege = privilege;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getIs_pay() {
        return is_pay;
    }

    public void setIs_pay(String is_pay) {
        this.is_pay = is_pay;
    }

    public String getIs_rater() {
        return is_rater;
    }

    public void setIs_rater(String is_rater) {
        this.is_rater = is_rater;
    }

    public String getIs_expert() {
        return is_expert;
    }

    public void setIs_expert(String is_expert) {
        this.is_expert = is_expert;
    }

    public String getIs_blacklist() {
        return is_blacklist;
    }

    public void setIs_blacklist(String is_blacklist) {
        this.is_blacklist = is_blacklist;
    }

    public String getIs_frozen() {
        return is_frozen;
    }

    public void setIs_frozen(String is_frozen) {
        this.is_frozen = is_frozen;
    }

    public String getIs_auth() {
        return is_auth;
    }

    public void setIs_auth(String is_auth) {
        this.is_auth = is_auth;
    }

    public String getIs_regphone() {
        return is_regphone;
    }

    public void setIs_regphone(String is_regphone) {
        this.is_regphone = is_regphone;
    }

    public String getIs_lead() {
        return is_lead;
    }

    public void setIs_lead(String is_lead) {
        this.is_lead = is_lead;
    }

    public class Response extends HttpConnectionUtil.Response<UserInfo> {
    }
}
