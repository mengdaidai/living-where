package com.example.lenovo.livingwhere.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.lenovo.livingwhere.fragment.ReleaseHouseFragment;
import com.example.lenovo.livingwhere.entity.Houses;
import com.example.lenovo.livingwhere.view.OnFragmentListener;
import com.example.lenovo.livingwhere.R;

import java.io.Serializable;

public class EditHouseActivity extends AppCompatActivity implements OnFragmentListener{
    ReleaseHouseFragment fragment = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_house);
        TextView title = (TextView)findViewById(R.id.actionbar_edit_info_title);
        title.setText("修改住房信息");
        fragment = new ReleaseHouseFragment();
        fragment.setmHouse((Houses)getIntent().getSerializableExtra("house"));
        getFragmentManager().beginTransaction()
                .add(R.id.edit_house_fragment_container, fragment).show(fragment).commit();

        Intent intent = getIntent();
        Houses house = (Houses)intent.getSerializableExtra("house");
        fragment.setmHouse(house);

    }




    @Override
    public void updateMyHouseList(Houses house) {
        Intent intent = new Intent();
        intent.putExtra("house",(Serializable)house);
        setResult(RESULT_OK,intent);
        finish();
    }
}
