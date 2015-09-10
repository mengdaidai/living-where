package com.example.lenovo.livingwhere.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.lenovo.livingwhere.fragment.ReleaseHouseFragment;
import com.example.lenovo.livingwhere.entity.Houses;
import com.example.lenovo.livingwhere.view.OnFragmentListener;
import com.example.lenovo.livingwhere.R;

import java.io.Serializable;

public class EditHouseActivity extends AppCompatActivity implements OnFragmentListener{
    ReleaseHouseFragment fragment = null;
    TextView title;
    ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_house);
        title = (TextView)findViewById(R.id.toolbar_back_title_text);
        title.setText("修改住房信息");
        backButton = (ImageButton)findViewById(R.id.toolbar_back_title_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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
