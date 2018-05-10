package com.tumao.sync.bean;

import com.tumao.sync.util.HttpConnectionUtil;

import java.util.List;

/**
 * @author 王冰
 * @date 2018/5/9
 */
public class SimpleAlbum {


    /**
     * id : 20
     * sid : 64
     * name : 测试图集
     * cover_img : http://tomoyunshequ.oss-cn-shanghai.aliyuncs.com/cover_img/1514433511432.jpg
     * created_at : 2018-02-27 14:18:28
     * pic_num : 178
     */

    private String id;
    private String sid;
    private String name;
    private String cover_img;
    private String created_at;
    private String pic_num;
    private String money;
    private String type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCover_img() {
        return cover_img;
    }

    public void setCover_img(String cover_img) {
        this.cover_img = cover_img;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getPic_num() {
        return pic_num;
    }

    public void setPic_num(String pic_num) {
        this.pic_num = pic_num;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static class Response extends HttpConnectionUtil.Response<List<SimpleAlbum>> {

    }
}
