package me.example.sauce.databinding;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.sauce.InjectExtra;
import me.sauce.injectExtra.BindExtra;

/**
 * Created by sauce on 2017/3/13.
 * Version 1.0.0
 */
public class FragmentTest extends Fragment {
    @InjectExtra("data")
    String data;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BindExtra.inject(this);
    }
}
