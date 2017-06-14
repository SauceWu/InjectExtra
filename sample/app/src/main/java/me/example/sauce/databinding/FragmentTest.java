package me.example.sauce.databinding;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sauce.databinding.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        view.findViewById(R.id.bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RxBus.getInstance().post(200);
            }
        });
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.testView);
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 60, LinearLayoutManager.VERTICAL,true));

        recyclerView.setAdapter(new Adapter());
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

    class Adapter extends RecyclerView.Adapter<FragmentTest.Adapter.ViewHolder> {
        List<String> data = new ArrayList<>();
        String[] test = {"发送到发送地方说到发送地方大发送地方", "阿斯顿发水果"};

        public Adapter() {
            this.data = data;
            for (int i = 0; i < 600; i++) {
                data.add(i + "");
            }
            Collections.addAll(data, test);
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            TextView textView = new TextView(getActivity());
            textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return new ViewHolder(textView);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            viewHolder.itemView.setText(data.get(i));
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            private final TextView itemView;

            public ViewHolder(TextView itemView) {
                super(itemView);
                this.itemView = itemView;
            }
        }
    }
}
