package com.example.lenovo.livingwhere.entity;

/**
 * 出租历史对象
 */
public class RentHistoryObj {
    int bhid,hid,gender,age,state,type;//分别为预约历史id,房子id,性别,年龄,该预约状态 （0请求预约1房主接受2确认完成）,租房类型（长租，短租）
    String phoneNum;
    String nickname;

    public int getHid() {
        return hid;
    }

    public void setHid(int hid) {
        this.hid = hid;
    }

    public Houses getHouse() {
        return house;
    }

    public void setHouse(Houses house) {
        this.house = house;
    }

    String headPic;
    String signature;
    String start;
    String end;//分别为联系电话,昵称,头像,个性签名,开始日期，结束日期
    Houses house;

    public RentHistoryObj(int bhid, int gender, int age, int state, int type, String phoneNum, String nickname, String headPic, String signature, String start, String end,int hid,Houses house) {
        this.bhid = bhid;
        this.gender = gender;
        this.age = age;
        this.state = state;
        this.type = type;
        this.phoneNum = phoneNum;
        this.nickname = nickname;
        this.headPic = headPic;
        this.signature = signature;
        this.start = start;
        this.end = end;
        this.hid = hid;
        this.house = house;
    }

    public int getBhid() {
        return bhid;
    }

    public void setBhid(int bhid) {
        this.bhid = bhid;
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

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
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

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }
}
