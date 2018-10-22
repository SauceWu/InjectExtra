package me.example.sauce.databinding;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sauce.databinding.R;

import androidx.fragment.app.Fragment;
import me.sauce.InjectExtra;
import me.sauce.injectExtra.BindExtra;

/**
 * Created by sauce on 2017/3/13.
 * Version 1.0.0
 */
public class FragmentTest extends Fragment {
    @InjectExtra("data")
    String data;

    public static FragmentTest newInstance() {

        Bundle args = new Bundle();

        FragmentTest fragment = new FragmentTest();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_test, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BindExtra.inject(this);
    }
}
