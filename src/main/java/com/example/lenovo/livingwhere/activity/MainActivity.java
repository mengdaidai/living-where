package com.example.lenovo.livingwhere.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.example.lenovo.livingwhere.util.BitmapCache;
import com.example.lenovo.livingwhere.entity.Houses;
import com.example.lenovo.livingwhere.view.OnFragmentListener;
import com.example.lenovo.livingwhere.R;
import com.example.lenovo.livingwhere.fragment.FindHouseFragment;
import com.example.lenovo.livingwhere.fragment.HouseDetailsFragment;
import com.example.lenovo.livingwhere.fragment.RecommendMainFragment;
import com.example.lenovo.livingwhere.fragment.ReleaseHouseFragment;
import com.example.lenovo.livingwhere.adapter.SlideMenuAdapter;
import com.example.lenovo.livingwhere.entity.CurrentUserObj;
import com.google.gson.Gson;
import com.nineoldandroids.view.ViewHelper;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener,OnFragmentListener {

    private ImageButton[] mainTabs;
    private int index;//点击底部按钮的index

    private int currentTabIndex;//正在显示的fragment的index

    private Fragment[] fragments;
    private RecommendMainFragment fragment1;//首页推荐
    private ReleaseHouseFragment fragment2;//发布房子
    private FindHouseFragment fragment3;//找房子
    private HouseDetailsFragment fragment4;//房子详情
    private DrawerLayout drawerLayout;
    private ImageButton headButton;//头像


    public Button locationButton;//定位按钮
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();
    public static RequestQueue mQueue ;//Volley请求队列，找你哥哥application仅此一个
    public ListView slideMenuListView;//侧滑栏里显示的条目
    public SlideMenuAdapter slideMenuAdapter;
    public List<String> slideMenuText;

    public static CurrentUserObj userObj;//当前用户对象，整个application仅此一个
    ImageLoader imageLoader;
    public static double longitude,latitude;//当前经纬度


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("onCreate");
        setContentView(R.layout.activity_main);
        mQueue = Volley.newRequestQueue(this);
        userObj = new CurrentUserObj();
        initView();
        initEvents();
        initLocation();

    }

    /**
     * 初始化组件
     */
    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        headButton = (ImageButton)findViewById(R.id.actionbar_recommend_main_head);
        locationButton = (Button)findViewById(R.id.actionbar_location);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED,
                Gravity.RIGHT);

        setSupportActionBar(toolbar);
        currentTabIndex = 0;

        mainTabs = new ImageButton[3];
        mainTabs[0] = (ImageButton) findViewById(R.id.RecommendMainButton);
        mainTabs[1] = (ImageButton) findViewById(R.id.ReleaseHouseButton);
        mainTabs[2] = (ImageButton) findViewById(R.id.FindHouseButton);

        mainTabs[0].setOnClickListener(this);
        mainTabs[1].setOnClickListener(this);
        mainTabs[2].setOnClickListener(this);
        headButton.setOnClickListener(this);


        fragment1 = new RecommendMainFragment();
        fragment2 = new ReleaseHouseFragment();
        fragment3 = new FindHouseFragment();
        fragment4 = new HouseDetailsFragment();
        fragments = new Fragment[]{fragment1, fragment2, fragment3,fragment4};

        getFragmentManager().beginTransaction()
                .add(R.id.fragment_container, fragments[0])
                .add(R.id.fragment_container, fragments[1])
                .add(R.id.fragment_container, fragments[2])
                .add(R.id.fragment_container,fragments[3])
                .hide(fragments[1]).hide(fragments[2]).hide(fragments[3])
                .show(fragments[0]).commit();
        //侧滑栏相关
        slideMenuListView = (ListView)findViewById(R.id.left_menu);
        slideMenuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0://个人信息
                        startActivity(new Intent(MainActivity.this, MyInfoActivity.class));
                        break;
                    case 1://我的预约历史
                        startActivity(new Intent(MainActivity.this, MyOrderHistoryActivity.class));
                        break;
                    case 2://我的出租房订单
                        startActivity(new Intent(MainActivity.this, MyRentNoteActivity.class));
                        break;
                    case 3:
                        Intent intent = new Intent(MainActivity.this, OtherHouseActivity.class);
                        intent.putExtra("type",1);
                        startActivity(intent);
                }

            }
        });
        slideMenuText = new ArrayList<String>();
        slideMenuText.add("个人信息");
        slideMenuText.add("我的预约历史");
        slideMenuText.add("我的出租房订单");
        slideMenuText.add("我的出租房");
        slideMenuAdapter = new SlideMenuAdapter(MainActivity.this,slideMenuText);
        slideMenuListView.setAdapter(slideMenuAdapter);


        imageLoader = new ImageLoader(MainActivity.mQueue,new BitmapCache());
        ImageLoader.ImageListener listener = ImageLoader.getImageListener(headButton,
                R.drawable.pic_head_normal, R.drawable.pic_head_selected);
        imageLoader.get(userObj.getHeadPic(), listener,200,200);

    }


    private void initEvents() {
        drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerStateChanged(int newState) {
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                View mContent = drawerLayout.getChildAt(0);
                View mMenu = drawerView;
                float scale = 1 - slideOffset;
                float rightScale = 0.8f + scale * 0.2f;


                float leftScale = 1 - 0.3f * scale;

                ViewHelper.setScaleX(mMenu, leftScale);
                ViewHelper.setScaleY(mMenu, leftScale);
                ViewHelper.setAlpha(mMenu, 0.6f + 0.4f * (1 - scale));
                ViewHelper.setTranslationX(mContent,
                        mMenu.getMeasuredWidth() * (1 - scale));
                ViewHelper.setPivotX(mContent, 0);
                ViewHelper.setPivotY(mContent,
                        mContent.getMeasuredHeight() / 2);
                mContent.invalidate();
                ViewHelper.setScaleX(mContent, rightScale);
                ViewHelper.setScaleY(mContent, rightScale);


            }


            @Override
            public void onDrawerOpened(View drawerView) {
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                drawerLayout.setDrawerLockMode(
                        DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.RIGHT);
            }
        });
    }



    //底部三个按钮切换页面
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.RecommendMainButton:
                index = 0;
                break;
            case R.id.ReleaseHouseButton:
                index = 1;
                break;
            case R.id.FindHouseButton:
                index = 2;
                break;
            case R.id.actionbar_recommend_main_head:
                drawerLayout.openDrawer(GravityCompat.START);
                break;


        }
        if(currentTabIndex == 3){
            if(index!=0){
                mainTabs[index].setBackgroundColor(Color.rgb(1, 175, 242));
                mainTabs[0].setBackgroundColor(Color.rgb(242, 114, 112));
                FragmentTransaction trx = getFragmentManager().beginTransaction();
                trx.hide(fragments[currentTabIndex]);
                if (!fragments[index].isAdded()) {
                    trx.add(R.id.fragment_container, fragments[index]);
                }
                trx.show(fragments[index]).commit();
            }

        }
        else if (currentTabIndex != index) {
            mainTabs[index].setBackgroundColor(Color.rgb(1, 175, 242));

            mainTabs[currentTabIndex].setBackgroundColor(Color.rgb(242, 114, 112));

            FragmentTransaction trx = getFragmentManager().beginTransaction();
            trx.hide(fragments[currentTabIndex]);
            if (!fragments[index].isAdded()) {
                trx.add(R.id.fragment_container, fragments[index]);
            }
            trx.show(fragments[index]).commit();
        }

        currentTabIndex = index;
    }


     Handler mHandler = new Handler(){
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            Gson gson = new Gson();

            switch(msg.what)
            {
                case 0://更新定位
                    Bundle bundle = msg.getData();
                    String locateType = bundle.getString("locateType");
                    System.out.println(locateType);
                    if(locateType.equals("gps")||locateType.equals("network"))
                        locationButton.setText(bundle.getString("location"));
                    else if (locateType.equals("offline"))
                        Toast.makeText(MainActivity.this,"离线定位成功",Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(MainActivity.this,"定位失败，请检查网络或稍后再试",Toast.LENGTH_LONG).show();
                    break;



            }

        }

    };

    //用于刚进入程序时定位
    private void initLocation(){
        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener( myListener );    //注册监听函数
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        //int span=1000;
        option.setScanSpan(0);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    //OnFragmentListener必须实现的函数,用于展示房子详情页面
    @Override
    public void showDetailsFragment(Houses house) {
        getFragmentManager().beginTransaction()
                .hide(fragments[currentTabIndex])
                .show(fragments[3]).commit();
        currentTabIndex = 3;
        fragment4.displayDetails(house);
    }
    //OnFragmentListener必须实现的函数
    @Override
    public void updateMyHouseList(Houses house) {

    }
    //百度地图定位
    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //Receive Location
            StringBuffer sb = new StringBuffer(256);
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());

            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                Message msg = new Message();
                msg.what = 0;
                Bundle bundle = new Bundle();
                bundle.putString("location",location.getAddrStr());
                bundle.putString("locateType", "gps");
                msg.setData(bundle);
                mHandler.sendMessage(msg);
                fragment1.initListView(location.getLatitude(), location.getLongitude());
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                sb.append("\ndescribe : ");
                sb.append("gps success");

            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                Message msg = new Message();
                msg.what = 0;
                Bundle bundle = new Bundle();
                bundle.putString("location", location.getAddrStr());
                bundle.putString("locateType","network");
                msg.setData(bundle);
                mHandler.sendMessage(msg);
                System.out.println("mHandler成功！即将initListView");
                fragment1.initListView(location.getLatitude(), location.getLongitude());
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                sb.append("\ndescribe : ");
                sb.append("network success");
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("li xian ding wei cheng gong");
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                Message msg = new Message();
                msg.what = 0;
                Bundle bundle = new Bundle();
                bundle.putString("locateType","offline");
                msg.setData(bundle);
                mHandler.sendMessage(msg);
                fragment1.initListView(location.getLatitude(),location.getLongitude());
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("fu wu duan wang luo ding wei shi bai");
                Message msg = new Message();
                msg.what = 0;
                Bundle bundle = new Bundle();
                bundle.putString("locateType", "failed");
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("wang luo bu tong dao zhi ding wei shi bai ");
                Message msg = new Message();
                msg.what = 0;
                Bundle bundle = new Bundle();
                bundle.putString("locateType", "failed");
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("wu fa huo qu you xiao ding wei yi ju");
                Message msg = new Message();
                msg.what = 0;
                Bundle bundle = new Bundle();
                bundle.putString("locateType", "failed");
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            }
            Log.i("BaiduLocationApiDem", sb.toString());
        }
    }









}
