package com.example.lenovo.livingwhere.view;

import com.example.lenovo.livingwhere.entity.Houses;

/**
 * fragment与activity通信用
 */
public interface OnFragmentListener {
    public void updateMyHouseList(Houses house);
}
