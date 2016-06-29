package com.example.zengtaizhu.myapp;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Receiver extends Activity {

    Button btn;
    Button back;
    Bundle bundle;
    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            if(msg.what == 0x111)
            {
                Toast.makeText(getApplicationContext(), "编号:" + bundle.get("No"), Toast.LENGTH_LONG).show();
            }
            if(msg.what == 0x112)
            {
                //Intent intent = new Intent(Receiver.this,MainActivity.class);
                //startActivity(intent);
                //结束当前的Activity，回到第一个Activity
                finish();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver);
        btn = (Button) findViewById(R.id.btn);
        bundle = this.getIntent().getExtras();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.sendEmptyMessage(0x111);
            }
        });
        back = (Button)findViewById(R.id.goback);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.sendEmptyMessage(0x112);
            }
        });
    }
}
