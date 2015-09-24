package com.example.lenovo.livingwhere.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.example.lenovo.livingwhere.R;

/**
 * Created by rival on 2015/9/22.
 */
public class ChooseLocActivity extends Activity {
    //地图控件
    private MapView mMapView = null;
    // 地图实例
    private BaiduMap mBaiduMap;
    //初始的经纬度
    private double Lantitude;
    private double Longitude;
    // 当前的的经纬度和地址
    private double mCurrentLantitude;
    private double mCurrentLongitude;
    private String mCurrentLocation;

    private GeoCoder geoCoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_chooseloc);
        getData();
        initView();
    }

    private void getData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Lantitude = bundle.getDouble("Lantitude");
            Longitude = bundle.getDouble("Longitude");
        }
    }

    private void initView() {

        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();

        Toast.makeText(getApplicationContext(), "请长按房子的地点", Toast.LENGTH_LONG).show();

        //初始化地理编码
        geoCoder = GeoCoder.newInstance();
        OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {
            public void onGetGeoCodeResult(GeoCodeResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    //没有检索到结果
                }
                //获取地理编码结果
            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    //没有找到检索结果
                    Toast.makeText(getApplicationContext(), "请重新选择", Toast.LENGTH_LONG).show();
                    mBaiduMap.clear();
                    return;
                }
                mCurrentLocation = result.getAddress() + result.getBusinessCircle();
                dialog();

            }
        };
        geoCoder.setOnGetGeoCodeResultListener(listener);

        if (Longitude != 0 && Lantitude != 0) {
            LatLng ll = new LatLng(Lantitude, Longitude);
            MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
            mBaiduMap.animateMapStatus(u);
        }
        mBaiduMap.setOnMapLongClickListener(new BaiduMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                mCurrentLantitude = latLng.latitude;
                mCurrentLongitude = latLng.longitude;

                geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
                BitmapDescriptor bitmap = BitmapDescriptorFactory
                        .fromResource(R.drawable.order_cancel_32px);
                // 构建MarkerOption，用于在地图上添加Marker
                OverlayOptions option = new MarkerOptions().position(latLng).icon(bitmap);

                mBaiduMap.clear();
                mBaiduMap.addOverlay(option);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    protected void dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ChooseLocActivity.this);
        builder.setMessage("确认选择" + mCurrentLocation + "吗");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent();
                intent.putExtra("Lantitude", mCurrentLantitude);
                intent.putExtra("Longitude", mCurrentLongitude);
                intent.putExtra("Location", mCurrentLocation);
                setResult(RESULT_OK
                        , intent);
                finish();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}
