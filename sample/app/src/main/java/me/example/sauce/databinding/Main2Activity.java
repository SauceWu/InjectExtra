package me.example.sauce.databinding;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.sauce.databinding.R;

import me.sauce.InjectExtra;
import me.sauce.injectExtra.BindExtra;


public class Main2Activity extends AppCompatActivity {
    @InjectExtra("data")
    String data;
    @InjectExtra("math")
    int math = 1;

    public static void start(Context context) {
        Intent starter = new Intent(context, Main2Activity.class);
        starter.putExtra("data", "hello world");
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BindExtra.inject(this);
        setContentView(R.layout.activity_main2);
        TextView textView = (TextView) findViewById(R.id.text);

        textView.setText(data + "----" + math);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame,new FragmentTest()).commit();
    }
}
