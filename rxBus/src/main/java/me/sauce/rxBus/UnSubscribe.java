package me.sauce.rxBus;

import android.support.annotation.UiThread;

/**
 * Created by sauce on 2017/5/23.
 */

public interface UnSubscribe {
void unbind();

    UnSubscribe EMPTY = new UnSubscribe() {
        @Override public void unbind() { }
    };
}