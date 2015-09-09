package com.example.lenovo.livingwhere.entity;

import java.io.Serializable;

/**
 * 房子实体
 */
public class Houses implements Serializable{
    int hid;//房子索引
    String contactPhone;//联系电话
    int price;//价格
    int size;//大小
    String description;//描述
    String pictures;//图片
    String address;//地址
    double longitude;//经度
    double latitude;//纬度
    int amount;//交易数量
    double star;//星级
    int type;//0表示普通个人房子，1表示酒店
    int state;//0表示等待审核，1表示上架，2表示下架



    public Houses(int hid, String contactPhone, int price, int size, String description, String pictures, String address, double longitude, double latitude, int amount, double star, int type,int state) {
        this.hid = hid;
        this.contactPhone = contactPhone;
        this.price = price;
        this.size = size;
        this.description = description;
        this.pictures = pictures;
        this.address = address;
        this.longitude = longitude;
        this.latitude = latitude;
        this.amount = amount;
        this.star = star;
        this.type = type;
        this.state = state;
    }

    public int getHid() {
        return hid;
    }

    public void setHid(int hid) {
        this.hid = hid;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPictures() {
        return pictures;
    }

    public void setPictures(String pictures) {
        this.pictures = pictures;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public double getStar() {
        return star;
    }

    public void setStar(double star) {
        this.star = star;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
