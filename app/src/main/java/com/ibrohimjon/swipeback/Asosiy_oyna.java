package com.ibrohimjon.swipeback;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Asosiy_oyna extends AppCompatActivity {

    Button btn_second;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asosiy_oyna);

        btn_second = findViewById(R.id.btn_second);

        btn_second.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Asosiy_oyna.this, Second.class);
                startActivity(intent);
            }
        });
    }
}
