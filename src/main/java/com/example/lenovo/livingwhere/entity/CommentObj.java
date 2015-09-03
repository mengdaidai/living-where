package com.example.lenovo.livingwhere.entity;

/**
 * 评论对象
 */
public class CommentObj {
    int cid,uid,gender,age,type;//评论索引，用户id,性别0男1女，年龄，类型0短租1长租
    String phoneNum,nickname,headPic,signature,message,pictures,date;//手机号，昵称，头像地址，签名，评论内容，评论图片，日期

    public CommentObj(int cid, int uid, int gender, int age, int type, String phoneNum, String nickname, String headPic, String signature, String message, String pictures, String date) {
        this.cid = cid;
        this.uid = uid;
        this.gender = gender;
        this.age = age;
        this.type = type;
        this.phoneNum = phoneNum;
        this.nickname = nickname;
        this.headPic = headPic;
        this.signature = signature;
        this.message = message;
        this.pictures = pictures;
        this.date = date;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPictures() {
        return pictures;
    }

    public void setPictures(String pictures) {
        this.pictures = pictures;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
