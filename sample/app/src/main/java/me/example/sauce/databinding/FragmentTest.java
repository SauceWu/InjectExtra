package me.example.sauce.databinding;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.sauce.databinding.R;


import me.sauce.EventThread;
import me.sauce.InjectExtra;
import me.sauce.Subscribe;
import me.sauce.rxBus.RxBus;
import me.sauce.rxBus.RxManager;
import me.sauce.rxBus.UnSubscribe;

/**
 * Created by sauce on 2017/3/13.
 * Version 1.0.0
 */
public class FragmentTest extends Fragment {
    @InjectExtra("data")
    String data;
    private UnSubscribe unSubscribe;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unSubscribe = RxManager.init(this);
        view.findViewById(R.id.bt).setOnClickListener(v -> RxBus.getInstance().post(200));
    }

    @Subscribe(tag = 200, thread = EventThread.MAIN_THREAD)
    public void Event() {
        Toast.makeText(getActivity(), "成功", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onPause() {
        super.onPause();
        unSubscribe.unSubscribe();
    }
}
