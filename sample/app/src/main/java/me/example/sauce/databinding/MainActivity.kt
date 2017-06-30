package me.example.sauce.databinding

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View

import com.example.sauce.databinding.R
import me.sauce.InjectExtra

class MainActivity : AppCompatActivity() {
    @InjectExtra("222")
     var ee = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById(R.id.bt).setOnClickListener { Main2Activity.start(this@MainActivity) }
    }
}
