package me.example.sauce.databinding;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import me.sauce.InjectExtra;

import me.sauce.injectExtra.BindExtra;

public class Main2Activity extends AppCompatActivity {
    @InjectExtra("data")
    String data;

    public static void start(Context context) {
        Intent starter = new Intent(context, Main2Activity.class);
        starter.putExtra("data", "成功传过来了");
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BindExtra.inject(this);
        setContentView(R.layout.activity_main2);
        String s = (String) getIntent().getExtras().get("data");

        String xxxx = getIntent().getStringExtra(data);
        TextView textView = (TextView) findViewById(R.id.text);
        textView.setText(data);


    }
}
