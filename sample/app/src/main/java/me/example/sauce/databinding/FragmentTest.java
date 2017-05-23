package me.example.sauce.databinding

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.example.sauce.databinding.R


import me.sauce.EventThread
import me.sauce.InjectExtra
import me.sauce.Subscribe
import me.sauce.rxBus.RxBus
import me.sauce.rxBus.RxManager
import me.sauce.rxBus.UnSubscribe

/**
 * Created by sauce on 2017/3/13.
 * Version 1.0.0
 */
class FragmentTest : Fragment() {
    @InjectExtra("data")
    internal var data: String? = null
    private var unSubscribe: UnSubscribe? = null


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.activity_main, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        unSubscribe = RxManager.init(this)
        view!!.findViewById(R.id.bt).setOnClickListener { RxBus.getInstance().post(200) }
    }

    @Subscribe(tag = 200, thread = EventThread.MAIN_THREAD)
    fun Event() {
        Toast.makeText(activity, "成功", Toast.LENGTH_SHORT).show()

    }

    override fun onPause() {
        super.onPause()
        unSubscribe!!.unSubscribe()
    }
}
