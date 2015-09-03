package com.example.lenovo.livingwhere.entity;

import com.example.lenovo.livingwhere.entity.Houses;

import java.io.Serializable;

/**
 * Created by LENOVO on 2015-8-4.
 */
public class DistanceSort implements Serializable{

    Houses house;
    double distance;
    public DistanceSort(Houses house, double distance) {
        this.house = house;
        this.distance = distance;
    }


    public Houses getHouse() {
        return house;
    }

    public void setHouse(Houses house) {
        this.house = house;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
