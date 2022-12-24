package com.example.test2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;

public class Settings extends AppCompatActivity {

    private LinearLayout modeLayout;
    private Switch switch1, switch2, switch3;
    private ImageView returnButton;
    private int moveMode, modeSpeed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        modeLayout = findViewById(R.id.modeLayout);
        switch1 = findViewById(R.id.switch1);
        switch2 = findViewById(R.id.switch2);
        switch3 = findViewById(R.id.switch3);
        returnButton = findViewById(R.id.return1);

        Switches();

    }


    private void Switches()
    {
        SharedPreferences prefs = getSharedPreferences("CrashInfo", MODE_PRIVATE);
        moveMode = prefs.getInt("mode", 0);
        modeSpeed = prefs.getInt("modeSpeed", 1);

        if(moveMode == 0) {
            switch1.setChecked(true);
            switch2.setChecked(false);
        }
        else {
            switch1.setChecked(false);
            switch2.setChecked(true);
        }

        if(modeSpeed == 1)
        {
            switch3.setChecked(false);
        }
        else if(modeSpeed == 2)
        {
            switch3.setChecked(true);
        }

        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences.Editor editor = getSharedPreferences(
                        "CrashInfo", MODE_PRIVATE).edit();
                if(b)
                {
                    switch2.setChecked(false);
                    moveMode = 0;
                }
                else
                {
                    switch2.setChecked(true);
                    moveMode = 1;
                }
                editor.putInt("mode", moveMode);
                editor.apply();
            }
        });
        switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences.Editor editor = getSharedPreferences(
                        "CrashInfo", MODE_PRIVATE).edit();
                if(b)
                {
                    switch1.setChecked(false);
                    moveMode = 1;
                }
                else
                {
                    switch1.setChecked(true);
                    moveMode = 0;
                }
                editor.putInt("mode", moveMode);
                editor.apply();
            }
        });

        switch3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences.Editor editor = getSharedPreferences(
                        "CrashInfo", MODE_PRIVATE).edit();
                if(b)
                {
                    modeSpeed = 2;
                }
                else
                {
                    modeSpeed = 1;
                }
                editor.putInt("modeSpeed", modeSpeed);
                editor.apply();
            }
        });

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}