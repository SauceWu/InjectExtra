package me.sauce.widgetLibrary.base;


/**
 * Created by baixiaokang on 16/4/22.
 */
public abstract class BasePresenter<V> {
    protected V mView;

    public void setView(V v) {
        this.mView = v;
        this.onAttached();
    }

    public abstract void onAttached();

    public void onDetached() {
    }
}
